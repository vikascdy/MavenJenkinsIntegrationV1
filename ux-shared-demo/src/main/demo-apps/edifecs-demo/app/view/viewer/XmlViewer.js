//Used to display XML for the component
Ext.define('PP.view.viewer.XmlViewer', {
    extend       :'Ext.panel.Panel',
    alias        :'widget.xmlviewer',
    title        :'XML',
    autoScroll   :true,
    bodyPadding  :'0',
    style        :'backgroundColor:#ffffff',
    initComponent:function () {

        Ext.getBody().mask('Loading XML ...');
        var me = this;

        //AJAX call to get the passed JSON file
        Ext.Ajax.request({
            url    :this.pluginFile + '.xml',
            success:function (r) {

                Ext.getCmp(me.id).update('<pre style="padding: 10px;" class="sh_xml">' + Ext.htmlEncode(r.responseText) + '</pre>');
                designCode();
                Ext.getBody().unmask();
            },
            failure:function (r) {
                Ext.getCmp(me.id).update('<center><p class="no-file-found">No XML file found for this component !</p></center>'); //If no XML file found
                Ext.getBody().unmask();
            }
        });

        this.callParent(arguments);
    }

});
