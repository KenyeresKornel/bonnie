import { Component } from '@angular/core';
import { Router } from '@angular/router';

import { UserService } from './userService';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'bonnie-frontend';

    constructor(protected userService: UserService, protected router: Router) {
      userService.updateLoggedInIfOauthCookieLoggedIn();
      if (!userService.isLoggedIn()) {
        this.router.navigateByUrl(`/login-form`);
      }
    }

}
