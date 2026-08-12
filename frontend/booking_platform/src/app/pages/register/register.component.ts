import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { UniqueUsername } from '../../validators/uniqueUsername';
import { HttpClient } from '@angular/common/http';
import { UniqueEmail } from '../../validators/uniqueEmail';
import { RegisterService } from '../../services/register.service';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatNativeDateModule } from '@angular/material/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { AuthService } from '../../services/auth.service';
import { MobileNavbarComponent } from '../../components/mobile-navbar/mobile-navbar.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatNativeDateModule,
    RouterLink,
    NavbarComponent,
    MobileNavbarComponent
],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit{
  usernameValidator:UniqueUsername;
  emailValidator:UniqueEmail;
  constructor(
    private httpClient: HttpClient, 
    private registerService: RegisterService,
    private router:Router,
    private snackBar:MatSnackBar,
    private authService: AuthService,
  ){
    this.usernameValidator = new UniqueUsername(httpClient);
    this.emailValidator = new UniqueEmail(httpClient)
  }
   user = this.authService.getUser();
  registerForm:any = FormGroup;
   ngOnInit(): void {
    if(this.user){
      this.router.navigate(['/'])
    }
    this.registerForm = new FormGroup({
      prename:new FormControl("", Validators.required),
      lastname:new FormControl("", Validators.required),
      username:new FormControl(null,{
        validators: [Validators.required],
        asyncValidators: [this.usernameValidator.uniqueUsernameValidator()],
      updateOn: 'blur'
      } ),
      email:new FormControl(null,{
        validators:[Validators.required, Validators.email],
        asyncValidators:[this.emailValidator.uniqueEmailValidator()],
        updateOn:'blur'
      }),
      street: new FormControl("", Validators.required),
      houseNumber: new FormControl("", Validators.required),
      postalCode: new FormControl("", Validators.required),
      town: new FormControl("", Validators.required),
      country: new FormControl("", Validators.required),
      birthDate: new FormControl<Date | null>(null, { validators: [Validators.required] }),
      password: new FormControl("", [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\W)(?!.* ).+$/)
      ]),
      passwordConfirm:new FormControl("", Validators.required)
    }, {validators: this.passwordMatchValidator, updateOn:'blur'})
    // this.registerForm.get('birthDate')?.valueChanges.subscribe((v:any) => {
    //     console.log("Birthdate value:", v);
    //   });
    // this.registerForm.get('password')?.valueChanges.subscribe((v:any) => {
    //     console.log("Password value:", v);
    //   });

  }
  passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
      const password = group.get('password')?.value;
      const passwordConfirm = group.get('passwordConfirm')?.value;
      return password === passwordConfirm ? null : { passwordsMismatch: true };
    }
    onSubmit():void {
      if(this.registerForm.valid){
        const {prename, lastname, username, email, street, houseNumber, postalCode, town, country, birthDate, password } = this.registerForm.value
        this.registerService.registerUser(prename, lastname, username, email, street, houseNumber, postalCode, town, country, birthDate, password).subscribe({
          next:(response)=>{
            this.router.navigate(['/login'])
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
