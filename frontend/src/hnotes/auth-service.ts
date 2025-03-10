import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { shareReplay, tap } from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class AuthService {
  private statusSource = new BehaviorSubject('logged-out');
  private jwt = '';

  currentStatus = this.statusSource.asObservable();

  login(username: string, password: string) {
    return this.http
      .post('http://localhost:8090/users/login', {username, password}, { responseType: 'text' })
      .pipe(
        tap(r => this.handleLoginResponse(r)),
      );
  }

  handleLoginResponse(response: any) {
    this.jwt = response;
    this.statusSource.next('logged-in');
  }

  getJWT() {
    return this.jwt;
  }

  isLoggedIn() {
    return this.jwt != '';
  }

  getName() {
    return '[unknown]';
  }

  logout() {
    this.jwt = '';
    this.statusSource.next('logged-out');
  }

  constructor(private http: HttpClient) {
  }
}
