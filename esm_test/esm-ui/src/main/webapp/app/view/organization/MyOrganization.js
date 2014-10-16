Ext.define('Security.view.organization.MyOrganization', {
    extend: 'Ext.container.Container',
    alias : 'widget.myorganization',
    border:0,
    minHeight:700,
    requires: [
        'Ext.layout.container.Border'

    ],

    title: 'Manage Organization',
    layout: 'border',
    style : 'background-color: #FFFFFF !important',
    defaults: {
        collapsible: false,
        split: false
    },
    margins:{top:15, right:0, bottom:0, left:0},


});