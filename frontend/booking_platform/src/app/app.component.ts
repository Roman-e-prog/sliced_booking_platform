import { Component, ApplicationRef } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { InactivityService } from './services/inactivity.service';
import { take } from 'rxjs';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'booking_platform';
  constructor(private inactivityService: InactivityService, private appRef:ApplicationRef, private authService: AuthService){
  }
    ngOnInit() {
          this.authService.initAuthState();
          this.inactivityService.startMonitoring();
     
  }
}
