import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginStatusComponent } from './login-status.component';
import { AppService } from './app-service';

@Component({
  selector: 'header-component',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  imports: [LoginStatusComponent]
})
export class HeaderComponent {
  goHome() {
    this.router.navigate(['/']);
  }

  requestRefresh() {
    this.appService.requestRefresh();
  }

  constructor(private router: Router, private appService: AppService) {}
}
