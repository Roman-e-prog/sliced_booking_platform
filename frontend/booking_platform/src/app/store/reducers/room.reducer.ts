import { createReducer, on } from "@ngrx/store";
import { createRoom, createRoomError, createRoomSuccess, deleteRoom, deleteRoomError, deleteRoomSuccess, getAllRooms, getAllRoomsError, getAllRoomsSuccess, getRoom, getRoomError, getRoomSuccess, updateRoom, updateRoomError, updateRoomSuccess } from "../actions/room.actions";
export interface Combined{
    room:Room;
    images:Image[];
}
export interface Image{
    imageId:number,
    roomId:number,
    alt:string,
    title:string,
    path:string,
}
export interface Room{
    roomId?: number;
    roomType:string;
    description:string;
    isAvailable: boolean;
    alts?:string[],
    title?:string,
    images:Image[],
    created_at?: Date;
    updated_at?: Date;
    pricePerNight?: number;
    roomNumber?:number,
}
export interface RoomState{
    rooms: Room[];
    room: Room;
    isSuccess:boolean;
    isLoading:boolean;
    isError:boolean;
    message:string;
}
const initialState:RoomState = {
    rooms: [],
    room: {} as Room,
    isSuccess:false,
    isLoading:false,
    isError:false,
    message:"",
}
export const roomReducer = createReducer(
    initialState,
    on(createRoom,(state)=>({...state, isLoading:true})),
    on(createRoomSuccess, (state, {roomData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        rooms: [...state.rooms, roomData]
    })),
    on(createRoomError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(updateRoom,(state)=>({...state, isLoading:true})),
    on(updateRoomSuccess, (state, {roomData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        room:{...roomData}
    })),
    on(updateRoomError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(deleteRoom,(state)=>({...state, isLoading:true})),
    on(deleteRoomSuccess, (state, {id})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        rooms: state.rooms.filter((item)=>item.roomId !== id)
    })),
    on(deleteRoomError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(getRoom,(state)=>({...state, isLoading:true})),
    on(getRoomSuccess, (state, {roomData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        room: roomData,
    })),
    on(getRoomError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(getAllRooms,(state)=>({...state, isLoading:true})),
    on(getAllRoomsSuccess, (state, {roomData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        rooms: roomData,
    })),
    on(getAllRoomsError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
)