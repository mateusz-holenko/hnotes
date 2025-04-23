import { TestBed } from '@angular/core/testing';
import { NotesService } from './notes.service';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

describe('NotesService', () => {
  beforeEach(async () => {
    TestBed.configureTestingModule({
      providers: [
        NotesService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
  });

  it('should create service', () => {
    const service = TestBed.inject(NotesService);
    expect(service).toBeTruthy();
  });

  it('should prefetch notes', () => {
    const service = TestBed.inject(NotesService);
    const httpTesting = TestBed.inject(HttpTestingController);

    service.prefetchNotes()
      .subscribe(
        () => {},
        () => {});

    const req = httpTesting.expectOne('/api/notes?limit=15&page=0');
    expect(req.request.method).toBe('GET');
    req.flush([{},{}]);
    // TODO: return actual notes
    
    httpTesting.verify();

    expect(service.notes).toHaveSize(2);
  });
});
