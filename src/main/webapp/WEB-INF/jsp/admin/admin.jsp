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
<!-- Page Content -->
<div class="container">
    <div class="row">
        <div class="col-lg-4 col-lg-offset-6 text-right">
            <h5><p>Admin ${sessionScope.loginUser.firstName}
                (<a href="${url}/logout">logout</a>)</p></h5>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-8 col-lg-offset-2 text-left">
            <h5><p><a href="${url}/admin/users/add">Add new user</a></p></h5>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-8 col-lg-offset-2 text-left">
            <own:userTable/>
        </div>
    </div>
</div>

<!-- jQuery Version 1.11.1 -->
<script src="${url}/resources/js/jquery.js"></script>
<!-- jQuery UI -->
<script src="${url}/resources/js/jquery-ui.min.js"></script>
<!-- Bootstrap Core JavaScript -->
<script src="${url}/resources/js/bootstrap.min.js"></script>


<script>
    $(".btn-danger").on("click", function () {
        var userLogin = $(this).val();
        $("#dialog-confirm").dialog({
            resizable: false,
            height: "auto",
            width: 400,
            modal: true,
            buttons: {
                "Delete": function () {
                    $.post("${url}/admin/users/"+ userLogin +"/delete", {userId: userLogin}, function () {
                        location.reload();
                    })
                        .fail(function () {
                            location.reload();
                        });
                    $(this).dialog("close");
                },
                "Cancel": function () {
                    $(this).dialog("close");
                }
            }
        });
    });
</script>

<div hidden id="dialog-confirm" title="Delete user?">
    <p><span class="ui-icon ui-icon-alert" style="float:left; margin:12px 12px 20px 0;"></span>
        The user will be permanently deleted and cannot be recovered.
        Are you sure?</p>
</div>

</body>
</html>