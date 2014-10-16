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

// CONFIGMANAGER.JS
// Handles loading and saving configuration files.
// ----------------------------------------------------------------------------

Ext.define('Util.ConfigManager', {}); // Placeholder, ignore this.

window.ConfigManager = {
	config : null,
	usingDefaultConfig : false,
	validationErrors : [],
	serviceTypesRequired : null,
	product : null,
	totalConfigCount : 0,
    wroteConfig: false, //only true if config was written this session.

	checkIfClusterNameRequired : function(callback, scope) {

		Ext.Ajax.request({
			url : 'setup/clusterConfigurationRequired',
			success : function(response) {
				var respJson = Ext.decode(response.responseText);
				if (respJson.success) {
					Ext.callback(callback, scope, [ respJson.required ]);
				} else {
					Functions.errorMsg(respJson.error, "Connection Failed");
				}
			},
			failure : function(response) {
				Functions.errorMsg("Could not connect to backend.",
						"Connection Failed");
			}
		});
	},

	checkIfConnected : function(callback, scope) {



		Ext.Ajax.request({
			url : 'setup/connect',
			success : function(response) {
				var respJson = Ext.decode(response.responseText);
				if (respJson.success || this.wroteConfig) {
					Rest.environmentName=respJson.environment;
					//Ext.callback(callback, scope, [ respJson.connected ]);
                    Ext.callback(callback, scope, [ true ]);
				} else {
					Rest.environmentName='Not Available';
					Ext.callback(callback, scope, false);
				}
			},
			failure : function(response) {

				Rest.environmentName='Not Available';
				Ext.callback(callback, scope, false);
			}
		});
	}

};
