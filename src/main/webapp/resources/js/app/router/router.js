var userApp = userApp || {};

$(function () {
    userApp.Router = Backbone.Router.extend({
        initialize: function (opts) {
            this.users = opts.users;
        },
        routes: {
            "": "list",
            "edit/:login": "edit",
            "create": "create",
        },
        list: function () {
            console.log("list fn in router");
            this.users.fetch();
            userApp.usersView = new userApp.UsersView;
        },
        edit: function (login) {
            console.log("edit fn in router, login:" + login);
            userApp.userEditView = new userApp.UserEditView({login: login});
        },
        create: function () {
            console.log("create fn in router");
            userApp.userCreateView = new userApp.UserCreateView();
        },
    });
});
