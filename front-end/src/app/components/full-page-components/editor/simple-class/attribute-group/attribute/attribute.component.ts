import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { SimpleClassAttributeGroup } from 'src/app/components/models/SimpleClassAttributeGroup';
import { AttributeElement } from '../../../../../models/AttributeElement';
import { AttributeGroupComponent } from '../attribute-group.component';
@Component({
  selector: 'app-attribute',
  templateUrl: './attribute.component.html',
  styleUrls: ['./attribute.component.scss'],
})
export class AttributeComponent implements OnInit, OnChanges {
  targetDOM: any;
  inputDOM: Element;
  clname: string;
  dots: string;
  type_dispayed: any;
  str_displayed: any;

  name: string;
  type: string;

  constructor() {}
  @Input() parent: AttributeGroupComponent;
  @Input() isTitle: boolean;
  @Input() model: AttributeElement;
  ngOnInit(): void {
    this.render();
  }
  ngOnChanges() {
    this.render();
  }

  parentClass;
  deleteSelfFromParent = () => {
    this.parent.delete(this.model.id);
  };
  force = (e) => {
    this.render();
  };
  saveEvent = () => {
    if (this.isTitle) {
    } else if (this.model.name == '') {
      this.deleteSelfFromParent();
      console.log('deleted', this.model);
    }
    this.force(null);
  };

  onClick(e) {
    console.log('editing');
    this.model.edit = true;
    this.targetDOM = e.target;
    this.parent.editorService.model.canvas.edit_element_id = this.model.id;
    if (!this.isTitle) this.renderMode = 'ATTRIBUTE_EDIT';
    this.render();
    this.force(() => {
      let inputDOM = document.querySelector('#editor-input');
      if (inputDOM) {
        this.inputDOM = inputDOM;
        // inputDOM.focus();
      }
    });
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

    this.setNameAndType(e, isVisibilitySymbolWritten);
    this.force(null);
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

      let clname;
      if (this.isTitle) {
        this.clname = 'class-title class-element';
        if (this.model.edit) {
          this.renderMode = 'TITLE_EDIT';
          let inputwidth;
          let val1 =
            (this.parent.editorService.model.class_general.fontsize_scaled /
              this.elementScale) *
            (strfull.length + 1);
          let val2 =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.model.class_general.padding_scaled +
              this.parent.editorService.model.class_general.border_scaled) *
              2;
          inputwidth = Math.max(val1, val2);
          //old width: `${(strfull.length + 1) * (this.state.parent.parent.model.class_general.fontsize_scaled / this.titleScale)}px`
        } else {
          this.renderMode = 'TITLE';
          let rescale = this.titleScale;
          let charwidth =
            this.parent.editorService.model.class_general.fontsize_scaled /
            rescale;
          let textwidth =
            (this.parent.editorService.model.class_general.fontsize_scaled /
              rescale) *
            strfull.length;
          let width =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.model.class_general.padding_scaled +
              this.parent.editorService.model.class_general.border_scaled) *
              2;
          let l = false;
          if (textwidth > width) {
            this.showedText = strfull.substr(
              0,
              strfull.length - Math.round((textwidth - width) / charwidth + 1.5)
            );
            l = true;
          }
          name = this.showedText.split(':')[0];
          this.dots = '';
          if (l) {
            this.dots += '...';
          }
          this.renderMode = 'ATTRIBUTE';
        }
      } else {
        //not title

        this.clname = 'class-element';
        if (this.model.edit) {
          console.log('attr edit');
          this.renderMode = 'ATTRIBUTE_EDIT';
          let inputwidth;
          let val1 =
            (this.parent.editorService.model.class_general.fontsize_scaled /
              this.elementScale) *
            (strfull.length + 1);
          let val2 =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.model.class_general.padding_scaled +
              this.parent.editorService.model.class_general.border_scaled) *
              2;
          inputwidth = Math.max(val1, val2);
        } else {
          this.renderMode = 'ATTRIBUTE';
          let l = false;
          clname = this.clname;
          let rescale = this.elementScale;
          let charwidth =
            this.parent.editorService.model.class_general.fontsize_scaled /
            rescale;
          let textwidth =
            (this.parent.editorService.model.class_general.fontsize_scaled /
              rescale) *
            (strfull.length + 1);
          let width =
            this.parent.parent.model.scaledModel.width_scaled -
            (this.parent.editorService.model.class_general.padding_scaled +
              this.parent.editorService.model.class_general.border_scaled) *
              2;
          if (textwidth > width) {
            this.showedText = strfull.substr(
              0,
              strfull.length - Math.round((textwidth - width) / charwidth + 1.5)
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
            this.type_dispayed = type;
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
