Ext.define('DD.view.core.ImageGallery', {
    extend:'Ext.view.View',
    alias :'widget.imagegallery',
    style:"backgroundColor:#ffffff",
    trackOver: true,
    overItemCls: 'gallery-wrap-over',
    selectedItemCls: 'selected-gallery-wrap',
    itemSelector: 'div.gallery',
    autoScroll:true,
    initComponent : function() {
        this.store = Ext.create('DD.store.ImageGalleryStore');
        this.tpl = [
            '<tpl for=".">',
            '<div class="gallery-wrap">',
            '<div class="gallery"><img src="{url}" title="{name} - {description}" /></div>',
            '</div>',
            '</tpl>'];

        this.callParent(arguments);
    }

});