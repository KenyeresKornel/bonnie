package com.cgi.bonnie.cli;

import com.cgi.bonnie.commplugin.kafka.OrderJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

@Service
public class PlaceOrderKafkaProducer {

    @Value("${spring.bonnie.kafka.topic.order}")
    private String orderTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void placeOrder(String goods, int quantity) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        int year = LocalDate.now().getYear();
        int dayOfYear = LocalDate.now().getDayOfYear();
        OrderJson order = new OrderJson()
                .setGoodsId(goods)
                .setQuantity(quantity)
                .setShopOrderId(year + "/" + dayOfYear + "/" + new Random().nextInt(1000000))
                .setMetadata(objectNode);
        try {
            kafkaTemplate.send(orderTopic, objectMapper.writeValueAsString(order));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
