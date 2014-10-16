Ext.define('Security.view.common.DateDisplayField', {
    extend: 'Ext.form.DisplayField',
    alias : 'widget.datedisplayfield',

    setValue: function(dateValue) {
        if (Ext.isDate(dateValue)) {
            this.setRawValue(Ext.util.Format.date(dateValue));
        }
        else {
            this.callParent(dateValue);
        }
    }

});
