Ext.define('Security.view.organization.CreateOrganizationUser', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.createorganizationuser',
    minHeight:1000,
    autoScroll:true,
    bodyPadding:20,  
    border:false,
    layout:'anchor',
    defaults:{anchor:'50%'},
	treeId:'manageUsers',
    initComponent : function(){
    
    var me=this;
    
    var userTypeStore = [['certificate','System User with Certificate'],['user','User']];
    
    var securityRealm = me.organization.get('securityRealms');
      
    	if((securityRealm.length==0 || securityRealm[0].get('enabled')==false)){}
   else
	   userTypeStore.push(['ldap','LDAP User']);

    
    this.items = [
                {
                    xtype:'component',
                    html:'<a href="#" id="organizationUsers-link" class="redirectURL quickLinks">BACK TO USERS LIST</a>',
                    listeners : {
                    'afterrender':function () {
                            this.getEl().on('click', function(e, t, opts) {
                                e.stopEvent();
                                if(me.redirectPage){
                                       window.location = me.redirectPage;
                                }
                            }, null, {delegate: '.redirectURL'});

                        }
                    }
                },
                {
                    xtype:'component',
                    html:'<h1>Create User</h1>'
                }, 
                {
                    xtype:'combobox',
                    name:'typeOfUser',
                    id:'createOrganizationUsers-typeOfUser',
                    itemId:'typeOfUser',
                    fieldLabel:'User Type',
                    emptyText:'Select type of user',
                    labelAlign:'top',
                    editable:false,
                    allowblank:false,
                    store:userTypeStore,
                    listeners : {
                    'change' : function(combo, value){
                           var fields = null;
                           switch(value){
                                  case 'certificate' : fields = me.getSystemUserFields(); break;
                                  case 'user' : fields = me.getHumanUserFields(); break;
                                  case 'ldap' : fields = me.getLdapUserFields(); break;
                           }
                           me.down('#userTypeFieldsCont').removeAll();
                           me.down('#userTypeFieldsCont').add(fields);
                           
                     }
                    }
                },
                {
                xtype:'container',
                   itemId:'userTypeFieldsCont'
                }
            ];
    
    this.callParent(arguments);
    },
    
    getCommonUserAttributes : function(){
       
       return [
               {
                          xtype: 'checkbox',
                          inputValue:true,
                          uncheckedValue:false,
                          name: 'active',
                          id:'createOrganizationUsers-active',
                          checked:true,
                          boxLabel: 'Active',
                          fieldLabel: 'Status'
                      },
                      {
                          xtype:'component',
                          margin:'30 0 0 0',
                          html:'<h3>Contact Information</h3>'
                      },
                      {
                          xtype:'textfield',
                          name:'firstName',
                          id:'createOrganizationUsers-firstName',
                          maxLength:20,
                          regex: /^[A-Za-z0-9 _]*$/,
						  regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
						  msgTarget: 'side',
                          fieldLabel:'First Name',
                          allowBlank:false
                      },
                      {
                          xtype:'textfield',
                          name:'middleName',
                          id:'createOrganizationUsers-middleName',
                          maxLength:20,
						  regex:/^[A-Za-z0-9 _]*$/,
						  regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
						  msgTarget: 'side',
                          fieldLabel:'Middle Name',
                          defaultValue:''
                      },
                      {
                          xtype:'textfield',
                          name:'lastName',
                          id:'createOrganizationUsers-lastName',
                          maxLength:19,
						  regex: /^[A-Za-z0-9 _]*$/,
						  regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
						  msgTarget: 'side',
                          fieldLabel:'Last Name',
                          allowBlank:false
                      },
                      {
                          xtype:'textfield',
                          name:'salutation',
                          id:'createOrganizationUsers-salutation',
                          maxLength:20,
						  regex: /^[A-Za-z0-9 _]*$/,
						  regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
						  msgTarget: 'side',
                          fieldLabel:'Title',
                          allowBlank:true
                      },
                      {
                          xtype:'textfield',
                          name:'emailAddress',
                          id:'createOrganizationUsers-emailAddress',
                          fieldLabel:'Email',
                          maxLength:100,
                          vtype: 'email',
                          allowBlank:false
                      },
                      {
                               xtype:'component',
                               margin:'30 0 0 0',
                               html:'<div style="border-style:dashed none  none none; border-width: 1px; border-color: gray;"></div>'
                           },
                           {
                               xtype:'container',
                               margin:'10 0 0 0',
                               layout:'hbox',
                                items:[
                                   {
                                       xtype:'tbspacer',
                                       flex:1
                                   },
                                   {
                                       xtype:'button',
                                       formBind:true,
                                       action:'createUser',
                                       id:'createOrganizationUsers-createUser',
                                       text:'Save',
                                       ui:'bluebutton'
                                   }
                               ]
                           }
                     ];
       
    },
    
    getSystemUserFields : function(){
       var itemsConfig = [
				                          {
				                              xtype:'component',
				                              margin:'30 0 0 0',
				                              html:'<h3>Basic Information</h3>'
				                          },
                                         {
                                             xtype:'textfield',
                                             fieldLabel:'Domain',
                                             name:'domain',
                                             id:'createOrganizationUsers-domain',
                                             labelAlign:'top',
                                             allowBlank:false,
                                             readOnly:true,											 
                                             value:this.organization.get('tenant').domain
                                         },
                                         {
                                             xtype:'textfield',
                                             name:'username',
                                             id:'createOrganizationCertUsers-username',
                                             maxLength:60,
                                             fieldLabel:'Username',
                                             allowBlank:false,
                                         },
                                         {
                                             xtype:'textarea',
                                             fieldLabel:'Certificate',
                                             name:'certificate',
                                             id:'createOrganizationUsers-certificate',
                                             labelAlign:'top',
                                             allowBlank:false,
                                             minLength:256,
                                             minLengthText:'Certificate should contain atleast 256 characters.'
                                             
                                         }
                                         ];
       Ext.each(this.getCommonUserAttributes(),function(item){
              itemsConfig.push(item);
       });
       var config = [
                                  {
                                      xtype:'form',
                                      border:false,
                                      layout:'anchor',
                                      defaults:{labelAlign:'top',labelSeparator:'',anchor:'100%'},
                                      items:itemsConfig
                                  }
                
            ];       
       return config;       
    },
    
    
    getHumanUserFields : function(){
       
       var itemsConfig = [
                          {
                              xtype:'component',
                              margin:'30 0 0 0',
                              html:'<h3>Basic Information</h3>'
                          },
                          {
                              xtype:'textfield',
                              name:'username',
                              id:'createOrganizationUsers-username',
                              maxLength:60,
                              fieldLabel:'Username',
                              allowBlank:false,
                              regex: /^[A-Za-z0-9 _]*$/,
							  regexText: '# check that the string contains *only* one or more alphanumeric chars or underscores',
							  msgTarget: 'side',
                          },
                          {
                              xtype:'textfield',
                              name:'password',
	                          id:'userPassword',
	                          inputType: 'password',
	                          fieldLabel:'Password',
	                          vtype:'regeXPassword',
	                          msgTarget: 'side',
	                          allowBlank:false,
	                          enableFieldFocus:false                    
                          },
                          {
                              xtype:'textfield',
                              name:'confirmPassword',                              
                              id:'userPassword2',
                              inputType: 'password',
                              fieldLabel:'Confirm Password',
                              vtype:'password',
                              initialPassField: 'userPassword',
                              allowBlank:false
                          }
                      ];
       Ext.each(this.getCommonUserAttributes(),function(item){
              itemsConfig.push(item);
       });
              var config = [
                                  {
                                      xtype:'form',
                                      border:false,
                                      layout:'anchor',
                                      defaults:{labelAlign:'top',labelSeparator:'',anchor:'100%'},
                                      items:itemsConfig
                                  }
                          	];          
              return config;       
    },
    
    getLdapUserFields : function(){
    var me=this;
       var itemsConfig = [
                          {
                              xtype:'component',
                              margin:'30 0 0 0',
                              html:'<h3>Basic Information</h3>'
                          },
                          {
                              xtype:'textfield',
                              fieldLabel:'Domain',
                              name:'domain',
                              id:'createOrganizationUsers-domain',
                              labelAlign:'top',
                              allowBlank:false,
                              readOnly:true,
                              value:me.organization.get('tenant').domain                               
                          },
                          {
                              xtype:'textfield',
                              name:'username',
                              id:'createOrganizationUsers-username',
                              maxLength:60,
                              fieldLabel:'Username',
                              allowBlank:false,
                              enableKeyEvents :true,
                              listeners : {
                            	  'keyup' : function(field){
                            		  var form = field.up('form');
                            		  if(!form.down('#userDetailCont').isHidden())
                            			  form.down('#userDetailCont').hide();
                            	  }
                              }
                          },
                          {
                              xtype:'container',
                              margin:'10 0 0 0',
                              layout:'hbox',
                              items:[
                                  {
                                      xtype:'tbspacer',
                                      flex:1
                                  },
                                  {
                                      xtype:'button',
                                      margin:'0 0 0 10',
                                      action:'getLdapUserAttributes',
                                      id:'createOrganizationUsers-getLdapUserAttributes',
                                      text:'Verify Username',
                                      ui:'bluebutton'
                                  }
                              ]
                          },
                          {
                              xtype:'container',
                              hidden:true,
                              itemId:'userDetailCont',
                              layout:'anchor',
                              defaults:{labelAlign:'top',labelSeparator:'',anchor:'100%'},
                              items:me.getCommonUserAttributes()
                          }
                         
                      ];
       

              var config = [
                                  {
                                      xtype:'form',
                                      border:false,
                                      layout:'anchor',
                                      defaults:{labelAlign:'top',labelSeparator:'',anchor:'100%'},
                                      items:itemsConfig
                                  }
                   
               ];          
              return config;       
    }
    

});
