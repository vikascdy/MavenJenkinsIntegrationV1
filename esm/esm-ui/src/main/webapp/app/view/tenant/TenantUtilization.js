Ext.define('Security.view.tenant.TenantUtilization', {
    extend:'Ext.tree.Panel',
    alias:'widget.tenantutilization',
    useArrows:true,
    rootVisible:false,
    multiSelect:true,
    singleExpand:true,
    cls:'no-icon-tree',
    overflowX:'hidden',
    initComponent:function () {
        var me = this;
        Ext.apply(this, {
            store:new Ext.data.TreeStore({
                model:'Security.model.TenantUtilization',
                proxy:{
                    type:'ajax',
                    url:'resources/json/tenant-utilization.json'
                },
                folderSort:true
            }),
            head: ['->', {xtype:'component', html:'<h1>Tenant Utilization</h1>'}],
            tbar:['->', {xtype:'button', text:'Edit', iconCls:'edit'}],
            columns:[
                {
                    xtype:'treecolumn',
                    menuDisabled:true,
                    text:'Name',
                    flex:1,
                    sortable:true,
                    dataIndex:'name',
                    renderer:function (v, m, r) {
                        if (r.get('valueUnit').length > 0)
                            return v + '<b> (' + r.get('currentValue') + r.get('valueUnit') + ' / ' + r.get('totalValue') + r.get('valueUnit') + ')</b>';
                        else
                            return v;
                    }
                },
                {
                    text:'Value',
                    menuDisabled:true,
                    flex:2,
                    sortable:true,
                    dataIndex:'currentValue',
                    renderer:function (v, m, r) {

                        if (r.get('currentValue') && r.get('totalValue')) {
                            var id = Ext.id();
                            var widget = null;
                            Ext.defer(function () {
                                if (r.get('valueUnit') == 'TB' || r.get('valueUnit') == 'M')
                                   // widget = me.getGaugeChart(r, id);
                                    widget = me.getProgressBar(r, id);
                                else
                                    widget = me.getProgressBar(r, id);

                                Ext.widget(widget);
                            }, 100);
                            return Ext.String.format('<div id="{0}"></div>', id);
                        }
                        else
                            return v;
                    }
                },
            ]
        });
        this.callParent();
    },

    getProgressBar:function (r, id) {
        return {
            xtype:'progressbar',
            height:30,
            renderTo:id,
            value:r.get('currentValue') / r.get('totalValue'),
            width:350,
            text:'<span class="utilizationValues">' + r.get('currentValue') + r.get('valueUnit') + ' / ' + r.get('totalValue') + r.get('valueUnit') + '</span>'
        };
    },

    getGaugeChart:function (r, id) {

        var store = Ext.create('Ext.data.JsonStore', {
            fields:['value'],
            data:[
                { 'value':r.get('currentValue') }
            ]
        });

        var series = [
            {
                type:'gauge',
                field:'value',
                colorSet:['#82B525', '#ddd']
            }
        ];
        console.log(r.get('currentValue'), r.get('totalValue') / 2 + r.get('totalValue') / 4);


        if (r.get('currentValue') >= r.get('totalValue') / 2) {
            series = [
                {
                    type:'gauge',
                    field:'value',
                    colorSet:['#F49D10', '#ddd']
                }
            ];


            if (r.get('currentValue') >= (r.get('totalValue') / 2 + r.get('totalValue') / 4)) {
                series = [
                    {
                        type:'gauge',
                        field:'value',
                        colorSet:['#950F1D', '#ddd']
                    }
                ];
            }

        }


        return {
            xtype:'chart',
            width:300,
            height:150,
            store:store,
            animate:true,
            insetPadding:10,
            animate:{
                easing:'elasticIn',
                duration:1000
            },
            renderTo:id,
            axes:[
                {
                    type:'gauge',
                    position:'gauge',
                    minimum:0,
                    maximum:r.get('totalValue'),

                    margin:-10
                }
            ],
            series:series

        };
    }
});