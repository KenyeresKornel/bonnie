package com.cgi.hexagon.commplugin.kafka;

import com.cgi.hexagon.communicationplugin.SendRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JsonOrderMapper {

    private final Logger log = LoggerFactory.getLogger(JsonOrderMapper.class.getName());
    private final ObjectMapper objectMapper = JsonMapper.builder().addModule( new JavaTimeModule()).build();

    public OrderJson read(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, OrderJson.class);
        } catch (JsonProcessingException e) {
            log.error("Json conversion error in OrderJson :" + jsonString, e);
            return null;
        }
    }

    public List<OrderJson> readAll(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<OrderJson>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Json conversion error in :" + jsonString, e);
            return null;
        }
    }

    public String write(SendRequest sendRequest) {
        try {
            return objectMapper.writeValueAsString(sendRequest);
        } catch (JsonProcessingException e) {
            log.error("Json conversion error in :" + sendRequest.toString(), e);
            return null;
        }
    }
}
