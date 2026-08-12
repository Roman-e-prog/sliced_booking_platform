import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RoomCreatorComponent } from './room-creator.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PLATFORM_ID } from '@angular/core';
import { getAllRooms, createRoom, deleteRoom } from '../../../store/actions/room.actions';
import { Room } from '../../../store/reducers/room.reducer';

class SnackBarMock {
  open = jasmine.createSpy('open');
}

describe('RoomCreatorComponent', () => {

  let component: RoomCreatorComponent;
  let fixture: ComponentFixture<RoomCreatorComponent>;
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
      imports: [RoomCreatorComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: MatSnackBar, useValue: snackBar },
        { provide: PLATFORM_ID, useValue: 'browser' }
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(RoomCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch getAllRooms on init when browser and not loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = false;
    component.ngOnInit();

    expect(dispatchSpy).toHaveBeenCalledWith(getAllRooms());
    expect(component.loaded).toBeTrue();
  });

  it('should not dispatch getAllRooms when already loaded', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.loaded = true;
    component.ngOnInit();

    expect(dispatchSpy).not.toHaveBeenCalled();
  });

  it('should show snackbar when room error occurs', () => {
    store.overrideSelector('selectRoomError' as any, true);
    store.overrideSelector('selectRoomMessage' as any, 'Room error');

    fixture.detectChanges();
    component.ngOnInit();

    expect(snackBar.open).toHaveBeenCalledWith(
      'Room error',
      'Close',
      { duration: 5000 }
    );
  });

  it('should dispatch createRoom with FormData when form is valid', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    const file = new File(['content'], 'test.jpg', { type: 'image/jpeg' });

    component.roomForm.setValue({
      room_type: 'ONE_BED',
      description: 'Nice room',
      price_per_night: '120',
      images: [file],
      alts: ['Alt text'],
      roomNumber: 10
    });

    component.onSubmit();

    expect(dispatchSpy).toHaveBeenCalled();
    const action = dispatchSpy.calls.mostRecent().args[0] as any;
    expect(action.type).toBe(createRoom.type);
    expect(action.roomData instanceof FormData).toBeTrue();
  });

  it('should toggle existingModule', () => {
    expect(component.existingModule).toBeFalse();

    component.toggleExistingModule();
    expect(component.existingModule).toBeTrue();

    component.toggleExistingModule();
    expect(component.existingModule).toBeFalse();
  });

  it('should activate editModule and set editData on handleEdit', () => {
    const room: Room = {
      id: 1,
      roomType: 'ONE_BED',
      description: 'Nice',
      pricePerNight: 120,
      roomNumber: 10,
      images: [],
      alts: []
    } as any;

    component.handleEdit(room);

    expect(component.editModule).toBeTrue();
    expect(component.editData).toEqual(room);
  });

  it('should dispatch deleteRoom on handleDelete', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.handleDelete(5);

    expect(dispatchSpy).toHaveBeenCalledWith(deleteRoom({ id: 5 }));
  });

  it('should disable editModule on handleClose', () => {
    component.editModule = true;

    component.handleClose();

    expect(component.editModule).toBeFalse();
  });

});
