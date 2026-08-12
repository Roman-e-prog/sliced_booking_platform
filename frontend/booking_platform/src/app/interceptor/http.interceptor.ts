import {
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { Observable, BehaviorSubject, catchError, filter, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class JWTInterceptor implements HttpInterceptor {
  //exclude the urls that should be not intercepted
  private excludedUrls: string[] = [
    'assets/',
    '/api/auth/login',
    '/api/auth/register',
    '/api/auth/uniqueEmail',
    '/api/auth/uniqueUsername',
    '/api/auth/refresh'
  ];
  //set a boolean for is refreshing
  private isRefreshing = false;
  //catcher Subject
  private refreshSubject = new BehaviorSubject<string | null>(null);

  constructor(
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    //exclude do nothing
    if (this.isExcluded(req.url)) {
      return next.handle(req);
    }
    //catch the token from localstorage
     let token = null;

  if (isPlatformBrowser(this.platformId)) {
    token = localStorage.getItem('token');
  }
    //clone the token
    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }

    return next.handle(authReq).pipe(
      catchError((err: HttpErrorResponse) => {

        if (err.status !== 401) {
          return throwError(() => err);
        }

        if (this.isRefreshUrl(req.url)) {
          return throwError(() => err);
        }

        return this.handle401Error(req, next);
      })
    );
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap(response => {
          this.isRefreshing = false;
          this.refreshSubject.next(response.token);

          const retryReq = req.clone({
            setHeaders: { Authorization: `Bearer ${response.token}` }
          });

          return next.handle(retryReq);
        }),
        catchError(refreshErr => {
          this.isRefreshing = false;
          this.snackBar.open('Session expired. Please log in again.', 'error', {
            duration: 3000,
            panelClass: ['error-snackbar']
          });
          this.authService.logout();
          this.router.navigate(['/login']);

          return throwError(() => refreshErr);
        })
      );
    }

    return this.refreshSubject.pipe(
      filter(token => token !== null),
      switchMap(token => {
        const retryReq = req.clone({
          setHeaders: { Authorization: `Bearer ${token}` }
        });
        return next.handle(retryReq);
      })
    );
  }

  private isExcluded(url: string): boolean {
    return this.excludedUrls.some(excluded => url.includes(excluded));
  }

  private isRefreshUrl(url: string): boolean {
    return url.includes('/api/auth/refresh');
  }
}
