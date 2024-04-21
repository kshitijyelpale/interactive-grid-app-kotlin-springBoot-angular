import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GridsComponent } from './component/grids/grids.component';
import {HttpClientModule} from "@angular/common/http";
import {GridComponent} from "./component/grid/grid.component";
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@NgModule({
  declarations: [
    AppComponent,
    GridsComponent,
    GridComponent
  ],
    imports: [
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        MatTable,
        MatHeaderCell,
        MatCell,
        MatColumnDef,
        MatHeaderCellDef,
        MatRowDef,
        MatRow,
        MatCellDef,
        MatHeaderRow,
        MatHeaderRowDef,
        MatProgressSpinner
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
