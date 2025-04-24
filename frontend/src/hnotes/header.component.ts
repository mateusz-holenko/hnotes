import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginStatusComponent } from './login-status.component';
import { AppService } from './app-service';
import { NotesService } from './notes.service';

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

  onSearchChange(pattern: any) {
    this.notesService.setFilter(pattern.value);
  }

  constructor(private router: Router, private appService: AppService, private notesService: NotesService) {}
}
