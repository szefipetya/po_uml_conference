import { HttpErrorResponse } from '@angular/common/http';
import { renderFlagCheckIfStmt } from '@angular/compiler/src/render3/view/template';
import { AfterContentChecked, AfterContentInit, AfterViewInit, Component, ElementRef, EventEmitter, Inject, Input, OnChanges, OnInit, Output, SimpleChange, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ClientModel } from '../../models/Diagram/ClientModel';
import { FileClientModel } from '../../models/management/FileClientModel';
import { File_cDto } from '../../models/management/File_cDto';
import { ICON } from '../../models/management/ICON';
import { ProjectFileDto } from '../../models/management/project/ProjectFileDto';
import { ProjectFileHeaderDto } from '../../models/management/project/ProjectFileHeaderDto';
import { ProjectFolderDto } from '../../models/management/project/projectFolderDto';
import { FileResponse } from '../../models/management/response/FileResponse';
import { RequestEvent } from '../left-panel-component.component';
import { FileManagerService } from '../service/file-manager.service';
import { FileShareRequest } from "../../models/management/request/FileShareRequest";
@Component({
  selector: 'app-file',
  templateUrl: './file.component.html',
  styleUrls: ['./file.component.scss']
})
export class FileComponent implements OnInit, AfterViewInit {
  @Input() model: any;
  @Input() renderMode: string;
  nestedFiles: ProjectFileHeaderDto[];
  iconLink: string = "";
  nested_opened: boolean = false;
  @ViewChild('input') input: ElementRef;
  constructor(private fileService: FileManagerService, private snack: MatSnackBar, public dialog: MatDialog) { }
  @Output() focusEvent: EventEmitter<{ name: string, type: string }> = new EventEmitter<{ name: string, type: string }>();
  @Output() destroyEvent: EventEmitter<string> = new EventEmitter<string>();
  @Output() dblClickEvent: EventEmitter<File_cDto> = new EventEmitter<File_cDto>();
  @Output() clickEvent: EventEmitter<FileComponent> = new EventEmitter<FileComponent>();
  @Output() requestEvent: EventEmitter<RequestEvent> = new EventEmitter<RequestEvent>();
  ngOnInit(): void {
    this.model.viewModel = this;
    if (!this.model.clientModel) this.model.clientModel = new FileClientModel();
    this.input?.nativeElement.focus();
    switch (this.model.icon) {
      case ICON.FOLDER: this.iconLink = "../../../../assets/svg/management/folder_normal.svg"; break;
      case ICON.PROJECT: this.iconLink = "../../../../assets/svg/management/folder_project.svg"; break;
      case ICON.PROJECT_FOLDER: this.iconLink = "../../../../assets/svg/management/folder_normal.svg"; break;
    }
  }
  ngAfterViewInit() {
    this.input?.nativeElement.focus();
    console.log('filemodel', this.model)
  }
  onClick(e) {
    this.clickEvent.emit(this.model);
  }
  onKeyPress(e: KeyboardEvent) {

    if (e.key == "Enter" && this.input.nativeElement.value.trim().length > 0) {
      this.save(this.input.nativeElement.value)
    }
    if (e.key == "Escape" || e.key == 'Esc') {
      this.destroySelf();
    }
  }
  dblClick(e) {
    this.dblClickEvent.emit(e);
  }
  openNestedTree() {
    this.nested_opened = !this.nested_opened;
    this.fileService.getFile(this.model.id, this.model._type).subscribe((r: FileResponse) => {
      console.log('resp', r); if (r.file._type == 'projectFolderDto') {
        this.nestedFiles = (r.file as ProjectFolderDto).files;
        console.log(this.nestedFiles)
      }
    });
  }
  deleteRecursiveIfSelected() {
    if (this.model.clientModel.selected) {

    }
    if (this.nestedFiles) {

      this.nestedFiles.map(n => {
        if (n.clientModel.selected) {
          this.fileService.deleteFile(n.id, n._type)
            .pipe(catchError(this.handleError<FileResponse>(this, 'getFile', null)))
            .subscribe((r) => {
              if (r?.file._type) {
                this.openNestedTree();
              }
              console.log('file arrived', r)
              //if (r) this.snack.open("file could not be deleted", "Delete");
            })
        }
      });
    }
  }

  private handleError<T>(view: FileComponent, operation = 'operation', result?: T) {
    return (error: HttpErrorResponse): Observable<T> => {
      // TODO: send the error to remote logging infrastructure
      console.error(error); // log to console instead

      view.snack.open(JSON.stringify(error.error.error), "Error", { duration: 2000 });
      // TODO: better job of transforming error for user consumption
      console.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
  deselectNested() {
    this.model.clientModel.selected = false;
    if (this.nestedFiles)
      this.nestedFiles.map(n => { n.viewModel.deselectNested() })
  }
  onfocusOut(e) {
    console.log(this.model)
    this.save(e.target.value);
  }
  save(name) {
    if (this.model.clientModel.edit) {
      console.log(name);
      this.model.name = name;
      this.model.clientModel.edit = false;
      this.focusEvent.emit({ name: this.model.name, type: this.model._type });
    }
  }
  destroySelf() {
    this.destroyEvent.emit('destroy');
  }
  openShareDialog(): void {
    const dialogRef = this.dialog.open(ShareDialogComponent, {
      width: '250px',
      data: { auth_jwt: getCookie("jwt_token"), file_id: this.model.id, target_userName: "" }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed', result);
      this.fileService.shareFile(result).pipe(catchError(this.handleError<FileResponse>(this, 'share', null)))
        .subscribe((r: FileShareRequest) => {
          if (r) {

            this.snack.open("file succesfully shared with " + r.target_userName, null, { duration: 2000 });

          }
        })
    });
  }
  validate() {

  }
  inputFocus(e) {
    console.log('loadedS')
    e.target.click()
  }


}




import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { getCookie } from 'src/app/utils/cookieUtils';




@Component({
  selector: 'dialog-share',
  templateUrl: './dialog-share.html',
})
export class ShareDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ShareDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FileShareRequest) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
