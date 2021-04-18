import { r3JitTypeSourceSpan } from '@angular/compiler';
import { LineCanvasComponent } from '../../full-page-components/editor/canvas-box/line-canvas/line-canvas.component';
import { CommonService } from '../../full-page-components/editor/services/common/common.service';
import { EditorSocketControllerService } from '../../full-page-components/editor/services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../full-page-components/editor/services/global-editor/global-editor.service';
import { ACTION_TYPE } from '../socket/ACTION_TYPE';
import { InteractiveItemBase } from '../socket/bases/InteractiveItemBase';
import { EditorAction } from '../socket/EditorAction';
import { BreakPoint } from './BreakPoint';
import { Line } from './Line';
import { LineCanvas } from './LineCanvas';
import { Vector } from './Vector';
export class LineController extends InteractiveItemBase {
  lineCanvasComponent: LineCanvasComponent;
  model: Line;
  public editBegin(): void {
    let action = new EditorAction(this.model.id, this.model._type, this.getParentId());
    console.log('edit_begin', this.model, this.sessionState)
    action.action = ACTION_TYPE.SELECT;
    action.json = '{}';
    action.target.target_id = this.model.id;
    this.sendAction(action);
  }
  public editEnd(): void {
    Line.downScaleBreakPoints(this.model, this.editorService.clientModel.canvas.scale)
    let action = new EditorAction(this.model.id, this.model._type, this.getParentId());
    action.action = ACTION_TYPE.UPDATE;

    this.model.viewModel = null;
    action.json = JSON.stringify(this.model);
    this.model.viewModel = this;


    console.log('edit ended', action.json);
    this.sendAction(action);
  }
  editorService: GlobalEditorService
  constructor(socket: EditorSocketControllerService, commonService: CommonService, editorService: GlobalEditorService) {
    super(socket, commonService);
    this.editorService = editorService;

  }
  updateModel(model: any, action_id: string, msg?: string): void {
    console.log('UPDATE RECEIVED');
    this.model.breaks = model.breaks;
    Line.upScaleBreakPoints(this.model, this.editorService.clientModel.canvas.scale)
    this.model.id = model.id;
    this.model.object_start_id = model.object_start_id;
    this.model.object_end_id = model.object_end_id;
    this.lineCanvasComponent.update();
  }
  getParentId(): string {
    return 'l_root';
  }
  deleteSelfFromParent(): void {

    this.socket.unregister(this);
    this.lineCanvasComponent.deleteController(this);
  }
  saveEvent(wastrue: any): void {
    console.error('saveEvent not implemented.');
  }
  disableEdit(): void {
    console.error('disableEdit not implemented.');
  }

  drawLine() {
    if (this.model?.object_start_id && this.model?.object_end_id) {
      let vec: Vector = new Vector();

      let ctx = this.lineCanvasComponent.ctx;
      ctx.beginPath();
      let spt;
      try {
        spt = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(this.model.object_start_id), this);
      } catch (error) {
        console.error(error);
        return;
      }
      let prevPoint: BreakPoint = new BreakPoint();
      prevPoint.point = spt;
      this.model.breaks_scaled.map((breakPoint) => {
        if (this.lineCanvasComponent.selectedLine == this.model) {
          this.lineCanvasComponent.strike(
            this.lineCanvasComponent.LINE_SELECTED_STROKE, this.lineCanvasComponent.LINE_WIDTH * 2, prevPoint.point, breakPoint.point
          );
          ctx.moveTo(breakPoint.point.x, breakPoint.point.y);
          ctx.beginPath();

          if (breakPoint.edit) {
            ctx.fillStyle = '#444';
            ctx.arc(breakPoint.point.x, breakPoint.point.y, 5, 0, 2 * Math.PI);
          } else {

            ctx.fillStyle = 'black';
            ctx.arc(breakPoint.point.x, breakPoint.point.y, 5, 0, 2 * Math.PI);
          }
          ctx.stroke();
        }
        //draw default line
        //  console.log("lockers", this.sessionState?.lockerUser_id, " --- ", this.editorservice.getUser().id)
        if (this.sessionState?.lockerUser_id != '-' && this.sessionState?.lockerUser_id != this.editorService.getUser().id) {
          this.lineCanvasComponent.strike(
            "red",
            this.lineCanvasComponent.LINE_WIDTH * 3,
            prevPoint.point,
            breakPoint.point
          );
        }
        this.lineCanvasComponent.strike(
          this.lineCanvasComponent.LINE_DEFAULT_STROKE,
          this.lineCanvasComponent.LINE_WIDTH,
          prevPoint.point,
          breakPoint.point
        );
        prevPoint = breakPoint;
      });

      let ept = this.lineCanvasComponent.getCenter(this.lineCanvasComponent.getDgObjectById(this.model.object_end_id), this);

      //draw outline if selected
      if (this.lineCanvasComponent.selectedLine == this.model) {
        this.lineCanvasComponent.strike(
          this.lineCanvasComponent.LINE_SELECTED_STROKE,
          this.lineCanvasComponent.LINE_WIDTH * 2,
          prevPoint.point,
          ept
        );
      }//if locked
      if (this.sessionState?.lockerUser_id != '-' && this.sessionState?.lockerUser_id != this.editorService.getUser().id) {
        this.lineCanvasComponent.strike(
          "red",
          this.lineCanvasComponent.LINE_WIDTH * 2,
          prevPoint.point,
          ept
        );
      }
      this.lineCanvasComponent.strike(
        this.lineCanvasComponent.LINE_DEFAULT_STROKE,
        this.lineCanvasComponent.LINE_WIDTH,
        prevPoint.point,
        ept
      );
      vec.sx = prevPoint.point.x;
      vec.sy = prevPoint.point.y;
      vec.ex = ept.x;
      vec.ey = ept.y;
      //draw the img
      let angle = (vec.ey - vec.sy) / (vec.ex - vec.sx);

      let angleRad = Math.atan(angle);
      let angleDeg = (angleRad * 180) / Math.PI + 90;
      if (ept.x < prevPoint.point.x) angleDeg += 180;
      let img = this.lineCanvasComponent.getLineHeadImg(this.model.lineType.endHead);
      let p = this.lineCanvasComponent.getVectorInterSectionWithBox(
        new Vector(prevPoint.point.x, prevPoint.point.y, ept.x, ept.y),
        this.lineCanvasComponent.getDgObjectById(this.model.object_end_id).scaledModel
      );
      if (img)
        this.lineCanvasComponent.drawRotatedImage(img, p.x, p.y, angleDeg);
    }
  }
}
