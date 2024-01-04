<template>
  <el-dropdown trigger="click" @command="handleAlign" style="float: left;">
    <span class="el-dropdown-link">
      <div class="component-icon-btn">
        <div class="component-icon-btn-icon" style="line-height: 16px;float: left;">
          <svg v-show="align==='left'" xmlns="http://www.w3.org/2000/svg" height="16" viewBox="0 0 16 16" width="16">
            <path d="M2 13h12v1H2zm0-3h8v1H2zm0-3h12v1H2zm0-6h12v1H2zm0 3h8v1H2z" fill="#3d4757" fill-rule="evenodd" />
          </svg>
          <svg v-show="align==='center'" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
            <path d="M2 13h12v1H2v-1zm2-3h8v1H4v-1zM2 7h12v1H2V7zm0-6h12v1H2V1zm2 3h8v1H4V4z" fill="#3D4757" fill-rule="evenodd" />
          </svg>
          <svg v-show="align==='right'" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
            <path d="M2 13h12v1H2v-1zm4-3h8v1H6v-1zM2 7h12v1H2V7zm0-6h12v1H2V1zm4 3h8v1H6V4z" fill="#3D4757" fill-rule="evenodd" />
          </svg>
        </div>
        <div style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn"></div>
        </div>
      </div>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="left">
        <svg style="vertical-align: text-bottom;" xmlns="http://www.w3.org/2000/svg" height="16" viewBox="0 0 16 16"
          width="16">
          <path d="M2 13h12v1H2zm0-3h8v1H2zm0-3h12v1H2zm0-6h12v1H2zm0 3h8v1H2z" fill="#3d4757" fill-rule="evenodd" />
        </svg>
        居左对齐
      </el-dropdown-item>
      <el-dropdown-item command="center">
        <span>
          <svg style="vertical-align: text-bottom;" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
            <path d="M2 13h12v1H2v-1zm2-3h8v1H4v-1zM2 7h12v1H2V7zm0-6h12v1H2V1zm2 3h8v1H4V4z" fill="#3D4757" fill-rule="evenodd" />
          </svg>
        </span>
        居中对齐
      </el-dropdown-item>
      <el-dropdown-item command="right">
        <svg style="vertical-align: text-bottom;" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
          <path d="M2 13h12v1H2v-1zm4-3h8v1H6v-1zM2 7h12v1H2V7zm0-6h12v1H2V1zm4 3h8v1H6V4z" fill="#3D4757" fill-rule="evenodd" />
        </svg>
        居右对齐
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import {
    undoManager
  } from '../Utils.js'

  export default {
    name: 'AlignLeftTool',
    props: {
      context: {
        type: Object,
        required: true
      }
    },
    data: function() {
      return {
        align: 'left'
      }
    },
    mounted: function() {

    },
    methods: {
      handleAlign(v) {
        this.align = v
        const selectedCells = this.context.hot.getSelected()
        if (!selectedCells || selectedCells.length === 0) {
          return
        }
        const align = this.align
        const that = this
        let oldAligns = this._buildCellAlign(this.context, align)
        undoManager.add({
          undo: function() {
            oldAligns = that._buildCellAlign(that.context, null, oldAligns)
          },
          redo: function() {
            oldAligns = that._buildCellAlign(that.context, align)
          }
        })
      },
      refresh(startRow, startCol, endRow, endCol) {
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
            let cellDef = this.context.getCell(i, j)
            if (!cellDef) {
              continue
            }
            let cellStyle = cellDef.cellStyle;
            this.align = cellStyle.align || "left"
            break
          }
          break
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
            oldAligns[i + "," + j] = cellStyle.align || "";
            if (prevAligns) {
              align = prevAligns[i + "," + j];
            }
            td.style.textAlign = align
            cellStyle.align = align
            this.align = align
          }
        }
        return oldAligns
      }
    }
  }
</script>

<style>
</style>
