import { Injectable, inject, Inject, PLATFORM_ID } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn, Router, UrlTree } from "@angular/router";
import { AuthService } from "./auth.service";
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
class PermissionsService {

  constructor(
    private router: Router, 
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object,
  ) {}

  canActivate(): boolean | UrlTree {
    // SSR: always allow
    if (!isPlatformBrowser(this.platformId)) {
      return true;
    }

    // Browser: check BehaviorSubject value
    if (this.authService.isTokenExpired()) {
      return this.router.createUrlTree(['/login']);
    }
    const user = this.authService.getUser();
    if(!user || user.role !== "ADMIN"){
      return false
    }
    return true;
  }
}

export const AuthGuard: CanActivateFn = () =>
  inject(PermissionsService).canActivate();
