import {
  Component,
  OnInit,
  Input,
  OnChanges,
  AfterContentInit,
} from '@angular/core';
import { AttributeElement } from 'src/app/components/models/DiagramObjects/AttributeElement';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { GlobalEditorService } from '../services/global-editor/global-editor.service';

@Component({
  selector: 'app-simple-class',
  templateUrl: './simple-class.component.html',
  styleUrls: ['./simple-class.component.scss'],
})
export class SimpleClassComponent
  implements OnInit, OnChanges, AfterContentInit {
  editorService;
  constructor(editorService: GlobalEditorService) {
    this.editorService = editorService;
  }
  ngAfterContentInit(): void {}
  @Input() model: SimpleClass;
  @Input() id: string;
  ngOnInit(): void {}
  ngOnChanges(): void {
    console.log('changed');
  }
}
