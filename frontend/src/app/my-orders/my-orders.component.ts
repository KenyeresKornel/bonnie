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
        let now = new Date().getTime();
        let oneDayFromLastUpdate = new Date(myOrder.lastUpdate!.toString()).getTime() + 86400000;
        return (oneDayFromLastUpdate > now && myOrder.status == 'SHIPPED') || (myOrder.status!='SHIPPED');
      });
    });    
  }  

}
