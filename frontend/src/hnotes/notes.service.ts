import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoteResult } from './note-result';
import { tap } from 'rxjs/operators';
import { environment } from '../environments/environment';

const pageSize = 15;

@Injectable({providedIn: 'root'})
export class NotesService {
  private fetchedPages = 0;
  // TODO: add timetouts

  notes:NoteResult[] = [];

  private getFullUrl(path: string) {
    return environment.notesServiceUrl + path;
  }

  private getFetchUrl(page: number) {
      return this.getFullUrl(`/notes?limit=${pageSize}&page=${page}`);
  }

  prefetchNotes() : Observable<NoteResult[]> {
    // TODO: handle errors
    var observableNotesResult = this.http
      .get<NoteResult[]>(this.getFetchUrl(0))
      .pipe(
        tap(notes => this.handleNotes('new', notes))
      );
    return observableNotesResult;
  }

  private handleNotes(kind: string, notes: NoteResult[]) {
    if(kind == 'new') {
      this.fetchedPages = 1;
      this.notes = notes;
    } else if(kind == 'append') {
      this.fetchedPages += 1;
      this.notes = [...this.notes, ...notes];
    }
  }

  loadMoreNotes() : Observable<NoteResult[]> {
    // TODO: race condition!
    var nextPage = this.fetchedPages + 1;
    var observableNotesResult = this.http
      .get<NoteResult[]>(this.getFetchUrl(nextPage))
      .pipe<NoteResult[]>(
        tap(n => { this.handleNotes('append', n) })
      );
    return observableNotesResult;
  }

  removeNote(id: number) {
    return this.http
      .delete(this.getFullUrl(`/notes/${id}`))
      .pipe(
        tap(() => { this.handleNoteRemoved(id); })
      );
  }

  private handleNoteRemoved(id: Number) {
    var idx = this.notes.findIndex(n => n.id == id);
    if(idx > -1) {
      this.notes.splice(idx, 1);
    }
  }

  addNote(n: NoteResult) {
    return this.http
      .post<any>(this.getFullUrl('/notes'), n)
      .pipe(
        tap(r => {
          n.id = r.id;
          this.notes.splice(0, 0, n);
        })
      );
  }

  updateNote(id: number, n: NoteResult) {
    return this.http
      .put<any>(this.getFullUrl(`/notes/${id}`), n)
      .pipe(
        tap(() => {
          // move the edited note to front
          this.handleNoteRemoved(id);
          this.notes.splice(0, 0, n);
        })
      );
  }

  constructor(private http : HttpClient) {}
}
