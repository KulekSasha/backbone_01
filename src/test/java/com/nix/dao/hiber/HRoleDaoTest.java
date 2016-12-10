package com.nix.dao.hiber;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.nix.config.DaoTestConfig;
import com.nix.dao.RoleDao;
import com.nix.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DaoTestConfig.class)
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class,
})
@DatabaseSetup("/dbunit-data/initial-data.xml")
@Transactional
public class HRoleDaoTest {

    @Autowired
    private RoleDao roleDao;

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/expected/role/create.xml", table = "PERSON_ROLE")
    public void testCreate() throws Exception {
        Role role = new Role(11L, "NewRole");
        roleDao.create(role);
    }

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/expected/role/update.xml", table = "PERSON_ROLE")
    public void testUpdate() throws Exception {
        Role role = new Role(2L, "User");
        roleDao.update(role);
        roleDao.findByName("");
    }

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/initial-data.xml", table = "PERSON_ROLE")
    public void testUpdateWithWrongParam() throws Exception {
        Role role = new Role(15L, "UserUpdated");
        roleDao.update(role);
        roleDao.findByName("");
    }

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/initial-data.xml", table = "PERSON_ROLE")
    public void testUpdateEmptyRole() throws Exception {
        Role role = new Role();
        roleDao.update(role);
        roleDao.findByName("");
    }

    @Test(timeout = 2000L)
    @ExpectedDatabase(value = "/dbunit-data/expected/role/remove.xml", table = "PERSON_ROLE")
    public void testRemove() throws Exception {
        Role role = new Role(10L, "RoleForRemove");
        roleDao.remove(role);
    }

    @Test(timeout = 2000L)
    public void testFindByName() throws Exception {
        Role expectedRole = new Role(1L, "Admin");

        Role actualRole = roleDao.findByName("Admin");

        assertEquals("id should be equals",
                expectedRole.getId(), actualRole.getId());

        assertEquals("names should be equals",
                expectedRole.getName(), actualRole.getName());
    }


}
