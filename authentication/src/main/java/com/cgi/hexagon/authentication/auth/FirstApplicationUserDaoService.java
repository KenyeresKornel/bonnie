package com.cgi.hexagon.authentication.auth;

import com.cgi.hexagon.h2storage.user.H2AssemblyUserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cgi.hexagon.authentication.security.ApplicationUserRole.ASSEMBLER;

@Repository("first")
public class FirstApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;

    private final H2AssemblyUserStorage userStorage;

    @Autowired
    public FirstApplicationUserDaoService(PasswordEncoder passwordEncoder, H2AssemblyUserStorage userStorage) {
        this.passwordEncoder = passwordEncoder;
        this.userStorage = userStorage;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUsers() {
        List<ApplicationUser> applicationUsers = new ArrayList<>();
        userStorage.getAssemblyUsers().stream().forEach(
                user -> applicationUsers.add(
                        new ApplicationUser(user.getName(),
                                            passwordEncoder.encode(user.getPassword()),
                                            ASSEMBLER.getGrantedAuthorities(),
                                            true,
                                            true,
                                            true,
                                            true
                        )
                )
        );
        return applicationUsers;
    }

}
