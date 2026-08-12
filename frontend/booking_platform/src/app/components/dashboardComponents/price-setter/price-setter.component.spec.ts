import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PriceSetterComponent } from './price-setter.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PLATFORM_ID } from '@angular/core';
import { getAllPrices, createPrice, deletePrice } from '../../../store/actions/price.actions';
import { Price } from '../../../store/reducers/price.reducer';

class SnackBarMock {
  open = jasmine.createSpy('open');
}

describe('PriceSetterComponent', () => {

  let component: PriceSetterComponent;
  let fixture: ComponentFixture<PriceSetterComponent>;
  let store: MockStore;
  let snackBar: SnackBarMock;

  const initialState = {
    price: {
      data: [
        { id: 1, roomType: 'DELUXE', bookingType: 'STANDARD', nettoPrice: 100, taxRate: 19 }
      ],
      loading: false,
      error: false,
      message: null
    }
  };

  beforeEach(async () => {
    snackBar = new SnackBarMock();

    await TestBed.configureTestingModule({
      imports: [PriceSetterComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: MatSnackBar, useValue: snackBar },
        { provide: PLATFORM_ID, useValue: 'browser' }
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(PriceSetterComponent);
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
  // 2. Should dispatch getAllPrices on init
  // ---------------------------------------------------------
  it('should dispatch getAllPrices on init when browser and not loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = false;
    component.ngOnInit();

    expect(dispatchSpy).toHaveBeenCalledWith(getAllPrices());
    expect(component.loaded).toBeTrue();
  });

  // ---------------------------------------------------------
  // 3. Should NOT dispatch getAllPrices when already loaded
  // ---------------------------------------------------------
  it('should not dispatch getAllPrices when loaded is true', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = true;
    component.ngOnInit();

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // 4. Should show snackbar when price error occurs
  // ---------------------------------------------------------
  it('should show snackbar when price error occurs', () => {
    store.overrideSelector('selectPriceError' as any, true);
    store.overrideSelector('selectPriceMessage' as any, 'Price error');

    fixture.detectChanges();
    component.ngOnInit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Price error',
      'Close',
      { duration: 5000 }
    );
  });

  // ---------------------------------------------------------
  // 5. Should dispatch createPrice on submit
  // ---------------------------------------------------------
  it('should dispatch createPrice when form is valid', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.priceSetterForm.setValue({
      roomType: 'ONE_BED',
      bookingType: 'ONLY_REST',
      nettoPrice: 200,
      taxRate: 19
    });

    component.onSubmit();

    expect(dispatchSpy).toHaveBeenCalledWith(
      createPrice({
        priceData: {
          roomType: 'ONE_BED',
          bookingType: 'ONLY_REST',
          nettoPrice: 200,
          taxRate: 19
        }
      })
    );
  });

  // ---------------------------------------------------------
  // 6. Should reset form after submit
  // ---------------------------------------------------------
  it('should reset form after submit', () => {
    component.priceSetterForm.setValue({
      roomType: 'ONE_BED',
      bookingType: 'ONLY_REST',
      nettoPrice: 200,
      taxRate: 19
    });

    component.onSubmit();

    expect(component.priceSetterForm.value).toEqual({
      roomType: "",
      bookingType: "",
      nettoPrice: null,
      taxRate: null
    });
  });

  // ---------------------------------------------------------
  // 7. handleEdit(): should activate edit module and set editData
  // ---------------------------------------------------------
  it('should activate edit module and set editData on handleEdit', () => {
    const price: Price = {
      priceId: 1,
      roomType: 'ONE_BED',
      bookingType: 'ONLY_REST',
      nettoPrice: 200,
      taxRate: 19
    };

    component.handleEdit(price);

    expect(component.editModule).toBeTrue();
    expect(component.editData).toEqual(price);
  });

  // ---------------------------------------------------------
  // 8. handleDelete(): should dispatch deletePrice
  // ---------------------------------------------------------
  it('should dispatch deletePrice on handleDelete', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.handleDelete(5);

    expect(dispatchSpy).toHaveBeenCalledWith(deletePrice({ id: 5 }));
  });

  // ---------------------------------------------------------
  // 9. handleClose(): should disable edit module
  // ---------------------------------------------------------
  it('should disable edit module on handleClose', () => {
    component.editModule = true;

    component.handleClose();

    expect(component.editModule).toBeFalse();
  });

});
