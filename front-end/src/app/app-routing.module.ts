import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {MainComponent} from '../app/components/main/main.component'
import { EditorComponent } from './components/full-page-components/editor/editor.component';

const routes: Routes = [

  { path: '', component: EditorComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
