import { Injectable, NgZone } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class InactivityService {

  private timeout: any;
  private readonly inactivityLimit = 15 * 60 * 1000; // 15 minutes

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
    private ngZone: NgZone
  ) {}

  startMonitoring() {
    this.ngZone.runOutsideAngular(() => {
      this.resetTimer();
      if(typeof window !== 'undefined'){
      ['click', 'mousemove', 'keydown', 'scroll', 'touchstart']
        .forEach(event => {
          document.addEventListener(event, () => this.resetTimer());
        });
      }
    });
  }

  private resetTimer() {
    clearTimeout(this.timeout);

    this.timeout = setTimeout(() => {
      // Re-enter Angular zone for logout + navigation + snackbar
      this.ngZone.run(() => {
        this.authService.logout();
        this.router.navigate(['/login']);
        this.snackBar.open('Session expired due to inactivity.', 'error', {
          duration: 4000,
          panelClass: ['error-snackBar']
        });
      });
    }, this.inactivityLimit);
  }
   ngOnDestroy() {
    clearTimeout(this.timeout);
  }
}
