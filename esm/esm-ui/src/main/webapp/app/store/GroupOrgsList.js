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

Ext.define('Security.store.GroupOrgsList', {
    extend: 'Ext.data.Store',
    storeId: 'groupOrgsList',
    requires: 'Security.model.Organizations',
    model: 'Security.model.Organizations',
    pageSize: 20,
    autoLoad: false,
    
    proxy: {
        type: 'remoteproxy',
        startParam:'startRecord',
        limitParam:'recordCount',

        url: JSON_SERVICE_SERVLET_PATH + 'esm-service/organization.getOrganizationsForGroup',
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
