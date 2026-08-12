import { HttpClient } from "@angular/common/http";
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from "@angular/forms";
import { Observable, map, of } from "rxjs";

export class UniqueUsername{
    constructor(private httpClient: HttpClient ){}
    private baseUrl = 'http://localhost:8080/api/auth';

    uniqueUsernameValidator(): AsyncValidatorFn{
        return (control:AbstractControl): Observable<ValidationErrors | null> =>{
            const value = control.value;
            if(!value){
                return of(null)
            }
            return this.httpClient.get<boolean>(`${this.baseUrl}/uniqueUsername`,{params:{username:value}}).pipe(
                map(isTaken => (isTaken ? { usernameTaken: true } : null))

            )
        }
    }
}