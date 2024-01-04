<template>
  <el-container id="container" style="position: absolute;overflow: hidden;">
    <el-header style="height: 38px;">
      <tool-bar v-if="context" ref="childToolbar" :context="context" :tools="tools" />
      <div class="report-tilte">报表名称：{{ reportFullName }}</div>
    </el-header>
    <el-main id="designerPanel" style="position: relative;display: flex;">
      <div id="designerLeftPanel" :style="{width: leftExpand ? '250px !important':'0px',position: 'relative'}">
        <div class="aside-open-close" @click="handleExpand('left')">
          <i class="el-icon-arrow-left" v-if="leftExpand"></i>
          <i class="el-icon-arrow-right" v-else></i>
        </div>
        <data-set :data-set-value="dataSetValue" :refresh-child-property-panel="refreshChildPropertyPanel" style="height: 100%;" />
      </div>
      <div style="flex:1;position: relative;">
        <div style="border-left: 1px solid #eeeeee;font-size: 14px;line-height: 28px;padding: 4px 10px;height: 36px;background: #f2f4f7;display: flex;">
          <div style="text-align: center;width: 120px;border: 1px solid #e2e6ed;height: 28px;background: #fff;border-radius: 4px;">{{cellName}}</div>
          <i class="icons icons-16 icons-16-func" style="margin: auto 5px;"></i>
          <div id="selectionCellBoard" style="overflow:hidden;border: 1px solid #e2e6ed;height: 28px;padding-left:10px;flex:1;background: #fff;border-radius: 4px;">{{cellContent}}</div>
        </div>
        <div id="designer" style="font-size: 13px;" />
      </div>
      <div id="designerRightPanel" :style="{width: rightExpand ? '400px !important':'0px',position: 'relative'}">
        <div class="aside-open-close right" @click="handleExpand('right')">
          <i class="el-icon-arrow-right" v-if="rightExpand"></i>
          <i class="el-icon-arrow-left" v-else></i>
        </div>
        <property-panel v-show="rightExpand" v-if="context" ref="childPropertyPanel" :context="context" />
      </div>
    </el-main>
  </el-container>
</template>

<script>
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/hint/show-hint.css'
import 'codemirror/addon/lint/lint.css'

import 'handsontable/dist/handsontable.css'
import Context from './components/Context.js'
import ReportTable from './components/table/ReportTable.js'
import PropertyPanel from './components/panel/PropertyPanel'

import DataSet from './components/dataset'
import { undoManager, debounce } from './components/Utils.js'
import PrintLine from './components/PrintLine.js'
import FreezeLine from './components/FreezeLine.js'
import { renderRowHeader } from './components/table/HeaderUtils.js'
import ToolBar from './components/tools/ToolBar'
import {refreshSelectionCellBoard} from './components/table/CellRenderer.js'

export default {
  name: 'Table',
  components: {
    ToolBar,
    PropertyPanel,
    DataSet
  },
  data: function() {
    return {
      loading: true,
      leftExpand: true,
      rightExpand: true,
      cellName: '',
      cellContent: '',
      reportFullName: '未命名',
      extend: false,
      activeName: 'first',
      context: null,
      tools: [],
      dataSetValue: {}
    }
  },
  mounted: function() {
    this.init()
  },
  methods: {
    init: function() {
      const elementId = 'designer'
      const ele = document.getElementById(elementId)
      const that = this
      const elementResizeDetectorMaker = require('element-resize-detector')
      // 监听元素变化
      const erd = elementResizeDetectorMaker()
      erd.listenTo(document.getElementById('designerPanel'), () => {
        this.$nextTick(function () {
          debounce(() => {
            const designerLeftPanel = document.getElementById('designerLeftPanel')
            const designerRightPanel = document.getElementById('designerRightPanel')
            const leftWidth = parseInt(designerLeftPanel.style.width)
            const rightWidth = parseInt(designerRightPanel.style.width)
            that.resizeTable(leftWidth, rightWidth)
          }, 150)
        })
      })
      undoManager.setLimit(100)
      const _this = this
      new ReportTable(ele, function() {
        _this.context = new Context(this)
        _this.context.elementId = elementId
        const reportFullName = _this.context.reportDef.reportFullName
        if (reportFullName === 'classpath:templates/template.ureport.xml') {
          _this.reportFullName = '未命名'
        } else {
          const prefix = 'file:'
          _this.reportFullName = reportFullName.substring(prefix.length, reportFullName.length)
        }
        this.bindSelectionEvent(function(rowIndex, colIndex, row2Index, col2Index) {
          _this.correctionHandsontableInputHolder(ele, rowIndex, colIndex)
          _this.context.setSelection(rowIndex, colIndex, row2Index, col2Index)
          let td = _this.context.hot.getCell(rowIndex, colIndex)
          const cell = _this.context.getCell(rowIndex, colIndex)
          refreshSelectionCellBoard(td, cell)
          _this.cellName = _this.context.getCellName(rowIndex, colIndex)
          _this.$refs.childPropertyPanel.refresh(rowIndex, colIndex, row2Index, col2Index)
          _this.$refs.childToolbar.refresh(rowIndex, colIndex, row2Index, col2Index)
        })
        _this.printLine = new PrintLine(_this.context)
        _this.freezeLine = new FreezeLine(_this.context)
        const rows = _this.context.reportDef.rows
        for (const row of rows) {
          const band = row.band
          if (!band) {
            continue
          }
          _this.context.addRowHeader(row.rowNumber - 1, band)
        }
        renderRowHeader(_this.context.hot, _this.context)
        _this.dataSetValue = {
          context: _this.context
        }
        _this.$nextTick(function () {
          const designerLeftPanel = document.getElementById('designerLeftPanel')
          const designerRightPanel = document.getElementById('designerRightPanel')
          const leftWidth = parseInt(designerLeftPanel.style.width)
          const rightWidth = parseInt(designerRightPanel.style.width)
          _this.resizeTable(leftWidth, rightWidth)
        })
      })

      const sheetDiv = document.getElementById('container')
      document.ondragstart = function(ev) {
        ev.dataTransfer.setData('dataset', ev.target.attributes.dataset.nodeValue)
        ev.dataTransfer.setData('field', ev.target.textContent)
      }
      sheetDiv.ondragover = function(ev) {
        ev.preventDefault()
      }
      sheetDiv.ondrop = function(ev) {
        const ele = ev.target
        if (ele.nodeName.toLowerCase() !== 'td') {
          return false
        }
        const cellIndex = ele.cellIndex
        const tbody_tr = ele.parentNode
        const thead_tr = tbody_tr.parentNode.previousElementSibling.children[0]
        const letter = thead_tr.children[cellIndex].textContent

        const colIndex = letter.charCodeAt(0) - 65
        const rowIndex = tbody_tr.rowIndex - 1

        let cellDef = _this.context.getCell(rowIndex, colIndex)
        const dataset = ev.dataTransfer.getData('dataset').trim()
        const field = ev.dataTransfer.getData('field').trim()

        if (cellDef.value.type !== 'dataset') {
          _this.context.removeCell(cellDef)
          cellDef = {
            value: {
              type: 'dataset',
              conditions: []
            },
            rowNumber: cellDef.rowNumber,
            columnNumber: cellDef.columnNumber,
            cellStyle: cellDef.cellStyle
          }
          _this.context.addCell(cellDef)
        }
        cellDef.expand = 'Down'
        const value = cellDef.value
        value.aggregate = 'group'
        value.datasetName = dataset
        value.property = field
        value.order = 'none'

        const text = `${value.datasetName}.${value.aggregate}(${value.property})`
        _this.context.hot.setDataAtCell(rowIndex, colIndex, text)
        ev.preventDefault()
        const selected = _this.context.hot.getSelected()
        if (selected) {
          const startRow = selected[0]
          const startCol = selected[1]
          const endRow = selected[2]
          const endCol = selected[3]
          _this.refreshChildPropertyPanel(startRow, startCol, endRow, endCol)
        }
      }
    },
    handleExpand(type) {
      const designerLeftPanel = document.getElementById('designerLeftPanel')
      const designerRightPanel = document.getElementById('designerRightPanel')
      let leftWidth = 0
      if((type === 'left' && !this.leftExpand) || (type === 'right' && this.leftExpand)) { // 左边展开
        leftWidth = 250
      }
      let rightWidth = 0
      if((type === 'right' && !this.rightExpand) || (type === 'left' && this.rightExpand)) {
        rightWidth = 400
      }
      this.resizeTable(leftWidth, rightWidth)
      if(type === 'left') {
        this.leftExpand = !this.leftExpand
      } else {
        this.rightExpand = !this.rightExpand
      }
    },
    resizeTable(offsetLeft,offsetRight) {
      if (this.context && this.context.hot) {
        const height = document.documentElement.clientHeight
        const width = document.documentElement.clientWidth
        const ele = document.getElementById('designerPanel')

        let offset = ele.getBoundingClientRect().left
        if(offsetLeft || offsetRight) {
          offset = offset + offsetLeft  + offsetRight
        }
        this.context.hot.updateSettings({
          height: height - ele.getBoundingClientRect().top - 36,
          width: width - offset
        })
        this.context.printLine.refresh()
        this.context.freezeLine.refresh()
      }
    },
    refreshChildPropertyPanel(rowIndex, colIndex, row2Index, col2Index) {
      this.$refs.childPropertyPanel.refresh(rowIndex, colIndex, row2Index, col2Index)
    },
    correctionHandsontableInputHolder(ele, rowIndex, colIndex) {
      const targets = ele.querySelectorAll('.handsontableInputHolder')
      const target = targets[targets.length - 1]
      target.style.marginTop = '0px'
      if (rowIndex > 0) {
        target.style.marginTop = '1px'
      }
      target.style.marginLeft = '0px'
      if (colIndex > 0) {
        target.style.marginLeft = '1px'
      }
    }
  }
}
</script>

<style>
  body {
    -webkit-user-select:none;
    -moz-user-select:none;
    -ms-user-select:none;
    user-select:none;
    overflow: hidden;
  }

  div.cell {
    -webkit-user-select:text;
    -moz-user-select:text;
    -ms-user-select:text;
    user-select:text;
  }

  img {
    vertical-align: middle;
    border: 0;
  }

  .tool-top-btn {
    margin-top: 10px;
    font-size: 12px;
    margin-left: 20px;
  }

  .dataset-item {
    display: none
  }

  .el-tree-node__content:hover .dataset-item {
    display: inline-block;
  }

  .handsontable tr,.handsontable td,.handsontable th {
    background: transparent;
  }

  .handsontable table.htCore {
    border-collapse: collapse
  }

  .handsontable th {
    border-color: #eeeff1 !important;
    background: #f4f5f8;
    border-bottom: 1px solid #eeeff1;
    color: #909399;
  }

  .handsontable tr:first-child th,
  .handsontable tr:first-child td {
    border-top: 1px solid #e6e6e6;
  }

  .handsontable th, .handsontable td {
    border-top: 1px solid #e6e6e6;
    border-right: 1px solid #e6e6e6;
    border-bottom: 1px solid #e6e6e6;
    border-left: 1px solid #e6e6e6;
  }

  .handsontable tbody th.ht__highlight,
  .handsontable thead th.ht__highlight {
    background-color: #e7edf9;
  }

  .handsontable .wtBorder.current.corner {
    width: 4px !important;
    height: 4px !important;
  }

  .handsontable td.area {
    background-color: transparent;
  }

  .handsontable .wtBorder.area.corner {
    width: 4px !important;
    height: 4px !important;
  }

  div.htBorders > div:nth-child(1) {
    left: -1px !important;
  }

  .htBorders div:first-child div:first-child{
    display: inline-block !important;
    width: 0px;
    background-color: transparent !important;
    border-left: 1px dashed #999999;
  }

  .htBorders div:first-child div:nth-child(2){
    display: inline-block !important;
    height: 0px;
    background-color: transparent !important;
    border-top: 1px dashed #999999;
  }

  .htBorders div:first-child div:nth-child(3){
    display: inline-block !important;
    width: 0px;
    background-color: transparent !important;
    border-left: 1px dashed #67C23A;
  }

  .htBorders div:first-child div:nth-child(4){
    display: inline-block !important;
    height: 0px;
    background-color: transparent !important;
    border-top: 1px dashed #67C23A;
  }

  .htBorders div.wtBorder.area:nth-child(2),
  .htBorders div.wtBorder.area:nth-child(3),
  .htBorders div.wtBorder.area:nth-child(5),
  .htBorders div.wtBorder.current:nth-child(3),
  .htBorders div.wtBorder.current:nth-child(5){
    margin-top: 1px;
  }

  .handsontable.ht_clone_top table.htCore,
  .handsontable.ht_master table.htCore,
  .htBorders div.wtBorder.area,
  .htBorders div.wtBorder.current{
    margin-left: 1px;
  }

  .htBorders div.wtBorder.area:nth-child(1),
  .htBorders div.wtBorder.area:nth-child(4),
  .htBorders div.wtBorder.area:nth-child(5),
  .htBorders div.wtBorder.current:nth-child(4),
  .htBorders div.wtBorder.current:nth-child(5) {
    margin-left: 2px;
  }

  .htContextMenu table.htCore {
    color: #606266;
    font-size: 14px;
    border: 0px solid #e6e6e6;
    border-bottom-width: 0px;
    border-right-width: 0px;
    background: #fff;
    box-shadow: 1px 2px 5px 2px rgba(51, 51, 51, 0.15);
  }

  .ht_clone_top.handsontable .wtHolder {
    overflow: hidden;
  }

  fieldset {
    padding: 10px;
    border: solid 1px #dddddd;
    border-radius: 3px;
  }

  .condition-dialog fieldset {
    height: 600px;
    width: 170px;
    display: inline-block;
  }

  legend {
    width: auto;
    margin-bottom: 1px;
    border-bottom: none;
    font-size: inherit;
    color: #4b4b4b;
    padding: 0px 5px;
  }

  .condition-dialog .condition-name-block {
    padding: 5px 4px;
    cursor: pointer;
    border-radius: 1px;
  }

  .condition-dialog .condition-name-block-selected {
    background: #409EFF;
    color: #FFFFFF;
  }

  .condition-dialog .condition-name-block:hover {
    background: #F56C6C;
    color: #FFFFFF;
  }

  .CodeMirror-lint-tooltip {
    z-index: 3000;

  }

  .property-row-item {
    height: 28px;
    margin-top: 5px;
  }

  .property-row-item-label {
    line-height: 28px;
  }

  .property-row-item-label.second {
    text-align: center;
  }

  #container {
    margin: 0px;
    padding: 0px;
    width: 100%;
    height: 100%;
    overflow: hidden;
  }

  .sheet-toolbar-btns {
    padding-top: 8px;
    padding-left: 5px;
    color: #606266;
  }

  .sheet-toolbar-btns .el-dropdown {
    color: #606266;
  }

  .sheet-toolbar-btn {
    height: 26px;
    width: 26px;
    cursor: pointer;
    display: inline-block;
    border: 1px solid #dcdfe6;
    border-color: #f9f9f9;
    position: relative;
    border-radius: 2px;
    margin-right: 5px
  }

  .sheet-toolbar-btn:hover,
  .sheet-toolbar-btn.active {
    background: #E4E7ED;
  }

  .sheet-toolbar-btn:hover .sheet-toolbar-dropdown-icon {
    background: #DCDFE6;
  }

  .sheet-toolbar-icon {
    position: absolute;
    top: 0px;
    left: 0px;
    bottom: 0px;
    right: 0px;
    margin: auto;
    height: 17px;
    width: 16px;
  }

  .sheet-toolbar-btn.dropdown {
    margin-right: 15px;
  }

  .sheet-toolbar-dropdown-icon {
    position: absolute;
    height: 24px;
    right: -14px;
    top: 0px;
    padding: 0px 2px;
    border-radius: 1px;
  }

  .sheet-toolbar-icon i {
    position: absolute;
    top: 0px;
  }

  .sheet-toolbar-btn.dropdown .el-dropdown {
    position: absolute;
    right: 0px;
    height: 22px;
  }

  .caret {
    display: inline-block;
    width: 0;
    height: 0;
    margin: 0px 2px;
    border-top: 4px dashed;
    border-top: 4px solid;
    border-right: 4px solid transparent;
    border-left: 4px solid transparent;
  }

  .x-spreadsheet-color-palette-cell {
    width: 20px;
    height: 20px;
    border: 1px solid #FFFFFF;
    cursor: pointer;
  }

  .x-spreadsheet-color-palette-cell:hover {
    border-color: #DCDFE6;
  }
  .div-block-clearance .el-form-item {
    margin-bottom: 10px;
  }
  .report-tilte {
    position: absolute;
    top:14px;
    right:10px;
    color: #606266;
    font-size: 12px;
  }
  .aside-open-close {
    position: absolute;
    right: -12px;
    top: 45%;
    width: 12px;
    height: 48px;
    line-height: 48px;
    color: #fff;
    background-color: #C0C4CC;
    border-radius: 0 6px 6px 0;
    font-size: 12px;
    z-index: 1000;
    cursor: pointer;
  }

  .aside-open-close.right {
    border-radius: 6px 0px 0px 6px;
    left: -14px;
  }

  .aside-open-close:hover{
    background-color: #606266;
  }
</style>
