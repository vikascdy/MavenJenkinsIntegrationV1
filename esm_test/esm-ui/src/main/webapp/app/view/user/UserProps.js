Ext.define('Security.view.user.UserProps', {
        extend:'Ext.container.Container',
        alias:'widget.userprops',
        tpl: new Ext.XTemplate(
            '<ul class="info-pane-stats">\n' +
                '<li><span class="ips-top">E-Mail</span> <span class="ips-bottom">{emailAddress:this.checkEmailAddress}</span></li>\n' +
                '<li><span class="ips-top">Status</span> <span class="ips-bottom">{active:this.formatActive}</span></li>\n' +
                '<li><span class="ips-top">Suspended</span> <span class="ips-bottom">{suspended:this.formatSuspended}</span></li>\n' +
            '<li><span class="ips-top">Last Logged On</span> <span class="ips-bottom">{formattedLastLoginDateTime:this.formatDate}</span></li>\n'+
            '<li><span class="ips-top">Created On</span> <span class="ips-bottom">{formattedCreatedDateTime}</span></li>\n'+
            '<li style="border: 0;"><span class="ips-top">Modified On</span> <span class="ips-bottom">{formattedModifiedDateTime}</span></li>\n'+
            '</ul>',
            {
            	checkEmailAddress : function(emailAddress){
            		return emailAddress ? emailAddress : 'NA';
            	},
            	formatDate:function (dateValue) {
                	return dateValue ?  dateValue : 'Never';
                    },
                formatActive:function(booleanValue){
                        return booleanValue ? "Active" : "Inactive";
                },
                formatSuspended:function(booleanValue){
                    return booleanValue ? "True" : "False";
            }
            }
        )
    }
);