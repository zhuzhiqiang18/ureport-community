<template>
  <el-dropdown trigger="click" @command="handleValign" style="float: left;">
    <span class="el-dropdown-link">
      <div class="component-icon-btn">
        <div class="component-icon-btn-icon" style="line-height: 16px;float: left;">
          <svg v-show="valign==='top'" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <path d="M8 8H7v6h1zm-.5-3L10 8H5zM2 3h11v1H2z" fill="#3D4757" fill-rule="evenodd" />
          </svg>
          <svg v-show="valign==='middle'" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
            <path d="M8 12H7v3h1zm-.5-3l2.5 3H5zM7 3h1V0H7zm.5 3L5 3h5zM2 7h11v1H2z" fill="#3D4757" fill-rule="evenodd" />
          </svg>
          <svg v-show="valign==='bottom'" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
            <path d="M7 9h1V3H7zm.5 3L5 9h5zM2 13h11v1H2z" fill="#3D4757" fill-rule="evenodd" />
          </svg>
        </div>
        <div style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn"></div>
        </div>
      </div>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="top">
        <svg style="vertical-align: text-bottom;" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
          <path d="M8 8H7v6h1zm-.5-3L10 8H5zM2 3h11v1H2z" fill="#3D4757" fill-rule="evenodd" />
        </svg>
        顶部对齐
      </el-dropdown-item>
      <el-dropdown-item command="middle">
        <svg style="vertical-align: text-bottom;" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
          <path d="M8 12H7v3h1zm-.5-3l2.5 3H5zM7 3h1V0H7zm.5 3L5 3h5zM2 7h11v1H2z" fill="#3D4757" fill-rule="evenodd" />
        </svg>
        中部对齐
      </el-dropdown-item>
      <el-dropdown-item command="bottom">
        <svg style="vertical-align: text-bottom;" xmlns="http://www.w3.org/2000/svg" width="16" height="16">
          <path d="M7 9h1V3H7zm.5 3L5 9h5zM2 13h11v1H2z" fill="#3D4757" fill-rule="evenodd" />
        </svg>
        底部对齐
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import {
    undoManager
  } from '../Utils.js'

  export default {
    name: 'AlignTopTool',
    props: {
      context: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        valign: 'middle'
      }
    },
    mounted() {

    },
    methods: {
      handleValign(v) {
        this.valign = v
        const selectedCells = this.context.hot.getSelected()
        if (!selectedCells || selectedCells.length === 0) {
          this.$message('请选择单元格')
          return
        }
        const valign = this.valign
        const that = this
        let oldAligns = this._buildCellAlign(this.context, valign)
        undoManager.add({
          undo: function() {
            oldAligns = that._buildCellAlign(that.context, null, oldAligns)
          },
          redo: function() {
            oldAligns = that._buildCellAlign(that.context, valign)
          }
        })
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
            this.valign = cellStyle.valign || "middle";
            break;
          }
          break;
        }
      },
      _buildCellAlign(context, align, prevAligns) {
        const oldAligns = {}
        const selected = context.hot.getSelected()
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
        for (let i = startRow; i <= endRow; i++) {
          for (let j = startCol; j <= endCol; j++) {
            let cellDef = context.getCell(i, j);
            let td = context.hot.getCell(i, j);
            if (!cellDef) {
              continue;
            }
            const cellStyle = cellDef.cellStyle;
            oldAligns[i + "," + j] = cellStyle.valign || "";
            if (prevAligns) {
              align = prevAligns[i + "," + j];
            }
            td.style.verticalAlign = align
            cellStyle.valign = align
            this.valign = align
          }
        }
        return oldAligns
      }
    }
  }
</script>

<style>
</style>
