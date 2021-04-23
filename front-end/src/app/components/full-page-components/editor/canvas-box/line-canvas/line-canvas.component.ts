import { InteractivityChecker } from '@angular/cdk/a11y';
import { Line } from '../../../../models/line/Line';
import { Vector } from '../../../../models/line/Vector';
import { LineCanvas } from '../../../../models/line/LineCanvas';
import { clone1, Pair } from '../../../../../utils/utils';
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
import { Point } from 'src/app/components/models/line/Point';
import { LineController } from 'src/app/components/models/line/LineController';
import { BreakPoint } from 'src/app/components/models/line/BreakPoint';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { EditorAction } from 'src/app/components/models/socket/EditorAction';
import { uniqId } from 'src/app/utils/utils';
import { ACTION_TYPE } from 'src/app/components/models/socket/ACTION_TYPE';
import { SessionInteractiveContainer } from 'src/app/components/models/socket/interface/SessionInteractiveContainer';
import { DynamicSerialObject } from 'src/app/components/models/common/DynamicSerialObject';
import { CallbackItem } from 'src/app/components/models/socket/interface/CallbackItem';
import { SessionState } from 'src/app/components/models/socket/SessionState';
import { CommonService, MSG_TYPE } from '../../services/common/common.service';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { TARGET_TYPE } from 'src/app/components/models/socket/response/TARGET_TYPE';

@Component({
  selector: 'app-line-canvas',
  templateUrl: './line-canvas.component.html',
  styleUrls: ['./line-canvas.component.scss'],
})
export class LineCanvasComponent
  implements OnInit, AfterContentInit, SessionInteractiveContainer {
  deleteController(cont: LineController) {
    this.editorService.model.lines = this.editorService.model.lines.filter((l) => l.id != cont.getId());

    this.lineControllers = this.lineControllers.filter(
      (lc) =>
        lc.getId() != cont.getId()
    );
    console.log('controllers', this.lineControllers)

  }
  LINE_LOCKED_STROKE: '#f00'
  constructor(
    private resourceLoader: ResourceLoaderService,
    public editorService: GlobalEditorService,
    private socket: EditorSocketControllerService,
    private commonService: CommonService
  ) { }
  createItem(model: DynamicSerialObject, extra?: any) {
    this.createLineWithControllerLocally(model as Line);
  }
  hasItem(target_id: string) {
    throw new Error('Method not implemented.');
  }
  restoreItem(item_id: string, model: DynamicSerialObject) {
    throw new Error('Method not implemented.');
  }
  deleteItem(item_id: string) {
    // this.editorService.model.lines = this.editorService.model.lines.filter((l) => l.id != item_id);
    this.lineControllers.map((lc) => { if (lc.getId() == item_id) lc.deleteSelfFromParent() });
  }
  msgPopup(msg: string) {
    throw new Error('Method not implemented.');
  }
  getId(): string {
    return GlobalEditorService.L_ROOT_ID;
  }
  updateState(state: SessionState, m?: any): void {
    throw new Error('Method not implemented.');
  }
  editBegin() {
    throw new Error('Method not implemented.');
  }
  editEnd() {
    throw new Error('Method not implemented.');
  }
  updateModel(model: any, action_id: string, msg?: string) {
    throw new Error('Method not implemented.');
  }
  restoreModel(model: any, action_id: string, msg?: string) {
    throw new Error('Method not implemented.');
  }
  deleteSelfFromParent() {
    throw new Error('Method not implemented.');
  }
  log(msg: string, type: MSG_TYPE) {
    throw new Error('Method not implemented.');
  }
  sessionState: SessionState;
  callback_queue: CallbackItem[];

  @ViewChild('canvas')
  public canvasDOM: ElementRef<HTMLCanvasElement>;
  public ctx: CanvasRenderingContext2D;
  @Input() lineCanvasModel: LineCanvas;
  @Input() canvasModel: Canvas;
  @Input() class_general: SimpleClass_General;
  // @Input() dgObjects: DiagramObject[];
  // @Input() lines: Line[]; //does not work because at init, this will be null
  lineControllers: LineController[] = [];
  lineInstance: Line;
  ngAfterContentInit(): void {
    setTimeout(() => {
      this.init();
    }, 1);
  }
  lineHeads;
  LINE_WIDTH = 3;
  LINE_SELECTED_STROKE = 'orange';
  LINE_DEFAULT_STROKE = 'black';
  init() {
    this.ctx = this.canvasDOM.nativeElement.getContext('2d');
    this.ctx.canvas.onload = this.update;
    let ctx = this.ctx;
    this.ctx.fillStyle = 'red';
    this.ctx.fillRect(0, 0, 5, 5);
    this.ctx.lineWidth = this.LINE_WIDTH;

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
    this.update();
  }
  getLineHeadImg(h: LINE_HEAD): any {
    return this.lineHeads.filter((l) => l.head == h)[0].img;
  }

  //may throw error
  getCenter(obj: DiagramObject, caster: LineController): { x: number; y: number } {
    let x, y;
    try {
      x = obj.scaledModel.posx_scaled + obj.scaledModel.width_scaled / 2;
      y = obj.scaledModel.posy_scaled + obj.scaledModel.height_scaled / 2;
    } catch (e) {
      console.error('line endpoints did not found');
      caster?.deleteSelfFromParent();
      throw new Error("end_object_not_found")
      return null;
    }

    return { x, y };
  }
  getDgObjectById(id: string): DiagramObject {
    return this.editorService.model.dgObjects.find((e) => e.id == id);
  }
  drawBegin(e, type) {
    this.lineInstance = new Line(type);
    let target;
    if (e.target.className != 'd-class') {
      target = e.target.closest('.d-class');
    } else {
      target = e.target;
    }
    if (target) {
      this.lineInstance.object_start_id = this.editorService.model.dgObjects.filter(
        (c) => target.id == c.id
      )[0].id;
    }
  }
  drawMove(e) {
    if (!this.lineInstance?.object_start_id) return;

    this.update();
    let ctx = this.ctx;
    ctx.beginPath();
    let spt
    try {


      spt = this.getCenter(this.getDgObjectById(this.lineInstance.object_start_id), this.getControllerById(this.lineInstance.id));
    } catch (error) {
      console.error(error);
      return;
    }
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

    if (target) {
      this.lineInstance.object_end_id = this.editorService.model.dgObjects.filter(
        (c) => target.id == c.id
      )[0].id;
      if (this.lineInstance.object_end_id != this.lineInstance.object_start_id) {



        this.createLineWithControllerLocally(this.lineInstance);
        this.lineInit(this.lineInstance);
        this.sendLineCreated(this.lineInstance);
      }
    } else {
      console.log('err: thats not a valid endpoint');
    }
    this.lineInstance = null;
    this.update();
  }


  sendLineCreated(l: Line) {
    let action: EditorAction = new EditorAction(l.id, 'Line', GlobalEditorService.L_ROOT_ID);
    action.action = ACTION_TYPE.CREATE;
    action.extra = { create_method: 'individual' };
    let save = { vm: l.viewModel };
    l.viewModel = null;
    action.json = JSON.stringify(l);
    l.viewModel = save.vm;
    this.socket.send(action);
  }
  lineInit(l) {
    Line.upScaleBreakPoints(l, this.editorService.clientModel.canvas.scale);

  }
  createLineWithControllerLocally(l: Line) {

    this.lineInit(l);
    this.editorService.model.lines.push(l);
    let lc = new LineController(this.socket, this.commonService, this.editorService);
    lc.model = l;
    lc.lineCanvasComponent = this;
    lc.init_register(TARGET_TYPE.ITEM);
    this.lineControllers.push(lc);
  }
  getClassGeneralDimension() {
    //width
    return (
      this.class_general.border_scaled * 2 +
      this.class_general.padding_scaled * 2
    );
  }
  zoom(newScale) {
    this.lineControllers.map(lc => {
      Line.upScaleBreakPoints(lc.model, newScale);
    })
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
    this.lineControllers.map((l) => {
      l.drawLine();
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
  drawStoredLines() { }
  onMouseDown(e) {

    if (
      this.selectedLine &&
      this.selectedLine == this.getTheLineThatsIntersectingWithCursor(e)
    ) {
      if (!this.getControllerById(this.selectedLine.id).isAccessible()) return
      this.transformingLine = this.selectedLine;
      this.transformingPoint = this.getMousePoint(e);

      let bp = new BreakPoint();
      bp.point = this.transformingPoint;
      let found = false;
      this.transformingLine.breaks_scaled.forEach((l) => {
        if (l.edit) {
          found = true;
          this.transformingPoint = l.point;
        }
      });
      if (!found) {
        let pair = this.findThe2BreakPointsNearby(this.transformingLine, e);
        //thepairs first thing is the start object itself
        let index = this.transformingLine.breaks_scaled.length;
        try {


          if (
            pair.key?.point.x ==
            this.getCenter(this.getDgObjectById(this.transformingLine.object_start_id), this.getControllerById(this.transformingLine.id)).x &&
            pair.key?.point.y ==
            this.getCenter(this.getDgObjectById(this.transformingLine.object_start_id), this.getControllerById(this.transformingLine.id)).y
          ) {
            this.selectedLine.breaks_scaled.splice(0, 0, bp);
          } else {
            index = this.transformingLine.breaks_scaled.findIndex(
              (k) => k == pair.key
            );

            if (index == -1) index = this.transformingLine.breaks_scaled.length;
            bp.index = index + 1;
            this.selectedLine.breaks_scaled.splice(index + 1, 0, bp);
            this.transformingPoint = bp.point;
          }
          //push the new point inside the normal breakpoints array
        } catch (error) {
          console.error(error);
          return;
        }
        let downScaledBp = new BreakPoint(
          bp.point.x * this.editorService.clientModel.canvas.scale,
          bp.point.y * this.editorService.clientModel.canvas.scale
        );
        this.selectedLine.breaks.splice(index + 1, 0, downScaledBp);
      }
    }
  }
  transformingPoint: Point;
  transformingLine: Line;
  onMouseMove(e) {
    if (this.transformingLine) {
      this.transformingPoint.x = this.getMousePoint(e).x;
      this.transformingPoint.y = this.getMousePoint(e).y;
      this.update();
    }
  }
  onMouseUp(e) {

    if (this.selectedLine)
      this.getControllerById(this.selectedLine.id)?.editEnd();
    this.update();
    this.transformingLine = null;
    this.transformingPoint = null;
  }

  isTransformingInprogress(): boolean {
    return this.transformingLine != null;
  }

  getControllerById(id: string) {
    return this.lineControllers.find(c => c.getId() == id);
  }
  onKeyPress(e) {
    if (e.key?.match('Delete') || e.keyCode == 46) {
      if (this.selectedLine) {
        this.getControllerById(this.selectedLine.id).deleteAsync(this.getId());
      }
    }
  }
  onClick(e: MouseEvent) {
    let newselected = this.getTheLineThatsIntersectingWithCursor(e);
    if (newselected) {//new selection,

      this.getControllerById(newselected.id).editBegin();

    }
    this.selectedLine = newselected;
    this.update();
  }
  getTheLineThatsIntersectingWithCursor(e) {
    let mousePoint = this.getMousePoint(e);
    let return_p = null;

    let return_line = null;
    let box = new DiagramObject_Scaled();
    box.posx_scaled = mousePoint.x - 2;
    box.posy_scaled = mousePoint.y - 2;
    box.width_scaled = 4;
    box.height_scaled = 4;

    this.editorService.model.lines.map((l) => {
      let prevPoint = new BreakPoint();
      try {


        prevPoint.point = this.getCenter(this.getDgObjectById(l.object_start_id), this.getControllerById(l.id));
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
          endPoint = this.getCenter(this.getDgObjectById(l.object_end_id), this.getControllerById(l.id));
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
  selectedLine: Line;
  isLineSelected(): boolean {
    return this.selectedLine != null;
  }


  strike(stroke_style: string, width: number, p1: Point, p2: Point) {
    let ctx = this.ctx;
    ctx.lineWidth = width;
    ctx.strokeStyle = stroke_style;
    ctx.moveTo(p1.x, p1.y);
    ctx.lineTo(p2.x, p2.y);
    ctx.stroke();
  }

  makeBoxFromCursor(e): DiagramObject_Scaled {
    let mousePoint = this.getMousePoint(e);
    let return_p = null;

    let return_line = null;
    let box = new DiagramObject_Scaled();
    box.posx_scaled = mousePoint.x - 10;
    box.posy_scaled = mousePoint.y - 10;
    box.width_scaled = 20;
    box.height_scaled = 20;
    return box;
  }

  findThe2BreakPointsNearby(
    l: Line,
    e: MouseEvent
  ): Pair<BreakPoint, BreakPoint> {
    let pair = new Pair<BreakPoint, BreakPoint>(null, null);
    let box: DiagramObject_Scaled = this.makeBoxFromCursor(e);
    let prevPoint = new BreakPoint();
    let p1;
    try {
      prevPoint.point = this.getCenter(this.getDgObjectById(l.object_start_id), this.getControllerById(l.id));

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
      endPoint = this.getCenter(this.getDgObjectById(l.object_end_id), this.getControllerById(l.id));
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

    let t2_vec: Vector;
    t2_vec = new Vector();
    t2_vec.sx = box2.posx_scaled;
    t2_vec.sy = box2.posy_scaled;

    t2_vec.ey = t2_vec.sy;
    t2_vec.ex =
      box2.posx_scaled + box2.width_scaled + this.getClassGeneralDimension();

    let l2_vec: Vector;
    l2_vec = new Vector();
    l2_vec.sx = box2.posx_scaled;
    l2_vec.sy = box2.posy_scaled;

    l2_vec.ex = l2_vec.sx;
    l2_vec.ey =
      box2.posy_scaled + box2.height_scaled + this.getClassGeneralDimension();

    let b2_vec: Vector;
    b2_vec = new Vector();
    b2_vec.sx = box2.posx_scaled;
    b2_vec.sy =
      box2.posy_scaled + box2.height_scaled + this.getClassGeneralDimension();

    b2_vec.ex = b2_vec.sx + box2.width_scaled + this.getClassGeneralDimension();
    b2_vec.ey = b2_vec.sy;

    let r2_vec: Vector;
    r2_vec = new Vector();
    r2_vec.sx =
      box2.posx_scaled + box2.width_scaled + this.getClassGeneralDimension();
    r2_vec.sy = box2.posy_scaled;

    r2_vec.ex = r2_vec.sx;
    r2_vec.ey =
      r2_vec.sy + box2.height_scaled + this.getClassGeneralDimension();

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
      let crossP = this.calcSegmentCross(lineVec, v);
      if (crossP != null) p_return = crossP;
    });
    return p_return;
  }

  ngOnInit(): void {
    this.socket.registerContainer(GlobalEditorService.L_ROOT_ID, this);
    this.socket.addListenerToEvent(this, (t) => {
      t.update();
    }, 'update');

    this.editorService.addListenerAfterDgFetch(this, (target) => {
      console.log('MODEL', target.editorService.model);
      target.editorService.model.lines.map((l) => {

        Line.upScaleBreakPoints(l, target.editorService.clientModel.canvas.scale);


        target.createLineWithControllerLocally(l);
      });
      console.log('lines');
      console.log('objects', target.dgObjects);
      //target.update();
    });
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
      let p1s = this.getCenter(this.getDgObjectById(l1.object_start_id), this.getControllerById(l1.id));
      let line1StartX = p1s.x;
      let line1StartY = p1s.y;

      let p1e = this.getCenter(this.getDgObjectById(l1.object_end_id), this.getControllerById(l1.id));
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
  getVectorOfLine(l: Line) {
    let v = new Vector();
    v.sx = this.getDgObjectById(l.object_start_id).scaledModel.posx_scaled;
    v.sy = this.getDgObjectById(l.object_start_id).scaledModel.posy_scaled;
    v.ex = this.getDgObjectById(l.object_end_id).scaledModel.posx_scaled;
    v.ey = this.getDgObjectById(l.object_end_id).scaledModel.posy_scaled;
    return v;
  }
  getMousePoint(e) {
    var rect = this.canvasDOM.nativeElement.getBoundingClientRect();
    let mousex = e.clientX - rect.x;
    let mousey = e.clientY - rect.y;
    return new Point(mousex, mousey);
  }
}
