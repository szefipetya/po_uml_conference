//Angular core
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { HttpClientModule } from '@angular/common/http';

//Material
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

//custom source
import { MainComponent } from './components/main/main.component';
import { TopMenuComponent } from './components/top-menu/top-menu.component';
import { EditorComponent } from './components/full-page-components/editor/editor.component';
import { CanvasBoxComponent } from './components/full-page-components/editor/canvas-box/canvas-box.component';
import { AttributeGroupComponent } from './components/full-page-components/editor/diagram-objects/simple-class/attribute-group/attribute-group.component';
import { SimpleClassComponent } from './components/full-page-components/editor/diagram-objects/simple-class/simple-class.component';
import { AttributeComponent } from './components/full-page-components/editor/diagram-objects/simple-class/attribute-group/attribute/attribute.component';
import { EditorRootComponent } from './components/full-page-components/editor/editor-root/editor-root.component';
import { WindowComponent } from './components/windows/windowed-components/window/window.component';
import { DockableWindowComponent } from './components/windows/windowed-components/dockable-window/dockable-window.component';
import { FixedWindowComponent } from './components/windows/windowed-components/fixed-window/fixed-window.component';
import { WindowManagerComponent } from './components/windows/window-manager/window-manager.component';
import { ToolBoxWindowComponent } from './components/windows/windowed-components/_instances/editor/tool-box-window/tool-box-window.component';
import { LineCanvasComponent } from './components/full-page-components/editor/canvas-box/line-canvas/line-canvas.component';
import { SocketCommunicationWindowComponent } from './components/windows/windowed-components/_instances/editor/socket-communication-window/socket-communication-window.component';
import { NoteBoxComponent } from './components/full-page-components/editor/diagram-objects/note-box/note-box.component';
import { DiagramObjectComponent } from './components/full-page-components/editor/diagram-objects/diagram-object/diagram-object.component';
import { SessionMessageWindowComponent } from './components/windows/windowed-components/_instances/editor/session-message-window/session-message-window.component';
import { LeftPanelComponentComponent } from './components/left-panel-component/left-panel-component.component';
import { AuthModule } from "src/app/auth/auth.module";
import { RouterModule } from '@angular/router';
import { FileComponent } from './components/left-panel-component/file/file.component';
import { HomeComponentComponent } from './components/full-page-components/home-component/home-component.component';
import { FileManagerService } from './components/left-panel-component/service/file-manager.service';
import { PackageObjectComponent } from './components/full-page-components/editor/diagram-objects/package-object/package-object.component';
import { ShareDialogComponent } from "./components/left-panel-component/file/file.component";
@NgModule({
  declarations: [ShareDialogComponent,
    AppComponent,
    MainComponent,
    TopMenuComponent,
    EditorComponent,
    CanvasBoxComponent,
    AttributeGroupComponent,
    SimpleClassComponent,
    AttributeComponent,
    EditorRootComponent,
    WindowComponent,
    DockableWindowComponent,
    FixedWindowComponent,
    WindowManagerComponent,
    ToolBoxWindowComponent,
    LineCanvasComponent,
    SocketCommunicationWindowComponent,
    NoteBoxComponent,
    DiagramObjectComponent,
    SessionMessageWindowComponent,
    LeftPanelComponentComponent,
    FileComponent,
    HomeComponentComponent,
    PackageObjectComponent,
  ],
  imports: [
    //Angular core

    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule, FormsModule,
    //Material
    MatInputModule, MatButtonModule,
    MatToolbarModule, MatDialogModule,
    MatIconModule,
    MatSidenavModule, MatFormFieldModule,
    //Custom
    AuthModule, RouterModule
  ],
  providers: [FileManagerService],
  bootstrap: [AppComponent],
})
export class AppModule { }
