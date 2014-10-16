package com.edifecs.epp.isc.json

import java.lang.reflect._
import scala.collection.immutable._
import scala.collection.mutable.Map
import scala.collection.JavaConversions._
import com.google.gson._

sealed abstract class Schema extends JsonWritable {
  private var id: Option[String] = None
  final def withId(id: String): Schema = {this.id = Some(id); this}
  private var description: Option[String] = None
  final def withDescription(desc: String): Schema = {this.description = Some(desc); this}
  protected var defName: Option[String] = None
  final def withDefName(name: String): Schema = {this.defName = Some(name); this}
  private var enum: Seq[JsonElement] = Nil
  final def withEnum(choices: JsonElement*): Schema = {this.enum = Seq(choices: _*); this}
  
  protected def _toJson(
    defnObject: JsonObject,
    defnMap: Map[Schema, String]
  ): JsonObject
  
  protected[json] final def toJson(
    defnObject: JsonObject,
    defnMap: Map[Schema, String]
  ): JsonObject = {
    val o = _toJson(defnObject, defnMap)
    if (!enum.isEmpty) {
      val a = new JsonArray
      enum.foreach(a.add(_))
      o.add("enum", a)
    }
    o
  }
  
  override final def toJson: JsonObject = {
    val definitions = new JsonObject
    val o = new JsonObject
    val result = toJson(definitions, Map[Schema, String]())
    o.addProperty("$schema", "http://json-schema.org/draft-04/schema#")
    if (!definitions.entrySet().isEmpty) {
      o.add("definitions", definitions)
    }
    id.foreach(o.addProperty("id", _))
    description.foreach(o.addProperty("description", _))
    result.entrySet.foreach(e => o.add(e.getKey, e.getValue))
    o
  }
  
  override final lazy val getJsonSchema = Schema.schemaForSchema
  override def toString = Schema.gson.toJson(toJson)
}

object Schema {

  private type JavaBoolean = scala.Boolean
  private type JavaString = java.lang.String
  type Element = Schema

  def fromType(t: Type): Element = fromType(t, Map.empty, Map.empty)

  private def fromType(
    t: Type,
    typeVariables: Map[JavaString, Type],
    namedTypes: Map[JavaString, Element]
  ): Element = t match {
    case c: Class[_] =>
      if (c == classOf[Long]) {
        Integer
      } else if (c == classOf[Int]) {
        Integer.withMinimum(Int.MinValue, true).withMaximum(Int.MaxValue, true)
      } else if (c == classOf[Short]) {
        Integer.withMinimum(Short.MinValue, true).withMaximum(Short.MaxValue, true)
      } else if (c == classOf[Byte]) {
        Integer.withMinimum(Byte.MinValue, true).withMaximum(Byte.MaxValue, true)
      } else if (c == classOf[Char]) {
        Integer.withMinimum(Char.MinValue, true).withMaximum(Char.MaxValue, true)
      } else if (c == classOf[Float] || c == classOf[Double]) {
        Number
      } else if (c == classOf[JavaBoolean]) {
        Boolean
      } else if (c == classOf[JavaString]) {
        TypeUnion("string", "null")
      } else if (c == classOf[java.lang.Long]) {
        TypeUnion("integer", "null")
      } else if (c == classOf[java.lang.Integer]) {
        OneOf(fromType(classOf[Int]), Null)
      } else if (c == classOf[java.lang.Short]) {
        OneOf(fromType(classOf[Short]), Null)
      } else if (c == classOf[java.lang.Byte]) {
        OneOf(fromType(classOf[Byte]), Null)
      } else if (c == classOf[java.lang.Character]) {
        OneOf(fromType(classOf[Char]), Null)
      } else if (c == classOf[java.lang.Float] || c == classOf[java.lang.Double]) {
        TypeUnion("number", "null")
      } else if (c == classOf[java.lang.Boolean]) {
        TypeUnion("boolean", "null")
      } else if (classOf[java.util.Collection[_]].isAssignableFrom(c)) {
        TypeUnion("array", "null")
      } else if (classOf[java.util.Properties].isAssignableFrom(c)) {
        OneOf(new Object().withAdditionalProperties(new String()), Null)
      } else if (c.isArray) {
        OneOf(
          new Array().withAllItems(fromType(c.getComponentType, typeVariables, namedTypes)),
          Null)
      } else if (c.getEnumConstants != null && !c.getEnumConstants.isEmpty) {
        fromEnumType(c.asInstanceOf[Class[_ <: Enum[_]]])
      } else if (c == classOf[AnyVal]) {
        TypeUnion("number", "boolean")
      } else if (c == classOf[Any] ||
                 c == classOf[AnyRef] ||
                 c == classOf[java.lang.Object] ||
                 c == classOf[java.io.Serializable]) {
        Wildcard
      } else {
        OneOf(Ref(fromUnknownType(c, typeVariables, namedTypes)), Null)
      }
    case pt: ParameterizedType =>
      pt.getRawType match {
        case c: Class[_] =>
          // FIXME: Need a smarter way to identify all collection and map types.
          if (classOf[java.util.Collection[_]].isAssignableFrom(c) &&
              pt.getActualTypeArguments.length == 1) {
            OneOf(
              Array().withAllItems(
                fromType(pt.getActualTypeArguments()(0), typeVariables, namedTypes)),
              Null)
          } else if (classOf[java.util.Map[String, _]].isAssignableFrom(c) &&
                     pt.getActualTypeArguments.length == 2 &&
                     pt.getActualTypeArguments()(0) == classOf[JavaString]) {
            OneOf(
              Object().withAdditionalProperties(
                fromType(pt.getActualTypeArguments()(1), typeVariables, namedTypes)),
              Null)
          } else {
            fromType(c, typeVariables ++ Map(
              c.getTypeParameters.map(_.getName) zip pt.getActualTypeArguments: _*),
              namedTypes)
          }
        case _ =>
          Wildcard
      }
    case ga: GenericArrayType =>
      OneOf(
        Array().withAllItems(fromType(ga.getGenericComponentType, typeVariables, namedTypes)),
        Null)
    case tv: TypeVariable[_] =>
      typeVariables.get(tv.getName).map(fromType(_, typeVariables, namedTypes)).getOrElse(Wildcard)
    // TODO: Handle WildcardTypes.
    case _ =>
      Wildcard
  }

  private def fromEnumType(cls: Class[_ <: Enum[_]]): Element = {
    // TODO: Handle GSON annotations.
    String.withEnum(cls.getEnumConstants.map(e => new JsonPrimitive(e.name)) :+ new JsonNull: _*)
  }

  private def fromUnknownType(
    cls: Class[_],
    typeVariables: Map[JavaString, Type],
    namedTypes: Map[JavaString, Element]
  ): Element = {
    // TODO: Handle GSON annotations.
    val name = nameFor(cls, typeVariables)
    if (namedTypes contains name) return namedTypes(name)
    val el = new Object()
    el.withDefName(nameFor(cls, typeVariables))
    if ((cls.getModifiers & Modifier.FINAL) != 0) {
      el.withNoAdditionalProperties
    }
    val newNamedTypes = namedTypes ++ Map(name -> el)
    var classes: Seq[Class[_]] = Seq(cls)
    while (classes.last.getSuperclass != null) classes = classes :+ classes.last.getSuperclass
    var usedNames: Set[JavaString] = Set.empty
    classes.flatMap(_.getDeclaredFields).filter { f =>
      !(usedNames contains f.getName) &&
      (f.getModifiers & Modifier.TRANSIENT) == 0 &&
      (f.getModifiers & Modifier.STATIC) == 0
    }.foldLeft(el) { (e, f) =>
      usedNames = usedNames + f.getName
      e.withProperty(f.getName, fromType(f.getGenericType, typeVariables, newNamedTypes))
    }
  }

  private def nameFor(cls: Class[_], typeVariables: Map[JavaString, Type]): JavaString =
    if (cls.getTypeParameters.isEmpty) {
      cls.getName
    } else {
      cls.getName + "<" + cls.getTypeParameters.map { v =>
        typeVariables.get(v.getName).get.toString
      }.mkString(", ") + ">"
    }

  private lazy val gson = new GsonBuilder().setPrettyPrinting().create()

  class Number extends Element {

    private var multipleOf: Option[Double] = None
    def withMultipleOf(n: Double) = {multipleOf = Some(n); this}
    private var minimum: Option[Double] = None
    private var exclusiveMin = false
    def withMinimum(min: Double, exclusive: JavaBoolean): Number = {
      minimum = Some(min)
      exclusiveMin = exclusive
      this
    }
    private var maximum: Option[Double] = None
    private var exclusiveMax = false
    def withMaximum(max: Double, exclusive: JavaBoolean): Number = {
      maximum = Some(max)
      exclusiveMax = exclusive
      this
    }

    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "number")
      multipleOf.foreach(o.addProperty("multipleOf", _))
      maximum foreach {n =>
        o.addProperty("maximum", n)
        o.addProperty("exclusiveMaximum", exclusiveMax)
      }
      minimum foreach {n =>
        o.addProperty("minimum", n)
        o.addProperty("exclusiveMinimum", exclusiveMin)
      }
      o
    }
  }
  def Number = new Number

  class Integer extends Element {

    private var multipleOf: Option[Int] = None
    def withMultipleOf(n: Int) = {multipleOf = Some(n); this}
    private var minimum: Option[Int] = None
    private var exclusiveMin = false
    def withMinimum(min: Int, exclusive: JavaBoolean): Integer = {
      minimum = Some(min)
      exclusiveMin = exclusive
      this
    }
    private var maximum: Option[Int] = None
    private var exclusiveMax = false
    def withMaximum(max: Int, exclusive: JavaBoolean): Integer = {
      maximum = Some(max)
      exclusiveMax = exclusive
      this
    }

    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "integer")
      multipleOf.foreach(o.addProperty("multipleOf", _))
      maximum foreach {n =>
        o.addProperty("maximum", n)
        o.addProperty("exclusiveMaximum", exclusiveMax)
      }
      minimum foreach {n =>
        o.addProperty("minimum", n)
        o.addProperty("exclusiveMinimum", exclusiveMin)
      }
      o
    }
  }
  def Integer = new Integer

  class String extends Element {

    private var maxLength: Option[Int] = None
    def withMaxLength(n: Int): String = {maxLength = Some(n); this}
    private var minLength: Option[Int] = None
    def withMinLength(n: Int): String = {minLength = Some(n); this}
    private var pattern: Option[JavaString] = None
    def withPattern(p: JavaString): String = {pattern = Some(p); this}

    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "string")
      maxLength.foreach(o.addProperty("maxLength", _))
      minLength.foreach(o.addProperty("minLength", _))
      pattern.foreach(o.addProperty("pattern", _))
      o
    }
  }
  def String = new String

  class Array(initialItems: Element*) extends Element {

    private var items: Seq[Element] = Seq.empty
    def withItem(item: Element) = {items = items :+ item; this}
    private var allItems: Option[Element] = None
    def withAllItems(pattern: Element) = {allItems = Some(pattern); this}
    private var additionalItems: Option[Element] = None
    def withAdditionalItems(pattern: Element) = {additionalItems = Some(pattern); this}
    private var noAdditionalItems = false
    def withNoAdditionalItems = {noAdditionalItems = true; this}

    if (initialItems.size == 1) {
      allItems = initialItems.headOption
    } else if (initialItems.size > 1) {
      items = Seq(initialItems: _*)
    }

    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "array")
      if (allItems.isDefined) {
        allItems.foreach(e => o.add("items", e.toJson(defnObject, defnMap)))
      } else if (!items.isEmpty) {
        val a = new JsonArray
        items.foreach(i => a.add(i.toJson(defnObject, defnMap)))
        o.add("items", a)
      }
      if (noAdditionalItems) {
        o.addProperty("additionalItems", false)
      } else {
        additionalItems.foreach(e => o.add("additionalItems", e.toJson(defnObject, defnMap)))
      }
      o
    }
  }
  def Array() = new Array()
  def Array(allItems: Element) = new Array(allItems)
  def Array(first: Element, rest: Element*) = new Array(first :: rest.toList: _*)
  def Array(initialItems: scala.Array[Element]) = new Array(initialItems: _*)

  class Object(initialProperties: (JavaString, Element)*) extends Element {

    private var properties: Map[JavaString, Element] = Map(initialProperties: _*)
    private var requiredProperties: Set[JavaString] = Set.empty
    def withRequiredProperty(name: JavaString, schema: Element): Object = {
      properties += name -> schema
      requiredProperties = requiredProperties + name
      this
    }
    def withProperty(name: JavaString, schema: Element): Object = {
      properties += name -> schema
      this
    }
    private var additionalProperties: Option[Element] = None
    def withAdditionalProperties(pattern: Element): Object = {
      additionalProperties = Some(pattern)
      this
    }
    private var noAdditionalProperties = false
    def withNoAdditionalProperties: Object = {noAdditionalProperties = true; this}

    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "object")
      if (!properties.isEmpty) {
        val p = new JsonObject
        properties.foreach(e => p.add(e._1, e._2.toJson(defnObject, defnMap)))
        o.add("properties", p)
      }
      if (!requiredProperties.isEmpty) {
        val a = new JsonArray
        requiredProperties.foreach(p => a.add(new JsonPrimitive(p)))
        o.add("required", a)
      }
      if (noAdditionalProperties) {
        o.addProperty("additionalProperties", false)
      } else {
        additionalProperties.foreach(e =>
          o.add("additionalProperties", e.toJson(defnObject, defnMap)))
      }
      o
    }
  }
  def Object() = new Object()
  def Object(first: (JavaString, Element), rest: (JavaString, Element)*) =
    new Object(first :: rest.toList: _*)
  def Object(initialProperties: java.util.Map[JavaString, Element]) =
    new Object(initialProperties.entrySet.map(e => (e.getKey, e.getValue)).toSeq: _*)

  object _Boolean extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "boolean")
      o
    }
  }
  def Boolean = _Boolean

  object _Null extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val o = new JsonObject
      o.addProperty("type", "null")
      o
    }
  }
  def Null = _Null

  object _Wildcard extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ) = new JsonObject
  }
  def Wildcard = _Wildcard

  class AllOf(choices: Element*) extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val (o, a) = (new JsonObject, new JsonArray)
      choices.foreach(e => a.add(e.toJson(defnObject, defnMap)))
      o.add("allOf", a)
      o
    }
  }
  def AllOf(a: Element, b: Element) = new AllOf(a, b)
  def AllOf(a: Element, b: Element, c: Element) = new AllOf(a, b, c)
  def AllOf(a: Element, b: Element, c: Element, d: Element) = new AllOf(a, b, c, d)
  def AllOf(a: Element, b: Element, c: Element, d: Element, rest: Element*) =
    new AllOf(a :: b :: c :: d :: rest.toList: _*)
  def AllOf(choices: scala.Array[Element]) = new AllOf(choices: _*)

  class AnyOf(choices: Element*) extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val (o, a) = (new JsonObject, new JsonArray)
      choices.foreach(e => a.add(e.toJson(defnObject, defnMap)))
      o.add("anyOf", a)
      o
    }
  }
  def AnyOf(a: Element, b: Element) = new AnyOf(a, b)
  def AnyOf(a: Element, b: Element, c: Element) = new AnyOf(a, b, c)
  def AnyOf(a: Element, b: Element, c: Element, d: Element) = new AnyOf(a, b, c, d)
  def AnyOf(a: Element, b: Element, c: Element, d: Element, rest: Element*) =
    new AnyOf(a :: b :: c :: d :: rest.toList: _*)
  def AnyOf(choices: scala.Array[Element]) = new AnyOf(choices: _*)

  class OneOf(choices: Element*) extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val (o, a) = (new JsonObject, new JsonArray)
      choices.foreach(e => a.add(e.toJson(defnObject, defnMap)))
      o.add("oneOf", a)
      o
    }
  }
  def OneOf(a: Element, b: Element) = new OneOf(a, b)
  def OneOf(a: Element, b: Element, c: Element) = new OneOf(a, b, c)
  def OneOf(a: Element, b: Element, c: Element, d: Element) = new OneOf(a, b, c, d)
  def OneOf(a: Element, b: Element, c: Element, d: Element, rest: Element*) =
    new OneOf(a :: b :: c :: d :: rest.toList: _*)
  def OneOf(choices: scala.Array[Element]) = new OneOf(choices: _*)

  class TypeUnion(types: JavaString*) extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      val (o, a) = (new JsonObject, new JsonArray)
      types.foreach(t => a.add(new JsonPrimitive(t)))
      o.add("type", a)
      o
    }
  }
  def TypeUnion(a: JavaString, b: JavaString) = new TypeUnion(a, b)
  def TypeUnion(a: JavaString, b: JavaString, c: JavaString) = new TypeUnion(a, b, c)
  def TypeUnion(a: JavaString, b: JavaString, c: JavaString, d: JavaString) = new TypeUnion(a, b, c, d)
  def TypeUnion(a: JavaString, b: JavaString, c: JavaString, d: JavaString, rest: JavaString*) =
    new TypeUnion(a :: b :: c :: d :: rest.toList: _*)
  def TypeUnion(choices: scala.Array[JavaString]) = new TypeUnion(choices: _*)

  class Ref(referenced: => Element) extends Element {
    override def _toJson(
      defnObject: JsonObject,
      defnMap: Map[Element, JavaString]
    ): JsonObject = {
      if (!(defnMap contains referenced)) {
        val name = referenced.defName.getOrElse {
          val rnd = new java.util.Random
          var n = "s" + rnd.nextInt()
          while (defnMap containsValue n) n = "s" + rnd.nextInt()
          n
        }
        defnMap.put(referenced, name)
        defnObject.add(name, referenced.toJson(defnObject, defnMap))
      }
      val o = new JsonObject
      o.addProperty("$ref", "#/definitions/" + defnMap(referenced))
      o
    }
  }
  def LazyRef(referenced: => Element) = new Ref(referenced)
  def Ref(referenced: Element) = new Ref(referenced)

  lazy val schemaForSchema = {
    val typeSchema = String.withEnum(
      Seq("number", "integer", "string", "boolean", "array", "object", "null")
        .map(new JsonPrimitive(_)): _*).withDefName("typeName")
    lazy val coreSchema: Schema = OneOf(
      Object().withRequiredProperty("$ref", String),
      Object().withRequiredProperty("allOf", Array(LazyRef(coreSchema))),
      Object().withRequiredProperty("anyOf", Array(LazyRef(coreSchema))),
      Object().withRequiredProperty("oneOf", Array(LazyRef(coreSchema))),
      Object().withRequiredProperty("not", LazyRef(coreSchema)),
      Object(
        "enum" -> Array(),
        "multipleOf" -> Number,
        "maximum" -> Number,
        "exclusiveMaximum" -> Boolean,
        "minimum" -> Number,
        "exclusiveMinimum" -> Boolean,
        "maxLength" -> Integer.withMinimum(0, false),
        "minLength" -> Integer.withMinimum(0, false),
        "pattern" -> String,
        "items" -> OneOf(LazyRef(coreSchema), Array(LazyRef(coreSchema))),
        "additionalItems" -> OneOf(Boolean, LazyRef(coreSchema)),
        "minItems" -> Integer.withMinimum(0, false),
        "maxItems" -> Integer.withMinimum(0, false),
        "uniqueItems" -> Boolean,
        "properties" -> Object().withAdditionalProperties(LazyRef(coreSchema)),
        "additionalProperties" -> Object().withAdditionalProperties(LazyRef(coreSchema)),
        "patternProperties" -> Object().withAdditionalProperties(LazyRef(coreSchema)),
        "minProperties" -> Integer.withMinimum(0, false),
        "maxProperties" -> Integer.withMinimum(0, false),
        "required" -> Array(String)
      ).withRequiredProperty(
        "type", OneOf(Ref(typeSchema), Array(Ref(typeSchema))))
    ).withDefName("schema")

    AllOf(
      Object(
        "id" -> String,
        "description" -> String,
        "definitions" -> Object().withAdditionalProperties(Ref(coreSchema))
      ).withRequiredProperty("$schema", String),
      Ref(coreSchema))
  }
}
