import { createAction, props } from "@ngrx/store";
import {Booking} from "../reducers/booking.reducer";

export const createBooking = createAction('[booking] Create Booking', props<{bookingData:Booking}>() )
export const createBookingSuccess = createAction('[booking] Create Booking Success', props<{bookingData:Booking}>())
export const createBookingError = createAction('[booking] Create Booking error', props<{error:any}>())

export const updateBooking = createAction('[booking] Update Booking', props<{id:number, bookingData:Booking}>() )
export const updateBookingSuccess = createAction('[booking] Update Booking Success', props<{bookingData:Booking}>())
export const updateBookingError = createAction('[booking] Update Booking error', props<{error:any}>())

export const deleteBooking = createAction('[booking] Delete Booking', props<{id:number}>() )
export const deleteBookingSuccess = createAction('[booking] delete Booking Success', props<{id:number}>())
export const deleteBookingError = createAction('[booking] delete Booking error', props<{error:any}>())

export const getBooking = createAction('[booking] get Booking', props<{id:number}>() )
export const getBookingSuccess = createAction('[booking] get Booking Success', props<{bookingData:Booking}>())
export const getBookingError = createAction('[booking] get Booking error', props<{error:any}>())

export const getAllBookings = createAction('[booking] getAll Booking')
export const getAllBookingsSuccess = createAction('[booking] getAll Booking Success', props<{bookingData:Booking[]}>())
export const getAllBookingsError = createAction('[booking] getAll Booking error', props<{error:any}>())