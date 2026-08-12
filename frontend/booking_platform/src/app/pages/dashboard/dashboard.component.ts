import { Component } from '@angular/core';
import { RoomCreatorComponent } from '../../components/dashboardComponents/room-creator/room-creator.component';
import { PriceSetterComponent } from '../../components/dashboardComponents/price-setter/price-setter.component';
import { BookingOverviewComponent } from '../../components/dashboardComponents/booking-overview/booking-overview.component';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { MobileNavbarComponent } from '../../components/mobile-navbar/mobile-navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RoomCreatorComponent, 
    PriceSetterComponent, 
    BookingOverviewComponent,  
    NavbarComponent,
    MobileNavbarComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {

}
