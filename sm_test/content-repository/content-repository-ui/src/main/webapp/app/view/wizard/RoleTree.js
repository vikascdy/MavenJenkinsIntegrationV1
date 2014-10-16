
// VIEW: Role Tree
// Displays the Roles available in the current Product in a tree view, allowing
// each Role to be expanded to show its ServiceTypes. The RoleTree is used to
// add and remove Roles to/from a Server, and provides checkboxes beside each
// Role on the tree.
// ----------------------------------------------------------------------------

Ext.define('SM.view.wizard.RoleTree', {
    extend: 'Ext.tree.Panel',
    alias : 'widget.roletree',

    title : 'Roles',
    server: null,
    rootVisible: false,
    preventHeader: true,
    autoScroll: true,

    initComponent: function() {
        this.store = Ext.create('SM.store.RoleTreeStore', {server: this.server}); 
        this.callParent(arguments);
        this.getStore().load({
            callback: function() {this.getRootNode().expand(false);},
            scope: this
        });
    }
});

