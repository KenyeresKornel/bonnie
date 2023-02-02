import { Component, OnInit } from '@angular/core';
import { Order, OrderControllerService } from 'generated-client';

@Component({
  selector: 'app-my-orders',
  templateUrl: './my-orders.component.html',
  styleUrls: ['./my-orders.component.css']
})
export class MyOrdersComponent implements OnInit {

  orders: Order[] = [];

  constructor(protected orderControllerService: OrderControllerService) { }

  ngOnInit(): void {
    this.orderControllerService.getMyOrders().subscribe(myOrders => {
      this.orders = myOrders.filter(myOrder => {
        return (myOrder.status != 'SHIPPED');
      });
    });
  }

}
