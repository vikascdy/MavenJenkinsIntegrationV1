package com.edifecs.servicemanager.codegen

import java.lang.reflect.{Method, Modifier, ParameterizedType, TypeVariable}

import com.edifecs.epp.isc.stream.MessageStream
import com.edifecs.servicemanager.{annotations => sa}
import com.edifecs.epp.isc.{annotations => ca, Args, Isc}

import treehugger.forest._
import definitions._
import treehuggerDSL._

import java.lang.reflect.Type // Out of order so that it replaces treehugger's Type class.

object ProxyGenerator {
  
  import Isc.{proxyClassPrefix => proxyPrefix}

  final val warningComment = Seq(
    "GENERATED SOURCE FILE - DO NOT MODIFY",
    "---",
    "This proxy class is generated automatically during the build process based",
    "on the annotations in another source file. Any changes will be overwritten the",
    "next time the project is built.",
    "---")

  private final val addrMethodName = "address"
  private final val serviceTypeMethodName = "serviceType"

  def proxyForService(serviceClass: Class[_]): String = {
    if (!serviceClass.isAnnotationPresent(classOf[sa.Service])) {
      throw new IllegalArgumentException(
        s"The class $serviceClass is not annotated with ${classOf[sa.Service]}, and therefore" +
        " cannot be used to generate a service proxy class.")
    }
    val serviceType = serviceClass.getAnnotation(classOf[sa.Service]).name
    val serviceTypeMethod = VAL(serviceTypeMethodName) := LIT(serviceType)
    val methods = serviceClass.getMethods.filter{ m =>
      (m.getModifiers & Modifier.ABSTRACT) != 0 &&
      m.getGenericParameterTypes.find{
        case tv: TypeVariable[_] => tv.getGenericDeclaration.isInstanceOf[Class[_]]
        case _ => false
      }.isEmpty &&
      (m.getGenericReturnType match {
        case tv: TypeVariable[_] => !tv.getGenericDeclaration.isInstanceOf[Class[_]]
        case _ => true
      })
    }.map { m =>
      if (m.isAnnotationPresent(classOf[sa.Handler])) {
        proxifyHandlerGetterMethod(m)
      } else {
        stubMethod(m, "This method cannot be accessed remotely.")
      }
    }
    treeToString(BLOCK(
      CLASSDEF(proxyPrefix + serviceClass.getSimpleName)
        withParams(
          PARAM("isc", classOf[Isc]))
        withParents(serviceClass.getSimpleName)
        := BLOCK((serviceTypeMethod :: methods.toList): _*)
    ) inPackage(serviceClass.getPackage.getName) withComment(warningComment: _*))
  }

  def proxyForCommandHandler(handlerClass: Class[_]): String = {
    if (!handlerClass.isAnnotationPresent(classOf[ca.CommandHandler])) {
      throw new IllegalArgumentException(
        s"The class $handlerClass is not annotated with ${classOf[ca.CommandHandler]}, and" +
        " therefore cannot be used to generate a command handler proxy class.")
    }
    val namespace = handlerClass.getAnnotation(classOf[ca.CommandHandler]).namespace
    val addrMethod = DEF(addrMethodName) :=
      ((REF("isc") DOT "getAddressRegistry") DOT "getAddressForServiceTypeName") APPLY (
        REF("serviceTypeName"))
    val methods = handlerClass.getMethods.filter{ m =>
      (m.getModifiers & Modifier.ABSTRACT) != 0 &&
      m.getGenericParameterTypes.find{
        case tv: TypeVariable[_] => tv.getGenericDeclaration.isInstanceOf[Class[_]]
        case _ => false
      }.isEmpty &&
      (m.getGenericReturnType match {
        case tv: TypeVariable[_] => !tv.getGenericDeclaration.isInstanceOf[Class[_]]
        case _ => true
      })
    }.map { m =>
      if (m.isAnnotationPresent(classOf[ca.SyncCommand]) ||
          m.isAnnotationPresent(classOf[ca.Command])) {
        proxifySyncCommandMethod(m, namespace)
      } else if (m.isAnnotationPresent(classOf[ca.AsyncCommand])) {
        proxifyAsyncCommandMethod(m, namespace)
      } else {
        stubMethod(m, "This method cannot be accessed remotely.")
      }
    }
    treeToString(BLOCK(
      IMPORT(classOf[Args].getName),
      //IMPORT("com.edifecs.epp.isc.async.Implicits._"),
      CLASSDEF(proxyPrefix + handlerClass.getSimpleName)
        withParams(
          PARAM("isc", classOf[Isc]),
          PARAM("serviceTypeName", classOf[String]))
        withParents(handlerClass.getSimpleName)
        := BLOCK((addrMethod :: methods.toList): _*)
    ) inPackage(handlerClass.getPackage.getName) withComment(warningComment: _*))
  }

  private def proxifyHandlerGetterMethod(method: Method): Tree = {
    if (method.getParameterTypes.length > 0) {
      throw new IllegalArgumentException(
        s"The method ${method.getName} is annotated with ${classOf[sa.Handler]}, but takes" +
        s" ${method.getParameterTypes.length} argument(s). Handler getter methods must take 0" +
         " arguments.")
    }
    (VAL(method.getName) withFlags(Flags.OVERRIDE, Flags.LAZY) :=
      NEW(proxifyType(method.getReturnType), REF("isc"), REF(serviceTypeMethodName)))
  }

  private def proxifySyncCommandMethod(method: Method, namespace: String): Tree = {
    val arguments = extractCommandArgs(method)
    val nameSuffix = Option(method.getAnnotation(classOf[ca.SyncCommand])).map { a =>
      if (a.root) ""
      else if (a.name.isEmpty) method.getName
      else a.name
    }.getOrElse {
      Option(method.getAnnotation(classOf[ca.Command])).map{ a =>
        if (a.name.isEmpty) method.getName
        else a.name
      }.getOrElse(method.getName)
    }
    val name =
      if (namespace == Isc.defaultNamespace) nameSuffix
      else if (nameSuffix == "") namespace
      else namespace + Isc.commandSeparator + nameSuffix
    var body: Tree = (REF("isc") DOT "sendSync") APPLY(REF(addrMethodName), LIT(name), arguments)
    if (method.getReturnType() == classOf[java.io.InputStream]) {
      // Legacy InputStream method
      body = ((body DOT "asInstanceOf") APPLYTYPE classOf[MessageStream]) DOT "toInputStream"
    } else if (method.getReturnType != java.lang.Void.TYPE) {
      body = (body DOT "asInstanceOf") APPLYTYPE method.getGenericReturnType
    }
    (DEF(method.getName)
      withFlags(Flags.OVERRIDE)
      withTypeParams(method.getTypeParameters.map{ t =>
        TYPEVAR(t.getName): TypeDef // TODO: Handle type variable bounds.
      })
      withParams(extractParams(method))
      := body)
  }

  private def proxifyAsyncCommandMethod(method: Method, namespace: String): Tree = {
    val arguments = extractCommandArgs(method)
    val nameSuffix = Option(method.getAnnotation(classOf[ca.AsyncCommand])).map{ a =>
      if (a.root) ""
      else if (a.name.isEmpty) method.getName
      else a.name
    }.getOrElse(method.getName)
    val name =
      if (namespace == Isc.defaultNamespace) nameSuffix
      else if (nameSuffix == "") namespace
      else namespace + Isc.commandSeparator + nameSuffix
    val futureType = method.getGenericReturnType.asInstanceOf[ParameterizedType]
                           .getActualTypeArguments.head
    (DEF(method.getName)
      withFlags(Flags.OVERRIDE) 
      withTypeParams(method.getTypeParameters.map{ t =>
        TYPEVAR(t.getName): TypeDef // TODO: Handle type variable bounds.
      })
      withParams(extractParams(method))
      := ((REF("isc") DOT "send") APPLY(REF(addrMethodName), LIT(name), arguments)
           DOT "as") APPLY (REF("classOf") APPLYTYPE futureType))
  }

  private def stubMethod(method: Method, errorMessage: String): Tree = {
    (DEF(method.getName)
      withFlags(Flags.OVERRIDE) 
      withTypeParams(method.getTypeParameters.map{ t =>
        TYPEVAR(t.getName): TypeDef // TODO: Handle type variable bounds.
      })
      withParams(extractParams(method): _*)
      := THROW(NEW(classOf[UnsupportedOperationException], LIT(errorMessage))))
  }

  private def extractParams(method: Method): Seq[ValDef] = {
    var nextArgNum = -1
    (method.getParameterAnnotations zip method.getGenericParameterTypes) map { tuple =>
      val (anns, atype) = tuple
      anns.find(_.isInstanceOf[ca.Arg]).map { arg =>
        PARAM(mangle(arg.asInstanceOf[ca.Arg].name), atype).empty
      }.getOrElse {
        anns.find(_.isInstanceOf[ca.StreamArg]).map { arg =>
          PARAM(mangle(arg.asInstanceOf[ca.StreamArg].name), atype).empty
        }.getOrElse {
          nextArgNum += 1
          PARAM("$arg" + nextArgNum, atype).empty
        }
      }
    }
  }

  private def extractCommandArgs(method: Method): Tree = {
    var stream: Option[String] = None
    val arguments = (method.getParameterAnnotations zip method.getParameterTypes).flatMap { t =>
      val (anns, atype) = t
      anns.collect{
        case a: ca.Arg => (a.name, false)
        case a: ca.StreamArg => (a.name, true)
      }.headOption.map { t =>
        val (name, isStream) = t
        TUPLE(LIT(name), (if (isStream) {
            (typeToTree(classOf[MessageStream]) DOT "fromInputStream") APPLY REF(mangle(name))
          } else if (atype.isPrimitive || classOf[java.io.Serializable].isAssignableFrom(atype)) {
            REF(mangle(name))
          } else {
            (REF(mangle(name)) DOT "asInstanceOf") APPLYTYPE(classOf[java.io.Serializable])
          })
        ) :: Nil
      } getOrElse Nil
    }
    if (arguments.isEmpty) (REF("Map") DOT "empty") APPLYTYPE(TYPE_REF("String"), classOf[java.io.Serializable])
    else REF("Map") APPLYTYPE(TYPE_REF("String"), classOf[java.io.Serializable]) APPLY(arguments: _*)
  }

  private def proxifyType(t: Type): treehugger.forest.Type = t match {
    case pt: ParameterizedType =>
      Option(pt.getOwnerType).map { ownerType =>
        val c = pt.getRawType.asInstanceOf[Class[_]]
        TYPE_REF(TYPE_REF(typeToTree(ownerType) DOT (proxyPrefix + c.getSimpleName)) APPLYTYPE(
          pt.getActualTypeArguments.map(typeToTree): _*))
      } getOrElse {
        TYPE_REF(proxifyType(pt.getRawType) APPLYTYPE(
          pt.getActualTypeArguments.map(typeToTree): _*))
      }
    case c: Class[_] =>
      val parts = c.getCanonicalName.split("[.]")
      if (parts.length > 1) {
        var tree: Tree = REF(parts.head)
        (1 until parts.length) foreach { i =>
          if (i == parts.length - 1) {
            tree = tree DOT (proxyPrefix + parts(i))
          } else {
            tree = tree DOT parts(i)
          }
        }
        TYPE_REF(tree)
      } else {
        TYPE_REF(proxyPrefix + parts.head)
      }
    case _ =>
      throw new UnsupportedOperationException(
        s"The code generator does not know how to represent a ${t.getClass}.")
  }

  private def mangle(name: String): String =
    name.toCharArray.map { c =>
      if (c.isLetterOrDigit) {
        c.toString
      } else {
        "$" + c.intValue.toHexString
      }
    }.mkString("")

  private implicit def typeToTree(t: Type): treehugger.forest.Type = t match {
    case pt: ParameterizedType =>
      Option(pt.getOwnerType).map { ownerType =>
        val c = pt.getRawType.asInstanceOf[Class[_]]
        TYPE_REF(TYPE_REF(typeToTree(ownerType) DOT c.getSimpleName) APPLYTYPE(
          pt.getActualTypeArguments.map(typeToTree): _*))
      } getOrElse {
        TYPE_REF(typeToTree(pt.getRawType) APPLYTYPE(
          pt.getActualTypeArguments.map(typeToTree): _*))
      }
    case tv: TypeVariable[_] =>
      TYPE_REF(tv.getName)
    case c: Class[_] =>
      if (c.isPrimitive) {
        javaPrimitivesToScalaPrimitives(c)
      } else if (c.isArray) {
        TYPE_REF(TYPE_REF("Array") APPLYTYPE typeToTree(c.getComponentType))
      } else {
        val parts = c.getCanonicalName.split("[.]")
        var tree: Tree = REF(parts.head)
        parts.tail foreach {name => tree = tree DOT name}
        TYPE_REF(tree)
      }
    case _ =>
      throw new UnsupportedOperationException(
        s"The code generator does not know how to represent a ${t.getClass}.")
  }

  final val javaPrimitivesToScalaPrimitives = Map[Class[_], treehugger.forest.Type](
    java.lang.Long.TYPE      -> TYPE_REF("Long"),
    java.lang.Integer.TYPE   -> TYPE_REF("Int"),
    java.lang.Short.TYPE     -> TYPE_REF("Short"),
    java.lang.Byte.TYPE      -> TYPE_REF("Byte"),
    java.lang.Double.TYPE    -> TYPE_REF("Double"),
    java.lang.Float.TYPE     -> TYPE_REF("Float"),
    java.lang.Boolean.TYPE   -> TYPE_REF("Boolean"),
    java.lang.Character.TYPE -> TYPE_REF("Char"),
    java.lang.Void.TYPE      -> TYPE_REF("Unit")
  )
}

