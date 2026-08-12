import { createAction, props } from "@ngrx/store";
import { Room } from "../reducers/room.reducer";

export const createRoom = createAction('[room] Create Room', props<{roomData:FormData}>() )
export const createRoomSuccess = createAction('[room] Create Room Success', props<{roomData:Room}>())
export const createRoomError = createAction('[room] Create Room error', props<{error:any}>())

export const updateRoom = createAction('[room] Update Room', props<{id:number, roomData:FormData}>() )
export const updateRoomSuccess = createAction('[room] Update Room Success', props<{roomData:Room}>())
export const updateRoomError = createAction('[room] Update Room error', props<{error:any}>())

export const deleteRoom = createAction('[room] Delete Room', props<{id:number}>() )
export const deleteRoomSuccess = createAction('[room] delete Room Success', props<{id:number}>())
export const deleteRoomError = createAction('[room] delete Room error', props<{error:any}>())

export const getRoom = createAction('[room] get Room', props<{id:number}>() )
export const getRoomSuccess = createAction('[room] get Room Success', props<{roomData:Room}>())
export const getRoomError = createAction('[room] get Room error', props<{error:any}>())

export const getAllRooms = createAction('[room] getAll Room')
export const getAllRoomsSuccess = createAction('[room] getAll Room Success', props<{roomData:Room[]}>())
export const getAllRoomsError = createAction('[room] getAll Room error', props<{error:any}>())