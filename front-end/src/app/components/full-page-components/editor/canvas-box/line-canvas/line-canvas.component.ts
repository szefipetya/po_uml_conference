import { InteractivityChecker } from '@angular/cdk/a11y';
import { Line } from '../../../../models/line/Line';
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
} from '@angular/core';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { Canvas } from 'src/app/components/models/canvas';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';

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
    this.clear();
    let ctx = this.ctx;
    ctx.beginPath();
    let spt = this.getCenter(this.lineInstance.object_start);
    ctx.moveTo(spt.x, spt.y);
    let canvasRect = this.canvasDOM.nativeElement.getBoundingClientRect();
    ctx.lineTo(e.clientX - canvasRect.left, e.clientY - canvasRect.top);
    ctx.stroke();
  }
  drawEnd(e) {
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

        this.lineCanvasModel.lines.push(
          clone1(this.lineInstance, this.lineInstance.lineType.type)
        );
      }
    } else {
      console.log('err: thats not a valid endpoint');
    }
    this.lineInstance = null;
  }
  drawLine(l: Line) {
    if (this.lineInstance?.object_start && this.lineInstance?.object_end) {
      let ctx = this.ctx;
      ctx.beginPath();
      let spt = this.getCenter(this.lineInstance.object_start);
      ctx.moveTo(spt.x, spt.y);
      let spt2 = this.getCenter(this.lineInstance.object_end);
      ctx.lineTo(spt2.x, spt2.y);
      ctx.stroke();
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

  ngOnInit(): void {}
}
