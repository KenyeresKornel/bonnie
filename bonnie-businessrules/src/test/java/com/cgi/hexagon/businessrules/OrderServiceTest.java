package com.cgi.hexagon.businessrules;

import com.cgi.hexagon.businessrules.order.Order;
import com.cgi.hexagon.businessrules.order.OrderStorage;
import com.cgi.hexagon.businessrules.order.OrderService;
import com.cgi.hexagon.businessrules.user.User;
import com.cgi.hexagon.businessrules.user.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    final long ORDER_ID = 1L;
    final long USER_ID = 1L;

    final String TRACING_NUMBER = "1";

    OrderStorage orderLoader;

    OrderService orderService;

    UserStorage userStorage;

    MessageService sender;

    @BeforeEach
    public void setup() {
        orderLoader = Mockito.mock(OrderStorage.class);
        when(orderLoader.save(any())).thenReturn(true);
        userStorage = Mockito.mock(UserStorage.class);
        sender = Mockito.mock(MessageService.class);
        orderService = new OrderService(orderLoader, userStorage, sender);
    }

    @Test
    public void expectCallToALoaderWhenLoadIsCalled() {
        Order toBeLoaded = getOrder();

        when(orderLoader.load(ORDER_ID)).thenReturn(toBeLoaded);
        Order loadedOrder = orderService.loadOrder(ORDER_ID);
        assertEquals(toBeLoaded, loadedOrder, "Loaded order should be the same one we provided to the mock. ");
    }

    @Test
    public void expectReleaseClaimedOrderReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        assertTrue(orderService.releaseOrder(ORDER_ID), "Should return with true");
    }

    @Test
    public void expectReleaseClaimedOrderSetsAssemblerToNull() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        orderService.releaseOrder(ORDER_ID);

        verify(orderLoader).save(argThat(order -> order.getAssembler() == null));
    }

    @Test
    public void expectReleaseClaimedOrderSetsStatusToNew() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        orderService.releaseOrder(ORDER_ID);

        verify(orderLoader).save(argThat(order -> order.getStatus() == Status.NEW));
    }

    @Test
    public void expectReleaseUnClaimedOrderReturnsFalse() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        assertFalse(orderService.releaseOrder(ORDER_ID), "Should return with false");
    }

    @Test
    public void expectReleaseNonExistingOrderReturnsFalse() {
        when(orderLoader.load(ORDER_ID)).thenReturn(null);

        assertFalse(orderService.releaseOrder(ORDER_ID), "Should return with false when order does not exists");
    }

    @Test
    public void expectReleaseReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        assertFalse(orderService.releaseOrder(ORDER_ID));
    }

    @Test
    public void expectClaimOrderReturnsFalseWhenOrderDoesNotExist() {
        when(orderLoader.load(ORDER_ID)).thenReturn(null);

        assertFalse(orderService.claimOrder(ORDER_ID, USER_ID), "Should return with false");
    }

    @Test
    public void expectClaimNewOrderReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertTrue(orderService.claimOrder(ORDER_ID, USER_ID));
    }

    @Test
    public void expectClaimNewOrderReturnsFalseWhenUserDoesNotExist() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        when(userStorage.load(USER_ID)).thenReturn(null);

        assertFalse(orderService.claimOrder(ORDER_ID, USER_ID));
    }

    @Test
    public void expectClaimNewOrderReturnsFalseWhenOrderStatusISNotNew() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.SHIPPED));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertFalse(orderService.claimOrder(ORDER_ID, USER_ID));
    }

    @Test
    public void expectClaimNewOrderReturnsFalseWhenThereIsAnAssembler() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withAssembler(getUser()));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertFalse(orderService.claimOrder(ORDER_ID, USER_ID));
    }

    @Test
    public void expectClaimOrderSavesWithAssembler() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        orderService.claimOrder(ORDER_ID, USER_ID);

        verify(orderLoader).save(argThat(order -> USER_ID == order.getAssembler().getId()));
    }

    @Test
    public void expectClaimOrderSavesWithClaimedStatus() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        orderService.claimOrder(ORDER_ID, USER_ID);

        verify(orderLoader).save(argThat(order -> order.getStatus() == Status.CLAIMED));
    }

    @Test
    public void expectClaimOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());
        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertFalse(orderService.claimOrder(ORDER_ID, USER_ID));
    }

    @Test
    public void expectSetTrackingNumberReturnsFalseWhenOrderDoesNotExists() {
        when(orderLoader.load(ORDER_ID)).thenReturn(null);

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberReturnsFalseWhenOrderIsNotAssembled() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberSavesOrderWithTrackingNumber() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED));

        orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER);

       verify(orderLoader).save(argThat(order -> order.getMetadata().get("trackingNr") == TRACING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED));

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberOrderReturnsFalseWhenTNrIsNull() {
        assertFalse(orderService.setTrackingNumber(ORDER_ID, null));
    }

    @Test
    public void expectSetTrackingNumberOrderReturnsFalseWhenTNrIsEmpty() {
        assertFalse(orderService.setTrackingNumber(ORDER_ID, ""));
    }

    @Test
    public void expectSetTrackingNumberSavesOrderWithShippedStatus() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED));

        orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER);

        verify(orderLoader).save(argThat(order -> order.getStatus() == Status.SHIPPED));
    }

    @Test
    public void expectSetTrackingNumberReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED));

        assertTrue(orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberCallsSender() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED));

        orderService.setTrackingNumber(ORDER_ID, TRACING_NUMBER);

        verify(sender).send(argThat(sendRequest -> sendRequest.getOrderId() == ORDER_ID && sendRequest.getStatus() == Status.SHIPPED && TRACING_NUMBER.equals(sendRequest.getMetadata().get("trackingNr"))));
    }

    @Test
    public void expectCreateOrderCallsCreate() {
        final String productId = "1";
        final int quantity = 1;
        final long assignedTo = 1L;
        final Status status = Status.NEW;

        orderService.createOrder(productId, quantity, assignedTo, status);

        verify(orderLoader).create(productId, quantity, assignedTo, status);
    }

    @Test
    public void expectUpdateStatusReturnsFalseWhenOrderDoesNotExist() {
        when(orderLoader.load(ORDER_ID)).thenReturn(null);

        assertFalse(orderService.updateStatus(ORDER_ID, Status.SHIPPED));
    }

    @Test
    public void expectUpdateStatusReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        when(orderLoader.save(any())).thenReturn(true);

        assertTrue(orderService.updateStatus(ORDER_ID, Status.SHIPPED));
    }

    @Test
    public void expectUpdateStatusSetsStatus() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        orderService.updateStatus(ORDER_ID, Status.SHIPPED);

        verify(orderLoader).save(argThat(order -> order.getStatus() == Status.SHIPPED));
    }

    @Test
    public void expectUpdateStatusCallsSender() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        when(orderLoader.save(any())).thenReturn(true);

        orderService.updateStatus(ORDER_ID, Status.SHIPPED);

        verify(sender).send(argThat(sendRequest -> sendRequest.getStatus() == Status.SHIPPED && sendRequest.getOrderId() == ORDER_ID));
    }

    @Test
    public void expectUpdateStatusReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        assertFalse(orderService.updateStatus(ORDER_ID, Status.NEW));
    }

    @Test
    public void expectFinishOrderReturnsFalseWhenOrderDoesNotExist() {
        when(orderLoader.load(ORDER_ID)).thenReturn(null);

        assertFalse(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectFinishOrderReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        assertTrue(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectFinishOrderCallsSender() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        orderService.finishOrder(ORDER_ID);

        verify(sender).send(argThat(sendRequest -> sendRequest.getStatus() == Status.ASSEMBLED));
    }

    @Test
    public void expectFinishOrderReturnsFalseWhenStatusInNotClaimed() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        assertFalse(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectFinishOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED));

        assertFalse(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectGetAllOrdersCallsFindAll() {
        orderService.getAllOrders();

        verify(orderLoader).findAll();
    }

    private Order getOrder() {
        return new Order()
                .withStatus(Status.NEW)
                .withId(ORDER_ID)
                .withGoodsId("awesome kit");
    }

    private User getUser() {
        return new User()
                .withId(USER_ID)
                .withName("user")
                .withRole(Role.ASSEMBLER);
    }

}