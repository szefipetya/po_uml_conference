import { AttributeComponent } from '../../full-page-components/editor/diagram-objects/simple-class/attribute-group/attribute/attribute.component';
import { DynamicSerialObject } from '../common/DynamicSerialObject';
export class Element_c extends DynamicSerialObject {
  name: string;
  edit: boolean;
  viewModel: AttributeComponent;
  extra: any;
}
