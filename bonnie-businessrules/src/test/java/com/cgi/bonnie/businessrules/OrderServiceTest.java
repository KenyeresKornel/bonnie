package com.cgi.bonnie.businessrules;

import com.cgi.bonnie.businessrules.order.Order;
import com.cgi.bonnie.businessrules.order.OrderService;
import com.cgi.bonnie.businessrules.order.OrderStorage;
import com.cgi.bonnie.businessrules.user.AuthUserStorage;
import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserStorage;
import com.cgi.bonnie.communicationplugin.MessageService;
import com.cgi.bonnie.schema.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    final long ORDER_ID = 1L;
    final long USER_ID = 1L;

    final String SHOP_ORDER_ID = "2022/Ord0001";

    final String TRACKING_NUMBER = "1";
    OrderStorage orderLoader;

    OrderService orderService;

    UserStorage userStorage;

    MessageService sender;

    AuthUserStorage authUserStorage;

    @BeforeEach
    public void setup() {
        orderLoader = Mockito.mock(OrderStorage.class);
        when(orderLoader.save(any())).thenReturn(true);
        userStorage = Mockito.mock(UserStorage.class);
        sender = Mockito.mock(MessageService.class);
        authUserStorage = Mockito.mock(AuthUserStorage.class);
        when(userStorage.findByUsername(any())).thenReturn(getUser());
        when(userStorage.findByEmail(any())).thenReturn(getUser());
        orderService = new OrderService(orderLoader, userStorage, sender, authUserStorage);
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
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

        assertTrue(orderService.releaseOrder(ORDER_ID), "Should return with true");
    }

    @Test
    public void expectReleaseClaimedOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

        when(orderLoader.save(any())).thenReturn(false);

        assertFalse(orderService.releaseOrder(ORDER_ID), "Should return with false");
    }

    @Test
    public void expectReleaseClaimedOrderReturnsFalseWhenUserNotAssigned() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser().withId(2L)));

        assertFalse(orderService.releaseOrder(ORDER_ID), "Should return with false");
    }

    @Test
    public void expectReleaseClaimedOrderSetsAssemblerToNull() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

        orderService.releaseOrder(ORDER_ID);

        verify(orderLoader).save(argThat(order -> order.getAssignedTo() == null));
    }

    @Test
    public void expectReleaseClaimedOrderSetsStatusToNew() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

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

        assertFalse(orderService.claimOrder(ORDER_ID), "Should return with false");
    }

    @Test
    public void expectClaimNewOrderReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertTrue(orderService.claimOrder(ORDER_ID));
    }

    @Test
    public void expectClaimNewOrderReturnsFalseWhenUserDoesNotExist() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        when(userStorage.load(USER_ID)).thenReturn(null);
        when(userStorage.findByEmail(any())).thenReturn(null);

        assertFalse(orderService.claimOrder(ORDER_ID));
    }

    @Test
    public void expectClaimNewOrderReturnsFalseWhenOrderStatusISNotNew() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.SHIPPED));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertFalse(orderService.claimOrder(ORDER_ID));
    }

    @Test
    public void expectClaimNewOrderReturnsFalseWhenThereIsAnAssembler() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withAssignedTo(getUser()));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertFalse(orderService.claimOrder(ORDER_ID));
    }

    @Test
    public void expectClaimOrderSavesWithAssembler() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        orderService.claimOrder(ORDER_ID);

        verify(orderLoader).save(argThat(order -> USER_ID == order.getAssignedTo().getId()));
    }

    @Test
    public void expectClaimOrderSavesWithClaimedStatus() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        orderService.claimOrder(ORDER_ID);

        verify(orderLoader).save(argThat(order -> order.getStatus() == Status.CLAIMED));
    }

    @Test
    public void expectClaimOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder());
        when(userStorage.load(USER_ID)).thenReturn(getUser());

        assertFalse(orderService.claimOrder(ORDER_ID));
    }

    @Test
    public void expectSetTrackingNumberReturnsFalseWhenOrderDoesNotExists() {
        when(orderLoader.load(ORDER_ID)).thenReturn(null);

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberReturnsFalseWhenOrderIsNotAssembled() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberSavesOrderWithTrackingNumber() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(getUser()));

        orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER);

        verify(orderLoader).save(argThat(order -> order.getTrackingNr().equals(TRACKING_NUMBER)));
    }

    @Test
    public void expectSetTrackingNumberOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(getUser()));

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER));
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
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(getUser()));

        orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER);

        verify(orderLoader).save(argThat(order -> order.getStatus() == Status.SHIPPED));
    }

    @Test
    public void expectSetTrackingNumberReturnsTrue() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(getUser()));

        assertTrue(orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberReturnsFalseWhenNotAssignedUser() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(getUser().withId(2L)));

        assertFalse(orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER));
    }

    @Test
    public void expectSetTrackingNumberCallsSender() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(getUser()));
        doNothing().when(sender).send(any());
        orderService.setTrackingNumber(ORDER_ID, TRACKING_NUMBER);

        verify(sender).send(argThat(sendRequest ->
                sendRequest.getShopOrderId().equals(SHOP_ORDER_ID)
                        && sendRequest.getStatus() == OrderStatus.SHIPPED
                        && TRACKING_NUMBER.equals(sendRequest.getTrackingNr())));
    }

    @Test
    public void expectCreateOrderCallsCreate() {
        final String productId = "1";
        final String shopOrderId = "1";
        final int quantity = 1;
        final Status status = Status.NEW;
        Order o = new Order().withGoodsId(productId)
                .withQuantity(quantity)
                .withShopOrderId(shopOrderId)
                .withStatus(status);

        orderService.createOrder(o);

        verify(orderLoader).create(o);
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
    public void expectUpdateStatusReturnsTrueWhenAssembled() {
        User user = getUser();
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.ASSEMBLED).withAssignedTo(user));

        when(userStorage.load(USER_ID)).thenReturn(user);

        when(orderLoader.save(any())).thenReturn(true);

        assertTrue(orderService.updateStatus(ORDER_ID, Status.SHIPPED));
    }

    @Test
    public void expectUpdateStatusReturnsFalseWhenUserNotAssigned() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withAssignedTo(getUser().withId(2L)).withStatus(Status.CLAIMED));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        when(orderLoader.save(any())).thenReturn(true);

        assertFalse(orderService.updateStatus(ORDER_ID, Status.ASSEMBLED));
    }

    @Test
    public void expectUpdateStatusReturnsFalseWhenStatusIsShipped() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withAssignedTo(getUser()).withStatus(Status.SHIPPED));

        when(userStorage.load(USER_ID)).thenReturn(getUser());

        when(orderLoader.save(any())).thenReturn(true);

        assertFalse(orderService.updateStatus(ORDER_ID, Status.ASSEMBLED));
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

        verify(sender).send(argThat(sendRequest ->
                sendRequest.getStatus() == OrderStatus.SHIPPED && sendRequest.getShopOrderId().equals(SHOP_ORDER_ID)));
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
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

        assertTrue(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectFinishOrderReturnsFalseWhenUserNotAssigned() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser().withId(2L)));

        assertFalse(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectFinishOrderCallsSender() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

        orderService.finishOrder(ORDER_ID);

        verify(sender).send(argThat(sendRequest -> sendRequest.getStatus() == OrderStatus.ASSEMBLED));
    }

    @Test
    public void expectFinishOrderReturnsFalseWhenStatusInNotClaimed() {
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.NEW));

        assertFalse(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectFinishOrderReturnsFalseWhenSaveFails() {
        when(orderLoader.save(any())).thenReturn(false);
        when(orderLoader.load(ORDER_ID)).thenReturn(getOrder().withStatus(Status.CLAIMED).withAssignedTo(getUser()));

        assertFalse(orderService.finishOrder(ORDER_ID));
    }

    @Test
    public void expectCreateOrderReturnsErrorWhenQuantityIsInvalid() {
        assertEquals(-1, orderService.createOrder(getOrder().withQuantity(-2)));
    }

    @Test
    public void expectCreateOrderReturnsErrorWhenOrderIsNotNew() {
        assertEquals(-1, orderService.createOrder(getOrder().withId(4)));
    }

    @Test
    public void expectCreateOrderSuceedsWhenLoadFails() {
        doThrow(new IllegalStateException()).when(orderLoader).load(2);

        assertEquals(0, orderService.createOrder(getOrder().withId(2)));
    }

    @Test
    public void expectCreateOrderReturnsErrorShowOrderIdAlreadyExists() {
        when(orderLoader.findAllByShopOrderId(SHOP_ORDER_ID)).thenReturn(
                Arrays.asList(
                        getOrder(), getOrder()
                )
        );

        assertEquals(-1, orderService.createOrder(getOrder().withQuantity(3).withShopOrderId(SHOP_ORDER_ID)));
    }

    @Test
    public void expectCreateOrderSucceeds() {
        final long ORDER_ID2 = 23;
        when(orderLoader.create(any())).thenReturn(ORDER_ID2);

        assertEquals(ORDER_ID2, orderService.createOrder(getOrder()));
    }

    @Test
    public void expectCreateNewOrderCallsCreate() {
        final long ORDER_ID2 = 23;
        when(orderLoader.create(any())).thenReturn(ORDER_ID2);

        orderService.createOrder(getOrder());

        verify(orderLoader).create(argThat(order -> order.getStatus() == Status.NEW && order.getAssignedTo() == null));
    }

    @Test
    public void expectCreateOrdersCallsCreate() {
        orderService.createOrders(
                Arrays.asList(
                        getOrder(), getOrder()
                )
        );

        assertEquals(2, Mockito.mockingDetails(orderLoader)
                .getInvocations()
                .stream()
                .filter(invocation -> invocation.getMethod().getName().equals("create"))
                .count());
    }

    private Order getOrder() {
        return new Order()
                .withStatus(Status.NEW)
                .withId(ORDER_ID)
                .withShopOrderId(SHOP_ORDER_ID)
                .withQuantity(2)
                .withGoodsId("awesome kit");
    }

    private User getUser() {
        return new User()
                .withId(USER_ID)
                .withName("user")
                .withEmail("user@user.com")
                .withRole(Role.ASSEMBLER);
    }
}