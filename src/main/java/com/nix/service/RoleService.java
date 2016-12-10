package com.nix.service;

import com.nix.model.Role;

public interface RoleService {

    void create(Role role);

    void update(Role role);

    void remove(Role role);

    Role findByName(String name);

}
