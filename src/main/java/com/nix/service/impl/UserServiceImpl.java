package com.nix.service.impl;

import com.nix.dao.UserDao;
import com.nix.model.User;
import com.nix.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(@Qualifier("userDao") UserDao userDao) {
        log.info("Instantiate {}", this.getClass().getSimpleName());
        this.userDao = userDao;
    }

    @Override
    public void create(User user) {
        log.trace("Invoke create with param: {}", user);
        userDao.create(user);
    }

    @Override
    public void update(User user) {
        log.trace("Invoke update with param: {}", user);
        userDao.update(user);
    }

    @Override
    public void remove(User user) {
        log.trace("Invoke remove with param: {}", user);
        userDao.remove(user);
    }

    @Override
    public List<User> findAll() {
        log.trace("Invoke findAll");
        return userDao.findAll();
    }

    @Override
    public User findByLogin(String login) {
        log.debug("Invoke findByLogin with param: {}", login);
        return userDao.findByLogin(login);
    }

    @Override
    public User findByEmail(String email) {
        log.debug("Invoke findByEmail with param: {}", email);
        return userDao.findByEmail(email);
    }

}
