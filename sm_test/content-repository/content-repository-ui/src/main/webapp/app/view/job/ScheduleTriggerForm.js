Ext
		.define(
				'SM.view.job.ScheduleTriggerForm',
				{
					extend : 'Ext.form.Panel',
					alias : 'widget.scheduletriggerform',
					flex : 1,
					bodyPadding : '5',
					layout : 'anchor',
					defaults : {
						anchor : '100%',
						flex : 1
					},

					initComponent : function() {

						this.items = [
			              	{
							xtype : 'fieldset',							
							title : 'Settings',
							layout : 'hbox',
							items : [ {
								xtype : 'radiogroup',
								itemId:'settings',
								columns : 1,
								vertical : true,
								border : '0 2 0 0',
								width : 100,
								defaults : {
									flex : 1
								},
								items : [ {
									boxLabel : 'One time',
									name : 'time',
									inputValue : 'oneTime',
									checked : true
								}, {
									boxLabel : 'Daily',
									name : 'time',
									inputValue : 'daily'
								}, {
									boxLabel : 'Weekly',
									name : 'time',
									inputValue : 'weekly'
								}, {
									boxLabel : 'Monthly',
									name : 'time',
									inputValue : 'monthly'
								},
								{
									boxLabel : 'Yearly',
									name : 'time',
									inputValue : 'yearly'
								}],
								listeners : {
									change : function(radioGrp, value) {
										this.up('form').showTimeConfig(value);
									}
								}

							}, {
								xtype : 'container',
								flex : 1,
								items : [ {
									xtype : 'container',
									layout : {
										type : 'hbox',
										pack : 'start',
										align : 'middle'
									},
									defaults : {
										margin : '0 5 0 0'
									},
									items : [ {
										xtype : 'component',
										html : 'Starts:'
									}, {
										xtype : 'datefield',
										value : new Date(),
										maxValue : new Date()
									}, {
										xtype : 'timefield',
										value : '12:00 PM'
									// value: Ext.Date.format(new
									// Date(),'G:i:s'),
									// increment:Ext.Date.format(new Date(),'s')
									} ]
								}, {
									xtype : 'fieldset',
									margin : '5 0 0 0',
									minHeight : 120,
									id : 'timeConfig',
									disabled : true

								} ]

							}
							
							
							]
						},

						{
							xtype : 'fieldset',
							title : 'Advanced settings',
							itemId:'advancedSettings',
							items:[
							       {
							    	   xtype:'fieldcontainer',
							    	   layout:{
							    		   type:'hbox',
							    		   pack:'start',
							    		   align:'middle'
							    	   },
							    	   items:[
							    	          {
							    	        	  xtype:'checkbox',
							    	        	  boxLabel:'Delay job for up to (random delay)'
							    	          },
							    	          {
							    	        	  xtype:'combo',
							    	        	  margin:'0 0 0 5'							    	        	  
							    	          }
							    	          ]
							       },
							       {
							    	   xtype:'fieldcontainer',
							    	   layout:{
							    		   type:'hbox',
							    		   pack:'start',
							    		   align:'middle'
							    	   },
							    	   items:[
							    	          {
							    	        	  xtype:'checkbox',
							    	        	  boxLabel:'Stop job if it runs longer than'
							    	          },
							    	          {
							    	        	  xtype:'combo',
							    	        	  margin:'0 0 0 5'							    	        	  
							    	          }
							    	          ]
							       },
							       {
					    	        	  xtype:'checkbox',
					    	        	  itemId:'enabledStatus',
					    	        	  boxLabel:'Enabled',
					    	        	  checked:true
					    	          }
							       ]
						}];

						this.callParent(arguments);
					},
					showTimeConfig : function(value) {
						var timeConfigField = Ext.getCmp('timeConfig');
						var items = null;
						switch (value.time) {
						case 'oneTime':
							timeConfigField.setDisabled(true);
							break;
						case 'daily':
							items = this.getDailyTimeConfig();
							break;
						case 'weekly':
							items = this.getWeeklyTimeConfig();
							break;
						case 'monthly':
							items = this.getMonthlyTimeConfig();
							break;
						case 'yearly':
							items = this.getYearlyTimeConfig();
							break;
						}

						timeConfigField.setDisabled(false);
						timeConfigField.removeAll();
						timeConfigField.add(items);
					},
					getDailyTimeConfig : function() {
						return [ {
							xtype : 'fieldcontainer',
							layout : {
								type : 'hbox',
								pack : 'start',
								align : 'middle'
							},
							items : [ {
								xtype : 'numberfield',
								value : 1,
								fieldLabel : 'Recur every'
							}, {
								xtype : 'label',
								text : 'days',
								margin : '0 0 0 5'
							} ]
						} ];
					},
					getWeeklyTimeConfig : function() {
						return [ {
							xtype : 'fieldcontainer',
							layout : {
								type : 'hbox',
								pack : 'start',
								align : 'middle'
							},
							items : [ {
								xtype : 'numberfield',
								value : 1,
								fieldLabel : 'Recur every'
							}, {
								xtype : 'label',
								text : 'weeks on:',
								margin : '0 0 0 5'
							} ]
						}, {
							xtype : 'checkboxgroup',
							columns : 4,
							items : [ {
								boxLabel : 'Sunday',
								name : 'days',
								inputValue : 'sunday'
							}, {
								boxLabel : 'Monday',
								name : 'days',
								inputValue : 'monday'
							}, {
								boxLabel : 'Tuesday',
								name : 'days',
								inputValue : 'tuesday'
							}, {
								boxLabel : 'Wednesday',
								name : 'days',
								inputValue : 'wednesday'
							}, {
								boxLabel : 'Thursday',
								name : 'days2',
								inputValue : 'thursday'
							}, {
								boxLabel : 'Friday',
								name : 'days',
								inputValue : 'friday'
							}, {
								boxLabel : 'Saturday',
								name : 'days',
								inputValue : 'saturday'
							} ]
						} ];
					},
					getMonthlyTimeConfig : function() {
						return [
								{
									xtype : 'combo',
									multiSelect : true,
									forceSelection : true,
									editable : false,
									flex : 1,
									mode : 'local',
									triggerAction : 'all',
									listConfig : {
										getInnerTpl : function() {
											return '<div class="x-combo-list-item"><img src="'
													+ Ext.BLANK_IMAGE_URL
													+ '"'
													+ ' class="chkCombo-default-icon chkCombo" /> {display} </div>';
										}
									},
									fieldLabel : 'Months',
									labelWidth:65,
									displayField : 'display',
									valueField : 'value',
									store : Ext.create('Ext.data.Store', {
										fields : [ 'display', 'value' ],
										autoLoad : true,
										data : [ {
											display : 'January',
											value : 'january'
										}, {
											display : 'February',
											value : 'february'
										}, {
											display : 'March',
											value : 'march'
										}, {
											display : 'April',
											value : 'april'
										}, {
											display : 'May',
											value : 'may'
										}, {
											display : 'June',
											value : 'june'
										}, {
											display : 'July',
											value : 'july'
										}, {
											display : 'August',
											value : 'august'
										}, {
											display : 'September',
											value : 'september'
										}, {
											display : 'October',
											value : 'october'
										}, {
											display : 'November',
											value : 'november'
										}, {
											display : 'December',
											value : 'december'
										} ]
									})
								},
								{
									xtype : 'fieldcontainer',
									flex : 1,
									layout : {
										type : 'hbox',
										pack : 'start',
										align : 'middle'
									},
									items : [
											{
												xtype : 'radiogroup',
												width : 70,
												columns : 1,
												vertical : true,
												border : '0 2 0 0',
												items : [ {
													boxLabel : 'Days',
													name : 'day',
													inputValue : 'days',
													checked : true
												}, {
													boxLabel : 'On',
													name : 'day',
													inputValue : 'on'
												} ],
												listeners : {
													change : function(radioGrp,value) {
														this.up('form').toggleTimeConfig(value);
													}
												}

											},
											{
												xtype : 'form',
												width:250,
												defaults : {
													anchor : '100%',
													flex : 1
												},
												border:false,
												items : [

														{
															xtype : 'pickerfield',
															id : 'daysPicker',
															
															createPicker : function() {
																var me = this, picker = new Ext.panel.Panel(
																		{
																			pickerField : me,
																			floating : true,
																			hidden : true,
																			ownerCt : this.ownerCt,
																			layout : 'anchor',
																			defaults : {
																				anchor : '100%',
																				flex : 1
																			},
																			items : {
																				xtype : 'checkboxgroup',
																				defaults : {
																					margin : '0 5 0 0'
																				},
																				columns : 7,
																				items : [
																						{
																							boxLabel : '1',
																							name : 'day',
																							inputValue : '1'
																						},
																						{
																							boxLabel : '2',
																							name : 'days',
																							inputValue : '2'
																						},
																						{
																							boxLabel : '3',
																							name : 'days',
																							inputValue : '3'
																						},
																						{
																							boxLabel : '4',
																							name : 'days',
																							inputValue : '4'
																						},
																						{
																							boxLabel : '5',
																							name : 'days2',
																							inputValue : '5'
																						},
																						{
																							boxLabel : '6',
																							name : 'days',
																							inputValue : '6'
																						},
																						{
																							boxLabel : '7',
																							name : 'days',
																							inputValue : '7'
																						},
																						{
																							boxLabel : '8',
																							name : 'days',
																							inputValue : '8'
																						},
																						{
																							boxLabel : '9',
																							name : 'days',
																							inputValue : '9'
																						},
																						{
																							boxLabel : '10',
																							name : 'days',
																							inputValue : '10'
																						},
																						{
																							boxLabel : '11',
																							name : 'days',
																							inputValue : '11'
																						},
																						{
																							boxLabel : '12',
																							name : 'days',
																							inputValue : '12'
																						},
																						{
																							boxLabel : '13',
																							name : 'days',
																							inputValue : '13'
																						},
																						{
																							boxLabel : '14',
																							name : 'days',
																							inputValue : '14'
																						},
																						{
																							boxLabel : '15',
																							name : 'days',
																							inputValue : '15'
																						},
																						{
																							boxLabel : '16',
																							name : 'days',
																							inputValue : '16'
																						},
																						{
																							boxLabel : '17',
																							name : 'days',
																							inputValue : '17'
																						},
																						{
																							boxLabel : '18',
																							name : 'days',
																							inputValue : '18'
																						},
																						{
																							boxLabel : '19',
																							name : 'days',
																							inputValue : '19'
																						},
																						{
																							boxLabel : '20',
																							name : 'days',
																							inputValue : '20'
																						},
																						{
																							boxLabel : '21',
																							name : 'days',
																							inputValue : '21'
																						},
																						{
																							boxLabel : '22',
																							name : 'days',
																							inputValue : '22'
																						},
																						{
																							boxLabel : '23',
																							name : 'days',
																							inputValue : '23'
																						},
																						{
																							boxLabel : '24',
																							name : 'days',
																							inputValue : '24'
																						},
																						{
																							boxLabel : '25',
																							name : 'days',
																							inputValue : '25'
																						},
																						{
																							boxLabel : '26',
																							name : 'days',
																							inputValue : '26'
																						},
																						{
																							boxLabel : '27',
																							name : 'days',
																							inputValue : '27'
																						},
																						{
																							boxLabel : '28',
																							name : 'days',
																							inputValue : '28'
																						},
																						{
																							boxLabel : '29',
																							name : 'days',
																							inputValue : '29'
																						},
																						{
																							boxLabel : '30',
																							name : 'days',
																							inputValue : '30'
																						},
																						{
																							boxLabel : '31',
																							name : 'days',
																							inputValue : '31'
																						} ]
																			}
																		});
																return picker;
															},
														},

														{
															xtype : 'container',
															layout : {
																type : 'hbox',
																pack : 'end',
																align : 'middle'
															},
															items : [

																	{
																		xtype : 'combo',
																		width:96,
																		disabled : true,
																		id : 'onPicker',
																		multiSelect : true,
																		forceSelection : true,
																		editable : false,
																		mode : 'local',
																		triggerAction : 'all',
																		listConfig : {
																			getInnerTpl : function() {
																				return '<div class="x-combo-list-item"><img src="'
																						+ Ext.BLANK_IMAGE_URL
																						+ '"'
																						+ ' class="chkCombo-default-icon chkCombo" /> {display} </div>';
																			}
																		},
																		displayField : 'display',
																		valueField : 'value',
																		store : Ext
																				.create(
																						'Ext.data.Store',
																						{
																							fields : [
																									'display',
																									'value' ],
																							autoLoad : true,
																							data : [
																									{
																										display : 'First',
																										value : 'first'
																									},
																									{
																										display : 'Second',
																										value : 'second'
																									},
																									{
																										display : 'Third',
																										value : 'third'
																									},
																									{
																										display : 'Fourth',
																										value : 'fourth'
																									},
																									{
																										display : 'Last',
																										value : 'last'
																									} ]
																						})
																	},
																	{
																		xtype : 'combo',
																		disabled : true,
																		id : 'onPickerDays',
																		margin : '0 0 0 5',
																		multiSelect : true,
																		forceSelection : true,
																		editable : false,
																		mode : 'local',
																		triggerAction : 'all',
																		listConfig : {
																			getInnerTpl : function() {
																				return '<div class="x-combo-list-item"><img src="'
																						+ Ext.BLANK_IMAGE_URL
																						+ '"'
																						+ ' class="chkCombo-default-icon chkCombo" /> {display} </div>';
																			}
																		},
																		displayField : 'display',
																		valueField : 'value',
																		store : Ext
																				.create(
																						'Ext.data.Store',
																						{
																							fields : [
																									'display',
																									'value' ],
																							autoLoad : true,
																							data : [
																									{
																										display : 'Sunday',
																										value : 'sunday'
																									},
																									{
																										display : 'Monday',
																										value : 'monday'
																									},
																									{
																										display : 'Tuesday',
																										value : 'tuesday'
																									},
																									{
																										display : 'Wednesday',
																										value : 'wednesday'
																									},
																									{
																										display : 'Thursday',
																										value : 'thursday'
																									},
																									{
																										display : 'Friday',
																										value : 'friday'
																									},
																									{
																										display : 'Saturday',
																										value : 'saturday'
																									} ]
																						})
																	}

															]
														}

												]
											}

									]

								} ];
					},
					toggleTimeConfig : function(value) {

						var daysPicker = Ext.getCmp('daysPicker');
						var onPicker = Ext.getCmp('onPicker');
						var onPickerDays = Ext.getCmp('onPickerDays');
						if (value.day == 'on') {
							daysPicker.setDisabled(true);
							onPicker.setDisabled(false);
							onPickerDays.setDisabled(false);
						} else if (value.day == 'days') {
							daysPicker.setDisabled(false);
							onPicker.setDisabled(true);
							onPickerDays.setDisabled(true);
						}
					}
				});
