import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoteComponent } from './note.component';
import { LoginComponent } from './login.component';
import { NoteResult } from './note-result';
import { AuthService } from './auth-service';

@Component({
  selector: 'notes',
  imports: [HttpClientModule, NoteComponent],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent implements OnInit {
  notes:any[] = [];
  url_base = 'http://localhost:8080/';

  getNotes() : Observable<NoteResult[]>
  {
    return this.http.get<NoteResult[]>(this.url_base + 'notes');
  }

  logout()
  {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  ngOnInit()
  {
    this.getNotes().subscribe(notes => { this.notes = notes });
  }

  constructor(private http : HttpClient, private authService: AuthService, private router: Router) {}
}
