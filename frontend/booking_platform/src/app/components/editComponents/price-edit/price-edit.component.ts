import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Price } from '../../../store/reducers/price.reducer';
import { Store } from '@ngrx/store';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { updatePrice } from '../../../store/actions/price.actions';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-price-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './price-edit.component.html',
  styleUrl: './price-edit.component.scss'
})
export class PriceEditComponent implements OnInit {
  constructor(
    private store: Store
  ){}
  
       @Input() editPriceData : Price | null = null;
       @Output() closeEdit = new EventEmitter();
        handleClose = ()=>{
      this.closeEdit.emit()
    }
    ngOnInit():void{
      if(this.editPriceData){
        this.priceEditForm.get('roomType')?.setValue(this.editPriceData.roomType);
        this.priceEditForm.get('bookingType')?.setValue(this.editPriceData.bookingType);
        this.priceEditForm.get('nettoPrice')?.setValue(this.editPriceData.nettoPrice);
        this.priceEditForm.get('taxRate')?.setValue(this.editPriceData.taxRate);
      }

    }
      priceEditForm = new FormGroup({
        roomType: new FormControl<string>("",  {nonNullable: true, validators: Validators.required}),
        bookingType: new FormControl<string>("", {nonNullable: true, validators: Validators.required}),
        nettoPrice: new FormControl<number | null>(null, {validators: Validators.required}),
        taxRate: new FormControl<number>(19.00, {validators: Validators.required}),
      })
      onSubmit() {
        if (this.priceEditForm.valid) {
          const raw = this.priceEditForm.getRawValue();
      
          if (raw.nettoPrice === null || raw.taxRate === null) {
            return; 
          }
      
          const data: Price = {
            ...raw,
            nettoPrice: raw.nettoPrice, 
            taxRate: raw.taxRate  
          };
      
          this.store.dispatch(updatePrice({ priceData: data, id:this.editPriceData!.priceId! }));
          this.priceEditForm.reset();
          this.closeEdit.emit();
        }
      }
}
