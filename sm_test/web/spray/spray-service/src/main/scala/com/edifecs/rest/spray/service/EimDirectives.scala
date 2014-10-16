package com.edifecs.rest.spray.service

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import javax.xml.bind.DatatypeConverter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
// Base64 encoding in the JDK standard library!

import com.edifecs.epp.isc.CommandCommunicator
import com.edifecs.epp.security.SessionId
import com.edifecs.epp.security.data.token.UsernamePasswordAuthenticationToken
import shapeless._
import spray.http.{HttpCookie, StatusCodes}
import spray.routing.Directives._
import spray.routing.authentication.{BasicAuth, UserPass}
import spray.routing.{Directive1, Route}

/** 
 * Defines some spray.io directives that can be used to authenticate against
 * the ESM security framework.
 *
 * @author c-adamnels
 * @see http://spray.io/documentation/1.2.1/spray-routing/key-concepts/directives/
 */
object EimDirectives {
  
  /** The name of the cookie that stores the user's session ID. */
  val loginCookieName = "eim_session"

  private val loginUrl = "/rest/login"
  
  /**
   * A directive which attempts to authenticate using either a cookie or HTTP
   * basic authentication. Responds with an error message if neither option
   * provides valid authentication.
   *
   * The cookie that this directive looks for can be created using the {@link
   * #loginToEim(String, String)} directive.
   */
  object authenticateWithEim extends Directive1[SessionId] {
    def happly(f: SessionId :: HNil => Route): Route = {
      val security = CommandCommunicator.getInstance.getSecurityManager
      // Unregister the current session tied to the thread. This is a fail safe to make absolutely
      // sure that, even if the initialization of the Spray Container is done in the wrong order,
      // there is no scenario where a user session can exist before the execution of the
      // authentication process.
      security.getSessionManager.unregisterCurrentSession()

      authenticate(BasicAuth(loginBasic _, realm = "EPP REST Service")) { id =>
        f(id :: HNil)
      } ~
      cookie(loginCookieName) { httpCookie =>
        deserializeSessionId(httpCookie.content) match {
          case Some(id) =>
            f(id :: HNil)
          case None =>
            deleteCookie(loginCookieName) {
              respondWithStatus(StatusCodes.BadRequest) {
                complete {
                  <p>
                    Request contained an invalid session cookie. Please try again.
                  </p>
                }
              }
            }
        }
      } ~
      noop {
        val id = security.getSessionManager.createAndRegisterNewSession()
        f(id :: HNil)
      }
    }
  }

  def loginToEim(domain: String, organization: String, username: String, password: String) =
    new Directive1[Try[Unit]] {
      def happly(f: Try[Unit] :: HNil => Route): Route = {
        val c = CommandCommunicator.getInstance
        def failure(ex: Throwable) = {
          deleteCookie(loginCookieName) {
            f(Failure(ex) :: HNil)
          }
        }
        onSuccess(login(new UsernamePasswordAuthenticationToken(domain, organization, username, password))) {
          case Success(id) =>
            try {
              c.getSecurityManager.getSessionManager.registerCurrentSession(id)
              if (c.getSecurityManager.getAuthenticationManager.isSubjectAuthenticated) {
                setCookie(HttpCookie(loginCookieName, content = serializeSessionId(id))) {
                  f(Success(()) :: HNil)
                }
              } else {
                throw new SecurityException("Although login appears to have succeeded, the" +
                  " Security Service still says that this session is not authenticated.")
              }
            } catch {
              case ex: Exception =>
                failure(ex)
            }
          // Success(None) should not be possible here.
          case Failure(ex) => failure(ex)
        }
      }
    }

  def loginBasic(userPass: Option[UserPass]): Future[Option[SessionId]] =
    userPass map { up =>
      val org = up.user.split('@')
      val domain = org(0).split('\\')

      if((org.length == 2) && (domain.length == 2)) {
        login(new UsernamePasswordAuthenticationToken(domain(0), org(1), domain(1), up.pass)) map {
          case Success(id) => Some(id)
          case Failure(ex) =>
            ex.printStackTrace()
            None
        }
      } else {
        Future.apply(None)
      }
    } getOrElse Future(None)

  def login(userPass: UsernamePasswordAuthenticationToken): Future[Try[SessionId]] =
    Future {
      Try {
        val c = CommandCommunicator.getInstance
        val sm = c.getSecurityManager
        sm.getSessionManager.createAndRegisterNewSession()
        sm.getAuthenticationManager.loginToken(userPass)
      }
    }

  def deserializeSessionId(serialized: String): Option[SessionId] = {
    var in: ObjectInputStream = null
    try {
      in = new ObjectInputStream(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(serialized)))
      return Some(in.readObject().asInstanceOf[SessionId])
    } catch {
      case e: Exception => return None
    } finally {
      if (in != null) in.close()
    }
  }

  private def serializeSessionId(id: SessionId): String = {
    val baos = new ByteArrayOutputStream
    val out = new ObjectOutputStream(baos)
    out.writeObject(id)
    out.close()
    DatatypeConverter.printBase64Binary(baos.toByteArray)
  }
}
