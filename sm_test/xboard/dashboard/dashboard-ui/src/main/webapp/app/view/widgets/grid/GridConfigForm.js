Ext.define('DD.view.widgets.grid.GridConfigForm', {
    extend:'Ext.form.Panel',
    title: 'Grid Configuration',
    bodyPadding: 5,
    alias:'widget.gridconfigform',
    layout: 'anchor',
    border:false,
    defaults: {
        anchor: '100%'
    },

    defaultType: 'textfield',
    initComponent : function() {

        this.items = [

            {
                fieldLabel:'Title',
                name:'title'
            },
            {
                xtype:'checkbox',
                fieldLabel:'Auto Scroll',
                name:'autoScroll'
            },
            {
                xtype:'checkbox',
                fieldLabel:'Collapsible',
                name:'collapsible'
            },
            {
                xtype:'checkbox',
                fieldLabel:'Hide Headers',
                name:'hideHeaders'
            },
            {
                xtype:'checkbox',
                fieldLabel:'Border',
                name:'border'
            },
            {
                xtype:'fieldset',
                itemId:'columnConfig',
                disabled:true,
                collapsible:true,
                title:'Column Configuration',
                defaultType:'textfield',
                items:[
                    {
                        xtype:'combo',
                        anchor:'95%',
                        allowBlank:false,
                        name:'columns',
                        editable:false,
                        itemId:'columns',
                        fieldLabel:'Columns',
                        store:Ext.create('Ext.data.Store', {
                            fields:["name"]
                        }),
                        displayField:'name',
                        valueField:'name',
                        queryMode:'local',
                        multiSelect:true,
                        forceSelection : true,
                        listConfig : {
                            getInnerTpl : function() {
                                return '<div class="x-combo-list-item"><img src="'
                                    + Ext.BLANK_IMAGE_URL
                                    + '"'
                                    + 'class="chkCombo-default-icon chkCombo" /> {name} </div>';
                            }
                        }
                    },
                    {
                        xtype:'checkbox',
                        fieldLabel:'Group Data',
                        name:'groupData',
                        itemId:'groupData'
                    },
                    {
                        xtype:'combobox',
                        anchor:'95%',
                        hidden:true,
                        fieldLabel:'Grouping Field',
                        name:'groupingField',
                        itemId:'groupingField',
                        editable:false,
                        queryMode:'local',
                        store:Ext.create('Ext.data.Store', {
                            fields:["name"]
                        }),
                        displayField:'name',
                        valueField:'name',
                        forceSelection : true
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }
});