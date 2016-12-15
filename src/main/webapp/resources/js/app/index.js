var userApp = userApp || {};

//setup basic authentication for rest services
$.ajaxSetup({
    username: localStorage.getItem('currentLogin'),
    password: localStorage.getItem('currentPass')
});

//setup deployment context
$.ajaxPrefilter(function (options) {
    let deploymentContext = '/lab-22-backbone';
    options.url = deploymentContext + options.url;
});

userApp.Initializer = function () {
    this.start = function () {
        userApp.userList = new userApp.UserCollection();

        userApp.userList.fetch({
            username: localStorage.getItem('currentLogin'),
            password: localStorage.getItem('currentPass')
        }).then(function () {
            userApp.router = new userApp.Router({
                users: userApp.userList
            });
            Backbone.history.start();
        });
    }
};

$(function () {
    const app = new userApp.Initializer();
    app.start();
});