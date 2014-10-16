Ext.define('DD.controller.ParameterController', {
    extend: 'Ext.app.Controller',
    views:[
        'parameters.SingleSliderControl',
        'parameters.MultiSliderControl',
        'parameters.ComboBoxControl',
        'parameters.NumberFieldControl',
        'parameters.CheckBoxGroupControl',
        'parameters.ParameterToolbar',
        'parameters.ParametersConfigurationForm',
        'parameters.ParametersConfigurationWindow'
    ],
    models:[
        'ParametersListModel'
    ],
    stores:[
        'ParameterToolbarStore'
    ],
    refs: [
        {
            ref: 'ParameterToolbar',
            selector: 'parametertoolbar'
        }
    ],
    init :function() {
        this.control({
            'parametersconfigurationwindow #applyChanges':{
                click : this.applyChangesForParameters
            }
        });
    },
    applyChangesForParameters : function(btn) {
        var window = btn.up('window');
        var parameterControl = window.parameter;
        var parameterControlHolder = window.parameterControlHolder;

        var form = window.down('form').getForm();
        var parameterField = window.down('form').down('#parameterField');
        if (form.isValid()) {
            parameterControl.updateConfiguration(form.getValues(), parameterField.valueModels, function() {
                parameterControlHolder.setConfigObj(form.getValues());
                parameterControlHolder.setParameterConfig(parameterField.valueModels);
                parameterControlHolder.setIsParameterConfigured(true);
                window.close();
            });
        }
        else {
            Ext.Msg.alert('Parameter Error', 'Please specify complete information.');
        }

    }
});