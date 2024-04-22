import {Component, OnInit} from '@angular/core';
import {Observable, take} from "rxjs";
import {Grid} from "../../model";
import {GridService} from "../../service/grid.service";

@Component({
  selector: 'app-grids',
  templateUrl: './grids.component.html',
  styleUrl: './grids.component.scss'
})
export class GridsComponent implements OnInit{
  grids$: Observable<Grid[]> | undefined

  constructor(private readonly gridService: GridService) {
  }

  ngOnInit() {
    this.loadGridData();
  }

  initialize() {
    const grid: Grid = {
      numberOfRows: 50,
      numberOfColumns: 50
    }
    this.gridService.createGrid(grid).pipe(take(1)).subscribe(() => {
      this.loadGridData();
    });
  }

  loadGridData() {
    this.grids$ = this.gridService.getAllGrid();
  }
}
