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


    userApp.UserEditView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#user-create-template').html()),

        initialize: function (opt) {
            console.log("initialize in UserCreateView");
            this.render(opt);
        },
        render: function (opt) {
            this.$el.empty().html(this.template());
            $('h3').empty().text('Edit user: ' + opt.login);
            let edtUser = userApp.userList.findWhere({login: opt.login})
            _.each(edtUser.attributes, (val, key) => $("#" + key).val(val));
            $("#login").attr({disabled: true});
            $("#passConfirm").val(edtUser.get("password"));
        },
        destroy: function () {
            console.log("destroy UserEditView");
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
            console.log("cancel in UserEditView: " + evt.target.getAttribute("href"));
            this.destroy();
            evt.preventDefault();
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
        saveNewUser: function (evt) {
            console.log("saveNewUser in UserCreateView: " + evt.currentTarget);
            evt.preventDefault();
            let formData = Backbone.Syphon.serialize(evt.currentTarget);
            let newUser = new userApp.UserModel();

            newUser.save({
                    login: formData["login"],
                    password: formData["password"],
                    email: formData["email"],
                    firstName: formData["firstName"],
                    lastName: formData["lastName"],
                    birthday: formData["birthday"],
                    role: formData["role"],
                },
                {
                    wait: true,
                    type: 'POST',
                    success: function () {
                        userApp.userList.add(newUser);
                        userApp.userCreateView.destroy();
                        userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
                    },
                    error: function (model, resp, options) {
                        console.log("error create new user");
                        userApp.userCreateView.showValidationErrors(resp);
                    },
                }
            );
        },
        showValidationErrors: function (resp) {
            console.log(resp);
            let errors = $.parseJSON(resp.responseText);
            console.log(errors);

            $(".form-group").removeClass("has-error");
            $('span[id$=-srv-err]').empty();

            _.each(errors, function (val, key, list) {
                $('#' + key + '-fg').addClass('has-error');
                $('#' + key + '-srv-err').append(val + '</br>');
            });
        }
    });

    userApp.Initializer = function () {
        this.start = function () {
            userApp.userList = new userApp.UserCollection();

            userApp.userList.fetch().then(function () {
                userApp.router = new userApp.Router({
                    users: userApp.userList
                });
                // Backbone.history.start({pushState: true});
                Backbone.history.start();
            });
        }
    }

});

$(function () {
    var app = new userApp.Initializer();
    app.start();
})