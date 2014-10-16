Ext.define('Security.view.user.UserProfileProps', {
        extend:'Ext.container.Container',
        alias:'widget.userprofileprops',
        tpl: new Ext.XTemplate(
            '<ul class="info-pane-stats">\n' +
                '<li><span class="ips-top">First Name</span> <span class="ips-bottom">{firstName}</span></li>\n' +
                '<li><span class="ips-top">Middle Name</span> <span class="ips-bottom">{middleName}</span></li>\n' +
                '<li><span class="ips-top">Last Name</span> <span class="ips-bottom">{lastName}</span></li>\n' +
                '<li><span class="ips-top">User Name</span> <span class="ips-bottom">{username}</span></li>\n' +
                '<li><span class="ips-top">E-Mail</span> <span class="ips-bottom">{emailAddress}</span></li>\n' +
                '<li><span class="ips-top">Created On</span> <span class="ips-bottom">{formattedCreatedDateTime}</span></li>\n' +
                '<li><span class="ips-top">Modified Date</span> <span class="ips-bottom">{formattedModifiedDateTime}</span></li>\n' + 
            '</ul>'
        )
    }
);