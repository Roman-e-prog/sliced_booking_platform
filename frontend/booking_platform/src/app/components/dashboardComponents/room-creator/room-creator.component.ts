import { ChangeDetectionStrategy, Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { selectRoomsData, selectRoomError, selectRoomLoading, selectRoomMessage } from '../../../store/selectors/room.selector';
import { createRoom, deleteRoom, getAllRooms } from '../../../store/actions/room.actions';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { Room } from '../../../store/reducers/room.reducer';
import { RoomEditComponent } from '../../editComponents/room-edit/room-edit.component';
import { QuillModule } from 'ngx-quill';
import { NavbarComponent } from '../../navbar/navbar.component';
import { tap } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-room-creator',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    FormsModule, 
    RoomEditComponent, 
    QuillModule,
  ],
  templateUrl: './room-creator.component.html',
  styleUrl: './room-creator.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RoomCreatorComponent {
  constructor(
    private store: Store,
    private snackBar: MatSnackBar,
     @Inject(PLATFORM_ID) private platformId: Object
  ){}
    allRooms$ = this.store.select(selectRoomsData);
    isError$ = this.store.select(selectRoomError);
    isLoading$ = this.store.select(selectRoomLoading);
    message$ = this.store.select(selectRoomMessage);
    //htmlParser
 processedRooms$ = this.store.select(
      selectRoomsData
);
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
        this.loaded = true;
        this.store.dispatch(getAllRooms())
      }
    }
      
      
  selectedFiles: File[] = []
  fileIndexes: number[] = []
  updatefileIndexes() {
    this.fileIndexes = Array.from({ length: this.selectedFiles.length }, (_, i) => i);
}

  handleFileChange = (e:Event)=>{
    let files = (e.target as HTMLInputElement).files ? (e.target as HTMLInputElement).files! : null;
    if(!files){
      return
    }
    if (files) {
      for (let i = 0; i < files.length; i++) {
        this.selectedFiles = [...this.selectedFiles, files[i]];
      }
    }
    this.roomForm.get('images')?.setValue(this.selectedFiles)
    this.updatefileIndexes()
  }
  altStorage: string[] = [];
  handleAltChange(e: Event, index: number) {
  const value = (e.target as HTMLInputElement).value;
  this.altStorage[index] = value;
  this.roomForm.patchValue({ alts: this.altStorage });
}

  roomForm = new FormGroup({
    room_type: new FormControl("", Validators.required),
    description: new FormControl("", Validators.required),
    price_per_night: new FormControl("", Validators.required),
    images: new FormControl<File[]>([], Validators.required),
    alts: new FormControl<String[]>([], Validators.required),
    roomNumber: new FormControl<Number>(0, Validators.required),
  })

  onSubmit() {
  if (this.roomForm.valid) {
    const formValue = this.roomForm.value;
    const formData = new FormData();
   
    // 1. Append JSON as a Blob
    const json = JSON.stringify({
      roomType: formValue.room_type,
      description: formValue.description,
      pricePerNight: formValue.price_per_night,
      roomNumber:formValue.roomNumber,
      alts:formValue.alts
    });
    console.log("JSON to be sent:", json);
    formData.append("data", new Blob([json], { type: "application/json" }));
    for (const file of formValue.images!) {
  console.log("File:", file.name, "Size:", file.size / 1024 / 1024, "MB");
}

    // 2. Append images
    for (const file of formValue.images!) {
      formData.append("images", file);
    }
    // send to API
    console.log("FormData to be sent:", formData);
    this.store.dispatch(createRoom({roomData: formData}))
        this.roomForm.reset()
  }
}
  //existingModule
  existingModule = false;
  toggleExistingModule = ()=>{
    this.existingModule = !this.existingModule;
  }
//update & delete
  editModule = false;
  editData: Room | null = null
  handleEdit = (data: Room)=>{
    this.editModule = true;
    this.editData = data;
  }
  handleDelete = (id:number)=>{
    this.store.dispatch(deleteRoom({id:id}))
  }
//close
  handleClose = ()=>{
    this.editModule = false;
  }
}
