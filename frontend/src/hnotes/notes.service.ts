import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoteResult } from './note-result';
import { tap } from 'rxjs/operators';

const pageSize = 15;

@Injectable({providedIn: 'root'})
export class NotesService {
  private fetchedPages = 0;
  private urlBase = 'http://localhost:8080';

  notes:NoteResult[] = [];

  getNotes() : Observable<NoteResult[]>
  {
    return this.http.get<NoteResult[]>(this.urlBase + 'notes');
  }

  prefetchNotes() : Observable<NoteResult[]> {
    // TODO: handle errors
    var observableNotesResult = this.http
      .get<NoteResult[]>(`${this.urlBase}/notes?limit=${pageSize}`)
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
      .get<NoteResult[]>(`${this.urlBase}/notes?limit=${pageSize}&page=${nextPage}`)
      .pipe<NoteResult[]>(
        tap(n => { this.handleNotes('append', n) })
      );
    return observableNotesResult;
  }

  removeNote(id: number)
  {
    return this.http.delete(this.urlBase + '/notes/' + id);
  }

  addNote(n: NoteResult)
  {
    return this.http.post<any>(this.urlBase + '/notes', n);
  }

  updateNote(id: number, n: NoteResult)
  {
    return this.http.put<any>(this.urlBase + '/notes/' + id, n);
  }

  constructor(private http : HttpClient) {}
}
