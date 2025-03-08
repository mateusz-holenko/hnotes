import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { shareReplay, tap } from 'rxjs/operators';

@Injectable({providedIn: 'root'})
export class AuthService {
  jwt = '';

  login(username: string, password: string) {
    return this.http
      .post('http://localhost:8090/users/login', {username, password}, { responseType: 'text' })
      .pipe(
        tap(r => this.handleLoginResponse(r)),
      );
  }

  handleLoginResponse(response: any) {
    this.jwt = response;
  }

  getJWT() {
    return this.jwt;
  }

  logout() {
    this.jwt = '';
  }

  constructor(private http: HttpClient) {
  }
}
