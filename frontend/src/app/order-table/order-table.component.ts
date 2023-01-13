import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from "@angular/material/dialog";
import { TrackingNumberComponent } from '../common/tracking-number/tracking-number.component';
import { Order, OrderControllerService } from 'generated-client';
import { Router } from '@angular/router';
@Component({
  selector: 'order-table',
  templateUrl: './order-table.component.html',
  styleUrls: ['./order-table.component.css']
})
export class OrderTableComponent implements OnInit {

  @Input() orders: Order[] = [];

  displayedColumns: string[] = ['id', 'quantity', 'status', 'assignedTo', 'trackingNr' ];

  constructor(private dialog: MatDialog,private orderControllerService: OrderControllerService, private router: Router) { }

  ngOnInit(): void { }

  openTrackingNr(order: Order): void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;

    dialogConfig.data = order;

    this.dialog.open(TrackingNumberComponent, dialogConfig);
  }

  releaseOrder(order : number): void{ 
    this.orderControllerService.releaseOrder(order).subscribe();
    this.router.routeReuseStrategy.shouldReuseRoute = function() { return false; };
    this.router.navigate([this.router.url]);
  }

  claimOrder(order : number): void{ 
    this.orderControllerService.assignToMe(order).subscribe();
    this.router.routeReuseStrategy.shouldReuseRoute = function() { return false; };
    this.router.navigate([this.router.url]);
  }

  finishOrder(order : number): void{ 
    this.orderControllerService.finishOrder(order).subscribe();
    this.router.routeReuseStrategy.shouldReuseRoute = function() { return false; };
    this.router.navigate([this.router.url]);
  }
}
