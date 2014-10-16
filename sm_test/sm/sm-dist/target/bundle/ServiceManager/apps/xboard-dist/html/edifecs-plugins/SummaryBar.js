//SummaryBar plugin to generate summary for an object
//passed to it
Ext.define('Edifecs.SummaryBar', {
    extend :'Ext.Component',
    alias  :'widget.SummaryBar',
    border :false,
    padding:'2 0 0 0',
    fields :null,
    item   :null,
    me     :null,
    tmpData:{},

    afterRender      :function () {
        me = this;
        this.tpl = this._buildTpl();
        this.data = this.loadData();
        this.reload();
        Edifecs.SummaryBar.superclass.afterRender.apply(this, arguments);
        return;
    },

    // Capitalizes the first letter of a string.
    capitalize       :function (str) {
        return str.charAt(0).toUpperCase() + str.slice(1);
    },

    // Clones an object or array.
    clone            :function (o) {
        if (!o || 'object' !== typeof o) {
            return o;
        }
        if ('function' === typeof o.clone) {
            return o.clone();
        }
        var c = '[object Array]' === Object.prototype.toString.call(o) ? [] : {};
        var p, v;
        for (p in o) {
            if (o.hasOwnProperty(p)) {
                v = o[p];
                if (v && 'object' === typeof v) {
                    c[p] = this.clone(v);
                }
                else {
                    c[p] = v;
                }
            }
        }
        return c;
    },

    //Gives icon class for the provided status
    getStatusIconCls :function (status) {
        var iconTable = {
            active      :'success-status',
            offline     :'ico-offline',
            'new'       :'ico-New',
            unassociated:'ico-offline',
            error       :'error-status',
            unknown     :'unknown-status'
        };
        return iconTable[status] || 'ico-unknown';
    },

    //Gets iconCls attribute for the object
    statusIconHeader1:function () {
        return  {
            name   :'iconCls',
            dataFn :function (item) {				
                return me.getStatusIconCls(item.data.status);
            },
            visible:false
        }
    },

    //Template for the status icon and text
    statusIconHeader2:function () {
        return  {
            name  :'status',
            dataFn:function (item) {
                return me.capitalize(item.get('status'));
            },
            tpl   :"<div class='icon {iconCls}' style='position: absolute;'>&nbsp;</div> <div style='margin-left: 18px;'>{status}</div>"
        }
    },

    //Used to build tpl based upon given attributes for the object
    _buildTpl        :function () {

        var itemCount = 0;
        var overwriteStyle = "";
		var item = this.item;
        var descr = this.item.get('description');
        var tpl = "<ul class='info-pane-stats'>\n" +
            '<tpl if="description &amp;&amp; description.length&gt;0">\n' +
            "<li title='" + descr + "' style='width: 20em; border-left: 0px;'><span class='ips-top'>{description}</span></li>\n" +
            '</tpl>\n';


        Ext.each(this.fields, function (field) {
            overwriteStyle = (itemCount > 0) ? "padding-left:15px !important;" : "border-left: 0px;";
            itemCount++;


            if (field.visible !== false) {
                var subTpl = field.tpl || '{' + field.name + '}';
                var title = field.title || me.capitalize(field.name);
                var tooltip = '';
				
                var item_value = item.get(field.name);
                if (item_value) {
                    var len = item_value.length;
                    if (len > 60)
                        tooltip = item_value;
                }

                tpl += Ext.String.format("<tpl if=\"{2}\"><li title=\"{4}\" style=\"{3}\">" +
                    "<span class='ips-top'>{0}</span> " +
                    "<span class='ips-bottom'>{1}</span>" +
                    "</li></tpl>\n",
                    title, subTpl, field.name, overwriteStyle, tooltip);

            }
        });

        tpl += "</ul>";
        return new Ext.XTemplate(tpl);
    },

    _asyncUpdate:function (name, value) {
        var newData = this.clone(this.tmpData);
        newData[name] = value;
        this.tmpData = newData;
        this.update(newData);
    },

    //Loads the data for attributes provided with object into the tpl
    loadData    :function () {
        var me = this;
		var item = this.item;
        var data = {};
        Ext.each(this.fields, function (field) {
            var name = field.name || Ext.Error.raise("InfoPaneSubheader fields must have a 'name' attribute.");
            var value;
            if (field.dataFn) {
                if (field.async) {
                    value = "Loading...";
                    field.dataFn(item, function (value) {
                        me._asyncUpdate.apply(me, [name, value]);
                    });
                } else {
                    value = field.dataFn(item);
                }
            } else {
                value = item.get(name);
            }

            data[name] = value.length > 30 ? value.substr(0, 30) + '.....' : value;

        });
        var description = item.get('description') ? item.get('description') : '';
        data.description = description.length > 60 ? description.substr(0, 60) + '.....' : description;
        this.tmpData = data;
        return data;
    },

    reload:function () {
        this.update(this.loadData());
    }
});