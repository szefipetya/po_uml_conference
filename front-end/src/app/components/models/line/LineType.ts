import { LINE_BODY } from './LINE_BODY';
import { LINE_HEAD } from './LINE_HEAD';
import { LINE_TYPE } from './LINE_TYPE';

export class LineType {
  startHead: LINE_HEAD;
  endHead: LINE_HEAD;
  body: LINE_BODY;
  type: LINE_TYPE;
  public constructor(t: LINE_TYPE) {
    this.body = LINE_BODY.SOLID;
    this.startHead = LINE_HEAD.NONE;
    this.type = t;
    switch (t) {
      case LINE_TYPE.AGGREGATION:
        this.endHead = LINE_HEAD.RHOMBUS_EMPTY;
        break;
      case LINE_TYPE.ASSOCIATION:
        this.endHead = LINE_HEAD.NONE;
        break;
      case LINE_TYPE.COMPOSITION:
        this.endHead = LINE_HEAD.RHOMBUS_FILLED;
        break;
      case LINE_TYPE.DEPENDENCY:
        this.endHead = LINE_HEAD.ARROW;
        this.body = LINE_BODY.DASHED;
        break;
      case LINE_TYPE.DIRECTED_ASSOC:
        this.endHead = LINE_HEAD.ARROW;
        break;
      case LINE_TYPE.GENERALIZATION:
        this.endHead = LINE_HEAD.TRI_ARROW_EMPTY;

        break;
      case LINE_TYPE.INTERFACE:
        this.endHead = LINE_HEAD.TRI_ARROW_EMPTY;
        this.body = LINE_BODY.DASHED;
        break;
      default:
        this.endHead = LINE_HEAD.NONE;
        break;
    }
  }
}
