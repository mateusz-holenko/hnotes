import { Injectable, inject } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({providedIn: 'root'})
export class AppService {
  private snackBar = inject(MatSnackBar);
  private refreshSource = new BehaviorSubject('refresh-request');
  currentRefreshStatus = this.refreshSource.asObservable();

  requestRefresh() {
    this.refreshSource.next('refresh-requested');
  }

  showError(text: string) {
    this.snackBar.open(text, 'Close');
  }

  closeError() {
    this.snackBar.dismiss();
  }
}
