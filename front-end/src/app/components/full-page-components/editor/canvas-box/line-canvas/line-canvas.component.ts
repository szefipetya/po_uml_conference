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
import { ResourceLoaderService } from '../../services/resource-loader/resource-loader.service';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { Canvas } from 'src/app/components/models/Diagram/Canvas';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { DiagramObject_Scaled } from 'src/app/components/models/DiagramObjects/DiagramObject_Scaled';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { LINE_HEAD } from 'src/app/components/models/line/LINE_HEAD';

@Component({
  selector: 'app-line-canvas',
  templateUrl: './line-canvas.component.html',
  styleUrls: ['./line-canvas.component.scss'],
})
export class LineCanvasComponent implements OnInit, AfterContentInit {
  constructor(private resourceLoader: ResourceLoaderService) {}
  @ViewChild('canvas')
  public canvasDOM: ElementRef<HTMLCanvasElement>;
  private ctx: CanvasRenderingContext2D;
  @Input() lineCanvasModel: LineCanvas;
  @Input() canvasModel: Canvas;
  @Input() class_general: SimpleClass_General;
  @Input() classes: SimpleClass[];
  @Input() lines: Line[];
  lineInstance: Line;
  ngAfterContentInit(): void {
    setTimeout(() => {
      this.init();
    }, 1);
  }
  lineHeads;
  init() {
    this.ctx = this.canvasDOM.nativeElement.getContext('2d');
    console.dir(this.canvasDOM);
    console.dir(this.ctx);
    let ctx = this.ctx;
    this.ctx.fillStyle = 'red';
    this.ctx.fillRect(0, 0, 5, 5);
    this.ctx.lineWidth = 4;
    console.log('drawed');

    this.drawStoredLines();
    this.lineHeads = [
      {
        head: LINE_HEAD.RHOMBUS_FILLED,
        img: this.resourceLoader.getSvgHead(LINE_HEAD.RHOMBUS_FILLED),
      },
      {
        head: LINE_HEAD.RHOMBUS_EMPTY,
        img: this.resourceLoader.getSvgHead(LINE_HEAD.RHOMBUS_EMPTY),
      },
      {
        head: LINE_HEAD.TRI_ARROW_EMPTY,
        img: this.resourceLoader.getSvgHead(LINE_HEAD.TRI_ARROW_EMPTY),
      },
      {
        head: LINE_HEAD.TRI_ARROW_FILLED,
        img: this.resourceLoader.getSvgHead(LINE_HEAD.TRI_ARROW_FILLED),
      },
      {
        head: LINE_HEAD.ARROW,
        img: this.resourceLoader.getSvgHead(LINE_HEAD.ARROW),
      },
      {
        head: LINE_HEAD.NONE,
        img: null,
      },
    ];
    console.log(this.lineHeads);
  }
  getLineHeadImg(h: LINE_HEAD): any {
    return this.lineHeads.filter((l) => l.head == h)[0].img;
  }
  getCenter(obj: DiagramObject): { x: number; y: number } {
    let x = obj.scaledModel.posx_scaled + obj.scaledModel.width_scaled / 2;
    let y = obj.scaledModel.posy_scaled + obj.scaledModel.height_scaled / 2;
    return { x, y };
  }
  getClassById(id: string): SimpleClass {
    return this.classes.find((e) => e.id == id);
  }
  drawBegin(e, type) {
    console.log('DRAWING');
    this.lineInstance = new Line(type);
    let target;
    if (e.target.className != 'd-class') {
      target = e.target.closest('.d-class');
    } else {
      target = e.target;
    }
    if (target) {
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
    let target;
    if (e.target.className != 'd-class') {
      target = e.target.closest('.d-class');
    } else {
      target = e.target;
    }
    console.log('target:', target);
    if (target) {
      this.lineInstance.object_end = this.classes.filter(
        (c) => target.id == c.id
      )[0];
      if (this.lineInstance.object_end != this.lineInstance.object_start) {
        console.log(this.lineInstance.object_start);
        console.log(this.lineInstance.object_end);

        this.lines.push(this.lineInstance.clone());
      }
    } else {
      console.log('err: thats not a valid endpoint');
    }
    this.lineInstance = null;
    this.update();
  }

  getClassGeneralDimension() {
    //width
    return (
      this.class_general.border_scaled * 2 +
      this.class_general.padding_scaled * 2
    );
  }
  drawLine(l: Line) {
    if (l?.object_start && l?.object_end) {
      let p = this.getLineIntersectionWithBox(l, l.object_end.scaledModel);
      let vec: Vector = new Vector();

      let ctx = this.ctx;
      // if (p) ctx.fillRect(p.x - 10, p.y - 10, 20, 20);
      ctx.beginPath();
      let spt = this.getCenter(l.object_start);

      ctx.moveTo(spt.x, spt.y);
      let spt2 = this.getCenter(l.object_end);
      ctx.lineTo(spt2.x, spt2.y);
      ctx.stroke();
      vec.sx = spt.x;
      vec.sy = spt.y;
      vec.ex = spt2.x;
      vec.ey = spt2.y;

      let angle = (vec.ey - vec.sy) / (vec.ex - vec.sx);
      console.log(angle);
      let angleRad = Math.atan(angle);
      let angleDeg = (angleRad * 180) / Math.PI + 90;
      if (spt2.x < spt.x) angleDeg += 180;
      let img = this.getLineHeadImg(l.lineType.endHead);

      if (img)
        //  ctx.drawImage(img, p.x - img.width / 2, p.y - img.height / 2);
        this.drawRotatedImage(img, p.x, p.y, angleDeg);
    }
  }
  TO_RADIANS = Math.PI / 180;
  drawRotatedImage(image, x, y, angle) {
    // save the current co-ordinate system
    // before we screw with it
    let ctx = this.ctx;
    ctx.save();

    // move to the middle of where we want to draw our image
    ctx.translate(x, y);

    // rotate around that point, converting our
    // angle from degrees to radians
    ctx.rotate(angle * this.TO_RADIANS);

    // draw it up and to the left by half the width
    // and height of the image
    ctx.drawImage(image, -(image.width / 2), -(image.height / 2));

    // and restore the co-ords to how they were when we began
    ctx.restore();
  }
  drawWithRotation(angle, fn) {
    let ctx = this.ctx;
    ctx.save(); // save current state
    ctx.rotate(angle); // rotate
    fn();
    ctx.restore(); // restore original states (no rotation etc)
  }

  update() {
    this.clear();
    this.lines.map((l: Line) => {
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
    t_vec.ex =
      box.posx_scaled + box.width_scaled + this.getClassGeneralDimension();

    let l_vec: Vector;
    l_vec = new Vector();
    l_vec.sx = box.posx_scaled;
    l_vec.sy = box.posy_scaled;

    l_vec.ex = l_vec.sx;
    l_vec.ey =
      box.posy_scaled + box.height_scaled + this.getClassGeneralDimension();

    let b_vec: Vector;
    b_vec = new Vector();
    b_vec.sx = box.posx_scaled;
    b_vec.sy =
      box.posy_scaled + box.height_scaled + this.getClassGeneralDimension();

    b_vec.ex = b_vec.sx + box.width_scaled + this.getClassGeneralDimension();
    b_vec.ey = b_vec.sy;

    let r_vec: Vector;
    r_vec = new Vector();
    r_vec.sx =
      box.posx_scaled + box.width_scaled + this.getClassGeneralDimension();
    r_vec.sy = box.posy_scaled;

    r_vec.ex = r_vec.sx;
    r_vec.ey = r_vec.sy + box.height_scaled + this.getClassGeneralDimension();

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
