package com.cgi.bonnie.configuration;

import com.cgi.bonnie.businessrules.order.OrderService;
import com.cgi.bonnie.businessrules.order.OrderStorage;
import com.cgi.bonnie.businessrules.user.AuthUserStorage;
import com.cgi.bonnie.businessrules.user.UserService;
import com.cgi.bonnie.businessrules.user.UserStorage;
import com.cgi.bonnie.communicationplugin.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@ComponentScan({"com.cgi.bonnie.storage.user", "com.cgi.bonnie.storage.order"})
@EnableJdbcRepositories({"com.cgi.bonnie.storage.user", "com.cgi.bonnie.storage.order"})
public class StorageConfiguration {

    @Bean
    OrderService orderService(OrderStorage orderStorage,
                              UserStorage userStorage,
                              MessageService sender,
                              AuthUserStorage authUserStorage) {
        return new OrderService(orderStorage, userStorage, sender, authUserStorage);
    }

    @Bean
    UserService userService(UserStorage userStorage,
                            AuthUserStorage authUserStorage) {
        return new UserService(userStorage, authUserStorage); }

}
