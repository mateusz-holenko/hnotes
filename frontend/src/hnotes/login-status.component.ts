import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth-service'

@Component({
  selector: 'login-status',
  templateUrl: './login-status.component.html',
  styleUrl: './login-status.component.css'
})
export class LoginStatusComponent {
  loggedIn: boolean = false;

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  constructor(private authService: AuthService, private router: Router) {
    authService.currentStatus.subscribe(s => {
      this.loggedIn = this.authService.isLoggedIn();
    });
  }
}
