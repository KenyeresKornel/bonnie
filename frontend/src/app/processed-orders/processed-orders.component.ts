import {Component, OnInit} from '@angular/core';
import {Order, OrderControllerService} from 'generated-client';

@Component({
  selector: 'app-processed-orders',
  templateUrl: './processed-orders.component.html',
  styleUrls: ['./processed-orders.component.css']
})
export class ProcessedOrdersComponent implements OnInit {

  orders: Order[] = [];

  constructor(protected orderControllerService: OrderControllerService) { }

  ngOnInit(): void {
    this.orderControllerService.getMyOrders().subscribe(myOrders => {
      this.orders = myOrders.filter(myOrder => {
        return (myOrder.status == 'SHIPPED');
      });
    });
  }

}
