import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectRoomError, selectRoomLoading, selectRoomMessage, selectRoomsData } from '../../store/selectors/room.selector';
import { MatSnackBar } from '@angular/material/snack-bar';
import { getAllRooms } from '../../store/actions/room.actions';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { RouterLink } from "@angular/router";
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { tap } from 'rxjs';
import { MobileNavbarComponent } from '../../components/mobile-navbar/mobile-navbar.component';

@Component({
  selector: 'app-all-rooms',
  standalone: true,
  imports: [NavbarComponent, RouterLink, CommonModule, MobileNavbarComponent],
  templateUrl: './all-rooms.component.html',
  styleUrl: './all-rooms.component.scss'
})
export class AllRoomsComponent implements OnInit{
  constructor(
    private store: Store,
    private snackBar: MatSnackBar,
    @Inject(PLATFORM_ID) private platformId: Object
  ){}
 
  allRooms$ = this.store.select(selectRoomsData);
  isError$ = this.store.select(selectRoomError);
  isLoading$ = this.store.select(selectRoomLoading);
  message$ = this.store.select(selectRoomMessage);
 
  loaded = false;
     ngOnInit(): void {
      this.allRooms$.subscribe(rooms => {
        console.log('Fetched rooms:', rooms);
      });
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
}
