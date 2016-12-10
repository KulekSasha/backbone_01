package com.nix.controller;


import com.nix.model.Role;
import com.nix.model.User;
import com.nix.service.RoleService;
import com.nix.service.UserService;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    private static final int DATE_LENGTH = 10;

    private UserService userService;
    private RoleService roleService;
    private ReCaptchaImpl reCaptcha;

    @Autowired
    public void setUserService(@Qualifier("userService") UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(@Qualifier("roleService") RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setReCaptcha(@Qualifier("reCaptcha") ReCaptchaImpl reCaptcha) {
        this.reCaptcha = reCaptcha;
    }

    @InitBinder
    public void bindingPreparation(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        CustomDateEditor birthdayEditor = new CustomDateEditor(dateFormat, true, DATE_LENGTH);
        binder.registerCustomEditor(Date.class, "birthday", birthdayEditor);
        binder.registerCustomEditor(Role.class, "role", new RoleEditor(roleService));
        binder.setDisallowedFields("recaptcha_challenge_field");
        binder.setDisallowedFields("recaptcha_response_field");
    }

    @RequestMapping(value = "registration/new", method = RequestMethod.GET)
    public ModelAndView registerNewUser(ModelAndView modelAndView) {

        log.debug("get form for new user registration");

        modelAndView.addObject("newUser", new User());
        modelAndView.setViewName("registration/registration");
        return modelAndView;
    }

    @RequestMapping(value = "registration/new", method = RequestMethod.POST)
    public ModelAndView registerNewUserPost(ModelAndView modelAndView,
                                            @Valid @ModelAttribute("newUser") User newUser,
                                            BindingResult result,
                                            @RequestParam("passConfirm") String passConfirm,
                                            @RequestParam("recaptcha_challenge_field") String challengeField,
                                            @RequestParam("recaptcha_response_field") String responseField,
                                            ServletRequest servletRequest) {

        validateConfirmPassword("newUser", result, newUser, passConfirm);

        if (userService.findByLogin(newUser.getLogin()) != null) {
            FieldError loginUniqueError = new FieldError("newUser", "login",
                    newUser.getLogin(), false, new String[]{"non.unique.login"},
                    null, "not unique");
            result.addError(loginUniqueError);
        }

        log.debug("registerNewUserPost - ModelAttribute - newUser: {}", newUser);
        log.debug("registerNewUserPost - BindingResult - errors: {}", result);

        ReCaptchaResponse reCaptchaResponse =
                reCaptcha.checkAnswer(servletRequest.getRemoteAddr(),
                        challengeField, responseField);

        if (!reCaptchaResponse.isValid()) {
            modelAndView.addObject("invalidRecaptcha", true);
        }

        if (result.hasErrors() || !reCaptchaResponse.isValid()) {
            modelAndView.addObject("errors", result);
            modelAndView.setViewName("registration/registration");
            return modelAndView;
        }

        userService.create(newUser);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    private void validateConfirmPassword(String objectName, BindingResult result,
                                         User user, String passConfirm) {
        if (!user.getPassword().equals(passConfirm)) {
            FieldError error = new FieldError(objectName, "password", passConfirm,
                    true, new String[]{"NotEqual.password"}, new String[]{}, "passwords not equal");

            result.addError(error);
        }

    }

}
