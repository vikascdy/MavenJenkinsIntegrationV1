# Unified Edifecs Exception Classes

## EppException

`final class EppException extends RuntimeException`

The new core exception class for all Edifecs-specific exceptions. This exception should never be thrown directly; instead, subclasses of `ExceptionTemplate` should be thrown, and these will be converted to `EppException`s at various places in the SM/ISC code.

`EppException` replaces `MessageException` as a single common exception class that serializes any cause exceptions as `String`s, and that can be passed between nodes without any classpath concerns.

### Fields

 * `originalClassName` - The name of the `ExceptionTemplate` class from which this `EppException` was created. Functions as a unique identifier for this exception type. In some cases, this may be the name of a non-`ExceptionTemplate` exception class (for example, if this `EppException` was converted from a standard `RuntimeException`, such as an `UnsupportedOperationException`).
 
 * `properties` - An immutable `Map<String, String>` containing any properties passed to the `ExceptionTemplate` from which this `EppException` was created. Although these properties are usually inserted into the error message, this map can be used to extract their exact values if necessary.

 * `userMessage` - The message to be displayed to the user. Will be translated into the user's language, if a translation is available.
 
 * `logMessage` - The message to be output to the node's log file. Will be translated into the system language, if a translation is available.

 * `userLanguage` - A two-letter [ISO 639-1][iso6391] language code indicating the language of the `userMessage`.
 
 * `logLanguage` - A two-letter [ISO 639-1][iso6391] language code indicating the language of the `logMessage`.
 
 * `httpStatus` - The HTTP status code that this error represents, if it is returned as the result of a REST command. Defaults to 500.
 
 * `cause` - The same as the `cause` field of a standard `Exception`, except that an `EppException`'s `cause`, if non-null, is *always* another `EppException`.

## ExceptionTemplate

`abstract class ExceptionTemplate extends RuntimeException`

An abstract base class for `EppException` templates. Exception templates don't have messages; instead, they have a class name and a `Map<String, String>` of properties, and, upon conversion to `EppException`s, a language-specific message is added and populated with the contents of the properties map.

### Fields

 * `properties` - An immutable `Map<String, String>` containing properties which may be interpolated into this exception's language-specific message, or which provide supplementary information about the exception.
 
 * `httpStatus` - The HTTP status code that this error represents, if it is returned as the result of a REST command. Defaults to 500.
 
 * `cause` - May be any `Exception`; will be converted to an `EppException` when the `ExceptionTemplate` itself is converted.

### Message Properties Files

When an `ExceptionTemplate` is converted to an `EppException`, its language-specific messages are pulled from properties files located in the `/exception-messages` directory of the JAR in which the `ExceptionTemplate` subclass being converted is contained. Each properties file is named `xx.properties`, where `xx` is the two-letter [ISO 639-1][iso6391] language code of the file's language:

    exception-messages/
      en.properties
      es.properties
      fr.properties

Each properties file uses fully-qualified class names (of `ExceptionTemplate` subclasses) as keys, and exception messages as values. The exception messages may contain property keys surrounded by `{}`, which will be replaced with property values when the `ExceptionTemplate` is converted to an `EppException`.

    # An example en.properties file
    com.edifecs.test.ExampleException = This is an example exception.
    com.edifecs.test.FileNotFoundException = The file {fileName} does not exist.

When searching for a message string, the system first looks in the language-specific properties file for the language of the currently-logged-in EIM user; if the file does not exist or does not contain the relevant key, the system looks in `en.properties` next; if the key is still not found, a default "debug" message is used, which includes the class name and the full contents of the properties map.

[iso6391]: http://en.wikipedia.org/wiki/ISO_639-1