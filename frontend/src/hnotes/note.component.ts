import { Component, OnInit, AfterViewChecked, Input, Output, EventEmitter, HostListener, ViewChild } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';
import { NewNoteComponent } from './new-note.component';

@Component({
  selector: 'note',
  templateUrl: './note.component.html',
  styleUrl: './note.component.css',
  imports: [ReactiveFormsModule, NewNoteComponent]
})
export class NoteComponent implements OnInit, AfterViewChecked {
  @ViewChild("editNote") editNote!: NewNoteComponent;

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

  handleNote(note: NoteResult) {
    console.log("handling note");
    this.editable = false;
  }

  ngOnInit(): void {
    this.visibleProp = true;
    this.editForm.patchValue({
      title: this.note.title,
      content: this.note.content
    })
  }

  ngAfterViewChecked(): void {
    // if(this.editable) {
    //   this.editNote.editNote(this.note);
    // }
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
