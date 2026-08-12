import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { selectPriceData, selectPriceError, selectPriceLoading, selectPriceMessage, selectPricesData } from '../../../store/selectors/price.selector';
import { NavbarComponent } from '../../navbar/navbar.component';
import { createPrice, deletePrice, getAllPrices } from '../../../store/actions/price.actions';
import { Price } from '../../../store/reducers/price.reducer';
import { tap } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PriceEditComponent } from '../../editComponents/price-edit/price-edit.component';

@Component({
  selector: 'app-price-setter',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    FormsModule, 
    PriceEditComponent],
  templateUrl: './price-setter.component.html',
  styleUrl: './price-setter.component.scss'
})
export class PriceSetterComponent {

  constructor(
    private store:Store,
    private snackBar: MatSnackBar,
     @Inject(PLATFORM_ID) private platformId: Object
  ){}
  allPrices$ = this.store.select(selectPricesData);
      isError$ = this.store.select(selectPriceError);
      isLoading$ = this.store.select(selectPriceLoading);
      message$ = this.store.select(selectPriceMessage);
      loaded = false;
    ngOnInit(): void {

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
    if(isPlatformBrowser(this.platformId) && !this.loaded){
    this.store.dispatch(getAllPrices())
    this.loaded = true;
    }
  }
    
    
  priceSetterForm = new FormGroup({
    roomType: new FormControl<string>("",  {nonNullable: true, validators: Validators.required}),
    bookingType: new FormControl<string>("", {nonNullable: true, validators: Validators.required}),
    nettoPrice: new FormControl<number | null>(null, {validators: Validators.required}),
    taxRate: new FormControl<number>(19.00, {validators: Validators.required}),
  })
  
  onSubmit() {
  if (this.priceSetterForm.valid) {
    const raw = this.priceSetterForm.getRawValue();

    if (raw.nettoPrice === null || raw.taxRate === null) {
      return; 
    }

    const data: Price = {
      ...raw,
      nettoPrice: raw.nettoPrice, 
      taxRate: raw.taxRate  
    };

    this.store.dispatch(createPrice({ priceData: data }));
    this.priceSetterForm.reset();
  }
}
editModule = false;
editData: Price | null = null;

handleEdit = (priceData:Price)=>{
  this.editModule = true;
  this.editData = priceData;
};
handleDelete = (id:number)=>{
  this.store.dispatch(deletePrice({id:id}))
}
handleClose = ()=>{
  this.editModule = false;
}

}
