Ext.define('Edifecs.WidgetUtils', {
    statics: {

        //Utility function that walks the dom until
        //it finds the enclosing EXT component.
        findComponentByElement: function(node) {
            var topmost = document.body,
                target = node,
                cmp;

            while (target &&  target !== topmost) {
                cmp = Ext.getCmp(target.id);

                if (cmp) {
                    return cmp;
                }

                target = target.parent();
            }

            return null;
        }


    }



});