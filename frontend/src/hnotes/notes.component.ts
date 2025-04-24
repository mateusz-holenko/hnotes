import { Component, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { NoteComponent } from './note.component';
import { NoteResult } from './note-result';
import { NotesService } from './notes.service';
import { AppService } from './app-service';
import { NoteEditorComponent } from './note-editor.component';
import { LazyWrapperComponent, LazyWrapperModeEnum } from './lazy-wrapper.component';

@Component({
  selector: 'notes',
  imports: [NoteComponent, NoteEditorComponent, LazyWrapperComponent],
  templateUrl: './notes.component.html',
  styleUrl: './notes.component.css'
})
export class NotesComponent implements AfterViewInit, OnDestroy {
  LazyWrapperMode = LazyWrapperModeEnum;

  private refreshSubscription: Subscription | undefined;
  @ViewChild('notes_grid_wrapper') gridWrapper!: LazyWrapperComponent;

  handleDeleteNote(wrapper: LazyWrapperComponent, id: number) {
    console.log('Requested to remove note #' + id);
    wrapper.mode = LazyWrapperModeEnum.Loading;

    this.notesService
      .removeNote(id)
      .subscribe({
        next: () => { wrapper.mode = LazyWrapperModeEnum.Loaded; },
        error: () => {
          wrapper.mode = LazyWrapperModeEnum.Error;
          this.appService.showError('Error while removing a note. Please try again.');
        }
      });
  }

  handleAcceptNote(wrapper: LazyWrapperComponent, note: NoteResult) {
    wrapper.mode = LazyWrapperModeEnum.Loading;

    var operation = (note.id == null)
      ? this.notesService.addNote(note)
      : this.notesService.updateNote(note);

    operation
        .subscribe({
          next: () => {
            wrapper.mode = LazyWrapperModeEnum.Loaded;
          },
          error: () => {
            wrapper.mode = LazyWrapperModeEnum.Error;
            this.appService.showError('Error while adding/updating a note. Please try again.');
          }
        });
  }

  refreshNotes() {
    console.log("refreshing notes");

    this.gridWrapper.mode = LazyWrapperModeEnum.Loading;

    this.notesService
      .prefetchNotes()
      .subscribe({
        next: () => { this.gridWrapper.mode = LazyWrapperModeEnum.Loaded; },
        error: () => {
          this.gridWrapper.mode = LazyWrapperModeEnum.Error;
          this.appService.showError('Error while loading notes. Please try again');
        }
      });
  }

  loadMoreNotes(wrapper: LazyWrapperComponent) {
    console.log("loading more notes");

    wrapper.mode = LazyWrapperModeEnum.Loading;
    this.notesService
      .loadMoreNotes()
      .subscribe({
        next: () => { wrapper.mode = LazyWrapperModeEnum.Loaded; },
        error: () => {
          wrapper.mode = LazyWrapperModeEnum.Error;
          this.appService.showError('Error while loading notes. Please try again')
        }
      });
  }

  ngAfterViewInit() {
    console.log("notes created");
    // we use this hook to make sure 'this.gridWrapper' is initialized
    // since this hook is part of page refresh, we can't do any UI-related operations - hence delaying it to the next loop start
    setTimeout(
      () => { this.refreshSubscription = this.appService.currentRefreshStatus.subscribe(() => { this.refreshNotes() }); },
      0);
  }

  ngOnDestroy() {
    console.log("notes destroyed");
    this.refreshSubscription!.unsubscribe();
  }

  constructor(public notesService: NotesService, private appService: AppService) {
  }
}
