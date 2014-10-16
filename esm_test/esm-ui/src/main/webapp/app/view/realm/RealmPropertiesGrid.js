Ext.define('Security.view.realm.RealmPropertiesGrid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.realmpropertiesgrid',
    store:'RealmProperties',
	title:'Authentication Properties',
    plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })
    ],
    initComponent : function(){
    	
    this.columns=[
        {
        	header:'Property',
        	dataIndex:'name',
        	flex:1,
    	 	renderer:  function(value, metadata, record) {
                 metadata.tdAttr = 'data-qtip="' + record.get('description') +'"';
                 var val = null;
                 if(record.get('required'))
                	 val = value +'<span style="color:red;"><b> *</b></span>';
                 else
                	 val = value;
                 return '<span id="realmPropertyName-'+record.get('name')+'">'+val+'</span>';
                 
             }
        },
        {
        	header:'Value',
        	dataIndex:'value',
        	flex:2,
        	renderer:  function(value, metadata, record) {
        		var val=null;
        		if(record.get('name')=='System Password'){
        			var str='';
        			for(var i=0;i<value.length;i++)
        				str+='*';
        			
        			val=str;
        		}
        		else        			
        			val=value;
        		
        		return '<span id="realmPropertyValue-'+record.get('name')+'">'+val+'</span>';
            },
            getEditor : function(record, defaultField){
            	var inputType=record.get('name')=='System Password' ? 'password' : 'text';
           		
        		return Ext.create('Ext.grid.CellEditor', { 
        			field: Ext.create('Ext.form.field.Text',   {
        			id:'realmPropertyValueEdit-'+record.get('name'),
        			selectOnFocus: true,
        			inputType:inputType,
        			allowBlank:false
        			})
    			});
            		
            }
        }
           
    ];
   
    
    this.callParent(arguments);
    }

});

