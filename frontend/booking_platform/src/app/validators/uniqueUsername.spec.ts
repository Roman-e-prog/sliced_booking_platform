import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FormControl, ValidationErrors } from '@angular/forms';
import { UniqueUsername } from './uniqueUsername';
import { Observable } from 'rxjs';

describe('UniqueUsername Validator', () => {

  let validator: UniqueUsername;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    httpMock = TestBed.inject(HttpTestingController);
    validator = new UniqueUsername(TestBed.inject(HttpTestingController as any));
  });

  afterEach(() => {
    httpMock.verify(); // stellt sicher, dass keine offenen Requests bleiben
  });

  // ---------------------------------------------------------
  // 1. Wenn kein Wert eingegeben wurde → Validator gibt null zurück
  // ---------------------------------------------------------
  it('should return null when control value is empty', () => {
    const control = new FormControl('');
    const fn = validator.uniqueUsernameValidator();

    (fn(control) as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toBeNull();
    });
  });

  // ---------------------------------------------------------
  // 2. Wenn Username frei ist → Validator gibt null zurück
  // ---------------------------------------------------------
  it('should return null when username is not taken', () => {
    const control = new FormControl('roman123');
    const fn = validator.uniqueUsernameValidator();

    (fn(control) as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toBeNull();
    });

    const req = httpMock.expectOne(
      req => req.url === 'http://localhost:8080/api/auth/uniqueUsername'
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('username')).toBe('roman123');

    req.flush(false); // false = Username ist frei
  });

  // ---------------------------------------------------------
  // 3. Wenn Username belegt ist → Validator gibt Fehler zurück
  // ---------------------------------------------------------
  it('should return { usernameTaken: true } when username is taken', () => {
    const control = new FormControl('roman123');
    const fn = validator.uniqueUsernameValidator();

    (fn(control) as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toEqual({ usernameTaken: true });
    });

    const req = httpMock.expectOne(
      req => req.url === 'http://localhost:8080/api/auth/uniqueUsername'
    );

    req.flush(true); // true = Username ist belegt
  });

  // ---------------------------------------------------------
  // 4. Fehlerfall → Validator soll null zurückgeben
  // ---------------------------------------------------------
  it('should return null when server returns an error', () => {
    const control = new FormControl('roman123');
    const fn = validator.uniqueUsernameValidator();

    (fn(control) as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toBeNull(); // Fehler → wir behandeln wie "frei"
    });

    const req = httpMock.expectOne(
      req => req.url === 'http://localhost:8080/api/auth/uniqueUsername'
    );

    req.flush('Network error', {
        status: 500,
        statusText: 'Server Error'
        });
  });

});
