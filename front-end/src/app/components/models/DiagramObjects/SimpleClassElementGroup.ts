import { DynamicSerialObject } from '../common/DynamicSerialObject';
import { AttributeElement } from './AttributeElement';
import { GROUP_SYNTAX } from './GROUP_SYNTAX';

export class SimpleClassElementGroup extends DynamicSerialObject {
  group_name: string;
  group_syntax: GROUP_SYNTAX;
  attributes: AttributeElement[];
}
