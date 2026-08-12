import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS, provideHttpClient, withFetch,withInterceptorsFromDi } from '@angular/common/http';
import { JWTInterceptor } from './interceptor/http.interceptor';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideQuillConfig } from 'ngx-quill/config';
import { roomReducer } from './store/reducers/room.reducer';
import { RoomEffect } from './store/effects/room.effects';
import { provideAnimations } from '@angular/platform-browser/animations';
import { DateAdapter, NativeDateAdapter, MAT_DATE_FORMATS, MAT_NATIVE_DATE_FORMATS } from '@angular/material/core';
import { priceReducer} from './store/reducers/price.reducer';
import { PriceEffect } from './store/effects/price.effects';
import { bookingReducer } from './store/reducers/booking.reducer';
import { BookingEffect } from './store/effects/booking.effects';
import { LOCALE_ID } from '@angular/core'; 
import { registerLocaleData } from '@angular/common'; 
import localeDe from '@angular/common/locales/de'; 
registerLocaleData(localeDe);

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideClientHydration(),
    provideAnimations(),
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS,
        useClass: JWTInterceptor,
        multi: true },
    provideStore({
      room:roomReducer,
      price:priceReducer,
      booking:bookingReducer
    }),
    provideEffects([
      RoomEffect,
      PriceEffect, 
      BookingEffect
    ]),
    {provide: DateAdapter, useClass: NativeDateAdapter},
    { provide: LOCALE_ID, useValue: 'de-DE' },
   {provide: MAT_DATE_FORMATS, useValue: MAT_NATIVE_DATE_FORMATS},
     provideQuillConfig({
      modules: {
        syntax: false,
        toolbar: [
          ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
          ['blockquote', 'code-block'],
      
          [{ 'header': 1 }, { 'header': 2 }],               // custom button values
          [{ 'list': 'ordered'}, { 'list': 'bullet' }],
          [{ 'script': 'sub'}, { 'script': 'super' }],      // superscript/subscript
          [{ 'indent': '-1'}, { 'indent': '+1' }],          // outdent/indent
          [{ 'direction': 'rtl' }],                         // text direction
      
          [{ 'size': ['small', false, 'large', 'huge'] }],  // custom dropdown
          [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
      
          [{ 'color': [] }, { 'background': [] }],          // dropdown with defaults from theme
          [{ 'font': [] }],
          [{ 'align': [] }],
      
          ['clean'],                                         // remove formatting button
      
          ['link', 'image', 'video'] 
        ]
      }
    }),
]
};
