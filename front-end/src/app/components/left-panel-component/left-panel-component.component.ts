import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, Input, OnInit, Output } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of, Subject, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { getCookie, setCookie } from 'src/app/utils/cookieUtils';
import { endP, environment } from 'src/environments/environment';
import { FolderDto } from "../models/management/FolderDto";
import { ProjectFolderDto } from "../models/management/project/projectFolderDto";
import { FolderHeaderDto } from "../models/management/FolderHeaderDto";
import { EventEmitter } from '@angular/core';
import { File_cDto } from '../models/management/File_cDto';
import { FileResponse } from '../models/management/response/FileResponse';
import { FileClientModel } from '../models/management/FileClientModel';
import { FileHeaderDto } from '../models/management/FileHeaderDto';
import { disableDebugTools } from '@angular/platform-browser';
import { ClientModel } from '../models/Diagram/ClientModel';
import { FileManagerService } from "./service/file-manager.service";

import { ICON } from '../models/management/ICON';
import { GlobalEditorService } from '../full-page-components/editor/services/global-editor/global-editor.service';
import { FileComponent } from './file/file.component';
export class RequestEvent {
  constructor(alias, id, _type) {
    this.alias = alias;
    this.id = id;
    this._type = _type;
  }
  alias: string;
  id: number;
  _type: string;
}
@Component({
  selector: 'app-left-panel-component',
  templateUrl: './left-panel-component.component.html',
  styleUrls: ['./left-panel-component.component.scss']
})
export class LeftPanelComponentComponent implements OnInit {
  actualFolder: FolderDto;
  constructor(private http: HttpClient,
    private snackBar: MatSnackBar,
    private editorService: GlobalEditorService,
    private fileService: FileManagerService) {

    editorService.addListenerToEvent(this, (t) => {
      t.getDgInNext = false;
      t.onrefreshClick(false);
    }, 'update');
  }
  private eventsSubscription: Subscription;
  public errorMsg = "--";
  isShiftDown: boolean;
  isCtrlDown: boolean;
  getDgInNext = true;
  /* updateEventToChild: Subject<void> = new Subject<void>();

   emitEventToChild() {
     this.eventsSubject.next();
   }*/

  @Output() clickEvent = new EventEmitter();
  @Input() events: Observable<{ str: string, extra: any }>;
  ngOnInit() {
    this.eventsSubscription = this.events
      .subscribe((evt) => {
        if (evt.str == 'panel-click') {
          this.containerClick(evt.extra)
        }

        else if (evt.str == 'toggle') {
          if (getCookie('actual_folder')) {
            let f = JSON.parse(getCookie('actual_folder'));
            this.getFile(f.id, f._type, false);
          } else
            this.getRootFolder();
        }
        else if (evt.str == 'keydown') {
          this.onKeyDown(evt.extra)
        }
        else if (evt.str == 'keyup') {
          this.onKeyUp(evt.extra)
        }
      })
  }
  actualPath: FileHeaderDto[];
  private setActualFolderFromResponse(r: FileResponse) {
    if (r.pathFiles) {
      r.pathFiles = r.pathFiles.sort((b, a) => a.index - b.index);
      this.actualPath = r.pathFiles.map(f => { return f.file; })
    }
    this.actualFolder = r.file as FolderDto;
    this.actualFolder.clientModel = new FileClientModel();
    this.actualFolder.files.map(f => { f.clientModel = new FileClientModel() })
    setCookie('actual_folder', JSON.stringify({ id: this.actualFolder.id, _type: this.actualFolder._type }), 8);
  }


  containerClick(e) {
    console.log(e.target)
    if (!e.target.classList.contains('file') && !e.target.classList.contains('icon-square'))
      this.unselectAll();
  }

  fileClick(file: File_cDto) {
    console.log("file click")
    /* file.clientModel.selected = true;
     file.name = "--"
     let cm = file.clientModel;
     file.clientModel = JSON.parse(JSON.stringify(file.clientModel));*/
    // this.unselectAll();
    this.actualFolder.files.map(f => { f.viewModel.deselectNested(); })

    file.clientModel.selected = true;
    //   this.actualFolder.files.find(f => f.id == file.id).clientModel.selected = true;
    //  this.actualFolder.files = Object.assign([], this.actualFolder.files);

  }

  onKeyDown(e: KeyboardEvent) {
    console.log('down', e)
    if (e.key == 'Shift') {
      this.isShiftDown = true;
    }
    if (e.key == 'Control') {
      this.isCtrlDown = true;
    }
  }
  onKeyUp(e: KeyboardEvent) {
    if (e.key == 'Shift') {
      this.isShiftDown = false;
    }
    if (e.key == 'Control') {
      this.isCtrlDown = false;
    }

  }
  fileDblClick(file: File_cDto) {
    if (file.id) this.getFile(file.id, file._type, true);

  }

  onCreateFolderClick() {
    let folder = new FolderHeaderDto();
    folder.clientModel.edit = true;
    folder._type = 'folder';
    this.actualFolder.files.push(folder);
    this.recentlyAddedFile = folder
    console.log("file pushed")
  }
  onCreateProjectClick() {
    let folder = new FileHeaderDto();
    folder.clientModel.edit = true;
    folder._type = 'project';
    folder.icon = ICON.PROJECT
    this.actualFolder.files.push(folder);
    this.recentlyAddedFile = folder
    console.log("file pushed")
  }
  onDeleteClick() {

    this.actualFolder.files.map(f => {
      if (f.clientModel.selected)
        this.deleteFile(f.id, f._type);
      else
        f.viewModel.deleteRecursiveIfSelected();
    })
  }
  fileFocusEventHandler(param) {
    if (this.actualFolder._type == 'projectFolderDto') {
      this.createProjectFolderToActual(param.name);
    } else
      if (param.type == 'folder') {
        this.createFolderToActual(param.name);
      } else if (param.type == 'project') {
        this.createProjectToActual(param.name);
      }
    console.log('actual files', this.actualFolder.files)
  }
  recentlyAddedFile: any = null;
  deleteRecentlyAddedFile() {
    if (this.recentlyAddedFile) {
      this.actualFolder.files = this.actualFolder.files.filter(f => f != this.recentlyAddedFile);
      this.recentlyAddedFile = null;
    }

  }
  fileDestroyHandler(param) {
    console.log('refilter')
    this.actualFolder.files.filter(f => !f.clientModel?.edit)

  }
  createProjectToActual(name: string) {
    this.http
      .get<FileResponse>(environment.api_url_http + endP.project_management + endP.create_project + this.actualFolder.id + "?name=" + name.trim(), {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      })
      .pipe(catchError(this.handleError<FileResponse>(this, 'getDiagram', null)))
      .subscribe((r) => {
        console.log('folder arrived', r)
        if (r)
          this.setActualFolderFromResponse(r)
      })
  }
  createFolderToActual(name: string) {
    this.http
      .get<FileResponse>(environment.api_url_http + endP.management + endP.create_folder + this.actualFolder.id + "?name=" + name.trim(), {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      })
      .pipe(catchError(this.handleError<FileResponse>(this, 'createFolder', null)))
      .subscribe((r) => {
        console.log('folder arrived', r)
        if (r)
          this.setActualFolderFromResponse(r)
      })
  }
  createProjectFolderToActual(name: string) {
    this.http
      .get<FileResponse>(environment.api_url_http + endP.project_management + endP.create_folder + this.actualFolder.id + "?name=" + name.trim(), {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      })
      .pipe(catchError(this.handleError<FileResponse>(this, 'createFolder', null)))
      .subscribe((r) => {
        console.log('folder arrived', r)
        if (r)
          this.setActualFolderFromResponse(r)
      })
  }
  deleteFile(id, _type) {
    this.fileService.deleteFile(id, _type)
      .pipe(catchError(this.handleError<FileResponse>(this, 'delFile', null)))
      .subscribe((r) => {
        if (r?.file._type) {
          this.getFile(r.file.id, r.file._type, true)
        }
        console.log('file arrived', r)
        if (r) this.errorMsg = '';
      })
  }
  getFile(id, _type, refreshCanvas) {
    this.fileService.getFile(id, _type)
      .pipe(catchError(this.handleError<FileResponse>(this, 'getFile', null)))
      .subscribe((r) => {
        if (r?.file._type) {
          if (r.file._type == 'projectFolderDto') {

            this.setActualProjectFolder(r, refreshCanvas);
          }
          else
            this.setActualFolderFromResponse(r);
        }
        console.log('file arrived', r)
        if (r) this.errorMsg = '';
      })
  }
  getRootFolder() {
    console.log("req sent for root")
    this.http
      .get<FileResponse>(environment.api_url_http + endP.management + endP.user_root_folder, {
        headers: { 'Authorization': 'Bearer ' + getCookie("jwt_token") }
      })
      .pipe(catchError(this.handleError<FileResponse>(this, 'getRootFolder', null)))
      .subscribe((r) => {
        if (r) {
          console.log('folder arrived', r)
          this.setActualFolderFromResponse(r)


        }
      })
  }
  onrefreshClick(makeDgRefresh) {
    if (this.actualFolder)
      this.getFile(this.actualFolder.id, this.actualFolder._type, makeDgRefresh)
  }

  latestFileRequestFailed = false;
  private handleError<T>(view: LeftPanelComponentComponent, operation = 'operation', result?: T) {
    return (error: HttpErrorResponse): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead
      if (error.error.errorMsg) {
        view.errorMsg = error.error.errorMsg;
        if (error.error.file._type == 'projectFolderDto') {
          this.setActualProjectFolder(error.error, false);
        }
      }
      else view.errorMsg = error.error;
      if (error.error.error == "Forbidden") { view.errorMsg += ". Log in to access your files!" }
      this.deleteRecentlyAddedFile();
      view.snackBar.open(view.errorMsg, "Error", { duration: 2000 });
      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);

    };
  }
  isAuthenticated() {
    return getCookie('jwt_token');
  }

  setActualProjectFolder(r: FileResponse, refreshCanvas) {
    console.log('this is a project folder', r, this.getDgInNext);
    if (refreshCanvas)
      this.editorService.initFromServer((r.file as ProjectFolderDto).relatedDiagramId);

    this.setActualFolderFromResponse(r);
  }

  fileGoBack() {
    if (this.actualFolder && this.actualFolder.parentFolder_id != null) {
      if (this.actualFolder._type == 'projectFolder') {
        this.getFile(this.actualFolder.parentFolder_id, 'folder', true);
      } else
        this.getFile(this.actualFolder.parentFolder_id, this.actualFolder._type, true);
    }
  }
  unselectAll() {

    if (!this.isCtrlDown && this.actualFolder)
      this.actualFolder.files.map(f => f.clientModel.selected = false)
  } ngOnDestroy() {
    this.eventsSubscription.unsubscribe();
  }
}
