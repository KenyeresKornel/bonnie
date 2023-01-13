import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatDialog, MatDialogConfig } from "@angular/material/dialog";

import { TrackingNumberComponent } from '../common/tracking-number/tracking-number.component';
import { Order, OrderControllerService } from 'generated-client';
import { Router } from '@angular/router';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {

  order?: Order = undefined;

  dataFields: object[] = [ ];

  displayedColumns: string[] = [ 'key', 'value' ];
  
  columnNameMapping: any = {
    shopOrderId : 'Shop Order Id',
    assignedTo: 'Assigned To',
    goodsId: 'Goods Id',
    quantity: 'Quantity',
    status: 'Status',
    lastUpdate: 'Last update',
    placementDate: 'Placement date',
    actions: 'Actions'
  };

  constructor(protected orderControllerService: OrderControllerService,
    private dialog: MatDialog,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.orderControllerService.getOrder(id).subscribe(order => {
      this.order = order;
      this.dataFields = Object.entries(order)
        .filter(([key, value]) =>  {
          if (value instanceof Object) {
            return false;
          }
          return Object.getOwnPropertyNames(this.columnNameMapping).indexOf(key) != -1;
        })
        .map(([key, value]) => ( { key, value } ));

        this.dataFields.push({
          key: 'assignedTo',
          value: !order.assignedTo ? '' : order.assignedTo?.name
        });
    });
  }

  openTrackingNr(order: Order): void {
    const dialogConfig = new MatDialogConfig();

    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;

    dialogConfig.data = order;

    this.dialog.open(TrackingNumberComponent, dialogConfig);
  }

  releaseOrder(order : number): void{ 
    this.orderControllerService.releaseOrder(order).subscribe();
  }
  claimOrder(order : number): void{ 
    this.orderControllerService.assignToMe(order).subscribe();
  }
  finishOrder(order : number): void{ 
    this.orderControllerService.finishOrder(order).subscribe();
  }
}
