//Used to define a model for a object and pass object attributes and
//values to the summary plugin
Ext.onReady(function () {
    //Sample server model for object
    Ext.define('ServerModel', {
        extend:'Ext.data.Model',
        fields:[
            {name:'id', type:'string'},
            {name:'name', type:'string'},
            {name:'hostName', type:'string'},
            {name:'ipAddress', type:'string'},
            {name:'heartbeatInterval', type:'string'},
            {name:'description', type:'string'},
            {name:'status', type:'string', persist:false, defaultValue:'unknown'}
        ]
    });

    //Server object with sample values as per the model
    var server = Ext.create('ServerModel', {
        id               :1,
        name             :'Test Server',
        hostName         :'Test Host',
        ipAddress        :'127.0.0.1',
        heartbeatInterval:'60',
        description      :'If the description value for an object exceeds 60 characters then automatically tooltip is generated.',
        status           :'active'
    });

    //Instance of SummaryBar plugin for icon references
    var summary = Ext.widget('SummaryBar');

    //Sample view for rendering SummaryBar plugin
    Ext.create('Ext.panel.Panel',
        {
            padding    :20,
            bodyPadding:10,
            renderTo   :Ext.getBody(),
            items      :[
                {
                    xtype :'SummaryBar',
                    item  :server,
                    fields:[
                        summary.statusIconHeader1(),
                        summary.statusIconHeader2(),
                        {
                            name:'name'
                        },
                        {
                            name:'hostName'
                        }, {
                            name:'ipAddress'
                        }, {
                            name :'heartbeatInterval',
                            title:"Heartbeat Interval",
                            tpl  :"{heartbeatInterval} seconds"
                        }]
                }
            ]
        });
});
		