import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthService } from './auth-service';

export class AuthenticationInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, handler: HttpHandler) {
    var jwt = this.authService.getJWT();
    const requestWithJWT = request.clone({
      headers: request.headers
        .set('Authorization', `Bearer: ${jwt}`)
    });

    return handler.handle(requestWithJWT);
  }

  constructor(private authService: AuthService) { }

}
