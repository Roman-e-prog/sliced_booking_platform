import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Price } from '../../../store/reducers/price.reducer';
import { Booking } from '../../../store/reducers/booking.reducer';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { withLatestFrom, tap } from 'rxjs';
import { createBooking, updateBooking } from '../../../store/actions/booking.actions';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-booking-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './booking-edit.component.html',
  styleUrl: './booking-edit.component.scss'
})
export class BookingEditComponent {

    constructor(
      private store:Store,
      private snackBar:MatSnackBar,
    ){}
     @Input() editBookingData : Booking | null = null;
         @Output() closeEdit = new EventEmitter();
          handleClose = ()=>{
        this.closeEdit.emit()
      }
         ngOnInit():void{
      if(this.editBookingData){
        this.bookingEditForm.get('numberOfPersons')?.setValue(this.editBookingData.numberOfPersons);
        this.bookingEditForm.get('bookingType')?.setValue(this.editBookingData.bookingType);
        this.bookingEditForm.get('roomType')?.setValue(this.editBookingData.roomType);
        this.bookingEditForm.get('userType')?.setValue(this.editBookingData.userType);
        this.bookingEditForm.patchValue({
          startDate:this.editBookingData.startDate,
          endDate: this.editBookingData.endDate,
        })
      }

    }
         bookingEditForm = new FormGroup({
          roomNumber:new FormControl<number>(0, { nonNullable: true, validators: Validators.required }),
        numberOfPersons: new FormControl<number>(0, { nonNullable: true, validators: Validators.required }),
        startDate: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
        endDate: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
        bookingType: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
        roomType: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
        userType: new FormControl<string>("", { nonNullable: true, validators: Validators.required }),
      });
      //prefill
      
          
      
          onSubmit(){
            if(this.bookingEditForm.valid){
              const data = {
                roomNumber: this.bookingEditForm.get('roomNumber')?.value,
                numberOfPersons: this.bookingEditForm.get('numberOfPersons')!.value,
                startDate: this.bookingEditForm.get('startDate')!.value,
                endDate: this.bookingEditForm.get('endDate')!.value,
                bookingType: this.bookingEditForm.get('bookingType')!.value,
                roomType: this.bookingEditForm.get('roomType')!.value,
                userType: this.bookingEditForm.get('userType')!.value
              };
             
                this.store.dispatch(updateBooking({bookingData:data, id:this.editBookingData!.bookingId!}))
                this.closeEdit.emit()

      
            }
          }

}
