var userApp = userApp || {};

$(function () {

    // render single line in user's table
    userApp.UserView = Backbone.View.extend({
        tagName: "tr",
        template: _.template($('#user-template').html()),

        attributes: function () {
            return {
                id: this.model.get("login"),
            };
        },

        render: function () {
            let user = this.model.toJSON();
            user.birthday = new Date().getFullYear() - new Date(user.birthday).getFullYear();
            this.$el.html(this.template(user));
            return this;
        },
    });
});