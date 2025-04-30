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
    this.snackBar.open(text, 'Close', {panelClass: ['error-snackbar']});
  }

  closeError() {
    this.snackBar.dismiss();
  }
}

// TODO: extract to a separate file
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';

@Injectable({providedIn: 'root'})
export class WebSocketService extends RxStomp {
  private snackBar = inject(MatSnackBar);

  enable() {
    console.log('enabling...');
    var config: RxStompConfig = {
      brokerURL: 'ws://localhost:8100/websocket',
    };
    this.configure(config);
    this.activate();

    this.watch('/topic/notes').subscribe((msg) => {
        var o = JSON.parse(msg.body);
        console.log('GOT a MSG: ' + o.content);
        this.snackBar.open('GOT a MSG: ' + o.content, 'Close');
    })

    console.log('ENABLED!');
  }

  constructor() {
    super();
  }
}

