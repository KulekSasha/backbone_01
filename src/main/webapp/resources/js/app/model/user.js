var userApp = userApp || {};

userApp.UserModel = Backbone.Model.extend({

    defaults: {
        "id": "",
        "login": "",
        "password": "",
        "email": "",
        "firstName": "",
        "lastName": "",
        "birthday": "",
        "role": {
            "id": "",
            "name": ""
        }
    }
});

