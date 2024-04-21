export interface Cell {
  id: number;
  gridId: number,
  rowIndex: number,
  columnIndex: number,
  value?: number | string,
  effect?: string,
  highlighted?: boolean
}
