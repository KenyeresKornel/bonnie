package com.cgi.bonnie.storage.user;

import com.cgi.bonnie.businessrules.Role;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class AssemblyUser implements Serializable {

    @Id
    private Long id;

    private String name;

    private String email;

    private String password;

    private Role role;

    public AssemblyUser() {
    }

    public AssemblyUser withId(Long id) {
        this.setId(id);
        return this;
    }

    public AssemblyUser withName(String name) {
        this.setName(name);
        return this;
    }

    public AssemblyUser withPassword(String password) {
        this.setPassword(password);
        return this;
    }

    public AssemblyUser withRole(Role role) {
        this.setRole(role);
        return this;
    }

    public AssemblyUser withEmail(String email) {
        this.setEmail(email);
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
