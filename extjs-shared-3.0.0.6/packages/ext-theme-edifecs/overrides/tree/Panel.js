Ext.define('ExtThemeEdifecs.tree.Panel', {
    override		:  'Ext.tree.Panel',
	scrollPane      :  "",
	bodyCls			:  'scrollerPane',
	afterComponentLayout :	function()
	{
		var me = this;
		var treeId = me.getId();
		if (window.jQuery)
		{
            $(function() {
                var settings = {
                    showArrows: true,
                    autoReinitialise: true,
                    mouseWheelSpeed: 50,
                    maintainPosition:true,
                    stickToBottom:true
                };

                if (!me.scrollPane) {
                    me.scrollPane = $('#'+treeId+' > div.scrollerPane');
                    me.scrollPane.jScrollPane(settings);
                }

                var view = $('#'+treeId+' > div.scrollerPane div.x-tree-view').detach();
                var jspContainer = $('#'+treeId+' > div.scrollerPane .jspContainer').detach();
                $('#'+treeId+' > div.scrollerPane').append(jspContainer);
                $('#'+treeId+' > div.scrollerPane .jspPane').append(view);
                $('#'+treeId+' > div.scrollerPane div.x-tree-view').css('overflow', 'hidden');
                $('#'+treeId+' > div.scrollerPane div.x-tree-view').css('height', 'auto');
            });
		this.callParent(arguments);
        return;
        }
	}
});