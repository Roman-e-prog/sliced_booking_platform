import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RoomEditComponent } from './room-edit.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { updateRoom } from '../../../store/actions/room.actions';
import { Room, Image } from '../../../store/reducers/room.reducer';

describe('RoomEditComponent', () => {

  let component: RoomEditComponent;
  let fixture: ComponentFixture<RoomEditComponent>;
  let store: MockStore;

  const mockRoom: Room = {
    roomId: 10,
    roomType: 'DELUXE',
    description: 'Nice room',
    pricePerNight: 120,
    roomNumber: 5,
    isAvailable: true,
    images: [
      {
        imageId: 1,
        roomId: 10,
        alt: 'Alt 1',
        title: 'Title 1',
        path:"/path/to/image1.jpg"
      },
      {
        imageId: 2,
        roomId: 10,
        alt: 'Alt 2',
        title: 'Title 2',
        path:"/path/to/image2.jpg"
      }
    ],
    alts: []
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomEditComponent],
      providers: [
        provideMockStore()
      ]
    }).compileComponents();

    store = TestBed.inject(MockStore);
    fixture = TestBed.createComponent(RoomEditComponent);
    component = fixture.componentInstance;
    component.editRoomData = mockRoom;
    fixture.detectChanges();
  });

  // ---------------------------------------------------------
  // 1. Component creation
  // ---------------------------------------------------------
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ---------------------------------------------------------
  // 2. Form initialization
  // ---------------------------------------------------------
  it('should initialize form and images FormArray', () => {
    component.ngOnInit();

    expect(component.roomEditForm).toBeTruthy();
    expect(component.images).toBeTruthy();
    expect(component.images.length).toBe(2); // two existing images
  });

  // ---------------------------------------------------------
  // 3. Prefill existing room data
  // ---------------------------------------------------------
  it('should prefill room data into form', () => {
    component.ngOnInit();

    expect(component.roomEditForm.get('room_type')?.value).toBe('DELUXE');
    expect(component.roomEditForm.get('description')?.value).toBe('Nice room');
    expect(component.roomEditForm.get('price_per_night')?.value).toBe(120);
    expect(component.roomEditForm.get('roomNumber')?.value).toBe(5);
  });

  // ---------------------------------------------------------
  // 4. Prefill existing images
  // ---------------------------------------------------------
  it('should prefill existing images into FormArray', () => {
    component.ngOnInit();

    const img0 = component.images.at(0).value;
    expect(img0.imageId).toBe(1);
    expect(img0.roomId).toBe(10);
    expect(img0.alt).toBe('Alt 1');
    expect(img0.title).toBe('Title 1');
    expect(img0.isNew).toBeFalse();

    const img1 = component.images.at(1).value;
    expect(img1.imageId).toBe(2);
    expect(img1.isNew).toBeFalse();
  });

  // ---------------------------------------------------------
  // 5. addImage()
  // ---------------------------------------------------------
  it('should add a new image group', () => {
    component.ngOnInit();

    component.addImage();

    expect(component.images.length).toBe(3);
    expect(component.images.at(2).value.isNew).toBeTrue();
  });

  // ---------------------------------------------------------
  // 6. removeImage()
  // ---------------------------------------------------------
  it('should remove an image group', () => {
    component.ngOnInit();

    component.removeImage(0);

    expect(component.images.length).toBe(1);
    expect(component.images.at(0).value.imageId).toBe(2);
  });

  // ---------------------------------------------------------
  // 7. handleFile()
  // ---------------------------------------------------------
  it('should patch file into image group', () => {
    component.ngOnInit();

    const file = new File(['content'], 'test.jpg', { type: 'image/jpeg' });
    const event = {
      target: {
        files: [file]
      }
    } as unknown as Event;

    component.handleFile(0, event);

    expect(component.images.at(0).get('file')?.value).toBe(file);
  });

  // ---------------------------------------------------------
  // 8. onSubmit() → dispatch updateRoom
  // ---------------------------------------------------------
  it('should dispatch updateRoom with FormData on submit', () => {
    const dispatchSpy = spyOn(store, 'dispatch');

    component.ngOnInit();

    // Make form valid
    component.roomEditForm.patchValue({
      room_type: 'ONE_BED',
      description: 'Updated',
      price_per_night: 150,
      roomNumber: 7
    });

    // Add a new image with file (required)
    const file = new File(['content'], 'new.jpg', { type: 'image/jpeg' });
    component.addImage();
    component.images.at(2).patchValue({ file });

    component.onSubmit();

    expect(dispatchSpy).toHaveBeenCalled();

    const action = dispatchSpy.calls.mostRecent().args[0] as any;

    expect(action.type).toBe(updateRoom.type);
    expect(action.id).toBe(10);
    expect(action.roomData instanceof FormData).toBeTrue();
  });

  // ---------------------------------------------------------
  // 9. onSubmit() → should emit closeEdit
  // ---------------------------------------------------------
  it('should emit closeEdit after submit', () => {
    spyOn(component.closeEdit, 'emit');

    component.ngOnInit();

    component.roomEditForm.patchValue({
      room_type: 'ONE_BED',
      description: 'Updated',
      price_per_night: 150,
      roomNumber: 7
    });

    const file = new File(['content'], 'new.jpg');
    component.addImage();
    component.images.at(2).patchValue({ file });

    component.onSubmit();

    expect(component.closeEdit.emit).toHaveBeenCalled();
  });

});
