Ext.define('DD.view.parameters.ParametersConfigurationForm', {
    extend:'Ext.form.Panel',
    alias:'widget.parametersconfigurationform',
    bodyPadding: 5,
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },
    defaultType: 'textfield',
    initComponent : function() {
        var me = this;
        var paramList = [];

        ParameterManager.generateParametersList(function() {
            Ext.each(ParameterManager.parametersList.getKeys(), function(key) {
                var keyObj = ParameterManager.parametersList.get(key);

                paramList.push({
                    name:key,
                    value:keyObj.param,
                    widgetConfigObj:keyObj.widgetConfigObj
                });
            });
        });


        var itemsList = [
            {
                fieldLabel:'Name',
                name:'name',
                allowBlank:true
            },
            {
                fieldLabel:'Parameter Field',
                itemId:'parameterField',
                allowBlank:false,
                xtype:'combo',
                store:Ext.create('Ext.data.Store', {
                    fields:['name','value','widgetConfigObj'],
                    data:paramList
                }),
                displayField:'name',
                valueField:'value',
                editable:false,
                name:'parameter',
                forceSelection:true,
                queryMode:'local',
                multiSelect:true,
                listConfig : {
                    getInnerTpl : function() {
                        return '<div class="x-combo-list-item"><img src="'
                            + Ext.BLANK_IMAGE_URL
                            + '"'
                            + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                    }
                }
            }
        ];
        Ext.each(me.parameterConfig, function(config) {
            itemsList.push(config);
        });
        this.items = itemsList;
        this.callParent(arguments);
    }
});