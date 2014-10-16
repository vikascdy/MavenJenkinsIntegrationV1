Ext.define(
				'Security.controller.NavigationController',
				{
					extend : 'Ext.app.Controller',

					requires : [ 'Ext.util.History', 'Ext.toolbar.TextItem' ],

					config : {
						predefinedViews : {},
						defaultViewName : null,
						viewport : null,
						currentState : null
					},

					init : function() {
						Ext.util.History.init();
						Ext.util.History.on('change', this.onHistoryChange,
								this);
						this.application.on('changeurlpath', this.setNewPath,
								this);
					},

					initializeViews : function(predefinedViews, defaultViewName) {
						this.setPredefinedViews(predefinedViews);
						this.setDefaultViewName(defaultViewName);
						this.onHistoryChange(Ext.util.History.getToken());
					},


					onHistoryChange : function(token) {
						console.log(token);
						var me = this;
						var state = SessionManager.parseToken(token);
						var viewName = state['viewName'], path = state['path'];

						if (viewName == 'logout') {
							UserManager.logout(function() {
								window.location = 'login';
							}, this);

						} else {
							var previousViewName, previousPath;
							if (this.getCurrentState()) {
								previousViewName = this.getCurrentState()['viewName'];
								previousPath = this.getCurrentState()['path'];
							}
							this.setCurrentState(state);
							if (viewName == null) {
								Ext.util.History.add(this.createToken(null));
								return;
							}
							if (previousViewName == viewName) {
								var view = Ext
										.getCmp('Security-page-container')
										.getLayout().getLayoutItems()[0];
								view.fireEvent('statechange', path);
							} else if (this.getViewport()) {
								var tempName = viewName.split('?redirectURL=');
								viewName = tempName[0];
								if (this.getPredefinedViews()[viewName]) {
									var view = Ext.create(this
											.getPredefinedViews()[viewName]);
									Security.setPage(view);

									this.application.setPageTitle(view.title);
									view.fireEvent('statechange', path);
								} else {
									throw token
											+ " url fragment doesn't contain a valid view name.";
								}
							}
						}
					},

					createToken : function(state) {
						if (state == null || state['viewName'] == null) {
							return '!/' + this.getDefaultViewName();
						} else {
							var token = '!/' + state['viewName'];
							if (state['path'].length > 0) {
								token = token + '/' + state['path'].join('/');
							}
							return token;
						}
					},

					setNewPath : function(path) {
						if (Ext.isArray(path)) {
							var state = this.getCurrentState();
							state['path'] = path;
							var token = this.createToken(state);
							document.location.hash = token;
							// Ext.util.History.add(token);
						}
					}

				});
