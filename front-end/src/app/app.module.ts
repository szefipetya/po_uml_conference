//Angular core
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

//Material
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';

//custom source
import { MainComponent } from './components/main/main.component';
import { TopMenuComponent } from './components/top-menu/top-menu.component';
import { LeftSideMenuComponent } from './components/left-side-menu/left-side-menu.component';
import { EditorComponent } from './components/full-page-components/editor/editor.component';
import { CanvasBoxComponent } from './components/full-page-components/editor/canvas-box/canvas-box.component';
import { AttributeGroupComponent } from './components/full-page-components/editor/simple-class/attribute-group/attribute-group.component';
import { SimpleClassComponent } from './components/full-page-components/editor/simple-class/simple-class.component';
import { AttributeComponent } from './components/full-page-components/editor/simple-class/attribute-group/attribute/attribute.component';
import { EditorRootComponent } from './components/full-page-components/editor/editor-root/editor-root.component';
import { WindowComponent } from './components/windows/windowed-components/window/window.component';
import { DockableWindowComponent } from './components/windows/windowed-components/dockable-window/dockable-window.component';
import { FixedWindowComponent } from './components/windows/windowed-components/fixed-window/fixed-window.component';
import { WindowManagerComponent } from './components/windows/window-manager/window-manager.component';
import { ToolBoxWindowComponent } from './components/windows/windowed-components/_instances/dockable-window/tool-box-window/tool-box-window.component';

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    TopMenuComponent,
    LeftSideMenuComponent,
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
  ],
  imports: [
    //Angular core
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    //Material
    MatToolbarModule,
    MatIconModule,
    MatSidenavModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
