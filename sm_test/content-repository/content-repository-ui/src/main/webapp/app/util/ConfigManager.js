// CONFIGMANAGER.JS
// Handles loading and saving configuration files.
// ----------------------------------------------------------------------------

Ext.define('Util.ConfigManager', {}); // Placeholder, ignore this.

window.ConfigManager = {
	config : null,
	usingDefaultConfig : false,
	validationErrors : [],
	serviceTypesRequired : null,
	product : null,
	totalConfigCount : 0,
	productName : null,

	loadConfigIfNecessary : function(callback) {
		if (!ConfigManager.config)
			ConfigManager.loadDefaultConfig(callback);
		else if (callback)
			callback();
	},

	getEnvironmentName : function(callback, scope) {
		Functions.jsonCommand("UI Service", "getEnvironment", {}, {
			success : function(response) {
				if (response)
					SM.environmentName = response;
				else
					SM.environmentName = 'Not Available';
				Ext.callback(callback, scope, []);
			},
			failure : function(response) {
				SM.environmentName = 'Not Available';
				Ext.callback(callback, scope, []);
			}
		});
	},

	loadDefaultConfig : function(callback, scope) {
		Log.info('Requesting default config from server.');
		Functions
				.jsonCommand(
						"UI Service",
						"config.current",
						{},
						{
							success : function(response) {
								ConfigManager.usingDefaultConfig = true;
								ConfigManager.parseConfig(response);
								ConfigManager.updateStatuses(true, callback,
										scope);
							},
							failure : function(response) {
								Functions
										.errorMsg(Ext.String
												.format(
														"Failed to load config: {0} - {1}.\n<br />Please refresh the page.",
														response.errorClass,
														response.error));
							}
						});
	},

	loadSavedConfig : function(name, version, callback, scope) {
		Log.info("Requesting config '{0}', version {1}, from server.", name,
				version);
		Functions
				.jsonCommand(
						"UI Service",
						"config.load",
						{
							name : name,
							version : version
						},
						{
							success : function(response) {
								ConfigManager.usingDefaultConfig = false;
								ConfigManager.parseConfig(response);
								ConfigManager.updateStatuses(true, callback,
										scope);
							},
							failure : function(response) {
								Functions
										.errorMsg(Ext.String
												.format(
														"Failed to load config: {0} - {1}.\n<br />Please refresh the page.",
														response.errorClass,
														response.error));
							}
						});
	},

	loadConfigFromUrl : function(path) {
		Ext.define('configModel', {
			extend : 'Ext.data.Model',
			fields : [ {
				name : 'name',
				type : 'string'
			}, {
				name : 'version',
				type : 'string'
			}, {
				name : 'active',
				type : 'boolean'
			} ]
		});
		var config = Ext.create('configModel', {
			name : path[0].replace(/%20/g, ' '),
			version : path[1].replace('%20', ' '),
			active : path[2].replace('%20', ' ') === undefined ? false
					: path[2].replace('%20', ' ')
		});
		SM.loadAndViewConfig(config);
	},

	loadTemplate : function(name, callback, scope) {
		Log.info("Requesting template config '{0}' from server.", name);
		Functions
				.jsonCommand(
						"UI Service",
						"config.templateLoad",
						{
							filename : name
						},
						{
							success : function(response) {
								ConfigManager.usingDefaultConfig = false;
								ConfigManager.parseConfig(response);
								ConfigManager.updateStatuses(true, callback,
										scope);
							},
							failure : function(response) {
								Functions
										.errorMsg(Ext.String
												.format(
														"Failed to load template: {0} - {1}.\n<br />Please refresh the page.",
														response.errorClass,
														response.error));
							}
						});
	},

	clearConfig : function() {
		ConfigManager.config = null;
		ConfigManager.usingDefaultConfig = false;
		ConfigManager.validationErrors = [];
	},

	checkConfigList : function(callback) {
		var totalConfigCount = 0;
		Functions.jsonCommand("UI Service", "config.list", {}, {
			success : function(response) {
				totalConfigCount = response.length;
				ConfigManager.totalConfigCount = totalConfigCount;
				if (ConfigManager.totalConfigCount < 1)
					NavigationManager.showCreateConfigurationPage();
				else
					Ext.callback(callback, this, []);
			}
		});
		Ext.callback(callback, this, []);
	},

	saveConfig : function(callback) {
		ConfigManager.config.timestamp();
		var jsonData = ConfigManager.config.toJSON();
		var loadingWindow = Ext.widget('progresswindow', {
			text : 'Saving configuration...'
		});

		Functions
				.jsonCommand(
						"UI Service",
						"config.save",
						{
							name : ConfigManager.config.get('name'),
							version : ConfigManager.config.get('version'),
							config : jsonData
						},
						{
							success : function(response) {
								loadingWindow.destroy();
								Ext.Msg.alert("Save Confirmation",
										"Configuration saved successfully.");
								SM.changesSavedStatus = false;
								Ext.getStore('SavedConfigStore').load();
								SM.reloadAll();
								Ext.callback(callback);
							},
							failure : function(response) {
								loadingWindow.destroy();
								Functions
										.errorMsg("Failed to save config file. Error returned was: "
												+ response.error);
								Ext.callback(callback);
							}
						});
	},

	applyConfig : function(callback, scope) {
		var jsonData = ConfigManager.config.toJSON();
		var loadingWindow = Ext.widget('deploymentprogresswindow');
		// The following setup allows future developers to add more "steps" to
		// the DeploymentProgressWindow's progress readout. Each function in
		// the stepFuncs array, if it calls nextStep when it is done, will be
		// called repeatedly until it advances "step", in which case it will
		// update the step shown on the window and advance to the next function
		// in the array, if one exists.
		/*
		 * if (!SM.testMode) { var step = 1; var nextStep = function() {
		 * setTimeout(function() { if (step != loadingWindow.stage)
		 * loadingWindow.setStage(step); var s = stepFuncs[step - 1]; if (s) {
		 * s(); } }, 200); }; var stepFuncs = [ function() { // 1 // TODO: Need
		 * to check for a way to determine proper shutdown setTimeout(function() {
		 * step++; nextStep(); }, 10000); }, function() { // 2
		 * ConfigManager.checkIfConnected(function(connected) { if (connected)
		 * step++; nextStep(); }); }, function() { // 3 setTimeout(function() {
		 * step++; nextStep(); }, 10000); } ]; }
		 */

		Functions.jsonCommand("UI Service", "config.apply", {
			config : jsonData
		}, {
			success : function(response) {
				loadingWindow.setFinished();
				ConfigManager.usingDefaultConfig = true;
				Ext.callback(callback, scope);
				ConfigManager.updateStatuses(true, SM.reloadAll);
			},
			failure : function(response) {
				loadingWindow.destroy();
				Functions.errorMsg("An error occurred while deploying: "
						+ response.error);
			}
		});
	},

	validateConfig : function(callback, scope, hideLoadingWindow) {
		if (!hideLoadingWindow)
			var loadingWindow = Ext.widget('progresswindow', {
				text : 'Validating configuration...'
			});
		Functions
				.jsonCommand(
						"UI Service",
						"config.validate",
						{
							config : ConfigManager.config.toJSON()
						},
						{
							success : function(response) {
								if (!hideLoadingWindow)
									loadingWindow.destroy();
								var errors = [];
								Ext.each(response, function(err) {
									errors.push(Ext.create('SM.model.ErrorLog',
											err));
								});
								ConfigManager.validationErrors = errors;
								if (errors.length > 0 && !hideLoadingWindow) {
									Functions
											.errorMsg(
													Ext.String
															.format(
																	"Validation returned {0} {1}. To clear the {1}, run another validation after resolving {2}.",
																	errors.length,
																	errors.length == 1 ? 'error'
																			: 'errors',
																	errors.length == 1 ? 'it'
																			: 'them'),
													"Validation Failed");
								} else {
									Ext.callback(callback, scope);
								}
								SM.reloadAll();
							},
							failure : function(response) {
								if (!hideLoadingWindow)
									loadingWindow.destroy();
								Functions
										.errorMsg("An error occurred while validating: "
												+ response.error);
							}
						});
	},

	deleteConfig : function(configName, version, callback, scope) {
		var loadingWindow = Ext.widget('progresswindow', {
			text : 'Deleting configuration...'
		});
		Functions.jsonCommand("UI Service", "config.delete", {
			name : configName,
			version : version
		}, {
			success : function(response) {
				loadingWindow.destroy();
				Ext.callback(callback, scope);
				Ext.getStore('SavedConfigStore').load();
			},
			failure : function(response) {
				loadingWindow.destroy();
				Functions.errorMsg("An error occurred while deleting: "
						+ response.error);
			}
		});
	},

	renameConfig : function(oldName, newName, version, callback, scope) {
		var loadingWindow = Ext.widget('progresswindow', {
			text : 'Renaming configuration...'
		});
		Functions.jsonCommand("UI Service", "config.rename", {
			oldname : oldName,
			newname : newName,
			version : version
		}, {
			success : function(response) {
				loadingWindow.destroy();
				Ext.callback(callback, scope);
				Ext.getStore('SavedConfigStore').load();
			},
			failure : function(response) {
				loadingWindow.destroy();
				Functions.errorMsg("An error occurred while renaming: "
						+ response.error);
			}
		});
	},

	parseConfig : function(config) {
		// Before parsing a configuration, load the ProductStore and
		// ResourceTypeStore.
		// (This is as good a place as any to do it.)
		var productStore = Ext.getStore('ProductStore');
		if (productStore)
			productStore.load();

		var resourceTypeStore = Ext.getStore('ResourceTypeStore');
		if (resourceTypeStore)
			resourceTypeStore.load();

		Log.debug("Parsing config...");

		console.log("Product name: " + config.productName);
		ConfigManager.getCurrentProduct(config.productName, function(prod) {
			ConfigManager.product = prod;
		}, this);

		Log.debug("Building config object...");
		console.log(config);
		ConfigManager.config = Ext.create('SM.model.Config', config);
		Log.debug("Normalizing config object...");
		ConfigManager.config.normalize();

		ConfigManager.validationErrors = [];
		Log.info("Successfully loaded config file '{0}', version {1}.",
				ConfigManager.config.get('name'), ConfigManager.config
						.get('version'));
	},

	exportConfig : function(callback, scope) {
		// var loadingWindow = Ext.widget('progresswindow', {
		// text: 'Exporting configuration...'
		// });
		var config = ConfigManager.config;
		var jsonData = config.toJSON();
		Functions.jsonCommand("UI Service", "config.exportExtension", {}, {
			success : function(extension) {
				Functions.jsonCommandDownload("UI Service",
						"config.exportFile", config.getIdSegment() + '.'
								+ extension, {
							config : ConfigManager.config.toJSON()
						});
			}
		});
	},

	searchConfigById : function(firstId) {
		if (ConfigManager.config === null)
			return undefined;
		var recursiveSearch = function(item, id) {
			if (!id)
				return null;
			var parts = id.split(':');
			var name = parts[0];
			if (item.getIdSegment() == name) {
				if (parts.length === 1)
					return item;
				var found = null;
				var newId = "";
				for ( var i = 1; i < parts.length; i++) {
					if (i > 1)
						newId += ':';
					newId += parts[i];
				}
				item.eachChild(function(subItem) {
					found = found || recursiveSearch(subItem, newId);
					return found === null;
				});
				return found;
			} else {
				return null;
			}
		};
		return recursiveSearch(ConfigManager.config, firstId);
	},

	isNameTaken : function(name, item) {
		item = item || ConfigManager.config;
		if (!item)
			return false;
		var found = false;
		item.eachChild(function(child) {
			found = found || (child.get('name') == name)
					|| this.isNameTaken(name, child);
		}, this);
		return found;
	},

	getNextAvailableName : function(objectOrName) {
		var name = Functions.getStringOrName(objectOrName);
		var num = 1, currentName = name;
		while (this.isNameTaken(currentName)) {
			currentName = name + ' ' + num;
			num++;
		}
		return currentName;
	},

	getNextAvailableIncrementedName : function(objectOrName) {
		var name = Ext.String.trim(Functions.getStringOrName(objectOrName));

		var len = name.length;
		var count = "";
		var ch = Ext.Number.from(Ext.util.Format.substr(name, len - 1, 1),
				"invalid");
		while (len > 0 && ch != "invalid") {
			ch = Ext.Number.from(Ext.util.Format.substr(name, len - 1, 1),
					"invalid");
			if (ch != "invalid") {
				count = ch + count;
				len--;
			}
		}
		count = Ext.Number.from(count, 0).valueOf() + 1;
		var currentName = Ext.util.Format.substr(name, 0, len) + count;

		while (this.isNameTaken(currentName)) {
			count = Ext.Number.from(count, 0).valueOf() + 1;
			currentName = Ext.util.Format.substr(name, 0, len) + count;
		}
		return currentName;
	},

	getConfigErrors : function(item) {
		var errors = [];
		item = item || ConfigManager.config;
		Ext.each(item.getErrors(), function(e) {
			errors.push(e);
		});
		item.eachChild(function(child) {
			Ext.each(ConfigManager.getConfigErrors(child), function(e) {
				errors.push(e);
			});
		});
		return errors;
	},

	getValidationErrors : function() {
		return ConfigManager.validationErrors || [];
	},

	updateStatuses : function(firstTime, callback, scope) {
		Functions.jsonCommand("UI Service", "config.statuses", {}, {
			success : function(response) {
				ConfigManager.postUpdateStatuses(response, firstTime,callback,scope);
			},
			failure : function(response){
			}
		});
	},

	postUpdateStatuses : function(data, firstTime, callback, scope) {
		var items = ConfigManager.config.getChildrenRecursively();
		Ext.each(items, function(item) {
			var status = data[item.getId()];
			if (status)
				item.set('status', status);
			else if (item.getType() != 'Cluster') {
				if (item.get('status') != 'new') {
					item.set('status', 'offline');
				}
			}
		});
		Ext.callback(callback, scope);
	},

	// Return a map of available service types for services in config object
	getAvailableServiceTypes : function() {

		var configuration = ConfigManager.config;
		var clusters = configuration.get('clusters');
		var availableServiceTypes = new Ext.util.HashMap();

		Ext.each(clusters, function(value) {
			var servers = value.get('servers');

			Ext.each(servers, function(value) {
				var nodes = value.get('nodes');

				Ext.each(nodes, function(value) {
					var services = value.get('services');

					if (services.length > 0) {
						Ext.each(services, function(value) {
							availableServiceTypes.add(value.getServiceType()
									.get('name'), value.getServiceType().get(
									'name'));
						});
					}
				});
			});
		});
		return availableServiceTypes;
	},

	// Returns a map having all service types from META file related to the
	// product,
	// which are required to have at least one instance on any machine
	getRequiredServiceTypes : function() {

		var requiredServiceTypes = new Ext.util.HashMap();

		var product = ConfigManager.config.getProduct();
		// console.log(product);
		var serviceType = null;

		if (product)
			serviceType = product.get('serviceTypes');
		else {
			ConfigManager.getCurrentProduct('', function(prod) {
				serviceType = prod.serviceTypes;
			}, this);
		}

		if (serviceType) {
			Ext.each(serviceType, function(value, index) {
				if (value.data.required) {
					requiredServiceTypes.add(value.data.name, 0);
				}
			});
			ConfigManager.serviceTypesRequired = requiredServiceTypes;
		}
	},

	// Used to check available service types array against required service
	// types
	// map and raise warning if required
	checkRequiredServiceTypes : function(flag, callback, scope) {

		var availableServices = ConfigManager.getAvailableServiceTypes();
		var requiredServiceTypes = ConfigManager.serviceTypesRequired;

		if (requiredServiceTypes) {
			requiredServiceTypes.each(function(key, value, length) {
				availableServices.each(function(availableService) {
					if (availableService == key) {
						requiredServiceTypes.replace(key, 1);
					}
				});
			});
			var errors = [];
			requiredServiceTypes
					.each(function(key, value, length) {
						if (value === 0) {
							var err = Ext
									.create(
											'SM.model.ErrorLog',
											{
												sourceId : key,
												type : 'Required',
												severity : 'error',
												message : 'No service instance found for service type: '
														+ key
											});
							errors.push(err);
						}
					});
			ConfigManager.serviceTypesRequired = requiredServiceTypes;
			ConfigManager.validationErrors = errors;
			if (errors.length > 0) {
				if (flag)
					Ext.widget('servicetypeerrorwindow', {
						errorList : errors
					});
				else
					return errors;
			} else
				Ext.callback(callback, scope, false);
		} else
			Functions
					.errorMsg(Ext.String
							.format("Failed to get required service type information."));
	},

	getCurrentProduct : function(productName, callback, scope) {
		var productsList = null;

		Functions.jsonCommand("UI Service", "config.products", {}, {
			success : function(response) {
				productsList = response;
			}
		});

		Ext.Ajax.on('requestcomplete', function(conn, response, options) {
			if (!Ext.Ajax.isLoading()) {
				Ext.each(productsList, function(product) {
					if (product.name == productName) {
						Ext.callback(callback, scope, [ product ]);
						return product;
					}
				});
				Ext.callback(callback, scope, []);
			}
		});
	}
};
