package com.nix.api.rest;

import com.nix.model.User;
import com.nix.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Set;

@Component
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    @Qualifier("validator")
    private Validator validator;

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    @Context
    UriInfo uriInfo;

    @GET
    public List<User> getAllUsers() {
        log.debug("requesting all users");
        return userService.findAll();
    }

    @GET
    @Path("{login}")
    public Response getUserByLogin(@PathParam("login") String login) {
        log.debug("requesting user by login: {}", login);
        User user = userService.findByLogin(login);
        log.debug("found user: {}", user);

        return user != null
                ? Response.ok(user).build()
                : Response.status(404).entity("").build();
    }

    @POST
    public Response createUser(User newUser, @Context HttpServletRequest request) {
        log.debug("create user, incoming user: {}", newUser);

        MultivaluedMap<String, String> errorsMap = hibernateValidation(newUser);

        if (userService.findByLogin(newUser.getLogin()) != null) {
            errorsMap.add("login",
                    messageSource.getMessage("non.unique.login", null, request.getLocale()));
        }

        if (errorsMap.size() > 0) {
            log.debug("new user not pass validation, errors: {}", errorsMap);
            return Response.status(400).entity(errorsMap).type(MediaType.APPLICATION_JSON).build();
        }

        log.debug("create new user: {}", newUser);
        userService.create(newUser);

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(newUser.getLogin())
                .build();

        return Response
                .created(uri)
                .entity(userService.findByLogin(newUser.getLogin()))
                .build();
    }

    @PUT
    @Path("{login}")
    public Response updateUser(User updatedUser,
                               @Context HttpServletRequest request,
                               @PathParam("login") String login) {
        log.debug("update user, incoming user: {}", updatedUser);

        User currentUser = userService.findByLogin(login);

        if (currentUser == null) {
            return Response.status(404).entity("").build();
        }

        updatedUser.setId(currentUser.getId());

        MultivaluedMap<String, String> errorsMap = hibernateValidation(updatedUser);

        if (!login.equalsIgnoreCase(updatedUser.getLogin())) {
            errorsMap.add("login",
                    messageSource.getMessage("login.not.changeable", null, request.getLocale()));
        }

        if (errorsMap.size() > 0) {
            log.debug("updated user not pass validation, errors: {}", errorsMap);
            return Response.status(400).entity(errorsMap).type(MediaType.APPLICATION_JSON).build();
        }

        log.debug("update user: {}", updatedUser);
        userService.update(updatedUser);

        URI uri = uriInfo.getAbsolutePathBuilder()
                .path(updatedUser.getLogin())
                .build();

        return Response
                .ok()
                .entity(updatedUser)
                .type(MediaType.APPLICATION_JSON)
                .header("Location", uri)
                .build();
    }

    @DELETE
    @Path("{login}")
    public Response deleteUser(@PathParam("login") String login) {
        log.debug("delete user, incoming login: {}", login);
        User userToRemove = userService.findByLogin(login);

        if (userToRemove == null) {
            log.debug("user not found");
            return Response.status(404).entity("").build();
        }

        log.debug("remove user: {}", userToRemove);
        userService.remove(userToRemove);

        return Response
                .ok()
                .build();
    }

    private MultivaluedMap<String, String> hibernateValidation(User user) {
        Set<ConstraintViolation<User>> validationErrors = validator.validate(user);
        MultivaluedMap<String, String> errorsMap = new MultivaluedHashMap<>();

        if (validationErrors.size() > 0) {
            validationErrors.forEach(err ->
                    errorsMap.add(err.getPropertyPath().toString(), err.getMessage()));
        }

        return errorsMap;
    }

}
