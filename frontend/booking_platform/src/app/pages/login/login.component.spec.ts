import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';

// Router‑Mock
class RouterMock {
  navigate = jasmine.createSpy('navigate');
}

// SnackBar‑Mock
class SnackBarMock {
  open = jasmine.createSpy('open');
}

// AuthService‑Mock
class AuthServiceMock {
  login = jasmine.createSpy('login');
}

describe('LoginComponent', () => {

  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthServiceMock;
  let router: RouterMock;
  let snackBar: SnackBarMock;

  beforeEach(async () => {
    authService = new AuthServiceMock();
    router = new RouterMock();
    snackBar = new SnackBarMock();

    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
        { provide: MatSnackBar, useValue: snackBar }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Komponente sollte korrekt erzeugt werden
  // ---------------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Form sollte korrekt initialisiert werden
  // ---------------------------------------------------------
  it('should initialize the form with email and password controls', () => {
    component.ngOnInit();

    expect(component.loginForm.contains('email')).toBeTrue();
    expect(component.loginForm.contains('password')).toBeTrue();
  });

  // ---------------------------------------------------------
  // 3. Submit: Erfolgreicher Login
  // ---------------------------------------------------------
  it('should call authService.login and navigate on success', fakeAsync(() => {
    component.ngOnInit();

    // gültiges Formular setzen
    component.loginForm.setValue({
      email: 'roman@test.de',
      password: 'Test123!'
    });

    // Mock: erfolgreicher Login
    authService.login.and.returnValue(of({}));

    component.onSubmit();
    tick();

    expect(authService.login).toHaveBeenCalledWith('roman@test.de', 'Test123!');
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  }));

  // ---------------------------------------------------------
  // 4. Submit: Fehlerfall → SnackBar anzeigen
  // ---------------------------------------------------------
  it('should show snackbar on login error', fakeAsync(() => {
    component.ngOnInit();

    component.loginForm.setValue({
      email: 'roman@test.de',
      password: 'Test123!'
    });

    // Mock: Fehlerfall
    authService.login.and.returnValue(
      throwError(() => 'Login failed')
    );

    component.onSubmit();
    tick();

    expect(snackBar.open).toHaveBeenCalled();
  }));

  // ---------------------------------------------------------
  // 5. Submit: Ungültiges Formular → AuthService darf NICHT aufgerufen werden
  // ---------------------------------------------------------
  it('should not call authService.login when form is invalid', () => {
    component.ngOnInit();

    component.loginForm.setValue({
      email: '',
      password: ''
    });

    component.onSubmit();

    expect(authService.login).not.toHaveBeenCalled();
  });

});
