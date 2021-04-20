import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { MainComponent } from '../app/components/main/main.component'
import { EditorComponent } from './components/full-page-components/editor/editor.component';
import { HomeComponentComponent } from './components/full-page-components/home-component/home-component.component';
import { WindowManagerComponent } from './components/windows/window-manager/window-manager.component';

const routes: Routes = [
  { path: 'editor', component: WindowManagerComponent },
  { path: 'home', component: HomeComponentComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
