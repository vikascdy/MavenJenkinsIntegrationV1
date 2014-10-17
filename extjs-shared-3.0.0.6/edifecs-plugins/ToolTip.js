Ext.define('Edifecs.ToolTip', {
    extend: 'Ext.tip.ToolTip',
    alias: 'widget.tooltip',
    autoHide: false,
    itemsConfig: "",
    shadow: true,
    initComponent: function () {
        this.items = this.itemsConfig;

        if (this.shadow)
            this.addCls("tooltipShadow");
        else
            this.removeCls("tooltipShadow");

        this.callParent(arguments);
    },
    afterRender: function () {

        this.setAnchorPosition();

        this.callParent(arguments);
    },
    setAnchorPosition: function () {
        if ((this.anchorOffset == "center" && this.anchor == "left") || (this.anchorOffset == "center" && this.anchor == "right")) {
            this.anchorOffset = this.getHeight() / 2 - 11;
        }
        else if ((this.anchorOffset == "center" && this.anchor == "top") || (this.anchorOffset == "center" && this.anchor == "bottom")) {
            this.anchorOffset = this.getWidth() / 2 - 20;
        }
    }
});