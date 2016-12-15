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
            console.log("render in AbstractCreateEditView - should be overridden");
        },
        destroy: function () {
            console.log("destroy AbstractCreateEditView");
            this.$el.empty().off();
            this.stopListening();
        },

        // event handlers
        events: {
            "click #btn-cancel": "cancel",
            "submit form": "submitForm",
        },
        cancel: function (evt) {
            console.log("cancel in UserEditView: " + evt.target.getAttribute("href"));
            this.destroy();
            evt.preventDefault();
            userApp.router.navigate(evt.target.getAttribute("href"), {trigger: true});
        },
        submitForm: function (evt) {
            console.log("submitForm in AbstractCreateEditView - should be overridden");
        },
        showValidationErrors: function (resp) {
            console.log(resp);
            let errors = $.parseJSON(resp.responseText);
            console.log(errors);

            $(".form-group").removeClass("has-error");
            $('span[id$=-srv-err]').empty();

            _.each(errors, function (val, key, list) {
                $('#' + key + '-fg').addClass('has-error');
                $('#' + key + '-srv-err').append(val + '</br>');
            });
        }
    });
});