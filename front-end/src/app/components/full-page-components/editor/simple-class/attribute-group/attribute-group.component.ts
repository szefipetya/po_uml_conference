import { Component, Input, OnInit } from '@angular/core';
import { SimpleClass } from 'src/app/components/models/SimpleClass';
import { SimpleClassAttributeGroup } from 'src/app/components/models/SimpleClassAttributeGroup';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { SimpleClassComponent } from '../simple-class.component';

@Component({
  selector: 'app-attribute-group',
  templateUrl: './attribute-group.component.html',
  styleUrls: ['./attribute-group.component.scss']
})
export class AttributeGroupComponent implements OnInit {
  @Input() model: SimpleClassAttributeGroup;
  inputDOM: Element;
  @Input() parent:SimpleClassComponent;
editorService;
  constructor(editorService:GlobalEditorService) {
    this.editorService=editorService;
  }

  ngOnInit(): void {
  }
  delete(id) {
    console.dir('before: this.state.elements', this.model.attributes);
    /*  this.setState({
             elements: this.state.elements.filter(function (el) {
                 return el.id !== id || el.name.length > 0
             })
         });
  */
 this.model.attributes = this.model.attributes.filter(function(el) {
      return el.id !== id || el.name.length > 0;
    });

    console.dir('this.state.elements', this.model.attributes);
    //        this.state.elements = this.state.elements.filter(el => el.id != id)
    this.forceUpdate();
  }
  forceUpdate() {
    throw new Error('Method not implemented.');
  }



  onMouseOver = e => {
    // e.target.
  };

  getNewId = () => {
    const found = false;
    const match = 0;
    const id = undefined;
    let maxid = 0;
    this.parent.model.groups.map((clas, ind) => {
      clas.attributes.map((e, ind2) => {

          if (e.id > maxid) maxid = e.id;

      });
    });

    return maxid + 1;
  };

  pushNewElement = async (visibility, name, type) => {
    const id = this.getNewId();
    this.editorService.canvas.edit_element_id = id;

    await this.model.attributes.push({
      edit: true,
      id:id,
      visibility:visibility,
      name:name,
      type:type
    });

      const inputDOM = document.querySelector('#editor-input');
      if (inputDOM) {
        this.inputDOM = inputDOM;
       /* if(inputDOM!=null)
        inputDOM.focus();*/
      }

  };

  onNewButtonClick = e => {
    const inputDOM = document.querySelector('#editor-input');
    if (!inputDOM) this.pushNewElement('', '', '');
    this.forceUpdate();
  };


}
