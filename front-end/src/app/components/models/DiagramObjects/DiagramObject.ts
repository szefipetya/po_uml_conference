import { DiagramObjectComponent } from '../../full-page-components/editor/diagram-objects/diagram-object/diagram-object.component';
import { AttributeElement } from './AttributeElement';
import { DiagramObject_Scaled } from './DiagramObject_Scaled';
import { SimpleClass } from './SimpleClass';
import { SimpleClassElementGroup } from './SimpleClassElementGroup';
import { SessionState } from '../socket/SessionState';
export class DiagramObject {
  id: string;
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
  _type: string;
  sessionState: SessionState;
}
