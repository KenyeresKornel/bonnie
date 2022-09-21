package com.cgi.hexagon.businessrules.user;

import com.cgi.hexagon.businessrules.Role;

public interface IUserService {

    User load(long id);

    long createUser(String name, String password, Role role);

    boolean save(User user);

}
