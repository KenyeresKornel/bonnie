package com.cgi.bonnie.storage.order;

import com.cgi.bonnie.businessrules.order.Order;
import com.cgi.bonnie.businessrules.user.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    @Autowired
    private UserStorage userStorage;

    public AssemblyOrder fromOrder(Order order) {
        return new AssemblyOrder(order.getId())
                .withStatus(order.getStatus())
                .withShopOrderId(order.getShopOrderId())
                .withGoodsId(order.getGoodsId())
                .withAssignedTo(null == order.getAssignedTo() ? null : order.getAssignedTo().getId())
                .withQuantity(order.getQuantity())
                .withMetadata(order.getMetadata())
                .withPlacementDate(order.getPlacementDate())
                .withLastUpdated(order.getLastUpdate())
                .withTrackingNr(order.getTrackingNr());
    }

    public Order fromEntity(AssemblyOrder orderEntity) {
        return new Order()
                .withId(orderEntity.getId())
                .withStatus(orderEntity.getStatus())
                .withShopOrderId(orderEntity.getShopOrderId())
                .withGoodsId(orderEntity.getGoodsId())
                .withAssignedTo(null == orderEntity.getAssignedTo() || 0 == orderEntity.getAssignedTo() ? null : userStorage.load(orderEntity.getAssignedTo()))
                .withQuantity(orderEntity.getQuantity())
                .withMetadata(orderEntity.getMetadata())
                .withPlacementDate(orderEntity.getPlacementDate())
                .withLastUpdate(orderEntity.getLastUpdated())
                .withTrackingNr(orderEntity.getTrackingNr());
    }

    public List<AssemblyOrder> fromOrders(Iterable<Order> orders) {
        ArrayList<AssemblyOrder> ret = new ArrayList<>();
        orders.forEach(item -> ret.add(fromOrder(item)));
        return ret;
    }

    public List<Order> fromEntities(Iterable<AssemblyOrder> assemblyOrders) {
        ArrayList<Order> ret = new ArrayList<>();
        assemblyOrders.forEach(item -> ret.add(fromEntity(item)));
        return ret;
    }
}
