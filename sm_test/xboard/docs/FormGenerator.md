# Form Generator

Form Generator is a widget that creates dynamic forms from application supplied JSON.

## Usage
Using form generator requires that the edifecs-plugins directory of xboard be included in the application.
Note that future releases will break XBoard widgets out from the edifecs-plugins directory into their
own storage.

## Referencing FormGenerator Components
Two code files must be included for FormGenerator to work.  The first is the WidgetUtils class, which
holds functionality common to all XBoard widgets.  The second is the form generator widget class.  Typically,
this is done in the app.js of the client application:



    Ext.Loader.setPath({
        'Util': 'app/util',
        'Edifecs':'edifecs-plugins'
    });
    
    Ext.application({
        requires: [
            'Edifecs.DoormatNavigation',
            'Edifecs.DoormatApplicationBar',
            'Edifecs.Notifications',
            'Edifecs.Favourites',
            'Edifecs.FormGenerator',
            'Edifecs.WidgetUtils'
        ],
        /*other config*/
     });

## Inclusion and Configuration
**TODO--the configuration is currently over complex and will be simplified.  It will also be better documented after that occurs**

The FormGenerator widget is a class that extends Ext.container.Container and can be included
in the client application just like any other ExtJS container.  The xtype of the formgenerator component is *formfieldcomponent*

     items:[
                    {
                        xtype:'formfieldcomponent',
                        layout:'anchor',
                        defaults:{'anchor':'50%'},
                        margins:{top:10, right:10, bottom:10, left:0},
                        configUrl:'resources/json/TestFormFieldConfiguration.json',
                        entityId:1,
                        flex:1,
                        saveFieldEvent: "saveFieldEvent",
                        saveFormEvent: "saveFormEvent",
                        listeners: {
                            customSaveEvent: function(comp, val, old, id){
                                //handle event
                            },
                            customChangeEvent: function(comp, val, old, id){
                                //handle event
                            }
                        }
                    }
                ]

The most important configuration option is the path to the configuration JSON file.  The Form Generator performs an 
HTTP GET on that URL, so the JSON can be housed as either a static local file or can be a REST endpoint.  A typical configuration
looks like this:

    {
        "formType": "ReadOnly",
        "submissionType":"Event",
        "formGroupUrl":"http://localhost:10098/xboard/sample-service-get2",
        "saveValueUrl":"http://localhost:10098/xboard/saveField",
        "saveFormUrl": "http://localhost:10098/xboard/saveForm",
        "enableRootHeader":false,
        "tenantName":"*",
        "appName":"ESM",
        "componentName":"*",
        "entityName":"UserEntity",
        "entityId": 1,
        "saveEvent":"customFormSave",
        "changeEvent":"customChangeEvent"
    }
    
Important elements are: 

*  *formType*-The form Generator operates in three modes 

      * ReadOnly-the form labels and values are displayed as text and are not editable.
      * Inline-the form labels and values are displayed but become editable when clicked. On enter, the new values are posted to the server..
      * Submission-The form values are presented as traditional forms with a save button.
*  *SubmissionType*-Whether the form is submitted to a URL or fires an event with the save information (Event | URL)
*  *formGroupURL*-The URL that supplies the form definition JSON(more to come)
*  *saveValueUrl*-The URL that the form will post single value updates to when in _Inline_ mode
*  *saveFormUrl*- The URL that the form will use to post complete form submission.  The submitted data will be in the same format 
as the data returned from _formGroupUrl_ but with updated values.
*  *saveEvent*- An Event that will be fired when the form is saved. Two arguments: Submission object with new values, original submission object
*  *changEvent*- An event that will be fired when a field value is saved.  The event will include four arguments: the ExtJS component that has changed,
the new value of the component, the old value of the component, and the numeric id of the component.

## JSON Structure
The Form Generator uses the same JSON structure as the  [FlexFields Service](../../flexfields/README.md).  

Below is a very simple JSON that will result in a very simple form with three fields and no groups. ( __TODO: more complete examples__ )

    [
        {
            "id": 100,
            "name": "Example Form",
            "description": "An Example Form",
            "displayName": "A very simple form",
            "tenantName": "*",
            "appName": "ESM",
            "componentName": "UserProfile",
            "entityName": "UserEntity",
            "fieldsCollection": [
                {
                    "id": 999,
                    "activeFlag": true,
                    "dataType": "STRING",
                    "defaultValue": "http://edifecs.com",
                    "description": "An example URL",
                    "displayName": "Example URL",
                    "name": "URL",
                    "regEx": "((mailto\\:|(news|(ht|f)tp(s?))\\://){1}\\S+)",
                    "restricted": false,
                    "required": true,
                    "validationMessage": "Please enter a valid URL",
                    "fieldValue": {
                        "id": 111,
    
                        "value": "http://Edifecs.com"
                    }
                },
                {
                    "id": 501,
                    "activeFlag": true,
                    "dataType": "DATE",
                    "defaultValue": "",
                    "description": "A Date Field",
                    "displayName": "Example Date",
                    "name": "date",
                    "regEx": "",
                    "restricted": false,
                    "required": false,
                    "validationMessage": "",
                    "format":"l, M dS, Y",
                    "readOnly": true,
                    "fieldValue": {
                        "value": "20140710"
                    }
    
                },
                {
                    "id": 499,
                    "activeFlag": true,
                    "dataType": "SELECTONE",
                    "defaultValue": "",
                    "description": "State",
                    "displayName": "State",
                    "name": "State",
                    "restricted": false,
                    "required": true,
                    "validationMessage": "",
                    "selectOptions": {
                        "WA": "Washington",
                        "CA": "California"
                    }
                },
                {
                    "id": 500,
                    "activeFlag": true,
                    "dataType": "BOOLEAN",
                    "defaultValue": "false",
                    "description": "Insured",
                    "displayName": "Insured",
                    "name": "Insured",
                    "restricted": false,
                    "required": true,
                    "validationMessage": ""
                }
            ],
            "children": [
    
            ]
        }
    ]
    
Each fieldCollection entry represents a single form element.  The _children_ array can also contain arrays of
field components.  Field components in the children array will be presented together in a fieldset.

* *id* A STRING unique identifier for the field.  **Must Be Unique**
* *visible* If false, the field is not displayed
* *dataType* The datatype of the field
* *defaultValue* The value to be deplayed in the absence of the field value.
* *description* Description text to appear on hover.
* *displayName* Value of the field label.
* *regEx* Regular expression to check for validity
* *restricted* **TODO**
* *required* True if the field is required.
* *validationMessage* Message to display if regex validation fails.
* *format* Datatype specific format.
* *readOnly* If true, value is not editable
* *fieldValue* The value of the field.

## Context

Some applications may require that certain context values persist along with the form data.  If passed an ExtJS EventBus object,
the form generator will listen for an update context event:

The event name is "updatecontext" and the event handler listens for two parameters.  The first is an object consisting of name/value pairs that 
will be added to the context which will be made available upon form submit. The second parameter is a flag; if true, the existing context will be overridden with 
the new values.  If false, the existing context will be updated with the new values.  

Examples usage:

    comp.eventBus.fireEvent("updatecontext", {val1:"uno", val2:2}, false);
