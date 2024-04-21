import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {GridComponent} from "./component/grid/grid.component";
import {GridsComponent} from "./component/grids/grids.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'grids',
    pathMatch: 'full'
  },
  {
    path: 'grids',
    component: GridsComponent
  },
  {
    path: 'grid/:id',
    component: GridComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
