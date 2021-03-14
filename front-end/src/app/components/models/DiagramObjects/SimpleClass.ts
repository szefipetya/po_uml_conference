import { AttributeElement } from './AttributeElement';
import { SimpleClassElementGroup } from './SimpleClassElementGroup';
import { DiagramObject } from './DiagramObject';
import { Element_c } from './Element_c';

export class SimpleClass extends DiagramObject {
  groups: SimpleClassElementGroup[];
  titleModel: Element_c;
}
