package com.nix.controller;

import com.nix.config.ControllerTestConfig;
import com.nix.config.SecurityConfig;
import com.nix.config.WebAppConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {ControllerTestConfig.class,
                WebAppConfig.class,
                SecurityConfig.class})
@WebAppConfiguration
public class SecurityTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity(springSecurityFilterChain))
                .build();
    }

    @Test(timeout = 2000L)
    public void adminPerformLogin() throws Exception {
        mockMvc.perform(post("/login").param("login", "adminLogin").param("pwd", "adminPass"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/users"));
    }

    @Test(timeout = 2000L)
    public void userPerformLogin() throws Exception {
        mockMvc.perform(post("/login").param("login", "userLogin").param("pwd", "userPass"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/user/user"));
    }

    @Test(timeout = 2000L)
    public void adminPerformLoginBadPassword() throws Exception {
        mockMvc.perform(post("/login").param("login", "adminLogin").param("pwd", "incorrect"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test(timeout = 2000L)
    public void userPerformLoginBadPassword() throws Exception {
        mockMvc.perform(post("/login").param("login", "userLogin").param("pwd", "incorrect"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test(timeout = 2000L)
    public void authenticatedAdminGetAdminPage() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("admin").password("adminPass").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/admin/admin.jsp"));
    }

    @Test(timeout = 2000L)
    public void authenticatedUserGetUserPage() throws Exception {
        mockMvc.perform(get("/user/user").with(user("user").password("userPass").roles("USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/user"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/user/user.jsp"));
    }

    @Test(timeout = 2000L)
    public void authenticatedUserGetAdminPage() throws Exception {
        mockMvc.perform(get("/admin/users").with(user("user").password("userPass").roles("USER")))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(forwardedUrl("/access_denied"));
    }

}
