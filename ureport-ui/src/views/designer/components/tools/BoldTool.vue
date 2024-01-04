<template>
  <div class="component-icon-btn" @click="handleBold">
    <div :class="bold ? 'component-icon-btn-icon selected':'component-icon-btn-icon'">
      <i class="icons icons-16 icons-16-font_bold"></i>
    </div>
  </div>
</template>

<script>
import { undoManager } from '../Utils.js'

export default {
  name: 'BoldTool',
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      bold: false
    }
  },
  mounted: function() {

  },
  methods: {
    handleBold() {
      const table = this.context.hot
      const selected = table.getSelected()
      if (!selected) {
        this.$message('请选择单元格')
        return
      }
      this.bold = !this.bold
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
      let oldBoldStyle = updateCellsBoldStyle(_this.context, startRow, startCol, endRow, endCol)
      undoManager.add({
        redo: function() {
          oldBoldStyle = updateCellsBoldStyle(_this.context, startRow, startCol, endRow, endCol)
        },
        undo: function() {
          for (let i = startRow; i <= endRow; i++) {
            for (let j = startCol; j <= endCol; j++) {
              const cellDef = _this.context.getCell(i, j)
              if (!cellDef) {
                continue
              }
              const cellStyle = cellDef.cellStyle
              const bold = oldBoldStyle[i + ',' + j]
              cellStyle.bold = bold
            }
          }
          table.render()
        }
      })
    },
    refresh(startRow, startCol, endRow, endCol) {
      this.bold = false
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
          if(cellStyle.bold) {
            this.bold = true
          }
          break;
        }
        break;
      }
    }
  }
}

function updateCellsBoldStyle(context, startRow, startCol, endRow, endCol) {
  const hot = context.hot
  const oldBoldStyle = {}
  for (let i = startRow; i <= endRow; i++) {
    for (let j = startCol; j <= endCol; j++) {
      const cellDef = context.getCell(i, j)
      if (!cellDef) {
        continue
      }
      const cellStyle = cellDef.cellStyle
      oldBoldStyle[i + ',' + j] = cellStyle.bold
      if (cellStyle.bold) {
        cellStyle.bold = false
      } else {
        cellStyle.bold = true
      }
    }
  }
  hot.render()
  return oldBoldStyle
}
</script>

<style>
</style>
