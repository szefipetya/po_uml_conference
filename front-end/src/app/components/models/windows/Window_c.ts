import { WindowComponent } from '../../windows/windowed-components/window/window.component';

export class Window_c {
  selector: string;
  id: string;
  width: number;
  height: number;
  top: number;
  left: number;
  fixed: boolean;
  head: {
    height: number;
    title: string;
  };
  viewModelInstance: WindowComponent;
  contentViewModelInstance: any;
  defaultVisible: boolean;
}
