import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './hnotes/app.config';
import { AppComponent } from './hnotes/app.component';

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
