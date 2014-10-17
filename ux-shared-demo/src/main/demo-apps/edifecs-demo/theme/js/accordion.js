Ext.onReady(function() {
Ext.create('Ext.panel.Panel', {
    title: 'Title',
    width: 650,
    height: 300,
    layout:'accordion',
    componentCls: 'removeBG',
    defaults: {
        // applied to each contained panel
        bodyStyle: 'padding:15px'
    },
    layoutConfig: {
        // layout-specific configs go here
        titleCollapse: false,
        animate: true,
        activeOnTop: true
    },
                                                                items: [{
                                                                    title: 'Unified Testing',
                                                                    html: 'The Edifecs Unified Testing solution is a set of products and services that support internal and external healthcare transaction testing. Unified Testing includes systems to create and manage test data, test activity monitoring and reporting, and self-service testing portals for trading partners. <br><br>\
Edifecs Unified Testing allows for internal and external testing before a new transaction, business rule or scenario is rolled out in a production environment. It allows organizations to reduce testing implementation time and costs, improve transaction quality, and streamline trading partner support, while improving partner satisfaction.         \
'
                                                                },{
                                                                    title: 'Unified Compliance',
                                                                    html: 'The Edifecs Unified Compliance solution provides a channel-agnostic, centralized, single front-end approach to validate, split, route and translate healthcare transactions. It improves transaction workflow and ensures ongoing compliance of production information exchanged with business partners and between business systems. By moving trading partner-specific edits to the front-end, Edifecs Unified Compliance rejects claims with errors before they enter adjudication systems and negatively impact transaction first-pass rates. It includes both design-time and run-time tools to ensure consistency and accuracy through design, testing and production execution.'
                                                                },{
                                                                    title: 'Unified Visibility',
                                                                    html: 'The Edifecs Unified Visibility solution provides a comprehensive, enterprise-wide system to find, view, analyze, monitor and report on healthcare transactions. For example, from the moment an electronic claim is received, provider-relations support staff can track its progress through front-end and back-end systems, along with any updates or messages related to the transaction.<br><br>   \
Edifecs Unified Visibility provides real-time insight into transaction activity, from initiation to fulfillment, across all information processing channels. Edifecs Unified Visibility services claims management, provider relations, customer support and EDI users with reports, dashboards and analysis systems. The solution also supports self-service capabilities for trading partners with a portal to view the partner-specific transaction details, generate reports and troubleshoot transaction-related problems.   \
'
                                                                }],
    renderTo: document.getElementById('accordion')
});
});


