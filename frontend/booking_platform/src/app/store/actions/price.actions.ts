import { createAction, props } from "@ngrx/store";
import { Price } from "../reducers/price.reducer";
export const createPrice = createAction('[price] Create Price', props<{priceData:Price}>() )
export const createPriceSuccess = createAction('[price] Create Price Success', props<{priceData:Price}>())
export const createPriceError = createAction('[price] Create Price error', props<{error:any}>())

export const updatePrice = createAction('[price] Update Price', props<{id:number, priceData:Price}>() )
export const updatePriceSuccess = createAction('[price] Update Price Success', props<{priceData:Price}>())
export const updatePriceError = createAction('[price] Update Price error', props<{error:any}>())

export const deletePrice = createAction('[price] Delete Price', props<{id:number}>() )
export const deletePriceSuccess = createAction('[price] delete Price Success', props<{id:number}>())
export const deletePriceError = createAction('[price] delete Price error', props<{error:any}>())

export const getPrice = createAction('[price] get Price', props<{id:number}>() )
export const getPriceSuccess = createAction('[price] get Price Success', props<{priceData:Price}>())
export const getPriceError = createAction('[price] get Price error', props<{error:any}>())

export const getAllPrices = createAction('[price] getAll Price')
export const getAllPricesSuccess = createAction('[price] getAll Price Success', props<{priceData:Price[]}>())
export const getAllPricesError = createAction('[price] getAll Price error', props<{error:any}>())