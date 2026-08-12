import { createReducer, on } from "@ngrx/store";
import { createPrice, createPriceError, createPriceSuccess, deletePrice, deletePriceError, deletePriceSuccess, getAllPrices, getAllPricesError, getAllPricesSuccess, getPrice, getPriceError, getPriceSuccess, updatePrice, updatePriceError, updatePriceSuccess } from "../actions/price.actions";

export interface Price{
    priceId?: number;
    roomType:string;
    bookingType:string;
    nettoPrice:number,
    taxRate:number,
    created_at?: Date;
    updated_at?: Date;
}
export interface PriceState{
    Prices: Price[];
    Price: Price;
    isSuccess:boolean;
    isLoading:boolean;
    isError:boolean;
    message:string;
}
const initialState:PriceState = {
    Prices: [],
    Price: {} as Price,
    isSuccess:false,
    isLoading:false,
    isError:false,
    message:"",
}
export const priceReducer = createReducer(
    initialState,
    on(createPrice,(state)=>({...state, isLoading:true})),
    on(createPriceSuccess, (state, {priceData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Prices: [...state.Prices, priceData]
    })),
    on(createPriceError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(updatePrice,(state)=>({...state, isLoading:true})),
    on(updatePriceSuccess, (state, {priceData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Price:{...priceData}
    })),
    on(updatePriceError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(deletePrice,(state)=>({...state, isLoading:true})),
    on(deletePriceSuccess, (state, {id})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Prices: state.Prices.filter((item)=>item.priceId !== id)
    })),
    on(deletePriceError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(getPrice,(state)=>({...state, isLoading:true})),
    on(getPriceSuccess, (state, {priceData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Price: priceData,
    })),
    on(getPriceError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
    on(getAllPrices,(state)=>({...state, isLoading:true})),
    on(getAllPricesSuccess, (state, {priceData})=>({
        ...state, 
        isLoading:false,
        isSuccess:true,
        Prices: priceData,
    })),
    on(getAllPricesError, (state, { error }) => ({
         ...state, 
         isLoading: false, 
         isError: true, 
         message: error
        })),
)