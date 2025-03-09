import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';
import { NotesComponent } from './notes.component';
import { HelloComponent } from './hello.component';
import { AuthGuardService } from './auth-guard-service';

export const routes: Routes = [
  { path: '', component: HelloComponent },
  { path: 'login', component: LoginComponent },
  { path: 'notes', component: NotesComponent, canActivate: [AuthGuardService] }
];
