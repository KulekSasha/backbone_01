package com.nix.controller;

import com.nix.config.ControllerTestConfig;
import com.nix.config.WebAppConfig;
import com.nix.model.Role;
import com.nix.model.User;
import com.nix.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
public class UserControllerTest {

    @Autowired
    private UserService userServiceMock;
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test(timeout = 2000L)
    public void testUserGetUserPage() throws Exception {
        User user = getExpectedUser();
        Principal principal = user::getLogin;
        when(userServiceMock.findByLogin(user.getLogin())).thenReturn(user);

        mockMvc.perform(get("/user/user").principal(principal))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("user", user))
                .andExpect(view().name("user/user"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/user/user.jsp"));

        verify(userServiceMock, times(1)).findByLogin(user.getLogin());
    }

    private User getExpectedUser() {
        return new User(1L, "testUser_1", "testUser_1",
                "testUser_1@gmail.com", "Ivan", "Ivanov",
                new GregorianCalendar(1986, Calendar.JANUARY, 1).getTime(),
                new Role(1, "Admin"));
    }
}