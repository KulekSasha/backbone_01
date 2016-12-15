var userApp = userApp || {};

userApp.UserCollection = Backbone.Collection.extend({
    model: userApp.UserModel,

    url: "/api/rest/users",
});


