Ext.define('Security.controller.TenantsController', {
    extend:'Ext.app.Controller',

    stores:[
        'TenantsList',
        'TenantAppsListStore',
        'TenantRoles',
        'TenantGroups',
        'TenantOrgs'
    ],
    models:[
        'Tenants',
        'PasswordPolicy',
        'TenantOrganizations',
        'TenantUtilization'
    ],
    views:[
        'tenant.ManageTenants',
        'tenant.CreateTenant',
        'tenant.TenantConfiguration',
        'tenant.TenantOverview',
        'tenant.ManageTenantOrganizations',
        'tenant.TenantActionMenu',
        'tenant.TenantDetailPane',
        'tenant.TenantDetailPaneHeader',
        'tenant.TenantDetailPaneTabPanel',
        'tenant.TenantDetailTabPanel',
        'tenant.TenantNotificationTabPanel',
        'tenant.ManageTenantRoles',
        'tenant.CreateTenantRole',
        'tenant.CreateTenantUserGroup',
        'tenant.TenantAppsList',
        'tenant.TenantAppBrowse',
        'tenant.TenantUtilization',
        'tenant.TenantUtilizationHeader',
        'tenant.SettingsConfig',
        'core.UploadWindow'
    ],

    refs:[
        {
            ref:'tenantDetailPane',
            selector:'tenantdetailpane'
        },
        {
            ref:'tenantDetailPaneHeader',
            selector:'tenantdetailpaneheader'
        },
        {
            ref:'tenantDetailTabPanel',
            selector:'tenantdetailtabpanel'
        },
        {
            ref:'tenantNotificationTabPanel',
            selector:'tenantnotificationtabpanel'
        },
        {

            ref:'manageSiteTenants',
            selector:'managesitetenants'
        },
        {

            ref:'manageTenants',
            selector:'managetenants'
        },
        {

            ref:'tenantConfiguration',
            selector:'tenantconfiguration'
        },
        {

            ref:'manageTenantOrganizations',
            selector:'managetenantorganizations'
        },
        {

            ref:'manageTenantRoles',
            selector:'managetenantroles'
        },
        {

            ref:'manageTenantUserGroups',
            selector:'managetenantusergroups'
        },
        {
            ref:'settingsconfig',
            selector:'settingsconfig'
        },
        {
            ref:'regXGrid',
            selector:'regxgrid'
        }
    ],

    init:function () {
        this.control({
            '#configTenantMenu':{
                'itemclick':this.handleTreeOptions
            },
            'managetenants #tenantGrid':{
                'newItem':this.showCreateTenantPage,
                'deleteItem':this.deleteTenantRecord,
                'importItem':this.openUploadTenantsWindow
            },
            'createtenant button[action="createTenant"]':{
                'click':this.createTenant
            },
            'managetenants':{
                statechange:this.onStateChange
            },
            'tenantconfiguration':{
                statechange:this.showTenantInfo
            },
            'tenantdetailpanetabpanel':{
                'tabchange':this.handleTabChange
            },
            'tenantoverview': {
                'showDefaultTenant' : this.showDefaultTenant
            },
            'managesitetenants #tenantGrid':{
                'selectionchange':this.showTenantSummary,
                'newItem':this.showCreateTenantPage,
                'deleteItem':this.deleteTenantRecord,
                'importItem':this.openUploadTenantsWindow
            },
            'managetenants #tenantGrid gridview':{
                'cellclick':this.showTenantDetails
            },
            'managesitetenants #tenantGrid gridview':{
                'cellclick':this.showTenantDetails
            },
            'createorganization button[action="createOrganization"]':{
                'click':this.createOrganizationForTenant
            },
            'managetenantorganizations #tenantOrganizationGrid':{
                'selectionchange':this.showRealmConfigurationForm,
                'newItem':this.showCreateOrganizationPage,
                'deleteItem':this.deleteOrganizationRecord,
                'importItem':this.openUploadOrganizationWindow
            },
            'managetenantroles #tenantRoleGrid':{
                'selectionchange':this.showRoleSummary,
                'newItem':this.showCreateTenantRolePage,
                'deleteItem':this.deleteTenantRoleRecord,
                'importItem':this.openUploadRolesWindow
            },
            'createtenantrole button[action="createRole"]':{
                'click':this.createRoleForTenant
            },
            'managetenantusergroups #tenantUserGroupGrid':{
                'selectionchange':this.showGroupSummary,
                'newItem':this.showCreateTenantUserGroupPage,
                'deleteItem':this.deleteTenantUserGroupRecord,
                'importItem':this.openUploadUserGroupsWindow
            },
            'createtenantusergroup button[action="createUserGroup"]':{
                'click':this.createUserGroupForTenant
            },
            'settingsconfig button[action="savePasswordPolicy"]':{
                'click':this.savePasswordPolicy
            },
            'settingsconfig button[action="saveTenantLogo"]':{
                'click':this.saveTenantLogo
            },
            'settingsconfig button[action="saveTenantLandingPage"]':{
                'click':this.saveTenantLandingPage
            },
            'settingsconfig' : {
                statechange:this.showTenantInfo,
                'logoloaded': function(reader, data, m){
                    var tenantConfiguration = this.getTenantConfiguration();
                    var tenantRecord = tenantConfiguration.getTenantRecord();
                    tenantRecord.set('Logo', {'currentLogo': tenantRecord.get('logo').currentLogo, 'newLogo':data});
                    //update the current logo display.  Prob a better way to do this
                    var currentDisp= m.previousSibling();
                    //currentDisp.setValue("<img src=\""+ data +"\" />");

                }
            }
        });
    },

    onStateChange:function (path) {
        if (path.length > 0) {
            var tenantId = path[0];
            if (Ext.isNumeric(tenantId)) {
                var selected = this.getTenantsList().getSelectionModel().getSelection();
                if (selected.length > 0 && selected[0].getId() == tenantId) {
                    return; //ignore - tenant is already selected
                }

                var tenantIndex = this.getTenantsListStore().findExact('id', Ext.Number.from(tenantId));
                if (tenantIndex != -1) {
                    this.selectTenantRecord(tenantIndex);
                    return;
                } else { //tenant is not yet loaded
                    this.loadTenantStore(tenantId);
                }
            } else {
                this.application.fireEvent('changeurlpath', []);
            }
        } else {
            this.loadTenantStore();
        }
    },

    handleTabChange:function (tabPanel, newCard) {

        var manageSiteTenants = this.getManageSiteTenants();

        var tenantDetailPaneHeader = this.getTenantDetailPaneHeader();
        if(tenantDetailPaneHeader && tenantDetailPaneHeader.getEl()){
            var detailHeaderHeight = tenantDetailPaneHeader.getEl().dom.scrollHeight;
    
            if (newCard.down('#heightReference')) {
                var newHeight = newCard.down('#heightReference').el.dom.offsetTop;
    
                if (manageSiteTenants) {
                    manageSiteTenants.setMinHeight(detailHeaderHeight + newHeight + 300);
                    manageSiteTenants.updateLayout();
                }
            }
        }

        Ext.getCmp('Home-page-container').updateLayout();

    },


    showTenantDetails:function (view, cell, cellIndex, record, row, rowIndex, e) {
        if (e) {
            var linkClicked = (e.target.tagName == 'A');
            if (linkClicked) {
                location.href = '#!/TenantConfig/' + record.get('id')+'/?site=#!/Site';
                e.stopEvent();
                return false;
            }
        }
    },

    showTenantSummary:function (grid, selected) {
        var me = this;
        if (selected.length > 0) {
            me.getManageSiteTenants().update(selected[0]);
        }
        else
             me.getManageSiteTenants().reset();

    },
    showTenantInfo:function (path) {
        var me = this;
        if (path.length > 0) {
            var tenantId = path[0];
            TenantManager.getTenantById(tenantId, function (tenantInfo) {
                if (tenantInfo) {
                    
                    var tenantRecord = Ext.create('Security.model.Tenants', tenantInfo);
                    var tenantConfiguration = me.getTenantConfiguration();
                    tenantConfiguration.setTenantId(tenantId);
                    tenantConfiguration.setTenantRecord(tenantRecord);
                    if(tenantConfiguration.down('tenantoverview'))
                        tenantConfiguration.down('tenantoverview').showTenantInfo(tenantRecord);
                }
            });
        }
    },


    currentRecord:function () {
        return this.getTenantInfo().currentRecord;
    },


    loadTenantStore:function (selectedTenantId) {
        Ext.log('loadTenantStore ' + selectedTenantId);
        this.getTenantsListStore().load({
            scope:this,
            callback:function (records, operation, success) {
                if (success !== false) {
                    Ext.log('Tenant store loaded');
                    var tenantIndex = 0;
                    if (selectedTenantId) {
                        tenantIndex = this.getTenantsListStore().findExact('id', Ext.Number.from(selectedTenantId));
                    }
                    this.selectTenantRecord(tenantIndex > 0 ? tenantIndex : 0);
                } else {
                    window.location = 'login';
                }
            }
        });

    },

    /**
     * Selects a Tenant instance by record instance or index.
     * @param {Security.model.Tenant/Number} record An Tenant instance or its index
     */
    selectTenantRecord:function (record) {
        var selectionModel = this.getTenantsList().getSelectionModel();
        if (!selectionModel.isSelected(record)) {
            selectionModel.select(record);
        }
    },

    
    showDefaultTenant: function(form) {
        TenantManager.getDefaultTenantRecord(function(record){
            form.loadRecord(record);
        });
    },


    handleTreeOptions:function (treeView, record, item, index) {

        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        if (record) {
            var tenantUrl = location.hash;
            var newUrl = "#!/TenantConfig/"+tenantRecord.get('id')+"/?site=#!/Site";
            
            treeView.getSelectionModel().select(record);            

            if (record.childNodes.length == 0) {
                var page = Ext.widget('tenantoverview');
                switch (record.internalId) {
                    case 'overview' :
                        newUrl="#!/TenantConfig/"+tenantRecord.get('id')+"/?site=#!/Site";
                        break;
                    case 'manageOrganizations' :
                        newUrl="#!/TenantConfig/"+tenantRecord.get('id')+"/ManageOrganization/?site=#!/Site";
                        break;
                    case 'manageRoles' :
                        newUrl="#!/TenantConfig/"+tenantRecord.get('id')+"/ManageRoles/?site=#!/Site";
                        break;
                    case 'manageGroups' :
                        newUrl="#!/TenantConfig/"+tenantRecord.get('id')+"/ManageGroups/?site=#!/Site";
                        break;
                    case 'settingsConfig' :
                        newUrl="#!/TenantConfig/"+tenantRecord.get('id')+"/Settings/?site=#!/Site";
                        break;
                    case 'apps' :
                        newUrl="#!/TenantConfig/"+tenantRecord.get('id')+"/Apps/?site=#!/Site";
                        break;
                    case 'utilization' :
                        page = Ext.widget('tenantutilizationheader');
                        break;
                    default :
                        page = Ext.widget('tenantoverview');

                }
                if(tenantUrl!=newUrl)
                    window.location = newUrl;
            }
        }
    },

    showCreateTenantPage:function (grid, page) {
        window.location='#!/Site/CreateTenant';
    },

    showCreateTenantRolePage:function (grid, page) {
        window.location='#!/Tenant/CreateRole?redirect='+location.hash;
    },

    showCreateOrganizationPage:function (grid, page) {
        window.location='#!/Tenant/CreateOrganization?redirect='+location.hash;
    },
    
    showCreateTenantUserGroupPage:function (grid, page) {       
        window.location='#!/Tenant/CreateGroup?redirect='+location.hash;
    },

    createTenant:function (btn) {
        var page=btn.up('createtenant');
        var form = btn.up('form');
        var formBasic = form.getForm();
        var siteList = form.down('#siteList');
        var siteValue = siteList.getValue();

        var siteRecord = Ext.StoreManager.lookup('SitesListStore').getById(siteValue);

        var tenantObj = formBasic.getValues();
        tenantObj['site'] = siteRecord.data;

        var tenantRecord = Ext.create('Security.model.Tenants', tenantObj);
        tenantRecord.data['passwordPolicy'] = {};
        
        TenantManager.createTenant(tenantRecord.data,function(response){

            if(response)
                Functions.errorMsg("Tenant created successfully", 'Success', null, 'INFO');
            else
                Functions.errorMsg('Failed to create tenant.',"Failure",null,'Warn');
        });

        formBasic.reset();
        siteList.setValue(siteValue);
    },


    createOrganizationForTenant:function (btn) {
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        var form = btn.up('form');
        var formBasic = form.getForm();

        var organizationObj = formBasic.getValues();

        OrganizationManager.createOrganizationForTenant(tenantRecord.data, organizationObj, function (response) {
                if (response) {
                    Functions.errorMsg("Organization successfully added to tenant.", 'Success', null, 'INFO');
                    formBasic.reset();
                }
        }, this);

    },

    createRoleForTenant:function (btn) {
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        var form = btn.up('form');
        var formBasic = form.getForm();

        var roleObj = formBasic.getValues();

            TenantManager.createRoleForTenant(tenantRecord.data, roleObj, function (response) {
                if (response) {
                    Functions.errorMsg("Role successfully added to tenant.", 'Success', null, 'INFO');
                    formBasic.reset();
                }
            });
    },


    deleteTenantRoleRecord:function (grid) {
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        var flag=1;        
        var selections = grid.getSelectionModel().getSelection();
        
        Ext.each(selections,function(record){
            if(record.get('canonicalName')=='System Role' || record.get('canonicalName')=='Admin Role' )
                {
                flag=0;
                return false;
                }
        });
        
        if(flag==0)
            Functions.errorMsg('Cannot delete default role "System Role" or "Admin Role"', 'Invalid operation ', null, 'WARN');
        else
        if (selections.length > 0) {
            Ext.MessageBox.show({
                title:'Delete Tenant Role?',
                msg:'Are you sure you want to delete selected roles ?',
                buttons:Ext.MessageBox.YESNO,
                fn:function (btn) {
                    if (btn == 'yes') {
                        grid.down('#deleteItem').setDisabled(true);

                        RoleManager.deleteRole([selections[0].get('id')], function () {
                            grid.getStore().load({
                                callback:function () {
                                    var record =
                                        grid.getStore().getAt(0);
                                    if (record) {
                                        grid.getSelectionModel().select(record);
                                        grid.updateLayout();
                                    }
                                }
                            });

                        }, this);


                    }
                }
            });
            
        }
        else
            Functions.errorMsg("Select a role to delete.", 'Error', null, 'ERROR');
    },


    deleteTenantRecord:function (grid) {
        var flag=1;
        var selections = grid.getSelectionModel().getSelection();
        
        Ext.each(selections,function(record){
            if(record.get('canonicalName')=='_System')
                {
                flag=0;
                return false;
                }
        });     
    
        if(flag==0)
            Functions.errorMsg('Cannot delete default tenant "_System"', 'Invalid operation ', null, 'WARN');
        else
        if (selections.length > 0) {
        
            Ext.MessageBox.show({
                title:'Delete Tenant?',
                msg:'Are you sure you want to delete selected tenants ?',
                buttons:Ext.MessageBox.YESNO,
                fn:function (btn) {
                    if (btn == 'yes') {
                        TenantManager.deleteTenantFromSite([selections[0].get('id')], function () {
                            grid.getStore().load({
                                callback:function () {
                                    grid.down('#deleteItem').setDisabled(true);
                                    var record =
                                        grid.getStore().getAt(0);
                                    if (record) {
                                        grid.getSelectionModel().select(record);
                                        grid.updateLayout();
                                    }
                                }
                            });
                        });
                    }
                }
            });
        }
        else
            Functions.errorMsg("Select a tenant to delete.", 'Error', null, 'ERROR');
    },

    openUploadTenantsWindow:function(grid){
        var me = this;
            Ext.widget({    
            xtype :'uploadwindow',
            heading:'Import tenants',
            targetUrl:'tenant.importTenantFromJson',           
            grid :grid
        }).show();       

    },

    openUploadRolesWindow: function(grid){
        var me = this;
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        Ext.widget({    
            xtype :'uploadwindow',
            heading:'Import Roles',
            targetUrl:'role.importRoleFromJson',
            paramName:'tenantId',
            paramValue:tenantRecord.get('id'),
             grid :grid
        }).show();       

    },
    
    openUploadUserGroupsWindow: function(grid){
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        Ext.widget({    
            xtype :'uploadwindow',
            heading:'Import Tenant User Group',
            targetUrl:'group.importGroupsFromJson',
            paramName:'tenantId',
            paramValue:tenantRecord.get('id'),
             grid :grid
        }).show();
    },
    
    openUploadOrganizationWindow: function(grid){
    var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        Ext.widget({    
            xtype :'uploadwindow',
            heading:'Import Organizations',
            targetUrl:'organization.importOrganizationFromJson',
            paramName:'tenantId',
            paramValue:tenantRecord.get('id'),
            grid :grid
        }).show();
    },
    


    deleteOrganizationRecord:function (grid) {
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        var flag=1;
        var selections = grid.getSelectionModel().getSelection();
        
        Ext.each(selections,function(record){
            if(record.get('canonicalName')=='edfx')
                {
                flag=0;
                return false;
                }
        });         
       
        if(flag==0)
            Functions.errorMsg('Cannot delete default organization "edfx"', 'Invalid operation ', null, 'WARN');
        else
        if (selections.length > 0) {
         Ext.MessageBox.show({
                title:'Delete Organization?',
                msg:'Are you sure you want to delete selected organizations ?',
                buttons:Ext.MessageBox.YESNO,
                
                fn:function (btn) {
                    if (btn == 'yes') {
                        grid.down('#deleteItem').setDisabled(true);

                        OrganizationManager.deleteOrganizationRecord([selections[0].get('id')], function () {
                            grid.getStore().load({
                                callback:function (records, operation, success) {
                                if(records.length == 0 && operation.page > 1)
                                {
                                var record =
                                        grid.getStore().loadPage((operation.page) - 1)
                                }
                                    else
                                    {
                                    var record =
                                        grid.getStore().getAt(0);
                                    if (record) {
                                        grid.getSelectionModel().select(record);
                                        grid.updateLayout();
                                    }
                                }
                                }
                            });

                        }, this);


                    }
                }
            });
        }
        else
            Functions.errorMsg("Select an organization to delete.", 'Error', null, 'ERROR');
    },

    showRealmConfigurationForm:function (selectionModel, selected) {
        var me = this;
        if (selected.length > 0) 
            me.getManageTenantOrganizations().update(selected[0]);
        else
         me.getManageTenantOrganizations().reset();
    },

    showRoleSummary:function (selectionModel, selected) {
        var me = this;
        if (selected.length > 0) 
            me.getManageTenantRoles().update(selected[0]);
        else
             me.getManageTenantRoles().reset();
    },

    showGroupSummary:function (selectionModel, selected) {
        var me = this;
        if (selected.length > 0) 
            me.getManageTenantUserGroups().update(selected[0]);
        else
            me.getManageTenantUserGroups().reset();
    },
    
    createUserGroupForTenant:function (btn) {
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        var form = btn.up('form');
        var formBasic = form.getForm();

        var userGroupObj = formBasic.getValues();

            TenantManager.createGroupForTenant(tenantRecord.data, userGroupObj, function (response) {
                if (response) {
                    Functions.errorMsg("User Group successfully added to tenant.", 'Success', null, 'INFO');
                    formBasic.reset();
                }
            });
    },

    deleteTenantUserGroupRecord:function (grid) {
        var tenantConfiguration = this.getTenantConfiguration();
        var tenantRecord = tenantConfiguration.getTenantRecord();
        
        var selections = grid.getSelectionModel().getSelection();
        
        if (selections.length > 0) {
            Ext.MessageBox.show({
                title:'Delete Tenant User Group?',
                msg:'Are you sure you want to delete selected user groups?',
                buttons:Ext.MessageBox.YESNO,
                fn:function (btn) {
                    if (btn == 'yes') {
                        grid.down('#deleteItem').setDisabled(true);

                        GroupManager.deleteGroup([selections[0].get('id')], function () {
                            grid.getStore().load({
                                callback:function () {
                                    var record =
                                        grid.getStore().getAt(0);
                                    if (record) {
                                        grid.getSelectionModel().select(record);
                                        grid.updateLayout();
                                    }
                                }
                            });

                        }, this);


                    }
                }
            });
        }
        else
            Functions.errorMsg("Select a User Group to delete.", 'Error', null, 'ERROR');
    },

    updateTenantConfigRecord : function(tenantId){
        var tenantConfiguration = this.getTenantConfiguration();
        TenantManager.getTenantById(tenantId,function(tenantObj){
            var tenantRecord = Ext.create('Security.model.Tenants',tenantObj);
            tenantConfiguration.setTenantRecord(tenantRecord);
        });
    },

    savePasswordPolicy : function(btn){
        var me=this;
        var form = btn.up('form').getForm();
        if (form.isValid()) {
            var record= form.getRecord();
            var values = form.getValues();
            record.set(values);
            var tenantId = btn.up('settingsconfig').tenantId;
//            if(values.isValid=="true"){

            TenantManager.updateTenantPasswordPolicy(tenantId, record.data, function(tenant){
                if(tenant){
                    Functions.errorMsg("Password policy updated successfully.",'Success',null,'INFO');
                    me.updateTenantConfigRecord(tenantId);
                }
                else
                    Functions.errorMsg("Password policy update failed.",'Failure',null,'ERROR');
            });
//            }
//            else
//                Functions.errorMsg("Selected regular expression is not validated for a test password. Please test before proceeding.",'Warning',null,'WARN');
        }
        else
            Functions.errorMsg("Invalid Configuration / Regular Expression selection",'Warning',null,'ERROR');
    },


    saveTenantLogo : function(btn){
        var me=this;
        var formComp=btn.up('form');
        var form = formComp.getForm();


        var tenantId = btn.up('settingsconfig').tenantId;
        if (form.isValid()) {

            var tenantConfiguration = this.getTenantConfiguration();
            var tenantRecord = tenantConfiguration.getTenantRecord();
            var bytes=tenantRecord.get('Logo').newLogo;
            TenantManager.updateTenantLogo(tenantId, bytes, function(tenant, data){
                if(tenant){
                    Functions.errorMsg("Tenant logo updated successfully.",'Success',null,'INFO');
                    //since formComp isn't in scope.  Probably a better way...
                    var fc=form.getBoundItems('button').items[0].up('form');
                    var tenantId=fc.up('settingsconfig').tenantId;
                    var newLogo=TenantManager.getTenantLogo(tenantId, function(result){
                       // var im=new Image();
                       // im.url=result;
                        var logoView=form.getFields('displayfield').items[0];
                        logoView.setValue("<img src=\""+ result +"\" />");

                    });

                   // me.updateTenantConfigRecord(tenantId);
                }
                else
                    Functions.errorMsg("Uploading logo failed.",'Failure',null,'ERROR');
            });
        }
        else
            Functions.errorMsg("Error uploading logo",'Warning',null,'ERROR');
    },

    saveTenantLandingPage : function(btn){
        var me=this;
        var formComp=btn.up('form');
        var form = formComp.getForm();


        var tenantId = btn.up('settingsconfig').tenantId;
        if (form.isValid()) {

            var values = form.getValues();
            var landingPage=values.landingPage;
            //record.set(values);
            TenantManager.updateTenantLandingPage(tenantId, landingPage, function(res){
                if(res){
                    Functions.errorMsg("Landing page updated successfully.",'Success',null,'INFO');


                }
                else
                    Functions.errorMsg("Updating landing page failed.",'Failure',null,'ERROR');
            });
        }
        else
            Functions.errorMsg("Error updating landing page",'Warning',null,'ERROR');
    }
});
