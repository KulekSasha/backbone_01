package com.nix.controller;

import com.nix.config.ControllerTestConfig;
import com.nix.config.WebAppConfig;
import com.nix.model.Role;
import com.nix.model.User;
import com.nix.service.RoleService;
import com.nix.service.UserService;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {ControllerTestConfig.class,
                WebAppConfig.class,})
@WebAppConfiguration
public class RegistrationControllerTest {

    @Mock
    private UserService userServiceMock;
    @Mock
    private RoleService roleServiceMock;
    @Mock
    private ReCaptchaImpl reCaptchaMock;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    @InjectMocks
    RegistrationController registrationController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        MockitoAnnotations.initMocks(this);
    }

    @Test(timeout = 2000L)
    public void registerNewUser_GetRegistrationForm() throws Exception {
        User newUser = new User();

        mockMvc.perform(get("/registration/new"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(model().attribute("newUser", newUser))
                .andExpect(view().name("registration/registration"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/registration/registration.jsp"));

        verifyZeroInteractions(userServiceMock, roleServiceMock, reCaptchaMock);
    }

    @Test(timeout = 2000L)
    public void registerNewValidUserPost() throws Exception {

        MultiValueMap<String, String> paramMap = getValidPostParams();
        Role expectedRole = new Role(2L, "User");
        String remoteAddress = "127.0.0.1";
        ReCaptchaResponse captchaResponse = new ReCaptchaRespStub(true, "");

        when(roleServiceMock.findByName(paramMap.getFirst("role")))
                .thenReturn(expectedRole);
        when(reCaptchaMock.checkAnswer(remoteAddress,
                paramMap.getFirst("recaptcha_challenge_field"),
                paramMap.getFirst("recaptcha_response_field")))
                .thenReturn(captchaResponse);
        doNothing().when(userServiceMock).create(any(User.class));


        mockMvc.perform(post("/registration/new").params(paramMap))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/login.jsp"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().attributeExists("newUser"));

        verify(roleServiceMock, times(1)).findByName(paramMap.getFirst("role"));
        verify(reCaptchaMock, times(1)).checkAnswer(remoteAddress,
                paramMap.getFirst("recaptcha_challenge_field"),
                paramMap.getFirst("recaptcha_response_field"));
        verify(userServiceMock, times(1)).create(any(User.class));
        verify(userServiceMock, times(1)).findByLogin(paramMap.getFirst("login"));
        verifyNoMoreInteractions(userServiceMock, roleServiceMock, reCaptchaMock);
    }

    @Test(timeout = 2000L)
    public void registerNewUserPassNotEqualPost() throws Exception {

        MultiValueMap<String, String> paramMap = getValidPostParams();
        paramMap.set("passConfirm", "notEqualPass");
        Role expectedRole = new Role(2L, "User");
        String remoteAddress = "127.0.0.1";
        ReCaptchaResponse captchaResponse = new ReCaptchaRespStub(true, "");

        when(roleServiceMock.findByName(paramMap.getFirst("role")))
                .thenReturn(expectedRole);
        when(reCaptchaMock.checkAnswer(remoteAddress,
                paramMap.getFirst("recaptcha_challenge_field"),
                paramMap.getFirst("recaptcha_response_field")))
                .thenReturn(captchaResponse);
        doNothing().when(userServiceMock).create(any(User.class));

        mockMvc.perform(post("/registration/new").params(paramMap))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("newUser", "password"))
                .andExpect(view().name("registration/registration"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/registration/registration.jsp"));

        verify(roleServiceMock, times(1)).findByName(paramMap.getFirst("role"));
        verify(reCaptchaMock, times(1)).checkAnswer(remoteAddress,
                paramMap.getFirst("recaptcha_challenge_field"),
                paramMap.getFirst("recaptcha_response_field"));
        verify(userServiceMock, never()).create(any(User.class));
        verify(userServiceMock, times(1)).findByLogin(paramMap.getFirst("login"));
        verifyNoMoreInteractions(userServiceMock, roleServiceMock, reCaptchaMock);
    }

    private MultiValueMap<String, String> getValidPostParams() {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("login", "testLogin");
        paramMap.add("password", "testPassword");
        paramMap.add("passConfirm", "testPassword");
        paramMap.add("email", "test@mail.test");
        paramMap.add("firstName", "testFirstName");
        paramMap.add("lastName", "testLastName");
        paramMap.add("birthday", "2016-03-25");
        paramMap.add("role", "User");
        paramMap.add("recaptcha_challenge_field", "challenge");
        paramMap.add("recaptcha_response_field", "response");
        return paramMap;
    }

    private static class ReCaptchaRespStub extends ReCaptchaResponse {

        ReCaptchaRespStub(boolean valid, String errorMessage) {
            super(valid, errorMessage);
        }
    }
}

