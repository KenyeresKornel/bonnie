package com.cgi.bonnie.businessrules.order;

import com.cgi.bonnie.businessrules.Status;
import com.cgi.bonnie.businessrules.user.AuthUserStorage;
import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserService;
import com.cgi.bonnie.businessrules.user.UserStorage;
import com.cgi.bonnie.communicationplugin.MessageService;
import com.cgi.bonnie.schema.OrderStatus;
import com.cgi.bonnie.schema.OrderStatusUpdateJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class.getName());

    final private OrderStorage orderStorage;

    final private UserService userService;

    final private MessageService messageService;

    public OrderService(OrderStorage loader, UserStorage userStorage, MessageService messageService, AuthUserStorage authUserStorage) {
        this.orderStorage = loader;
        this.userService = new UserService(userStorage, authUserStorage);
        this.messageService = messageService;
    }

    public Order loadOrder(long id) {
        return orderStorage.load(id);
    }

    public List<Order> findAllByStatus(Status status){
        return orderStorage.findAllByStatus(status);
    }

    public boolean releaseOrder(long id) {
        try {
            Order order = loadOrder(id);
            User currentUser = userService.getCurrentUser();
            if (order.getStatus() == Status.CLAIMED && order.getAssignedTo().getId() == currentUser.getId()) {
                order.setAssignedTo(null);
                order.setStatus(Status.NEW);
                order.setLastUpdate(LocalDateTime.now());
                if (orderStorage.save(order)) {
                    messageService.send(createSendRequest(order));
                    // Is it released, when stored, but coldn't sent a message about it?
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean claimOrder(long orderId) {
        Order order = loadOrder(orderId);
        User currentUser = userService.getCurrentUser();
        if (order == null) {
            return false;
        }
        if (null == order.getAssignedTo() && order.getStatus() == Status.NEW) {
            if (null != currentUser) {
                order.setAssignedTo(currentUser);
                order.setStatus(Status.CLAIMED);
                order.setLastUpdate(LocalDateTime.now());
                if (orderStorage.save(order)) {
                    messageService.send(createSendRequest(order));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean setTrackingNumber(long id, String trackingNr) {
        User currentUser = userService.getCurrentUser();
        if (null != trackingNr && !trackingNr.isEmpty()) {
            try {
                Order order = loadOrder(id);
                if (order.getStatus() == Status.ASSEMBLED && order.getAssignedTo().getId() == currentUser.getId()) {
                    order.setStatus(Status.SHIPPED);
                    order.setTrackingNr(trackingNr);
                    order.setLastUpdate(LocalDateTime.now());
                    if (orderStorage.save(order)) {
                        messageService.send(createSendRequest(order));
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    public void createOrders(List<Order> orders) {
        for (Order order : orders) {
            createOrder(order);
        }
    }

    public List<Order> findAllByAssembler(Long id) {
        return orderStorage.findAllByAssignedTo(id);
    }

    public List<Order> getMyOrders() {
        User user = userService.getCurrentUser();
        return orderStorage.findAllByAssignedTo(user.getId());
    }

    public long createOrder(Order order) {
        log.debug("orders arrived to Bonnie :-) ");
        if (!isNewOrder(order)) {
            log.error(" False or duplicated orderID in : {}", order);
            return -1;
        }
        if (order.getQuantity() < 1) {
            log.error(" Invalid quantity in {}", order);
            return -1;
        }
        if (order.getShopOrderId() == null || order.getShopOrderId().isBlank()) {
            log.error("ShopOrderId cannot be null or blank");
            return -1;
        }
        if (orderStorage.findAllByShopOrderId(order.getShopOrderId()).size() > 0) {
            log.error(" ShopOrderId [{}] already exists {}", order.getShopOrderId(), order);
            return -1;
        }
        order.setStatus(Status.NEW);
        order.setAssignedTo(null);
        if (order.getLastUpdate() == null) {
            order.setLastUpdate(LocalDateTime.now());
        }
        long id = orderStorage.create(order);
        if (id > 0L) {
            order.setId(id);
            log.debug("Order is created: {}", order);
        } else
            log.error("Can't created an order: {}", order);
        return id;
    }

    private boolean isNewOrder(Order order) {
        if (order.getId() <= 1)
            return true;
        try {
            orderStorage.load(order.getId());
        } catch (IllegalStateException e) {
            return true;
        }
        return false;
    }

    public boolean updateStatus(long orderId, Status status) {
        try{
            User currentUser = userService.getCurrentUser();
            Order order = loadOrder(orderId);
            if (order.getStatus().equals(Status.NEW) || order.getAssignedTo().getId() == currentUser.getId() && !order.getStatus().equals(Status.SHIPPED)) {
                order.setStatus(status);
                order.setLastUpdate(LocalDateTime.now());
                if (orderStorage.save(order)) {
                    messageService.send(createRequest(order.getShopOrderId(), status));
                    return true;
                }
            }
            return false;
        }catch (Exception e) {
            return false;
        }
    }

    public boolean finishOrder(long orderId) {
        User currentUser = userService.getCurrentUser();
        Order order = loadOrder(orderId);
        if (order != null && order.getStatus() == Status.CLAIMED && order.getAssignedTo().getId() == currentUser.getId()) {
            order.setStatus(Status.ASSEMBLED);
            if (orderStorage.save(order)) {
                messageService.send(createRequest(order.getShopOrderId(), Status.ASSEMBLED));
                return true;
            }
        }
        return false;
    }

    public OrderStatusUpdateJson createRequest(String shopOrderId, Status status) {
        return new OrderStatusUpdateJson().withShopOrderId(shopOrderId)
                .withStatus(OrderStatus.valueOf(status.name()));
    }

    public OrderStatusUpdateJson createSendRequest(Order order) {
        return new OrderStatusUpdateJson()
                .withStatus(OrderStatus.valueOf(order.getStatus().name()))
                .withShopOrderId(order.getShopOrderId())
                .withMetadata(order.getMetadata())
                .withTrackingNr(order.getTrackingNr());
    }
}
