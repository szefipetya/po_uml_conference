import { LineCanvasComponent } from '../../full-page-components/editor/canvas-box/line-canvas/line-canvas.component';
import { Line } from './Line';
import { LINE_TYPE } from './LINE_TYPE';
export class LineCanvas {
  lines: Line[];
  drawLineType: LINE_TYPE;
  viewModel: LineCanvasComponent;
  width: number;
  height: number;
}
