package com.nix.config;


import com.nix.service.RoleService;
import com.nix.service.UserService;
import com.nix.service.impl.UserServiceImpl;
import net.tanesha.recaptcha.ReCaptchaImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static java.util.Arrays.asList;

@Configuration
public class ControllerTestConfig {

    @Bean
    public UserService userService() {
        return Mockito.mock(UserServiceImpl.class);
    }

    @Bean
    public RoleService roleService() {
        return Mockito.mock(RoleService.class);
    }

    @Bean
    public ReCaptchaImpl reCaptcha() {
        return Mockito.mock(ReCaptchaImpl.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {

        GrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        GrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");

        UserDetails adminUserDetails = new User("adminLogin", "adminPass",
                asList(adminAuthority));
        UserDetails userUserDetails = new User("userLogin", "userPass",
                asList(userAuthority));

        return new InMemoryUserDetailsManager(asList(adminUserDetails, userUserDetails));
    }

}
