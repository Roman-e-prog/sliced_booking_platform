import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {
  authUrl = 'http://localhost:8080/api/auth'
  constructor(private httpClient: HttpClient) { }

  registerUser(prename:string, lastname:string, username:string, email:string, street:string, houseNumber:string, postalCode:number, town:string, country:string, birthDate:Date, password:string ){
    const response = this.httpClient.post(`${this.authUrl}/register`, {
      prename, lastname, username, email, street, houseNumber, postalCode, town, country, birthDate, password
    })
    return response;
  }
}
