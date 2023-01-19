package com.cgi.bonnie.storage.user;

import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserCredentials;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public AssemblyUser assemblyUserFromUser(User user, String password) {
        if (user != null) {
            return new AssemblyUser()
                    .withId(user.getId())
                    .withName(user.getName())
                    .withRole(user.getRole())
                    .withEmail(user.getEmail())
                    .withPassword(password);
        }
        return null;
    }

    public User userFromAssemblyUser(AssemblyUser userEntity) {
        if (userEntity != null) {
            return new User()
                    .withId(userEntity.getId())
                    .withName(userEntity.getName())
                    .withRole(userEntity.getRole())
                    .withEmail(userEntity.getEmail());
        }
        return null;
    }

    public UserCredentials credentialsFromAssemblyUser(AssemblyUser userEntity) {
        if (userEntity != null) {
            return new UserCredentials(userEntity.getName(), userEntity.getEmail(), userEntity.getPassword());
        }
        return null;
    }


}
