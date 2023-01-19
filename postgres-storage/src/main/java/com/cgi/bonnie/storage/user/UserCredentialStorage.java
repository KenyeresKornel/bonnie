package com.cgi.bonnie.storage.user;

import com.cgi.bonnie.businessrules.user.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserCredentialStorage implements com.cgi.bonnie.businessrules.user.UserCredentialStorage {

    private final UserRepository repository;

    private final UserMapper mapper;

    @Autowired
    public UserCredentialStorage(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<UserCredentials> findAll() {
        return repository.findAll().stream().map(mapper::credentialsFromAssemblyUser).collect(Collectors.toList());
    }

    @Override
    public UserCredentials findByName(String name) {
        return mapper.credentialsFromAssemblyUser(repository.findByName(name));
    }

    @Override
    public UserCredentials findByEmail(String email) {
        return mapper.credentialsFromAssemblyUser(repository.findByEmail(email));
    }
}
