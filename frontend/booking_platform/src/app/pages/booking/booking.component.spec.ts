import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookingComponent } from './booking.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { getRoom } from '../../store/actions/room.actions';
import { createBooking } from '../../store/actions/booking.actions';

class SnackBarMock {
  open = jasmine.createSpy('open');
}

describe('BookingComponent', () => {

  let component: BookingComponent;
  let fixture: ComponentFixture<BookingComponent>;
  let store: MockStore;
  let snackBar: SnackBarMock;

  const initialState = {
    rooms: {
      data: { id: 1, roomType: 'DELUXE_ROOM' },
      loading: false,
      error: false,
      message: null
    },
    booking: {
      success: false,
      error: false,
      message: null
    }
  };

  beforeEach(async () => {
    snackBar = new SnackBarMock();

    await TestBed.configureTestingModule({
      imports: [BookingComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: MatSnackBar, useValue: snackBar },
        { provide: PLATFORM_ID, useValue: 'browser' },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => '1'
              }
            }
          }
        }
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(BookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Component creation
  // ---------------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Should dispatch getRoom(id) on init
  // ---------------------------------------------------------
  it('should dispatch getRoom on init when browser and not loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = false;
    component.ngOnInit();

    expect(dispatchSpy).toHaveBeenCalledWith(getRoom({ id: 1 }));
    expect(component.loaded).toBeTrue();
  });

  // ---------------------------------------------------------
  // 3. Should NOT dispatch getRoom when already loaded
  // ---------------------------------------------------------
  it('should not dispatch getRoom when loaded is true', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = true;
    component.ngOnInit();

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // 4. Should show snackbar on room error
  // ---------------------------------------------------------
  it('should show snackbar when room error occurs', () => {
    store.overrideSelector('selectRoomError' as any, true);
    store.overrideSelector('selectRoomMessage' as any, 'Room not found');

    fixture.detectChanges();
    component.ngOnInit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Room not found',
      'Close',
      { duration: 5000 }
    );
  });

  // ---------------------------------------------------------
  // 5. Should dispatch createBooking on submit
  // ---------------------------------------------------------
  it('should dispatch createBooking when form is valid', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.bookingForm.setValue({
      numberOfPersons: 2,
      startDate: '2024-01-01',
      endDate: '2024-01-05',
      bookingType: 'STANDARD',
      roomType: 'DELUXE',
      userType: 'ADULT'
    });

    component.onSubmit();

    expect(dispatchSpy).toHaveBeenCalledWith(
      createBooking({
        bookingData: {
          numberOfPersons: 2,
          startDate: '2024-01-01',
          endDate: '2024-01-05',
          bookingType: 'STANDARD',
          roomType: 'DELUXE',
          userType: 'ADULT'
        }
      })
    );
  });

  // ---------------------------------------------------------
  // 6. Should show success snackbar and reset form
  // ---------------------------------------------------------
  it('should show success snackbar and reset form on booking success', () => {
    store.overrideSelector('selectBookingSuccess' as any, true);

    fixture.detectChanges();
    component.onSubmit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Booking created successfully!',
      'Close',
      { duration: 5000 }
    );

    expect(component.bookingForm.value).toEqual({
      numberOfPersons: 0,
      startDate: "",
      endDate: "",
      bookingType: "",
      roomType: "",
      userType: ""
    });
  });

  // ---------------------------------------------------------
  // 7. Should show error snackbar on booking error
  // ---------------------------------------------------------
  it('should show error snackbar on booking error', () => {
    store.overrideSelector('selectBookingError' as any, true);
    store.overrideSelector('selectRoomMessage' as any, 'Booking failed');

    fixture.detectChanges();
    component.onSubmit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Booking failed',
      'Close',
      { duration: 5000 }
    );
  });

});
