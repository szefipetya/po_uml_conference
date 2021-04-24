import { LineType } from './LineType';
import { Point } from './Point';
import { SimpleClass } from '../DiagramObjects/SimpleClass';
import { LINE_TYPE } from './LINE_TYPE';
import { BreakPoint } from './BreakPoint';
import { DiagramObject } from '../DiagramObjects/DiagramObject';
import { DynamicSerialObject } from '../common/DynamicSerialObject';
import { uniqId } from '../../../utils/utils';
export class Line extends DynamicSerialObject {
  static sortBreakPoints(l: Line) {
    l.breaks = l.breaks.sort((a, b) => a.index - b.index);
  }
  id: string;
  lineType: LineType;
  object_start_id: string;
  object_end_id: string;
  // object_end: DiagramObject; //ignore in REST
  // object_start: DiagramObject; //ignore in REST
  public constructor(lt: LINE_TYPE) {
    super();
    this.id = uniqId();
    this._type = 'Line';
    this.lineType = new LineType(lt);
  }
  public static clone(l): Line {
    let l2 = new Line(l.lineType.type);
    l2.object_end_id = l.object_end_id;
    l2.object_start_id = l.object_start_id;
    //  l2.object_end = l.object_end;
    //   l2.object_start = l.object_start;
    l2.lineType = l.lineType;
    return l2;
  }
  breaks: BreakPoint[] = [];
  breaks_scaled: BreakPoint[] = [];

  static downScaleBreakPoints(l: Line, scale) {
    l.breaks = [];
    l.breaks_scaled.map((b, i) => {
      let b2 = new BreakPoint(b.point.x / scale, b.point.y / scale);
      b2.index = b.index;
      l.breaks.push(b2);
    })
  }
  static upScaleBreakPoints(l: Line, scale) {
    l.breaks_scaled = [];
    l.breaks.map((b, i) => {
      let b2 = new BreakPoint(b.point.x * scale, b.point.y * scale);
      b2.index = b.index;
      l.breaks_scaled.push(new BreakPoint(b.point.x * scale, b.point.y * scale));
    })
  }
  gerBreaks(): BreakPoint[] {
    return this.breaks;
  }
  gerBreaks_scaled(): BreakPoint[] {
    return this.breaks_scaled;
  }
}
