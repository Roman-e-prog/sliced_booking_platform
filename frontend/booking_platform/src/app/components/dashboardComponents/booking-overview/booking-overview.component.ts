import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectBookingsData, selectBookingError, selectBookingLoading, selectBookingMessage } from '../../../store/selectors/booking.selector';
import { tap } from 'rxjs';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { deleteBooking, getAllBookings, updateBooking } from '../../../store/actions/booking.actions';
import { Booking } from '../../../store/reducers/booking.reducer';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BookingEditComponent } from '../../editComponents/booking-edit/booking-edit.component';
@Component({
  selector: 'app-booking-overview',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, BookingEditComponent],
  templateUrl: './booking-overview.component.html',
  styleUrl: './booking-overview.component.scss'
})
export class BookingOverviewComponent {
handleClose() {
throw new Error('Method not implemented.');
}
  constructor(
    private store: Store,
    private snackBar: MatSnackBar,
    @Inject(PLATFORM_ID) private platformId: Object,
    private route: ActivatedRoute,
  ) {}
  allBookings$ = this.store.select(selectBookingsData);
  bookingError$ = this.store.select(selectBookingError);
  bookingLoading$ = this.store.select(selectBookingLoading);
  bookingMessage$ = this.store.select(selectBookingMessage);
  loaded = false;
      ngOnInit(): void {
     
      this.bookingError$.pipe(
        tap(isError => {
          if (isError) {
            this.bookingMessage$.subscribe(errorMessage => {
              this.snackBar.open(errorMessage || 'An error occurred', 'Close', {
                duration: 5000
              });
            });
          }
        })
      ).subscribe();
      if(isPlatformBrowser(this.platformId) && !this.loaded){
      this.store.dispatch(getAllBookings())
      this.loaded = true;
      }
    }
    editModule = false;
    editData: Booking | null = null;
    handleUpdate = (data:Booking)=>{
      this.editModule = true;
      this.editData = data;
    }
    handleDelete = (id:number)=>{
      this.store.dispatch(deleteBooking({id:id}))
    }
}
