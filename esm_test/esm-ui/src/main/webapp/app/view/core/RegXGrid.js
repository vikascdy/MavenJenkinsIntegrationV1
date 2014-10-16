Ext.define('Security.view.core.RegXGrid', {
    extend : 'Ext.grid.Panel',
    alias : 'widget.regxgrid',
    title:'Password Regular Expression',
    columns : [
        {
            xtype : 'actioncolumn',
            width : 25,
            items : [
                {
                    iconCls:'info-status',
                    text:'Description',
                    handler : function(grid, rowIndex, colIndex, item, e, record, row) {
                        Ext.create('Ext.tip.ToolTip', {
                            html: record.get('description'),
                            dismissDelay: 15000,
                            target:row.id
                        });
                    }
                }
            ]

        },
        {
            text : 'Regular Expression',
            menuDisabled:true,
            dataIndex : 'regex',
            flex : 1,
            editor : {
                xtype : 'textfield',
                allowBlank : false
            }
        }, {
            text : 'Test Password',
            menuDisabled:true,
            dataIndex : 'password',
            flex : 1,
            editor : {
                xtype : 'textfield',
                allowBlank : false
            }
        }, {
            xtype : 'actioncolumn',
            width : 25,
            items : [ {
                iconCls:'validate',
                text:'Validate',
                tooltip : 'Validate password against given regular expression',
                handler : function(grid, rowIndex, colIndex) {
                    var rec = grid.getStore().getAt(rowIndex);
                    if(rec.get('password').length<=0 || rec.get('regex').length<=0)
                        Functions.errorMsg("Expression OR Password Field Empty", 'Warning', null, 'WARN');
                    else
                    {
                        UserManager.matchPattern(rec.get('regex'),rec.get('password'),function(isValid){
                            console.log(isValid);
                            if(isValid){
                                Functions.errorMsg("Password matched sucessfully to given expression.", 'Success', null, 'INFO');
                                rec.set('isValid',true);
                            }
                            else{
                                Functions.errorMsg("Password matching failed.", 'Warning', null, 'WARN');
                                rec.set('isValid',false);
                            }
                        },this);

                    }
                }
            } ]
        },

    ],

    initComponent : function() {
        var me = this;

        Ext.define('RegXModel', {
            extend : 'Ext.data.Model',
            fields : [
                {name:'regex',type:'string'},
                {name:'description',type:'string'},
                {name:'password',type:'string'},
                {name:'isValid',type:'boolean',defaultValue:false}
            ]
        });

        var selModel = Ext.create('Ext.selection.CheckboxModel', {
            mode : 'single',
            showHeaderCheckbox : false,

        });
        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit : 1
        });

        this.store = Ext.create('Ext.data.Store',{
            model:'RegXModel',
            data: [
                {
                    regex:"^[a-zA-Z]\w{3,14}$",
                    description:"The password's first character must be a letter, it must contain at least 4 characters and no more than 15 characters and no characters other than letters, numbers and the underscore may be used"
                },
                {
                    regex:"^([a-zA-Z0-9@*#]{8,15})$",
                    description:"Password matching expression. Match all alphanumeric character and predefined wild characters. Password must consists of at least 8 characters and not more than 15 characters."
                },
                {
                    regex:"^(?![0-9]{6})[0-9a-zA-Z]{6}$",
                    description:"Matches a six character &quot;password&quot; that has to consist of numbers and letters with at least one letter in it."
                }

            ]
        });

        this.plugins = [ cellEditing ];
        this.selModel = selModel;

        this.tbar = [ {
            text : 'Add Expression',
            iconCls:'add',
            handler : function() {

                var rec = Ext.create('RegXModel', {
                    regex : '',
                    description : '',
                    password : '',
                    isValid:false

                });

                me.getStore().insert(0, rec);
                cellEditing.startEditByPosition({
                    row : 0,
                    column : 0
                });
            }
        }, {
            text : 'Delete Expression',
            iconCls:'delete',
            handler : function() {

                var sm = me.getSelectionModel();
                var sel = sm.getSelection();
                me.getStore().remove(sel[0]);

            }
        } ];

        this.callParent(arguments);
    }

});