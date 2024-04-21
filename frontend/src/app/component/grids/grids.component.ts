import { Component } from '@angular/core';
import {Observable} from "rxjs";
import {Grid} from "../../model";
import {GridService} from "../../service/grid.service";

@Component({
  selector: 'app-grids',
  templateUrl: './grids.component.html',
  styleUrl: './grids.component.scss'
})
export class GridsComponent {
  grids$: Observable<Grid[]> | undefined

  constructor(private readonly gridService: GridService) {
    this.grids$ = this.gridService.getAllGrid()
  }

  initialize() {
    // const grid: Grid = {
    //   numberOfRows: rows,
    //   numberOfColumns: columns
    // }
    // this.gridService.createGrid(grid).pipe(take(1)).subscribe((grid: Grid) => {
    //   this.numberOfRows = grid.numberOfRows;
    //   this.numberOfColumns = grid.numberOfColumns;
    // });
  }
}
