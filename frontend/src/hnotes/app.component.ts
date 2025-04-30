import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header.component';
import { AppService, WebSocketService } from './app-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  constructor(appService : AppService, wsService: WebSocketService, private router: Router) {
    wsService.enable();
    this.router.events.subscribe((event: any) => {
      if (event instanceof NavigationEnd) {
        appService.closeError();
      }
    });
  }
}
