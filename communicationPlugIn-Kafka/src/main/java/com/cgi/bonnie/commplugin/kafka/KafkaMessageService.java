package com.cgi.bonnie.commplugin.kafka;

import com.cgi.bonnie.communicationplugin.MessageService;
import com.cgi.bonnie.schema.OrderStatusUpdateJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class KafkaMessageService implements MessageService {

    private final Logger log = LoggerFactory.getLogger(KafkaMessageService.class.getName());
    private final JsonOrderMapper jsonOrderMapper = new JsonOrderMapper();
    @Autowired
    final private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${spring.bonnie.kafka.topic.message}")
    private String topicName;

    public KafkaMessageService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Message producer for topic [{}] running.", topicName);
    }

    @Override
    public void send(OrderStatusUpdateJson request) {
        kafkaTemplate.send(topicName, jsonOrderMapper.write(request));
        log.debug("[{}] topic a message sent: {}", topicName, request);
    }
}
