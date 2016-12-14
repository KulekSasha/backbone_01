package com.nix.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nix.config.JerseyAppConfig;
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

        List<UserModelForTest> list = jsonMapper.readValue(output.readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class,
                        UserModelForTest.class));

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());
        assertEquals("should contain 5 user", expectedListSize, list.size());
    }

    @Test(timeout = 15000L)
    public void getUserByLogin() throws Exception {
        Response output = target("users/testUser_1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();

        UserModelForTest user = jsonMapper.readValue(output.readEntity(String.class),
                UserModelForTest.class);

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
        int expectedNewId = 6;

        UserModelForTest userFromClient = new UserModelForTest(0, "testUser_6", "password",
                "testUser_6@gmail.com", "firstNameTest", "lastNameTest", "1986-01-01", "Admin");

        Response output = target("users")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(userFromClient, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("should return status 201", Response.Status.CREATED.getStatusCode(),
                output.getStatus());

        userFromClient.setId(expectedNewId);
        UserModelForTest createdUser = jsonMapper.readValue(output.readEntity(String.class),
                UserModelForTest.class);
        assertEquals(userFromClient, createdUser);

    }

    @Test(timeout = 15000L)
    public void createUserWrongParam() throws Exception {
        UserModelForTest newUser = new UserModelForTest(0, "testUser_1", "p",
                "t", "f", "l", "2050-01-01", "Admin");

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
        UserModelForTest updateUser = getExistedUser();
        updateUser.setFirstName("OlegUp");
        updateUser.setLastName("GazmanovUp");

        Response output = target("users/testUser_1")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updateUser, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());

        UserModelForTest updatedUser = jsonMapper.readValue(output.readEntity(String.class),
                UserModelForTest.class);
        assertEquals(updateUser, updatedUser);
    }

    @Test(timeout = 15000L)
    public void updateNonexistentUser() throws Exception {
        UserModelForTest updateUser = new UserModelForTest(100, "testUser_100", "testUser_5",
                "testUser_5@gmail.com", "OlegUp", "GazmanovUp", "1980-05-05", "Admin");

        Response output = target("users/testUser_100")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(updateUser, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("should return status 404", Response.Status.NOT_FOUND.getStatusCode(),
                output.getStatus());
    }

    @Test(timeout = 15000L)
    public void updateUserInvalidNameBirthday() throws Exception {
        UserModelForTest updateUser = getExistedUser();
        updateUser.setFirstName("O");
        updateUser.setBirthday("2050-05-05");

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
        UserModelForTest updateUser = getExistedUser();
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

        List<UserModelForTest> listBefore = jsonMapper.readValue(
                invocationAllUser.invoke().readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class,
                        UserModelForTest.class));

        Response output = target("users/testUser_5")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .delete();

        assertEquals("should return status 200", Response.Status.OK.getStatusCode(),
                output.getStatus());

        List<UserModelForTest> listAfter = jsonMapper.readValue(
                invocationAllUser.invoke().readEntity(String.class),
                TypeFactory.defaultInstance().constructCollectionType(List.class,
                        UserModelForTest.class));

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

    private UserModelForTest getExistedUser() {
        return new UserModelForTest(1L, "testUser_1", "testUser_1",
                "testUser_1@gmail.com", "Ivan", "Ivanov",
                "1986-01-01", "Admin");
    }

    private static class UserModelForTest {
        private long id;
        private String login;
        private String password;
        private String email;
        private String firstName;
        private String lastName;
        private String birthday;
        private String role;

        public UserModelForTest() {
        }

        public UserModelForTest(long id, String login, String password, String email,
                                String firstName, String lastName,
                                String birthday, String role) {
            this.id = id;
            this.login = login;
            this.password = password;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthday = birthday;
            this.role = role;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserModelForTest that = (UserModelForTest) o;

            if (login != null ? !login.equals(that.login) : that.login != null) return false;
            if (password != null ? !password.equals(that.password) : that.password != null)
                return false;
            if (email != null ? !email.equals(that.email) : that.email != null) return false;
            if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
                return false;
            if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
                return false;
            if (birthday != null ? !birthday.equals(that.birthday) : that.birthday != null)
                return false;
            return role != null ? role.equals(that.role) : that.role == null;
        }

        @Override
        public int hashCode() {
            int result = login != null ? login.hashCode() : 0;
            result = 31 * result + (password != null ? password.hashCode() : 0);
            result = 31 * result + (email != null ? email.hashCode() : 0);
            result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
            result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
            result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
            result = 31 * result + (role != null ? role.hashCode() : 0);
            return result;
        }
    }
}