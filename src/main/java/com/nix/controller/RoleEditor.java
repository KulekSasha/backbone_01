package com.nix.controller;

import com.nix.model.Role;
import com.nix.service.RoleService;

import java.beans.PropertyEditorSupport;


public class RoleEditor extends PropertyEditorSupport {

    private RoleService roleService;

    public RoleEditor(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text != null) {
            Role role = roleService.findByName(text);
            setValue(role);
            return;
        }
        throw new java.lang.IllegalArgumentException("argument is null");
    }
}
