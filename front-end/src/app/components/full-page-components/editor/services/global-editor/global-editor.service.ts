import {
  Injectable,
  ModuleWithComponentFactories,
  NgModuleRef,
} from '@angular/core';
import { Element_c } from 'src/app/components/models/DiagramObjects/Element_c';
import { LINE_TYPE } from 'src/app/components/models/line/LINE_TYPE';
import { Model } from '../../../../models/Model';
import { CanvasBoxComponent } from '../../canvas-box/canvas-box.component';
import { LineCanvasComponent } from '../../canvas-box/line-canvas/line-canvas.component';
@Injectable({
  providedIn: 'root',
})
export class GlobalEditorService {
  model;
  alignment;

  constructor() {
    this.alignment = {
      left_dock: {
        width: 100,
      },
      right_dock: {
        width: 100,
      },
      bottom_dock: {
        height: 100,
      },
    };
    this.model = {
      user: { userid: '001' },
      lineCanvas: {
        lines: [],
        drawLineType: LINE_TYPE,
        viewModel: null,
        width:
          window.innerWidth -
          this.alignment.left_dock.width -
          this.alignment.right_dock.width,
        height: window.innerHeight - this.alignment.bottom_dock.height,
      },
      canvas: {
        viewModel: null,
        edit_element_id: null,
        edit_classTitle_id: null,
        scale: 1,
        posx: 0,
        posy: 0,
        width:
          window.innerWidth -
          this.alignment.left_dock.width -
          this.alignment.right_dock.width,
        height: window.innerHeight - this.alignment.bottom_dock.height,
        gridSize: 10,
        drawMode: 'cursor',
        drawRect: {
          x: 0,
          y: 0,
          width: 0,
          height: 0,
        },
        selectedClassIds: ['c1'],
      },
      clip: {
        width: 0,
        height: 0,
      },
      class_general: {
        padding_scaled: 3,
        border_scaled: 1.5,
        min_width: 80,
        min_width_scaled: 80,
        min_height: 75,
        min_height_scaled: 75,
        fontsize: 16,
        fontsize_scaled: 16,
      },
      selectedClass: null,
      classes: [
        {
          id: 'c1',
          posx: 110,
          posy: 220,
          width: 110,
          height: 250,
          min_height: 75,
          scaledModel: {
            posx_scaled: 110,
            posy_scaled: 220,
            width_scaled: 110,
            height_scaled: 250,
            min_height_scaled: 75,
          },
          z: 2,
          edit: false,
          name: 'Class1',
          class_type: 'classDG',
          titleModel: {
            id: '-1',
            edit: false,
            name: 'attr2',
          },
          groups: [
            {
              group_name: 'attributes',
              group_syntax: 'attribute',
              attributes: [
                {
                  id: '1',
                  visibility: '+',
                  edit: false,
                  name: 'attr1',
                  type: 'int',
                },
                {
                  id: '2',
                  visibility: '+',
                  edit: false,
                  name: 'attr2',
                  type: 'int',
                },
              ],
            },
            {
              group_name: 'functions',
              group_syntax: 'function',
              attributes: [
                {
                  id: '3',
                  visibility: '-',
                  edit: false,
                  name: 'func1',
                  type: 'int',
                },
                {
                  id: '4',
                  visibility: '#',
                  edit: false,
                  name: 'func2',
                  type: 'int',
                },
              ],
            },
          ],
        },
        {
          id: 'c2',
          posx: 310,
          posy: 420,
          width: 110,
          height: 250,
          min_height: 75,
          scaledModel: {
            posx_scaled: 310,
            posy_scaled: 420,
            width_scaled: 110,
            height_scaled: 250,
            min_height_scaled: 75,
          },
          z: 1,
          edit: false,
          name: 'Person',
          class_type: 'classDG',
          titleModel: {
            id: '-2',
            edit: false,
            name: 'attr2',
          },
          groups: [
            {
              group_name: 'attributes',
              group_syntax: 'attribute',
              attributes: [
                {
                  id: '5',
                  visibility: '+',
                  edit: false,
                  name: 'name',
                  type: 'string',
                },
                {
                  id: '6',
                  visibility: '#',
                  edit: false,
                  name: 'age',
                  type: 'intssssssssssssssssssssssssssssssssssssssssssssssss',
                },
              ],
            },
            {
              group_name: 'functions',
              group_syntax: 'function',
              attributes: [
                {
                  id: '7',
                  visibility: '-',
                  edit: false,
                  name: 'apply()',
                  type: 'void',
                },
                {
                  id: '8',
                  visibility: '#',
                  edit: false,
                  name: 'promote()',
                  type: 'void',
                },
              ],
            },
          ],
        },
      ],
    };
  }
}
