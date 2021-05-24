import { InteractivityChecker } from '@angular/cdk/a11y';
import { Line } from '../../../../models/line/Line';
import { Vector } from '../../../../models/line/Vector';
import { LineCanvas } from '../../../../models/line/LineCanvas';
import { clone1, Pair } from '../../../../../utils/utils';
import {
  AfterContentInit,
  AfterViewInit,
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
import { DiagramObject_General } from 'src/app/components/models/DiagramObjects/DiagramObject_General';
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
import { MathHelper } from "./MathHelper";
@Component({
  selector: 'app-line-canvas',
  templateUrl: './line-canvas.component.html',
  styleUrls: ['./line-canvas.component.scss'],
})
export class LineCanvasComponent
  implements OnInit, AfterViewInit, SessionInteractiveContainer {
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
  ) {
    this.mathHelper = new MathHelper(this);
  }
  updateColorOnly() {
    throw new Error('Method not implemented.');
  }
  createItem(model: DynamicSerialObject, extra?: any) {
    this.createLineWithControllerLocally(model as Line);
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
  @Input() class_general: DiagramObject_General;
  // @Input() dgObjects: DiagramObject[];
  // @Input() lines: Line[]; //does not work because at init, this will be null
  lineControllers: LineController[] = [];
  lineInstance: Line;
  selectedLine: Line;
  ngAfterViewInit(): void {
    setTimeout(() => {
      this.init();
    }, 100);
  }
  lineHeads;
  LINE_WIDTH = 3;
  // LINE_SELECTED_STROKE =  this.socket.getColorByUserId(this.sessionState.lockerUser_id);
  LINE_DEFAULT_STROKE = 'black';
  init() {
    this.ctx = this.canvasDOM.nativeElement.getContext('2d');
    //  this.ctx.canvas.onload = this.update;
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
    if (this.ctx == null) {
      console.error('canvas context could not load, retrying...')
      setTimeout(() => { this.init() }, 100);
    }
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
      //this.update();
      throw new Error("end_object_not_found");

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
        (c) => target.dataset.id == c.id
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
        (c) => target.dataset.id == c.id
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

  update() {
    this.clear();
    this.lineControllers.map((l) => {
      try {
        l.drawLine();
      } catch (err) {
        console.error(err);
      }
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
      this.selectedLine == this.mathHelper.getTheLineThatsIntersectingWithCursor(e)
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
        let pair = this.mathHelper.findThe2BreakPointsNearby(this.transformingLine, e);
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
  isLineSelected(): boolean {
    return this.selectedLine != null;
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
    let newselected = this.mathHelper.getTheLineThatsIntersectingWithCursor(e);
    if (newselected) {//new selection,

      this.getControllerById(newselected.id).editBegin();

    }
    this.selectedLine = newselected;
    this.update();
  }
  mathHelper: MathHelper;

  getMousePoint(e) {
    var rect = this.canvasDOM.nativeElement.getBoundingClientRect();
    let mousex = e.clientX - rect.x;
    let mousey = e.clientY - rect.y;
    return new Point(mousex, mousey);
  }
  ngOnInit(): void {
    this.socket.addListenerToEvent(this, (t) => {
      t.update();
    }, 'update');

    this.editorService.addListenerToEvent(this, (target) => {
      target.lineControllers = [];
      if (!this.ctx) this.init();
      this.socket.registerContainer(GlobalEditorService.L_ROOT_ID, this);

      console.log('MODEL', target.editorService.model);
      target.editorService.model.lines.map((l) => {


        Line.upScaleBreakPoints(l, target.editorService.clientModel.canvas.scale);


        target.createLineWithControllerLocally(l);
      });
      console.log('lines');
      console.log('objects', target.dgObjects);
      target.update();
    }, 'diagram_fetch');
  }
  getVectorOfLine(l: Line) {
    let v = new Vector();
    v.sx = this.getDgObjectById(l.object_start_id).scaledModel.posx_scaled;
    v.sy = this.getDgObjectById(l.object_start_id).scaledModel.posy_scaled;
    v.ex = this.getDgObjectById(l.object_end_id).scaledModel.posx_scaled;
    v.ey = this.getDgObjectById(l.object_end_id).scaledModel.posy_scaled;
    return v;
  }
  hasItem(target_id: string) {
    throw new Error('Method not implemented.');
  }
  restoreItem(item_id: string, model: DynamicSerialObject) {
    throw new Error('Method not implemented.');
  }
}
