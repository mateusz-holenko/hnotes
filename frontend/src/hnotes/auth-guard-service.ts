import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { AuthService } from './auth-service';

@Injectable({providedIn: 'root'})
export class AuthGuardService implements CanActivate {
  canActivate() {
    if(!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  } 

  constructor(private authService: AuthService, private router: Router) {
  }
}
