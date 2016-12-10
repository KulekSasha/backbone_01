<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
    <c:set var="url">${pageContext.request.contextPath}</c:set>
    <title>Admin add</title>
    <link href="${url}/resources/css/bootstrap.min.css" rel="stylesheet">
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
        <div class="col-lg-4 col-lg-offset-7 text-left">
            <h5><p class="text-center">Admin ${sessionScope.loginUser.firstName}
                (<a href="${url}/logout">logout</a>)</p></h5>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-8 col-lg-offset-2 text-left">
            <h3>Add user</h3> </br>

            <form:form method="post" modelAttribute="newUser" class="form-horizontal"
                       action="${url}/admin/users/add">
                <spring:bind path="login">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <label class="control-label col-lg-2" for="login">Login:</label>
                        <div class="col-lg-4">
                            <form:input path="login" class="form-control" placeholder="Login..."/>
                            <form:errors path="login" class="control-label"/>
                        </div>
                    </div>
                </spring:bind>

                <c:set var="pswd">${newUser.password}</c:set>
                <spring:bind path="password">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <div class="form-group ">
                            <label class="control-label col-lg-2" for="password">Password:</label>
                            <div class="col-lg-4">
                                <form:password path="password" class="form-control"
                                               showPassword="true"
                                               placeholder="Enter password" value="${pswd}"/>
                            </div>
                        </div>
                        <div class="form-group ">
                            <label class="control-label col-lg-2 " for="passConfirm">Password
                                again:</label>
                            <div class="col-lg-4">
                                <input type="password" class="form-control" id="passConfirm"
                                       name="passConfirm" placeholder="Enter password"
                                       value="${pswd}"/>
                                <form:errors path="password" class="control-label"/>
                            </div>
                        </div>
                    </div>
                </spring:bind>

                <spring:bind path="email">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <label class="control-label col-lg-2" for="email">Email:</label>
                        <div class="col-lg-4">
                            <form:input path="email" class="form-control" placeholder="E-mail..."/>
                            <form:errors path="email" class="control-label"/>
                        </div>
                    </div>
                </spring:bind>

                <spring:bind path="firstName">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <label class="control-label col-lg-2" for="firstName">First
                            name:</label>
                        <div class="col-lg-4">
                            <form:input path="firstName" class="form-control" placeholder="Name..."/>
                            <form:errors path="firstName" class="control-label"/>
                        </div>
                    </div>
                </spring:bind>

                <spring:bind path="lastName">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <label class="control-label col-lg-2" for="lastName">Last name:</label>
                        <div class="col-lg-4">
                            <form:input path="lastName" class="form-control" placeholder="Surname..."/>
                            <form:errors path="lastName" class="control-label"/>
                        </div>
                    </div>
                </spring:bind>

                <spring:bind path="birthday">
                    <div class="form-group ${status.error ? 'has-error' : ''}">
                        <label class="control-label col-lg-2" for="birthday">Birthday:</label>
                        <div class="col-lg-4">
                            <fmt:formatDate pattern="yyyy-MM-dd" value="${newUser.birthday}"
                                            var="dob"/>
                            <form:input path="birthday" class="form-control" value="${dob}"
                                        type="date"/>
                            <form:errors path="birthday" class="control-label"/>
                        </div>
                    </div>
                </spring:bind>

                <div class="form-group">
                    <label class="control-label col-lg-2" for="role">Role:</label>
                    <div class="col-lg-4">
                        <form:select path="role" class="list-group-item" id="role" name="role">
                            <form:option value="User"/>
                            <form:option value="Admin"/>
                        </form:select>
                        <form:errors path="role" element="div" cssClass="alert alert-danger"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-default">Ok</button>
                        <button type="reset" class="btn btn-default">Rest</button>
                        <a href="${url}/admin/users" class="btn btn-default"
                           role="button">Cancel</a>
                    </div>
                </div>
            </form:form>

        </div>
    </div>
</div>

<!-- jQuery Version 1.11.1 -->
<script src="${url}/resources/js/jquery.js"></script>

<!-- Bootstrap Core JavaScript -->
<script src="${url}/resources/js/bootstrap.min.js"></script>

</body>
</html>