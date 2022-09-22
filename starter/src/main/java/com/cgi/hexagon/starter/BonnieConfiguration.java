package com.cgi.hexagon.starter;

import com.cgi.hexagon.bonnierest.BonnieRestConfiguration;
import com.cgi.hexagon.businessrules.order.IOrderService;
import com.cgi.hexagon.businessrules.order.OrderService;
import com.cgi.hexagon.businessrules.user.IUserService;
import com.cgi.hexagon.businessrules.user.UserService;
import com.cgi.hexagon.commplugin.kafka.TopicConfig;
import com.cgi.hexagon.h2storage.H2StorageApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TopicConfig.class, BonnieRestConfiguration.class, H2StorageApplication.class})
public class BonnieConfiguration {

    @Bean
    OrderService orderService(IOrderService orderStorage, IUserService userService) {
        return new OrderService(orderStorage, userService);
    }

    @Bean
    UserService userService(IUserService userStorage) {
        return new UserService(userStorage);
    }
}
