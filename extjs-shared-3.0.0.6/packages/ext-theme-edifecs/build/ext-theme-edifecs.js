Ext.define("ExtThemeEdifecs.window.Window",{override:"Ext.window.Window",shadow:"sides",shadowOffset:35});Ext.define("ExtThemeEdifecs.form.field.Base",{override:"Ext.form.field.Base",labelSeparator:""});Ext.define("ExtThemeEdifecs.picker.Date",{override:"Ext.picker.Date",shadow:"sides",shadowOffset:8});Ext.define("ExtThemeEdifecs.grid.Panel",{override:"Ext.grid.Panel",scrollPane:"",bodyCls:"scrollerPane",afterComponentLayout:function(){var b=this;var a=b.getId();if(window.jQuery){$(function(){var d={showArrows:true,autoReinitialise:true,mouseWheelSpeed:50,maintainPosition:true,stickToBottom:true};if(!b.scrollPane){b.scrollPane=$("#"+a+" > div.scrollerPane");b.scrollPane.jScrollPane(d)}var c=$("#"+a+" > div.scrollerPane div.x-grid-view").detach();var e=$("#"+a+" > div.scrollerPane .jspContainer").detach();$("#"+a+" > div.scrollerPane").append(e);$("#"+a+" > div.scrollerPane .jspPane").append(c);$("#"+a+" > div.scrollerPane div.x-grid-view").css("overflow","hidden");$("#"+a+" > div.scrollerPane div.x-grid-view").css("height","auto")});this.callParent(arguments);return}}});Ext.define("ExtThemeEdifecs.menu.Menu",{override:"Ext.menu.Menu",shadow:"sides",shadowOffset:11,showSeparator:false});Ext.define("ExtThemeEdifecs.tree.Panel",{override:"Ext.tree.Panel",scrollPane:"",bodyCls:"scrollerPane",afterComponentLayout:function(){var a=this;var b=a.getId();if(window.jQuery){$(function(){var d={showArrows:true,autoReinitialise:true,mouseWheelSpeed:50,maintainPosition:true,stickToBottom:true};if(!a.scrollPane){a.scrollPane=$("#"+b+" > div.scrollerPane");a.scrollPane.jScrollPane(d)}var c=$("#"+b+" > div.scrollerPane div.x-tree-view").detach();var e=$("#"+b+" > div.scrollerPane .jspContainer").detach();$("#"+b+" > div.scrollerPane").append(e);$("#"+b+" > div.scrollerPane .jspPane").append(c);$("#"+b+" > div.scrollerPane div.x-tree-view").css("overflow","hidden");$("#"+b+" > div.scrollerPane div.x-tree-view").css("height","auto")});this.callParent(arguments);return}}});