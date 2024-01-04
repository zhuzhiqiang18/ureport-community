<template>
  <el-dropdown
  trigger="click"
  @command="handleFreezeCell"
  @visible-change="handleDropdownMenu"
  style="float: left;">
    <span class="el-dropdown-link">
      <div class="component-icon-btn">
        <div class="component-icon-btn-icon" style="line-height: 16px;float: left;">
          <i class="icons icons-16 icons-16-frozen_cell"></i>
        </div>
        <div style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn"></div>
        </div>
      </div>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item :command="item.command" v-for="item in items">
        {{item.label}}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
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
      dialogVisible: false,
      form: {
        row: 0,
        col: 0
      },
      items: [],
      cellName: ''
    }
  },
  mounted: function() {

  },
  methods: {
    handleDropdownMenu(val) {
      if(val) {
        const items = []
        const freeze = this.context.reportDef.freeze
        if(freeze) {
          items.push({
            label: `取消固定`,
            command: 'cancel',
            col: null,
            row: null,
          })
        }
        const table = this.context.hot
        const selected = table.getSelected()
        if (selected) {
          const startRow = selected[0]
          const startCol = selected[1]
          if(startRow == 0 && startCol > 0) {// 第一行
            const cellName = this.context.getCellName(startRow, startCol - 1)
            const col = cellName.replace('1','')
            items.push({
              label: `固定至第${col}列`,
              command: 'col',
              col: startCol,
              row: null,
            })
          } else if(startRow > 0 && startCol == 0) {// 第一列
            items.push({
              label: `固定至第${startRow}行`,
              command: 'row',
              col: null,
              row: startRow,
            })
          } else if(startRow > 0 && startCol > 0) {// 第n行第n列
            const cellName = this.context.getCellName(startRow - 1, startCol - 1)
            const col = cellName.replace(startRow,'')
            if(startRow > 0 && startCol > 0) {
              items.push({
                label: `固定至第${startRow}行第${col}列`,
                command: 'col-row',
                col: startCol,
                row: startRow,
              })
            }
            items.push({
              label: `固定至第${startRow}行`,
              command: 'row',
              col: null,
              row: startRow,
            })
            items.push({
              label: `固定至第${col}列`,
              command: 'col',
              col: startCol,
              row: null,
            })
          }
        }
        items.push({
          label: `固定首行`,
          command: 'first-row',
          col: null,
          row: 1,
        })
        items.push({
          label: `固定首列`,
          command: 'first-col',
          col: 1,
          row: null,
        })
        this.items = items
      }
    },
    handleFreezeCell(val) {
      if(val === 'cancel') {
        this.context.reportDef.freeze = null
      } else {
        const item = this.items.find(ele => ele.command === val)
        this.context.reportDef.freeze = {
          col: item.col,
          row: item.row
        }
      }
      this.context.freezeLine.refresh()
    }
  }
}
</script>
