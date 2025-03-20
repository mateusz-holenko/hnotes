import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({providedIn: 'root'})
export class AppService {
  private refreshSource = new BehaviorSubject('refresh-request');
  currentRefreshStatus = this.refreshSource.asObservable();

  requestRefresh() {
    this.refreshSource.next('refresh-requested');
  }

  constructor() {
  }
}
