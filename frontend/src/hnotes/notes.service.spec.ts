import { TestBed } from '@angular/core/testing';
import { NotesService } from './notes.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { NoteResult } from './note-result';

describe('NotesService', () => {
  var httpTesting: HttpTestingController;
  var service: NotesService;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        NotesService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    httpTesting = TestBed.inject(HttpTestingController);
    service = TestBed.inject(NotesService);
  });

  afterEach(async () => {
  });

  it('should create service', () => {
    const service = TestBed.inject(NotesService);
    expect(service).toBeTruthy();
  });

  it('should prefetch notes', () => {
    service.prefetchNotes()
      .subscribe({ next: () => {}, error: () => {} });

    httpTesting
      .expectOne({ method: 'GET', url: '/api/notes?limit=15&page=0' })
      .flush(Array.from({length: 2}, () => new NoteResult()));
    
    expect(service.notes).toHaveSize(2);
    httpTesting.verify();
  });

  it('should fetch more', () => {
    service.prefetchNotes()
      .subscribe({ next: () => {}, error: () => {} });
    service.loadMoreNotes()
      .subscribe({ next: () => {}, error: () => {} });

    httpTesting
      .expectOne({method: 'GET', url: '/api/notes?limit=15&page=0'})
      .flush(Array.from({length: 15}, () => new NoteResult()));

    httpTesting
      .expectOne({method: 'GET', url: '/api/notes?limit=15&page=1'})
      .flush(Array.from({length: 15}, () => new NoteResult()));

    expect(service.notes).toHaveSize(30);
    httpTesting.verify();
  });

  it('should remove note', () => {
    service.prefetchNotes()
      .subscribe({ next: () => {}, error: () => {} });
    service.removeNote(0)
      .subscribe({ next: () => {}, error: () => {} });

    httpTesting
      .expectOne({method: 'GET', url: '/api/notes?limit=15&page=0'})
      .flush(Array.from({length: 15}, (_, i) => new NoteResult(i)));

    httpTesting
      .expectOne({method: 'DELETE', url: '/api/notes/0'})
      .flush(null);

    expect(service.notes).toHaveSize(14);
    httpTesting.verify();
  });

  it('should add note', () => {
    let note = new NoteResult(undefined, "Title", "Content");
    service.addNote(note)
      .subscribe({ next: () => {}, error: () => {} });

    httpTesting
      .expectOne({method: 'POST', url: '/api/notes'})
      // only ID of the returned note should be used
      .flush(new NoteResult(0, "Ignored title", "Ignored content"));

    expect(service.notes).toHaveSize(1);
    expect(service.notes[0].id).toEqual(0);
    expect(service.notes[0].title).toEqual("Title");
    expect(service.notes[0].content).toEqual("Content");
    httpTesting.verify();
  });

  it('should update note', () => {
    let note = new NoteResult(undefined, "Title", "Content");
    service.addNote(note)
      .subscribe({ next: () => {}, error: () => {} });
    httpTesting
      .expectOne({method: 'POST', url: '/api/notes'})
      // only ID of the returned note should be used
      .flush(new NoteResult(0, "Ignored title", "Ignored content"));

    note = service.notes[0];
    note.title = "Modified title";
    service.updateNote(note)
      .subscribe({ next: () => {}, error: () => {} });
    httpTesting
      .expectOne({method: 'PUT', url: '/api/notes/0'})
      .flush(null);

    expect(service.notes).toHaveSize(1);
    expect(service.notes[0].id).toEqual(0);
    expect(service.notes[0].title).toEqual("Modified title");
    expect(service.notes[0].content).toEqual("Content");
    httpTesting.verify();
  });

  it('should filter notes', () => {
    service.setFilter('ananas');

    service.prefetchNotes()
      .subscribe({ next: () => {}, error: () => {} });

    httpTesting
      .expectOne({method: 'GET', url: '/api/notes?limit=15&page=0&query=ananas'})
      // only ID of the returned note should be used
      .flush(null);

    httpTesting.verify();
  });

  it('should clear filter', () => {
    service.setFilter('ananas');
    service.setFilter('');

    service.prefetchNotes()
      .subscribe({ next: () => {}, error: () => {} });

    httpTesting
      .expectOne({method: 'GET', url: '/api/notes?limit=15&page=0'})
      // only ID of the returned note should be used
      .flush(null);

    httpTesting.verify();
  });
});
