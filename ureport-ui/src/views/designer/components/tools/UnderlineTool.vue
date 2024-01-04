<template>
  <div class="component-icon-btn" @click="handleUnderline">
    <div :class="underline ? 'component-icon-btn-icon selected':'component-icon-btn-icon'">
      <i class="icons icons-16 icons-16-font_underline"></i>
    </div>
  </div>
</template>

<script>
import { undoManager } from '../Utils.js'

export default {
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      underline: false
    }
  },
  mounted: function() {

  },
  methods: {
    handleUnderline() {
      const table = this.context.hot
      const selected = table.getSelected()
      if (!selected) {
        this.$message('请选择单元格')
        return
      }
       this.underline = !this.underline
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
      let oldUnderlineStyle = updateCellsUnderlineStyle(this.context, startRow, startCol, endRow, endCol)
      undoManager.add({
        redo: function() {
          oldUnderlineStyle = updateCellsUnderlineStyle(_this.context, startRow, startCol, endRow, endCol)
        },
        undo: function() {
          oldUnderlineStyle = updateCellsUnderlineStyle(_this.context, startRow, startCol, endRow, endCol,
            oldUnderlineStyle)
        }
      })
    },
    refresh(startRow, startCol, endRow, endCol) {
      this.underline = false
      let tmp = endRow;
      if (startRow > endRow) {
        endRow = startRow;
        startRow = tmp;
      }
      tmp = endCol;
      if (startCol > endCol) {
        endCol = startCol;
        startCol = tmp;
      }
      for (let i = startRow; i <= endRow; i++) {
        for (let j = startCol; j <= endCol; j++) {
          let cellDef = this.context.getCell(i, j);
          if (!cellDef) {
            continue;
          }
          let cellStyle = cellDef.cellStyle;
          if(cellStyle.underline) {
            this.underline = true
          }
          break;
        }
        break;
      }
    }
  }

}

function updateCellsUnderlineStyle(context, startRow, startCol, endRow, endCol, prevUnderLine) {
  const oldUnderlineStyle = {}
  const hot = context.hot
  for (let i = startRow; i <= endRow; i++) {
    for (let j = startCol; j <= endCol; j++) {
      const td = hot.getCell(i, j)
      const cellDef = context.getCell(i, j)
      if (!cellDef) {
        continue
      }
      let underline = 'underline'
      if (prevUnderLine) {
        underline = prevUnderLine[i + ',' + j]
      }
      const cellStyle = cellDef.cellStyle
      oldUnderlineStyle[i + ',' + j] = cellStyle.underline
      if (cellStyle.underline) {
        cellStyle.underline = false
      } else {
        cellStyle.underline = true
      }
    }
  }
  hot.render()
  return oldUnderlineStyle
}
</script>
