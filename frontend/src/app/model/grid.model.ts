import { Cell } from "./cell.model";

export interface Grid {
  id?: number;
  numberOfRows: number;
  numberOfColumns: number;
  cells?: Cell[]
}
