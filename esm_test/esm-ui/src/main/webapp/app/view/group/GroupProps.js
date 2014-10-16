Ext.define('Security.view.group.GroupProps', {
        extend:'Ext.container.Container',
        alias:'widget.groupprops',
        tpl: new Ext.XTemplate(
            '<div class="info-pane-header"><h2>{canonicalName}</h2></div><br/>'+
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