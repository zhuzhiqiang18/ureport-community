<template>
  <el-dropdown size="small" trigger="click" @command="handleFontSize" style="float: left;">
    <span class="el-dropdown-link">
      <div class="component-icon-btn selfdefine">
        <div class="component-icon-btn-icon" style="line-height: 16px;width: 60px;float: left;font-size: 12px;">
          {{fontSize}}
        </div>
        <div style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn"></div>
        </div>
      </div>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item v-for="(value,index) in fontSizes" :command="value">{{value}}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import { undoManager } from '../Utils.js'
  import { fontSizes } from '@/data/select-options'

  export default {
    props: {
      context: {
        type: Object,
        required: true
      }
    },
    data: function() {
      return {
        fontSizes: fontSizes,
        fontSize: 10
      }
    },
    mounted: function() {

    },
    methods: {
      handleFontSize(val) {
        this.fontSize = val
        const table = this.context.hot
        const selected = table.getSelected();
        if (!selected) {
          this.$message('请选择单元格')
          return;
        }
        let startRow = selected[0]
        let startCol = selected[1]
        let endRow = selected[2]
        let endCol = selected[3]
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
        const _this = this
        let oldFontSize = updateFontSize(this.context, startRow, startCol, endRow, endCol, val, _this);
        undoManager.add({
          redo: function() {
            oldFontSize = updateFontSize(_this.context, startRow, startCol, endRow, endCol, val, _this);
          },
          undo: function() {
            for (let i = startRow; i <= endRow; i++) {
              for (let j = startCol; j <= endCol; j++) {
                let cellDef = _this.context.getCell(i, j);
                if (!cellDef) {
                  continue;
                }
                let cellStyle = cellDef.cellStyle;
                cellStyle.fontSize = oldFontSize[i + ',' + j] || 10;
                _this.fontSize = cellStyle.fontSize;
              }
            }
            table.render();
          }
        });
      },
      refresh(startRow, startCol, endRow, endCol) {
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
            const fontSize = cellStyle.fontSize || 10;
            this.fontSize = fontSize;
            break;
          }
          break;
        }

      }
    }
  }

  function updateFontSize(context, startRow, startCol, endRow, endCol, size, _this) {
    let hot = context.hot;
    const oldFontSize = {};
    for (let i = startRow; i <= endRow; i++) {
      for (let j = startCol; j <= endCol; j++) {
        let cellDef = context.getCell(i, j);
        if (!cellDef) {
          continue;
        }
        let cellStyle = cellDef.cellStyle;
        oldFontSize[i + ',' + j] = cellStyle.fontSize || 10;
        cellStyle.fontSize = size;
        _this.fontSize = size;
      }
    }
    hot.render();
    return oldFontSize;
  }
</script>

<style>
</style>
