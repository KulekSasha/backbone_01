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
            // user.birthday = new Date().getFullYear() - new Date(user.birthday).getFullYear();
            user.birthday = this.getAge(new Date(user.birthday), new Date());
            this.$el.html(this.template(user));
            return this;
        },
        getAge: function (dateOfBirth, dateToCalculate) {
            let calculateYear = dateToCalculate.getFullYear();
            let calculateMonth = dateToCalculate.getMonth();
            let calculateDay = dateToCalculate.getDate();

            let birthYear = dateOfBirth.getFullYear();
            let birthMonth = dateOfBirth.getMonth();
            let birthDay = dateOfBirth.getDate();

            let age = calculateYear - birthYear;
            let ageMonth = calculateMonth - birthMonth;
            let ageDay = calculateDay - birthDay;

            if (ageMonth < 0 || (ageMonth == 0 && ageDay < 0)) {
                age = parseInt(age) - 1;
            }
            return age;
        }
    });
});