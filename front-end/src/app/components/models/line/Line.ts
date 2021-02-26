import { LineType } from './LineType';
import { SimpleClass } from '../DiagramObjects/SimpleClass';
import { LINE_TYPE } from './LINE_TYPE';
export class Line {
  lineType: LineType;
  object_start: SimpleClass;
  object_end: SimpleClass;
  public constructor(lt: LINE_TYPE) {
    this.lineType = new LineType(lt);
  }
}
