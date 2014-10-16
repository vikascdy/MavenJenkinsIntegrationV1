Ext.define('Util.ChartManager', {});

window.ChartManager = {

    getSampleConfigurations : function(flag) {
        if (flag == 0)
            return null;
        var storeConfig = {
            autoLoad:true,
            fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
            proxy:'memory',
            groupField:null,
            name:'Sample Store'
        };
        var storeData = [
            { 'name': 'metric one',   'data1':10, 'data2':12, 'data3':14, 'data4':8,  'data5':13 },
            { 'name': 'metric two',   'data1':7,  'data2':8,  'data3':16, 'data4':10, 'data5':3  },
            { 'name': 'metric three', 'data1':5,  'data2':2,  'data3':14, 'data4':12, 'data5':7  },
            { 'name': 'metric four',  'data1':2,  'data2':14, 'data3':6,  'data4':1,  'data5':23 },
            { 'name': 'metric five',  'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33 }
        ];
        if (flag != 4)
            return {
                XfieldLabel:flag == 1 ? 'Sample Values' : 'Sample Metrics',
                YfieldLabel:flag == 1 ? 'Sample Metrics' : 'Sample Values',
                Xfields: flag == 1 ? ['data1'] : ['name'],
                Yfields:flag == 1 ? ['name'] : (flag == 2 ? ['data1'] : ['data1','data2','data3','data4','data5']),
                minimum:0,
                maximum:null,
                legend:false,
                legendPosition:'bottom',
                storeConfig:storeConfig,
                storeData:storeData,
                theme:'Base',
                donut:0
            };
        else
            return{
                columns :  ['name','data1','data2','data3','data4','data5'],
                title:'Sample Grid',
                collapsible:false,
                hideHeaders:false,
                autoScroll:false,
                border:true,
                groupData:false,
                groupingField:null,
                storeConfig:storeConfig,
                storeData:storeData
            };
    },

    createStoreInstanceUsingConfig : function(storeConfig, storeData, groupField) {

        return Ext.create('Ext.data.Store', {
            autoLoad:true,
            fields:storeConfig.fields,
            proxy:{type:'memory'},
            data:storeData.length > 0 ? storeData : [],
            groupField:groupField
        });


    },
    getComponentForId : function(id, type, useStaticConfig, configObj, dataSetId, storeInstance,storeFields, callback) {
        var xtype = null;
        var dataIndex = 0;

        switch (id.toLowerCase()) {
            case 'barchart':
                xtype = 'barchart';
                dataIndex = 1;
                break;
            case 'columnchart':
                xtype = 'columnchart';
                dataIndex = 2;
                break;
            case 'areachart':
                xtype = 'areachart';
                dataIndex = 3;
                break;
            case 'linechart':
                xtype = 'linechart';
                dataIndex = 3;
                break;
            case 'piechart':
                xtype = 'piechart';
                dataIndex = 1;
                break;
            case 'scatterchart':
                xtype = 'scatterchart';
                dataIndex = 3;
                break;
            case 'radarchart':
                xtype = 'radarchart';
                dataIndex = 3;
                break;
            case  'image':
                xtype = 'imagepanel';
                dataIndex = 0;
                break;
            case  'text':
                xtype = 'textpanel';
                dataIndex = 0;
                break;
            case  'datagrid':
                xtype = 'datagrid';
                dataIndex = 4;
                break;
            default :
                xtype = 'panel';
                dataIndex = 0;
                break;
        }


        Ext.callback(callback, this, [
            Ext.widget({
                flex:1,
                xtype:xtype,
                componentType:type,
                configObj:useStaticConfig ? ChartManager.getSampleConfigurations(dataIndex) : configObj,
                useSampleStore:useStaticConfig,
                storeInstance:storeInstance,
                dataSetId:dataSetId,
                storeFields:storeFields
            })
        ]
        );
    }

};

