import { Component, HostListener, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';

@Component({
  selector: 'new-note',
  imports: [ReactiveFormsModule],
  templateUrl: './new-note.component.html',
  styleUrl: './new-note.component.css'
})
export class NewNoteComponent {
  @Output() addedRequestEvent = new EventEmitter<NoteResult>();

  compactView: boolean = true;

  editForm = new FormGroup({
    title: new FormControl(''),
    content: new FormControl('')
  });

  @HostListener('focusin', ['true', '$event'])
  @HostListener('focusout', ['false', '$event'])
  handleFocusChange(isInFocus: boolean, ev: any) {
    if(isInFocus) {
      this.compactView = false;
      return;
    }

    // ignore focusout events from internal div elements
    if(ev.explicitOriginalTarget == null || ev.explicitOriginalTarget.localName != 'new-note') {
      return;
    }

    this.handleDone();
  }

  handleDone() {
    let newNote = Object.assign(new NoteResult(), this.editForm.value);

    if(newNote.title == '' && newNote.content == '') {
      console.log("form empty, ignoring");
      this.resetView();
      return;
    }
    this.notesService.addNote(newNote).subscribe(() => {
      console.log("new note created");
      this.addedRequestEvent.emit(newNote);
    });
    this.resetView();
  }

  resetView() {
    this.editForm.patchValue({
      title: '',
      content: ''
    });
    this.compactView = true;
  }

  constructor(private notesService: NotesService) {}
}
