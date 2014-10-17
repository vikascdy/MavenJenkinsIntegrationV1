// Core Contoller is responsible for handling tree node click actions,
// adding nodes to the tab panel and check for duplicate tabs.

Ext.define('PP.controller.CoreController', {
    extend:'Ext.app.Controller',
    stores:['ComponentListStore'],
    models:['ComponentListModel'],
    views:[
        'core.PluginDetail',
        'core.TabCloseMenu',
        'core.ProgressWindow',
        'core.UXDemoPortal',
		'core.MenuItemWindow',
        'layout.SplitterPanelVertical',
        'layout.SplitterPanelHorizontal',
        'viewer.JsCodeViewer',
        'viewer.CssViewer',
        'viewer.PreviewViewer',
        'viewer.PreviewViewerDoc',
        'viewer.HtmlViewer',
        'viewer.JsonViewer',
        'viewer.XmlViewer'
    ],
    init    :   function () {
        var recordObj = null;
        this.control({
            'LeftMenu > treeview'              :{
                itemclick:this.handleTreeItems
            },
            'jscodeviewer tool[type=maximize]' :{
                click:function (e, targer, owner, tool) {
                    this.handleComponentActions(e);
                }
            },
            'previewviewer tool[type=maximize]':{
                click:function (e, target, owner, tool) {
                    this.handleComponentActions(e);
                }
            },
            'previewviewerdoc tool[type=maximize]':{
                click:function (e, target, owner, tool) {
                    this.handleComponentActions(e);
                }
            },
            'previewviewer tool[type=gear]'    :{
                click:function (e, target, owner, tool) {
                    this.handleComponentActions(e);
                }
            },
            'cssviewer tool[type=maximize]'    :{
                click:function (e, target, owner, tool) {
                    this.handleComponentActions(e);
                }
            },
            'htmlviewer tool[type=maximize]'   :{
                click:function (e, target, owner, tool) {
                    this.handleComponentActions(e);
                }
            },
            'splitterpanelhorizontal button'   :{
                click:function (e) {
                    this.handleComponentActions(e);
                }
            },
            'splitterpanelvertical button'     :{
                click:function (e) {
                    this.handleComponentActions(e);
                }
            },
            'plugindetail tab'                 :{
                deactivate:this.handleTabDeactivate
            }

        });
    },

    //Used to add tab to tab panel on tree node click
    addTreeNodeToTab      :function (record, tabPanel, nodeName, nodeId) {
        var isShowContent = true;
        var splitterType = '';
        recordObj = record;
        if (record.raw.masterSplitter == 'vertical')
            splitterType = 'splitterpanelvertical';
        else if (record.raw.masterSplitter == 'horizontal')
            splitterType = 'splitterpanelhorizontal';

        if (record.raw.leaf && nodeName != 'Welcome') {
            tabPanel.add({
                closable      :true,
                title         :nodeName,
                id            :nodeId,
                toolbar       :true,
                xtype         :splitterType,
                pane11        :record.raw.pane11,
                pane12        :record.raw.pane12,
                pane21        :record.raw.pane21,
                pane22        :record.raw.pane22,
                toolbar       :record.raw.toolbar,
                masterSplitter:record.raw.masterSplitter,
                showContent   :isShowContent     // For welcome page, to check whether to display any pane or not. For welcome page it is FALSE.
            });
            tabPanel.setActiveTab(nodeId);
        }
    },

    //Used to add panes to tab panel on maximize tool icon click
    addPaneWindowToTab    :function (tabPanel, nodeName, activeTabId, btnId, newNodeId, activeTab, buttonAttr) {
        var type = '';
        var pluginPath = '';
        switch (btnId) {
            case 'preview':
                pluginPath = activeTab.pane11.url ? activeTab.pane11.url : activeTabId; //Check if file URL is available otherwise component id is the filename.
                type = 'previewviewer';
                break;
            case 'previewDoc':
                pluginPath = activeTab.pane12.url ? activeTab.pane12.url : activeTabId; //Check if file URL is available otherwise component id is the filename.
                type = 'previewviewerdoc';
                break;
            case 'css':
                pluginPath = activeTab.pane21.url;
                type = 'cssviewer';
                break;
            case 'js':
                type = 'jscodeviewer';
                pluginPath = activeTab.pane22.url ? activeTab.pane22.url : '../edifecs-plugins/' + activeTabId;
                break;
            case 'html':
                pluginPath = activeTab.pane12.url ? activeTab.pane12.url : activeTabId;
                type = 'htmlviewer';
                break;
            case 'openNewWindow':
                pluginPath = activeTab.pane11.url ? activeTab.pane11.url : activeTabId;
                type = 'newWindow';
                break;
            case 'json':
                pluginPath = buttonAttr.url ? buttonAttr.url : 'json/' + activeTabId;
                type = 'jsonviewer';
                break;
            case 'ojs':
                pluginPath = buttonAttr.url ? buttonAttr.url : '../edifecs-plugins/' + activeTabId;
                type = 'jscodeviewer';
                break;
            case 'ocss':
                pluginPath = buttonAttr.url;
                type = 'cssviewer';
                break;
            case 'xml':
                pluginPath = buttonAttr.url ? buttonAttr.url : activeTabId;
                type = 'xmlviewer';
                break;
        }
        if (type == 'newWindow') {
            window.open(pluginPath);
            type = null;
        }
        else {

            tabPanel.add({
                closable  :true,
                title     :nodeName,
                id        :newNodeId,
                pluginFile:pluginPath,
                xtype     :type
            });

            tabPanel.setActiveTab(newNodeId);
        }
    },

    //Used to check that whether it is trying to add duplicate tab
    //If duplicate tab is getting created it activates the already added tab.
    checkForDuplicateNodes:function (tabPanel, nodeId) {
        if (Ext.getCmp(nodeId)) {
            tabPanel.setActiveTab(nodeId);
            return false;
        }
        else
            return true;
    },

    //Used to handle tree items and call addTreeNodeToTab to add node to tab panel
    handleTreeItems       :function (view, record, item, index, event) {
        try {
            var tabPanel = Ext.getCmp('plugin-container');
            var nodeName = record.data.text;
            var nodeId = record.data.id;
            if (this.checkForDuplicateNodes(tabPanel, nodeId)) {

                this.addTreeNodeToTab(record, tabPanel, nodeName, nodeId);
            }

        } catch (ex) {
            Functions.errorMsg(ex);
            return false;
        }
    },

    //Used to retrieve the button group attributes
    getButtonAttributes   :function (btn) {
        var buttonAction = recordObj.raw.toolbar.buttonAction;
        var attributes;
        Ext.Array.each(buttonAction, function (attr) {
            if (attr.text == btn) {
                attributes = attr;
            }
        });
        return attributes;
    },


    //Used to handle deactivation of tab so as to find and highlight the active tab in component list tree
    handleTabDeactivate   :function (tab) {
        var tabPanel = Ext.getCmp('plugin-container');
        var tree = Ext.getCmp('pp-tree-container');
        var treeView = tree.getView();
        var activeTabId = Ext.String.trim(tabPanel.getActiveTab().id.split('-')[0]);

        var oldTabId = Ext.String.trim(tab.card.id.split('-')[0]);
        var oldTreeNode = tree.getStore().getNodeById(oldTabId);
        var newTreeNode = tree.getStore().getNodeById(activeTabId);

        oldTreeNode.collapse();
        newTreeNode.bubble(function (node) {
            node.expand()
        });

        if (activeTabId != oldTabId) {
            tree.getSelectionModel().select(newTreeNode);
            tree.getSelectionModel().deselect(oldTreeNode);
            treeView.addRowCls(newTreeNode, "selectedCls");
            treeView.removeRowCls(oldTreeNode, "selectedCls");
        }
    },

    //Used to handle tool buttons available on various panes
    handleComponentActions:function (btn) {
        var tabPanel = Ext.getCmp('plugin-container');
        var buttonTitle = "";
        var btnId = btn.getItemId();
        var toolbarBtnId = btnId;
        var activeTab = tabPanel.getActiveTab();
        var activeTabTitle = activeTab.title;
        var activeTabId = tabPanel.getActiveTab().id;
        var buttonAttr = null;
        var flag = 0;
        switch (btnId) {
            case 'preview':
                buttonTitle = "Live preview";
                break;
            case 'previewDoc':
                buttonTitle = "Comments Preview";
                break;
            case 'css':
                buttonTitle = "CSS";
                break;
            case 'js':
                buttonTitle = "JS Code";
                break;
            case 'html':
                buttonTitle = "HTML + JS";
                break;
            case 'openNewWindow':
                buttonTitle = "Live preview";
                break;
            default :
                buttonAttr = this.getButtonAttributes(btnId);
                flag = 1;
                switch (buttonAttr.type) {
                    case 'json':
                        buttonTitle = btnId;
                        btnId = 'json'
                        break;
                    case 'js':
                        buttonTitle = btnId;
                        btnId = 'ojs'
                        break;
                    case 'css':
                        buttonTitle = btnId;
                        btnId = 'ocss'
                        break;
                    case 'xml':
                        buttonTitle = btnId;
                        btnId = 'xml'
                        break;
                }
        }
        var newTabId = flag ? activeTab.id + "-" + toolbarBtnId : activeTab.id;
        if (btnId == 'openNewWindow') {
            newTabId = newTabId + "_blank";
        }
        var newTabName = activeTabTitle + " : " + buttonTitle;
        if (this.checkForDuplicateNodes(tabPanel, newTabId)) {
            this.addPaneWindowToTab(tabPanel, newTabName, activeTabId, btnId, newTabId, activeTab, buttonAttr);
        }
    }
});