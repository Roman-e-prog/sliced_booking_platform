import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NavlinksService } from '../../services/navlinks.service';
import { Router, RouterLink } from "@angular/router";
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
interface Navlinks{
  url:string,
  name:string,
}

@Component({
  selector: 'app-mobile-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mobile-navbar.component.html',
  styleUrl: './mobile-navbar.component.scss'
})
export class MobileNavbarComponent implements OnInit {
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
    menuOpen = false;
    handleMenu = ()=>{
      this.menuOpen = !this.menuOpen
      console.log(this.menuOpen)
    }

}
