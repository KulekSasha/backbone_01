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
            userApp.usersView = new userApp.UsersView;
            userApp.usersView.render();
        },
        edit: function () {
            console.log("edit fn in router");
        },
        create: function () {
            console.log("create fn in router");
            userApp.userCreateView = new userApp.UserCreateView;
            userApp.userCreateView.render();
        },
    });


    userApp.UserCreateView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#user-create-template').html()),

        initialize: function () {
            console.log("initialize in UserCreateView");
            // userApp.userList.on('add', this.addOne, this); // is needed?
            // userApp.userList.on('reset', this.addAll, this); // is needed?
        },

        render: function () {
            this.$el.empty().html(this.template());
        },
        destroy: function () {
            console.log("destroy UserCreateView");
            this.undelegateEvents();
            this.unbind();
            this.$el.empty();
        },

        // event handlers
        events: {
            "click #btn-cancel": "cancel",
            "submit form": "saveNewUser",
        },
        cancel: function (evt) {
            console.log("cancel in UserCreateView: " + evt.target.getAttribute("href"));
            this.destroy();
            evt.preventDefault();
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
        saveNewUser: function (evt) {
            console.log("saveNewUser in UserCreateView: " + evt.currentTarget);
            evt.preventDefault();
            var formData = Backbone.Syphon.serialize(evt.currentTarget);
            console.log(formData);
            var u = new userApp.UserModel();


        },
    });


    //--------------
    // Initializers
    //--------------
    userApp.userList = new userApp.UserCollection();

    userApp.userList.fetch().then(function () {
        userApp.router = new userApp.Router({
            users: userApp.userList
        });
        // Backbone.history.start({pushState: true});
        Backbone.history.start();
    });

});