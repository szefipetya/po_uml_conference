import { Injectable, ModuleWithComponentFactories, NgModuleRef } from '@angular/core';
import {Model} from '../../../../models/Model'
@Injectable({
  providedIn: 'root'
})
export class GlobalEditorService {
model;
  constructor() {
    this.model={
      user:{userid:'001'},

      toolbox: {
        width: 120,
      },
      menubar: {
        height: 40,
      },
      canvas: {
        edit_element_id: null,
        edit_classTitle_id: null,
        scale: 1,
        posx: 0,
        posy: 0,
        width: 1400,
        height: 1000,
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
        width: 1000,
        height: 550,
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
          width: 114,
          height: 250,
          min_height: 75,
          scaledModel:{
            posx_scaled: 110,
            posy_scaled: 220,
            width_scaled: 114,
            height_scaled: 250,
            min_height_scaled: 75
          },
          z: 2,
          edit: false,
          name: 'Class1',
          class_type: 'classDG',
          groups: [
            {
              group_name: 'attributes',
              group_syntax: 'attribute',
              attributes: [
                {
                  id: 1,
                  visibility: '+',
                  edit: false,
                  name: 'attr1',
                  type: 'int',
                },
                {
                  id: 2,
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
                  id: 3,
                  visibility: '-',
                  edit: false,
                  name: 'func1',
                  type: 'int',
                },
                {
                  id: 4,
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
          width: 114,
          height: 250,
          min_height: 75,
          scaledModel:{
            posx_scaled: 310,
            posy_scaled: 420,
            width_scaled: 114,
            height_scaled: 250,
            min_height_scaled: 75
          },
          z: 1,
          edit: false,
          name: 'Person',
          class_type: 'classDG',
          groups: [
            {
              group_name: 'attributes',
              group_syntax: 'attribute',
              attributes: [
                {
                  id: 5,
                  visibility: '+',
                  edit: false,
                  name: 'name',
                  type: 'string',
                },
                {
                  id: 6,
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
                  id: 7,
                  visibility: '-',
                  edit: false,
                  name: 'apply()',
                  type: 'void',
                },
                {
                  id: 8,
                  visibility: '#',
                  edit: false,
                  name: 'promote()',
                  type: 'void',
                },
              ],
            },
          ],
        },
      ]
    };
  }


}


