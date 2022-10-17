import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from "@angular/material/dialog";
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatFormFieldModule  } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { BonnieHeaderComponent } from './bonnie-header/bonnie-header.component';
import { OrderTableComponent } from './order-table/order-table.component';
import { AllOrdersComponent } from './all-orders/all-orders.component';
import { MyOrdersComponent } from './my-orders/my-orders.component';
import { UnassignedOrdersComponent } from './unassigned-orders/unassigned-orders.component';
import { UsersComponent } from './users/users.component';
import { OrderControllerService, UserControllerService } from 'generated-client';
import { HttpClientModule } from '@angular/common/http';
import { OrderDetailsComponent } from './order-details/order-details.component';
import { TrackingNumberComponent } from './common/tracking-number/tracking-number.component';
import { BASE_PATH } from 'generated-client';

@NgModule({
  declarations: [
    AppComponent,
    BonnieHeaderComponent,
    OrderTableComponent,
    AllOrdersComponent,
    MyOrdersComponent,
    UnassignedOrdersComponent,
    UsersComponent,
    OrderDetailsComponent,
    TrackingNumberComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule
  ],
  providers: [
    { provide: BASE_PATH, useValue: "http://localhost:8082" },
    OrderControllerService,
    UserControllerService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
