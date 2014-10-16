Ext.define('Edifecs.CustomButtonGroup', {
    extend         :    'Ext.container.ButtonGroup',
    alias          :    'widget.CustomButtonGroup',
    buttonText     :    null,
    padding        :    0,
    buttonItems    :    "",
    ui             :    "edifecs-buttongroup",
    // init Component template method
    initComponent  :    function () {
        this.buttonRenderedCount = 0;
        if(this.buttonItems)
            this.buttonNumber = this.buttonItems.length;
        this.addEvents('itemsAfterrender');

        // set tooltip config options
        Ext.tip.QuickTipManager.init();
        Ext.apply(Ext.tip.QuickTipManager.getQuickTip(),
            {
                showDelay :50,
                minWidth  :10,
                trackMouse:true
            });
        this.callParent(arguments);
    },
    // overriding superclass template method
    afterRender    :    function () {

        var me = this;
        var buttonArray = this.buttonItems;
        Ext.Array.each(buttonArray, function (button, index) {
            // create button instance

            var buttonObject = me.createButton(button.toggleGroup,button.pressed,button.ui);

            // set button icon
            if (button.icon != undefined && button.icon != "") {
                buttonObject.setIcon(button.icon);
            }

            if (button.iconCls != undefined && button.iconCls != "") {
                buttonObject.setIconCls(button.iconCls);
            }

            // set button text
            if (button.text != undefined && button.text != "")
                buttonObject.setText(button.text);

            // set icon alignment for button either left or right
            if (button.iconAlign != undefined)
                if (button.iconAlign != 'top' && button.iconAlign != 'bottom')
                    buttonObject.iconAlign = button.iconAlign;

            // set button disabled
            if (button.disabled)
            {
                buttonObject.setDisabled(true);
                buttonObject.style = {"cursor":"default!important"};
            }

            // set itemId for each button
            buttonObject.itemId = button.itemId;
            buttonObject.setHeight(26);

            // set tooltip for button
            buttonObject.setTooltip(button.tooltip);

            // hide button
            if (button.hidden)
                buttonObject.hide();

            // add each instance in button group
            me.add(buttonObject);

            // set button radius class for each button
            me.setButtonRadius(index, buttonObject, buttonArray);
        });
        Edifecs.CustomButtonGroup.superclass.afterRender.apply(this, arguments);
        return;
    },

    //private Methods
    setButtonRadius :   function (indexValue, buttonObject, arrayItems) {
        if (arrayItems.length == 1) {
            buttonObject.addCls("btn-radius");
            return;
        }
        else if (indexValue == 0) {
            buttonObject.addCls("first-btn-radius");
            return;
        }
        else if ((indexValue + 1) == arrayItems.length) {
            buttonObject.addCls("last-btn-radius");
            return;
        }
        else {
            buttonObject.addCls("middle-btn-radius");
            return;
        }
    },

    createButton    :   function (toggleGroup,pressed,buttonUI) {
        var me = this;

        var toolbarButton = new Ext.button.Button({
            scale    :  'small',
            textAlign:  'left',
            pressed:(pressed!=undefined)?pressed:false,
            allowDepress : false,
            toggleGroup: (toggleGroup!=undefined)? toggleGroup:"",
            border   :  true,
            //ui       :  "edifecs-groupbuttons",
            ui             : (buttonUI!=undefined)?"edifecs-multiselect":'edifecs-groupbuttons',
            listeners:  {
                afterrender: function(btn, eOpts) {
                    me.buttonRendered(btn);
                },
                'mouseout'  :   function(btn)
                {
                    btn.blur();
                }
            }
        });
        return toolbarButton;
    },

    // private, clean up 
    onDestroy   :   function () {
        this.removeAll();
        Edifecs.CustomButtonGroup.superclass.onDestroy.apply(this, arguments);
    },
    buttonRendered: function(btn) {
        var me = this;
        me.buttonRenderedCount++;
        if (me.buttonRenderedCount >= me.buttonNumber) {
            me.fireEvent('itemsAfterrender');
        }
    }
});