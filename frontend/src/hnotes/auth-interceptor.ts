import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class AuthenticationInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, handler: HttpHandler) {
    // TODO: obtain JWT value
    var jwt = "";
    const requestWithJWT = request.clone({
      headers: request.headers
        .set('Authorization', `Bearer: ${jwt}`)
    });

    return handler.handle(requestWithJWT);
  }
}
