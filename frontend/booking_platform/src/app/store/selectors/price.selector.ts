import {createFeatureSelector, createSelector} from '@ngrx/store';
import { PriceState } from '../reducers/price.reducer';

const selectPrice = createFeatureSelector<PriceState>('price');

export const selectPricesData = createSelector(
    selectPrice,
    (state)=>state.Prices ?? []
)
export const selectPriceData = createSelector(
    selectPrice,
    (state)=>state.Price ?? {}
)
export const selectPriceLoading = createSelector(
    selectPrice,
    (state)=>state.isLoading
)
export const selectPriceSuccess = createSelector(
    selectPrice,
    (state)=>state.isSuccess
)
export const selectPriceError = createSelector(
    selectPrice,
    (state)=>state.isError
)
export const selectPriceMessage = createSelector(
    selectPrice,
    (state)=>state.message
)