import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, catchError, map, tap, throwError } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
interface User{
  prename:string;
  lastname:string;
  username:string;
  email:string;
  street:string;
  house_number:string;
  postal_code: Number;
  town: string;
  country:string;
}
interface AuthResponse{
  token:string;
  user: User;
}
export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  errorCode?: string;
  path: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  authUrl = "http://localhost:8080/api/auth"

  constructor(
    private httpClient:HttpClient, 
    private snackBar:MatSnackBar,
    private router:Router,
      @Inject(PLATFORM_ID) private platformId: Object,
  ) { }
  private userSubject = new BehaviorSubject<User | null>(null);
  public user = this.userSubject.asObservable();

login(email: string, password: string) {
  return this.httpClient.post<AuthResponse>(`${this.authUrl}/login`, { email, password })
    .pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
        this.userSubject.next(response.user);
      }),
      catchError((error: HttpErrorResponse) => {
        const apiError = error.error as ApiError;
        this.snackBar.open(apiError.message, 'error', {
          duration: 3000,
          panelClass:['error-snackbar']
        })
        return throwError(() => new Error(apiError.message));
      })
    );
  }
  getUser() {
  if (!isPlatformBrowser(this.platformId)) {
    return null;
  }
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  this.userSubject.next(user);
  return user;
}

  logout() {
  if (!isPlatformBrowser(this.platformId)) {
    // On server: just clear subject, no navigation, no localStorage
    this.userSubject.next(null);
    return;
  }

  localStorage.removeItem('user');
  localStorage.removeItem('token');
  this.userSubject.next(null);
  this.router.navigate(['/']);
}

    refreshToken() {
    return this.httpClient.post<AuthResponse>(
      `${this.authUrl}/refresh`,
      {},
      { withCredentials: true }
    ).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(response.user));
        this.userSubject.next(response.user);
      })
    );
  }
  isTokenExpired(): boolean {
    // On server: never treat as expired (let browser decide)
    if (!isPlatformBrowser(this.platformId)) {
      return false;
    }
     const token = localStorage.getItem('token');
    //if there is no token its expired
    if (!token){
      this.logout()
      return true;
    } 

    try {
      //I catch the expiration in milliseconds
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log(payload.exp, 'is there something')
      //if there is no payload its expired
      if (!payload?.exp) return true;
      //otherwise return the rest of time
      return payload.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }
  initAuthState() {
  if (!isPlatformBrowser(this.platformId)) return;

  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');

  // No token or no user → logout
  if (!token || !user) {
    this.logout();
    return;
  }

  // Token expired → logout
  if (this.isTokenExpired()) {
    this.logout();
    return;
  }

  // Token valid → restore user
  this.userSubject.next(JSON.parse(user));
}

}


