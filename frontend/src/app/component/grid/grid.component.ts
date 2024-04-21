import {Component, HostListener, OnInit} from '@angular/core';
import {GridService} from "../../service/grid.service";
import {Cell, Grid} from "../../model";
import {finalize, take} from "rxjs";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-grid',
  templateUrl: './grid.component.html',
  styleUrl: './grid.component.scss'
})
export class GridComponent implements OnInit {
  cellWidth: string = '50px';
  cellHeight: string = '50px';
  columns: string[] = [];
  displayedColumns: string[] = [];
  gridData: Cell[][] = [];
  numberOfRows: number = 0;
  numberOfColumns: number = 0;
  gridId = 0;
  loading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly gridService: GridService
  ) {
    console.log('GridComponent');
  }

  ngOnInit() {
    this.gridId = this.route.snapshot.params['id'] as number;
    this.initializeGrid(); // Creating a 50x50 grid
  }

  @HostListener('window:resize', ['$event'])
  onResize(event?: Event) {
    this.adjustCellSize();
  }

  adjustCellSize() {
    const screenWidth = window.innerWidth;
    const screenHeight = window.innerHeight;
    console.log(screenWidth, screenHeight);
    this.cellWidth = (screenWidth / 50) + 'px';
    this.cellHeight = (screenWidth / 50) + 'px';
  }

  initializeGrid() {
    this.gridService.getGridState(this.gridId).pipe(take(1)).subscribe((grid: Grid) => {
      this.numberOfRows = grid.numberOfRows;
      this.numberOfColumns = grid.numberOfColumns;
      const cells = grid.cells;

      // Create grid cell
      for (let i = 1; i <= this.numberOfColumns; i++) {
        this.columns.push(`Column ${i}`);
      }

      this.displayedColumns = ['rowHeader', ...this.columns];

      this.gridData = new Array(this.numberOfRows);  // Create an array to hold rows
      for (let i = 0; i < this.numberOfRows; i++) {
        this.gridData[i] = new Array(this.numberOfColumns); // Create an array to hold columns of each row
        for (let j = 0; j < this.numberOfColumns; j++) {
          const cellIndex = cells?.findIndex(c => c.rowIndex === i + 1 && c.columnIndex === j + 1);
          if (cellIndex !== -1 && cellIndex !== undefined) {
            this.gridData[i][j] = {...cells![cellIndex], effect: ''};
          } else {
            this.gridData[i][j] = {
              id: 0,
              gridId: this.gridId,
              rowIndex: i + 1,
              columnIndex: j + 1,
              value: '',
              highlighted: false
            };
          }
        }
      }
    });
  }

  onCellClick(row: Cell[], gridRowIndex: number, gridColumnIndex: number) {
    this.loading = true;
    // console.log('Cell clicked:', gridRowIndex + 1, gridColumnIndex + 1, row[gridColumnIndex].value);
    if (row[gridColumnIndex].id) {
      this.gridService.updateCellState(row[gridColumnIndex].id)
        .pipe(
          take(1),
          finalize(() => this.loading = false)
        )
        .subscribe((data) => this.updateCells(data));
    } else {
      this.gridService.createGridCell({
        id: 0,
        gridId: this.gridId,
        rowIndex: gridRowIndex + 1,
        columnIndex: gridColumnIndex + 1
      }).pipe(
        take(1),
        finalize(() => this.loading = false)
      )
        .subscribe((data) => this.updateCells(data));
    }
  }

  private updateCells(data: Cell[]) {
    data.forEach(cell => {
      // Assuming rowIndex and columnIndex are 1-based indexes from the backend
      if (this.gridData[cell.rowIndex - 1] && this.gridData[cell.rowIndex - 1][cell.columnIndex - 1]) {
        this.gridData[cell.rowIndex - 1][cell.columnIndex - 1] = cell;
        this.scheduleFiboCellEffect(cell)
        this.scheduleUpdateEffect(cell);
      }
    });
  }

  private scheduleUpdateEffect(cell: Cell) {
    setTimeout(() => {
      this.gridData[cell.rowIndex - 1][cell.columnIndex - 1].effect = '';
    }, 2000);
  }

  private scheduleFiboCellEffect(cell: Cell) {
    if (cell.effect === 'GREEN') {
      setTimeout(() => {
        this.gridData[cell.rowIndex - 1][cell.columnIndex - 1].effect = '';
        this.gridService.deleteCellState(cell.id)
          .pipe(take(1))
          .subscribe(() => {
            this.gridData[cell.rowIndex - 1][cell.columnIndex - 1] = { ...cell, id: 0, value: '' };
          });
      }, 5000);
    }
  }
}
