import { TestBed } from '@angular/core/testing';
import { RegisterService } from './register.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('RegisterService', () => {

  let service: RegisterService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [RegisterService]
    });

    service = TestBed.inject(RegisterService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // ---------------------------------------------------------
  // 1. Service sollte erzeugt werden
  // ---------------------------------------------------------
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. registerUser sollte POST an /register senden
  // ---------------------------------------------------------
  it('should send POST request with correct payload', () => {
    const mockPayload = {
      prename: 'Roman',
      lastname: 'Test',
      username: 'roman123',
      email: 'roman@test.de',
      street: 'Teststr.',
      houseNumber: '12',
      postalCode: 59846,
      town: 'Sundern',
      country: 'Germany',
      birthDate: new Date('1990-01-01'),
      password: 'Test123!'
    };

    service.registerUser(
      mockPayload.prename,
      mockPayload.lastname,
      mockPayload.username,
      mockPayload.email,
      mockPayload.street,
      mockPayload.houseNumber,
      mockPayload.postalCode,
      mockPayload.town,
      mockPayload.country,
      mockPayload.birthDate,
      mockPayload.password
    ).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/register');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockPayload);

    req.flush({ success: true });
  });

});
