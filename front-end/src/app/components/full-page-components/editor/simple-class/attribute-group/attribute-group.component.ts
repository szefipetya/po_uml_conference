import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { SimpleClassElementGroup } from 'src/app/components/models/DiagramObjects/SimpleClassElementGroup';
import { GlobalEditorService } from '../../services/global-editor/global-editor.service';
import { SimpleClassComponent } from '../simple-class.component';

@Component({
  selector: 'app-attribute-group',
  templateUrl: './attribute-group.component.html',
  styleUrls: ['./attribute-group.component.scss'],
})
export class AttributeGroupComponent implements OnInit, AfterViewInit {
  @Input() model: SimpleClassElementGroup;
  inputDOM: any;
  @Input() parent: SimpleClassComponent;
  editorService;
  constructor(editorService: GlobalEditorService) {
    this.editorService = editorService;
  }
  ngAfterViewInit(): void {
    if (this.inputDOM) this.inputDOM.focus();
  }

  ngOnInit(): void {}
  delete(id) {
    console.dir('before: this.state.elements', this.model.attributes);
    /*  this.setState({
             elements: this.state.elements.filter(function (el) {
                 return el.id !== id || el.name.length > 0
             })
         });
  */
    this.model.attributes = this.model.attributes.filter(function (el) {
      return el.id !== id || el.name.length > 0;
    });

    console.dir('this.state.elements', this.model.attributes);
    //        this.state.elements = this.state.elements.filter(el => el.id != id)
  }

  onMouseOver = (e) => {
    // e.target.
  };

  getNewId = () => {
    const found = false;
    const match = 0;
    const id = undefined;
    let maxid = 0;
    this.parent.model.groups.map((clas, ind) => {
      clas.attributes.map((e, ind2) => {
        if (Number.parseInt(e.id) > maxid) maxid = Number.parseInt(e.id);
      });
    });

    return '' + (maxid + 1);
    // return '_' + Math.random().toString(36).substr(2, 9);
  };

  pushNewElement = async (visibility, name, type) => {
    const id = this.getNewId();
    this.editorService.clientModel.canvas.edit_element_id = id;
    let newAttr = {
      edit: true,
      id: id,
      visibility: visibility,
      name: name,
      type: type,
      viewModel: null,
    };
    await this.model.attributes.push(newAttr);
    setTimeout(() => {
      this.inputDOM = document.querySelector('#editor-input');
      if (this.inputDOM) {
        this.inputDOM.click();
        this.inputDOM.focus();
        console.log('focused');
      }
    }, 0);
  };

  onNewButtonClick = (e) => {
    const inputDOM = document.querySelector('#editor-input');
    if (!inputDOM) this.pushNewElement('', '', '');
  };
}
