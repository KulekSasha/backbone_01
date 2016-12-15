var userApp = userApp || {};

$(function () {
    userApp.UserCreateView = userApp.AbstractCreateEditView.extend({
        submitForm: function (evt) {
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
                    error: function (model, resp) {
                        console.log("error create new user");
                        userApp.userCreateView.showValidationErrors(resp);
                    },
                }
            );
        },
    });
});
