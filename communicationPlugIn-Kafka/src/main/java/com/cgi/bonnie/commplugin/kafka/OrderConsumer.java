package com.cgi.bonnie.commplugin.kafka;

import com.cgi.bonnie.businessrules.order.OrderService;
import com.cgi.bonnie.schema.OrderJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Service
public class OrderConsumer {

    private final Logger log = LoggerFactory.getLogger(JsonOrderMapper.class.getName());
    private OrderService orderService;
    private JsonOrderMapper jsonOrderMapper = new JsonOrderMapper();
    private OrderMapper orderMapper= new OrderMapper();

    @Value("${spring.bonnie.kafka.topic.order}")
    private String topicName;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupID;

    @Autowired
    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Kafka consumer is started for Group: [{}] and  Topic: [{}]", groupID, topicName);
    }

    @KafkaListener(topics = "${spring.bonnie.kafka.topic.order}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        log.info("Message received in Group: [{}] Topic: [{}] Message: [{}]", groupID, topicName, message);

        List<OrderJson> jsonOrders;

        if (message.trim().startsWith("[")) {//Json array
            jsonOrders = jsonOrderMapper.readAll(message);
        } else {
            jsonOrders = Collections.singletonList(jsonOrderMapper.read(message));
        }

        if (!jsonOrders.isEmpty()) {
            orderService.createOrders(orderMapper.fromOrderJsonList(jsonOrders));
        }
    }
}
