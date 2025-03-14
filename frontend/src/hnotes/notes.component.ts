import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { NoteComponent } from './note.component';
import { LoginComponent } from './login.component';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';
import { NewNoteComponent } from './new-note.component';

@Component({
  selector: 'notes',
  imports: [NoteComponent, NewNoteComponent],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent implements OnInit {
  notes:any[] = [];

  handleRemoveRequest(id: number) {
    console.log('Requested to remove note #' + id);
    this.notesService.removeNote(id).subscribe(
      () => {
        var idx = this.notes.findIndex(n => n.id == id);
        if(idx > -1) {
          this.notes.splice(idx, 1);
        }
      }
    );
  }

  handleAdded(note: NoteResult) {
    this.notes.splice(0, 0, note);
  }

  ngOnInit()
  {
    this.notesService.getNotes().subscribe(notes => { this.notes = notes.sort(x => x.id ?? 0) });
  }

  constructor(private notesService: NotesService, private router: Router) {}
}
