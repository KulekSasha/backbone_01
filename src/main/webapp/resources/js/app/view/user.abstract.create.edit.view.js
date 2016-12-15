var userApp = userApp || {};

$(function () {
    userApp.AbstractCreateEditView = Backbone.View.extend({
        el: $("#app-block"),
        template: _.template($('#user-create-edit-template').html()),

        initialize: function (opt) {
            console.log("initialize in AbstractCreateEditView");
            this.$el.empty().html(this.template());
            this.render(opt);
        },
        render: function (opt) {

        },
        destroy: function () {
            this.$el.empty().off();
            this.stopListening();
        },

        events: {
            "click #btn-cancel": "cancel",
            "submit form": "submitForm",
        },
        cancel: function (evt) {
            this.destroy();
            evt.preventDefault();
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
        submitForm: function (evt) {
        },
        showValidationErrors: function (resp) {
            let errors = $.parseJSON(resp.responseText);

            $(".form-group").removeClass("has-error");
            $('span[id$=-srv-err]').empty();

            _.each(errors, (val, key) => {
                $('#' + key + '-fg').addClass('has-error');
                $('#' + key + '-srv-err').append(val + '</br>');
            });
        }
    });
});