Ext.define('Security.view.realm.RealmConfigProperties', {
    extend: 'Ext.window.Window',
    alias : 'widget.realmconfigproperties',
    bodyPadding:5,
    width:400,
    height:250,
    resizable:false,
    modal:true,
    closeAction:'destroy',
    title:'Realm Properties',
    initComponent : function(){
    	
    var me=this;	
   	
    this.items=[
        {
            xtype:'form',
            border:false,
            layout:'anchor',
            defaults:{anchor:'100%'},
            defaultType:'textfield',
            items:[
                   {
                	   xtype:'combobox',
                	   name:'property',
                	   id:'realmConfigProperties-propertyName',
                	   valueField:'name',
                	   queryMode:'local',
                	   displayField:'name',
                	   store:'OptionalRealmProperties',
                	   fieldLabel:'Property Name',
                	   allowBlank:false,
                	   listeners : {
                		   select : function(combo,records){

	               			var propertyValue=me.down('#propertyValue');
	               			var propertyName=me.down('#propertyName');
	               			var description=me.down('#description');
             				
	               			
	               			propertyName.setValue(records[0].get('name'));
        				    propertyValue.setValue(records[0].get('value'));
        				    description.setValue(records[0].get('description'));
        			   	   
                				   
                		   },
                		   change : function(combo,newValue){

   	               			var propertyName=me.down('#propertyName');
   	               			propertyName.setValue(combo.getRawValue());
           				    
           			   	   
                   				   
                   		   }
                	   }
                	   
                   },
                   {
                 	  
                	   hidden:true,
                	   name:'name',
                	   itemId:'propertyName'
                   },
                   {
                	  
                	   fieldLabel:'Property Value',
                	   allowBlank:false,
                	   name:'value',
                	   id:'realmConfigProperties-propertyValue',
                	   itemId:'propertyValue'
                   },
                   {
                 	   xtype:'textarea',
                	   fieldLabel:'Description',
                	   name:'description',
                	   id:'realmConfigProperties-description',
                	   itemId:'description'
                   },
                   {
                	   hidden:true,
                	   name:'required',
                	   value:false
                   }
               ]
        }    
    ];
    this.buttons=[
        {
            text:'Cancel',
            id:'realmConfigProperties-cancel',
            width:80,
            handler:function() {
                this.up('window').close();
            }
        },
        {
            text:'Add',
            id:'realmConfigProperties-add',
            width:80,
            action: 'addProperty'
        }
    ];
    
    this.callParent(arguments);
    }

});

