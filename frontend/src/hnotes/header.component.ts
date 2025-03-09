import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoteResult } from './note-result';
import { LoginStatusComponent } from './login-status.component';

@Component({
  selector: 'header-component',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  imports: [LoginStatusComponent]
})
export class HeaderComponent {
}
