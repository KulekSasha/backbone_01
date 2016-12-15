var userApp = userApp || {};

$(function () {
    // render table with users
    userApp.UsersView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#users-template').html()),

        initialize: function () {
            console.log("initialize in UsersView");
            this.listenTo(userApp.userList, 'sync', this.render);
            this.render();
        },
        render: function () {
            this.$el.empty().html(this.template());
            this.addAll();
        },
        addAll: function () {
            this.$('#user-list').html(''); // clean the list
            userApp.userList.each(this.addOne, this);
        },
        addOne: function (user) {
            var view = new userApp.UserView({model: user});
            $('#user-list').append(view.render().el);
        },
        destroy: function () {
            console.log("destroy UsersView");
            this.$el.empty().off();
            this.stopListening();
        },

        // event handlers
        events: {
            'click .edit': 'editUser', //not implemented yet
            'click .delete': 'deleteUser',
            'click #nav-create-new': 'createUser',
        },
        editUser: function (evt) {
            console.log("edit in UsersView invoked, login: " + evt.target.getAttribute("value"));
            this.destroy();
            evt.preventDefault();
            let login = evt.target.getAttribute("value");
            userApp.router.navigate('edit/' + login, {trigger: true});
        },
        deleteUser: function (evt) {
            console.log("deleteUser in UsersView invoked");
            var login = evt.target.value;
            var userToDelete = userApp.userList.findWhere({login: login});

            $("#dialog-confirm").dialog({
                resizable: false,
                height: "auto",
                width: 400,
                modal: true,
                buttons: {
                    "Delete": function () {
                        if (userToDelete) {
                            userToDelete.destroy({
                                url: "http://localhost:8080/lab-22-backbone/api/rest/users/" +
                                userToDelete.get('login'),
                                wait: true,
                                dataType: "text",
                                success: function (model) {
                                    this.$("#" + model["id"]).remove();
                                }
                            });
                        }
                        $(this).dialog("close");
                    },
                    "Cancel": function () {
                        $(this).dialog("close");
                    }
                }
            });
        },
        createUser: function (evt) {
            console.log("createUser in UsersView invoked: " + evt.target.getAttribute("href"));
            this.destroy();
            evt.preventDefault();
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
    });
});
