import { AttributeElement } from './AttributeElement';
import { SimpleClassAttributeGroup } from './SimpleClassAttributeGroup';
import { DiagramObject } from './DiagramObject';

export class SimpleClass extends DiagramObject {
  class_type: string;
  groups: SimpleClassAttributeGroup[];
  titleModel: AttributeElement;
}
