package com.nix.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView index(ModelAndView modelAndView) {
        log.debug("invoke index page");
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/access_denied", method = RequestMethod.GET)
    public String accessDenied() {
        log.debug("invoke access denied page");
        return "access_denied";
    }

}
