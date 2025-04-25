import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { LoginStatusComponent } from './login-status.component';
import { provideHttpClient } from '@angular/common/http';

describe('LoginStatusComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginStatusComponent],
      providers: [
        provideHttpClient()
      ]
    }).compileComponents();
  });

  it('should create the component', () => {
    const fixture = TestBed.createComponent(LoginStatusComponent);
    const loginStatus = fixture.componentInstance;
    expect(loginStatus).toBeTruthy();
  });

  it('should show link when not logged in', () => {
    const fixture = TestBed.createComponent(LoginStatusComponent);
    const loginStatus = fixture.componentInstance;
    const debugElement = fixture.debugElement;

    fixture.detectChanges();
    const links = debugElement.queryAll(By.css('a'));

    expect(loginStatus.loggedIn).toEqual(false);
    expect(links.length).toEqual(1);
    expect(links[0].nativeElement.text).toEqual('Log in!');
  });

  it('should show username when logged in', () => {
    const fixture = TestBed.createComponent(LoginStatusComponent);
    const loginStatus = fixture.componentInstance;
    const debugElement = fixture.debugElement;

    loginStatus.loggedIn = true;
    loginStatus.name = 'User1';

    fixture.detectChanges();
    const links = debugElement.queryAll(By.css('a'));
    const divs = debugElement.queryAll(By.css('div'));

    expect(links.length).toEqual(1);
    expect(links[0].nativeElement.text).toEqual('log out');

    console.log(divs[0].nativeElement)
    expect(divs.length).toEqual(1);
    expect(divs[0].nativeElement.textContent).toEqual('Logged as User1, log out');
  });
});
