// VIEW: EULA Page
// Prompts the user to accept an End-User License Agreement.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.EulaPage', {
    extend: 'Ext.container.Container',
    alias : 'widget.eulapage',
    layout:'border',
    cls:'eula-page',
    items : [{
        xtype:'ApplicationBar',
        id: 'menuToolbar',
        url:'plainNavigation.json',
        logoIcon:'edifecs-logo',
        region:'north'
    }, {
        xtype:'container',
        region:'center',

        items:[{
            xtype:'component',
            cls:'generic-page-header',
            padding:'25 0 0 21',
            height:75,
            html:'<h1>End User License Agreement</h1>'
        }, {
            xtype:  'htmleditor',
            itemId: 'eulabox',
            enableColors: false,
            enableAlignments: false,
            enableFormat: false,
            enableLinks: false,
            enableFont: false,
            enableFontSize: false,
            enableLinks: false,
            enableLists: false,
            enableSourceEdit: false,
            cls:'eula-box padded-box',
            margin: '0 0 0 20',
            width: 550,
            minHeight:300,
            height:'auto',
            readOnly: true,
            autoScroll:true,
            grow:true,
            shrinkWrap:'2'

        }, {
            xtype:    'checkboxfield',
            itemId:   'acceptbox',
            width:550,
            style:'color:#222b36;',
            boxLabel: 'I accept the terms of this license agreement',
            disabled: true,
            margin:'6 0 10 20',

            listeners: {
                change: function(chkbox) {
                    var value = chkbox.getValue();
                    chkbox.up('container').down('#nextButton').setDisabled(!value);
                }
            }
        }, {
            xtype:'component',
            cls:'separator-line',
            width:550,
            margin:'0 0 10 20',
            html:'<div></div>'
        }, {
            xtype : 'container',
            margin: '0 0 150 20',
            width : 550,
            layout: 'hbox',
            items:[{
                xtype:'component',
                width: 470
            }, {
                xtype:    'button',
                itemId:   'nextButton',
                text:     'Next',
                disabled: true,
                width:    80
            }]
        }]
    }, {
        xtype: 'component',
        border: true,
        id:'eulaFooterDetail',
        region: 'south',
        padding:'14 20 0 0',
        cls: 'generic-page-footer',
        html: '<p>Copyright &copy; 2013, Edifecs Inc</p>',
        height:40
    }],

    initComponent: function() {
        this.callParent(arguments);
        if (SM.testMode) {
            Ext.getCmp('eulaFooterDetail').html='<p><span style="color:red; font-weight:bold;">TEST MODE ENABLED&nbsp;&nbsp;&nbsp;&nbsp;</span>Copyright &copy; 2013, Edifecs Inc</p>';
        }
        // Load the EULA text, disabling the checkbox and displaying a loading
        // indicator while the AJAX call loads.
        var eulaBox = this.down("#eulabox");
        var checkbox = this.down("#acceptbox");
        eulaBox.setLoading(true);
        Ext.Ajax.request({
            url: JSON_URL + '/eula',
            success: function(response) {
                eulaBox.setValue(response.responseText);
                eulaBox.setLoading(false);
                checkbox.enable();
            },
            failure: function(response) {
                Functions.errorMsg("Failed to load EULA. Try refreshing the page.");
            }
        });
    }
});

