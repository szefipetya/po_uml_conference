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
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { User } from 'src/app/components/models/User';
import { DiagramObject } from 'src/app/components/models/DiagramObjects/DiagramObject';
import { Pair } from '../../Utils/utils';
import { DiagramObject_Scaled } from 'src/app/components/models/DiagramObjects/DiagramObject_Scaled';
@Injectable({
  providedIn: 'root',
})
export class GlobalEditorService {
  deleteGlobalObject(model: DiagramObject) {
    this.model.dgObjects = this.model.dgObjects.filter(
      (obj) => obj.id != model.id
    );
  }
  canvasBox: CanvasBoxComponent;
  hasGlobalObject(model: DiagramObject): boolean {
    return this.model.dgObjects.find((o) => o.id == model?.id) != null;
  }
  hasGlobalObjectById(id: string): boolean {
    return this.model.dgObjects.find((o) => o.id == id) != null;
  }
  createGlobalObject(model: DiagramObject) {
    this.model.dgObjects.push(model);
  }
  model: Diagram;
  alignment;
  // url_pre = 'http://84.2.193.197:8101/';
  url_pre = 'http://localhost:8101/';
  url_pre_test = 'https://jsonplaceholder.typicode.com/posts';
  url_get_diagram = 'get/dg';
  clientModel: ClientModel;

  user: User = {
    id: new Date().getUTCMilliseconds().toString(),
    email: 'email',
    username: 'username',
    name: 'name',
  };
  constructor(private http: HttpClient) {
    this.model = new Diagram();

    console.log('id ', this.user.id);
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
        selectedClassIds: [],
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
  namespace(str, scope) {
    return scope;
  }

  public async initFromServer() {
    let response = await this.getDiagramFromServer('001');
    console.log(response);

    console.log({ ...response });
    this.model = { ...response };
    this.model.dgObjects.map((dg) => {
      dg.scaledModel = new DiagramObject_Scaled();
      dg.scaledModel.posx_scaled = dg.dimensionModel.x;
      dg.scaledModel.posy_scaled = dg.dimensionModel.y;
      dg.scaledModel.width_scaled = dg.dimensionModel.width;
      dg.scaledModel.height_scaled = dg.dimensionModel.height;
      dg.scaledModel.min_height_scaled = this.clientModel.class_general.min_height_scaled;
    });
    console.log(JSON.stringify(this.model));
    this.afterDgFetchFunctions.map((p) => {
      p.value(p.key.value);
    });

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
  //-----------------------------//target,alias,callback with (target,model params)
  eventListenerFunctions: Pair<Pair<string, any>, Function>[] = [];
  afterDgFetchFunctions: Pair<Pair<string, any>, Function>[] = [];
  addListenerAfterDgFetch(target, fn, alias: string = '') {
    this.afterDgFetchFunctions.push(new Pair(new Pair(alias, target), fn));
  }
  public triggerEvent(wich: string) {

    this.eventListenerFunctions.map((p) => {
      if (p.key.key == wich)
        p.value(p.key.value);
    });

  }
  addListenerToEvent(target, fn, alias: string = '') {
    this.eventListenerFunctions.push(new Pair(new Pair(alias, target), fn));
  }

  init(): Diagram {
    return new Diagram();
  }
}
