import { Injectable } from '@angular/core';
import { Window_c } from '../../models/windows/Window_c';

@Injectable({
  providedIn: 'root',
})
export class WindowManagerService {
  model;
  constructor() {
    //TODO ITT
    this.model = {
      windows: [{}],
    };
  }
}
