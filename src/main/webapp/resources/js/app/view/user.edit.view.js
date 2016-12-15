var userApp = userApp || {};

$(function () {
    userApp.UserEditView = userApp.AbstractCreateEditView.extend({
        render: function (opt) {
            $('h3').empty().text('Edit user:');
            let edtUser = userApp.userList.findWhere({login: opt.login});
            _.each(edtUser.attributes, (val, key) => $("#" + key).val(val));
            $("#login").attr({disabled: true});
            $("#passConfirm").val(edtUser.get("password"));
        },
        submitForm: function (evt) {
            evt.preventDefault();
            let formData = Backbone.Syphon.serialize(evt.currentTarget);
            let updUser = userApp.userList.get(formData["login"]);

            userApp.credentials = {
                username: localStorage.getItem('currentLogin'),
                password: localStorage.getItem('currentPass')
            };

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
                    url: "/api/rest/users/" + formData["login"],
                    success: function () {
                        userApp.userEditView.destroy();
                        userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
                    },
                    error: function (model, resp) {
                        userApp.userEditView.showValidationErrors(resp);
                    },
                }
            );
        },
    });
});