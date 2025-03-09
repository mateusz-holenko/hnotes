import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { NoteComponent } from './note.component';
import { LoginComponent } from './login.component';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';
import { AuthService } from './auth-service';

@Component({
  selector: 'notes',
  imports: [NoteComponent],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent implements OnInit {
  notes:any[] = [];

  logout()
  {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  ngOnInit()
  {
    this.notesService.getNotes().subscribe(notes => { this.notes = notes });
  }

  constructor(private authService: AuthService, private notesService: NotesService, private router: Router) {}
}
