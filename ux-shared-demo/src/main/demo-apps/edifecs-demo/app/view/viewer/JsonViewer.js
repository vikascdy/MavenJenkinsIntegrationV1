//Used to display JSON for the component
Ext.define('PP.view.viewer.JsonViewer', {
    extend       :  'Ext.panel.Panel',
    alias        :  'widget.jsonviewer',
    title        :  'Component JSON',
    autoScroll   :  true,
    bodyPadding  :  '0',
    style        :  'backgroundColor:#ffffff',
    initComponent:function () {
        Ext.getBody().mask('Loading JSON ...');
        var me = this;
        //AJAX call to get the passed JSON file
        Ext.Ajax.request({
            url    :this.pluginFile + '.json',
            success:function (r) {
                Ext.getCmp(me.id).update('<pre style="padding: 10px;" class="sh_javascript">' + Ext.htmlEncode(r.responseText) + '</pre>');
                designCode();
                Ext.getBody().unmask();
            },
            failure:function (r) {
                Ext.getCmp(me.id).update('<center><p class="no-file-found">No JSON file found for this component !</p></center>'); //If no JSON file found
                Ext.getBody().unmask();
            }
        });
        this.callParent(arguments);
    },
    afterComponentLayout :	function()
    {
        setPanelScroller(this);
        this.callParent(arguments);
    }
});
