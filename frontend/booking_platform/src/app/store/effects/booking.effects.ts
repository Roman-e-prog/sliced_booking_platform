import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import {
  createBooking, createBookingError, createBookingSuccess,
  deleteBooking, deleteBookingError, deleteBookingSuccess,
  getAllBookings, getAllBookingsError, getAllBookingsSuccess,
  getBooking, getBookingError, getBookingSuccess,
  updateBooking, updateBookingError, updateBookingSuccess
} from "../actions/booking.actions";
import { catchError, map, mergeMap, of, tap } from "rxjs";
import { MatSnackBar } from "@angular/material/snack-bar";


@Injectable()
export class BookingEffect {

  apiUrl = 'http://localhost:8080/api/booking';

  // -----------------------------
  // CREATE
  // -----------------------------
  createBooking$ = createEffect(() =>
    this.actions$.pipe(
      ofType(createBooking),
      mergeMap((action) =>{
        console.log("Action received in effect:", action.bookingData);
        return this.httpClient.post(`${this.apiUrl}/`, action.bookingData).pipe(
          map((response: any) => {
            return createBookingSuccess({ bookingData: response });
          }),
          catchError(error => {
            console.error("Error creating booking:", error);
            return of(createBookingError({ error }));
          })
        );
      })
    )
  );

  // -----------------------------
  // UPDATE
  // -----------------------------
  updateBooking$ = createEffect(() =>
    this.actions$.pipe(
      ofType(updateBooking),
      mergeMap(action =>
        this.httpClient.put(`${this.apiUrl}/${action.id}`, action.bookingData).pipe(
          map((response: any) => {
            return updateBookingSuccess({ bookingData: response });
          }),
          catchError(error => of(updateBookingError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // DELETE
  // -----------------------------
  deleteBooking$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteBooking),
      mergeMap(action =>
        this.httpClient.delete(`${this.apiUrl}/${action.id}`).pipe(
          map(() => deleteBookingSuccess({ id: action.id })),
          catchError(error => of(deleteBookingError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // GET SINGLE BOOKING
  // -----------------------------
  getBooking$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getBooking),
      mergeMap(action =>
        this.httpClient.get(`${this.apiUrl}/${action.id}`).pipe(
          map((response:any) => {
            return getBookingSuccess({ bookingData: response });
          }),
          catchError(error => of(getBookingError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // GET ALL BOOKINGS
  // -----------------------------
  getAllBookings$ = createEffect(() =>
    this.actions$.pipe(
      ofType(getAllBookings),
      mergeMap(() =>
        this.httpClient.get(`${this.apiUrl}/all`).pipe(
          map((response: any) => {
        
            return getAllBookingsSuccess({ bookingData: response });
          }),
          catchError(error => of(getAllBookingsError({ error })))
        )
      )
    )
  );

  // -----------------------------
  // AUTO-RELOAD AFTER SUCCESS
  // -----------------------------
  reloadAfterCreate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(createBookingSuccess),
      map(() => getAllBookings())
    )
  );

  reloadAfterUpdate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(updateBookingSuccess),
      map(() => getAllBookings())
    )
  );

  reloadAfterDelete$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteBookingSuccess),
      map(() => getAllBookings())
    )
  );

  // -----------------------------
  // ERROR HANDLING
  // -----------------------------
  showBookingError$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(createBookingError, updateBookingError, deleteBookingError, getAllBookingsError),
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
  ) {}
}
