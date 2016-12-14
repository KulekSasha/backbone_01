var userApp = userApp || {};

userApp.UserModel = Backbone.Model.extend({

    url: "http://localhost:8080/lab-22-backbone/api/rest/users/",

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

