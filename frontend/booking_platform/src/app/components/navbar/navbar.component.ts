import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NavlinksService } from '../../services/navlinks.service';
import { Router, RouterLink } from "@angular/router";
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
interface Navlinks{
  url:string,
  name:string,
}
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent implements OnInit{
  constructor(
     private navlinksService: NavlinksService, 
     private authService: AuthService,
    ){}
  navlinks: Navlinks[] | null = null;
  user = this.authService.getUser();
  
  ngOnInit(): void {
    this.navlinksService.getNavlinks().subscribe({
      next:(data)=>this.navlinks = data,
      error:(error)=>console.log("Error fetching data", error)
    })
    this.navlinksService.fetchNavlinks();
  }
  handleLogout = ()=>{
    this.authService.logout();
  }
}
