import { Component, HostListener, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';

@Component({
  selector: 'new-note',
  imports: [ReactiveFormsModule],
  templateUrl: './new-note.component.html',
  styleUrl: './new-note.component.css'
})
export class NewNoteComponent implements OnInit {
  @Input() note: NoteResult | undefined;
  @Output() addedRequestEvent = new EventEmitter<NoteResult>();

  compactView: boolean = true;

  ngOnInit(): void {
    if(this.note != null) {
      this.editForm.patchValue({
        title: this.note!.title,
        content: this.note!.content
      });
      this.compactView = false;
    }
  }

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
    if(this.note != null) {
      let currentNote = Object.assign(this.note, this.editForm.value);
      if(currentNote.title == '' && currentNote.content == '') {
        console.log("form empty, ignoring");
        this.resetView();
        return;
      }
      this.notesService.updateNote(currentNote.id!, currentNote).subscribe(() => {
        console.log("note edited");
        this.addedRequestEvent.emit(currentNote);
      });
      this.resetView();
    } else {
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
