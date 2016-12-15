var userApp = userApp || {};

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

$(function () {
    var app = new userApp.Initializer();
    app.start();
})