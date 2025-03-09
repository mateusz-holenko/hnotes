import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoteResult } from './note-result';

@Injectable({providedIn: 'root'})
export class NotesService {
  notes:any[] = [];
  url_base = 'http://localhost:8080/';

  getNotes() : Observable<NoteResult[]>
  {
    return this.http.get<NoteResult[]>(this.url_base + 'notes');
  }

  removeNote(id: number)
  {
    return this.http.delete(this.url_base + 'notes/' + id);
  }

  addNote(n: NoteResult)
  {
    return this.http.post<any>(this.url_base + 'notes', n);
  }

  updateNote(id: number, n: NoteResult)
  {
    return this.http.put<any>(this.url_base + 'notes/' + id, n);
  }

  constructor(private http : HttpClient) {}
}
