import { supportsPassiveEventListeners } from '@angular/cdk/platform';
import { rendererTypeName } from '@angular/compiler';
import {
  AfterContentInit,
  Component,
  Input,
  OnChanges,
  OnInit,
} from '@angular/core';
import { SimpleClassElementGroup } from 'src/app/components/models/DiagramObjects/SimpleClassElementGroup';
import { AttributeElement } from '../../../../../models/DiagramObjects/AttributeElement';
import { AttributeGroupComponent } from '../attribute-group.component';
@Component({
  selector: 'app-attribute',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.scss'],
})
export class AttributeComponent implements OnInit, OnChanges, AfterContentInit {
  targetDOM: any;
  inputDOM: Element;
  clname: string;
  dots: string;
  type_dispayed: any;
  str_displayed: any;

  name: string;
  type: string;

  constructor() {}
  ngAfterContentInit(): void {
    this.model.viewModel = this;
    this.render();
  }
  @Input() parent: any;

  @Input() isTitle: boolean;
  @Input() model: AttributeElement;
  ngOnInit(): void {
    // this.render();
  }
  ngOnChanges() {
    //  this.render();
  }

  save() {
    console.log('save');
    this.saveEvent();
    this.render();
  }
  parentClass;
  deleteSelfFromParent = () => {
    this.parent.delete(this.model.id);
  };
  saveEvent() {
    if (this.isTitle) {
      if (this.model.name == '') {
        this.deleteSelfFromParent();
        console.log('deleted', this.model);
        this.model.name = 'Class';
        this.model.edit = true;
      }
    } else {
      //NOT TITLE
      if (this.model.name == '') {
        this.deleteSelfFromParent();
        console.log('deleted', this.model);
      }
    }
  }

  onClick(e) {
    console.log('editing');
    this.model.edit = true;
    this.targetDOM = e.target;
    console.log(this.parent.editorService);
    this.parent.editorService.clientModel.canvas.edit_element = this;
    if (!this.isTitle) this.renderMode = 'ATTRIBUTE_EDIT';

    this.render();

    let inputDOM = document.querySelector('#editor-input');
    if (inputDOM) {
      this.inputDOM = inputDOM;
      // inputDOM.focus();
    }
  }
  setVisibility = (e) => {
    let vis = '+';
    let l = false;
    if (
      e.target.value[0] == '+' ||
      e.target.value[0] == '-' ||
      e.target.value[0] == '#' ||
      e.target.value[0] == '~'
    ) {
      l = true;
      vis = e.target.value[0];
    }
    this.model.visibility = vis;
    return l;
  };
  setNameAndType = (e, l) => {
    let splitted = e.target.value.split(':');
    this.model.name = splitted[0].trim();
    if (l) {
      this.model.name = this.model.name.substr(1);
    }
    if (splitted[1]) this.model.type = splitted[1].trim();
    else {
      this.model.type = '';
    }
  };
  onInput = (e) => {
    let isVisibilitySymbolWritten = this.setVisibility(e);
    console.log(this.model);
    this.setNameAndType(e, isVisibilitySymbolWritten);
    this.render();
  };
  showedText = '';
  titleScale = 1.5;
  elementScale = 2.35;
  inputWidth = 20;
  renderMode: string;

  str;
  render() {
    if (this.model) {
      let { name, type, id } = this.model;

      if (type == '') {
        this.str = '';
      } else {
        this.str = ':';
      }
      let strfull;
      if (type) strfull = name + this.str + type;
      else {
        strfull = name;
      }
      this.showedText = strfull;

      if (this.isTitle) {
        this.clname = 'class-title class-element';

        if (this.model.edit) {
          //TITLE_EDIT
          let val1 =
            (this.parent.editorService.clientModel.class_general
              .fontsize_scaled /
              this.elementScale) *
            1.418 *
            strfull.length;
          let val2 =
            this.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.clientModel.class_general
              .padding_scaled +
              this.parent.editorService.clientModel.class_general
                .border_scaled) *
              2;
          this.inputWidth = Math.max(val1, val2);
          //old width: `${(strfull.length + 1) * (this.state.parent.parent.clientModel.class_general.fontsize_scaled / this.titleScale)}px`
        } else {
          //TITLE
          let rescale = this.titleScale;
          let charwidth =
            this.parent.editorService.clientModel.class_general
              .fontsize_scaled / rescale;
          let textwidth =
            (this.parent.editorService.clientModel.class_general
              .fontsize_scaled /
              rescale) *
            strfull.length;
          let width =
            this.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.clientModel.class_general
              .padding_scaled +
              this.parent.editorService.clientModel.class_general
                .border_scaled) *
              2;
          let l = false;

          this.showedText = this.model.name.substr(
            0,
            this.model.name.length -
              Math.round(((textwidth - width) / charwidth) * 1.1 + 1.5)
          );
          if (textwidth > width) {
            l = true;
          }

          this.dots = '';
          if (l) {
            this.dots += '...';
          }
        }
      } else {
        this.clname = 'class-element';
        if (this.model.edit) {
          //ATTRIBUTE EDIT

          this.renderMode = 'ATTRIBUTE_EDIT';

          let val1 =
            (this.parent.editorService.clientModel.class_general
              .fontsize_scaled /
              this.elementScale) *
            0.92 *
            strfull.length;
          let val2 =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.clientModel.class_general
              .padding_scaled +
              this.parent.editorService.clientModel.class_general
                .border_scaled) *
              2;
          this.inputWidth = Math.max(val1, val2);
        } else {
          //ATTRIBUTE
          let l = false;

          let rescale = this.elementScale;
          let charwidth =
            this.parent.editorService.clientModel.class_general
              .fontsize_scaled / rescale;
          let textwidth =
            (this.parent.editorService.clientModel.class_general
              .fontsize_scaled /
              rescale) *
            (strfull.length + 1);
          let width =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.clientModel.class_general
              .padding_scaled +
              this.parent.editorService.clientModel.class_general
                .border_scaled) *
              2;
          if (textwidth > width) {
            this.showedText = strfull.substr(
              0,
              strfull.length - Math.round((textwidth - width) / charwidth)
            );
            l = true;
          }
          this.name = this.showedText.split(':')[0];
          this.str = this.showedText.includes(':');
          this.type = this.showedText.split(':')[1];
          this.type_dispayed;
          this.str_displayed;
          if (this.str) {
            this.str_displayed = ':';
          } else this.str_displayed = '';
          if (type) {
            this.type_dispayed = this.type;
          } else this.type_dispayed = '';
          this.dots = '';
          if (l) {
            this.dots += '...';
          }
        }
      }
    }
  }
}
