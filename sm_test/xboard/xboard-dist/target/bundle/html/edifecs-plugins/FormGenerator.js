/*Code taken from flexfield UI and modified. Currently requires
two JSONs, one for config and one for data.  We may want to simplify
that model to include only one JSON.
 */

Ext.define('Edifecs.FormGenerator', {
    extend: 'Ext.container.Container',
    alias: 'widget.formfieldcomponent',
    minHeight:20,
    /*loadUrl:"",
    saveUrl:"",*/
    config : {
        entityId : null,
        entityName : null,
        saveValueUrl : null,
        saveFormUrl: null,
        enableRootHeader : null,
        formType: null, //Submission, Inline, or ReadOnly
        submissionType:null, //Event, URL
        saveButton: null,
        saveEvent:null,
        changeEvent:null,
        eventBus: null

    },

    //cache for the original form in case we need to reset
    origFormObj:{},
    currFormObj:{},
    fieldsMap:{},
    fileConfig:{},
    allowedSaveTypes:['readonly', 'inline', 'submission'],
    allowedSubmissionTypes:['event','url'],
    context:{},



    initComponent : function(){


        var me=this;


        me.fileConfig=Ext.clone(me.config);
        me.defineFormModels();

        this.items = [] ;

        this.callParent(arguments);
    },
    afterRender: function () {
        var me = this;
        if(me.ownerCt)
            me.ownerCt.setLoading('Loading Fields...');

        var fieldConfigStore = me.getFieldConfiguration(me.configUrl);
        var fieldGroupArray = [];



        fieldConfigStore.load({

            callback : function(records, operation, success){
                if(records.length>0){
                    var config = records[0];
                    //set the entity ID...prefer one set locally if available. Not sure
                    //this is necessary.
                    var eid;
                    var myEid=me.getEntityId();
                    if(myEid){
                        eid=myEid.toString();
                    }
                    else {
                        eid=config.get('entityId');
                    }
                    var formGroupParams = {
                        'tenantName':config.get('tenantName'),
                        'appName':config.get('appName'),
                        'componentName':config.get('componentName'),
                        'entityName':config.get('entityName'),
                        'entityId': me.getEntityId().toString()
                    };
                    var saveType=config.get("formType");
                    if(me.allowedSaveTypes.indexOf(saveType.toLowerCase())===-1){
                        var err="Form type of "+saveType+" not allowed."+
                                " Must be \"ReadOnly\", \"Inline\", or \"Submission\"";
                        throw err;
                    }

                    var submissionType=config.get("submissionType");

                    if(me.allowedSubmissionTypes.indexOf(submissionType.toLowerCase())===-1){
                        var err="submissionType of "+submissionType+" not allowed."+
                            " Must be \"Event\" or \"URL\"";
                        throw err;
                    }


                    if(!saveType && !submissionType){
                        var err="Either saveType or submissionType must be specified.";
                        throw err;
                    }

                    var submitType=config.get("")

                    me.setFormType(config.get("formType"));
                    me.setSubmissionType(submissionType);
                    me.setEntityName(config.get('entityName'));
                    me.setEnableRootHeader(config.get('enableRootHeader'));

                    me.setSaveValueUrl(config.get('saveValueUrl'));
                    me.setChangeEvent(config.get('changeEvent'));
                    me.setSaveEvent(config.get('saveEvent'));

                    me.setSaveFormUrl(config.get('saveFormUrl'));
                    if(me.useStaticData && me.useStaticData==true){
                        if(me.staticFlexGroupArray)
                            me.createFlexGroup(me.staticFlexGroupArray,function(fieldGroupArray){
                                Ext.each(fieldGroupArray,function(fieldGroup){
                                    me.addChildToParent(me, fieldGroup);
                                });
                                if(me.ownerCt) {
                                    me.ownerCt.setLoading(false);
                                }
                                if(me.getFormType().toLowerCase()==='submission') {
                                    me.insert({
                                        xtype: 'container',
                                        margin: '10 0 0 0',
                                        layout: 'hbox',
                                        width: 600,
                                        items: [
                                            {
                                                xtype: 'tbspacer',
                                                flex: 1
                                            },
                                            {
                                                xtype: 'button',
                                                formBind: true,
                                                text: 'Save',
                                                ui: 'bluebutton',
                                                action: 'saveTenantLandingPage',
                                                id: 'tenantLogo-saveTenantLandingPage',
                                                handler: function() {
                                                    var items=me.items;
                                                    me.handleFormClick();
                                                }
                                            }
                                        ]
                                    }, me.items.length);
                                }
                            });
                    }
                    else
                    {
                        me.getFormGroupConfiguration(config.get('formGroupUrl'), formGroupParams, function(formGroupConfig){
                            if(formGroupConfig){


                                me.origFormObj=Ext.clone(formGroupConfig);
                                me.currFormObj=formGroupConfig;

                                //parse the group object for easier access when saving
                                Ext.each(me.currFormObj, function(obj){
                                    me.parseFormObject(obj);
                                }, me);


                                me.createFormGroup(formGroupConfig,function(fieldGroupArray){
                                    Ext.each(fieldGroupArray,function(fieldGroup){
                                        console.log(fieldGroup);
                                        me.addChildToParent(me, fieldGroup);
                                    });
                                    if(me.ownerCt) {
                                        me.ownerCt.setLoading(false);
                                    }
                                    if(me.getFormType().toLowerCase()==='submission') {

                                        me.insert({
                                            xtype: 'container',
                                            margin: '10 0 0 0',
                                            layout: 'hbox',
                                            width: 600,
                                            items: [
                                                {
                                                    xtype: 'tbspacer',
                                                    flex: 1
                                                },
                                                {
                                                    xtype: 'button',
                                                    formBind: true,
                                                    text: 'Update LandingPage',
                                                    ui: 'bluebutton',
                                                    action: 'saveTenantLandingPage',
                                                    id: 'tenantLogo-saveTenantLandingPage',
                                                    handler: function() {
                                                        me.handleFormClick();
                                                    }
                                                }
                                            ]
                                        }, me.items.length);
                                    }
                                });
                            }
                        });
                    }



                }
            }

        });


        Edifecs.FormGenerator.superclass.afterRender.apply(this, arguments);
        return;
    },

    /**Slice up the current form object for ease of access when saving form*/

    parseFormObject: function(obj){
        var me=this;

        var fields=obj.fieldsCollection;
        //Ext.each is a pain with scope issues...
        for(var i=0; i < fields.length; i++){

            if(fields[i]&& fields[i].id){
                me.fieldsMap[fields[i].id]=fields[i];
            }

        }

        //now deal with the children....story of my life...
        if(obj.children && obj.children.length > 0){
            for(var j=0; j< obj.children.length; j++ ){
                me.parseFormObject(obj.children[j]);
            }
        }



    },

    /**Handles the click on the save form button.
     *
     * Matches form fields to their respective JSON fields,
     * updates values, and sends JSON back to server
     * with updated values.
     */

    handleFormClick: function(){
        var me=this;
        var items=me.items.items;
        var isEventSubmit=me.getSubmissionType().toLowerCase()==='event';

        for(key in me.fieldsMap){
            //var keyInt=typeof(key)!=='number' ? parseInt(key) : key;

            //now have the object, need to somehow map that back to the fielditem
            var comps=Ext.ComponentQuery.query('component[cls="formFieldItem"]');
            Ext.each(comps, function(comp){
                //the double equals (==) is intentional.  Since keys can be numbers or strings,
                //we're using explicit soft matching on purpose.
                if(comp.formFieldID == key){
                    console.log("matched key "+ key);
                    var fieldRec=this.fieldsMap[key];

                    /**construct the field value.  Not sure why all these are needed but
                     * it conforms to flexfields model.
                     */

                    switch (comp.xtype) {
                        case "datefield":
                            comp.value = Ext.Date.format(comp.getValue(), 'Ymd');
                            break;
                        default:
                            break;
                    }
                    var formFieldValue={
                        formFieldDefinitionId : comp.formFieldDefinitionId,
                        formGroupId : comp.formGroupId,
                        entityName : comp.entityName,
                        entityID : comp.entityId,
                        value : comp.value
                    };
                    fieldRec.fieldValue=formFieldValue;
                    return false;
                }

            },me);
        }

        if(isEventSubmit)
        {
            var eventName=me.getSaveEvent();
            if(!eventName){
                var err="Event submission specified but no submission event specified.";
                throw err;
            }

            //append the context name value pairs to argument list

            var eventArgs=[eventName, me.currFormObj, me.origFormObj];
            for (prop in me.context){
                if(me.context.hasOwnProperty(prop)){

                    var val={};
                    val[prop]=me.context[prop];
                    eventArgs.push(val);
                }
            }

            me.fireEvent.apply(me, eventArgs);

        }
        else {
            Ext.Ajax.request({
                url: me.getSaveFormUrl(),
                params: {'data': Ext.encode(me.currFormObj)},
                method: 'POST',
                success: function (response, opts) {
                    if (response) {
                        var respObj = Ext.decode(response.responseText);
                        if (respObj.success && respObj.success == false)
                            Ext.MessageBox.show({
                                title: 'ERROR',
                                msg: 'Failed to set save form',
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.ERROR,
                                closeAction: 'destroy'
                            });
                        else
                            Ext.MessageBox.show({
                                title: 'Success',
                                msg: 'Your values have been saved.',
                                buttons: Ext.MessageBox.OK,
                                icon: Ext.MessageBox.ERROR,
                                closeAction: 'destroy'
                            });

                        if(me.eventBus) {
                            me.eventBus.fireEvent('onFormSubmitSuccess', respObj);
                        }

                    }
                    else
                        Ext.callback(callback, this, null);
                },
                failure: function (response, opts) {
                    Ext.MessageBox.show({
                        title: 'ERROR',
                        msg: 'Failed to set save form',
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR,
                        closeAction: 'destroy'
                    });
                }
            });
        }

    },

    /** Recursive function that adds child nodes to parent nodes.
     * First invocation, parent is the container (I think)
     * @param parent
     * @param child
     */
    addChildToParent : function(parent, child){

        var me = this;
        var tempChildHolder = [];

        if(child.items){
            tempChildHolder = child.items;
            child.items=[];
        }
        var recentlyAddedChild = parent.add(Ext.widget(child));
        if(recentlyAddedChild){
            Ext.each(tempChildHolder,function(child){
                me.addChildToParent(recentlyAddedChild, child);
            });
        }

    },

    defineFormModels : function(){

        Ext.define('formGroupModel',{
            extend:'Ext.data.Model',
            fields: [
                {name: 'id', type: 'integer'},
                {name: 'name', type: 'string'},
                {name: 'description', type: 'string'},
                {name: 'displayName', type: 'string'},
                {name: 'tenantName', type: 'string'},
                {name: 'appName', type: 'string'},
                {name: 'componentName', type: 'string'},
                {name: 'permissionRequired', type: 'string'},
                {name: 'restricted', type: 'boolean', defaultValue: false},
                {name: 'entityName', type: 'string'},
                {name: 'namespace', type: 'string'},
                {name: 'fieldsCollection', type: 'auto'},
                {name: 'children', type: 'auto'}
            ]
        });


        Ext.define('FormFieldDefinitionModel',{
            extend:'Ext.data.Model',
            fields: [
                {name: 'id', type: 'string'},
                {name: 'activeFlag', type: 'boolean', defaultValue: true},
                {name: 'dataType', type: 'string'},
                {name: 'type', type: 'string'},
                {name: 'defaultValue', type: 'string'},
                {name: 'description', type: 'string'},
                {name: 'displayName', type: 'string'},
                {name: 'fieldSize', type: 'integer'},
                {name: 'name', type: 'string'},
                {name: 'precisionValue', type: 'string'},
                {name: 'regEx', type: 'string'},
                {name: 'restricted', type: 'boolean', defaultValue: false},
                {name: 'required', type: 'boolean', defaultValue: false},
                {name: 'namespace', type: 'string'},
                {name: 'requiredPermission', type: 'string'},
                {name: 'validationMessage', type: 'string'},
                {name:"format", type:'string'},
                {name: 'fieldValue', type: 'auto'},
                {name: 'selectOptions', type: 'auto'},
                {name: 'readOnly', type: 'boolean', defaultValue: false},
                {name: 'visible', type: 'boolean', defaultValue: true}
            ]
        });

    },

    createFormDisplay: function(fieldConfig){
        var isSubmissionForm=this.getFormType().toLowerCase()==="submission";
        var isReadOnly=this.getFormType().toLowerCase()==="readonly";

        var displayFieldConfig={
            "xtype": "displayfield",
            "fieldLabel": fieldConfig.fieldLabel,
            "name": fieldConfig.name+"_display",
            "id": fieldConfig.id+"_display",
            "cls": "formDisplay",
            "fieldStyle":  "display:inline-block; width:auto; padding-right:18px;",
            "editableId":fieldConfig.id,
            "hidden": isSubmissionForm ? true : false,
            "listeners": {


                render: {

                    fn: function(th, eOpts){

                        var theEl=th.getEl();

                        if(fieldConfig.description.length>0)
                            Ext.QuickTips.register({
                                target: theEl,
                                text: fieldConfig.description,
                                enabled: true,
                                showDelay: 20,
                                trackMouse: true,
                                autoShow: true
                            });

                        //no OOTB click handler for displayfield
                        if(!isSubmissionForm && !isReadOnly) {
                            theEl.addListener('click', function (evt, el, dom) {

                                var elem = Ext.get(el.id);
                                var displayComp = Edifecs.WidgetUtils.findComponentByElement(elem);
                                var editableElem = Ext.get(displayComp.editableId);
                                var editableComp = Ext.getCmp(displayComp.editableId);
                                displayComp.hide();
                                editableComp.show();
                                editableComp.focus();
                                //editableComp.origValue=editableComp.value;
                            });
                        }




                    }

                }
            }
        };


        switch(fieldConfig.xtype){
            case "datefield":
                displayFieldConfig.value=Ext.Date.format(fieldConfig.value, 'M d, Y'); break;
            case "checkbox":
                displayFieldConfig.value=fieldConfig.value ? "Yes" : "No"; break;
            default: displayFieldConfig.value=fieldConfig.value;
        }






        return displayFieldConfig;
    },


    createFormGroup : function(formGroupConfig, callback){

        var me=this;
        var fieldGroupArray = [];

        Ext.each(formGroupConfig,function(groupObj){
            var fieldArray = [];
            Ext.each(groupObj['fieldsCollection'],function(formFieldObj){
                var fieldRec = Ext.create('FormFieldDefinitionModel',formFieldObj);

                if(fieldRec){
                    var fieldConfig = me.generateFieldObject(fieldRec, groupObj.id);
                    var fieldDisplayConfig=me.createFormDisplay(fieldConfig);
                    fieldConfig.displayId=fieldDisplayConfig.id;
                    fieldArray.push(fieldConfig);
                    fieldArray.push(fieldDisplayConfig);

                }
            });

            Ext.each(groupObj['children'],function(childGroup){
                me.createFormGroup([childGroup],function(fieldGroupArray){
                    Ext.each(fieldGroupArray,function(fieldGroup){
                        fieldGroup['border'] = true;
                        fieldGroup['title']=groupObj['displayName'];
                        fieldArray.push(fieldGroup);

                    });
                });
            });
            fieldGroupArray.push({
                xtype:'fieldset',
                border : me.getEnableRootHeader() ? true : false,
                flex:1,
                cls: "formFieldSet",
                id:'group-'+groupObj['id'],
                title: me.getEnableRootHeader() ? groupObj['displayName'] : null,
                layout: 'anchor',
                defaults : {anchor:'100%'},
                items : fieldArray
            });
        });

        Ext.callback(callback,this,[fieldGroupArray]);
    },

    generateFieldObject : function(fieldRec, formGroupID){
        var me = this;
        var isSubmission=me.getFormType().toLowerCase()==='submission';
        var isInline=me.getFormType().toLowerCase()==='inline';
        var isReadOnly=me.getFormType().toLowerCase()==='readonly';
        var isEventSubmit=me.getSubmissionType().toLowerCase()==='event';
        var itemId='FFItem'+fieldRec.get('id');

        var singleClickListeners = {
            change : function(field, value, old){

                if(isInline) {
                    field.setLoading('Saving...');

                    if (field.xtype == 'datefield') {
                        value = Ext.Date.format(value, 'Ymd');
                    }


                    if(!isEventSubmit) {
                        me.sendUpdateToFormField(field.formFieldID, field.formGroupID, me.getEntityName(), value, field.entityID, function (response) {
                            field.setLoading(false);
                            if (response == null)
                                Ext.MessageBox.show({
                                    title: 'ERROR',
                                    msg: 'Failed to set value',
                                    buttons: Ext.MessageBox.OK,
                                    icon: Ext.MessageBox.ERROR,
                                    closeAction: 'destroy'
                                });

                        });
                    }
                    else {
                        var changeEvent=me.getChangeEvent();
                        if(changeEvent){
                            me.fireEvent(changeEvent,field, value, old, field.formFieldID);
                        }

                    }
                }

                //throw a change event if defined

            },


            blur:  function(comp, ev, opts){
                var theComp=comp;
                var displayText;
                if(me.getFormType().toLowerCase()!=='submission' &&
                    me.getFormType().toLowerCase()!=='readonly') {
                    switch (comp.xtype) {
                        case "datefield":
                            displayText = Ext.Date.format(comp.getValue(), 'M d, Y');
                            break;
                        case "checkbox":
                            displayText = comp.getValue() ? "Yes" : "No";
                            break;
                        default:
                            displayText = comp.getValue();
                    }
                    var theDisplayComp = Ext.getCmp(theComp.displayId);
                    theDisplayComp.setValue(displayText);
                    theComp.hide();
                    theDisplayComp.show();
                }


            }
        };


        //an object containing listeners to add to input boxes:
        var fieldListeners={
            blur:  function(comp, two, three){
                if(isInline) {
                    var theComp = comp;
                    theComp.reset();
                    var theDisplayComp = Ext.getCmp(theComp.displayId);
                    theDisplayComp.setValue(theComp.getValue());
                    theComp.hide();
                    theDisplayComp.show();
                }else {
                    //console.log("non inline blur");
                }

            },



            change : function(field, value, old){

                if(isInline) {
                    field.setLoading('Saving...');

                    if (field.xtype == 'datefield') {
                        value = Ext.Date.format(value, 'Ymd');
                    }


                    if(!isEventSubmit) {
                        me.sendUpdateToFormField(field.formFieldID, field.formGroupID, me.getEntityName(), value, field.entityID, function (response) {
                            field.setLoading(false);
                            if (response == null)
                                Ext.MessageBox.show({
                                    title: 'ERROR',
                                    msg: 'Failed to set value',
                                    buttons: Ext.MessageBox.OK,
                                    icon: Ext.MessageBox.ERROR,
                                    closeAction: 'destroy'
                                });

                        });
                    }
                    else {
                        var changeEvent=me.getChangeEvent();
                        if(changeEvent){
                            me.fireEvent(changeEvent,field, value, old, field.formFieldID);
                        }

                    }
                }



            },


            /*For some reason, I am unable to add these events as listeners, so I attach them
             this way.  There's surely a better EXTjs way to do this...
             */

            render: function(p){
                var theEl=p.getEl();

                if(p.description.length>0)
                    Ext.QuickTips.register({
                        target: p.getEl(),
                        text: p.description,
                        enabled: true,
                        showDelay: 20,
                        trackMouse: true,
                        autoShow: true
                    });


                theEl.on('keydown',function(ev, targ){
                    var isInline=me.getFormType().toLowerCase()==='inline';
                    if(!isInline){
                        return;
                    }

                    var elem=Ext.get(targ.id);
                    if(elem.hasCls("x-formFieldCls")){
                        var charCode = ev.getCharCode();
                        if(charCode===13 || charCode===9)
                        {

                            var comp=Edifecs.WidgetUtils.findComponentByElement(elem);
                            var value=comp.getValue();

                            if(p.isValid()){
                                comp.resetOriginalValue();
                                p.setLoading('Saving...');

                                me.sendUpdateToFormField(p.formFieldID,p.formGroupID,me.getEntityName(),value,p.entityID,function(response){
                                    p.setLoading(false);
                                    if(response==null)
                                        Ext.MessageBox.show({
                                            title : 'ERROR',
                                            msg : 'Failed to set value',
                                            buttons : Ext.MessageBox.OK,
                                            icon : Ext.MessageBox.ERROR,
                                            closeAction:'destroy'
                                        });

                                });

                            }
                            comp.fireEvent('blur', comp);
                        }

                    }
                });


            }
        };


        var tm = new Ext.util.TextMetrics(),
            n = tm.getWidth(fieldRec.get('displayName') + ":");
        if(n<100)
            n=100;

        var fieldLabel = fieldRec.get('displayName');

        //if it's read only, blank all the required and validation stuff
        if(isReadOnly){
            fieldRec.set('required', false);
            fieldRec.set('regex',"");
        }
        if(fieldRec.get('validationMessage').length==0)
            fieldRec.set('validationMessage','This field is required.');

        if(fieldRec.get('required'))
            fieldLabel = fieldRec.get('displayName')+' <span style="color:red"><b>*</b></span>'

        //it's hidden if it's not a submission form OR if it's not active
        //var isInactive=( typeof fieldRec.get('activeFlag') ==='boolean' && !fieldRec.get('activeFlag'));
       // var hiddenFlag
        var isVisible= me.getFormType().toLowerCase()==="submission" && fieldRec.get('visible');

        var isReadOnly=( typeof fieldRec.get('readOnly') ==='boolean' && fieldRec.get('readOnly'));
        var commonConfig = {
            "id":'field-'+fieldRec.get('id'),
            "focusCls":"formFieldCls",
            "cls": "formFieldItem",
            "flex":1,
            "itemId":itemId,
            "enabled":fieldRec.get('activeFlag'),
            "allowBlank":!fieldRec.get('required'),
            "defaultValue":fieldRec.get('defaultValue'),
            "fieldLabel":fieldLabel,
            "labelWidth":n,
            "msgTarget":"side",
            "regex":fieldRec.get('regEx').length > 0 ? new RegExp(fieldRec.get('regEx')) : null ,
            "regexText":fieldRec.get('validationMessage'),
            "blankText":fieldRec.get('validationMessage'),
            "name":fieldRec.get('name'),
            "value":fieldRec.get('fieldValue') ? fieldRec.get('fieldValue')['value'] : null,
            "entityID":fieldRec.get('fieldValue') ? fieldRec.get('fieldValue')['entityID'] : null,
            "origFieldRecFieldValue":fieldRec.get('fieldValue'),
            "description":fieldRec.get('description'),
            "formFieldID":fieldRec.get('id'),
            "formGroupID":formGroupID,
            "validateOnChange": true,
            "validateOnBlur": true,
            "format":fieldRec.get('format'),
            "hidden": !isVisible,
            "readOnly": isReadOnly


        } ;




        //me.fieldsMap[fieldRec.get('id')]=fieldRec; NO!

        commonConfig.listeners=fieldListeners;

        switch(fieldRec.get('dataType')){
            case 'STRING' : commonConfig['xtype']='textfield';break;
            case 'TEXT'   : commonConfig['xtype']='textarea'; break;
            case 'DOUBLE' : commonConfig['xtype']='numberfield'; break;
            case 'LONG' : commonConfig['xtype']='numberfield'; commonConfig['decimalPrecision']= 0; break;
            case 'DATE' : commonConfig['xtype']='datefield';
                commonConfig['listeners']=singleClickListeners;
                if(!commonConfig.format) {
                    commonConfig.format='m/d/Y';
                }
                commonConfig['value']=commonConfig.value ? Ext.Date.parse(commonConfig.value, "Ymd") : null;
                break;
            case 'BOOLEAN' : commonConfig['xtype']='checkbox';
                commonConfig['defaultValue']=fieldRec.get('defaultValue')=="true" ? true : false;
                commonConfig['listeners']=singleClickListeners;
                break;
            case 'SELECTONE' : commonConfig['xtype']='combobox';
                var selectOptions = fieldRec.get('selectOptions');
                var storeRecords=[];
                if(selectOptions)
                {
                    for(var i in selectOptions){
                        var temp=[];
                        temp.push(i);
                        temp.push(selectOptions[i]);
                        storeRecords.push(temp);
                    }
                }
                commonConfig['selectOnTab']=true;
                commonConfig['typeAhead']=true;

                commonConfig['store'] = storeRecords;
                break;
            default : commonConfig['xtype']='textfield';
        }
//    	console.log(commonConfig);
        return commonConfig;

    },

    //send updates to formfield service

    sendUpdateToFormField: function(formFieldDefinitionId, formGroupId, entityName, value, entityID, callback){

        var me=this;
        var eid = entityID;

        if(eid==null || eid==undefined)
            eid = me.getEntityId();
        if(eid || eid==0){


            var formFieldValue={
                formFieldDefinitionId : formFieldDefinitionId,
                formGroupId : formGroupId,
                entityName : entityName,
                entityID : eid,
                value : value
            };
            Ext.Ajax.request({
                url: me.saveValueUrl,
                params : {'data':Ext.encode({'formFieldValue':formFieldValue})},
                method : 'POST',
                success: function(response, opts) {
                    if(response){
                        var respObj = Ext.decode(response.responseText);
                        if(respObj.success && respObj.success==false)
                            Ext.callback(callback,this,null);
                        else
                            Ext.callback(callback,this,[Ext.decode(response.responseText)]);
                    }
                    else
                        Ext.callback(callback,this,null);
                },
                failure: function(response, opts) {
                    console.log('server-side failure with status code ' + response.status);
                }
            });

        }
        else
            Ext.MessageBox.show({
                title : 'ERROR',
                msg : 'Failed to retrieve Entity ID',
                buttons : Ext.MessageBox.OK,
                icon : Ext.MessageBox.ERROR,
                closeAction:'destroy'
            });
    },

    /** get field configuration */
    getFieldConfiguration : function(configUrl){
        var store = new Ext.create('Ext.data.Store',
            {
                fields: [
                    {name: 'formGroupUrl', type: 'string'},
                    {name: 'saveValueUrl', type: 'string'},
                    {name: 'enableRootHeader', type: 'boolean', defaultValue:false},
                    {name: 'tenantName', type: 'string'},
                    {name: 'appName', type: 'string'},
                    {name: 'componentName', type: 'string'},
                    {name: 'entityName', type: 'string'},
                    {name: 'saveFormUrl', type: 'string'},
                    {name: 'formType', type: 'string'},
                    {name: 'submissionType', type: 'string'},
                    {name: 'changeEvent', type: 'string'},
                    {name: 'saveEvent', type: 'string'}
                ],
                autoLoad: false,
                storeId: 'fieldConfigStore',
                proxy: {
                    type: 'ajax',
                    url: configUrl,
                    reader: {
                        type: 'json'
                    }
                },
                afterRequest: function(req, res) {
                    console.log("Ahoy!", req.operation.response);
                }
            });
        return store;
    },

    //get configuration for flex group
    getFormGroupConfiguration : function(formGroupUrl, formGroupParams, callback){

        Ext.Ajax.request({
            url: formGroupUrl,
            params : {'data':Ext.encode({'contextMap':formGroupParams})},
            method : 'GET',
            cors: true,
            success: function(response, opts) {

                if(response)
                    Ext.callback(callback,this,[Ext.decode(response.responseText)]);
                else
                    Ext.callback(callback,this,null);
            },
            failure: function(response, opts) {

                console.log('server-side failure with status code ' + response.status);
            }
        });

    },

    // load configuration for flex group
    loadFormGroupStore: function (formGroupData) {

        var groupDefinitionStore = Ext.create('Ext.data.Store',{
            model:'FormGroupModel',
            data : formGroupData
        });

        return groupDefinitionStore.getRange();

    },

    /**Override the event bus setter**/
     setEventBus: function(eventBus){

        this.eventBus=eventBus;
        this.eventBus.addListener('updatecontext',
            function(context, override){
                console.log("got fx");

                if(override===true){
                    console.log("Overriding context.");
                    this.context=context;
                }
                else{
                    console.log("updating context");
                    for(var prop in context){
                        if(context.hasOwnProperty(prop)){
                            this.context[prop]=context[prop];
                        }
                    }
                }
                console.dir(this.context);
            }, this );


    },


    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Security.view.common.FormFieldComponent.superclass.onDestroy.apply(this, arguments);
    }
});