Ext.onReady(function () {

    Ext.Loader.setConfig({enabled:true});
    Ext.require([
        'Ext.grid.*',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.tip.*',
        'Ext.Action'
    ]);

    var store = Ext.create('Ext.data.Store', {
        fields:['Icon', 'NamePurpose', 'className'],
        data:{
            items:[
                { Icon:'add', NamePurpose:'Add', className:'.add'},
                { Icon:'upload', NamePurpose:'Upload', className:'.upload'},
                { Icon:'delete', NamePurpose:'Delete', className:'.delete'},
                { Icon:'edit', NamePurpose:'Edit', className:'.edit'},
                { Icon:'publish', NamePurpose:'Publish', className:'.publish'},
                { Icon:'run', NamePurpose:'Run', className:'.run'},
                { Icon:'partner', NamePurpose:'partner', className:'.partner'},
                { Icon:'close', NamePurpose:'close', className:'.close'},
                { Icon:'cancel', NamePurpose:'Cancel', className:'.cancel'},
                { Icon:'save', NamePurpose:'Save', className:'.save'},
                { Icon:'reload', NamePurpose:'Reload', className:'.reload'},
                { Icon:'download', NamePurpose:'Download', className:'.download'},
                { Icon:'new', NamePurpose:'New', className:'.new'},
                { Icon:'open', NamePurpose:'Open', className:'.open'},
                { Icon:'apply', NamePurpose:'Apply', className:'.apply'},
                { Icon:'email', NamePurpose:'Email', className:'.email'},
                { Icon:'search', NamePurpose:'Search', className:'.search'},
                { Icon:'submit', NamePurpose:'Submit', className:'.submit'},
                { Icon:'copy', NamePurpose:'Copy', className:'.copy'},
                { Icon:'rename', NamePurpose:'Rename', className:'.rename'},
                { Icon:'import', NamePurpose:'Import', className:'.import'},
                { Icon:'export', NamePurpose:'Export', className:'.export'},
                { Icon:'validate', NamePurpose:'Validate', className:'.validate'},
                { Icon:'print', NamePurpose:'Print', className:'.print'},
                { Icon:'file', NamePurpose:'File', className:'.file'},
                { Icon:'favorite', NamePurpose:'Favorite', className:'.favorite'},
                { Icon:'link', NamePurpose:'Link', className:'.link'},
                { Icon:'move-up', NamePurpose:'Move Up', className:'.move-up'},
                { Icon:'move-down', NamePurpose:'Move Down', className:'.move-down'},
                { Icon:'first', NamePurpose:'First', className:'.first'},
                { Icon:'previous', NamePurpose:'Previous', className:'.previous'},
                { Icon:'next', NamePurpose:'Next', className:'.next'},
                { Icon:'last', NamePurpose:'Last', className:'.last'},
                { Icon:'flag', NamePurpose:'Flag', className:'.flag'},
                { Icon:'user', NamePurpose:'User', className:'.user'}
            ]
        },
        proxy:{
            type:'memory',
            reader:{
                type:'json',
                root:'items'
            }
        }
    });


    var statusObjectstore = Ext.create('Ext.data.Store', {
        fields:['Icon', 'NamePurpose', 'className'],
        data:{
            items:[
                { Icon:'help-status', NamePurpose:'Help Status', className:'.help-status'},
                { Icon:'success-status', NamePurpose:'Success Status', className:'.success-status'},
                { Icon:'warning2-status', NamePurpose:'Warning2 Status', className:'.warning2-status'},
                { Icon:'warning-status', NamePurpose:'Warning Status', className:'.warning-status'},
                { Icon:'fail-status', NamePurpose:'Fail Status', className:'.fail-status'},
                { Icon:'error-status', NamePurpose:'Error Status', className:'.error-status'},
                { Icon:'info-status', NamePurpose:'Information Status', className:'.info-status'},
                { Icon:'search-status', NamePurpose:'Search Status', className:'.search-status'},
                { Icon:'unknown-status', NamePurpose:'Unknown Status', className:'.unknown-status'},
                { Icon:'time-status', NamePurpose:'Time Status', className:'.time-status'},
                { Icon:'validation-status', NamePurpose:'Validation Status', className:'.validation-status'},

                { Icon:'fail-generic', NamePurpose:'Fail', className:'.fail-generic'},
                { Icon:'warning-generic', NamePurpose:'Warning', className:'.warning-generic'},
                { Icon:'success-generic', NamePurpose:'Success', className:'.success-generic'},
                { Icon:'confirmation-generic', NamePurpose:'Confirmation', className:'.confirmation-generic'},
                { Icon:'error-generic', NamePurpose:'Error', className:'.error-generic'},

                { Icon:'fail-small-generic', NamePurpose:'Fail', className:'.fail-small-generic'},
                { Icon:'warning-small-generic', NamePurpose:'Warning', className:'.warning-small-generic'},
                { Icon:'success-small-generic', NamePurpose:'Success', className:'.success-small-generic'},
                { Icon:'confirmation-small-generic', NamePurpose:'Confirmation', className:'.confirmation-small-generic'},
                { Icon:'error-small-generic', NamePurpose:'Error', className:'.error-small-generic'},


                { Icon:'inbound-object', NamePurpose:'Inbound', className:'.inbound-object'},
                { Icon:'batch-object', NamePurpose:'Batch', className:'.batch-object'},
                { Icon:'organization-object', NamePurpose:'Organization', className:'.organization-object'},
                { Icon:'messageIncoming-object', NamePurpose:'Message Incoming', className:'.messageIncoming-object'},
                { Icon:'messageOutgoing-object', NamePurpose:'Message Outgoing', className:'.messageOutgoing-object'},
                { Icon:'message-object', NamePurpose:'Message', className:'.message-object'},
                { Icon:'note-object', NamePurpose:'Note', className:'.note-object'},

                { Icon:'address-object', NamePurpose:'Address', className:'.address-object'},
                { Icon:'fax-object', NamePurpose:'Fax', className:'.fax-object'},
                { Icon:'phone-object', NamePurpose:'Phone', className:'.phone-object'},

                { Icon:'web-object', NamePurpose:'Web', className:'.web-object'},
                { Icon:'partner-object', NamePurpose:'Partner', className:'.partner-object'},
                { Icon:'transmission-object', NamePurpose:'Transmission', className:'.transmission-object'},
                { Icon:'user-object', NamePurpose:'User', className:'.user-object'},

                { Icon:'transactionFile-object', NamePurpose:'Transaction File', className:'.transactionFile-object'},
                { Icon:'pin-object', NamePurpose:'Pin', className:'.pin-object'},
                { Icon:'favorite-object', NamePurpose:'Favorite', className:'.favorite-object'},
                { Icon:'flag-object', NamePurpose:'Flag', className:'.flag-object'},

                { Icon:'favorite-fill', NamePurpose:'Favorite', className:'.favorite-fill'},
                { Icon:'favorite-empty', NamePurpose:'Favorite Empty', className:'.favorite-empty'}
            ]
        },
        proxy:{
            type:'memory',
            reader:{
                type:'json',
                root:'items'
            }
        }
    });

    Ext.create('Ext.container.Container',
        {
            width:500,
            renderTo:'grid',
            border:false,
            items:[
                {
                    xtype:'gridpanel',
                    store:store,
                    columnLines:false,
                    columns:[
                        {
                            text:'Icon',
                            dataIndex:'Icon',
                            width:50,
                            align:'center',
                            renderer:function (value) {
                                return '<div class="' + value + ' icon16"></div>'
                            }
                        },
                        {
                            text:'Name/Purpose',
                            width:150,
                            dataIndex:'NamePurpose'
                        },
                        {
                            text:'Responsible CSS Class Name',
                            flex:1,
                            dataIndex:'className'
                        }
                    ],
                    width:600,
                    height:400
                }
            ]
        });


    Ext.create('Ext.container.Container',
        {
            width:500,
            renderTo:'gridStatus',
            border:false,
            items:[
                {
                    xtype:'gridpanel',
                    store:statusObjectstore,
                    columnLines:false,
                    columns:[
                        {
                            text:'Icon',
                            dataIndex:'Icon',
                            width:50,
                            align:'center',
                            renderer:function (value) {
                                return '<div class="' + value + ' icon16"></div>'
                            }
                        },
                        {
                            text:'Name/Purpose',
                            width:150,
                            dataIndex:'NamePurpose'
                        },
                        {
                            text:'Responsible CSS Class Name',
                            flex:1,
                            dataIndex:'className'
                        }
                    ],
                    width:600,
                    height:400
                }
            ]
        });
});