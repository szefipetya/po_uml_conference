import { HttpClient } from '@angular/common/http';
import {
  Injectable,
  ModuleWithComponentFactories,
  NgModuleRef,
} from '@angular/core';
import { Observable, of } from 'rxjs';
import { Diagram } from 'src/app/components/models/Diagram/Diagram';
import { Element_c } from 'src/app/components/models/DiagramObjects/Element_c';
import { GROUP_SYNTAX } from 'src/app/components/models/DiagramObjects/GROUP_SYNTAX';
import { Rect } from 'src/app/components/models/DiagramObjects/Rect';
import { LINE_TYPE } from 'src/app/components/models/line/LINE_TYPE';
import { CanvasBoxComponent } from '../../canvas-box/canvas-box.component';
import { LineCanvasComponent } from '../../canvas-box/line-canvas/line-canvas.component';
import { catchError, map, tap } from 'rxjs/operators';
import { ClientModel } from 'src/app/components/models/Diagram/ClientModel';
@Injectable({
  providedIn: 'root',
})
export class GlobalEditorService {
  model: Diagram;
  alignment;
  url_pre = 'http://localhost:8080/';
  url_pre_test = 'https://jsonplaceholder.typicode.com/posts';
  url_get_diagram = 'get/dg';
  clientModel: ClientModel;
  constructor(private http: HttpClient) {
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
    this.clientModel = {
      lineCanvas: {
        drawLineType: null,
        width:
          window.innerWidth -
          this.alignment.left_dock.width -
          this.alignment.right_dock.width,
        height: window.innerHeight - this.alignment.bottom_dock.height,
      },
      canvas: {
        selectedClass: null,
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
        drawRect: null,
        selectedClassIds: ['c1'],
        clip: {
          width: 0,
          height: 0,
        },
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
    };

    //this.init();
  }
  init() {
    this.clientModel = {
      lineCanvas: {
        drawLineType: null,
        width:
          window.innerWidth -
          this.alignment.left_dock.width -
          this.alignment.right_dock.width,
        height: window.innerHeight - this.alignment.bottom_dock.height,
      },
      canvas: {
        selectedClass: null,
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
        drawRect: null,
        selectedClassIds: ['c1'],
        clip: {
          width: 0,
          height: 0,
        },
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
    };
    this.model = {
      owner: {
        id: '001',
        username: 'test',
        email: 'example@hu.hu',
        name: 'test',
      },

      lines: [],

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
            viewModel: null,
          },
          groups: [
            {
              group_name: 'attributes',
              group_syntax: GROUP_SYNTAX.ATTRIBUTE,
              attributes: [
                {
                  id: '1',
                  visibility: '+',
                  edit: false,
                  name: 'attr1',
                  type: 'int',
                  viewModel: null,
                },
                {
                  id: '2',
                  visibility: '+',
                  edit: false,
                  name: 'attr2',
                  type: 'int',
                  viewModel: null,
                },
              ],
            },
            {
              group_name: 'functions',
              group_syntax: GROUP_SYNTAX.FUNCTION,
              attributes: [
                {
                  id: '3',
                  visibility: '-',
                  edit: false,
                  name: 'func1',
                  type: 'int',
                  viewModel: null,
                },
                {
                  id: '4',
                  visibility: '#',
                  edit: false,
                  name: 'func2',
                  type: 'int',
                  viewModel: null,
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
            viewModel: null,
          },
          groups: [
            {
              group_name: 'attributes',
              group_syntax: GROUP_SYNTAX.ATTRIBUTE,
              attributes: [
                {
                  id: '5',
                  visibility: '+',
                  edit: false,
                  name: 'name',
                  type: 'string',
                  viewModel: null,
                },
                {
                  id: '6',
                  visibility: '#',
                  edit: false,
                  name: 'age',
                  type: 'intssssssssssssssssssssssssssssssssssssssssssssssss',
                  viewModel: null,
                },
              ],
            },
            {
              group_name: 'functions',
              group_syntax: GROUP_SYNTAX.FUNCTION,
              attributes: [
                {
                  id: '7',
                  visibility: '-',
                  edit: false,
                  name: 'apply()',
                  type: 'void',
                  viewModel: null,
                },
                {
                  id: '8',
                  visibility: '#',
                  edit: false,
                  name: 'promote()',
                  type: 'void',
                  viewModel: null,
                },
              ],
            },
          ],
        },
      ],
    };
  }

  public async initFromServer() {
    let response = await this.getDiagramFromServer('001');
    console.log(response);

    console.log({ ...response });
    this.model = { ...response };
    console.log(JSON.stringify(this.model));
  }
  getDiagramFromServer(id: string): Promise<Diagram> {
    return this.http
      .get<Diagram>(this.url_pre + this.url_get_diagram + '/' + id)
      .pipe(catchError(this.handleError<Diagram>('getDiagram', new Diagram())))
      .toPromise();
  }
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
