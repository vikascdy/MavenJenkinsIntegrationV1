// VIEW: Info Pane Subheader
// A list of information displayed under the header of an info pane. It is
// styled with CSS (by default, it is a <ul> element), and is hidden whenever
// the Edit Form of the Info Pane is displayed.
//
// In order for this component to function, it must be passed a Record called
// `item` and an array called `fields`. Each object in the `fields` array can
// have the following properties:
// - name: The name to be used in the template. Will also be the name of the
//         property retrieved from the record, unless dataFn is defined.
// - title: The title to display. If not present, will be name, capitalized.
// - dataFn: A function to retrieve the data for this field. Takes the record
//           as its first argument, and (if async=true) a callback function as
//           its second argument.
// - tpl: An alternate template to display the data for this field. May also
//        incorporate the values of other fields.
// - async: Boolean value. If true, dataFn will be passed a callback function
//          as its second argument. Default false.
// - visible: Boolean value. If false, this field will not be rendered. Default
//            true.
// ----------------------------------------------------------------------------

Ext.define('SM.view.core.InfoPaneSubheader', {
    extend : 'Ext.Component',
    alias  : 'widget.infopanesubheader',
    border : false,
    padding: '2 0 16 0',

    fields : null,
    item   : null,
    tmpData: {},

    _buildTpl: function() {
        var itemCount = 0;
        var overwriteStyle = "";
        var tpl = "<ul class='info-pane-stats'>\n" +
                      '<tpl if="description &amp;&amp; description.length&gt;0">\n' +
                          "<li style='width: 20em; border-left: 0px; border-right: 1px dotted #c8d6df; margin-right:15px;'><span class='ips-top'>{description}</span></li>\n" +
                      '</tpl>\n';
        Ext.each(this.fields, function(field) {
            overwriteStyle = (itemCount>1) ? "padding-left:15px !important;" : "border-left: 0px;";
            itemCount++;

            if (field.visible !== false) {
                var subTpl = field.tpl || '{' + field.name + '}';
                var title = field.title || Functions.capitalize(field.name);
                tpl += Ext.String.format("<tpl if=\"{2}\"><li style=\"{3}\">" +
                                             "<span class='ips-top'>{0}</span> " +
                                             "<span class='ips-bottom'>{1}</span>" +
                                         "</li></tpl>\n",
                    title, subTpl, field.name, overwriteStyle);
            }
        });

        tpl += "</ul>";
        return new Ext.XTemplate(tpl);
    },

    _asyncUpdate: function(name, value) {
        var newData = Functions.clone(this.tmpData);
        newData[name] = value;
        this.tmpData = newData;
        this.update(newData);
    },

    loadData: function() {
        var item = this.item;
        var me = this;
        var data = {};
        Ext.each(this.fields, function(field) {
            var name = field.name || Ext.Error.raise("InfoPaneSubheader fields must have a 'name' attribute.");
            var value;
            if (field.dataFn) {
                if (field.async) {
                    value = "Loading...";
                    field.dataFn(item, function(value) {
                        me._asyncUpdate.apply(me, [name, value]);
                    });
                } else {
                    value = field.dataFn(item);
                }
            } else {
                value = item.get(name);
            }
            data[name] = value;
        });
        data.description = item.get('description');
        this.tmpData = data;
        return data;
    },

    initComponent: function(config) {
        this.tpl = this._buildTpl();
        this.data = this.loadData();
        this.callParent(arguments);
    },

    reload: function() {
        this.update(this.loadData());
    }
});

