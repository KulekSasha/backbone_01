var userApp = userApp || {};

$(function () {
    userApp.UserEditView = userApp.AbstractCreateEditView.extend({
        render: function (opt) {
            $('h3').empty().text('Edit user: ' + opt.login);
            let edtUser = userApp.userList.findWhere({login: opt.login})
            _.each(edtUser.attributes, (val, key) => $("#" + key).val(val));
            $("#login").attr({disabled: true});
            $("#passConfirm").val(edtUser.get("password"));
        },
        submitForm: function (evt) {
            console.log("updateUser in UserEditView: " + evt.currentTarget);
            evt.preventDefault();
            let formData = Backbone.Syphon.serialize(evt.currentTarget);
            let updUser = userApp.userList.get(formData["login"]);

            updUser.save({
                    password: formData["password"],
                    email: formData["email"],
                    firstName: formData["firstName"],
                    lastName: formData["lastName"],
                    birthday: formData["birthday"],
                    role: formData["role"]
                },
                {
                    wait: true,
                    type: "PUT",
                    url: "http://localhost:8080/lab-22-backbone/api/rest/users/" + formData["login"],
                    success: function () {
                        userApp.userEditView.destroy();
                        userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
                    },
                    error: function (model, resp, options) {
                        console.log("error update user");
                        userApp.userEditView.showValidationErrors(resp);
                    },
                }
            );
        },
    });
});

// $(function () {
//     userApp.UserEditView = Backbone.View.extend({
//         el: $("#app-block"),
//         template: _.template($('#user-create-edit-template').html()),
//
//         initialize: function (opt) {
//             console.log("initialize in UserEditView");
//             this.render(opt);
//         },
//         render: function (opt) {
//             this.$el.empty().html(this.template());
//             $('h3').empty().text('Edit user: ' + opt.login);
//             let edtUser = userApp.userList.findWhere({login: opt.login})
//             _.each(edtUser.attributes, (val, key) => $("#" + key).val(val));
//             $("#login").attr({disabled: true});
//             $("#passConfirm").val(edtUser.get("password"));
//         },
//         destroy: function () {
//             console.log("destroy UserEditView");
//             this.$el.empty().off();
//             this.stopListening();
//         },
//
//         // event handlers
//         events: {
//             "click #btn-cancel": "cancel",
//             "submit form": "updateUser",
//         },
//         cancel: function (evt) {
//             console.log("cancel in UserEditView: " + evt.target.getAttribute("href"));
//             this.destroy();
//             evt.preventDefault();
//             userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
//         },
//         updateUser: function (evt) {
//             console.log("updateUser in UserEditView: " + evt.currentTarget);
//             evt.preventDefault();
//             let formData = Backbone.Syphon.serialize(evt.currentTarget);
//             let updUser = userApp.userList.get(formData["login"]);
//
//             updUser.save({
//                     password: formData["password"],
//                     email: formData["email"],
//                     firstName: formData["firstName"],
//                     lastName: formData["lastName"],
//                     birthday: formData["birthday"],
//                     role: formData["role"]
//                 },
//                 {
//                     wait: true,
//                     type: "PUT",
//                     url: "http://localhost:8080/lab-22-backbone/api/rest/users/" + formData["login"],
//                     success: function () {
//                         // userApp.userList.add(newUser);
//                         userApp.userEditView.destroy();
//                         userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
//                     },
//                     error: function (model, resp, options) {
//                         console.log("error update user");
//                         userApp.userEditView.showValidationErrors(resp);
//                     },
//                 }
//             );
//         },
//         showValidationErrors: function (resp) {
//             console.log(resp);
//             let errors = $.parseJSON(resp.responseText);
//             console.log(errors);
//
//             $(".form-group").removeClass("has-error");
//             $('span[id$=-srv-err]').empty();
//
//             _.each(errors, function (val, key, list) {
//                 $('#' + key + '-fg').addClass('has-error');
//                 $('#' + key + '-srv-err').append(val + '</br>');
//             });
//         }
//     });
// });