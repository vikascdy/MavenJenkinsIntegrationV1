Ext.define('Edifecs.Widget.FormGenerator', {
    extend: 'Ext.container.Container',
    alias: 'widget.formfieldcomponent',
    minHeight:20,
    loadUrl:"",
    saveUrl:"",
    config : {

    },
    initComponent : function(){

        var me=this;

        me.defineFormModels();

        this.items = [] ;

        this.callParent(arguments);
    },
    afterRender: function () {

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
                {name: 'formFieldsCollection', type: 'auto'},
                {name: 'children', type: 'auto'}
            ]
        });


        Ext.define('FormFieldDefinitionModel',{
            extend:'Ext.data.Model',
            fields: [
                {name: 'id', type: 'integer'},
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
                {name: 'formFieldValue', type: 'auto'},
                {name: 'selectOptions', type: 'auto'}
            ]
        });

    },

    createFormDisplay: function(fieldConfig){

        var displayFieldConfig={
            "xtype": "displayfield",
            "fieldLabel": fieldConfig.fieldLabel,
            "name": fieldConfig.name+"_display",
            "id": fieldConfig.id+"_display",
            "cls": "formDisplay",
            "fieldStyle":  "display:inline-block; width:auto; padding-right:18px;",
            "editableId":fieldConfig.id,
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


                        theEl.on('click', function(ev, targ){
                            var elem=Ext.get(targ.id);
                            var displayComp=Functions.findComponentByElement(elem);
                            var editableElem= Ext.get(displayComp.editableId);
                            var editableComp = Ext.getCmp(displayComp.editableId);
                            displayComp.hide();
                            editableComp.show();
                            editableComp.focus();
                            //editableComp.origValue=editableComp.value;
                        })



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
            Ext.each(groupObj['formFieldsCollection'],function(formFieldObj){
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
        var itemId='FFItem'+fieldRec.get('id');

        var singleClickListeners = {
            change : function(field, value){
                field.setLoading('Saving...');

                if(field.xtype=='datefield') {
                    value = Ext.Date.format(value, 'Ymd');
                }

                me.sendUpdateToFormField(field.formFieldID,field.formGroupID,me.getEntityName(),value,field.entityID,function(response){
                    field.setLoading(false);
                    if(response==null)
                        Ext.MessageBox.show({
                            title : 'ERROR',
                            msg : 'Failed to set value',
                            buttons : Ext.MessageBox.OK,
                            icon : Ext.MessageBox.ERROR,
                            closeAction:'destroy'
                        });

                });
            },

            blur:  function(comp, ev, opts){
                var theComp=comp;
                var displayText;
                switch(comp.xtype){
                    case "datefield":
                        displayText=Ext.Date.format(comp.getValue(), 'M d, Y'); break;
                    case "checkbox":
                        displayText=comp.getValue() ? "Yes" : "No"; break;
                    default: displayText=comp.getValue();
                }
                var theDisplayComp=Ext.getCmp(theComp.displayId);
                theDisplayComp.setValue(displayText);
                theComp.hide();
                theDisplayComp.show();

            }
        };


        //an object containing listeners to add to input boxes:
        var fieldListeners={
            blur:  function(comp, two, three){

                var theComp=comp;
                theComp.reset();
                var theDisplayComp=Ext.getCmp(theComp.displayId);
                theDisplayComp.setValue(theComp.getValue());
                theComp.hide();
                theDisplayComp.show();

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
                    if($(targ).hasClass("x-formFieldCls")||true){
                        var charCode = ev.getCharCode();
                        if(charCode===13 || charCode===9)
                        {
                            var elem=Ext.get(targ.id);
                            var comp=Functions.findComponentByElement(elem);
                            if(p.isValid()){
                                comp.resetOriginalValue();
                                p.setLoading('Saving...');
                                me.sendUpdateToFormField(p.formFieldID,p.formGroupID,me.getEntityName(),$(targ).val(),p.entityID,function(response){
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

        if(fieldRec.get('validationMessage').length==0)
            fieldRec.set('validationMessage','This field is required.');

        if(fieldRec.get('required'))
            fieldLabel = fieldRec.get('displayName')+' <span style="color:red"><b>*</b></span>'

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
            "value":fieldRec.get('formFieldValue') ? fieldRec.get('formFieldValue')['value'] : null,
            "entityID":fieldRec.get('formFieldValue') ? fieldRec.get('formFieldValue')['entityID'] : null,
            "description":fieldRec.get('description'),
            "formFieldID":fieldRec.get('id'),
            "formGroupID":formGroupID,
            "validateOnChange": true,
            "validateOnBlur": true,
            "hidden": true

        } ;

        commonConfig.listeners=fieldListeners;

        switch(fieldRec.get('dataType')){
            case 'STRING' : commonConfig['xtype']='textfield';break;
            case 'TEXT'   : commonConfig['xtype']='textarea'; break;
            case 'DOUBLE' : commonConfig['xtype']='numberfield'; break;
            case 'LONG' : commonConfig['xtype']='numberfield'; commonConfig['decimalPrecision']= 0; break;
            case 'DATE' : commonConfig['xtype']='datefield';
                commonConfig['listeners']=singleClickListeners;
                commonConfig['format']='Ymd';
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

            console.log(formFieldDefinitionId, formGroupId, entityName, value, eid);

            var formFieldValue={
                formFieldDefinitionId : formFieldDefinitionId,
                formGroupId : formGroupId,
                entityName : entityName,
                entityID : eid,
                value : value
            };

            Ext.Ajax.request({
                url: me.getSaveValueUrl(),
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

    // get field configuration
    getFieldConfiguration : function(loadUrl){
        var store = new Ext.create('Ext.data.Store',
            {
                fields: [
                    {name: 'formGroupUrl', type: 'string'},
                    {name: 'saveValueUrl', type: 'string'},
                    {name: 'enableRootHeader', type: 'boolean', defaultValue:false},
                    {name: 'tenantName', type: 'string'},
                    {name: 'appName', type: 'string'},
                    {name: 'componentName', type: 'string'},
                    {name: 'entityName', type: 'string'}
                ],
                autoLoad: false,
                storeId: 'fieldConfigStore',
                proxy: {
                    type: 'ajax',
                    url: loadUrl,
                    reader: {
                        type: 'json'
                    }
                }
            });
        return store;
    },

    //get configuration for flex group
    getFormGroupConfiguration : function(formGroupUrl, formGroupParams, callback){

        Ext.Ajax.request({
            url: formGroupUrl,
            params : {'data':Ext.encode({'contextMap':formGroupParams})},
            method : 'POST',
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

    // private, clean up
    onDestroy: function () {
        this.removeAll();
        Security.view.common.FormFieldComponent.superclass.onDestroy.apply(this, arguments);
    }
});