import { Component, HostListener, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule } from '@angular/forms';
import { NoteResult } from './note-result';

@Component({
  selector: 'note-editor',
  imports: [ReactiveFormsModule],
  templateUrl: './note-editor.component.html',
  styleUrl: './note-editor.component.css'
})
export class NoteEditorComponent implements OnInit {
  @Input() note: NoteResult | undefined;
  @Output() acceptNote = new EventEmitter<NoteResult>();

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
    if(ev.explicitOriginalTarget == null || ev.explicitOriginalTarget.localName != 'note-editor') {
      return;
    }

    this.handleDone();
  }

  handleDone() {
    if(this.note != null) {
      let updatedNote = Object.assign(this.note, this.editForm.value);
      this.acceptNote.emit(updatedNote);
    } else {
      let newNote = Object.assign(new NoteResult(), this.editForm.value);
      if(newNote.title != '' || newNote.content != '') {
        this.acceptNote.emit(newNote);
      }
    }
    this.resetView();
  }

  resetView() {
    this.editForm.patchValue({
      title: '',
      content: ''
    });
    this.compactView = true;
  }
}
