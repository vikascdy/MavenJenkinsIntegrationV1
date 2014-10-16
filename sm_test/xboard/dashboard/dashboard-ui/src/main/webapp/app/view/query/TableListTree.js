Ext.define('DD.view.query.TableListTree', {
    extend:'Ext.tree.Panel',
    alias:'widget.tablelisttree',
    rootVisible:true,
    initComponent : function() {
        var me = this;

        this.store = Ext.create('Ext.data.TreeStore', {
            root: {
                expanded: true,
                text:'Tables',
                children: [
                    { text: "Products", leaf: true },
                    { text: "Employees", leaf: true },
                    { text: "Offices", leaf: true },
                    { text: "Payments", leaf: true },
                    { text: "Orders", leaf: true },
                    { text: "Sales", leaf: true },
                    { text: "Departments", leaf: true }
                ]
            }
        });

        this.callParent(arguments);
    }
});