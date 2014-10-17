Ext.define('ExtThemeEdifecs.grid.Panel', {
    override		:  'Ext.grid.Panel',
	scrollPane      :  '',
	bodyCls			:  'scrollerPane',
	afterComponentLayout :	function()
	{
		var me = this;
		var gridId = me.getId();
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
				me.scrollPane = $('#'+gridId+' > div.scrollerPane');
				me.scrollPane.jScrollPane(settings);
			}

			var view = $('#'+gridId+' > div.scrollerPane div.x-grid-view').detach();
			var jspContainer = $('#'+gridId+' > div.scrollerPane .jspContainer').detach();
			$('#'+gridId+' > div.scrollerPane').append(jspContainer);
			$('#'+gridId+' > div.scrollerPane .jspPane').append(view);
			$('#'+gridId+' > div.scrollerPane div.x-grid-view').css('overflow', 'hidden');
			$('#'+gridId+' > div.scrollerPane div.x-grid-view').css('height', 'auto');
		});
		this.callParent(arguments);
        return;
       }
	}
});