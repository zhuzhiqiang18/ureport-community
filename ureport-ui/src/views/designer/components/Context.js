/**
 * Created by Jacky.Gao on 2017-01-25.
 */
import {afterRenderer} from './table/CellRenderer';
import renderChart from './widget/ChartWidget.js';

export default class Context {
  constructor(reportTable){
    this.reportTable = reportTable
    this.reportDef = reportTable.reportDef
    this.hot = reportTable.hot
    this.hot.context = this
    this._initLetters()
    this.cellsMap = reportTable.cellsMap
    this.rowHeaders = []
    this.selection = [-1,-1,-1,-1]
  }

  setSelection(rowIndex, colIndex, row2Index, col2Index) {
    this.selection[0] = rowIndex
    this.selection[1] = colIndex
    this.selection[2] = row2Index
    this.selection[3] = col2Index
  }

  _initLetters(){
      const letters=["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"];
      this.LETTERS=letters.concat([]);
      for(let i=0;i<letters.length;i++){
          let name=letters[i];
          for(let j=0;j<letters.length;j++){
              this.LETTERS.push(name+letters[j]);
          }
      }
  }

  adjustInsertRowHeaders(row){
      for(let header of this.rowHeaders){
          if(header.rowNumber>=row){
              header.rowNumber+=1;
          }
      }
  }

  adjustDelRowHeaders(row){
      let targetHeader=null;
      for(let header of this.rowHeaders){
          if(header.rowNumber===row){
              targetHeader=header;
              break;
          }
      }
      if(targetHeader){
          const index=this.rowHeaders.indexOf(targetHeader);
          this.rowHeaders.splice(index,1);
      }
  }

  addRowHeader(row,band){
      let targetHeader=null;
      for(let header of this.rowHeaders){
          if(header.rowNumber===row){
              targetHeader=header;
              break;
          }
      }
      if(targetHeader){
          targetHeader.band=band;
      }else{
          const newHeader={band,rowNumber:row};
          this.rowHeaders.push(newHeader);
      }
  }

  getCellName(rowIndex,colIndex) {
    if(rowIndex!=null) {
      return this.LETTERS[colIndex]+(rowIndex+1)
    }else {
      return this.LETTERS[colIndex]
    }
  }

  getParentCellName(row, col, isLeft) {
    if(isLeft) {
      col--
    } else {
      row--
    }
    if (row < 0 || col < 0) {
      return null
    }
    let td = this.hot.getCell(row, col)
    if (td.style.display === 'none') {
      const mergeCells = this.hot.getSettings().mergeCells
      for (const item of mergeCells) {
        const rowStart = item.row
        const rowspan = item.rowspan
        const colStart = item.col
        const colspan = item.colspan
        const rowEnd = rowStart + rowspan - 1
        const colEnd = colStart + colspan - 1
        if (row >= rowStart && row <= rowEnd && col >= colStart && col <= colEnd) {
          row = rowStart
          col = colStart
          break
        }
      }
    }
    const cellName = this.getCellName(row, col)
    return cellName
  }

  getCell(rowIndex,colIndex){
    let key = (rowIndex+1) + ',' + (colIndex+1)
    return this.cellsMap.get(key)
  }

  addCell(cell){
    let key = cell.rowNumber + ',' + cell.columnNumber
    this.cellsMap.set(key,cell)
  }

  removeCell(cell){
    let key = cell.rowNumber + ',' + cell.columnNumber
    this.cellsMap.delete(key)
  }

  deleteCell(rowNumber,columnNumber) {
    let key = rowNumber + ',' + columnNumber
    this.cellsMap.delete(key)
  }

  getSelectedCells() {
    const selected = this.hot.getSelected()
    if(!selected){
      return null
    }
    const startRow = selected[0]
    const startCol = selected[1]
    const endRow = selected[2]
    const endCol = selected[3]
    const cells = []
    for(let i = startRow; i <= endRow; i++) {
      for(let j = startCol; j <= endCol; j++) {
        const cell = this.hot.getCell(i, j, true)
        const exist = cells.indexOf(cell)
        if(exist === -1) {
          cells.push(cell)
        }
      }
    }
    return cells
  }

  getSelectedCell(){
    const selected = this.hot.getSelected()
    if(!selected){
        return null
    }
    const startRow = selected[0]
    const startCol = selected[1]
    return this.getCell(startRow, startCol)
  }

  getDataSets(){
    const datasources = this.reportDef.datasources || []
    const datasets = []
    for (const i in datasources) {
      const ds = datasources[i]
      for (const j in ds.datasets) {
        const d = ds.datasets[j]
        datasets.push({
          name: d.name,
          fields: d.fields
        })
      }
    }
    return datasets
  }

  getFieldsByDataSetName(dataSetName){
    const datasources = this.reportDef.datasources || []
    const fields = []
    for (const i in datasources) {
      const ds = datasources[i]
      for (const j in ds.datasets) {
        const d = ds.datasets[j]
        if (d.name === dataSetName) {
          return d.fields
        }
      }
    }
    return fields
  }

  refresh(cellDef){
    if (cellDef.value.type === 'chart') {
      const selected = this.hot.getSelected()
      const startRow = selected[0]
      const startCol = selected[1]
      const td = this.hot.getCell(startRow,startCol)
      td.innerHTML = ''
      const container = document.createElement('div')
      container.style.width = td.clientWidth + 'px'
      container.style.height = td.clientHeight + 'px'
      td.appendChild(container)
      renderChart(container,cellDef)
    }
  }
}
