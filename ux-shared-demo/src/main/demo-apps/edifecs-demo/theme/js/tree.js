

Ext.onReady(function() {
Ext.BLANK_IMAGE_URL = '../../edifecs-theme/images/tree/s.gif'; //we may need to include it as part of the ext-all.js

var store = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        children: [
          {text: "Premera Production Config 1.0", expanded: true, children:
              [
                {text: "Cluster TM EDI Production" , expanded: true,  children:
                    [
                        {text: "Server ABC (10.10.1.1)", expanded: true, children:
                            [
                                 {text: "Node Core (Port 9101)", expanded: true, children:
                                    [
                                        {text:"Apache Felix OSGi Runtime", leaf: true},
                                        {text:"Edifecs Installer JGroups", leaf: true},
                                        {text:"Edifecs Components Service", leaf: true},
                                        {text:"Edifecs System Service A", leaf: true},
                                        {text:"Edifecs System Service B", leaf: true},
                                        {text:"Edifecs System Service C", leaf: true}
                                    ]
                                 },
                                 {text: "Node Aux (Port 9201)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true }
                                    ]
                                 },
                                 {text: "Node Comp (Port 8801)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                                 },
                                 {text: "Node Apps (Port 8801)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                                 }
                            ]
                         },
                         {text: "Server DEF (10.10.1.1)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                         },
                         {text: "Resources", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                         }
                    ]
                }
              ]
          }
        ]
    }
});

var store2 = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true,
        children: [
          {text: "Premera Production Config 1.0",expanded: true, children:
              [
                {text: "Cluster TM EDI Production" ,expanded: true, children:
                    [
                        {text: "Server ABC (10.10.1.1)",expanded: true,children:
                            [
                                 {text: "Node Core (Port 9101)",expanded: true, children:
                                    [
                                        {text:"Apache Felix OSGi Runtime", leaf: true},
                                        {text:"Edifecs Installer JGroups", leaf: true},
                                        {text:"Edifecs Components Service", leaf: true},
                                        {text:"Edifecs System Service A", leaf: true},
                                        {text:"Edifecs System Service B", leaf: true},
                                        {text:"Edifecs System Service C", leaf: true}
                                    ]
                                 },
                                 {text: "Node Aux (Port 9201)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true }
                                    ]
                                 },
                                 {text: "Node Comp (Port 8801)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                                 },
                                 {text: "Node Apps (Port 8801)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                                 }
                            ]
                         },
                         {text: "Server DEF (10.10.1.1)", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                         },
                         {text: "Resources", children:
                                    [
                                        {text: "Apache Felix OSGi Runtime", leaf: true}
                                    ]
                         }
                    ]
                }
              ]
          }
        ]
    }
});
var tree1 = Ext.create('Ext.tree.Panel', {
    width: 325,
    height: 300,
    store: store,
    rootVisible: false,
    renderTo: document.getElementById('tree1')
});

 var tree2 =  Ext.create('Ext.tree.Panel', {
    width: 325,
    height: 300,
    store: store2,
    margin: '0 0 0 40',
    rootVisible: false,
    useArrows: true,
    renderTo: document.getElementById('tree2')
});

});



