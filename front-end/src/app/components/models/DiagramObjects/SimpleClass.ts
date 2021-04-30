import { AttributeElement } from './AttributeElement';
import { SimpleClassElementGroup } from './SimpleClassElementGroup';
import { DiagramObject } from './DiagramObject';
import { Element_c } from './Element_c';
import { TitleElement } from './TitleElement';

export class SimpleClass extends DiagramObject {
  groups: SimpleClassElementGroup[];
  titleModel: TitleElement;
}
