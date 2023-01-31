import { HttpClient } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { BASE_PATH } from "generated-client";
import { Observable } from "rxjs";
import { OauthCookieService } from './oauthCookieService';

@Injectable()
export class UserService {

    basePath: string;
    loggedIn: boolean = false;

    constructor(
        protected httpClient: HttpClient,
        protected oauthCookieService: OauthCookieService,
        @Inject(BASE_PATH) basePath: string) {

        this.basePath = basePath;

    }

    isLoggedIn() : boolean {
        return this.loggedIn;
    }

    updateLoggedInIfOauthCookieLoggedIn() {
      if (this.oauthCookieService.isOauthLoggedIn()) {
        this.setLoggedIn(true);
      }
    }

    setLoggedIn(val : boolean) {
        this.loggedIn = val;
        if (!val) {
          this.oauthCookieService.deleteOauthLoggedInCookie();
        }
    }

    public login(email: string, password: string) : Observable<Object> {
        const body = {
            username: email,
            password: password
        };
        return this.httpClient.request('post', `${this.basePath}/login`,
            {
                body: body,
                responseType: 'text'
            }
        );
    }

    public logout() {
        console.log('Base path for logout: ' + this.basePath);
        return this.httpClient.request('post', `${this.basePath}/logout`);
    }

}
