var userApp = userApp || {};

userApp.UserModel = Backbone.Model.extend({

    url: "/api/rest/users",


    defaults: {
        "id": "",
        "login": "",
        "password": "",
        "email": "",
        "firstName": "",
        "lastName": "",
        "birthday": "",
        "role": "",
    },

    idAttribute: "login",

});

