//Used to provide a layout with vertical splitter as the master splitter 
//and horizontal scplitters as sub splitters within panes.

Ext.define('PP.view.layout.SplitterPanelVertical', {
    extend: 'Ext.container.Container',
    alias: 'widget.splitterpanelvertical',
    bodyPadding: '15',
    layout: 'border',
    style: 'background-color: #ffffff;',
    plugin: '',
    pane11Obj: '',
    pane12Obj: '',
    pane21Obj: '',
    pane22Obj: '',
    toolbarObj: '',
    initComponent: function () {

        if (this.showContent) {
            this.plugin = this.id;
            if (this.pane11.visible) {
                this.pane11Obj = this.pane11;
            }
            if (this.pane12.visible) {
                this.pane12Obj = this.pane12;
            }
            if (this.pane21.visible) {
                this.pane21Obj = this.pane21;
            }
            if (this.pane22.visible) {
                this.pane22Obj = this.pane22;
            }
            if (this.toolbar.visible) {
                this.toolbarObj = this.toolbar;
            }
        }

        this.items = [
            {
                xtype: 'container',
                height: 30,
                region: 'north',
                itemId: 'tabToolbar',
                hidden: !this.toolbarObj.visible,
                layout: {
                    type: 'hbox',
                    align: 'top',
                    pack: 'start'
                },
                defaultType: 'button',
                defaults: {
                    margin: '0 10 0 0'
                },
                items: [
                    {
                        xtype: 'CustomButtonGroup',
                        width:'100%',
                        height:30,
                        buttonItems: this.toolbarObj.buttons
                    }
                ]
            },
            //Left column
            {
                xtype: 'container',
                region: 'center',
                border: true,
                width: this.pane21Obj || this.pane22Obj ? '50%' : '100%',
                flex: 1,
                split: this.pane21Obj.visible || this.pane22Obj.visible,
                layout: {
                    type: 'border',
                    align: 'stretch'
                },
                items: [
                    {
                        // Left Column Top Pane "Preview"
                        xtype: this.getPaneType(this.pane11Obj.type),
                        pluginFile: this.pane11Obj.url ? this.pane11Obj.url : this.plugin,
                        border: true,
                        region: 'north',
                        hidden: !this.pane11Obj.visible,
                        split: this.pane12Obj.visible,
                        height: this.pane12Obj ? '50%' : '100%',
                        flex: 1
                    },
                    {
                        // Left Column Bottom Pane "HTML + JS"
                        xtype: this.getPaneType(this.pane12Obj.type),
                        pluginFile: this.pane12Obj.url ? this.pane12Obj.url : this.plugin,
                        border: true,
                        region: 'center',
                        hidden: !this.pane12Obj.visible,
                        split: this.pane11Obj.visible,
                        height: this.pane11Obj ? '50%' : '100%',
                        flex: 1
                    }

                ]
            },
            //Right column
            {
                xtype: 'container',
                region: 'east',
                width: this.pane11Obj || this.pane12Obj ? '50%' : '100%',
                hidden: !this.pane21Obj && !this.pane22Obj,
                flex: 1,
                border: true,
                split: this.pane11Obj.visible || this.pane12Obj.visible,
                layout: {
                    type: 'border',
                    align: 'stretch'
                },
                items: [
                    {
                        // Right Column Top Pane
                        xtype: this.getPaneType(this.pane21Obj.type),
                        pluginFile: this.pane21Obj.url,
                        border: true,
                        region: 'north',
                        hidden: !this.pane21Obj.visible,
                        split: this.pane22Obj.visible,
                        height: this.pane22Obj ? '50%' : '100%',
                        flex: 1
                    },
                    {
                        // Right Column Bottom Pane "JS"
                        xtype: this.getPaneType(this.pane22Obj.type),
                        pluginFile: this.pane22Obj.url ? this.pane22Obj.url : '../edifecs-plugins/' + this.plugin,
                        border: true,
                        region: 'center',
                        hidden: !this.pane22Obj.visible,
                        split: this.pane21Obj.visible,
                        height: this.pane21Obj ? '50%' : '100%',
                        flex: 1
                    }

                ]
            }
        ]
        this.callParent(arguments);
    },
    getPaneType: function (paneType) {
        switch (paneType) {
            case 'preview':
                return 'previewviewer';
            case 'previewDoc':
                return 'previewviewerdoc';
            case 'html':
                return 'htmlviewer';
            case 'css':
                return 'cssviewer';
            case 'js':
                return 'jscodeviewer';
            case 'json':
                return 'jsonviewer';
            case 'xml':
                return 'xmlviewer';
            case 'null' :
                return 'container';
        }
    }
});
