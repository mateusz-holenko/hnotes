import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NoteComponent } from './note.component';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';
import { AppService } from './app-service';
import { NewNoteComponent } from './new-note.component';

@Component({
  selector: 'notes',
  imports: [NoteComponent, NewNoteComponent],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent implements OnInit, OnDestroy {
  private refreshSubscription: Subscription | undefined;

  handleDeleteNote(id: number) {
    console.log('Requested to remove note #' + id);
    this.notesService
      .removeNote(id)
      .subscribe();
  }

  handleAcceptNote(note: NoteResult) {
    if(note.id == null) {
      this.notesService
        .addNote(note)
        .subscribe();
    } else {
      this.notesService
        .updateNote(note.id, note)
        .subscribe();
    }
  }

  refreshNotes() {
    console.log("refreshing notes");
    this.notesService
      .prefetchNotes()
      .subscribe();
  }

  loadMoreNotes() {
    console.log("loading more notes");
    this.notesService
      .loadMoreNotes()
      .subscribe();
  }

  ngOnInit() {
    console.log("notes created");
    this.refreshSubscription = this.appService.currentRefreshStatus.subscribe(() => { this.refreshNotes() });
  }

  ngOnDestroy() {
    console.log("notes destroyed");
    this.refreshSubscription!.unsubscribe();
  }

  constructor(public notesService: NotesService, private appService: AppService) {
  }
}
