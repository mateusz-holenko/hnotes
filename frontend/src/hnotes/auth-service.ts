import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { shareReplay, tap } from 'rxjs/operators';
import { environment } from '../environments/environment';

@Injectable({providedIn: 'root'})
export class AuthService {
  private statusSource = new BehaviorSubject('logged-out');
  private jwt = '';
  private name = '';

  currentStatus = this.statusSource.asObservable();

  login(username: string, password: string) {
    return this.http
      .post(environment.usersServiceUrl + '/users/login', {username, password}, { responseType: 'text' })
      .pipe(
        tap(r => this.handleLoginResponse(r)),
      );
  }

  handleLoginResponse(response: any) {
    let parsedResponse = JSON.parse(response)
    this.jwt = parsedResponse['jwt'];
    this.name = parsedResponse['username'];
    this.statusSource.next('logged-in');
  }

  getJWT() {
    return this.jwt;
  }

  isLoggedIn() {
    return this.jwt != '';
  }

  getName() {
    return this.name != '' ? this.name : '[unknown]';
  }

  logout() {
    this.jwt = '';
    this.statusSource.next('logged-out');
  }

  constructor(private http: HttpClient) {
  }
}
