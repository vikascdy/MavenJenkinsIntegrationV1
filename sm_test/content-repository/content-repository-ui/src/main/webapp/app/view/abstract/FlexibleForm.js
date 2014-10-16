// ABSTRACT VIEW: Flexible Form
// A form that can be displayed either vertically or horizontally, depending on
// the value of its `horizontal` config option. Instead of an `items` config,
// it takes a `columns` config, and the columns are displayed side-by-side if
// horizontal=true.
// ----------------------------------------------------------------------------

Ext.define('SM.view.abstract.FlexibleForm', {
    extend: 'Ext.form.Panel',

    initComponent: function(config) {
        if (!this.columns) {
            Ext.Error.raise("A FlexibleForm must have a 'columns' config.");
        }
        if (this.horizontal) {
            this.layout = 'column';
            this.fieldDefaults = Functions.merge(this.fieldDefaults, {
                labelAlign: 'top'
            });

            var cWidth = 1.0 / this.columns.length;
//          var cPadding = this.columnPadding || 4;
            var cPadding = 0;
            this.items = Ext.Array.map(this.columns, function(column) {
                return {
                    xtype: 'container',
                    layout: 'anchor',
                    defaults: {anchor: '100%'},
                    columnWidth: cWidth,
                    padding: cPadding,
                    items: column
                };
            }, this);
        } else {
            this.layout = 'anchor';
            this.draggable = false;
            this.items = this.columns[0];
            this.fieldDefaults = {
                labelWidth:140,
                labelSeparator:''
            };
            this.defaults = {anchor: '100%'};
            for (var i = 1; i < this.columns.length; i++)
                this.items = this.items.concat(this.columns[i]);
        }
        this.callParent(arguments);
    }
});

