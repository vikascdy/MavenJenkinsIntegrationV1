// GENERATED SOURCE FILE - DO NOT MODIFY
// ---
// This proxy class is generated automatically during the build process based
// on the annotations in another source file. Any changes will be overwritten the
// next time the project is built.
// ---
package com.edifecs.epp.security

import com.edifecs.epp.isc.Args

class __GeneratedProxy__IAuthenticationManager(isc: com.edifecs.epp.isc.Isc, serviceTypeName: java.lang.String) extends IAuthenticationManager {
  def address = isc.getAddressRegistry.getAddressForServiceTypeName(serviceTypeName)
  override def initiatePasswordReset(email: java.lang.String) = isc.sendSync(address, "user.sendResetPasswordEmail", Map[String, java.io.Serializable](("email", email))).asInstanceOf[Boolean]
  override def updatePassword(newPasswd: java.lang.String, token: java.lang.String) = isc.sendSync(address, "password.updatePassword", Map[String, java.io.Serializable](("newPasswd", newPasswd), ("token", token))).asInstanceOf[Boolean]
  override def loginToken(authenticationToken: org.apache.shiro.authc.AuthenticationToken) = isc.sendSync(address, "loginToken", Map[String, java.io.Serializable](("authenticationToken", authenticationToken))).asInstanceOf[com.edifecs.epp.security.SessionId]
  override def login(subject: org.apache.shiro.subject.Subject, authenticationToken: org.apache.shiro.authc.AuthenticationToken, username: java.lang.String, password: java.io.Serializable, domain: java.lang.String, organization: java.lang.String, remember: java.lang.Boolean) = isc.sendSync(address, "login", Map[String, java.io.Serializable](("subject", subject.asInstanceOf[java.io.Serializable]), ("authenticationToken", authenticationToken), ("username", username), ("password", password), ("domain", domain), ("organization", organization), ("remember", remember))).asInstanceOf[com.edifecs.epp.security.SessionId]
  override def logout = isc.sendSync(address, "logout", Map.empty[String, java.io.Serializable])
  override def getSubjectPrincipals = isc.sendSync(address, "getSubjectPrincipals", Map.empty[String, java.io.Serializable]).asInstanceOf[org.apache.shiro.subject.PrincipalCollection]
  override def isSubjectAuthenticated = isc.sendSync(address, "isSubjectAuthenticated", Map.empty[String, java.io.Serializable]).asInstanceOf[Boolean]
  override def loginCertificate(domain: java.lang.String, organization: java.lang.String, certificate: java.lang.String, username: java.lang.String) = isc.sendSync(address, "loginCertificate", Map[String, java.io.Serializable](("domain", domain), ("organization", organization), ("certificate", certificate), ("username", username))).asInstanceOf[com.edifecs.epp.security.SessionId]
}