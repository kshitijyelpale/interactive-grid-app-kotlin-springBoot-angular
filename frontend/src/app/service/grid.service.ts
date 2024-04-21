import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Grid, Cell } from "../model";

@Injectable({
  providedIn: 'root',
})
export class GridService {

  private apiRoot = 'http://localhost:9090/grid';

  constructor(private readonly http: HttpClient) { }

  getAllGrid(): Observable<Grid[]> {
    return this.http.get<Grid[]>(this.apiRoot);
  }

  getGridState(gridId: number): Observable<Grid> {
    return this.http.get<Grid>(`${this.apiRoot}/${gridId}`);
  }

  createGrid(grid: Grid): Observable<Grid> {
    return this.http.post<Grid>(this.apiRoot, grid);
  }

  createGridCell(cell: Cell): Observable<Cell[]> {
    return this.http.post<Cell[]>(`${this.apiRoot}/cell`, cell);
  }

  getCellState(cellId: number): Observable<Cell> {
    return this.http.get<Cell>(`${this.apiRoot}/cell/${cellId}`);
  }

  updateCellState(cellId: number): Observable<Cell[]> {
    return this.http.put<Cell[]>(`${this.apiRoot}/cell/${cellId}`, {});
  }

  deleteCellState(cellId: number): Observable<boolean> {
    return this.http.delete<boolean>(`${this.apiRoot}/cell/${cellId}`);
  }
}
