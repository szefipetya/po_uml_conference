import { Component, Input, OnInit, TemplateRef } from '@angular/core';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';

@Component({
  selector: 'app-diagram-object',
  templateUrl: './diagram-object.component.html',
  styleUrls: ['./diagram-object.component.scss'],
})
export class DiagramObjectComponent implements OnInit {
  constructor() {}
  updateScales(scale): void {}
  update(): void {}
  disableEdit() {
    this.model.edit = false;
  }
  getInnerWidth() {
    return (
      this.model.scaledModel.width_scaled - this.general.padding_scaled * 2
    );
  }
  getInnerHeight() {
    return (
      this.model.scaledModel.height_scaled - this.general.padding_scaled * 2
    );
  }
  @Input() public contentTemplate: TemplateRef<any>;
  @Input() public model: any;
  @Input() public general: SimpleClass_General;
  ngOnInit(): void {}
}
