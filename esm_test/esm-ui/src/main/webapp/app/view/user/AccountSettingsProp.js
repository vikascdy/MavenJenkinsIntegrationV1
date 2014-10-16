Ext.define('Security.view.user.AccountSettingsProp', {
		extend:'Ext.container.Container',
	    alias:'widget.accountsettingsprop',
	    tpl: new Ext.XTemplate(
	        '<div class="profileContainer">' +
	        '<div class="profileImage"><img width="80" height="80" src="resources/images/Profile.jpg"/></div>' +
	        '<div class="profilePrimaryDetails">' +
//	        '<div class="profileMainHeader">{salutation} {firstName} {middleName} {lastName} ({username})</div>' +
//	        '<div class="profileSubHeader" style="margin-top:5px;"><i>Unknown Designation</i></div>' +
//	        '<div class="profileSubHeader" style="margin-top:5px;"><i>Unknown Company</i></div>' +
	        '<div class="profileSubHeader" style="margin-top:5px;"><b>Created On : </b>{formattedCreatedDateTime}</div>' +
	        '<div class="profileSubHeader" style="margin-top:5px;"><b>Modified On : </b>{formattedModifiedDateTime}</div>' +
	        '<div class="profileSubHeader" style="margin-top:5px;"><b>Last Logged On : </b>{formattedLastLoginDateTime:this.formatDate}</div>' +
	        '</div>' +
	        '<div class="profileSecondaryDetails profileDetails">' +
//	        '<div class="profileDetailValue mico-profile-location "><i>Unknown Address</i></div>' +
	        '<div class="profileDetailValue mico-profile-email "><a href="#" style="pointer-events:none;">{emailAddress:this.checkEmailAddress}</a></div>' +
	        '<div class="profileSubHeader" style="margin-top:5px;"><b>Status : </b>{active:this.formatActive}</div>' +
	        '<div class="profileSubHeader" style="margin-top:5px;"><b>Suspended : </b>{suspended:this.formatSuspended}</div>' +
//	        '<div class="profileDetailValue mico-profile-phone "><i>Unknown Phone No.</i></div>' +
//	        '<div class="profileDetailValue mico-profile-fax "><i>Unknown Fax No.</i></div>' +
	        '</div>'+
	        '</div>',
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