package com.cgi.bonnie.storage.order;

import com.cgi.bonnie.businessrules.Status;
import com.cgi.bonnie.businessrules.order.Order;
import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserStorage;
import com.cgi.bonnie.storage.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class OrderStorage implements com.cgi.bonnie.businessrules.order.OrderStorage {

    private OrderRepository orderRepository;

    private UserStorage userStorage;

    private OrderMapper mapper;

    private UserMapper userMapper = new UserMapper();

    @Autowired
    public OrderStorage(OrderRepository orderRepository, UserStorage userStorage, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.userStorage = userStorage;
        this.mapper = mapper;
    }

    public boolean save(Order o) {
        try {
            orderRepository.save(mapper.fromOrder(o));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long create(Order order) {
        try {
            return orderRepository.save(mapper.fromOrder(order)).getId();
        } catch (Exception e) {
            return 0L;
        }
    }

    public Order findByAssembler(User assembler) {
        //return mapper.fromEntity(orderRepository.findByAssembler(assembler));
        return null;
    }

    public List<Order> findAll() {
        return orderRepository.findAll().stream().map(mapper::fromEntity).collect(Collectors.toList());
    }

    public List<Order> findAllByStatus(Status status) {
        return orderRepository.findAllByStatus(status).stream().map(mapper::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<Order> findAllByAssignedTo(Long user) {
        return orderRepository.findAllByAssignedTo(user).stream().map(mapper::fromEntity).collect(Collectors.toList());
    }

    public List<Order> findAllByShopOrderId(String shopOrderId) {
        return orderRepository.findAllByShopOrderId(shopOrderId).stream().map(mapper::fromEntity).collect(Collectors.toList());
    }


    public Order load(long id) {
        return mapper.fromEntity(orderRepository.findById(id).orElseThrow(() -> new IllegalStateException("Order not found")));
    }
}
