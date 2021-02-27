import { InteractivityChecker } from '@angular/cdk/a11y';
import { Line } from '../../../../models/line/Line';
import { Vector } from '../../../../models/line/Vector';
import { LineCanvas } from '../../../../models/line/LineCanvas';
import { clone1 } from '../../Utils/utils';
import {
  AfterContentInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { Canvas } from 'src/app/components/models/canvas';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { DiagramObject_Scaled } from 'src/app/components/models/DiagramObjects/DiagramObject_Scaled';

@Component({
  selector: 'app-line-canvas',
  templateUrl: './line-canvas.component.html',
  styleUrls: ['./line-canvas.component.scss'],
})
export class LineCanvasComponent implements OnInit, AfterContentInit {
  constructor() {}
  @ViewChild('canvas')
  public canvasDOM: ElementRef<HTMLCanvasElement>;
  private ctx: CanvasRenderingContext2D;
  @Input() lineCanvasModel: LineCanvas;
  @Input() canvasModel: Canvas;
  @Input() classes: SimpleClass[];
  lineInstance: Line;
  ngAfterContentInit(): void {
    setTimeout(() => {
      this.init();
    }, 1);
  }
  init() {
    this.ctx = this.canvasDOM.nativeElement.getContext('2d');
    console.dir(this.canvasDOM);
    console.dir(this.ctx);
    let ctx = this.ctx;
    this.ctx.fillStyle = 'red';
    this.ctx.fillRect(0, 0, 5, 5);
    console.log('drawed');
    this.drawStoredLines();
  }
  getCenter(obj: DiagramObject): { x: number; y: number } {
    let x = obj.scaledModel.posx_scaled + obj.scaledModel.width_scaled / 2;
    let y = obj.scaledModel.posy_scaled + obj.scaledModel.height_scaled / 2;
    return { x, y };
  }
  drawBegin(e, type) {
    console.log('DRAWING');
    this.lineInstance = new Line(type);
    let target = e.target;
    if (e.target.className != 'd-class') {
      target = e.target.closest('.d-class');
    }
    if (target.className == 'd-class') {
      this.lineInstance.object_start = this.classes.filter(
        (c) => target.id == c.id
      )[0];
      console.log(this.lineInstance.object_start);
    }
  }
  drawMove(e) {
    if (!this.lineInstance?.object_start) return;

    this.update();
    let ctx = this.ctx;
    ctx.beginPath();
    let spt = this.getCenter(this.lineInstance.object_start);
    ctx.moveTo(spt.x, spt.y);
    let canvasRect = this.canvasDOM.nativeElement.getBoundingClientRect();
    ctx.lineTo(e.clientX - canvasRect.left, e.clientY - canvasRect.top);
    ctx.stroke();
  }
  drawEnd(e) {
    if (!e.target) return;
    let target = e.target;
    if (e.target.className != 'd-class') {
      target = e.target.closest('.d-class');
    }
    console.log('target:', target);
    if (target.className == 'd-class') {
      this.lineInstance.object_end = this.classes.filter(
        (c) => target.id == c.id
      )[0];
      if (this.lineInstance.object_end != this.lineInstance.object_start) {
        console.log(this.lineInstance.object_start);
        console.log(this.lineInstance.object_end);

        this.lineCanvasModel.lines.push(this.lineInstance.clone());
      }
    } else {
      console.log('err: thats not a valid endpoint');
    }
    this.lineInstance = null;
  }
  drawLine(l: Line) {
    if (l?.object_start && l?.object_end) {
      let p = this.getLineIntersectionWithBox(l, l.object_end.scaledModel);

      let ctx = this.ctx;
      if (p) ctx.fillRect(p.x - 20, p.y - 20, 40, 40);
      ctx.beginPath();
      let spt = this.getCenter(l.object_start);
      ctx.moveTo(spt.x, spt.y);
      let spt2 = this.getCenter(l.object_end);
      ctx.lineTo(spt2.x, spt2.y);
      ctx.fillRect(
        l.object_start.scaledModel.posx_scaled - 10,
        l.object_start.scaledModel.posy_scaled - 10,
        l.object_start.scaledModel.width_scaled + 30,
        l.object_start.scaledModel.min_height_scaled + 30
      );
      console.log(l.object_start);
      ctx.stroke();
      let p2 = this.getCenter(l.object_start);
    }
  }
  update() {
    this.clear();
    this.lineCanvasModel.lines.map((l: Line) => {
      this.drawLine(l);
    });
  }

  clear() {
    if (this.ctx)
      this.ctx.clearRect(
        0,
        0,
        this.canvasDOM.nativeElement.width,
        this.canvasDOM.nativeElement.height
      );
  }
  drawStoredLines() {}

  getLineIntersectionWithBox(l1: Line, box: DiagramObject_Scaled): Point {
    //checkLineIntersection(line1StartX, line1StartY, line1EndX, line1EndY, line2StartX, line2StartY, line2EndX, line2EndY) {

    let p1s = this.getCenter(l1.object_start);
    let line1StartX = p1s.x;
    let line1StartY = p1s.y;

    let p1e = this.getCenter(l1.object_end);
    let line1EndX = p1e.x;
    let line1EndY = p1e.y;

    //a 4 oldalra ki kéne számolni

    let t_vec: Vector;
    t_vec = new Vector();
    t_vec.sx = box.posx_scaled;
    t_vec.sy = box.posy_scaled;

    t_vec.ey = t_vec.sy;
    t_vec.ex = box.posx_scaled + box.width_scaled;

    let l_vec: Vector;
    l_vec = new Vector();
    l_vec.sx = box.posx_scaled;
    l_vec.sy = box.posy_scaled;

    l_vec.ex = l_vec.sx;
    l_vec.ey = box.posy_scaled + box.height_scaled;

    let b_vec: Vector;
    b_vec = new Vector();
    b_vec.sx = box.posx_scaled;
    b_vec.sy = box.posy_scaled + box.height_scaled;

    b_vec.ex = b_vec.sx + box.width_scaled;
    b_vec.ey = b_vec.sy;

    let r_vec: Vector;
    r_vec = new Vector();
    r_vec.sx = box.posx_scaled + box.width_scaled;
    r_vec.sy = box.posy_scaled;

    r_vec.ex = r_vec.sx;
    r_vec.ey = r_vec.sy + box.height_scaled;

    // if the lines intersect, the result contains the x and y of the intersection (treating the lines as infinite) and booleans for whether line segment 1 or line segment 2 contain the point

    let vecs: Vector[] = [];
    vecs.push(t_vec, l_vec, b_vec, r_vec);
    // [t_vec, l_vec, b_vec, r_vec];
    let p_return = new Point(0, 0);
    vecs.forEach((v) => {
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
        (v.ey - v.sy) * (line1EndX - line1StartX) -
        (v.ex - v.sx) * (line1EndY - line1StartY);
      if (denominator == 0) {
        return result;
      }
      a = line1StartY - v.sy;
      b = line1StartX - v.sx;
      numerator1 = (v.ex - v.sx) * a - (v.ey - v.sy) * b;
      numerator2 =
        (line1EndX - line1StartX) * a - (line1EndY - line1StartY) * b;
      a = numerator1 / denominator;
      b = numerator2 / denominator;

      // if we cast these lines infinitely in both directions, they intersect here:
      result.x = line1StartX + a * (line1EndX - line1StartX);
      result.y = line1StartY + a * (line1EndY - line1StartY);
      /*
            // it is worth noting that this should be the same as:
            x = line2StartX + (b * (line2EndX - line2StartX));
            y = line2StartX + (b * (line2EndY - line2StartY));
            */
      // if line1 is a segment and line2 is a segment as well, they intersect if:
      if (a > 0 && a < 1 && b > 0 && b < 1) {
        p_return = new Point(result.x, result.y);
      }
    });
    return p_return;
  }

  ngOnInit(): void {}
}
class Point {
  x: number;
  y: number;
  public constructor(x, y) {
    this.x = x;
    this.y = y;
  }
}
