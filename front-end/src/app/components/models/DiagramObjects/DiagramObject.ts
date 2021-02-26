import { AttributeElement } from './AttributeElement';
import { DiagramObject_Scaled } from './DiagramObject_Scaled';
import { SimpleClass } from './SimpleClass';
import { SimpleClassAttributeGroup } from './SimpleClassAttributeGroup';

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
}
