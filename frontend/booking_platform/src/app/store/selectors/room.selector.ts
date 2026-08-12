import {createFeatureSelector, createSelector} from '@ngrx/store';
import { RoomState } from '../reducers/room.reducer';

const selectRoom = createFeatureSelector<RoomState>('room');


export const selectRoomsData = createSelector(
    selectRoom,
    (state)=>state.rooms
)
export const selectRoomData = createSelector(
    selectRoom,
    (state)=>state.room
)
export const selectRoomLoading = createSelector(
    selectRoom,
    (state)=>state.isLoading
)
export const selectRoomSuccess = createSelector(
    selectRoom,
    (state)=>state.isSuccess
)
export const selectRoomError = createSelector(
    selectRoom,
    (state)=>state.isError
)
export const selectRoomMessage = createSelector(
    selectRoom,
    (state)=>state.message
)