import { AfterContentChecked, AfterContentInit, AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChange, ViewChild } from '@angular/core';
import { of } from 'rxjs';
import { File_cDto } from '../../models/management/File_cDto';
import { ICON } from '../../models/management/ICON';

@Component({
  selector: 'app-file',
  templateUrl: './file.component.html',
  styleUrls: ['./file.component.scss']
})
export class FileComponent implements OnInit, AfterViewInit {
  @Input() model: File_cDto;
  @Input() renderMode: string;
  iconLink: string = "";
  @ViewChild('input') input: ElementRef;
  constructor() { }
  @Output() focusEvent: EventEmitter<{ name: string, type: string }> = new EventEmitter<{ name: string, type: string }>();
  @Output() destroyEvent: EventEmitter<string> = new EventEmitter<string>();
  ngOnInit(): void {
    this.input?.nativeElement.focus();
    switch (this.model.icon) {
      case ICON.FOLDER: this.iconLink = "../../../../assets/svg/management/folder_normal.svg"; break;
      case ICON.PROJECT: this.iconLink = "../../../../assets/svg/management/folder_project.svg"; break;
      case ICON.PROJECT_FOLDER: this.iconLink = "../../../../assets/svg/management/folder_normal.svg"; break;
    }
  }
  ngAfterViewInit() {
    this.input?.nativeElement.focus();
  }

  onKeyPress(e: KeyboardEvent) {

    if (e.key == "Enter" && this.input.nativeElement.value.trim().length > 0) {
      this.save(this.input.nativeElement.value)
    }
    if (e.key == "Escape" || e.key == 'Esc') {
      this.destroySelf();
    }
  }
  onfocusOut(e) {
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

  validate() {

  }
  inputFocus(e) {
    console.log('loadedS')
    e.target.click()
  }


}
