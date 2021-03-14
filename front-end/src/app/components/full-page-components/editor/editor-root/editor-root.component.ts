import { Component, OnInit } from '@angular/core';
import { Diagram } from 'src/app/components/models/Diagram/Diagram';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { GlobalEditorService } from '../services/global-editor/global-editor.service';

@Component({
  selector: 'app-editor-root',
  templateUrl: './editor-root.component.html',
  styleUrls: ['./editor-root.component.scss'],
})
export class EditorRootComponent implements OnInit {
  editorService;
  toolBoxWidth = 300;
  menuBarHeight = 80;
  fullWidth: number;
  fullHeight: number;
  //

  constructor(editorService: GlobalEditorService) {
    this.editorService = editorService;
    console.log('setup is running');
    this.setup();
  }

  ngOnInit(): void {}
  setup() {
    const p = new Promise(async (resolve, reject) => {
      await this.editorService.initFromServer();
      await resolve('success');
    });
    p.then((o) => {
      console.log(o);
      this.fullWidth = document.querySelector('html').clientWidth;
      this.fullHeight = document.querySelector('html').clientHeight;
      this.editorService.clientModel.canvas.clip.width =
        this.fullWidth -
        this.editorService.alignment.left_dock.width -
        this.editorService.alignment.right_dock.width;
      this.editorService.clientModel.canvas.clip.height =
        this.fullHeight - this.editorService.alignment.bottom_dock.height;
      this.editorService.editor_ref = this;
    });
  }
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
      this.editorService.model.dgObjects.map((clas) => {
        console.log(clas);
        console.log(clas instanceof SimpleClass);

        clas.viewModel.disableEdit();
        console.log('edit is false');
      });
      this.inputDOM = undefined;
    } else if (e.target.id == '#editor-input') this.inputDOM = e.target;
  };

  handleAsync = async (e) => {
    await this.disableEdits(e, false);
  };

  onMouseDown = (e) => {
    // e.persist();
    console.log('handle');
    this.handleAsync(e);
  };

  onKeyPress = (e) => {
    if (e.which == 13 || e.keyCode == 13) {
      // enter
      this.disableEdits(e, true);
    }
    if (e.key.match('Delete') || e.keyCode == 46) {
      console.log('del');
      let input = document.querySelector('#editor-input');
      if (!input) {
        e.preventDefault();
        this.editorService.clientModel.canvas.selectedClassIds.map((id) => {
          this.editorService.model.dgObjects = this.editorService.model.dgObjects.filter(
            (clas) => clas.id != id
          );
        });
      }
      console.log(this.editorService.model.dgObjects);
    }
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
