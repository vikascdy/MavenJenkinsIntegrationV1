
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

// VIEW: No Connection Page
// Shown as an error message when no connection to the backend is available.
// ----------------------------------------------------------------------------

Ext.define('Rest.view.configuration.NoConnectionPage', {
	extend : 'Ext.container.Container',
    alias : 'widget.noconnectionpage',

    header: 'No Connection Available',
    margin: '30 0 0 0',

    items : [{
        xtype : 'container',
        plain : true,
        layout: 'anchor',
        defaults: {
            anchor: '50%',
            margin: 32
        },
        items : [{
            xtype : 'component',
            plain : true,
            html  : '<p>' +
                        "A connection to the backend cannot be established." +
                    '</p><br /><p>' +
                        "Ensure that the cluster that this system manages is" +
                        " actively running. If this error persists, contact your system" +
                        " administrator." +
                    '</p><br /><p>' +
                        "If you want to attempt to access the Service Manager interface" +
                        " anyway, click the Proceed Anyway button below, keeping in mind" +
                        " that nothing will work until this error is resolved." +
                    '</p>'
        }, {
            xtype : 'container',
            plain : true,
            layout: 'hbox',
            items :[{
                xtype: 'component',
                flex : 1
            }, {
                xtype    : 'button',
                itemId   : 'proceed',
                text     : 'Proceed Anyway',
                iconCls  : 'ico-warning',
                width    : 140
            }]
        }]
    }]
});

