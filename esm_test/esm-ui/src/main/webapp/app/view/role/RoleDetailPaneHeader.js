Ext.define('Security.view.role.RoleDetailPaneHeader', {
    extend:'Ext.container.Container',
    alias:'widget.roledetailpaneheader',
    padding:20,
    initComponent : function() {
        var me = this;


        this.items = [

            {
                xtype:'container',
				itemId:'detailCont',
                flex:1,
                layout:{
                    type:'hbox',
                    align:'stretch'
                },
                items:[
                    {
                        xtype:'label',
                        itemId:'roleName',
                        text:'No Role Selected',
                        cls:'detailPaneHeading'
                    },
					{
						   xtype:'container',
						   margin:'4 0 0 10',
						   id:'editUserProfile',
						   itemId:'editButtonCtr'
				    },
                    {
                        xtype:'tbspacer',
                        flex:1
                    }
                ]
            }, {
                xtype:'form',
				border:0,
				itemId:'detailForm',
				hidden:true,
                flex:1,
				layout:'anchor',
				defaults:{labelAlign:'top',labelSeparator:'',anchor:'100%'},
                items:[
						{
							xtype:'textfield',
							allowBlank:false,
							fieldLabel:'NAME',
							name:'canonicalName',
							id:'updateTenantRole-canonicalName',
							emptyText:'Enter Name'
						},
						{
							xtype:'textarea',
							fieldLabel:'DESCRIPTION',
							name:'description',
							id:'updateTenantRole-description',
							emptyText:'Add Description'
						}
                ],
				buttons : [
					{
						text:'Save',
						ui:'bluebutton',
						formBind:true,
						handler : function(btn){
						var form = btn.up('form');
						var formValues = form.getForm().getValues();
						var record = btn.up('form').getRecord();
						RoleManager.updateRole(formValues,record.get('id'),function(roleObj){
							RoleManager.updateRoleRecord(record.get('id'),function(){
								me.down('#detailCont').show();
								me.down('#separatorLine').show();
								me.down('#itemDescriptionContainer').show();
								form.hide();
								
							});
						},this);
							
						}
					}
				]
            },
            {
                xtype:'component',
				itemId:'separatorLine',
                cls
                    :
                    'horizontalLine',
                margin
                    :
                    '10 0 10 0',
                html
                    :
                    '<div></div>'
            },
            {
                xtype:'container',
                layout
                    :
                    'anchor',
                margin
                    :
                    '10 0 10 0',
                itemId
                    :
                    'itemDescriptionContainer',
                items
                    :
                    [
                        {
                            xtype:'displayfield',
                            anchor:'100%',
                            value: '<i>This Role has no description.</i>'
                        }
                    ]

            }
        ]
            ;
        this.callParent(arguments);
    },

    loadRoleDetail : function(record, callback) {
		var me=this;
        if (record) {
		
			me.down('#detailCont').show();
			me.down('#separatorLine').show();
			me.down('#itemDescriptionContainer').show();
			me.down('#detailForm').hide();

            var roleNameLabel = this.down('#roleName');
            var roleDescriptionField = this.down('#itemDescriptionContainer').down('displayfield');

            roleNameLabel.setText(record.get('canonicalName'));
            this.role = record;
            me.down('#detailForm').loadRecord(record);
            if (record.get('description') && record.get('description').length > 0) {
                roleDescriptionField.setValue(record.get('description'));
            }
            else
                roleDescriptionField.reset();
			
			var editButtonCtr = me.down('#editButtonCtr');	

			if(record.get('readOnly')){
				editButtonCtr.removeAll();
			}
			else
			{
					editButtonCtr.removeAll();
					editButtonCtr.add({
					xtype: 'component',
					html:'<div style="width:50px"><img src="resources/images/edit.png" title="Edit Role" style="cursor:pointer" class="editRole" id="manageTenant-editRole" /></div>',
					listeners :{
						'afterrender' : function(){
							 this.getEl().on('click', function(e, t, opts) {
								 e.stopEvent();
								 me.down('#detailCont').hide();
								 me.down('#separatorLine').hide();
								 me.down('#itemDescriptionContainer').hide();
								 me.down('#detailForm').show();
							 }, null, {delegate: '.editRole'});
						}
					}
				});	        
			}

        }
        Ext.callback(callback, this);
    },
    
    reset : function(){
	    this.down('container').show();
		this.down('#separatorLine').show();
		this.down('#itemDescriptionContainer').show();			
    	this.down('#roleName').setText('No Role Selected');
        this.down('#itemDescriptionContainer').down('displayfield').reset();    
		this.down('#detailForm').hide();		
    }
});