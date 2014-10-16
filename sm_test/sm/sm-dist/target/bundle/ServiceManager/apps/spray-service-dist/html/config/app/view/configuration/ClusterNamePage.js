
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

// VIEW: Cluster Name Page
// Prompts the user to provide the name of the network cluster to connect this
// instance of the UI to. Should only be displayed on the first run of the UI.
// ----------------------------------------------------------------------------

Ext.define('Rest.view.configuration.ClusterNamePage', {
    extend : 'Ext.container.Container',
     alias : 'widget.clusternamepage',

     header: 'Enter Network Cluster Name',
     margin: '30 0 0 0',

     items : [{
          xtype : 'form',
          itemId: 'clusterNameForm',
          layout: 'anchor',
          defaults: {
                anchor: '50%',
                margin: 32
          },
          items : [{
                xtype : 'component',
                plain : true,
                html  : '<p>' +
                                "Enter the name of the network cluster to connect to." +
                          '</p><br /><p>' +
                                "This should be the same cluster name that you entered when " +
                                "configuring the Edifecs&reg; Agent software. You will only " +
                                "need to provide this information once." +
                          '</p>'
          },
          {
              xtype      : 'combobox',
              fieldLabel : 'Connection Type',
              store      : {
        	  fields : ['text','value'], 
                  data   : [ 
                            {text: 'TCP',value:'TCP'},
                            {text: 'UDP',value:'UDP'}
                  ] 
              } , 
              displayField:     'text',
              valueField    :     'value',
              value        :     'TCP',
              id          : 'connection_type',
              listeners: {
                  change: function(text, value) {
                      if(value=="TCP")
                          {
                              Ext.getCmp("host_id").show();
                              Ext.getCmp("listeningPort").setValue('7800');
                          }
                      else{
                              Ext.getCmp("host_id").hide();
                              Ext.getCmp("listeningPort").setValue('45588');
                          }

                     }
                }
              //readOnly    :    true
          },{
                xtype      : 'textfield',
                fieldLabel: 'Cluster Name',
                name        : 'clusterName',
                id          : 'clusterName',
                allowBlank: false
          },
          {
              xtype        :    'combobox',
              fieldLabel    :     'Environment',
              name        : 'environmentName',
              id        : 'environmentName',
              store        :     {
                  fields    :     ['text','value'], 
                    data    :     [ 
                         {text: 'Development',value:'Development'}, 
                         {text: 'Test',value:'Test'},
                         {text: 'Preproduction',value:'Preproduction'},
                         {text: 'Production',value:'Production'}
                     ] 
              } , 
              displayField:     'text',
              valueField    :     'value',
              value        :     'Development',
              readOnly:false
          },
          {
                xtype      : 'textfield',
                fieldLabel: 'Host',
                name        : 'host',
                hidden     : false,
                id          : 'host_id',
                allowBlank: true
          },
          
          {
                xtype      : 'textfield',
                fieldLabel: 'Listening Port',
                name        : 'listeningPort',
                id          : 'listeningPort',
                value      : 7800,
                vtype      : 'Port',
                allowBlank: false
          },{
                xtype : 'container',
                layout: 'hbox',
                items:[{
                     xtype: 'component',
                     flex : 1
                }, {
                     xtype     : 'button',
                     itemId    : 'nextButton',
                     text      : 'Next',
                     iconCls  : 'next-button-icon',
                     iconAlign: 'right',
                     formBind : true,
                     width     : 80
                }]
          }]
     }]
});
Ext.apply(Ext.form.field.VTypes, {
     IPAddress:  function(v) {
          return /^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$/.test(v);
     },
     IPAddressText: 'Must be a numeric IP address',
     IPAddressMask: /[\d\.]/i
});
Ext.apply(Ext.form.field.VTypes, {
     Port:  function(v) {
         return /^((6553[0-5]|655[0-2]\d|65[0-4]\d\d|6[0-4]\d{3}|[1-5]\d{4}|[1-9]\d{0,3}|0))$/.test(v);
          
     },
     PortText: 'Must be a valid port number',
     PortMask: /[\d\.]/i
});


