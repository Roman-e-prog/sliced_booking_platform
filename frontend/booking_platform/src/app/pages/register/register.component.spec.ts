import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RegisterService } from '../../services/register.service';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';

// Dummy mocks für Router und SnackBar
class RouterMock {
  navigate = jasmine.createSpy('navigate');
}

class SnackBarMock {
  open = jasmine.createSpy('open');
}

// Mock für AuthService
class AuthServiceMock {
  // Standard: kein User eingeloggt
  getUser = jasmine.createSpy('getUser').and.returnValue(null);
}

class RegisterServiceMock {
  registerUser = jasmine.createSpy('registerUser');
}

describe('RegisterComponent', () => {

  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let router: RouterMock;
  let snackBar: SnackBarMock;
  let registerService: RegisterServiceMock;
  let authService: AuthServiceMock;

  beforeEach(async () => {
    router = new RouterMock();
    snackBar = new SnackBarMock();
    registerService = new RegisterServiceMock();
    authService = new AuthServiceMock();

    await TestBed.configureTestingModule({
      imports: [
        RegisterComponent,
        ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: Router, useValue: router },
        { provide: MatSnackBar, useValue: snackBar },
        { provide: RegisterService, useValue: registerService },
        { provide: AuthService, useValue: authService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Komponente sollte korrekt erzeugt werden
  // ---------------------------------------------------------
  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Wenn ein User eingeloggt ist → redirect auf "/"
  // ---------------------------------------------------------
  it('should redirect to home if user is logged in', () => {
    authService.getUser.and.returnValue({ username: 'roman' });

    component.ngOnInit();

    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  // ---------------------------------------------------------
  // 3. Form sollte korrekt initialisiert werden
  // ---------------------------------------------------------
  it('should initialize the form with all controls', () => {
    component.ngOnInit();

    const controls = component.registerForm.controls;

    expect(controls['prename']).toBeTruthy();
    expect(controls['lastname']).toBeTruthy();
    expect(controls['username']).toBeTruthy();
    expect(controls['email']).toBeTruthy();
    expect(controls['street']).toBeTruthy();
    expect(controls['houseNumber']).toBeTruthy();
    expect(controls['postalCode']).toBeTruthy();
    expect(controls['town']).toBeTruthy();
    expect(controls['country']).toBeTruthy();
    expect(controls['birthDate']).toBeTruthy();
    expect(controls['password']).toBeTruthy();
    expect(controls['passwordConfirm']).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 4. Passwort-Match Validator testen
  // ---------------------------------------------------------
  it('should mark form invalid if passwords do not match', () => {
    component.ngOnInit();

    component.registerForm.controls['password'].setValue('Test123!');
    component.registerForm.controls['passwordConfirm'].setValue('Different123!');

    expect(component.registerForm.errors).toEqual({ passwordsMismatch: true });
  });

  it('should accept matching passwords', () => {
    component.ngOnInit();

    component.registerForm.controls['password'].setValue('Test123!');
    component.registerForm.controls['passwordConfirm'].setValue('Test123!');

    expect(component.registerForm.errors).toBeNull();
  });

  // ---------------------------------------------------------
  // 5. Submit: Erfolgreiche Registrierung
  // ---------------------------------------------------------
  it('should call registerService and navigate to login on success', fakeAsync(() => {
    component.ngOnInit();

    // Form gültig machen
    component.registerForm.setValue({
      prename: 'Roman',
      lastname: 'Test',
      username: 'roman123',
      email: 'roman@test.de',
      street: 'Teststraße',
      houseNumber: '12',
      postalCode: '59821',
      town: 'Meschede',
      country: 'Deutschland',
      birthDate: new Date(),
      password: 'Test123!',
      passwordConfirm: 'Test123!'
    });

    // Mock: erfolgreicher Response
    registerService.registerUser.and.returnValue(of({}));

    component.onSubmit();
    tick();

    expect(registerService.registerUser).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  }));

  // ---------------------------------------------------------
  // 6. Submit: Fehlerfall → SnackBar anzeigen
  // ---------------------------------------------------------
  it('should show snackbar on error', fakeAsync(() => {
    component.ngOnInit();

    component.registerForm.setValue({
      prename: 'Roman',
      lastname: 'Test',
      username: 'roman123',
      email: 'roman@test.de',
      street: 'Teststraße',
      houseNumber: '12',
      postalCode: '59821',
      town: 'Meschede',
      country: 'Deutschland',
      birthDate: new Date(),
      password: 'Test123!',
      passwordConfirm: 'Test123!'
    });

    registerService.registerUser.and.returnValue(
      throwError(() => 'Fehler beim Registrieren')
    );

    component.onSubmit();
    tick();

    expect(snackBar.open).toHaveBeenCalled();
  }));

});
