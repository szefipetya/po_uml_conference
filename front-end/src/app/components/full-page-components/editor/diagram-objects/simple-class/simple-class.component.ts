import {
  Component,
  OnInit,
  Input,
  OnChanges,
  AfterContentInit,
  TemplateRef,
} from '@angular/core';
import { AttributeElement } from 'src/app/components/models/DiagramObjects/AttributeElement';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClass_General } from 'src/app/components/models/DiagramObjects/SimpleClass_General';
import { EditorSocketControllerService } from '../../services/editor-socket-controller/editor-socket-controller.service';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { DiagramObjectComponent } from '../diagram-object/diagram-object.component';

@Component({
  selector: 'app-simple-class',
  templateUrl: './simple-class.component.html',
  styleUrls: ['./simple-class.component.scss'],
})
export class SimpleClassComponent
  extends DiagramObjectComponent
  implements OnInit, OnChanges, AfterContentInit {
  updateScales(scale: any): void {
    if (this.model.titleModel.edit) {
      this.model.titleModel.viewModel.render();
    }
    this.model.groups.map((group) => {
      group.attributes.map((e2) => {
        if (e2.edit) {
          e2.viewModel.inputWidth *= scale;
          e2.viewModel.render();
        }
      });
    });
  }
  update(): void {
    this.model.groups.map((group) => {
      group.attributes.map((a) => {
        a.viewModel.render();
      });
    });
    this.model.titleModel.viewModel.render();
  }

  disableEdit() {
    console.log('class', this.model.name, 'edit false');
    if (this.model.edit)
      if (this.model.name.trim() != '') {
        this.model.edit = false;
        //   this.editorService.clientModel.canvas.edit_classTitle_id = null;
      } else {
        this.model.name = 'Class';
        this.model.edit = false;
        // this.editorService.clientModel.canvas.edit_classTitle_id = null;
      }
    let prev = this.model.titleModel.edit;
    this.model.titleModel.edit = false;
    this.model.titleModel.viewModel.save(prev);

    this.model.groups.map((egroup) => {
      egroup.attributes.map((e) => {
        if (e && e.edit) {
          // this.editorService.clientModel.canvas.edit_element_id = null;
          console.log('EDITED FOUND ');
          let prev = e.edit;
          e.edit = false;
          e.viewModel.save(prev);
        }
      });
    });
  }
  // editorService;
  @Input() public model: SimpleClass;
  @Input() public general: SimpleClass_General;
  constructor(socket: EditorSocketControllerService) {
    super(socket);
  }
  ngAfterContentInit(): void {
    this.model.viewModel = this;
  }
  ngOnInit(): void {
    this.model.viewModel = this;
  }
  ngOnChanges(): void {
    console.log('changed');
  }
}
