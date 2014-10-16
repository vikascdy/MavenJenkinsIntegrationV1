Ext.define('DD.view.parameters.CheckBoxGroupControl', {
    extend:'Ext.form.CheckboxGroup',
    alias :'widget.checkboxgroupcontrol',
    columns: 2,
    vertical: true,
    config:{
        parameterField:null,
        storeRef:null
    },
    items:[{
        boxLabel:'Test'
    }],
    initComponent : function() {

        this.listeners = {
            'change' : function(combo, newValue) {
                console.log(newValue);
                var store = this.getStoreRef();
                if (store) {
                    store.clearFilter(true);
                    store.filterBy(function(item) {
                            if (combo.getParameterField())
                                return item.get(combo.getParameterField()) != newValue;
                        });
                    combo.setValue(newValue);
                }
            }
        };

        this.callParent(arguments);
    },
    updateConfiguration : function(configObj, store, callback) {

//        this.setFieldLabel(configObj.name);
//        this.setStoreRef(store);
//
//        var itemsList = [];
//        Ext.each(store.getRange(), function(rec) {
//            itemsList.push({
//                boxLabel:rec.get(configObj.parameter),
//                inputValue:rec.get(configObj.parameter),
//                name:configObj.parameter,
//                checked:true
//            });
//        });
//        this.removeAll();
//        this.add(itemsList);
//
//        this.updateLayout();

        Ext.callback(callback, this);
    }
});
