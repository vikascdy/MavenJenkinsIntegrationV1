//Used to display the preview for the component using iframe
Ext.define('PP.view.viewer.PreviewViewerDoc', {
    extend       :'Ext.panel.Panel',
    alias        :'widget.previewviewerdoc',
    title        :'Comments Preview',
    autoScroll   :true,
    bodyPadding  :'0',
    style        :'backgroundColor:#ffffff',
    initComponent:function () {
        this.tools = [
            {
                type  :'maximize',
                qtip  :'Open preview in new tab',
                itemId:'previewDoc'
            }
        ];

        //Using IFRAME to display the preview of the component. No AJAX call made here.
        this.html = '<iframe frameBorder="0" style="width:100%;height:100%;border:0px;" src="' + this.pluginFile + '"></iframe>';
        this.callParent(arguments);
    }
});
