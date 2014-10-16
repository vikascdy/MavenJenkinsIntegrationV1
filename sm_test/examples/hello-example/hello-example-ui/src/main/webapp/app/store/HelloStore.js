Ext.define('HelloExample.store.HelloStore', {
    extend  : 'Ext.data.Store',

    model   : 'HelloExample.model.HelloModel',
    
    autoLoad: true,
    
    proxy: {
        type: 'ajax',
        url : '/rest/service/hello-example-service/hello.greeting',
        actionMethods: {
            read:'GET'
        },
        reader: {
            type: 'json',
            root: 'data'
        }
    }
});
