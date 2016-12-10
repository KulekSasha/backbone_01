package com.nix.dao.hiber;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.nix.config.DaoTestConfig;
import com.nix.dao.UserDao;
import com.nix.model.Role;
import com.nix.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoTestConfig.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
})
@DatabaseSetup("/dbunit-data/initial-data.xml")
@Transactional
public class HUserDaoTest {

    @Autowired
    private UserDao userDao;


    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/expected/person/create.xml", table = "PERSON")
    public void testCreate() throws Exception {

        User newUser = new User();
        newUser.setLogin("created");
        newUser.setPassword("cretedPass");
        newUser.setEmail("created@gmail.com");
        newUser.setFirstName("createdName");
        newUser.setLastName("createdSurname");
        Date birthday = new GregorianCalendar(1986, Calendar.JUNE, 6).getTime();
        newUser.setBirthday(birthday);
        newUser.setRole(new Role(1L, "Admin"));

        userDao.create(newUser);

    }

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/expected/person/update.xml", table = "PERSON")
    public void testUpdate() throws Exception {

        User updUser = new User();
        updUser.setId(5L);
        updUser.setLogin("testUser_5");
        updUser.setPassword("passUp");
        updUser.setEmail("emailUp@up.ua");
        updUser.setFirstName("nameUp");
        updUser.setLastName("surnameUp");
        Date birthday = new GregorianCalendar(1999, Calendar.DECEMBER, 31).getTime();
        updUser.setBirthday(birthday);
        updUser.setRole(new Role(1L, "Admin"));

        userDao.update(updUser);
        userDao.findByLogin("test");
    }

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/expected/person/remove.xml", table = "PERSON")
    public void testRemove() throws Exception {
        User remUser = new User();
        remUser.setLogin("testUser_5");
        userDao.remove(remUser);
    }

    @Test(timeout = 2000L)
    public void testFindAll() throws Exception {
        int expectedListSize = 5;

        List<User> users = userDao.findAll();

        assertNotNull("list shouldn't be null", users);
        assertEquals("actual list size should be: 5", expectedListSize, users.size());
    }

    @Test(timeout = 2000L)
    public void testFindByLogin() throws Exception {
        User actualUser = userDao.findByLogin("testUser_1");
        User expectedUser = getExpectedUser();

        assertNotNull("user shouldn't be null", actualUser);
        assertEquals("users should be equal", expectedUser, actualUser);
    }

    @Test(timeout = 2000L)
    public void testFindByWrongLogin() throws Exception {
        User actualUser = userDao.findByLogin("wrongLogin");
        assertNull("user should be null", actualUser);
    }

    @Test(timeout = 2000L)
    public void testFindByEmail() throws Exception {
        User actualUser = userDao.findByEmail("testUser_1@gmail.com");
        User expectedUser = getExpectedUser();

        assertNotNull("user shouldn't be null", actualUser);
        assertEquals("users should be equal", expectedUser, actualUser);
    }

    @Test(timeout = 2000L)
    public void testFindByWrongEmail() throws Exception {
        User actualUser = userDao.findByEmail("wrongEmail");
        assertNull("user should be null", actualUser);
    }


    private User getExpectedUser() {
        User user = new User();

        user.setId(1L);
        user.setLogin("testUser_1");
        user.setPassword("testUser_1");
        user.setEmail("testUser_1@gmail.com");
        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        Date birthday = new GregorianCalendar(1986, Calendar.JANUARY, 1).getTime();
        user.setBirthday(birthday);
        user.setRole(new Role(1L, "Admin"));

        return user;
    }

}