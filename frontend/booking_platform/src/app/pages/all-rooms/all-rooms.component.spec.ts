import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AllRoomsComponent } from './all-rooms.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { getAllRooms } from '../../store/actions/room.actions';
import { PLATFORM_ID } from '@angular/core';
import { of } from 'rxjs';

// SnackBar Mock
class SnackBarMock {
  open = jasmine.createSpy('open');
}

describe('AllRoomsComponent', () => {

  let component: AllRoomsComponent;
  let fixture: ComponentFixture<AllRoomsComponent>;
  let store: MockStore;
  let snackBar: SnackBarMock;

  const initialState = {
    rooms: {
      data: [],
      loading: false,
      error: false,
      message: null
    }
  };

  beforeEach(async () => {
    snackBar = new SnackBarMock();

    await TestBed.configureTestingModule({
      imports: [AllRoomsComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: MatSnackBar, useValue: snackBar },
        { provide: PLATFORM_ID, useValue: 'browser' }
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(AllRoomsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Komponente sollte erzeugt werden
  // ---------------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Sollte getAllRooms dispatchen beim ersten Browser-Load
  // ---------------------------------------------------------
  it('should dispatch getAllRooms on init when browser and not loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = false;
    component.ngOnInit();

    expect(dispatchSpy).toHaveBeenCalledWith(getAllRooms());
    expect(component.loaded).toBeTrue();
  });

  // ---------------------------------------------------------
  // 3. Sollte NICHT dispatchen, wenn loaded = true
  // ---------------------------------------------------------
  it('should not dispatch getAllRooms when already loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = true;
    component.ngOnInit();

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  // ---------------------------------------------------------
  // 4. Sollte Fehler anzeigen, wenn isError$ = true
  // ---------------------------------------------------------
  it('should show snackbar when error occurs', () => {
    // Mock selectors
    store.overrideSelector('selectRoomError' as any, true);
    store.overrideSelector('selectRoomMessage' as any, 'Something went wrong');

    fixture.detectChanges();
    component.ngOnInit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Something went wrong',
      'Close',
      { duration: 5000 }
    );
  });

  // ---------------------------------------------------------
  // 5. Sollte keinen Fehler anzeigen, wenn isError$ = false
  // ---------------------------------------------------------
  it('should not show snackbar when no error', () => {
    store.overrideSelector('selectRoomError' as any, false);
    store.overrideSelector('selectRoomMessage' as any, null);

    fixture.detectChanges();
    component.ngOnInit();

    expect(snackBar.open).not.toHaveBeenCalled();
  });

});
