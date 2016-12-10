package com.nix.api.soap;

import com.nix.api.soap.exception.UserValidationException;
import com.nix.api.soap.exception.ValidationExceptionDetails;
import com.nix.model.User;
import com.nix.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@WebService(endpointInterface = "com.nix.api.soap.UserWebService")
public class UserWebServiceImpl implements UserWebService {

    public static final Logger log = LoggerFactory.getLogger(UserWebServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;
    @Autowired
    private MessageSource messageSource;


    @Override
    public List<User> getAllUsers() {
        log.debug("invoke getAllUsers");
        return userService.findAll();
    }

    @Override
    public User getUserByLogin(String login) throws WebServiceException {
        log.debug("invoke getUserByLogin with login: {}", login);
        User user = userService.findByLogin(login);

        if (user != null) {
            return user;
        } else {
            throw new WebServiceException("user with login [" + login + "] not found");
        }
    }

    @Override
    public void createUser(User newUser) throws UserValidationException {
        log.debug("invoke createUser with user: {}", newUser);
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);

        boolean isLoginExist = false;
        if (userService.findByLogin(newUser.getLogin()) != null) {
            isLoginExist = true;
        }

        if (violations.size() > 0 || isLoginExist) {
            List<ValidationExceptionDetails> listErrors = new ArrayList<>();

            violations.forEach(v -> listErrors.add(
                    new ValidationExceptionDetails(v.getPropertyPath().toString(),
                            v.getMessage())));

            if (isLoginExist) {
                listErrors.add(new ValidationExceptionDetails("login",
                        messageSource.getMessage("non.unique.login", null, Locale.ENGLISH)));
            }

            throw new UserValidationException(listErrors);
        }

        try {
            userService.create(newUser);
        } catch (Exception e) {
            log.error("can not create user: {}; exception: {}", newUser, e);
            throw new WebServiceException("can not save user", e);
        }
    }

    @Override
    public void updateUser(User updUser) throws UserValidationException {
        log.debug("invoke updateUser with user: {}", updUser);
        Set<ConstraintViolation<User>> violations = validator.validate(updUser);

        if (violations.size() > 0) {
            List<ValidationExceptionDetails> listErrors = new ArrayList<>();

            violations.forEach(v -> listErrors.add(
                    new ValidationExceptionDetails(v.getPropertyPath().toString(),
                            v.getMessage())));

            throw new UserValidationException(listErrors);
        }
        try {
            userService.update(updUser);
        } catch (Exception e) {
            log.error("can not update user: {}; exception: {}", updUser, e);
            throw new WebServiceException("can not save user", e);
        }
    }

    @Override
    public void deleteUser(String login) {
        log.debug("invoke deleteUser with login: {}", login);

        User user = userService.findByLogin(login);
        if (user != null) {
            userService.remove(user);
        } else {
            throw new WebServiceException("user with login [" + login + "] does not exist");
        }
    }
}
