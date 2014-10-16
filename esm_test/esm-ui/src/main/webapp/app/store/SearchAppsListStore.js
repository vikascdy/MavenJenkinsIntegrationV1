Ext.define('Security.store.SearchAppsListStore', {
    extend  :'Ext.data.Store',
    model   :'Security.model.AppsListModel',
    data:[
			{
				id:1,
			    name:'CNC',
			    description:'Edifecs CNC provides a secure end-to-end view of the healthcare transaction lifecycle to internal users and trading' +
			        'partners. It enables better business decision-making, monitoring of performance against internal and external performance metrics.',
			    version:'9.0.1',
			    releaseDate:'06/15/2014',
			    publishedBy:'Edifecs',
			    rating:'3',
			    pendingRequest:0,
			    acceptedRequest:4
			},
          {
				id:2,
              name:'Transaction Management',
              description:'Edifecs Transaction Management (TM) provides a secure end-to-end view of the healthcare transaction lifecycle to internal users and trading' +
                  'partners. It enables better business decision-making, monitoring of performance against internal and external performance metrics.',
              version:'9.0.1',
              releaseDate:'06/15/2014',
              publishedBy:'Edifecs',
              rating:'3',
              pendingRequest:0,
              acceptedRequest:4
          },
          {
        	  id:3,
              name:'Trading Partner Management',
              description:'Edifecs Trading Partner Management (TPM) provides a secure end-to-end view of the healthcare transaction lifecycle to internal users and trading' +
                  'partners. It enables better business decision-making, monitoring of performance against internal and external performance metrics.',
              version:'9.0.1',
              releaseDate:'06/15/2014',
              publishedBy:'Edifecs',
              rating:'4',
              pendingRequest:0,
              acceptedRequest:2
          },
          {
        	  id:4,
              name:'Enrollment Management',
              description:'Edifecs Enrollment Management (EM) provides a secure end-to-end view of the healthcare transaction lifecycle to internal users and trading' +
                  'partners. It enables better business decision-making, monitoring of performance against internal and external performance metrics.',
              version:'9.0.1',
              releaseDate:'06/15/2014',
              publishedBy:'Edifecs',
              rating:'4',
              pendingRequest:1,
              acceptedRequest:2
          },
          {
        	  id:5,
              name:'Claims Lifecycle Management',
              description:'Edifecs Claims Lifecycle Management (CLM) provides a secure end-to-end view of the healthcare transaction lifecycle to internal users and trading' +
                  'partners. It enables better business decision-making, monitoring of performance against internal and external performance metrics.',
              version:'9.0.1',
              releaseDate:'06/15/2014',
              publishedBy:'Edifecs',
              rating:'3',
              pendingRequest:4,
              acceptedRequest:2
              
          }
    ]
});

