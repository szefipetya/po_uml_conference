import { Component, OnInit } from '@angular/core';
import { Model } from 'src/app/components/models/Model';
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
    this.fullWidth = document.querySelector('html').clientWidth;
    this.fullHeight = document.querySelector('html').clientHeight;
    this.editorService.model.clip.width =
      this.fullWidth - this.editorService.model.toolbox.width;
    this.editorService.model.clip.height =
      this.fullHeight - this.editorService.model.menubar.height;
  }

  ngOnInit(): void {}
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
    if (bool || e.target.nodeName != 'INPUT') {
      console.log('false on all');
      this.editorService.model.classes.map((clas) => {
        if (clas.edit)
          if (clas.name.trim() != '') {
            clas.edit = false;
            this.editorService.model.canvas.edit_classTitle_id = null;
          } else {
            clas.name = 'Class';
            clas.edit = false;
            this.editorService.model.canvas.edit_classTitle_id = null;
          }
        clas.titleModel.edit = false;
        clas.titleModel.viewModel.save();

        clas.groups.map((egroup) => {
          egroup.attributes.map((e) => {
            if (e && e.edit) {
              this.editorService.model.canvas.edit_element_id = null;
              console.log('EDITED FOUND ');
              e.edit = false;
              e.viewModel.save();
              //e.forceUpdate();
            }
          });
        });
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
      e.preventDefault();
      this.editorService.model.canvas.selectedClassIds.map((id) => {
        this.editorService.model.classes = this.editorService.model.classes.filter(
          (clas) => clas.id != id
        );
      });
      console.log(this.editorService.model.classes);
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