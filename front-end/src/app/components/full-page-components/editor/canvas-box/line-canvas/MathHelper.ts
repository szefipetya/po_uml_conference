import { Pair } from "../../../../../utils/utils";
import { DiagramObject_Scaled } from "../../../../models/DiagramObjects/DiagramObject_Scaled";
import { BreakPoint } from "../../../../models/line/BreakPoint";
import { Line } from "../../../../models/line/Line";
import { Point } from "../../../../models/line/Point";
import { Vector } from "../../../../models/line/Vector";
import { LineCanvasComponent } from "./line-canvas.component";
export class MathHelper {
  constructor(lineCanvasComponent: LineCanvasComponent) {
    this.lineCanvasComponent = lineCanvasComponent;
  }
  lineCanvasComponent: LineCanvasComponent;

  getTheLineThatsIntersectingWithCursor(e) {
    let mousePoint = this.lineCanvasComponent.getMousePoint(e);
    let return_p = null;

    let return_line = null;
    let box = new DiagramObject_Scaled();
    box.posx_scaled = mousePoint.x - 2;
    box.posy_scaled = mousePoint.y - 2;
    box.width_scaled = 4;
    box.height_scaled = 4;

    this.lineCanvasComponent.editorService.model.lines.map((l) => {
      let prevPoint = new BreakPoint();
      try {
        prevPoint.point = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(l.object_start_id), this.lineCanvasComponent.getControllerById(l.id));
      } catch (error) {
        console.error(error);
        return;
      }
      if (prevPoint.point) {
        l.breaks_scaled.map((b) => {
          return_p = this.getVectorInterSectionWithBox(
            new Vector(
              prevPoint?.point.x,
              prevPoint?.point.y,
              b.point.x,
              b.point.y
            ),
            box
          );
          let pointBox = new DiagramObject_Scaled();
          pointBox.posx_scaled = b.point.x - 5;
          pointBox.posy_scaled = b.point.y - 5;
          pointBox.width_scaled = 10;
          pointBox.height_scaled = 10;
          if (
            this.getVectorInterSectionWithBox(
              new Vector(
                b.point.x - 10,
                b.point.y - 10,
                b.point.x + 10,
                b.point.y + 10
              ),
              box
            ).x > 0 ||
            this.getVectorInterSectionWithBox(
              new Vector(
                b.point.x + 10,
                b.point.y + 10,
                b.point.x - 10,
                b.point.y - 10
              ),
              box
            ).x > 0
          ) {
            b.edit = true;
            return_line = l;
          } else {
            b.edit = false;
          }
          prevPoint = b;
          if (return_p?.x > 0) {
            return_line = l;
          }
        });
        let endPoint
        try {
          endPoint = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(l.object_end_id), this.lineCanvasComponent.getControllerById(l.id));
        } catch (error) {
          console.error(error);
          return;
        }
        let ret2;
        if (return_p?.x > 0) {
          return_line = l;
          return;
        } else {
          ret2 = this.getVectorInterSectionWithBox(
            new Vector(
              prevPoint.point.x,
              prevPoint.point.y,
              endPoint.x,
              endPoint.y
            ),
            box
          );
          if (ret2?.x > 0) {
            return_line = l;
            return;
          }
        }
      }

    });

    return return_line;
  }




  makeBoxFromCursor(e): DiagramObject_Scaled {
    let mousePoint = this.lineCanvasComponent.getMousePoint(e);
    let return_p = null;

    let return_line = null;
    let box = new DiagramObject_Scaled();
    box.posx_scaled = mousePoint.x - 10;
    box.posy_scaled = mousePoint.y - 10;
    box.width_scaled = 20;
    box.height_scaled = 20;
    return box;
  }
  //marad
  findThe2BreakPointsNearby(
    l: Line,
    e: MouseEvent
  ): Pair<BreakPoint, BreakPoint> {
    let pair = new Pair<BreakPoint, BreakPoint>(null, null);
    let box: DiagramObject_Scaled = this.makeBoxFromCursor(e);
    let prevPoint = new BreakPoint();
    let p1;
    try {
      prevPoint.point = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(l.object_start_id), this.lineCanvasComponent.getControllerById(l.id));

    } catch (error) {
      console.error(error);
      return;
    }
    l.breaks_scaled.map((b) => {
      p1 = this.getVectorInterSectionWithBox(
        new Vector(
          prevPoint?.point.x,
          prevPoint?.point.y,
          b.point.x,
          b.point.y
        ),
        box
      );
      if (p1.x > 0) {
        pair.key = prevPoint;
        pair.value = b;
        return;
      }
      prevPoint = b;
    });
    let endPoint;
    try {
      endPoint = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(l.object_end_id), this.lineCanvasComponent.getControllerById(l.id));
    } catch (error) {
      console.error(error);
      return;
    }

    let p2 = this.getVectorInterSectionWithBox(
      new Vector(prevPoint.point.x, prevPoint.point.y, endPoint.x, endPoint.y),
      box
    );
    if (p2?.x > 0) {
      pair.key = prevPoint;
      pair.value = new BreakPoint(endPoint.x, endPoint.y);
    }
    return pair;
  }

  getBoxInterSectionWithBox(
    box: DiagramObject_Scaled,
    box2: DiagramObject_Scaled
  ): Point {
    let t_vec: Vector;
    t_vec = new Vector();
    t_vec.sx = box.posx_scaled;
    t_vec.sy = box.posy_scaled;

    t_vec.ey = t_vec.sy;
    t_vec.ex =
      box.posx_scaled + box.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let l_vec: Vector;
    l_vec = new Vector();
    l_vec.sx = box.posx_scaled;
    l_vec.sy = box.posy_scaled;

    l_vec.ex = l_vec.sx;
    l_vec.ey =
      box.posy_scaled + box.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let b_vec: Vector;
    b_vec = new Vector();
    b_vec.sx = box.posx_scaled;
    b_vec.sy =
      box.posy_scaled + box.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    b_vec.ex = b_vec.sx + box.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();
    b_vec.ey = b_vec.sy;

    let r_vec: Vector;
    r_vec = new Vector();
    r_vec.sx =
      box.posx_scaled + box.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();
    r_vec.sy = box.posy_scaled;

    r_vec.ex = r_vec.sx;
    r_vec.ey = r_vec.sy + box.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let t2_vec: Vector;
    t2_vec = new Vector();
    t2_vec.sx = box2.posx_scaled;
    t2_vec.sy = box2.posy_scaled;

    t2_vec.ey = t2_vec.sy;
    t2_vec.ex =
      box2.posx_scaled + box2.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let l2_vec: Vector;
    l2_vec = new Vector();
    l2_vec.sx = box2.posx_scaled;
    l2_vec.sy = box2.posy_scaled;

    l2_vec.ex = l2_vec.sx;
    l2_vec.ey =
      box2.posy_scaled + box2.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let b2_vec: Vector;
    b2_vec = new Vector();
    b2_vec.sx = box2.posx_scaled;
    b2_vec.sy =
      box2.posy_scaled + box2.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    b2_vec.ex = b2_vec.sx + box2.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();
    b2_vec.ey = b2_vec.sy;

    let r2_vec: Vector;
    r2_vec = new Vector();
    r2_vec.sx =
      box2.posx_scaled + box2.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();
    r2_vec.sy = box2.posy_scaled;

    r2_vec.ex = r2_vec.sx;
    r2_vec.ey =
      r2_vec.sy + box2.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    // if the lines intersect, the result contains the x and y of the intersection (treating the lines as infinite) and booleans for whether line segment 1 or line segment 2 contain the point

    let vecs: Vector[] = [];
    vecs.push(t_vec, l_vec, b_vec, r_vec);
    let vecs2: Vector[] = [];
    vecs.push(t2_vec, l2_vec, b2_vec, r2_vec);
    // [t_vec, l_vec, b_vec, r_vec];
    let p_return = new Point(0, 0);
    vecs.forEach((v) => {
      vecs2.forEach((v2) => {
        let crossP = this.calcSegmentCross(v2, v);

        if (crossP != null) p_return = crossP;
      });
    });
    return p_return;
  }
  getVectorInterSectionWithBox(
    lineVec: Vector,
    box: DiagramObject_Scaled
  ): Point {
    let t_vec: Vector;
    t_vec = new Vector();
    t_vec.sx = box.posx_scaled;
    t_vec.sy = box.posy_scaled;

    t_vec.ey = t_vec.sy;
    t_vec.ex =
      box.posx_scaled + box.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let l_vec: Vector;
    l_vec = new Vector();
    l_vec.sx = box.posx_scaled;
    l_vec.sy = box.posy_scaled;

    l_vec.ex = l_vec.sx;
    l_vec.ey =
      box.posy_scaled + box.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    let b_vec: Vector;
    b_vec = new Vector();
    b_vec.sx = box.posx_scaled;
    b_vec.sy =
      box.posy_scaled + box.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    b_vec.ex = b_vec.sx + box.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();
    b_vec.ey = b_vec.sy;

    let r_vec: Vector;
    r_vec = new Vector();
    r_vec.sx =
      box.posx_scaled + box.width_scaled + this.lineCanvasComponent.getClassGeneralDimension();
    r_vec.sy = box.posy_scaled;

    r_vec.ex = r_vec.sx;
    r_vec.ey = r_vec.sy + box.height_scaled + this.lineCanvasComponent.getClassGeneralDimension();

    // if the lines intersect, the result contains the x and y of the intersection (treating the lines as infinite) and booleans for whether line segment 1 or line segment 2 contain the point

    let vecs: Vector[] = [];
    vecs.push(t_vec, l_vec, b_vec, r_vec);
    // [t_vec, l_vec, b_vec, r_vec];
    let p_return = new Point(0, 0);
    vecs.forEach((v) => {
      let crossP = this.calcSegmentCross(lineVec, v);
      if (crossP != null) p_return = crossP;
    });
    return p_return;
  }



  calcSegmentCross(v: Vector, lineVec: Vector) {
    var denominator,
      a,
      b,
      numerator1,
      numerator2,
      result: Point = {
        x: null,
        y: null,
      };
    denominator =
      (v.ey - v.sy) * (lineVec.ex - lineVec.sx) -
      (v.ex - v.sx) * (lineVec.ey - lineVec.sy);
    if (denominator == 0) {
      return result;
    }
    a = lineVec.sy - v.sy;
    b = lineVec.sx - v.sx;
    numerator1 = (v.ex - v.sx) * a - (v.ey - v.sy) * b;
    numerator2 = (lineVec.ex - lineVec.sx) * a - (lineVec.ey - lineVec.sy) * b;
    a = numerator1 / denominator;
    b = numerator2 / denominator;

    // if we cast these lines infinitely in both directions, they intersect here:
    result.x = lineVec.sx + a * (lineVec.ex - lineVec.sx);
    result.y = lineVec.sy + a * (lineVec.ey - lineVec.sy);

    // if line1 is a segment and line2 is a segment as well, they intersect if:
    if (a > 0 && a < 1 && b > 0 && b < 1) {

      return new Point(result.x, result.y);
    }

    return null;
  }
  getLineIntersectionWithBox(l1: Line, box: DiagramObject_Scaled): Point {
    //checkLineIntersection(line1StartX, line1StartY, line1EndX, line1EndY, line2StartX, line2StartY, line2EndX, line2EndY) {
    let lineVec = new Vector();
    try {
      let p1s = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(l1.object_start_id), this.lineCanvasComponent.getControllerById(l1.id));
      let line1StartX = p1s.x;
      let line1StartY = p1s.y;

      let p1e = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(l1.object_end_id), this.lineCanvasComponent.getControllerById(l1.id));
      let line1EndX = p1e.x;
      let line1EndY = p1e.y;

      lineVec.sx = line1StartX;
      lineVec.ex = line1EndX;
      lineVec.sy = line1StartY;
      lineVec.ey = line1EndY;
    } catch (error) {
      console.error(error);
      console.log(error);
      return null;
    }



    return this.getVectorInterSectionWithBox(lineVec, box);
    //a 4 oldalra ki kéne számolni
  }

}
