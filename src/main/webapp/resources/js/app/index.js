(function ()  {
    var user = Backbone.Model.extend({
        defaults: {
            "id": "",
            "login": "",
            "password": "",
            "email": "",
            "firstName": "",
            "lastName": "",
            "birthday": "",
            "role": {
                "id": "",
                "name": ""
            }
        }
    });

    var MyCollection = Backbone.Collection.extend({
        url: "http://localhost:8085/lab-22-backbone/api/rest/users",
        model: user
    });


    var myCollection = new MyCollection();
    myCollection.fetch({
        success: function () {
            console.log(myCollection.toJSON());
        },
        error: function () {
            alert('error');
        }
    });
})