import { DiagramObjectComponent } from '../../full-page-components/editor/diagram-objects/diagram-object/diagram-object.component';
import { DynamicSerialObject } from '../common/DynamicSerialObject';
import { DiagramObject_Scaled } from './DiagramObject_Scaled';
import { Rect } from './Rect';

export class DiagramObject extends DynamicSerialObject {
  dimensionModel: Rect;

  scaledModel: DiagramObject_Scaled;
  min_height: number;
  z: number;
  name: string;
  viewModel: DiagramObjectComponent; //ignored in rest
  doc: string;
}
