Ext.define('Security.view.group.GroupDetailPaneHeader', {
    extend:'Ext.container.Container',
    alias:'widget.groupdetailpaneheader',
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
                        itemId:'groupName',
                        text:'No Group Selected',
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
            },
			 {
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
						GroupManager.updateGroup(formValues,record.get('id'),function(roleObj){
							GroupManager.updateGroupRecord(record.get('id'),function(){
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
                            value: '<i>This Group has no description.</i>'
                        }
                    ]

            }
        ]
            ;
        this.callParent(arguments);
    },

    loadGroupDetail : function(record, callback) {
		var me=this;
        if (record) {
		
			me.down('#detailCont').show();
			me.down('#separatorLine').show();
			me.down('#itemDescriptionContainer').show();
			me.down('#detailForm').hide();

            var groupNameLabel = this.down('#groupName');
            var groupDescriptionField = this.down('#itemDescriptionContainer').down('displayfield');

            groupNameLabel.setText(record.get('canonicalName'));
            this.group = record;
            me.down('#detailForm').loadRecord(record);
            if (record.get('description') && record.get('description').length > 0) {
                groupDescriptionField.setValue(record.get('description'));
            }
            else
                groupDescriptionField.reset();
				
			var editButtonCtr = me.down('#editButtonCtr');	
			if(editButtonCtr)
			{
				editButtonCtr.removeAll();
				editButtonCtr.add({
					xtype: 'component',
					html:'<div style="width:50px"><img src="resources/images/edit.png" title="Edit Group" style="cursor:pointer" class="editGroup" id="manageTenant-editGroup" /></div>',
					listeners :{
						'afterrender' : function(){
							 this.getEl().on('click', function(e, t, opts) {
								 e.stopEvent();
								 me.down('#detailCont').hide();
								 me.down('#separatorLine').hide();
								 me.down('#itemDescriptionContainer').hide();
								 me.down('#detailForm').show();
							 }, null, {delegate: '.editGroup'});
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
    	this.down('#groupName').setText('No Group Selected');
        this.down('#itemDescriptionContainer').down('displayfield').reset();    
		this.down('#detailForm').hide();		
    }
});