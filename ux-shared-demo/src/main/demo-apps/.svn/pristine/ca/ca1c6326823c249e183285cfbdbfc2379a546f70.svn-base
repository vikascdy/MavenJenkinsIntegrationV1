Ext.onReady(function() {


	     Ext.create('Ext.Button', {
             text     : 'Show Tooltip',
             id: 'button1',
             renderTo : document.getElementById('previewBox'),
             margin:10,
         });

         	Ext.create('Ext.Button', {
             text     : 'Show Preview Box',
             id: 'button2',
             renderTo : document.getElementById('previewBox'),
             margin:10
         });


        Ext.create('Ext.tip.ToolTip', {
            target: document.getElementById('button1'),
            title: '<span style="color: #990000; font-weight: bold;font-size:12px;">Error Status:</span>',
            html: '<div style="font-size:11px;padding:10px;"><span style="font-weight: bold;"> Not Available</span> <br><br><b>Details</b><br>1049EXA - You have limited permissions to view the transactions</div>'
        });

            Ext.create('Ext.tip.ToolTip', {
            target: document.getElementById('button2'),
            closable: true,
            dismissDelay: 0,
            autoHide: false,
            title: '<span style="color: #990000; font-weight: bold;font-size:12px;">Error Status:</span>',
            html: '<div style="font-size:11px;padding:10px;"><span style="font-weight: bold;"> Not Available</span> <br><br><b>Details</b><br>1049EXA - You have limited permissions to view the transactions</div>'
        });



});


