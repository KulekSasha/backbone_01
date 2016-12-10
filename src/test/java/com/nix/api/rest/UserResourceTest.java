package com.nix.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nix.config.JerseyAppConfig;
import com.nix.model.Role;
import com.nix.model.User;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.ContextLoaderListener;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class UserResourceTest extends JerseyTest {

    private static ObjectMapper jsonMapper;

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new GrizzlyWebTestContainerFactory();
    }

    @Override
    protected DeploymentContext configureDeployment() {
        return ServletDeploymentContext
                .forServlet(new ServletContainer(new JerseyAppConfig()))
                .contextParam("contextClass",
                        "org.springframework.web.context.support.AnnotationConfigWebApplicationContext")
                .contextParam("contextConfigLocation", "com.nix.config.UserResourceTestConfig")
                .addListener(ContextLoaderListener.class)
                .build();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        jsonMapper = new ObjectMapper();
        jsonMapper.setTimeZone(TimeZone.getDefault());
    }

    @Test(timeout = 15000L)
    public void getAllUsers() throws Exception {
        int expectedListSize = 5;

        Response output = target("users")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        List<User> list = jsonMapper.readValue(output.readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class,
                        User.class));

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());
        assertEquals("should contain 5 user", expectedListSize, list.size());
    }

    @Test(timeout = 15000L)
    public void getUserByLogin() throws Exception {
        Response output = target("users/testUser_1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        User user = jsonMapper.readValue(output.readEntity(String.class), User.class);

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());
        assertEquals("users should be equal", getExistedUser(), user);
    }

    @Test(timeout = 15000L)
    public void getUserByWrongLogin() throws Exception {
        Response output = target("users/testUser_100")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals("should return status 200", Response.Status.NOT_FOUND.getStatusCode(),
                output.getStatus());
    }

    @Test(timeout = 15000L)
    public void createUser() throws Exception {
        User newUser = new User(0, "testUser_6", "password",
                "testUser_6@gmail.com", "firstNameTest", "lastNameTest",
                new GregorianCalendar(1986, Calendar.JANUARY, 1).getTime(),
                new Role(1L, "Admin"));

        int expectedNewId = 6;

        Response output = target("users")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("should return status 201", Response.Status.CREATED.getStatusCode(),
                output.getStatus());

        newUser.setId(expectedNewId);
        User createdUser = jsonMapper.readValue(output.readEntity(String.class), User.class);
        assertEquals(newUser, createdUser);
    }

    @Test(timeout = 15000L)
    public void createUserWrongParam() throws Exception {
        User newUser = new User(0, "testUser_1", "p",
                "t", "f", "l",
                new GregorianCalendar(2050, Calendar.JANUARY, 1).getTime(),
                new Role(1L, "Admin"));

        int expectedFieldsWithError = 6;

        Response output = target("users")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE));

        MultivaluedMap<String, String> errors =
                output.readEntity(new GenericType<MultivaluedHashMap<String, String>>() {
                });

        assertEquals("should return status 400", Response.Status.BAD_REQUEST.getStatusCode(),
                output.getStatus());

        assertEquals("six fields with error must be returned",
                errors.size(), expectedFieldsWithError);

        assertThat(errors,
                allOf(
                        hasKey("login"),
                        hasKey("password"),
                        hasKey("firstName"),
                        hasKey("lastName"),
                        hasKey("email"),
                        hasKey("birthday")
                ));
    }

    @Test(timeout = 15000L)
    public void updateUser() throws Exception {
        User updateUser = getExistedUser();
        updateUser.setFirstName("OlegUp");
        updateUser.setLastName("GazmanovUp");

        Response output = target("users/testUser_1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updateUser, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());

        User updatedUser = jsonMapper.readValue(output.readEntity(String.class), User.class);
        assertEquals(updateUser, updatedUser);
    }

    @Test(timeout = 15000L)
    public void updateNonexistentUser() throws Exception {
        User updateUser = new User(100, "testUser_100", "testUser_5",
                "testUser_5@gmail.com", "OlegUp", "GazmanovUp",
                new GregorianCalendar(1980, Calendar.MAY, 5).getTime(),
                new Role(1L, "Admin"));

        Response output = target("users/testUser_100")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updateUser, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("should return status 404", Response.Status.NOT_FOUND.getStatusCode(),
                output.getStatus());
    }

    @Test(timeout = 15000L)
    public void updateUserInvalidNameBirthday() throws Exception {
        User updateUser = getExistedUser();
        updateUser.setFirstName("O");
        updateUser.setBirthday(new GregorianCalendar(2050, Calendar.MAY, 5).getTime());

        int expectedFieldsWithError = 2;

        Response output = target("users/testUser_1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updateUser, MediaType.APPLICATION_JSON_TYPE));

        MultivaluedMap<String, String> errors =
                output.readEntity(new GenericType<MultivaluedHashMap<String, String>>() {
                });

        assertEquals("should return status 400", Response.Status.BAD_REQUEST.getStatusCode(),
                output.getStatus());

        assertEquals("two fields with error must be returned",
                errors.size(), expectedFieldsWithError);

        assertThat(errors,
                allOf(
                        hasKey("firstName"),
                        hasKey("birthday")
                ));
    }

    @Test(timeout = 15000L)
    public void updateUserChangeLogin() throws Exception {
        User updateUser = getExistedUser();
        updateUser.setLogin("LoginUpdate");

        int expectedFieldsWithError = 1;

        Response output = target("users/testUser_1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updateUser, MediaType.APPLICATION_JSON_TYPE));

        MultivaluedMap<String, String> errors =
                output.readEntity(new GenericType<MultivaluedHashMap<String, String>>() {
                });

        assertEquals("should return status 400", Response.Status.BAD_REQUEST.getStatusCode(),
                output.getStatus());

        assertEquals("one field with error must be returned",
                errors.size(), expectedFieldsWithError);

        assertThat(errors,
                allOf(
                        hasKey("login")
                ));
    }

    @Test(timeout = 15000L)
    public void deleteUser() throws Exception {
        Invocation invocationAllUser = target("users")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .buildGet();

        List<User> listBefore = jsonMapper.readValue(
                invocationAllUser.invoke().readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class,
                        User.class));

        Response output = target("users/testUser_5")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());

        List<User> listAfter = jsonMapper.readValue(
                invocationAllUser.invoke().readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class,
                        User.class));

        assertEquals("list after should be less on one",
                listBefore.size(), listAfter.size() + 1);
    }

    @Test(timeout = 15000L)
    public void deleteNonexistentUser() throws Exception {
        Response output = target("users/testUser_100")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        assertEquals("should return status 404", Response.Status.NOT_FOUND.getStatusCode(),
                output.getStatus());
    }

    private User getExistedUser() {
        return new User(1L, "testUser_1", "testUser_1",
                "testUser_1@gmail.com", "Ivan", "Ivanov",
                new GregorianCalendar(1986, Calendar.JANUARY, 1).getTime(),
                new Role(1L, "Admin"));
    }
}