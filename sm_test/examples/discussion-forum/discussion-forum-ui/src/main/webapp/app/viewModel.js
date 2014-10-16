function DiscussionViewModel() {
    var self = this;
    self.categories = ko.observableArray();

    self.categories([
        {
            name: ko.observable('title #1'),
            description: ko.observable('description #1'),
            topics: 12,
            lastActivity: 'May 15, 2014',
            rowId: ko.observable('1')
        },
        {
            name: ko.observable('title #2'),
            description: ko.observable('description #2'),
            topics: 12,
            lastActivity: 'May 15, 2014',
            rowId: ko.observable('2')
        }
    ]);

    self.beginAdd = function() {
        alert("Add");
    }
    self.beginEdit = function(category) {
        alert("Edit: " + category.name());
    }
    self.remove = function(category) {
        alert("Remove: " + category.name());
    }

}
ko.applyBindings(new DiscussionViewModel());