import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { MainComponent } from '../app/components/main/main.component'
import { EditorComponent } from './components/full-page-components/editor/editor.component';
import { WindowManagerComponent } from './components/windows/window-manager/window-manager.component';

const routes: Routes = [
  { path: 'editor', component: WindowManagerComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
