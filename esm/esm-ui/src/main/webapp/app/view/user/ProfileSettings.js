Ext.define('Security.view.user.ProfileSettings', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.profilesettings',
    bodyPadding:'5 15 25 15',
    autoScroll:true,
    initComponent: function() {
	    this.items= [
//        {
//            xtype:'component',
//            html:'<div class="profileTabPanelHeading">Email</div><div class="profileTabDescription">All account related information and notification are sent to this address.</div>'
//        },
//        {
//            xtype:'emaillistview',
//            margin:'10 0 0 0'
//        },
//        {
//            xtype:'component',
//            html:'<div class="profileTabDescription">ADD A SECONDARY EMAIL ADDRESS</div>'
//        },
//        {
//            xtype:'container',
//            width:400,
//            margin:'10 0 0 0',
//            flex:1,
//            layout:{type:'hbox',align:'stretch'},
//            items:[
//                {
//                    xtype:'textfield',
//                    flex:1,
//                    emptyText:'Enter Secondary Email Address'
//                },
//                {
//                    xtype:'button',
//                    text:'Add',
//                    ui:'graybutton',
//                    margin:'0 0 0 15'
//                }
//            ]
//        },
        {
        	xtype:'container',
        	itemId:'credentialFormCont',
        	margin:'0 0 0 0'
        }
//	      {
//	      xtype:'component',
//	      margin:'30 0 0 0',
//	      html:'<div class="profileTabPanelHeading">Two-factor Authentication</div><div class="profileTabDescription">Two-factor Authentication provides another layer of security to your account.</div>'
//	      },
//        {
//            xtype:'button',
//            margin:'10 0 0 0',
//            text:'Setup Using SMS',
//            ui:'graybutton'
//        }
    ];
    
	this.callParent(arguments);
	
    },  
    
    update : function(record, authType, credentialObj, callback){
    	var me=this;
	
		var configForm =[];
		if(authType)
			switch(authType){
				case 'UsernamePassword' : configForm=me.getUsernamePasswordAuthenticationForm(); break;
				case 'Certificate' : configForm=me.getCertificateAuthenticationForm(credentialObj[0].credentialKey); break;
				case 'LDAP' : configForm=me.getLdapAuthenticationForm(); break;
			}    		
		else
			configForm =  [{
		             xtype:'component',
					 padding:0,
		             html:credentialObj
		         }];
		
		me.down('#credentialFormCont').removeAll();
		me.down('#credentialFormCont').add(configForm);
		
		
		/*var FormFieldComponent = me.down('#credentialFormCont').down('formfieldcomponent');
		if(FormFieldComponent)
            FormFieldComponent.setEntityId(record.get('id'));*/
		
		
        var FlexFieldComponent = me.down('#credentialFormCont').down('flexfieldcomponent');
        if(FlexFieldComponent)
            FlexFieldComponent.setEntityId(record.get('id'));
		
		if(me.down('#userInfoForm'))
			me.down('#userInfoForm').userInfo=record.data;
		
    	if(me.down('#username'))
    		me.down('#username').setValue(record.get('username'));
    	
//    	if(record.get('emailAddress')){
//    		var data=[{'email':record.get('emailAddress'),'emailType':'PRIMARY'}];
//	    	me.down('emaillistview').getStore().loadData(data);
//    	}
		if(record.data.username=='system' || record.data.username=='admin'){
			if(me.down('#userInfoForm'))
				me.down('#userInfoForm').disable();
			}
		else{
			if(me.down('#userInfoForm'))	
				me.down('#userInfoForm').enable();
			}

    	Ext.callback(callback,this,[]);
    },
    
    getUsernamePasswordAuthenticationForm : function(){

    	return [
		    	 {
		             xtype:'component',
		             margin:'10 0 0 0',
		             html:'<div class="profileTabPanelHeading">Username/Password</div>'
		         },
		         {
		             xtype:'form',
		             itemId:'userInfoForm',
		             width:500,
		             border:0,
		             layout:'anchor',
		             defaults:{
		                 labelAlign:'top',
		                 defaultType:'textfield'
		             },
		             items:[
		                    
		                    {
		 					   fieldLabel:'USERNAME',
		 					   disabled:true,
		 	                   xtype:'textfield',
		 	                   itemId:'username',
		 	                   labelAlign:'top',
		 	                   anchor:'100%',
		 	                   name:'username'
		 	               },
		                    {
		 	                   xtype:'textfield',
		 	                   vtype:'regeXPassword',
		 					   name:'password',
		 					   id:'userPassword',
		 					   inputType: 'password',
		 					   labelAlign:'top',
		 					   fieldLabel:'NEW PASSWORD',
		 					   allowBlank:false,
		 					   anchor:'100%',
		 					   msgTarget :'side',
		 					   enableFieldFocus:false	                   
		 	               },
		 	               {
		 	                   xtype:'textfield',
		 	                   name:'confirmPassword',
		 	                   id:'userPassword2',
		 	                   msgTarget:'qtip',
		 	                   inputType: 'password',
		 	                   fieldLabel:'CONFIRM NEW PASSWORD',
		 	                   vtype:'password',
		 	                   anchor:'100%',
		 	                   initialPassField: 'userPassword',
		 	                   allowBlank:false
		 	               },
		                   {
		                     xtype:'button',
		                     margin:'10 0 0 0',
		                     formBind:true,
		                     text:'Update',
		                     ui:'graybutton',
		                     itemId:'updateCred',
		                     action: 'updateCred'
		    	           }
		             ]
		         },
               {
            	   xtype:'flexfieldcomponent',
            	   layout:'anchor',
            	   defaults:{'anchor':'50%'},
            	   margin:'10 0 10 0',
            	   fieldConfigurationUrl:'resources/json/TestFieldConfiguration.json'
               }
              /* {
                   xtype:'formfieldcomponent',
                   layout:'anchor',
                   defaults:{'anchor':'50%'},
                   margin:'10 0 10 0',
                   loadUrl:'resources/json/TestFormFieldConfiguration.json',
                   saveUrl: 'resources/json/TestFormFieldConfiguration.json'
               }*/
	         ];
    	
    },
    
    getCertificateAuthenticationForm : function(certificateKey){
    	
    	return [
    	        {
    	            xtype:'component',
    	            margin:'10 0 0 0',
    	            html:'<div class="profileTabPanelHeading">Certificate Configuration</div>'
    	        },
    	        {
    	            xtype:'form',
    	            itemId:'userInfoForm',
    	            width:500,
    	            border:0,
    	            layout:'anchor',
    	            defaults:{
    	                labelAlign:'top',
    	                defaultType:'textfield'
    	            },
    	            items:[                   
    	               {
    	                   xtype:'textarea',
						   value:certificateKey,
    	                   fieldLabel:'CERTIFICATE',
    	                   labelAlign:'top',
    	                   anchor:'100%',
						   allowBlank:false,
						   minLength:256,
						   minLengthText:'Certificate should contain atleast 256 characters.',
    	                   name:'certificate'
    	               },
    	                {
    	                    xtype:'button',
    	                    margin:'10 0 0 0',
    	                    formBind:true,
    	                    text:'Update',
    	                    ui:'graybutton',
    	                    itemId:'updateCertificate',
    	                    action: 'updateCertificate',
    	   	             
    	                }
    	            ]
    	        }
    	        ];
    },
    
    getLdapAuthenticationForm : function(){
    	return [
    	        {
    	            xtype:'component',
    	            margin:'10 0 0 0',
    	            html:'<div class="profileTabPanelHeading">LDAP Configuration</div>'
    	        },
    	        {
    	            xtype:'form',
    	            itemId:'userInfoForm',
    	            width:500,
    	            border:0,
    	            layout:'anchor',
    	            defaults:{
    	                labelAlign:'top',
    	                defaultType:'textfield'
    	            },
    	            items:[
    	                   
    	                   {
    		                   xtype:'displayfield',
    		                   itemId:'username',
    		                   name:'username',
    		                   fieldLabel:'USERNAME',
    		                   anchor:'100%'
    		               }
    	            ]
    	        }
    	        ];
    }

});
