import { Point } from './Point';
export class BreakPoint {
  edit: boolean;
  point: Point;
  _type = 'BreakPoint';
  index: number;
  constructor(x?, y?) {
    this.point = new Point(x, y);
  }
}
