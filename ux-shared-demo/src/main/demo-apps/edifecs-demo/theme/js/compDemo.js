Ext.onReady(function() 
{
       Ext.create('Ext.tab.Panel', 
	   {
           width	: 750,
		   height	: 600,
           activeTab: 0,
           plain	: true,
           cls		: 'topFix',
           items	: [
						{
							title: 'Preview',
							bodyPadding: 10,
							html : '<iframe scrolling="auto" frameborder="0" style="width: 100%; height:100%;" src="../../edifecs-demo/button-group.html" id="tableau-view" name="tableau-view"></iframe>'
						},
						{
							title: 'Layout JavaScript',
							html: '<iframe scrolling="auto" frameborder="0" style="width: 100%; height:100%;" src="bg-html.html" id="tableau-view" name="tableau-view"></iframe>'
						},
						{
							title: 'Component JavaScript',
							html : '<iframe scrolling="auto" frameborder="0" style="width: 100%; height:100%;" src="../../extjs/edifecs-plugins/edifecs-buttongroup.js" id="tableau-view" name="tableau-view"></iframe>'
						},
						{
							title		:	'Loading via Ajax',
							html		:	'<pre id="ajaxload" style="padding:10px;"></pre>',
							autoScroll	:	true,
							listeners	:	{
												activate	: function()
												{
													Ext.Ajax.request({
														url		: '../../extjs/edifecs-plugins/edifecs-buttongroup.js',
														success	: function(response, opts) 
														{											
															Ext.get("ajaxload").update(Ext.htmlEncode(response.responseText));
														},
														failure	: function(response, opts) 
														{
															console.log('server-side failure with status code ' + response.status);
														}
													});
												}
											}
						}
					],
           renderTo : document.getElementById('tabs')
		});
});


