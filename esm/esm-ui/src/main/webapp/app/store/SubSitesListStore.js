Ext.define('Security.store.SubSitesListStore', {
    extend  :'Ext.data.Store',
    model   :'Security.model.SubSitesListModel',
    proxy: {
        type: 'ajax',
        url:'resources/json/SubSitesList.json'
    },
    autoLoad:false
});

