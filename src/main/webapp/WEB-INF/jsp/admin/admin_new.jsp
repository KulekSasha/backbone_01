<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="own" uri="com.nix.tag" %>

<!DOCTYPE html>
<html>
<head>
    <c:set var="url">${pageContext.request.contextPath}</c:set>
    <title>Admin</title>
    <link href="${url}/resources/css/bootstrap.min.css" rel="stylesheet">
    <link href="${url}/resources/css/jquery-ui.min.css" rel="stylesheet">
    <link href="${url}/resources/css/jquery-ui.structure.min.css" rel="stylesheet">
    <style>
        body {
            padding-top: 70px;
        }
    </style>
</head>
<body>

<div class="container-fluid">
    <div class="row">
        <div class="col-lg-4 col-lg-offset-6 text-right">
            <h5><p>Admin ${sessionScope.loginUser.firstName}
                (<a href="${url}/logout">logout</a>)</p></h5>
        </div>
    </div>

    <div id="app-block">

    </div>
</div>


<%--jQuery  & Bootstrap--%>
<script src="${url}/resources/js/jquery-3.1.1.js"></script>
<script src="${url}/resources/js/bootstrap.min.js"></script>
<script src="${url}/resources/js/jquery-ui.min.js"></script>

<%--App Libs--%>
<script src="${url}/resources/js/app/lib/underscore.js"></script>
<script src="${url}/resources/js/app/lib/backbone.js"></script>
<script src="${url}/resources/js/app/lib/backbone.syphon.js"></script>
<script src="${url}/resources/js/app/lib/backbone.basicauth.js"></script>

<!--App assets-->
<script src="${url}/resources/js/app/model/user.js"></script>
<script src="${url}/resources/js/app/collection/user.collection.js"></script>
<script src="${url}/resources/js/app/view/user.view.js"></script>
<script src="${url}/resources/js/app/view/users.view.js"></script>
<script src="${url}/resources/js/app/view/user.abstract.create.edit.view.js"></script>
<script src="${url}/resources/js/app/view/user.create.view.js"></script>
<script src="${url}/resources/js/app/view/user.edit.view.js"></script>
<script src="${url}/resources/js/app/router/router.js"></script>

<!--App initializer-->
<script type="text/javascript" src="${url}/resources/js/app/index.js"></script>


<!--Templates-->
<script type="text/template" id="user-template">
    <td><\%- login %></td>
    <td><\%- firstName %></td>
    <td><\%- lastName %></td>
    <td><\%- birthday %></td>
    <td><\%- role %></td>
    <td>
        <button type="button" class="btn-xs btn-success edit" value="<\%- login %>">Edit</button>
        <button type="button" class="btn-xs btn-danger delete" value="<\%- login %>">Delete</button>
    </td>
</script>

<script type="text/template" id="users-template">
    <div class="row" id="start-view">
        <div class="col-lg-8 col-lg-offset-2 text-left">
            <div>
                <a id="nav-create-new" href="create">Add new</a>
            </div>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>Login</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Age</th>
                    <th>Role</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody id="user-list">

                </tbody>
            </table>
        </div>
    </div>
    <div hidden id="dialog-confirm" title="Delete user?">
        <p><span class="ui-icon ui-icon-alert" style=" float:left; margin:12px 12px 20px 0;"></span>
            The user will be permanently deleted and cannot be recovered.
            Are you sure?</p>
    </div>
</script>

<div type="text/template" id="user-create-edit-template" hidden>
    <div class="row">
        <div class="col-lg-8 col-lg-offset-2 text-left">

            <form id="newUser" class="form-horizontal" action="#" method="post" role="form">

                <h3 class="col-sm-offset-4">Add user</h3></br>

                <div id="login-fg" class="form-group">
                    <label class="control-label col-sm-3" for="login">Login:</label>
                    <div class="col-sm-4">
                        <input type="text" id="login" name="login" placeholder="Login..."
                               class="form-control"
                               value="">
                        <span id="login-srv-err" class="control-label"></span>
                    </div>
                </div>

                <div id="password-fg" class="form-group ">
                    <div class="form-group">
                        <label class="control-label col-sm-3" for="password">Password:</label>
                        <div class="col-sm-4">
                            <input id="password" name="password" placeholder="Enter password"
                                   class="form-control" value="" type="password">
                        </div>
                    </div>
                    <div id="passConfirm-fg" class="form-group">
                        <label class="control-label col-sm-3 " for="passConfirm">Password
                            again:</label>
                        <div class="col-sm-4">
                            <input class="form-control" id="passConfirm" name="passConfirm"
                                   placeholder="Enter password" value="" type="password">
                            <span id="passConfirmError" class="control-label"></span>
                            <span id="password-srv-err" class="control-label"></span>
                        </div>
                    </div>
                </div>

                <div id="email-fg" class="form-group ">
                    <label class="control-label col-sm-3" for="email">Email:</label>
                    <div class="col-sm-4">
                        <input id="email" name="email" placeholder="E-mail..." class="form-control"
                               value="" type="text">
                        <span id="email-srv-err" class="control-label"></span>
                    </div>
                </div>

                <div id="firstName-fg" class="form-group ">
                    <label class="control-label col-sm-3" for="firstName">First
                        name:</label>
                    <div class="col-sm-4">
                        <input id="firstName" name="firstName" placeholder="Name..."
                               class="form-control" value="" type="text">
                        <span id="firstName-srv-err" class="control-label"></span>
                    </div>
                </div>

                <div id="lastName-fg" class="form-group ">
                    <label class="control-label col-sm-3" for="lastName">Last name:</label>
                    <div class="col-sm-4">
                        <input id="lastName" name="lastName" placeholder="Surname..."
                               class="form-control" value="" type="text">
                        <span id="lastName-srv-err" class="control-label"></span>
                    </div>
                </div>

                <div id="birthday-fg" class="form-group ">
                    <label class="control-label col-sm-3" for="birthday">Birthday:</label>
                    <div class="col-sm-4">
                        <input id="birthday" name="birthday" class="form-control" value=""
                               type="date">
                        <span id="birthday-srv-err" class="control-label"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-3" for="role">Role:</label>
                    <div class="col-sm-4">
                        <select id="role" name="role" class="list-group-item">
                            <option value="User">User</option>
                            <option value="Admin">Admin</option>
                        </select>
                    </div>
                </div>
                <div class="form-group ">
                    <div class="col-sm-offset-4">
                        <button id="btn-save" type="submit" class="btn btn-default">Ok</button>
                        <button type="reset" class="btn btn-default">Rest</button>
                        <a id="btn-cancel" href="/" class="btn btn-default" role="button">Cancel</a>
                    </div>
                </div>
                <div>
                </div>
            </form>
        </div>
    </div>

    <script type="application/javascript">
        function checkPasswordMatch() {
            let password = $("#password").val();
            let confirmPassword = $("#passConfirm").val();

            if (password != confirmPassword) {
                console.log('try check');
                $('#passConfirmError').html("Passwords do not match!</br>").addClass("control-label");
                $('#password-fg').addClass("has-error");
                $('#btn-save').attr("disabled", true);
            } else {
                console.log('valid');
                $("#passConfirmError").html("");
                $('#password-fg').removeClass("has-error");
                $('#btn-save').attr("disabled", false);
            }
        }

        $("#passConfirm").keyup(checkPasswordMatch);

        $("#password").keyup(function () {
            if ($("#passConfirm").val() !== "") {
                checkPasswordMatch();
            }
        })
    </script>
</div>

</body>
</html>