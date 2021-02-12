import { Component, OnInit,Input } from '@angular/core';
import { SimpleClass } from 'src/app/components/models/SimpleClass';
import {GlobalEditorService} from '../services/global-editor/global-editor.service'

@Component({
  selector: 'app-simple-class',
  templateUrl: './simple-class.component.html',
  styleUrls: ['./simple-class.component.scss']
})
export class SimpleClassComponent implements OnInit {
editorService;
  constructor(editorService:GlobalEditorService) {
    this.editorService=editorService;
  }
  @Input() model:SimpleClass;
  @Input() id: string;
  ngOnInit(): void {
  }

}
