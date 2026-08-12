import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { of } from 'rxjs';

class SnackBarMock {
  open = jasmine.createSpy('open');
}

class RouterMock {
  navigate = jasmine.createSpy('navigate');
}

describe('AuthService', () => {

  let service: AuthService;
  let httpMock: HttpTestingController;
  let snackBar: SnackBarMock;
  let router: RouterMock;

  beforeEach(() => {
    snackBar = new SnackBarMock();
    router = new RouterMock();

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: MatSnackBar, useValue: snackBar },
        { provide: Router, useValue: router },
        { provide: PLATFORM_ID, useValue: 'browser' } // wichtig für SSR‑Zweig
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    // localStorage mocken
    spyOn(localStorage, 'setItem').and.callFake(() => {});
    spyOn(localStorage, 'getItem').and.callFake(() => null);
    spyOn(localStorage, 'removeItem').and.callFake(() => {});
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ---------------------------------------------------------
  // 1. login(): erfolgreicher Login
  // ---------------------------------------------------------
  it('should store token and user on successful login', () => {
    const mockResponse = {
      token: 'abc123',
      user: { username: 'roman', prename: 'Roman', lastname: 'Test', email: 'roman@test.de', street: '', house_number: '', postal_code: 0, town: '', country: '' }
    };

    service.login('roman@test.de', 'Test123!').subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');

    req.flush(mockResponse);

    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'abc123');
    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(mockResponse.user));
  });

  // ---------------------------------------------------------
  // 2. login(): Fehlerfall → SnackBar + throwError
  // ---------------------------------------------------------
  it('should show snackbar on login error', () => {
    service.login('roman@test.de', 'wrong').subscribe({
      error: () => {}
    });

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');

    req.flush(
      { message: 'Invalid credentials' },
      { status: 401, statusText: 'Unauthorized' }
    );

    expect(snackBar.open).toHaveBeenCalledWith('Invalid credentials', 'error', {
      duration: 3000,
      panelClass: ['error-snackbar']
    });
  });

  // ---------------------------------------------------------
  // 3. getUser(): sollte User aus localStorage laden
  // ---------------------------------------------------------
  it('should return user from localStorage', () => {
    const mockUser = { username: 'roman' };
    (localStorage.getItem as jasmine.Spy).and.returnValue(JSON.stringify(mockUser));

    const user = service.getUser();

    expect(user).toEqual(mockUser);
  });

  // ---------------------------------------------------------
  // 4. logout(): sollte localStorage löschen + navigate
  // ---------------------------------------------------------
  it('should logout and navigate to root', () => {
    service.logout();

    expect(localStorage.removeItem).toHaveBeenCalledWith('user');
    expect(localStorage.removeItem).toHaveBeenCalledWith('token');
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  // ---------------------------------------------------------
  // 5. refreshToken(): sollte neuen Token speichern
  // ---------------------------------------------------------
  it('should refresh token and update user', () => {
    const mockResponse = {
      token: 'newToken',
      user: { username: 'roman' }
    };

    service.refreshToken().subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/refresh');
    expect(req.request.method).toBe('POST');

    req.flush(mockResponse);

    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'newToken');
    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(mockResponse.user));
  });

  // ---------------------------------------------------------
  // 6. isTokenExpired(): kein Token → expired
  // ---------------------------------------------------------
  it('should return true when no token exists', () => {
    (localStorage.getItem as jasmine.Spy).and.returnValue(null);

    const expired = service.isTokenExpired();

    expect(expired).toBeTrue();
  });

  // ---------------------------------------------------------
  // 7. isTokenExpired(): gültiger Token
  // ---------------------------------------------------------
  it('should return false when token is valid', () => {
    const payload = { exp: Math.floor(Date.now() / 1000) + 3600 }; // +1h
    const token = `header.${btoa(JSON.stringify(payload))}.sig`;

    (localStorage.getItem as jasmine.Spy).and.returnValue(token);

    const expired = service.isTokenExpired();

    expect(expired).toBeFalse();
  });

  // ---------------------------------------------------------
  // 8. isTokenExpired(): abgelaufen
  // ---------------------------------------------------------
  it('should return true when token is expired', () => {
    const payload = { exp: Math.floor(Date.now() / 1000) - 10 }; // abgelaufen
    const token = `header.${btoa(JSON.stringify(payload))}.sig`;

    (localStorage.getItem as jasmine.Spy).and.returnValue(token);

    const expired = service.isTokenExpired();

    expect(expired).toBeTrue();
  });

  // ---------------------------------------------------------
  // 9. initAuthState(): kein Token → logout
  // ---------------------------------------------------------
  it('should logout when no token or user exists', () => {
    (localStorage.getItem as jasmine.Spy).and.returnValue(null);

    service.initAuthState();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  // ---------------------------------------------------------
  // 10. initAuthState(): gültiger Token → userSubject setzen
  // ---------------------------------------------------------
  it('should restore user when token is valid', () => {
    const mockUser = {  prename:"Roman",
                        lastname:"Tester",
                        username:"roman",
                        email:"roman@test.de",
                        street:"Musterstraße",
                        house_number:"1",
                        postal_code:12345,
                        town:"Musterstadt",
                        country:"Deutschland", };

    const payload = { exp: Math.floor(Date.now() / 1000) + 3600 };
    const token = `header.${btoa(JSON.stringify(payload))}.sig`;

    (localStorage.getItem as jasmine.Spy).and.callFake((key: string) => {
      if (key === 'token') return token;
      if (key === 'user') return JSON.stringify(mockUser);
      return null;
    });

    service.initAuthState();

    service.user.subscribe(u => {
      expect(u).toEqual(mockUser);
    });
  });

});

