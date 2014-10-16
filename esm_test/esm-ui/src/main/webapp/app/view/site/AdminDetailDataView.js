Ext.define('Security.view.site.AdminDetailDataView', {
    extend:'Ext.view.View',
    alias:'widget.admindetaildataview',
    initComponent : function() {
        var me = this;
        this.store = Ext.create('Ext.data.Store', {
            fields:['id','name','email','userId','password','adminType']
        });
        this.tpl = new Ext.XTemplate(
            '<tpl for=".">' +
                '<div>' +
                    '<div class="adminDetailContainer">' +
                        '<div class="adminDetailHeader">{name}</div>' +
                        '<div class="arrowContainer">' +
                            '<div class="adminType adminType-{adminType}">{adminType}</div>' +
                            '<div id="arrow-{id}" class="arrow collapse-arrow-down"></div>' +
                        '</div>' +
                    '</div>' +
                    '<div id="detail-arrow-{id}" class="extraDetailContainer hideButton">' +
                                '<div class="adminDetailSeperator">{email}</div>' +
                                '<div class="adminDetailSeperator">{userId}</div>' +
                                '<div class="adminDetailSeperator" style="padding: 5px 0px 20px; 0px;" class="passwordRow" >' +
                                    '<div class="passwordContainer">{password:this.formatPassword}</div>' +
                                    '<div class="passwordOptions">' +
                                        '<div class="show-password"><div class="{password}"></div></div>' +
                                        '<div class="generate-password"><a href="#">Generate New</a></div>' +
                                    '</div>' +
                                '</div>' +
                    '</div>' +
                '</div>' +
                '</tpl>',{
                formatPassword : function(password){
                    return  password.replace(/./gi, "*");
                }
            }
        );
        this.listeners = {
            'boxready':function () {
                var me=this;
                this.getEl().on('click', function(e, t, opts) {
                    e.stopEvent();
                   // var arrow = me.getEl().dom.getElementsByClassName('arrow')[0];
                    $( '#detail-'+t.id ).toggle( "down" );
                    $( '#'+t.id ).toggleClass( "collapse-arrow-up" );

                }, null, {delegate: '.arrow'});

                this.getEl().on('click', function(e, t, opts) {
                    e.stopEvent();
//                    var passwordRow = me.getEl().dom.getElementsByClassName('passwordContainer')[0];
//                    var password=t.childNodes[0].getAttribute('class');
//                    passwordRow.innerHTML=password;

                }, null, {delegate: '.show-password'});


                this.setAddresses();
            }
        };
        this.callParent(arguments);
    },

    setAddresses : function() {
        var view = this;
        var data = [
            {'id':1,'name':'Joe Smith','email':'joe.smith@ast.com','userId':'joe.smith','password':'123456','adminType':'PRIMARY'},
            {'id':2,'name':'will Bruce','email':'will.bruce@ast.com','userId':'will.bruce','password':'123456','adminType':'SECONDARY'}
        ];
        this.getStore().loadData(data);
//            view.update(view.tpl.apply(data));

    }
});