import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Store } from "@ngrx/store";
import { createPrice, createPriceError, createPriceSuccess, deletePrice, deletePriceError, deletePriceSuccess, getAllPrices, getAllPricesError, getAllPricesSuccess, getPrice, getPriceError, getPriceSuccess, updatePrice, updatePriceError, updatePriceSuccess } from "../actions/price.actions";
import { catchError, map, mergeMap, of, tap } from "rxjs";
import { MatSnackBar } from "@angular/material/snack-bar";
@Injectable()
export class PriceEffect{
    apiUrl = 'http://localhost:8080/api/priceSetting';

    createPrice$ = createEffect(()=>
        this.actions$.pipe(
            ofType(createPrice),
            mergeMap((action)=>{
                return this.httpClient.post(`${this.apiUrl}/`, action.priceData).pipe(
                    map((response:any)=>{
                        return createPriceSuccess({priceData:response})
                    }),
                    catchError((error)=>{
                        console.log(error, 'error')
                        return of(createPriceError({error}))
                    })
                )
            })
        )
    )
    updatePrice$ = createEffect(()=>
        this.actions$.pipe(
            ofType(updatePrice),
            mergeMap((action)=>{
                
                return this.httpClient.put(`${this.apiUrl}/${action.id}`, action.priceData).pipe(
                    map((response:any)=>{
                        return updatePriceSuccess({priceData:response})
                    }),
                    catchError((error)=>{
                        return of(updatePriceError({error}))
                    })
                )
            })
        )
    )
    deletePrice$ = createEffect(()=>
        this.actions$.pipe(
            ofType(deletePrice),
            mergeMap((action)=>{
                return this.httpClient.delete(`${this.apiUrl}/${action.id}`).pipe(
                    map(()=>{
                        return deletePriceSuccess({id:action.id})
                    }),
                    catchError((error)=>{
                        return of(deletePriceError({error}))
                    })
                )
            })
        )
    )
    getPrice$ = createEffect(() =>
    this.actions$.pipe(
        ofType(getPrice),
        mergeMap(action =>
        this.httpClient.get(`${this.apiUrl}/${action.id}`).pipe(
            map((response: any) => {
                return getPriceSuccess({ priceData: response });
            }),
            catchError(error => of(getPriceError({error})))
        )
        )
    )
    );
    getAllPrices$ = createEffect(() =>
        this.actions$.pipe(
            ofType(getAllPrices),
            mergeMap(() =>
            this.httpClient.get(`${this.apiUrl}/all`).pipe(
                map((response: any) => {
                    return getAllPricesSuccess({ priceData: response });
                }),
                catchError(error => of(getAllPricesError({error})))
                    )
                )
            )
        );
// -----------------------------
  // AUTO-RELOAD AFTER SUCCESS
  // -----------------------------
  reloadAfterCreate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(createPriceSuccess),
      map(() => getAllPrices())
    )
  );

  reloadAfterUpdate$ = createEffect(() =>
    this.actions$.pipe(
      ofType(updatePriceSuccess),
      map(() => getAllPrices())
    )
  );

  reloadAfterDelete$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deletePriceSuccess),
      map(() => getAllPrices())
    )
  );


    showPriceError$ = createEffect(
  () =>
    this.actions$.pipe(
      ofType(createPriceError, updatePriceError, deletePriceError, getAllPricesError),
      tap(({ error }) => {
        console.log("triggered 6 of prices")
        const msg =
            error?.error?.message ||
            error?.message ||
            'Unknown error occurred';
            console.log(msg, 'msg')
        this.snackBar.open(msg, 'error', {
          duration: 3000,
          panelClass: ['error-snackBar']
        });
      })
    ),
  { dispatch: false }
);


        constructor(private store: Store, private actions$: Actions, private httpClient: HttpClient, private snackBar: MatSnackBar){}
}