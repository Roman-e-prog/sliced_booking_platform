import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

interface Navlinks{
  url:string,
  name:string,
}
@Injectable({
  providedIn: 'root'
})
export class NavlinksService {
  constructor(
    private httpClient:HttpClient
  ) { }
  //provide a current value, needs an initialValue
  private navlinksSubject = new BehaviorSubject<Navlinks[] | null>(null)
  //i store the subject as collection of multiple values
  private navlinks$ = this.navlinksSubject.asObservable();
  //fetcher and getter
  //get request and subscription of data to the subject
  fetchNavlinks = ()=>{
    return this.httpClient.get<Navlinks[]>("assets/json/navlinks.json").subscribe({
      next: (data)=> this.navlinksSubject.next(data),
      error:(error)=>console.log("No navlinks available", error)
    })
  }
  //read current value
  getNavlinks = ()=>{
    return this.navlinks$
  }
}
