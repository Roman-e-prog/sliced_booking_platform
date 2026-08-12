import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FormControl, ValidationErrors } from '@angular/forms';
import { UniqueEmail } from './uniqueEmail';
import { Observable } from 'rxjs';

describe('UniqueEmail Validator', () => {

  let validator: UniqueEmail;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    httpMock = TestBed.inject(HttpTestingController);
    validator = new UniqueEmail(TestBed.inject(HttpTestingController as any));
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should return null when control value is empty', () => {
    const control = new FormControl('');
    const fn = validator.uniqueEmailValidator();

    (fn(control)  as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toBeNull();
    });
  });

  it('should return null when email is not taken', () => {
    const control = new FormControl('roman@test.de');
    const fn = validator.uniqueEmailValidator();

    (fn(control)  as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toBeNull();
    });

    const req = httpMock.expectOne(
      req => req.url === 'http://localhost:8080/api/auth/uniqueEmail'
    );

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('email')).toBe('roman@test.de');

    req.flush(false);
  });

  it('should return { emailTaken: true } when email is taken', () => {
    const control = new FormControl('roman@test.de');
    const fn = validator.uniqueEmailValidator();

    (fn(control)  as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toEqual({ emailTaken: true });
    });

    const req = httpMock.expectOne(
      req => req.url === 'http://localhost:8080/api/auth/uniqueEmail'
    );

    req.flush(true);
  });

  it('should return null when server returns an error', () => {
    const control = new FormControl('roman@test.de');
    const fn = validator.uniqueEmailValidator();

    (fn(control)  as Observable<ValidationErrors | null>).subscribe(result => {
      expect(result).toBeNull();
    });

    const req = httpMock.expectOne(
      req => req.url === 'http://localhost:8080/api/auth/uniqueEmail'
    );

    req.flush('Network error', {
        status: 500,
        statusText: 'Server Error'
        });
  });

});
