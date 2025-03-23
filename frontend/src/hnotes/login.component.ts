import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from './auth-service'
import { AppService } from './app-service';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  imports: [ReactiveFormsModule]
})
export class LoginComponent {
  loginForm = new FormGroup({
    handle: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required)
  })

  doLogin() {
    const creds = this.loginForm.value;

    this.authenticationService
      .login(creds.handle!, creds.password!)
      .subscribe(
        x => {
          console.log("Successfull login: " + x);
          this.router.navigate(['/notes']);
        },
        err => {
          console.log("Login error: " + JSON.stringify(err));
          this.appService.showError('Could not log in');
        }
      );
  }

  constructor(private authenticationService : AuthService, private appService : AppService, private router: Router) {
  }
}
