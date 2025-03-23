import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NoteComponent } from './note.component';
import { NoteResult } from './note-result';

describe('NoteComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoteComponent],
    }).compileComponents();
  });

  it('should create the component', () => {
    const fixture = TestBed.createComponent(NoteComponent);
    const note = fixture.componentInstance;
    expect(note).toBeTruthy();
  });

  it('should not show buttons at startup', () => {
    const fixture = TestBed.createComponent(NoteComponent);
    const note = fixture.componentInstance;
    const debugElement = fixture.debugElement;
    const buttons = debugElement.queryAll(By.css('button'));

    expect(note.buttonsVisible).toEqual(false);
    expect(buttons.length).toEqual(0);
  });

  it('should show and hide buttons on hover', () => {
    const fixture = TestBed.createComponent(NoteComponent);
    const note = fixture.componentInstance;
    const nativeElement = fixture.nativeElement;
    const debugElement = fixture.debugElement;

    note.note = new NoteResult();

    nativeElement.dispatchEvent(new MouseEvent('mouseover', {
      view: window,
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();
   
    let buttons = debugElement.queryAll(By.css('button'));
    expect(note.buttonsVisible).toEqual(true);
    expect(buttons.length).toEqual(2);

    nativeElement.dispatchEvent(new MouseEvent('mouseout', {
      view: window,
      bubbles: true,
      cancelable: true
    }));
    fixture.detectChanges();
   
    buttons = debugElement.queryAll(By.css('button'));
    expect(note.buttonsVisible).toEqual(false);
    expect(buttons.length).toEqual(0);
  });
});
