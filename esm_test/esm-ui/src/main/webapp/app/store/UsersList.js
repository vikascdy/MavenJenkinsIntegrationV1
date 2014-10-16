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

Ext.require('Security.proxy.RemoteProxy');

Ext.define('Security.store.UsersList', {
    extend: 'Ext.data.Store',
    storeId: 'usersList',
    requires: 'Security.model.User',
    model: 'Security.model.User',
    remoteSort : true,
    pageSize: 20,
    autoLoad: false,
    remoteFilter: true,
    buffered:true,
    leadingBufferZone:500,
    // TODO: Delete this block and use the UserMananger Utility
    proxy: {
        type: 'remoteproxy',

        url: JSON_SERVICE_SERVLET_PATH + 'esm-service/user',
        reader: {
            type: 'json',
            root: 'data.resultList',
            totalProperty: 'data.total'
        },
        writer: {
            type: 'json'
        }
    }
});
