var userApp = userApp || {};

$(function () {
    userApp.UsersView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#users-template').html()),

        initialize: function () {
            this.listenTo(userApp.userList, 'sync', this.render);
            this.render();
        },
        render: function () {
            this.$el.empty().html(this.template());
            this.addAll();
        },
        addAll: function () {
            this.$('#user-list').html('');
            userApp.userList.each(this.addOne, this);
        },
        addOne: function (user) {
            var view = new userApp.UserView({model: user});
            $('#user-list').append(view.render().el);
        },
        destroy: function () {
            this.$el.empty().off();
            this.stopListening();
        },

        // event handlers
        events: {
            'click .edit': 'editUser',
            'click .delete': 'deleteUser',
            'click #nav-create-new': 'createUser',
        },
        editUser: function (evt) {
            this.destroy();
            evt.preventDefault();
            let login = evt.target.getAttribute("value");
            userApp.router.navigate('!/' + login, {trigger: true});
        },
        deleteUser: function (evt) {
            let login = evt.target.value;
            let userToDelete = userApp.userList.findWhere({login: login});

            $("#dialog-confirm").dialog({
                resizable: false,
                height: "auto",
                width: 400,
                modal: true,
                buttons: {
                    "Delete": function () {
                        if (userToDelete) {
                            userToDelete.destroy({
                                url: '/api/rest/users/' + userToDelete.get('login'),
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
            this.destroy();
            evt.preventDefault();
            console.log('before route: ' + evt.target.getAttribute("href"));
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
    });
});
