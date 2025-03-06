import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { shareReplay } from 'rxjs/operators';

@Injectable()
export class AuthService {
  login(username: string, password: string) {
    return this.http
      .post<any>('http://localhost:8090/', {username, password})
      .pipe(shareReplay());
  }

  constructor(private http: HttpClient) {
  }
}
