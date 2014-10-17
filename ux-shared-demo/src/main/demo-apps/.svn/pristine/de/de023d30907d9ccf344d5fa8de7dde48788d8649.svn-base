//Store with AJAX request for component tree list

Ext.define('PP.store.ComponentListStore', {
    extend  :'Ext.data.TreeStore',
    model   :'PP.model.ComponentListModel',
    autoLoad:true,
    autoSync:true,
    proxy   :{
        type  :'ajax',
        api   :{
            read:'json/ComponentList.json'
        },
        reader:{
            type:'json',
            id  :'id'
        }
    }
});

