package com.cgi.bonnie.commplugin.kafka;

import com.cgi.bonnie.businessrules.order.Order;
import com.cgi.bonnie.schema.OrderJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderMapper {
    private final Logger log = LoggerFactory.getLogger(OrderMapper.class);

    public static Order fromOrderJson(OrderJson orderJson) {
        return new Order().withShopOrderId(orderJson.getShopOrderId())
                .withGoodsId(orderJson.getGoodsId())
                .withQuantity(orderJson.getQuantity())
                .withPlacementDate(orderJson.getPlacementDate())
                .withMetadata(orderJson.getMetadata());
    }

    public List<Order> fromOrderJsonList(List<OrderJson> orderJsons) {
        List<Order> orders = orderJsons.stream().filter(Objects::nonNull).map(OrderMapper::fromOrderJson).collect(Collectors.toList());
        log.info("There are {} income order and parsed {} items", orderJsons.size(), orders.size());
        return orders;
    }
}
