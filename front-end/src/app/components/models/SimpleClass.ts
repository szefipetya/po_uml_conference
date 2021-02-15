import { AttributeElement } from './AttributeElement';
import { SimpleClassAttributeGroup } from './SimpleClassAttributeGroup';
import { SimpleClass_Scaled } from './SimpleClass_Scaled';

export class SimpleClass {
  id: string;
  posx: number;
  posy: number;
  width: number;
  height: number;
  scaledModel: SimpleClass_Scaled;
  min_height: number;
  z: number;
  edit: boolean;
  name: string;
  class_type: string;
  groups: SimpleClassAttributeGroup[];
  titleModel: AttributeElement;
}
