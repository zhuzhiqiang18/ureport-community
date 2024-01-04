<template>
  <el-dropdown trigger="click" @command="handleFontFamily" style="float: left;">
    <span class="el-dropdown-link">
      <div class="component-icon-btn selfdefine">
        <div class="component-icon-btn-icon" style="line-height: 16px;width: 120px;float: left;font-size: 12px;">
          {{fontFamily}}
        </div>
        <div style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn"></div>
        </div>
      </div>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item v-for="(item,index) in fontFamilys" :command="item.value">{{item.label}}</el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<script>
  import { undoManager} from '../Utils.js'
  import { fontFamilys} from '@/data/select-options'

  export default {
    name: 'FontFamilyTool',
    props: {
      context: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        fontFamilys: fontFamilys,
        show: false,
        fontFamily: '宋体'
      }
    },
    methods: {
      handleFontFamily(val) {
        this.fontFamily = val
        const table = this.context.hot
        const selected = table.getSelected();
        if (!selected) {
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
        let oldFontFamily = updateFontFamily(_this.context, startRow, startCol, endRow, endCol, val);
        undoManager.add({
          redo: function() {
            oldFontFamily = updateFontFamily(_this.context, startRow, startCol, endRow, endCol, val);
            _this.fontFamily = val
          },
          undo: function() {
            for (let i = startRow; i <= endRow; i++) {
              for (let j = startCol; j <= endCol; j++) {
                let cellDef = _this.context.getCell(i, j);
                if (!cellDef) {
                  continue;
                }
                let cellStyle = cellDef.cellStyle;
                cellStyle.fontFamily = oldFontFamily[i + ',' + j]
                _this.fontFamily = cellStyle.fontFamily;
              }
            }
            table.render()
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
            const fontFamily = cellStyle.fontFamily || "宋体";
            this.fontFamily = fontFamily;
            break;
          }
          break;
        }

      }
    }

  }

  function updateFontFamily(context, startRow, startCol, endRow, endCol, fontFamily) {
    let hot = context.hot;
    const oldFontFamily = {};
    for (let i = startRow; i <= endRow; i++) {
      for (let j = startCol; j <= endCol; j++) {
        let cellDef = context.getCell(i, j);
        if (!cellDef) {
          continue;
        }
        let cellStyle = cellDef.cellStyle;
        oldFontFamily[i + ',' + j] = cellStyle.fontFamily || '宋体';
        cellStyle.fontFamily = fontFamily;
      }
    }
    hot.render();
    return oldFontFamily;
  }
</script>
