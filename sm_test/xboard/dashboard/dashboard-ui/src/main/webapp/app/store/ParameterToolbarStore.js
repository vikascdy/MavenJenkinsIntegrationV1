Ext.define('DD.store.ParameterToolbarStore', {
    extend  :'Ext.data.Store',
    model   :'DD.model.ParametersListModel',
    data:[
        {
            text:'Radio',
            iconCls:'radio',
            controlType:'singleslidercontrol'
        },
        {
            text:'Checkbox',
            iconCls:'checkbox',
            controlType:'checkboxgroupcontrol'
        },
        {
            text:'Button',
            iconCls:'button',
            controlType:'singleslidercontrol'
        },
        {
            text:'Single Horizontal Slider',
            iconCls:'h-slider',
            controlType:'singleslidercontrol'
        },
//        {
//            text:'Single Vertical Slider',
//            iconCls:'v-slider',
//            controlType:'singleverticalslidercontrol'
//        },
        {
            text:'Multi Horizontal Slider',
            iconCls:'h-slider',
            controlType:'multislidercontrol'
        },
//        {
//            text:'Multi Vertical Slider',
//            iconCls:'v-slider',
//            controlType:'multiverticalslidercontrol'
//        },
        {
            text:'Date',
            iconCls:'date',
            controlType:'singleslidercontrol'
        },
        {
            text:'Time',
            iconCls:'time',
            controlType:'singleslidercontrol'
        },
        {
            text:'Text Field',
            iconCls:'textfield',
            controlType:'singleslidercontrol'
        },
        {
            text:'Combo Box',
            iconCls:'combobox',
            controlType:'comboboxcontrol'
        },
        {
            text:'Number Field',
            iconCls:'numfield',
            controlType:'numberfieldcontrol'
        },
        {
            text:'List',
            iconCls:'list',
            controlType:'singleslidercontrol'
        }
    ]
});
