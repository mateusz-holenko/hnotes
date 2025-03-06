import { Component } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from './auth-service'

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  imports: [ReactiveFormsModule],
  providers: [AuthService]
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
        () => { console.log("Successfull login"); },
        err => { console.log("Login error"); }
      );
  }

  constructor(private authenticationService : AuthService) {
  }
}
