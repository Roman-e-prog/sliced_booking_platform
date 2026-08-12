import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BookingOverviewComponent } from './booking-overview.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { PLATFORM_ID } from '@angular/core';
import { getAllBookings, deleteBooking } from '../../../store/actions/booking.actions';
import { Booking } from '../../../store/reducers/booking.reducer';

class SnackBarMock {
  open = jasmine.createSpy('open');
}

describe('BookingOverviewComponent', () => {

  let component: BookingOverviewComponent;
  let fixture: ComponentFixture<BookingOverviewComponent>;
  let store: MockStore;
  let snackBar: SnackBarMock;

  const initialState = {
    booking: {
      data: [
        { id: 1, roomType: 'DELUXE', userType: 'ADULT' }
      ],
      loading: false,
      error: false,
      message: null
    }
  };

  beforeEach(async () => {
    snackBar = new SnackBarMock();

    await TestBed.configureTestingModule({
      imports: [BookingOverviewComponent],
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
    fixture = TestBed.createComponent(BookingOverviewComponent);
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
  // 2. Should dispatch getAllBookings on init
  // ---------------------------------------------------------
  it('should dispatch getAllBookings on init when browser and not loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = false;
    component.ngOnInit();

    expect(dispatchSpy).toHaveBeenCalledWith(getAllBookings());
    expect(component.loaded).toBeTrue();
  });

  // ---------------------------------------------------------
  // 3. Should NOT dispatch getAllBookings when already loaded
  // ---------------------------------------------------------
  it('should not dispatch getAllBookings when loaded is true', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = true;
    component.ngOnInit();

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // 4. Should show snackbar when booking error occurs
  // ---------------------------------------------------------
  it('should show snackbar when booking error occurs', () => {
    store.overrideSelector('selectBookingError' as any, true);
    store.overrideSelector('selectBookingMessage' as any, 'Booking failed');

    fixture.detectChanges();
    component.ngOnInit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Booking failed',
      'Close',
      { duration: 5000 }
    );
  });

  // ---------------------------------------------------------
  // 5. handleUpdate(): should activate edit module and set editData
  // ---------------------------------------------------------
  it('should activate edit module and set editData on handleUpdate', () => {
    const booking: Booking = {
      bookingId: 1,
      roomType: 'ONE_BED',
      userType: 'PRIVATE_GUEST',
      numberOfPersons:2,
      startDate:"01.08.2026",
      endDate:"01.09.2026",
      bookingType:"ONLY_REST"
    };

    component.handleUpdate(booking);

    expect(component.editModule).toBeTrue();
    expect(component.editData).toEqual(booking);
  });

  // ---------------------------------------------------------
  // 6. handleDelete(): should dispatch deleteBooking
  // ---------------------------------------------------------
  it('should dispatch deleteBooking on handleDelete', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.handleDelete(5);

    expect(dispatchSpy).toHaveBeenCalledWith(deleteBooking({ id: 5 }));
  });

});
