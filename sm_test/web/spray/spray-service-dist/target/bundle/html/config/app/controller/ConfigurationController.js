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

// CONTROLLER: Core Controller
// Manages loading configuration files and displaying the Configuration 
// Overview Tree.
// ----------------------------------------------------------------------------

Ext.define('Rest.controller.ConfigurationController', {
    extend : 'Ext.app.Controller',

    views : [
        'configuration.NoConnectionPage',
        'configuration.ClusterNamePage',
        'configuration.ConnectedPage'
    ],

    init : function() {
        var me = this;
        this.control({
            'clusternamepage textfield' : {
                specialkey : function(obj, event) {
                    if (event.getKey() == event.ENTER && obj.up('form').getForm().isValid()) {
                        me.setCluster(obj);
                    }
                }
            },
            'clusternamepage #nextButton' : {
                click : function(btn) {
                    me.setCluster(btn);
                }
            },
            'noconnectionpage #proceed' : {
                click : function(btn) {
                    Rest.setPage(Ext.create('Rest.view.configuration.ClusterNamePage'));
                }
            }
        });
    },

    setCluster : function(btn) {
        var environmentName = Ext.getCmp("environmentName").getValue();
        var clusterName = Ext.getCmp("clusterName").getValue()
        var connectionType = Ext.getCmp("connection_type").getValue();
        var listeningPort = Ext.getCmp("listeningPort").getValue();
        var hostId = Ext.getCmp("host_id").getValue();
        this.sendUIConfig(clusterName, environmentName, connectionType, listeningPort, hostId, function(response) {
            Rest.setPage(Ext.create('Rest.view.configuration.ConnectedPage'));
        });
    },

    sendUIConfig : function(clusterName, environmentName, connectionType, listeningPort, hostId, callback) {
        Ext.Ajax.request({
            url : 'setup/cluster',
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            params : Ext.encode({
                    clusterName: clusterName,
                    environmentName: environmentName,
                    connectionType: connectionType,
                    listeningPort: listeningPort,
                    hostId: hostId
            }),
            success : function(response) {
                window.ConfigManager.wroteConfig=true;
                var respJson = Ext.decode(response.responseText);
                if (respJson.connected != 'false') {
                    callback(respJson.success);
                }
            },
            failure : function(response) {
                Functions.errorMsg(respJson.error, "UI changes could not be written to the file");
            }
        });
    }
});
