import { Window_c } from './Window_c';

export class DockableWindow {
  is_docked: boolean;
  dock: {
    id: string;
    DOM: any;
    order: number;
    width: number;
  };
  window: Window_c;
}
