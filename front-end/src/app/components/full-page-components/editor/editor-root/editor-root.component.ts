import { AfterContentInit, Component, OnInit } from '@angular/core';
import { Diagram } from 'src/app/components/models/Diagram/Diagram';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { NoteBox } from 'src/app/components/models/DiagramObjects/NoteBox';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClassComponent } from '../diagram-objects/simple-class/simple-class.component';
import { GlobalEditorService } from '../services/global-editor/global-editor.service';

@Component({
  selector: 'app-editor-root',
  templateUrl: './editor-root.component.html',
  styleUrls: ['./editor-root.component.scss'],
})
export class EditorRootComponent implements OnInit, AfterContentInit {
  editorService: GlobalEditorService;
  toolBoxWidth = 300;
  menuBarHeight = 80;

  //

  constructor(editorService: GlobalEditorService) {
    this.editorService = editorService;
    console.log('setup is running');

  }
  ngAfterContentInit() {

  }
  ngOnInit(): void { }


  newButton;
  newButtonTransition;
  newButtonFontSize;
  edit_target;
  inputDOM;
  interaction_blocked = false;

  disableEdits = (e, bool) => {
    const isnewButton = Array.from(e.target.classList).includes(
      'class-element-group-new_element-button'
    );
    if (isnewButton) {
      this.newButton = e.target;
      this.newButtonTransition = this.newButton.style.transition;
      this.newButtonFontSize = this.newButton.style.fontSize;
      this.newButton.style.transition = 'none';
      this.newButton.style.fontSize = '1.2em';
      this.newButton.style.height = '1.0em';
    }
    const isElem = Array.from(e.target.classList).includes('class-element');
    if (
      bool ||
      (e.target.nodeName != 'INPUT' && e.target.className != 'INPUT')
    ) {
      console.log('false on all');
      this.editorService.model.dgObjects.map((clas: DiagramObject) => {
        //  console.log(clas);
        clas.viewModel.disableEdit();

      });
      this.inputDOM = undefined;
    } else if (e.target.id == '#editor-input') this.inputDOM = e.target;
  };

  handleAsync = async (e) => {
    await this.disableEdits(e, false);
  };

  onMouseDown = (e) => {
    // e.persist();
    //  console.log('handle');
    this.handleAsync(e);
  };

  onKeyPress = (e: KeyboardEvent) => {

    if (e.which == 13 || e.keyCode == 13) {
      // enter
      this.disableEdits(e, true);
    }
    if (e.key?.match('Delete') || e.keyCode == 46) {
      //   console.log('del');
      let input = document.querySelector('#editor-input');
      if (!input) {
        e.preventDefault();
        this.editorService.clientModel.canvas.selectedClassIds.map((id) => {
          this.editorService.model.dgObjects.map((clas: DiagramObject) => {
            if (clas.id == id) {
              clas.viewModel.deleteAsync('root');
            }
          });
        });
      }

      //  console.log(this.editorService.model.dgObjects);
    }
    this.editorService.canvasBox.onKeyPress(e);
    //  this.forceUpdate();
  };

  onMouseUp = (e) => {
    const prop = this.findPropertyByRegex(
      document.querySelector('.edit-box'),
      '__reactEventHandlers*'
    );
    if (prop) prop.onMouseUp(e);
    if (this.newButton) {
      this.newButton.style.height = '0.85em';
      this.newButton.style.transition = this.newButtonTransition;
      this.newButton.style.fontSize = this.newButtonFontSize;
    }
  };

  onMouseMove = (e) => {
    const prop = this.findPropertyByRegex(
      document.querySelector('.edit-box'),
      '__reactEventHandlers*'
    );
    if (prop) {
      prop.onMouseMove(e);
    }
  };

  findPropertyByRegex = (o, r) => {
    for (const key in o) {
      if (key.match(r)) {
        return o[`${key}`];
      }
    }
    return undefined;
  };
}
