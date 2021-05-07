import { AfterViewChecked, Component, Input, OnInit, ViewChild } from '@angular/core';
import { DiagramObject_General } from 'src/app/components/models/DiagramObjects/DiagramObject_General';
import { CommonService, MSG_TYPE } from '../../services/common/common.service';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { DiagramObjectComponent } from '../diagram-object/diagram-object.component';
import { PackageObject } from "src/app/components/models/DiagramObjects/PackageObject";
import { LogInteractive_I } from 'src/app/components/models/socket/interface/LogInteractive_I';
import { ICON } from 'src/app/components/models/management/ICON';
@Component({
  selector: 'app-package-object',
  templateUrl: './package-object.component.html',
  styleUrls: ['./package-object.component.scss']
})
export class PackageObjectComponent extends DiagramObjectComponent
  implements OnInit, AfterViewChecked {
  @Input()
  model: PackageObject;
  @Input() public general: DiagramObject_General;
  //@ViewChild('texta') textarea: ElementRef<HTMLTextAreaElement>;
  constructor(
    protected socket: EditorSocketControllerService,
    protected commonService: CommonService,
    protected editorService: GlobalEditorService
  ) {
    super(socket, commonService, editorService);
  }
  iconEnumToSrc(icon: ICON): string {
    switch (icon) {
      case ICON.PROJECT_FOLDER: return "../../../../../../assets/svg/management/box.svg";
      case ICON.PROJECT_CLASS: return "../../../../../../assets/svg/management/class.svg";
    }
  }
  updateScales(scale): void {
    this.updateFont();
  }
  updateModel(model: any, action_id: string, msg?: string): void {
    super.updateModel(model, action_id, msg);
    this.model.elements = model.elements;
  }
  updateFont() {
    /*if (!this.textarea) return;
    this.textarea.nativeElement.style.fontSize =
      this.general.fontsize_scaled + 'px';*/
  }

  update(): void { }
  ngAfterViewChecked(): void {
    if (this.model.edit) {
      this.updateFont();

      this.updateHeight();
    }
  }
  onContentClick(e) {
    if (this.isAccessible()) this.model.edit = true;
  }

  inputClick(e) {
    this.model.edit = true;
  }
  deleteSelfFromParent() {
    // console.log("HAHAHAHAH NEM TUDSZ TÖRÖLNI")

  }
  deleteMessageToServer() {
    this.commonService.putLog("You can not delete this one", MSG_TYPE.ERROR, this)
    console.log("HAHAHAHAH NEM TUDSZ TÖRÖLNI")
  }

  updateHeight() {
    /*if (!this.textarea) return;
    this.textarea.nativeElement.style.height = '';
    this.textarea.nativeElement.style.height =
      Math.max(
        this.textarea.nativeElement.scrollHeight,
        this.getInnerHeight()
      ) + 'px';*/
    //  this.sendDimensionUpdate();
  }
  textInput(e) {
    this.updateHeight();
  }
  onKeyPress(e) {
    console.log(e);
  }

  disableEdit() {
    console.log('notebox edit false');

    if (this.model.edit) this.editEnd();
    this.model.edit = false;
  }
  ngOnInit(): void {
    //this.model._type = 'SimpleClass';
    this.model.viewModel = this;
    super.ngOnInit();
  }
  ngOnChanges(): void {
    console.log('changed');
  }
}
