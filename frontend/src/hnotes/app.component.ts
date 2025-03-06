import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NoteComponent } from './note.component'
import { LoginComponent } from './login.component'
import { NoteResult } from './note-result'

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HttpClientModule, NoteComponent, LoginComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'houen`s app';
  notes:any[] = [];
  url_base = 'http://localhost:8080/';

  getNotes() : Observable<NoteResult[]>
  {
    return this.http.get<NoteResult[]>(this.url_base + 'notes');
  }

  ngOnInit()
  {
    this.getNotes().subscribe(notes => { this.notes = notes });
  }

  constructor(private http : HttpClient) {}
}
