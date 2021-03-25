import { DiagramObjectComponent } from '../../full-page-components/editor/diagram-objects/diagram-object/diagram-object.component';
import { DynamicSerialObject } from '../common/DynamicSerialObject';
import { DiagramObject_Scaled } from './DiagramObject_Scaled';

export class DiagramObject extends DynamicSerialObject {
  posx: number;
  posy: number;
  width: number;
  height: number;
  scaledModel: DiagramObject_Scaled;
  min_height: number;
  z: number;
  edit: boolean;
  name: string;
  viewModel: DiagramObjectComponent; //ignored in rest
}
