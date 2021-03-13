import { Rect } from '../DiagramObjects/Rect';
import { Clip } from './Clip';
export class Canvas {
  edit_element_id: number;
  edit_classTitle_id: number;
  scale: number;
  posx: number;
  posy: number;
  width: number;
  height: number;
  gridSize: number;
  drawMode: string;
  drawRect: Rect;
  selectedClassIds: string[];
  selectedClass: any;
  clip: Clip;
}
