package com.nix.controller;

import com.nix.model.Role;
import com.nix.model.User;
import com.nix.service.RoleService;
import com.nix.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private static final int DATE_LENGTH = 10;

    private UserService userService;
    private RoleService roleService;

    @Autowired
    public void setUserService(@Qualifier("userService") UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(@Qualifier("roleService") RoleService roleService) {
        this.roleService = roleService;
    }

    @InitBinder
    public void bindingPreparation(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        CustomDateEditor birthdayEditor = new CustomDateEditor(dateFormat, true, DATE_LENGTH);
        binder.registerCustomEditor(Date.class, "birthday", birthdayEditor);
        binder.registerCustomEditor(Role.class, "role", new RoleEditor(roleService));
    }


    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public ModelAndView admin(ModelAndView modelAndView, Principal principal,
                              HttpServletRequest req) {

        User user = null;
        if (principal != null) {
            user = userService.findByLogin(principal.getName());
            log.debug("get users page for user: {}", principal.getName());
        }

        req.getSession().setAttribute("loginUser", user);

        modelAndView.setViewName("admin/admin");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/{login}/delete", method = RequestMethod.POST)
    public void adminDeleteUser(@PathVariable("login") String login) {
        log.debug("delete user with login: {}", login);
        User user = new User();
        user.setLogin(login);
        userService.remove(user);
    }

    @RequestMapping(value = "/admin/users/{login}/edit", method = RequestMethod.GET)
    public ModelAndView adminEditUser(ModelAndView modelAndView,
                                      @PathVariable(value = "login") String login) {

        log.debug("get user-edit form for user with login: {}", login);

        User user = userService.findByLogin(login);
        modelAndView.addObject("editableUser", user);

        modelAndView.setViewName("admin/admin_edit");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/{login}/edit", method = RequestMethod.POST)
    public ModelAndView adminSaveEditUser(ModelAndView modelAndView,
                                          @Valid @ModelAttribute("editableUser") User editableUser,
                                          BindingResult result,
                                          @RequestParam("passConfirm") String passConfirm) {

        validateConfirmPassword("editableUser", result, editableUser, passConfirm);

        log.debug("adminSaveEditUser - ModelAttribute - editableUser: {}", editableUser);
        log.debug("adminSaveEditUser - BindingResult - errors: {}", result);

        if (result.hasErrors()) {
            modelAndView.addObject("errors", result);
            modelAndView.setViewName("admin/admin_edit");
            return modelAndView;
        }

        userService.update(editableUser);
        modelAndView.setViewName("admin/admin");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/add", method = RequestMethod.GET)
    public ModelAndView adminAddUser(ModelAndView modelAndView) {
        modelAndView.addObject("newUser", new User());
        modelAndView.setViewName("admin/admin_add");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/users/add", method = RequestMethod.POST)
    public ModelAndView adminAddUserSave(ModelAndView modelAndView,
                                         @Valid @ModelAttribute("newUser") User newUser,
                                         BindingResult result,
                                         @RequestParam("passConfirm") String passConfirm) {

        validateConfirmPassword("newUser", result, newUser, passConfirm);

        if (userService.findByLogin(newUser.getLogin()) != null) {
            FieldError loginUniqueError = new FieldError("newUser", "login",
                    newUser.getLogin(), false, new String[]{"non.unique.login"},
                    null, "not unique");
            result.addError(loginUniqueError);
        }

        log.debug("adminAddUserSave - ModelAttribute - newUser: {}", newUser);
        log.debug("adminAddUserSave - BindingResult - errors: {}", result);

        if (result.hasErrors()) {
            modelAndView.addObject("errors", result);
            modelAndView.setViewName("admin/admin_add");
            return modelAndView;
        }

        userService.create(newUser);
        modelAndView.setViewName("admin/admin");
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
