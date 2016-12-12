var userApp = userApp || {};

$(function () {


    userApp.UserCollection = Backbone.Collection.extend({
        model: userApp.UserModel,

        url: "http://localhost:8080/lab-22-backbone/api/rest/users",

    });

    userApp.userList = new userApp.UserCollection();

    userApp.UserView = Backbone.View.extend({
        tagName: 'tr',
        template: _.template($('#user-template').html()),
        render: function () {
            var user = this.model.toJSON();
            user.birthday = new Date().getFullYear() - new Date(user.birthday).getFullYear();
            this.$el.html(this.template(user));
            return this;
        },

    });

    userApp.UserEditView = Backbone.View.extend({

        // things.find(function(model) { return model.get('name') === 'Lee'; });

        tagName: 'tr',
        template: _.template($('#user-template').html()),
        render: function () {
            var user = this.model.toJSON();
            user.birthday = new Date().getFullYear() - new Date(user.birthday).getFullYear();
            this.$el.html(this.template(user));
            return this;
        },

    });

    userApp.AppView = Backbone.View.extend({
        el: $("#app-block"),

        initialize: function () {
            userApp.userList.on('add', this.addOne, this);
            userApp.userList.on('reset', this.addAll, this);
            userApp.userList.fetch();
        },

        addAll: function () {
            this.$('#user-list').html(''); // clean the list
            userApp.userList.each(this.addOne, this);
        },

        addOne: function (user) {
            var view = new userApp.UserView({model: user});
            $('#user-list').append(view.render().el);
        },

        events: {
            'click .edit': 'editUser',
            'click .delete': 'deleteUser',
            'click #create-new': 'createUser',
        },

        editUser: function (e) {
            var val = $(e.currentTarget).val();
            alert(val);
        },

        deleteUser: function (e) {
            alert(e.target.value);
        },
        
        createUser: function () {
            alert("create in appView");
            userApp.router.navigate("create", true)
        }

    });

    userApp.Router = Backbone.Router.extend({
        routes: {
            "": "start",
            "!/edit/:login": "edit",
            "create": "create",
        },

        start: function () {
            // alert("start in router");

        },

        edit: function () {
            alert("edit");
        },

        create: function () {
            alert("create in router");
            userApp.appView.initialize();
        },

    });


    //--------------
    // Initializers
    //--------------
    userApp.router = new userApp.Router();
    Backbone.history.start();

    userApp.appView = new userApp.AppView;

})
;