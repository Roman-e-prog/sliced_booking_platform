import { Component, Inject, OnDestroy, OnInit, PLATFORM_ID } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { getRoom } from '../../store/actions/room.actions';
import { Store } from '@ngrx/store';
import { selectRoomData, selectRoomError, selectRoomLoading, selectRoomMessage } from '../../store/selectors/room.selector';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { map, tap, withLatestFrom } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { createBooking } from '../../store/actions/booking.actions';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { selectBookingError, selectBookingSuccess } from '../../store/selectors/booking.selector';
import { MobileNavbarComponent } from '../../components/mobile-navbar/mobile-navbar.component';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, NavbarComponent, MobileNavbarComponent],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.scss'
})
export class BookingComponent implements OnInit{
  
room: any;
   constructor(private authService:AuthService,
    private store: Store,
    private snackBar: MatSnackBar,
    @Inject(PLATFORM_ID) private platformId: Object,
    private route: ActivatedRoute,
   ){}
   user = this.authService.getUser()
   id = this.route.snapshot.paramMap.get('id')

      singleRoom$ = this.store.select(selectRoomData);
     isError$ = this.store.select(selectRoomError);
     isLoading$ = this.store.select(selectRoomLoading);
     message$ = this.store.select(selectRoomMessage);
     bookingSuccess$ = this.store.select(selectBookingSuccess);
     bookingError$ = this.store.select(selectBookingError);

    
     loaded = false;
     //vorfommatieren sonst undefined
      formattedRoomType$ = this.singleRoom$.pipe(
  map(room => room?.roomType?.split('_').join(' '))
);

  ngOnInit(): void {
    this.singleRoom$.subscribe(room => {
      this.room = room;
      console.log('Fetched room:', room);
    });
    this.isError$.pipe(
             tap(isError => {
               if (isError) {
                 this.message$.subscribe(errorMessage => {
                   this.snackBar.open(errorMessage || 'An error occurred', 'Close', {
                     duration: 5000
                   });
                 });
               }
             })
           ).subscribe();
           if(isPlatformBrowser(this.platformId) && !this.loaded && this.id !== null){
             this.loaded = true;
             this.store.dispatch(getRoom({id: parseInt(this.id)}))
           }
    
  }
  
      bookingForm = new FormGroup({
  numberOfPersons: new FormControl<number>(0, { nonNullable: true, validators: Validators.required }),
  startDate: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
  endDate: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
  bookingType: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
  roomType: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
  userType: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
});

    

    onSubmit(){
      if(this.bookingForm.valid){
        const data = {
          numberOfPersons: this.bookingForm.get('numberOfPersons')!.value,
          startDate: this.bookingForm.get('startDate')!.value,
          endDate: this.bookingForm.get('endDate')!.value,
          bookingType: this.bookingForm.get('bookingType')!.value,
          roomType: this.bookingForm.get('roomType')!.value,
          userType: this.bookingForm.get('userType')!.value
        };
       
          this.store.dispatch(createBooking({bookingData:data}))
          this.bookingSuccess$.subscribe(success => {
          if (success) {
            this.snackBar.open('Booking created successfully!', 'Close', {
              duration: 5000
            });
            this.bookingForm.reset();
          }
        });

        this.bookingError$.pipe(withLatestFrom(this.message$), tap(([error, errorMessage]) => {
          if (error) {
            console.error('Booking failed:', error);
            this.snackBar.open(errorMessage || 'An error occurred', 'Close', {
              duration: 5000
            });
          }
        })).subscribe();
      }
    }
  }
