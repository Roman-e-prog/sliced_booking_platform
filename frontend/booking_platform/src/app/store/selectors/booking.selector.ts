import {createFeatureSelector, createSelector} from '@ngrx/store';
import { BookingState } from '../reducers/booking.reducer';

const selectBooking = createFeatureSelector<BookingState>('booking');


export const selectBookingsData = createSelector(
    selectBooking,
    (state)=>state.Bookings
)
export const selectBookingData = createSelector(
    selectBooking,
    (state)=>state.Booking
)
export const selectBookingLoading = createSelector(
    selectBooking,
    (state)=>state.isLoading
)
export const selectBookingSuccess = createSelector(
    selectBooking,
    (state)=>state.isSuccess
)
export const selectBookingError = createSelector(
    selectBooking,
    (state)=>state.isError
)
export const selectBookingMessage = createSelector(
    selectBooking,
    (state)=>state.message
)