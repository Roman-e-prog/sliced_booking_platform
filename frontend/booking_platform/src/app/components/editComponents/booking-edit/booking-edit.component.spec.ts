import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookingEditComponent } from './booking-edit.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { updateBooking } from '../../../store/actions/booking.actions';
import { Booking } from '../../../store/reducers/booking.reducer';

class SnackBarMock {
  open = jasmine.createSpy('open');
}

describe('BookingEditComponent', () => {

  let component: BookingEditComponent;
  let fixture: ComponentFixture<BookingEditComponent>;
  let store: MockStore;
  let snackBar: SnackBarMock;

  const mockBooking: Booking = {
    bookingId: 99,
    roomNumber: 10,
    numberOfPersons: 2,
    startDate: '2024-01-01',
    endDate: '2024-01-05',
    bookingType: 'ONLY_REST',
    roomType: 'ONE_BED',
    userType: 'PRIVATE_GUEST'
  };

  beforeEach(async () => {
    snackBar = new SnackBarMock();

    await TestBed.configureTestingModule({
      imports: [BookingEditComponent],
      providers: [
        provideMockStore(),
        { provide: MatSnackBar, useValue: snackBar }
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(BookingEditComponent);
    component = fixture.componentInstance;
    component.editBookingData = mockBooking;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Component creation
  // ---------------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Prefill form when editBookingData is provided
  // ---------------------------------------------------------
  it('should prefill the form with editBookingData', () => {
    component.ngOnInit();

    expect(component.bookingEditForm.get('numberOfPersons')?.value).toBe(2);
    expect(component.bookingEditForm.get('bookingType')?.value).toBe('STANDARD');
    expect(component.bookingEditForm.get('roomType')?.value).toBe('DELUXE');
    expect(component.bookingEditForm.get('userType')?.value).toBe('ADULT');
    expect(component.bookingEditForm.get('startDate')?.value).toBe('2024-01-01');
    expect(component.bookingEditForm.get('endDate')?.value).toBe('2024-01-05');
  });

  // ---------------------------------------------------------
  // 3. Submit → dispatch updateBooking
  // ---------------------------------------------------------
  it('should dispatch updateBooking on submit', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.bookingEditForm.setValue({
      roomNumber: 10,
      numberOfPersons: 2,
      startDate: '2024-01-01',
      endDate: '2024-01-05',
      bookingType: 'ONLY_REST',
      roomType: 'ONE_BED',
      userType: 'PRIVATE_GUEST'
    });

    component.onSubmit();

    expect(dispatchSpy).toHaveBeenCalledWith(
      updateBooking({
        bookingData: {
          roomNumber: 10,
          numberOfPersons: 2,
          startDate: '2024-01-01',
          endDate: '2024-01-05',
          bookingType: 'ONLY_REST',
          roomType: 'ONE_BED',
          userType: 'PRIVATE_GUEST'
        },
        id: 99
      })
    );
  });

  // ---------------------------------------------------------
  // 4. Submit → should emit closeEdit
  // ---------------------------------------------------------
  it('should emit closeEdit after submit', () => {
    spyOn(component.closeEdit, 'emit');

    component.bookingEditForm.setValue({
      roomNumber: 10,
      numberOfPersons: 2,
      startDate: '2024-01-01',
      endDate: '2024-01-05',
      bookingType: 'ONLY_REST',
      roomType: 'ONE_BED',
      userType: 'PRIVATE_GUEST'
    });

    component.onSubmit();

    expect(component.closeEdit.emit).toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // 5. handleClose() → should emit closeEdit
  // ---------------------------------------------------------
  it('should emit closeEdit when handleClose is called', () => {
    spyOn(component.closeEdit, 'emit');

    component.handleClose();

    expect(component.closeEdit.emit).toHaveBeenCalled();
  });

});
