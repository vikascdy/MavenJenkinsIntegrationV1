Ext.onReady(function () {
    Ext.create('Ext.container.Viewport',
        {
            margin      :   20,
            renderTo    :   Ext.getBody(),
            items       :   [
                {
                    region            :    'north',
                    xtype             :    'MiniGrid',
                    tbarItems         :    [
                        {
                            iconCls    :    'add',
                            tooltip    :    'Add',
                            itemId     :    'addId'
                        },
                        {
                            iconCls    :    'edit',
                            tooltip    :    'Edit',
                            itemId     :    'editId'
                        },
                        {
                            iconCls :    'delete',
                            tooltip    :    'delete',
                            itemId     :    'deleteId'
                        }
                    ],
                    toolbarIcons        :    'add,delete,import',
                    url                 :    'json/MiniGrid.json',
                    rootValue           :    'Users',
                    headingText         :    'Includes Roles',
                    dockedToolbar       :    true,
                    totalVisibleRows    :    7,
                    hideHeaders         :    true,
                    headerIcon          :    false,
                    headerIconCls       :    'import'
                }
            ]
        });
});