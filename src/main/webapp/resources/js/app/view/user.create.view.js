var userApp = userApp || {};

$(function () {
    userApp.UserCreateView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#user-create-template').html()),

        initialize: function () {
            console.log("initialize in UserCreateView");
            this.render();
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
});