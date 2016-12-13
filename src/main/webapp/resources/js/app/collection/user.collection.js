var userApp = userApp || {};

userApp.UserCollection = Backbone.Collection.extend({
    model: userApp.UserModel,

    url: "http://localhost:8080/lab-22-backbone/api/rest/users",
});


