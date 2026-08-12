import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { MobileNavbarComponent } from '../../components/mobile-navbar/mobile-navbar.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NavbarComponent,
    MobileNavbarComponent
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  constructor(
    private authService:AuthService, 
    private router:Router,
    private snackBar: MatSnackBar){

  }
  loginForm:any = FormGroup;
  ngOnInit(): void {
    this.loginForm = new FormGroup({
      email:new FormControl(null,{
              validators:[Validators.required, Validators.email],
              updateOn:'blur'
            }),
      password:new FormControl("",[ Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).+$/)
      ]),
    })
  }
  onSubmit(): void{
    if(this.loginForm.valid){
      const {email, password} = this.loginForm.value;
      this.authService.login(email, password).subscribe({
        next:(response)=>{
          this.router.navigate(["/"])
        },
        error:(error)=>{
          this.snackBar.open(error, 'error',{
            duration:3000,
            panelClass: ['error-snackBar']
          })
        }
      })
    }
  }    
}
