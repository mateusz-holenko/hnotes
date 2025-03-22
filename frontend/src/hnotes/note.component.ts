import { Component, OnInit, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { NoteResult } from './note-result';
import { NewNoteComponent } from './new-note.component';

@Component({
  selector: 'note',
  templateUrl: './note.component.html',
  styleUrl: './note.component.css',
  imports: [ReactiveFormsModule, NewNoteComponent]
})
export class NoteComponent implements OnInit {
  @Input({required: true}) note!: NoteResult;
  @Input() editable: boolean = false;

  @Output() deleteNote = new EventEmitter<Number>();
  @Output() acceptNote = new EventEmitter<NoteResult>();

  private editForm = new FormGroup({
    title: new FormControl(''),
    content: new FormControl('')
  })

  buttonsVisible: boolean = false;

  editNote() {
    this.editable = true;
  }

  removeNote() {
    this.deleteNote.emit(this.note.id!);
  }

  ngOnInit(): void {
    this.editForm.patchValue({
      title: this.note.title,
      content: this.note.content
    })
  }

  handleAcceptNote(note: NoteResult) {
    this.acceptNote.emit(note);
  }

  @HostListener('mouseover', ['true'])
  @HostListener('mouseout', ['false'])
  handleMouseOver(isMouseOver: boolean) {
    this.buttonsVisible = isMouseOver;
  }
}
