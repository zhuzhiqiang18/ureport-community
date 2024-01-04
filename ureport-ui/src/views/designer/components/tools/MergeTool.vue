<template>
  <div class="component-icon-btn" @click="handleMerge">
    <div class="component-icon-btn-icon">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><g fill="#3D4757" fill-rule="evenodd"><path d="M6 1v1H2v11h4v1H2a1 1 0 01-1-1V2a1 1 0 011-1h4zm3 0h4a1 1 0 011 1v11a1 1 0 01-1 1H9v-1h4V2H9V1z"/><path fill-rule="nonzero" d="M6 1h1v4H6zm2 0h1v4H8z"/><path d="M3 7.5L5 6v3zm9 0L10 6v3z"/><path d="M4 7h3v1H4zm4 0h3v1H8z"/><path fill-rule="nonzero" d="M8 10h1v4H8zm-2 0h1v4H6z"/></g></svg>
    </div>
  </div>
</template>

<script>
import {
  undoManager,
  buildNewCellDef
} from '../Utils.js'

export default {
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {

    }
  },
  mounted: function() {

  },
  methods: {
    handleMerge() {
      const table = this.context.hot
      const selected = table.getSelected()
      if (!selected) {
        this.$message('请选择单元格')
        return
      }
      let mergeCells = table.getSettings().mergeCells || []
      let oldMergeCells = mergeCells.concat([])
      let startRow = selected[0]
      let startCol = selected[1]
      let endRow = selected[2]
      let endCol = selected[3]
      let tmp = endRow
      if (startRow > endRow) {
        endRow = startRow
        startRow = tmp
      }
      tmp = endCol
      if (startCol > endCol) {
        endCol = startCol
        startCol = tmp
      }
      const _this = this
      doMergeCells(startRow, startCol, endRow, endCol, table, this.context)
      undoManager.add({
        redo: function() {
          mergeCells = table.getSettings().mergeCells || []
          oldMergeCells = mergeCells.concat([])
          doMergeCells(startRow, startCol, endRow, endCol, table, _this.context)
        },
        undo: function() {
          table.updateSettings({
            mergeCells: oldMergeCells
          })
        }
      })
    }
  }
}

function doMergeCells(startRow, startCol, endRow, endCol, table, context) {
  let doMerge = true
  let doSplit = false
  const selectCell = context.getCell(startRow, startCol)
  const mergeCells = table.getSettings().mergeCells || []
  for (let i = startRow; i <= endRow; i++) {
    for (let j = startCol; j <= endCol; j++) {
      const td = table.getCell(i, j)
      if (!td) {
        continue
      }
      const colSpan = td.colSpan || 1
      const rowSpan = td.rowSpan || 1
      if (colSpan > 1 || rowSpan > 1) {
        let index = 0
        doSplit = true
        doMerge = false
        while (index < mergeCells.length) {
          const mergeItem = mergeCells[index]
          const row = mergeItem.row
          const col = mergeItem.col
          if (row === i && col === j) {
            mergeCells.splice(index, 1)
            break
          }
          index++
        }
      }
    }
  }
  if (doMerge) {
    if (endRow < startRow) {
      const tmp = startRow
      startRow = endRow
      endRow = tmp
    }
    if (endCol < startCol) {
      const tmp = startCol
      startCol = endCol
      endCol = tmp
    }
    let rowSpan = endRow - startRow
    let colSpan = endCol - startCol
    if (rowSpan === 0) {
      rowSpan = 1
    } else {
      rowSpan++
    }
    if (colSpan === 0) {
      colSpan = 1
    } else {
      colSpan++
    }
    const newMergeItem = {
      row: startRow,
      col: startCol,
      rowspan: rowSpan,
      colspan: colSpan
    }
    mergeCells.push(newMergeItem)
  } else {
    if (doSplit) {
      for (let i = startRow; i <= endRow; i++) {
        for (let j = startCol; j <= endCol; j++) {
          let cellDef = context.getCell(i, j)
          if (!cellDef) {
            cellDef = buildNewCellDef(i + 1, j + 1)
            context.addCell(cellDef)
          }
        }
      }
    }
  }
  table.updateSettings({
    mergeCells
  })
}
</script>
