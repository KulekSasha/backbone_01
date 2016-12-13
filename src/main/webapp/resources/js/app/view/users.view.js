var userApp = userApp || {};

$(function () {
    // render table with users
    userApp.UsersView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#users-template').html()),

        initialize: function () {
            console.log("initialize in UsersView");
            // userApp.userList.on('add', this.addOne, this);
            // userApp.userList.on('reset', this.addAll, this);
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
            this.undelegateEvents();
            this.unbind();
            this.$el.empty();
        },

        // event handlers
        events: {
            'click .edit': 'editUser', //not implemented yet
            'click .delete': 'deleteUser',
            'click #nav-create-new': 'createUser',
        },
        editUser: function (evt) {
            var val = $(evt.currentTarget).val();
            console.log(val);
        },
        deleteUser: function (evt) {
            console.log("event-handler deleteUser invoked");
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
            console.log("create in appView: " + evt.target.getAttribute("href"));
            console.log("prevent def");
            this.destroy();
            evt.preventDefault();
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
    });
});
