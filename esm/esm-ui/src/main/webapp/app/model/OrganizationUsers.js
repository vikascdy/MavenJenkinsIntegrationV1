// -----------------------------------------------------------------------------
// Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------

Ext.define('Security.model.OrganizationUsers', {
	extend : 'Ext.data.Model',
	idProperty : 'id',
	requires : [ 'Security.model.Contact' ],
	fields : [
			{
				name : 'id',
				type : 'long'
			},
			{
				name : 'humanUser',
				type : 'boolean'
			},
			{
				name : 'changePasswordAtFirstLogin',
				type : 'boolean'
			},
			{
				name : 'active',
				type : 'boolean',
				defaultValue : true
			},
			{
				name : 'suspended',
				type : 'boolean',
				defaultValue : false
			},
			{
				name : 'deleted',
				type : 'boolean',
				defaultValue : false
			},
			{
				name : 'createdDateTime',
				type : 'string'
			},
			{
				name : 'formattedCreatedDateTime',
				convert : function(value, record) {
					var dueDate = record.get('createdDateTime');
					if (dueDate) {
						var date = new Date(dueDate);
						return Ext.Date.format(date, 'd M Y, H:i:s');
					} else
						return "";
				}
			},
			{
				name : 'modifiedDateTime',
				type : 'string'
			},
			{
				name : 'formattedModifiedDateTime',
				convert : function(value, record) {
					var dueDate = record.get('modifiedDateTime');
					if (dueDate) {
						var date = new Date(dueDate);
						return Ext.Date.format(date, 'd M Y, H:i:s');
					} else {
						return "";
					}
				}
			},
			{
				name : 'lastLoginDateTime',
				type : 'string'
			},
			{
				name : 'formattedLastLoginDateTime',
				convert : function(value, record) {
					var lastLoginDateTime = record.get('lastLoginDateTime');
					if (lastLoginDateTime) {
						var date = new Date(lastLoginDateTime);
						return Ext.Date.format(date, 'd M Y, H:i:s');
					} else {
						return;
					}
				}
			},

			{
				name : 'contact',
				type : 'Security.model.Contact',
				defaultValue : null
			},

			{
				name : 'name',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						if (contact.salutation) {
							return contact.salutation + ' ' + contact.firstName
									+ ' ' + contact.lastName;
						} else {
							return contact.firstName + ' ' + contact.lastName;
						}
					} else {
						return record.get('username');
					}
				}
			}, {
				name : 'salutation',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						return contact.salutation;
					} else {
						return;
					}
				}
			}, {
				name : 'firstName',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						return contact.firstName;
					} else {
						return record.get('username');
					}
				}
			}, {
				name : 'middleName',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						return contact.middleName;
					} else {
						return;
					}
				}
			}, {
				name : 'lastName',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						return contact.lastName;
					} else {
						return;
					}
				}
			}, {
				name : 'address',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						return contact.address;
					} else {
						return;
					}
				}
			}, {
				name : 'emailAddress',
				convert : function(value, record) {
					var contact = record.get('contact');
					if (contact) {
						return contact.emailAddress;
					} else {
						return;
					}
				}
			}, {
				name : 'username',
				type : 'string'
			} 
			],

			
//			proxy : {
//				type : 'rest',
//				url : JSON_SERVLET_PATH
//						+ 'service/esm-service/user',
//				reader : {
//					type : 'json',
//					root : 'resultList',
//					totalProperty : 'total'
//				},
//				writer : {
//					type : 'json'
//				}
//			}
			
			
			proxy : {
				type : 'ajax',
				url : JSON_SERVICE_SERVLET_PATH + 'esm-service/user.getUsersForOrganization',
				startParam : 'startRecord',
				limitParam : 'recordCount',
				reader : {
					type : 'json',
					root : 'data.resultList',
					totalProperty : 'data.total'
				},
				writer : {
					type : 'json'
				}
			}

});