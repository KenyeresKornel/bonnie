package com.cgi.hexagon.bonnierest.model;

public class UserRequest {

    private String name;

    private String password;

    private String role;

    public UserRequest(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public UserRequest() {
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
