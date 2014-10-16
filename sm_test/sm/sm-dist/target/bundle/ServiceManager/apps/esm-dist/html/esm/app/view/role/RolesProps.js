Ext.define('Security.view.role.RolesProps', {
    extend:'Ext.container.Container',
    alias:'widget.roleprops',

    border:1,
    tpl: new Ext.XTemplate(
        '<div class="info-pane-header"><h2>{canonicalName:htmlEncode}</h2></div><br/>' +
            '<div class="info-pane-description ips-top">{description:this.getDescription}&nbsp;</div>',
            {
                getDescription:function (description) {
                        if (description) {
                            return description;
                        } else {
                            return '<i>No description available</i>';
                        }
                    }
            }
        )
});