import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import {
  createRoom, createRoomError, createRoomSuccess,
  deleteRoom, deleteRoomError, deleteRoomSuccess,
  getAllRooms, getAllRoomsError, getAllRoomsSuccess,
  getRoom, getRoomError, getRoomSuccess,
  updateRoom, updateRoomError, updateRoomSuccess
} from "../actions/room.actions";
import { catchError, map, mergeMap, of, tap } from "rxjs";
import { MatSnackBar } from "@angular/material/snack-bar";
import { normalizeCombined, normalizeCombinedList } from '../../utils/room-normalizer';
import { Combined } from "../reducers/room.reducer";
import { HtmlStripService } from "../../services/htmlStrip.service";

@Injectable()
export class RoomEffect {

  apiUrl = 'http://localhost:8080/api/rooms';

  // -----------------------------
  // CREATE
  // -----------------------------
  createRoom$ = createEffect(() =>
    this.actions$.pipe(
      ofType(createRoom),
      mergeMap((action) =>{
        console.log("Action received in effect:", action.roomData);
        return this.httpClient.post(`${this.apiUrl}/`, action.roomData).pipe(
          map((response: any) => {
            const normalized = normalizeCombined(response);
            console.log("Normalized response:", normalized);
            const processed = {
              ...normalized,
              description: this.htmlStripService.stripHtml(normalized.description).trim()
            };
            console.log("Processed response:", processed);
            return createRoomSuccess({ roomData: processed });
          }),
          catchError(error => {
            console.error("Error creating room:", error);
            return of(createRoomError({ error }));
          })
        );
      })
    )
  );

  // -----------------------------
  // UPDATE
  // -----------------------------
  updateRoom$ = createEffect(() =>
    this.actions$.pipe(
      ofType(updateRoom),
      mergeMap(action =>
        this.httpClient.post(`${this.apiUrl}/${action.id}`, action.roomData).pipe(
          map((response: any) => {
            const normalized = normalizeCombined(response);
            const processed = {
              ...normalized,
              description: this.htmlStripService.stripHtml(normalized.description).trim()
            };
            return updateRoomSuccess({ roomData: processed });
          }),
          catchError(error => of(updateRoomError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // DELETE
  // -----------------------------
  deleteRoom$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteRoom),
      mergeMap(action =>
        this.httpClient.delete(`${this.apiUrl}/${action.id}`).pipe(
          map(() => deleteRoomSuccess({ id: action.id })),
          catchError(error => of(deleteRoomError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // GET SINGLE ROOM
  // -----------------------------
  getRoom$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getRoom),
      mergeMap(action =>
        this.httpClient.get<Combined>(`${this.apiUrl}/${action.id}`).pipe(
          map((response: Combined) => {
            const normalized = normalizeCombined(response);
            const processed = {
              ...normalized,
              description: this.htmlStripService.stripHtml(normalized.description).replace(/\u00A0/g, ' ').trim()
            };
            return getRoomSuccess({ roomData: processed });
          }),
          catchError(error => of(getRoomError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // GET ALL ROOMS
  // -----------------------------
  getAllRooms$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getAllRooms),
      mergeMap(() =>
        this.httpClient.get<Combined[]>(`${this.apiUrl}/all`).pipe(
          map((response: Combined[]) => {
            const normalized = normalizeCombinedList(response);
            const processed = normalized.map(room => ({
              ...room,
              description: this.htmlStripService.stripHtml(room.description).replace(/\u00A0/g, ' ').trim()
            }));
            return getAllRoomsSuccess({ roomData: processed });
          }),
          catchError(error => of(getAllRoomsError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // AUTO-RELOAD AFTER SUCCESS
  // -----------------------------
  reloadAfterCreate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(createRoomSuccess),
      map(() => getAllRooms())
    )
  );

  reloadAfterUpdate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(updateRoomSuccess),
      map(() => getAllRooms())
    )
  );

  reloadAfterDelete$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteRoomSuccess),
      map(() => getAllRooms())
    )
  );

  // -----------------------------
  // ERROR HANDLING
  // -----------------------------
  showRoomError$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(createRoomError, updateRoomError, deleteRoomError, getAllRoomsError),
        tap(({ error }) => {
          const msg =
            error?.error?.message ||
            error?.message ||
            'Unknown error occurred';

          this.snackBar.open(msg, 'error', {
            duration: 3000,
            panelClass: ['error-snackBar']
          });
        })
      ),
    { dispatch: false }
  );

  constructor(
    private store: Store,
    private actions$: Actions,
    private httpClient: HttpClient,
    private snackBar: MatSnackBar,
    private htmlStripService: HtmlStripService
  ) {}
}
