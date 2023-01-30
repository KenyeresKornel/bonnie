import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';

import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormFieldModule  } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BonnieHeaderComponent } from './bonnie-header/bonnie-header.component';
import { OrderTableComponent } from './order-table/order-table.component';
import { LoginPageComponent } from './login-page/login-page.component';
import { MyOrdersComponent } from './my-orders/my-orders.component';
import { UnassignedOrdersComponent } from './unassigned-orders/unassigned-orders.component';
import { OrderControllerService, UserControllerService } from 'generated-client';
import { OrderDetailsComponent } from './order-details/order-details.component';
import { TrackingNumberComponent } from './common/tracking-number/tracking-number.component';
import { BASE_PATH } from 'generated-client';
import { SecurityInterceptor } from './security-interceptor';
import { UserService } from './userService';

import { environment } from 'src/environments/environment';


@NgModule({
  declarations: [
    AppComponent,
    BonnieHeaderComponent,
    OrderTableComponent,
    BonnieHeaderComponent,
    LoginPageComponent,
    MyOrdersComponent,
    UnassignedOrdersComponent,
    OrderDetailsComponent,
    TrackingNumberComponent,
    LoginPageComponent,
    OrderTableComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSnackBarModule,
    MatTableModule,
    MatToolbarModule,
    MatTooltipModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: BASE_PATH, useValue: environment.backendURL },
    { provide: HTTP_INTERCEPTORS, useClass: SecurityInterceptor, multi: true },
    OrderControllerService,
    UserControllerService,
    UserService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(private router: Router) {
    this.router.routeReuseStrategy.shouldReuseRoute = function() { return false; };
 }
}
