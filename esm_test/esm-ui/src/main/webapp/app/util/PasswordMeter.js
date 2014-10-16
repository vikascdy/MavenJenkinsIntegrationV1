
Ext.define('Util.PasswordMeter',{
	extend:'Ext.container.Container',
	alias:'widget.passwordmeter',
	layout:{type:'vbox',align:'stretch'},
	config:{
		errorMsgMap : null
	},
	initComponent : function(){
		
	var me=this;
    
    Ext.QuickTips.init();
    
    me.setErrorMsgMap(new Ext.util.HashMap());
	
	var tip = Ext.create('Ext.tip.ToolTip', {
	    target: 'passwordHelp',
	    html: 'Password must contain : <br/>'+
	    		'- One uppercase character<br/>'+
	    		'- One lowercase character<br/>'+
	    		'- One numeric character<br/>'+
	    		'- One special character<br/>'+
	    		'- Minimum 8 characters<br/>'
	});
	

	this.items=[
		
		{
			xtype:'textfield',
			labelSeparator:'',
			flex:1,
			enableKeyEvents:true,
			msgTarget:'side',
			fieldLabel:me.config.fieldLabel,
			inputType:'password',
			name:me.config.name,
            id:me.config.id,
//            minLength:me.config.minLength,
//            maxLength:me.config.maxLength,
            allowBlank:me.config.allowBlank,
            cls : me.config.cls ? me.config.cls : '',
            labelCls : me.config.labelCls ? me.config.labelCls : 'x-form-item-label',
            fieldCls : me.config.fieldCls ? me.config.fieldCls : 'x-form-field',
            labelAlign : me.config.labelAlign ? me.config.labelAlign : 'left',
            disabled:me.config.disabled,  
            validator: me.validatePassword,
			listeners : {
				'afterrender' :  function(field) {
					if(me.config.enableFieldFocus)
						field.focus(false, 200);
                },
				'keyup':function(field,e){
					me.updateMeter(field,e);
				},
				'focus':function(field,e){
					var objMeter=Ext.get('strengthMeter');
					if(!Ext.isOpera) // don't touch in Opera
		            	objMeter.addCls('strengthMeter-focus');
				},
				'blur':function(field,e){
					var objMeter=Ext.get('strengthMeter');
					if(!Ext.isOpera) // don't touch in Opera
						objMeter.removeCls('strengthMeter-focus');
					}					
				}
		},
		{
			xtype:'container',
			margin:me.config.labelAlign=='top' ? '0 0 0 10' : '',
			layout:{type:'hbox',align:'stretch',pack:'middle'},
			items:[

					{
						   xtype:'component',
						   hidden:me.config.labelAlign=='top',
						   flex:1
					},
			        {
						xtype:'component',
						height:20,
						width:200,
						html:'<div id="strengthMeter" class="strengthMeter"><div id="scoreBar" class="scoreBar"></div></div>'
					},
					{
						xtype:'component',
						height:20,
						width:15,
						mragin:'0 10 0 0',
						html:'<div id="passwordHelp" class="info-status" style="cursor:pointer; width:15px; height:15px;"></div>',
						listeners : {
							'afterrender' : function(){
								this.getEl().on('mouseover', function(e) {
		                            e.stopEvent();
		                            tip.showAt(e.getXY());
		                        }, null, {delegate: '.info-status'});
							}
						}
					},
					{
				    	   xtype:'component',
				    	   hidden:me.config.labelAlign!='top',
				    	   flex:1
			       }
			     ]
		}
		
	];
	
	this.callParent(arguments);	

	},
	
	validatePassword : function(v){
		
		var me=this.up('passwordmeter');
		
		var errorMsgMap = me.getErrorMsgMap();	
		var errorCount = 0;
		var errorMessages='Password does not satisfy following rules :<br/>';
		
		errorMsgMap.add('1',{msg:'Minimum 1 lowercase character',status:1});
		errorMsgMap.add('2',{msg:'Minimum 1 uppercase character',status:1});
		errorMsgMap.add('3',{msg:'Minimum 1 numeric character',status:1});
		errorMsgMap.add('4',{msg:'Minimum 1 special character',status:1});
		errorMsgMap.add('5',{msg:'Minimum length 8 characters',status:1});
		
		var errorMsg = 'Password does not satisfy following rules :<br/>';
		var errorFlag = false;
		
		if((/[a-z]/).test(v))
			errorMsgMap.get(1).status=0;
		if((/[A-Z]/).test(v))
			errorMsgMap.get(2).status=0;
		if((/\d/).test(v))
			errorMsgMap.get(3).status=0;
		if((/[$|~=[\]'`_+@!#%^&*(){};:"<>,?.-]/).test(v))
			errorMsgMap.get(4).status=0;
		if(v.length>=8)
			errorMsgMap.get(5).status=0;
		
		Ext.each(errorMsgMap.getValues(),function(msgObj){
			if(msgObj.status==1){
				errorCount +=1;
				errorMessages +='- '+(msgObj.msg)+'<br/>';
			}
		});
			
		if(errorCount>0)
			return errorMessages;
		else
			return true;
			
		
//        return (/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$|~=[\]'`_+@!#%^&*(){};:"<>,?.-])[a-zA-Z0-9$|~=[\]'`_+@!#%^&*(){};:"<>,?.-]{8,}$/.test(v)?true:'Password does not satisfy following rules.');
		
	},
	
	updateMeter: function(field,e) {
		
	
			var me=field.up('passwordmeter');
			
			var objMeter=Ext.get('strengthMeter');
			var scoreBar=Ext.get('scoreBar');
		
		    var p = e.target.value;
			
			var maxWidth = objMeter.getWidth() - 2;
			
			this.calcStrength(p,function(nScore){
				
				// Set new width
	    		var nRound = Math.round(nScore * 2);

				if (nRound > 100) {
					nRound = 100;
				}

				var scoreWidth = (maxWidth / 100) * nRound;
				scoreBar.setWidth(scoreWidth, true);
			});			
    		
		},
		
		
		calcStrength: function(p, callback) {
			var me=this;
			
			var intScore = 0;

			// PASSWORD LENGTH
			intScore += p.length;
			
			if(p.length > 0 && p.length <= 4) {                    // length 4
																	// or less
				intScore += p.length;
			}
			else if (p.length >= 5 && p.length <= 7) {	// length between 5 and
														// 7
				intScore += 6;
			}
			else if (p.length >= 8 && p.length <= 15) {	// length between 8 and
														// 15
				intScore += 12;
				// alert(intScore);
			}
			else if (p.length >= 16) {               // length 16 or more
				intScore += 18;
				// alert(intScore);
			}
			
			// LETTERS (Not exactly implemented as dictacted above because of my
			// limited understanding of Regex)
			if (p.match(/[a-z]/)) {              // [verified] at least one
													// lower case letter
				intScore += 1;
			}
			if (p.match(/[A-Z]/)) {              // [verified] at least one
													// upper case letter
				intScore += 5;
			}
			// NUMBERS
			if (p.match(/\d/)) {             	// [verified] at least one
												// number
				intScore += 5;
			}
			if (p.match(/.*\d.*\d.*\d/)) {            // [verified] at least
														// three numbers
				intScore += 5;				
			}
			
			// SPECIAL CHAR
			if (p.match(/[!,@,#,$,%,^,&,*,?,_,~]/)) {           // [verified] at
																// least one
																// special
																// character
				intScore += 5;
			}
			// [verified] at least two special characters
			if (p.match(/.*[!,@,#,$,%,^,&,*,?,_,~].*[!,@,#,$,%,^,&,*,?,_,~]/)) {
				intScore += 5;
			}
			
			// COMBOS
			if (p.match(/(?=.*[a-z])(?=.*[A-Z])/)) {        // [verified] both
															// upper and lower
															// case
				intScore += 2;
			}
			if (p.match(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])/)) { // [verified]
																// both letters
																// and numbers
				intScore += 2;
			}
	 		// [verified] letters, numbers, and special characters
			if (p.match(/(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!,@,#,$,%,^,&,*,?,_,~])/)) {
				intScore += 2;
			}
			
			Ext.callback(callback,this,[intScore]);		
		}
	
	
});

