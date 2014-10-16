Ext.define('DD.view.query.QueryBuilderGrid', {
    extend:'Ext.grid.Panel',
    alias:'widget.querybuildergrid',
    sortableColumns:false,
    initComponent : function() {
        var me = this;
        this.store = Ext.create('Ext.data.Store', {
            fields:['Action', 'Output', 'Expression', 'Aggregate', 'Alias', 'SortType', 'SortOrder', 'Grouping', 'Criteria'],
            data:[]
        });

        this.columns = [
            { text: 'Action',  dataIndex: 'Action', flex: 1,menuDisabled:true  },
            { text: 'Output', dataIndex: 'Output', flex: 1,menuDisabled:true },
            { text: 'Expression', dataIndex: 'Expression', flex: 1,menuDisabled:true  },
            { text: 'Aggregate', dataIndex: 'Aggregate', flex: 1,menuDisabled:true  },
            { text: 'Alias', dataIndex: 'Alias', flex: 1,menuDisabled:true  },
            { text: 'Sort Type', dataIndex: 'SortType', flex: 1,menuDisabled:true  },
            { text: 'Sort Order', dataIndex: 'SortOrder', flex: 1,menuDisabled:true  },
            { text: 'Grouping', dataIndex: 'Grouping', flex: 1,menuDisabled:true  },
            { text: 'Criteria', dataIndex: 'Criteria', flex: 1,menuDisabled:true  }
        ];


        this.callParent(arguments);
    }
});