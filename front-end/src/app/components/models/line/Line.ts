import { LineType } from './LineType';
import { SimpleClass } from '../DiagramObjects/SimpleClass';
import { LINE_TYPE } from './LINE_TYPE';
export class Line {
  id: string;
  lineType: LineType;
  object_start: SimpleClass;
  object_end: SimpleClass;
  public constructor(lt: LINE_TYPE) {
    this.id = new Date().getTime().toString();
    this.lineType = new LineType(lt);
  }
  public clone() {
    let l2 = new Line(this.lineType.type);
    l2.object_end = this.object_end;
    l2.object_start = this.object_start;
    l2.lineType = this.lineType;
    return l2;
  }
}
