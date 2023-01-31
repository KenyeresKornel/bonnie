import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor,
    HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserService } from './userService';

@Injectable()
export class SecurityInterceptor implements HttpInterceptor {

    constructor(private userService: UserService, protected router: Router) { }


    private handleAuthError(err: HttpErrorResponse): Observable<any> {

        if (err.status === 401 || err.status === 403) {
            this.router.navigateByUrl(`/login-form`);
            this.userService.setLoggedIn(false);
            return of(err.message);
        }
        return throwError(err);
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        request = request.clone({
            withCredentials: true
        });

        return next.handle(request).pipe(catchError(err => this.handleAuthError(err)));
    }
}
