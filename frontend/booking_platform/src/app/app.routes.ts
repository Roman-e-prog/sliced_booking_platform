import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { BookingComponent } from './pages/booking/booking.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { LoginComponent } from './pages/login/login.component';
import { AuthGuard } from './services/permission.service';
import { AllRoomsComponent } from './pages/all-rooms/all-rooms.component';
export const routes: Routes = [
    {path:"", component:HomeComponent, pathMatch:'full'},
    {path:"register", component:RegisterComponent},
    {path:"login", component:LoginComponent},
    {path:"dashboard", canActivate: [AuthGuard], component:DashboardComponent},
    {path: "booking", component:AllRoomsComponent},
    {path:"booking/:id", component:BookingComponent},
    
];
