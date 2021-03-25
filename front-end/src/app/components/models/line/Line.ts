import { LineType } from './LineType';
import { SimpleClass } from '../DiagramObjects/SimpleClass';
import { LINE_TYPE } from './LINE_TYPE';
import { DiagramObject } from '../DiagramObjects/DiagramObject';
export class Line {
  id: string;
  lineType: LineType;
  object_start_id: string;
  object_end_id: string;
  object_end: DiagramObject; //ignore in REST
  object_start: DiagramObject; //ignore in REST
  public constructor(lt: LINE_TYPE) {
    this.id = new Date().getMilliseconds().toString();

    this.lineType = new LineType(lt, this.object_start, this.object_end);
  }
  public clone() {
    let l2 = new Line(this.lineType.type);
    l2.object_end_id = this.object_end_id;
    l2.object_start_id = this.object_start_id;
    l2.object_end = this.object_end;
    l2.object_start = this.object_start;
    l2.lineType = this.lineType;
    return l2;
  }
}
