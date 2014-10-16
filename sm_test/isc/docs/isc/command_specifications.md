# Command Specifications

Command specifications are a form of dynamically-generated documentation available from any running cluster, allowing clients to learn about services' APIs even when source code and/or documentation is unavailable. A `CommandSpecification` object describes a single command, including its name, argument names and types, return type, and a text description (if available).

## Obtaining Command Specifications

Command specifications can be obtained from any `Isc` instance (such as a `CommandCommunicator`) by calling the `getAvailableCommands` method, which returns a `Map` from service addresses to `List`s of `CommandSpecification`s representing the commands available at the address.

## Structure

A `CommandSpecification` is a Scala [case class][caseclass], with the following members:

 * `name` - The name of the command.

 * `arguments` - A Scala `Seq` of `ArgumentSpecification`s, representing the command's arguments. An `ArgumentSpecification` is a Scala [case class][caseclass], with the following members:

      * `name` - The name of the argument.

      * `description` - A text description of the argument. Pulled from the `description` attribute of the argument's `@Arg` or `@StreamArg` annotation.

      * `required` - A boolean value; if false, the argument may be omitted when calling the command.

      * `stream` - A boolean value; if true, the argument is a stream argument. When making a REST call, a stream argument must be passed as part of a multipart form request body.

      * `typeName` - The fully-qualified class name of the argument's type.

      * `schema` - An object representing a [JSON Schema][schema], which describes the JSON format that the argument must match if sent via REST. The schema object can be converted to a JSON tree with its `toJson` method. Not applicable to non-REST commands.

 * `serviceTypeName` - The name of the service type that defines this command.

 * `description` - A text description of the command. Pulled from the `description` attribute of the command method's `@AsyncCommand` or `@SyncCommand` annotation.

 * `url` - A Scala `Option` which may contain a REST URL through which this command can be accessed. If this `CommandSpecification` was obtained through `Isc.getAvailableCommands`, this will always be `None`.

 * `accessibleBy` - An [`EnumSet`][enumset] of `CommandSource` objects, which list the methods (Akka, REST, etc.) by which this command may be accessed.

 * `responseTypeName` - The fully-qualified class name of the command's response type.

 * `responseSchema` - An object representing a [JSON Schema][schema], which describes the JSON format of the command's response type. The schema object can be converted to a JSON tree with its `toJson` method. Not applicable to non-REST commands.

[schema]: http://json-schema.org/
[caseclass]: http://www.scala-lang.org/old/node/107
[enumset]: http://docs.oracle.com/javase/7/docs/api/java/util/EnumSet.html

## JSON Format

REST interfaces to the cluster (such as `spray-service`) may provide JSON-encoded command specifications through a documentation URL. An example of JSON command specifications:

    [
      {
        "name": "echoUser",
        "description": "No description available.",
        "url": "/rest/service/Echo+Service/echoUser",
        "arguments": [],
        "accessibleBy": [
          "Akka",
          "REST"
        ],
        "responseType": "java.lang.String",
        "responseSchema": {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "type": [
            "string",
            "null"
          ]
        }
      },
      {
        "name": "echoArg",
        "description": "No description available.",
        "url": "/rest/service/Echo+Service/echoArg",
        "arguments": [
          {
            "name": "arg",
            "description": "the argument to echo",
            "required": true,
            "type": "java.lang.String",
            "schema": {
              "$schema": "http://json-schema.org/draft-04/schema#",
              "type": [
                "string",
                "null"
              ]
            }
          }
        ],
        "accessibleBy": [
          "Akka",
          "REST"
        ],
        "responseType": "java.lang.String",
        "responseSchema": {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "type": [
            "string",
            "null"
          ]
        }
      },
      {
        "name": "echoError",
        "description": "No description available.",
        "url": "/rest/service/Echo+Service/echoError",
        "arguments": [
          {
            "name": "message",
            "description": "the error message",
            "required": true,
            "type": "java.lang.String",
            "schema": {
              "$schema": "http://json-schema.org/draft-04/schema#",
              "type": [
                "string",
                "null"
              ]
            }
          }
        ],
        "accessibleBy": [
          "Akka",
          "REST"
        ],
        "responseType": "java.lang.Throwable",
        "responseSchema": {
          "$schema": "http://json-schema.org/draft-04/schema#",
          "definitions": {
            "java.lang.StackTraceElement": {
              "type": "object",
              "properties": {
                "declaringClass": {
                  "type": [
                    "string",
                    "null"
                  ]
                },
                "lineNumber": {
                  "type": "integer",
                  "maximum": 2147483647,
                  "exclusiveMaximum": true,
                  "minimum": -2147483648,
                  "exclusiveMinimum": true
                },
                "methodName": {
                  "type": [
                    "string",
                    "null"
                  ]
                },
                "fileName": {
                  "type": [
                    "string",
                    "null"
                  ]
                }
              },
              "additionalProperties": false
            },
            "java.lang.Throwable": {
              "type": "object",
              "properties": {
                "cause": {
                  "oneOf": [
                    {
                      "$ref": "#/definitions/java.lang.Throwable"
                    },
                    {
                      "type": "null"
                    }
                  ]
                },
                "detailMessage": {
                  "type": [
                    "string",
                    "null"
                  ]
                },
                "suppressedExceptions": {
                  "oneOf": [
                    {
                      "type": "array",
                      "items": {
                        "oneOf": [
                          {
                            "$ref": "#/definitions/java.lang.Throwable"
                          },
                          {
                            "type": "null"
                          }
                        ]
                      }
                    },
                    {
                      "type": "null"
                    }
                  ]
                },
                "stackTrace": {
                  "oneOf": [
                    {
                      "type": "array",
                      "items": {
                        "oneOf": [
                          {
                            "$ref": "#/definitions/java.lang.StackTraceElement"
                          },
                          {
                            "type": "null"
                          }
                        ]
                      }
                    },
                    {
                      "type": "null"
                    }
                  ]
                }
              }
            }
          },
          "oneOf": [
            {
              "$ref": "#/definitions/java.lang.Throwable"
            },
            {
              "type": "null"
            }
          ]
        }
      }
    ]

