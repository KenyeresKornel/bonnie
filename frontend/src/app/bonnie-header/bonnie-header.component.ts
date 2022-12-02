import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from 'generated-client';
import { UserService } from '../userService';

@Component({
  selector: 'bonnie-header',
  templateUrl: './bonnie-header.component.html',
  styleUrls: ['./bonnie-header.component.css']
})
export class BonnieHeaderComponent implements OnInit {

  constructor(protected userService: UserService,private router: Router) { }

  ngOnInit(): void {
  }

  toMyOrders() {
    this.router.navigate(["/my-orders"]);
  }

  toUnassignedOrders() {
    this.router.navigate(["/unassigned-orders"]);
  }

}
