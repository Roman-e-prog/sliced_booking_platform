import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PriceEditComponent } from './price-edit.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { updatePrice } from '../../../store/actions/price.actions';
import { Price } from '../../../store/reducers/price.reducer';

describe('PriceEditComponent', () => {

  let component: PriceEditComponent;
  let fixture: ComponentFixture<PriceEditComponent>;
  let store: MockStore;

  const mockPrice: Price = {
    priceId: 55,
    roomType: 'ONE_BED',
    bookingType: 'ONLY_REST',
    nettoPrice: 200,
    taxRate: 19
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PriceEditComponent],
      providers: [
        provideMockStore()
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(PriceEditComponent);
    component = fixture.componentInstance;
    component.editPriceData = mockPrice;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Component creation
  // ---------------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Prefill form when editPriceData is provided
  // ---------------------------------------------------------
  it('should prefill the form with editPriceData', () => {
    component.ngOnInit();

    expect(component.priceEditForm.get('roomType')?.value).toBe('ONE_BED');
    expect(component.priceEditForm.get('bookingType')?.value).toBe('ONLY_REST');
    expect(component.priceEditForm.get('nettoPrice')?.value).toBe(200);
    expect(component.priceEditForm.get('taxRate')?.value).toBe(19);
  });

  // ---------------------------------------------------------
  // 3. Submit → dispatch updatePrice
  // ---------------------------------------------------------
  it('should dispatch updatePrice on submit', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.priceEditForm.setValue({
      roomType: 'DELUXE',
      bookingType: 'STANDARD',
      nettoPrice: 200,
      taxRate: 19
    });

    component.onSubmit();

    expect(dispatchSpy).toHaveBeenCalledWith(
      updatePrice({
        priceData: {
          roomType: 'ONE_BED',
          bookingType: 'ONLY_REST',
          nettoPrice: 200,
          taxRate: 19
        },
        id: 55
      })
    );
  });

  // ---------------------------------------------------------
  // 4. Submit → should reset form
  // ---------------------------------------------------------
  it('should reset form after submit', () => {
    component.priceEditForm.setValue({
      roomType: 'ONE_BED',
      bookingType: 'ONLY_REST',
      nettoPrice: 200,
      taxRate: 19
    });

    component.onSubmit();

    expect(component.priceEditForm.value).toEqual({
      roomType: "",
      bookingType: "",
      nettoPrice: null,
      taxRate: null
    });
  });

  // ---------------------------------------------------------
  // 5. Submit → should emit closeEdit
  // ---------------------------------------------------------
  it('should emit closeEdit after submit', () => {
    spyOn(component.closeEdit, 'emit');

    component.priceEditForm.setValue({
      roomType: 'ONE_BED',
      bookingType: 'ONLY_REST',
      nettoPrice: 200,
      taxRate: 19
    });

    component.onSubmit();

    expect(component.closeEdit.emit).toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // 6. handleClose() → should emit closeEdit
  // ---------------------------------------------------------
  it('should emit closeEdit when handleClose is called', () => {
    spyOn(component.closeEdit, 'emit');

    component.handleClose();

    expect(component.closeEdit.emit).toHaveBeenCalled();
  });

});
