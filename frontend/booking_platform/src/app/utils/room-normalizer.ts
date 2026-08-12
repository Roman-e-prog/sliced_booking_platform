import { Combined, Room } from "../store/reducers/room.reducer";



export function normalizeCombined(c: Combined): Room {
  return {
    ...c.room,
    images: c.images
  };
}

export function normalizeCombinedList(list: Combined[]): Room[] {
  return list.map(c => normalizeCombined(c));
}
