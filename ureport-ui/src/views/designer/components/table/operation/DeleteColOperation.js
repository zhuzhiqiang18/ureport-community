/**
 * Created by Jacky.Gao on 2017-02-17.
 */
import {
  resetTableData,
  buildNewCellDef,
  undoManager
} from '../../Utils.js'

export function doDeleteCol() {
  const selected = this.getSelected()
  const context = this.context
  if (!selected) {
    return
  }
  let startCol = selected[1]
  let endCol = selected[3]
  if (endCol < startCol) {
    let tempStartCol = startCol
    startCol = endCol
    endCol = tempStartCol
  }
  let colWidths = this.getSettings().colWidths
  let mergeCells = this.getSettings().mergeCells
  let oldMergeCells = []
  let newMergeCells = mergeCells.concat([])
  for (let mergeItem of mergeCells) {
    oldMergeCells.push(Object.assign({}, mergeItem))
    let col = mergeItem.col
    let colspan = mergeItem.colspan
    let colEnd = col + colspan - 1
    let index = newMergeCells.indexOf(mergeItem)
    if (col >= startCol && colEnd <= endCol) {
      newMergeCells.splice(index, 1);
    } else if (col <= startCol && colEnd >= endCol) {
      let span = endCol - startCol + 1
      let leftSpan = colspan - span
      if (leftSpan === 0) {
        leftSpan = 1
      }
      if (leftSpan === 1 && mergeItem.rowspan === 1) {
        newMergeCells.splice(index, 1)
      } else {
        newMergeCells[index] = {
          col: col,
          row: mergeItem.row,
          rowspan: mergeItem.rowspan,
          colspan: leftSpan
        }
      }
    } else if (col > endCol) {
      let totalCols = endCol - startCol + 1
      newMergeCells[index] = {
        row: mergeItem.row,
        col: col - totalCols,
        rowspan: mergeItem.rowspan,
        colspan: mergeItem.colspan
      }
    }
  }
  this.updateSettings({
    mergeCells: []
  })
  let dif = endCol - startCol + 1
  let oldColWidths = colWidths.concat([])
  let newColWidths = colWidths.concat([])
  newColWidths.splice(startCol, dif)
  let countRows = this.countRows()
  let removeCells = []
  for (let i = endCol; i >= startCol; i--) {
    this.alter('remove_col', i)
    for (let j = 0; j < countRows; j++) {
      let cell = context.getCell(j, i)
      if (cell) {
        context.removeCell(cell)
        removeCells.push(cell)
      }
    }
  }
  let cellsMap = context.cellsMap
  let changeCells = []
  for (let cell of cellsMap.values()) {
    let colIndex = cell.columnNumber - 1
    if (colIndex >= endCol) {
      changeCells.push(cell)
    }
  }
  for (let cell of changeCells) {
    context.removeCell(cell)
  }
  for (let cell of changeCells) {
    cell.columnNumber = cell.columnNumber - dif
    context.addCell(cell)
  }
  this.updateSettings({
    colWidths: newColWidths,
    mergeCells: newMergeCells
  })
  resetTableData(this)
  const _this = this
  undoManager.add({
    redo: function() {
      colWidths = _this.getSettings().colWidths
      mergeCells = _this.getSettings().mergeCells
      oldMergeCells = []
      newMergeCells = mergeCells.concat([])
      for (let mergeItem of mergeCells) {
        oldMergeCells.push(Object.assign({}, mergeItem))
        let col = mergeItem.col
        let colspan = mergeItem.colspan
        let colEnd = col + colspan - 1
        let index = newMergeCells.indexOf(mergeItem)
        if (col >= startCol && colEnd <= endCol) {
          newMergeCells.splice(index, 1)
        } else if (col <= startCol && colEnd >= endCol) {
          let span = endCol - startCol + 1
          let leftSpan = colspan - span
          if (leftSpan === 0) {
            leftSpan = 1
          }
          if (leftSpan === 1 && mergeItem.rowspan === 1) {
            newMergeCells.splice(index, 1);
          } else {
            newMergeCells[index] = {
              col: col,
              row: mergeItem.row,
              rowspan: mergeItem.rowspan,
              colspan: leftSpan
            }
          }
        } else if (col > endCol) {
          let totalCols = endCol - startCol + 1
          newMergeCells[index] = {
            row: mergeItem.row,
            col: col - totalCols,
            rowspan: mergeItem.rowspan,
            colspan: mergeItem.colspan
          }
        }
      }
      _this.updateSettings({
        mergeCells: []
      })
      oldColWidths = colWidths.concat([])
      newColWidths = colWidths.concat([])
      newColWidths.splice(startCol, dif)
      countRows = _this.countRows()
      removeCells.splice(0, removeCells.length);
      for (let i = endCol; i >= startCol; i--) {
        for (let j = 0; j < countRows; j++) {
          let cell = context.getCell(j, i)
          if (cell) {
            context.removeCell(cell)
            removeCells.push(cell)
          }
        }
        _this.alter('remove_col', i)
      }
      changeCells.splice(0, changeCells.length)
      for (let cell of cellsMap.values()) {
        let colIndex = cell.columnNumber - 1
        if (colIndex >= endCol) {
          changeCells.push(cell)
        }
      }
      for (let cell of changeCells) {
        context.removeCell(cell)
      }
      for (let cell of changeCells) {
        cell.columnNumber = cell.columnNumber - dif
        context.addCell(cell)
      }
      _this.updateSettings({
        colWidths: newColWidths,
        mergeCells: newMergeCells
      });
      resetTableData(_this)
    },
    undo: function() {
      for (let i = endCol; i >= startCol; i--) {
        _this.alter('insert_col', i)
      }
      changeCells.splice(0, changeCells.length)
      for (let cell of cellsMap.values()) {
        let colIndex = cell.columnNumber - 1
        if (colIndex >= startCol) {
          changeCells.push(cell)
        }
      }
      for (let cell of changeCells) {
        context.removeCell(cell)
      }
      for (let cell of changeCells) {
        cell.columnNumber = cell.columnNumber + dif
        context.addCell(cell)
      }
      for (let cell of removeCells) {
        context.addCell(cell)
      }
      _this.updateSettings({
        colWidths: oldColWidths,
        mergeCells: oldMergeCells
      })
      resetTableData(_this)
    }
  })
}
