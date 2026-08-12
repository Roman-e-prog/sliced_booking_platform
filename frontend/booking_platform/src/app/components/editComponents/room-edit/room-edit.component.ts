import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule, FormArray } from '@angular/forms';
import { Store } from '@ngrx/store';
import { updateRoom } from '../../../store/actions/room.actions';
import { Image, Room } from '../../../store/reducers/room.reducer';
import { CommonModule } from '@angular/common';
import { QuillModule } from 'ngx-quill';

@Component({
  selector: 'app-room-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, QuillModule],
  templateUrl: './room-edit.component.html',
  styleUrl: './room-edit.component.scss'
})
export class RoomEditComponent implements OnInit {

  constructor(private store: Store) {}

  @Input() editRoomData: Room | null = null;
  @Output() closeEdit = new EventEmitter();
    handleClose = ()=>{
    this.closeEdit.emit()
  }
  images!: FormArray<FormGroup>;
  roomEditForm!: FormGroup;

  ngOnInit(): void {

    // ---------------------------------------------------------
    // 1. FormArray für Bilder initialisieren
    // ---------------------------------------------------------
    this.images = new FormArray<FormGroup>([]);

    // ---------------------------------------------------------
    // 2. Hauptformular
    // ---------------------------------------------------------
    this.roomEditForm = new FormGroup({
      room_type: new FormControl("", Validators.required),
      description: new FormControl("", Validators.required),
      price_per_night:new FormControl("", Validators.required),
      roomNumber: new FormControl<Number>(0, Validators.required),
      images: this.images
    });

    // ---------------------------------------------------------
    // 3. Prefill bestehender Daten
    // ---------------------------------------------------------
    if (this.editRoomData) {

      this.roomEditForm.patchValue({
        room_type: this.editRoomData.roomType,
        description: this.editRoomData.description,
        price_per_night: this.editRoomData.pricePerNight,
        roomNumber: this.editRoomData.roomNumber ? this.editRoomData.roomNumber : 0,
      });

      // ---------------------------------------------------------
      // 4. Bestehende Bilder in FormArray pushen
      //    → jedes Bild bekommt Metadaten + file=null + isNew=false
      // ---------------------------------------------------------
      this.editRoomData.images?.forEach(img => {
        this.images.push(
          new FormGroup({
            imageId: new FormControl(img.imageId),
            roomId: new FormControl(img.roomId),
            alt: new FormControl(img.alt),
            title: new FormControl(img.title),
            file: new FormControl(null),   // Datei optional
            isNew: new FormControl(false)  // bestehendes Bild
          })
        );
      });
    }
  }

  // ---------------------------------------------------------
  // 5. FormGroup für neue Bilder
  // ---------------------------------------------------------
  createImageGroup(): FormGroup {
    return new FormGroup({
      imageId: new FormControl(null),
      roomId: new FormControl(null),
      alt: new FormControl(""),
      title: new FormControl(""),
      file: new FormControl(null),
      isNew: new FormControl(true) // neues Bild → Datei MUSS vorhanden sein
    });
  }

  // ---------------------------------------------------------
  // 6. Datei-Upload für ein Bild
  // ---------------------------------------------------------
  handleFile(index: number, event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0] ?? null;
    this.images.at(index).patchValue({ file });
  }

  // ---------------------------------------------------------
  // 7. Neues Bild hinzufügen
  // ---------------------------------------------------------
  addImage() {
    this.images.push(this.createImageGroup());
  }

  // ---------------------------------------------------------
  // 8. Bild löschen
  // ---------------------------------------------------------
  removeImage(index: number) {
    this.images.removeAt(index);
  }

  // ---------------------------------------------------------
  // 9. Submit
  // ---------------------------------------------------------
  onSubmit() {
    if (!this.roomEditForm.valid) return;

    // ---------------------------------------------------------
    // 9.1 Validierung: jedes neue Bild MUSS eine Datei haben
    // ---------------------------------------------------------
    for (let i = 0; i < this.images.length; i++) {
      const img = this.images.at(i).value;

      if (img.isNew && !img.file) {
        alert("Bitte wählen Sie eine Datei für jedes neue Bild aus.");
        return;
      }
    }

    // ---------------------------------------------------------
    // 9.2 FormData erzeugen
    // ---------------------------------------------------------
    const formData = new FormData();

    // ---------------------------------------------------------
    // 9.3 JSON-Metadaten erzeugen
    // ---------------------------------------------------------
    const json = {
      roomType: this.roomEditForm.value.room_type,
      description: this.roomEditForm.value.description,
      pricePerNight: this.roomEditForm.value.price_per_night,
      roomNumber: this.roomEditForm.value.roomNumber,
      // Metadaten für jedes Bild
      images: this.images.value.map((img: Image) => ({
        imageId: img.imageId,
        roomId: img.roomId,
        alt: img.alt,
        title: img.title
      }))
    };
    console.log(json, 'json to send')
    console.log(this.images, 'images')
    formData.append(
      "data",
      new Blob([JSON.stringify(json)], { type: "application/json" })
    );

    // ---------------------------------------------------------
    // 9.4 Dateien anhängen (nur wenn vorhanden)
    // ---------------------------------------------------------
    this.images.controls.forEach((ctrl: any) => {
      const file = ctrl.get('file')?.value;
      if (file) {
        formData.append("images", file);
      }
    });

    // Debug
    for (const [key, value] of formData.entries()) {
      console.log(`${key}:`, value);
    }

    // ---------------------------------------------------------
    // 9.5 Dispatch an NgRx
    // ---------------------------------------------------------
    this.store.dispatch(updateRoom({
      id: this.editRoomData!.roomId!,
      roomData: formData
    }));

    this.closeEdit.emit();
  }
}
