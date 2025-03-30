import { Component, Input } from '@angular/core';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

export enum LazyWrapperModeEnum {
  Uninitialized,
  Loading,
  Loaded,
  Error
}

@Component({
  selector: 'lazy-wrapper',
  templateUrl: './lazy-wrapper.component.html',
  styleUrl: './lazy-wrapper.component.css',
  imports: [MatProgressSpinner]
})
export class LazyWrapperComponent {
  LazyWrapperMode = LazyWrapperModeEnum;

  @Input() mode: LazyWrapperModeEnum = LazyWrapperModeEnum.Uninitialized;
}
