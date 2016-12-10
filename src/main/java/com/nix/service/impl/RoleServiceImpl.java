package com.nix.service.impl;

import com.nix.dao.RoleDao;
import com.nix.model.Role;
import com.nix.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(@Qualifier("roleDao") RoleDao roleDao) {
        log.info("Instantiate {}", this.getClass().getSimpleName());
        this.roleDao = roleDao;
    }

    @Override
    public void create(Role role) {
        log.trace("Invoke create with param: {}", role);
        roleDao.create(role);
    }

    @Override
    public void update(Role role) {
        log.trace("Invoke update with param: {}", role);
        roleDao.update(role);
    }

    @Override
    public void remove(Role role) {
        log.trace("Invoke remove with param: {}", role);
        roleDao.remove(role);
    }

    @Override
    public Role findByName(String name) {
        log.trace("Invoke findByName with param: {}", name);
        return roleDao.findByName(name);
    }
}
