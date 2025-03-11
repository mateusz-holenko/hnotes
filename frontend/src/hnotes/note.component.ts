import { Component, OnInit, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';

@Component({
  selector: 'note',
  templateUrl: './note.component.html',
  styleUrl: './note.component.css',
  imports: [ReactiveFormsModule]
})
export class NoteComponent implements OnInit {
  @Input() note: NoteResult = new NoteResult();
  visibleProp: boolean = true;
  buttonsVisible: boolean = false;

  @Input() editable: boolean = false;
  editForm = new FormGroup({
    title: new FormControl(''),
    content: new FormControl('')
  })

  @Output() removeRequestEvent = new EventEmitter();
  @Output() addedRequestEvent = new EventEmitter<NoteResult>();

  edit() {
    this.editable = true;
  }

  remove() {
    this.removeRequestEvent.emit();
  }

  ngOnInit(): void {
    this.visibleProp = true;
    this.editForm.patchValue({
      title: this.note.title,
      content: this.note.content
    })
  }

  onSubmit() {
    let n = Object.assign(new NoteResult(), this.editForm.value);

    if (this.note.id == null) {
      this.notesService.addNote(n).subscribe(() => {
        this.addedRequestEvent.emit(n);
       });
    } else {
      this.notesService.updateNote(this.note.id, n).subscribe(() => { });
      this.note = n;
      this.editable = false;
    }
  }

  @HostListener('mouseover', ['true'])
  @HostListener('mouseout', ['false'])
  handleMouseOver(isMouseOver: boolean) {
    this.buttonsVisible = isMouseOver;
  }

  constructor(private http : HttpClient, private notesService: NotesService) {
  }
}
