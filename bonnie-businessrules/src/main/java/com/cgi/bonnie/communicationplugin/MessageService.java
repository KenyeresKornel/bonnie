package com.cgi.bonnie.communicationplugin;

import com.cgi.bonnie.schema.OrderStatusUpdateJson;

public interface MessageService {

    void send(OrderStatusUpdateJson request);

}
