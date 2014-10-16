Ext.define('Util.DataSourceManager', {});

window.DataSourceManager = {

    getDatasourceTypeById : function(dataSourceTypeId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'getDatasourceTypeById',
            method:'POST',
            params:{
                data : '{"datasourceTypeId":' + dataSourceTypeId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.callback(callback, this, [respJson]);
                }
                else
                    Ext.Msg.alert('Operation Failed', respJson.error);
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                Ext.Msg.alert('Operation Failed', respJson.error);
            }
        });

    },

    createDatasource : function(dataSourceTypeId, dataSourceProperties, callback) {


        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'createDatasource',
            method:'POST',
            params:{
                data : '{"datasourceTypeId":' + dataSourceTypeId + ',"datasourceProperties":' + Ext.encode(dataSourceProperties) + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Data source created successfully');
                    Ext.callback(callback, this, [respJson]);
                }
                else {
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', respJson.error);
                    });
                }
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                DD.removeLoadingWindow(function() {
                    Ext.Msg.alert('Operation Failed', respJson.error);
                });
            }
        });

    },

    removeDatasource : function(datasourceId, callback) {

        Ext.Ajax.request({
            url:JSON_SERVLET_PATH + 'removeDatasource',
            method:'POST',
            params:{
                data : '{"datasourceId":' + datasourceId + '}'
            },
            success : function(response) {
                var respJson = Ext.decode(response.responseText);
                if (respJson.success != false) {
                    Ext.Msg.alert('Operation Success', 'Data source deleted successfully');
                    Ext.callback(callback, this, [respJson]);
                }
                else {
                    DD.removeLoadingWindow(function() {
                        Ext.Msg.alert('Operation Failed', respJson.error);
                    });
                }
            },
            failure : function(response) {
                var respJson = Ext.decode(response.responseText);
                DD.removeLoadingWindow(function() {
                    Ext.Msg.alert('Operation Failed', respJson.error);
                });
            }
        });

    },


    generateDataSourceConfigForm : function(dataSourceType, callback) {

        var configItems = [];
        var properties = dataSourceType.properties;

        Ext.each(properties, function(prop) {
            configItems.push({
                xtype:'textfield',
                value:prop.name == 'Category' ? dataSourceType.category : null,
                inputType:prop.name == 'PassWord' ? 'password' : 'text',
                name:prop.name,
                fieldLabel:prop.name,
                allowBlank:prop.name == 'Category' ? true : !prop.isRequired
            });
        });

        Ext.callback(callback, this, [configItems]);

    }


};

