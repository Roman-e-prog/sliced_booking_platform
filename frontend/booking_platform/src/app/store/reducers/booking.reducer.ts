import { createReducer, on } from "@ngrx/store";
import { createBooking, createBookingError, createBookingSuccess, deleteBooking, deleteBookingError, deleteBookingSuccess, getAllBookings, getAllBookingsError, getAllBookingsSuccess, getBooking, getBookingError, getBookingSuccess, updateBooking, updateBookingError, updateBookingSuccess } from "../actions/booking.actions";

export interface Booking{
    bookingId?:number;
    numberOfPersons:number;
    startDate:string;
    endDate:string;
    bookingType:string;
    roomType:string;
    userType:string;
    pricePerNight?:number;
    fullPrice?:number;
    bruttoPrice?:number;
    tax?:number;
    userId?:number;
    user?:{
        userId:number,
        prename:string,
        lastname:string,
        username:string,
        street:string,
        houseNumber:string,
        postalCode:number,
        town:string,
        country:string,
        email:string,
        birthDate:string,
        role:string,
        createdAt:Date,
    };
    roomId?:number;
    createdAt?: Date;
    updated_at?: Date;
    roomNumber?:number,
}
export interface BookingState{
    Bookings: Booking[];
    Booking: Booking;
    isSuccess:boolean;
    isLoading:boolean;
    isError:boolean;
    message:string;
}
const initialState:BookingState = {
    Bookings: [],
    Booking: {} as Booking,
    isSuccess:false,
    isLoading:false,
    isError:false,
    message:"",
}
export const bookingReducer = createReducer(
    initialState,
    on(createBooking,(state)=>({...state, isLoading:true})),
    on(createBookingSuccess, (state, {bookingData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Bookings: [...state.Bookings, bookingData]
    })),
    on(createBookingError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(updateBooking,(state)=>({...state, isLoading:true})),
    on(updateBookingSuccess, (state, {bookingData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Booking: {...bookingData}
    })),
    on(updateBookingError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(deleteBooking,(state)=>({...state, isLoading:true})),
    on(deleteBookingSuccess, (state, {id})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Bookings: state.Bookings.filter((item)=>item.bookingId !== id)
    })),
    on(deleteBookingError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(getBooking,(state)=>({...state, isLoading:true})),
    on(getBookingSuccess, (state, {bookingData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Booking: bookingData,
    })),
    on(getBookingError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(getAllBookings,(state)=>({...state, isLoading:true})),
    on(getAllBookingsSuccess, (state, {bookingData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Bookings: bookingData,
    })),
    on(getAllBookingsError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
)