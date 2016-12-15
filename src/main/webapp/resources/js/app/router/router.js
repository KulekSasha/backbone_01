var userApp = userApp || {};

$(function () {
    userApp.Router = Backbone.Router.extend({
        initialize: function (opts) {
            this.users = opts.users;
        },
        routes: {
            "": "list",
            "!/:login": "edit",
            "create": "create",
        },
        list: function () {
            this.users.fetch();
            userApp.usersView = new userApp.UsersView;
        },
        edit: function (login) {
            userApp.userEditView = new userApp.UserEditView({login: login});
        },
        create: function () {
            userApp.userCreateView = new userApp.UserCreateView();
        },
    });
});
