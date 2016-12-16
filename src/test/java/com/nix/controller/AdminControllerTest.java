package com.nix.controller;

import com.nix.config.ControllerTestConfig;
import com.nix.config.WebAppConfig;
import com.nix.model.Role;
import com.nix.model.User;
import com.nix.service.RoleService;
import com.nix.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {ControllerTestConfig.class,
                WebAppConfig.class,})
@WebAppConfiguration
public class AdminControllerTest {

    @Mock
    private UserService userServiceMock;
    @Mock
    private RoleService roleServiceMock;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    @InjectMocks
    AdminController adminController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test(timeout = 2000L)
    public void adminUsersPageGet() throws Exception {
        User user = getExpectedUser();
        Principal principal = user::getLogin;
        when(userServiceMock.findByLogin(user.getLogin())).thenReturn(user);

        mockMvc.perform(get("/admin/users").principal(principal))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin_new"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin.jsp"))
                .andDo(result -> {
                    HttpSession session = result.getRequest().getSession();
                    User loginUser = (User) session.getAttribute("loginUser");
                    Assert.assertEquals("user should be equal", loginUser, user);
                });

        verify(userServiceMock, times(1)).findByLogin(user.getLogin());
        verifyNoMoreInteractions(userServiceMock, roleServiceMock);
    }

//    @Test(timeout = 2000L)
//    public void adminDeleteUserPost() throws Exception {
//
//        String testLogin = "testUser_1";
//        User user = new User();
//        user.setLogin(testLogin);
//
//        mockMvc.perform(post("/admin/users/{login}/delete", testLogin))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(model().hasNoErrors());
//
//        verify(userServiceMock, times(1)).remove(user);
//        verifyNoMoreInteractions(userServiceMock, roleServiceMock);
//    }
//
//    @Test(timeout = 2000L)
//    public void adminEditUserGet() throws Exception {
//        User user = getExpectedUser();
//
//        when(userServiceMock.findByLogin(user.getLogin())).thenReturn(user);
//
//        mockMvc.perform(get("/admin/users/{login}/edit", user.getLogin()))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("editableUser", user))
//                .andExpect(view().name("admin/admin_edit"))
//                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin_edit.jsp"));
//
//        verify(userServiceMock, times(1)).findByLogin(user.getLogin());
//        verifyNoMoreInteractions(userServiceMock, roleServiceMock);
//    }
//
//    @Test(timeout = 2000L)
//    public void adminSaveEditUserPost() throws Exception {
//        MultiValueMap params = getValidPostParams();
//        User userAdmin = getExpectedUser();
//        Role roleAdmin = new Role(2L, "Admin");
//
//        when(roleServiceMock.findByName(userAdmin.getRole().getName()))
//                .thenReturn(roleAdmin);
//
//        mockMvc.perform(post("/admin/users/{login}/edit", userAdmin.getLogin()).params(params))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("editableUser", userAdmin))
//                .andExpect(model().hasNoErrors())
//                .andExpect(view().name("admin/admin"))
//                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin.jsp"));
//
//        verify(userServiceMock, times(1)).update(userAdmin);
//        verify(roleServiceMock, times(1)).findByName(userAdmin.getRole().getName());
//        verifyNoMoreInteractions(userServiceMock, roleServiceMock);
//    }
//
//    @Test(timeout = 2000L)
//    public void adminAddUserGet() throws Exception {
//        User user = new User();
//
//        mockMvc.perform(get("/admin/users/add"))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("newUser", user))
//                .andExpect(view().name("admin/admin_add"))
//                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin_add.jsp"));
//
//        verifyZeroInteractions(roleServiceMock, userServiceMock);
//    }
//
//    @Test(timeout = 2000L)
//    public void adminAddUserSavePost() throws Exception {
//        MultiValueMap params = getValidPostParams();
//        User newUser = getExpectedUser();
//        Role roleAdmin = new Role(2L, "Admin");
//
//        when(roleServiceMock.findByName(newUser.getRole().getName()))
//                .thenReturn(roleAdmin);
//        when(userServiceMock.findByLogin(newUser.getLogin()))
//                .thenReturn(null);
//
//        mockMvc.perform(post("/admin/users/add").params(params))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("newUser", newUser))
//                .andExpect(model().hasNoErrors())
//                .andExpect(view().name("admin/admin"))
//                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin.jsp"));
//
//        verify(roleServiceMock, times(1)).findByName(roleAdmin.getName());
//        verify(userServiceMock, times(1)).create(newUser);
//        verify(userServiceMock, times(1)).findByLogin(newUser.getLogin());
//        verifyNoMoreInteractions(roleServiceMock, userServiceMock);
//    }
//
//    @Test(timeout = 2000L)
//    public void adminAddUserNotUniqueLoginPost() throws Exception {
//        MultiValueMap params = getValidPostParams();
//        User newUser = getExpectedUser();
//        Role roleAdmin = new Role(2L, "Admin");
//
//        when(roleServiceMock.findByName(newUser.getRole().getName()))
//                .thenReturn(roleAdmin);
//        when(userServiceMock.findByLogin(newUser.getLogin()))
//                .thenReturn(newUser);
//
//        mockMvc.perform(post("/admin/users/add").params(params))
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("newUser", newUser))
//                .andExpect(model()
//                        .attributeHasFieldErrorCode("newUser", "login", "non.unique.login"))
//                .andExpect(model().attributeErrorCount("newUser", 1))
//                .andExpect(view().name("admin/admin_add"))
//                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin_add.jsp"));
//
//        verify(roleServiceMock, times(1)).findByName(roleAdmin.getName());
//        verify(userServiceMock, times(1)).findByLogin(newUser.getLogin());
//        verifyNoMoreInteractions(roleServiceMock, userServiceMock);
//    }
//
    private User getExpectedUser() {
        return new User(0L, "testUser_1", "testUser_1",
                "testUser_1@gmail.com", "Ivan", "Ivanov",
                new GregorianCalendar(1986, Calendar.JANUARY, 1).getTime(),
                new Role(2L, "Admin"));
    }
//
//    private MultiValueMap<String, String> getValidPostParams() {
//        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
//        paramMap.add("login", "testUser_1");
//        paramMap.add("password", "testUser_1");
//        paramMap.add("passConfirm", "testUser_1");
//        paramMap.add("email", "testUser_1@gmail.com");
//        paramMap.add("firstName", "Ivan");
//        paramMap.add("lastName", "Ivanov");
//        paramMap.add("birthday", "1986-01-01");
//        paramMap.add("role", "Admin");
//        return paramMap;
//    }
}