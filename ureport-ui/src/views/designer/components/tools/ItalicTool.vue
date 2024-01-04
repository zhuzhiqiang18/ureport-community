<template>
  <div class="component-icon-btn" @click="handleItalic">
    <div :class="italic ? 'component-icon-btn-icon selected':'component-icon-btn-icon'">
      <i class="icons icons-16 icons-16-font_italic"></i>
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
      italic: false
    }
  },
  mounted: function() {

  },
  methods: {
    handleItalic() {
      const table = this.context.hot
      const selected = table.getSelected()
      if (!selected) {
        this.$message('请选择单元格')
        return
      }
      this.italic = !this.italic
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
      let oldItalicStyle = updateCellsItalicStyle(_this.context, startRow, startCol, endRow, endCol)
      undoManager.add({
        redo: function() {
          oldItalicStyle = updateCellsItalicStyle(_this.context, startRow, startCol, endRow, endCol)
        },
        undo: function() {
          for (let i = startRow; i <= endRow; i++) {
            for (let j = startCol; j <= endCol; j++) {
              const cellDef = _this.context.getCell(i, j)
              if (!cellDef) {
                continue
              }
              const cellStyle = cellDef.cellStyle
              const italic = oldItalicStyle[i + ',' + j]
              cellStyle.italic = italic
            }
          }
          table.render()
        }
      })
    },
    refresh(startRow, startCol, endRow, endCol) {
      this.italic = false
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
          if(cellStyle.italic) {
            this.italic = true
          }
          break;
        }
        break;
      }
    }
  }

}

function updateCellsItalicStyle(context, startRow, startCol, endRow, endCol) {
  const hot = context.hot
  const oldItalicStyle = {}
  for (let i = startRow; i <= endRow; i++) {
    for (let j = startCol; j <= endCol; j++) {
      const cellDef = context.getCell(i, j)
      if (!cellDef) {
        continue
      }
      const cellStyle = cellDef.cellStyle
      oldItalicStyle[i + ',' + j] = cellStyle.italic
      if (cellStyle.italic) {
        cellStyle.italic = false
      } else {
        cellStyle.italic = true
      }
    }
  }
  hot.render()
  return oldItalicStyle
}
</script>
