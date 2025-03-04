import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NoteResult } from './note-result'

@Component({
  selector: 'note',
  templateUrl: './note.component.html',
  styleUrl: './note.component.css',
  imports: [ReactiveFormsModule]
})
export class NoteComponent implements OnInit {
  @Input() note: NoteResult = { id: -1, title: '', content: '' }
  visibleProp: boolean = true;

  @Input() editable: boolean = false;
  editForm = new FormGroup({
    title: new FormControl(''),
    content: new FormControl('')
  })

  edit() {
    this.editable = true;
  }

  remove() {
    if(this.note.id == 20) {
      this.http.delete('http://localhost:8080/notes/10')
        .subscribe(
          () => { this.visibleProp = false; },
          error => { this.note.title = 'ERROR!!!!'; })
        
    } else {
      this.http.delete('http://localhost:8080/notes/' + this.note.id) .subscribe(() => { this.visibleProp = false; })
    }
  }

  ngOnInit(): void {
    this.visibleProp = true;
    this.editForm.patchValue({
      title: this.note.title,
      content: this.note.content
    })
  }

  onSubmit() {
    if (this.note.id == -1) {
      this.http.post<any>('http://localhost:8080/notes', this.editForm.value).subscribe(() => { })
    } else {
      this.http.put<any>('http://localhost:8080/notes/' + this.note.id, this.editForm.value).subscribe(() => { })
      this.editable = false;
    }
  }

  constructor(private http : HttpClient) {
  }
}
