import { Component, OnInit, Input, Output, EventEmitter, HostListener } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { NoteResult } from './note-result';
import { NoteEditorComponent } from './note-editor.component';

enum NoteComponentMode {
  ReadOnly,
  Editable
}

@Component({
  selector: 'note',
  templateUrl: './note.component.html',
  styleUrl: './note.component.css',
  imports: [ReactiveFormsModule, NoteEditorComponent]
})
export class NoteComponent implements OnInit {
  NoteComponentModeType = NoteComponentMode;

  @Input({required: true}) note!: NoteResult;
  @Input() mode: NoteComponentMode = NoteComponentMode.ReadOnly;

  @Output() deleteNote = new EventEmitter<number>();
  @Output() acceptNote = new EventEmitter<NoteResult>();

  private editForm = new FormGroup({
    title: new FormControl(''),
    content: new FormControl('')
  })

  buttonsVisible: boolean = false;

  editNote() {
    this.mode = NoteComponentMode.Editable;
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
    this.mode = NoteComponentMode.ReadOnly;
  }

  @HostListener('mouseover', ['true'])
  @HostListener('mouseout', ['false'])
  handleMouseOver(isMouseOver: boolean) {
    this.buttonsVisible = isMouseOver;
  }
}
