<template>
  <div class="my-property-panel" style="padding: 8px;overflow: auto;">
    <parent-cell-property ref="parentCellPropertyPanel" :context="context" />
    <el-form :model="form" class="div-block-clearance" label-position="left" size="mini">
      <el-form-item label="单元格类型:" label-width="90px">
        <el-select v-model="form.celllType" style="width:100%" @change="handleCelllTypeChange">
          <el-option v-for="item in celllTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
    </el-form>
    <simple-value-editor v-if="form.celllType==='simple'" :simple-value="simpleValue" />
    <expression-value-editor v-if="form.celllType==='expression'" :expression-value="expressionValue" />
    <data-set-value-editor v-if="form.celllType==='dataset'" :dataset-value="datasetValue" />
    <image-value-editor v-if="form.celllType==='image'" :image-value="imageValue" />
    <slash-value-editor v-if="form.celllType==='slash'" :slash-value="slashValue" />
    <zxing-value-editor v-if="form.celllType==='qrcode'" :qrcode-value="qrcodeValue" />
    <zxing-value-editor v-if="form.celllType==='barcode'" :qrcode-value="barcodeValue" />
    <chart-value-editor v-if="form.celllType==='chart'" :chart-value="chartValue" />
  </div>
</template>
<script>

import ParentCellProperty from './components/ParentCellProperty'
import SimpleValueEditor from './property/SimpleValueEditor'
import ExpressionValueEditor from './property/ExpressionValueEditor'
import DataSetValueEditor from './property/DataSetValueEditor'
import ImageValueEditor from './property/ImageValueEditor'
import SlashValueEditor from './property/SlashValueEditor'
import ZxingValueEditor from './property/ZxingValueEditor'
import ChartValueEditor from './property/ChartValueEditor'
import { undoManager } from '../Utils.js'

export default {
  components: {
    SimpleValueEditor,
    ExpressionValueEditor,
    DataSetValueEditor,
    ImageValueEditor,
    SlashValueEditor,
    ZxingValueEditor,
    ChartValueEditor,
    ParentCellProperty
  },
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      cellDef: {},
      show: false,
      activeName: 'first',
      celllTypeOptions: [
        { value: 'simple', label: '普通文本' },
        { value: 'expression', label: '表达式' },
        { value: 'dataset', label: '数据集' },
        { value: 'image', label: '图片' },
        { value: 'slash', label: '斜表头' },
        { value: 'qrcode', label: '二维码' },
        { value: 'barcode', label: '条码' },
        { value: 'chart', label: '图表' }
      ],
      form: {
        celllType: 'simple'
      },
      simpleValue: {
        value: ''
      },
      expressionValue: {
        value: ''
      },
      datasetValue: {
        value: ''
      },
      imageValue: {
        value: ''
      },
      slashValue: {
        value: ''
      },
      qrcodeValue: {
        value: ''
      },
      barcodeValue: {
        value: ''
      },
      chartValue: {
        value: ''
      }
    }
  },
  watch: {
    context: function(val) {
      this.init()
    }
  },
  mounted: function() {

  },
  methods: {
    init: function() {},
    handleCelllTypeChange(value) {
      const _this = this
      const oldCellDef = JSON.parse(JSON.stringify(this.cellDef))
      const cellDef = this.cellDef
      if (value === 'simple') {
        if (cellDef.value.type !== 'simple') {
          cellDef.value = {
            type: 'simple'
          }
        }
        cellDef.expand = 'None'
        this.simpleValue.context = this.context
        this.simpleValue.rowIndex = this.rowIndex
        this.simpleValue.colIndex = this.colIndex
        this.simpleValue.row2Index = this.row2Index
        this.simpleValue.col2Index = this.col2Index
      } else if (value === 'expression') {
        if (cellDef.value.type !== 'expression') {
          cellDef.value = {
            type: 'expression',
            value: ''
          }
        }
        cellDef.expand = 'None'
        this.expressionValue.context = this.context
        this.expressionValue.rowIndex = this.rowIndex
        this.expressionValue.colIndex = this.colIndex
        this.expressionValue.row2Index = this.row2Index
        this.expressionValue.col2Index = this.col2Index
      } else if (value === 'dataset') {
        if (cellDef.value.type !== 'dataset') {
          cellDef.value = {
            type: 'dataset',
            datasetName: '',
            property: '',
            aggregate: '',
            conditions: [],
            order: 'none'
          }
        }
        cellDef.expand = 'Down'
        this.datasetValue.context = this.context
        this.datasetValue.rowIndex = this.rowIndex
        this.datasetValue.colIndex = this.colIndex
        this.datasetValue.row2Index = this.row2Index
        this.datasetValue.col2Index = this.col2Index
      } else if (value === 'image') {
        const rowIndex = this.rowIndex
        const colIndex = this.colIndex
        const td = this.context.hot.getCell(rowIndex, colIndex)
        const width = this._buildWidth(colIndex, td.colSpan, this.context.hot)
        const height = this._buildHeight(rowIndex, td.rowSpan, this.context.hot)
        if (cellDef.value.type !== 'image') {
          cellDef.value = {
            width,
            height,
            type: 'image',
            source: 'text'
          }
        }
        cellDef.expand = 'None'
        this.imageValue.context = this.context
        this.imageValue.rowIndex = this.rowIndex
        this.imageValue.colIndex = this.colIndex
        this.imageValue.row2Index = this.row2Index
        this.imageValue.col2Index = this.col2Index
      } else if (value === 'slash') {
        if (cellDef.value.type !== 'slash') {
          cellDef.value = {
            type: 'slash'
          }
        }
        cellDef.expand = 'None'
        this.slashValue.context = this.context
        this.slashValue.rowIndex = this.rowIndex
        this.slashValue.colIndex = this.colIndex
        this.slashValue.row2Index = this.row2Index
        this.slashValue.col2Index = this.col2Index
      } else if (value === 'qrcode') {
        if (cellDef.value.type !== 'zxing' || cellDef.value.category !== 'qrcode') {
          const rowIndex = this.rowIndex
          const colIndex = this.colIndex
          const td = this.context.hot.getCell(rowIndex, colIndex)
          const width = this._buildWidth(colIndex, td.colSpan, this.context.hot)
          const height = this._buildHeight(rowIndex, td.rowSpan, this.context.hot)
          cellDef.value = {
            width,
            height,
            type: 'zxing',
            source: 'text',
            category: 'qrcode',
            data: ''
          }
          cellDef.expand = 'None'
        }
        this.qrcodeValue.context = this.context
        this.qrcodeValue.rowIndex = this.rowIndex
        this.qrcodeValue.colIndex = this.colIndex
        this.qrcodeValue.row2Index = this.row2Index
        this.qrcodeValue.col2Index = this.col2Index
      } else if (value === 'barcode') {
        if (cellDef.value.type !== 'zxing' || cellDef.value.category !== 'barcode') {
          const rowIndex = this.rowIndex
          const colIndex = this.colIndex
          const td = this.context.hot.getCell(rowIndex, colIndex)
          const width = this._buildWidth(colIndex, td.colSpan, this.context.hot)
          const height = this._buildHeight(rowIndex, td.rowSpan, this.context.hot)
          cellDef.value = {
            width,
            height,
            type: 'zxing',
            source: 'text',
            category: 'barcode',
            data: '',
            format: 'CODE_128'
          }
          cellDef.expand = 'None'
        }
        this.barcodeValue.context = this.context
        this.barcodeValue.rowIndex = this.rowIndex
        this.barcodeValue.colIndex = this.colIndex
        this.barcodeValue.row2Index = this.row2Index
        this.barcodeValue.col2Index = this.col2Index
      } else if (value === 'chart') {
        const rowIndex = this.rowIndex
        const colIndex = this.colIndex
        const td = this.context.hot.getCell(rowIndex, colIndex)
        const width = this._buildWidth(colIndex, td.colSpan, this.context.hot)
        const height = this._buildHeight(rowIndex, td.rowSpan, this.context.hot)
        cellDef.value = {
          width,
          height,
          type: 'chart',
          chart: {
            dataset: {
              type: 'histogram'
            }
          }
        }
        this.chartValue.context = this.context
        this.chartValue.rowIndex = this.rowIndex
        this.chartValue.colIndex = this.colIndex
        this.chartValue.row2Index = this.row2Index
        this.chartValue.col2Index = this.col2Index
      }
      const newCellDef = JSON.parse(JSON.stringify(this.cellDef))
      undoManager.add({
        redo: function() {
          _this.updateCell(newCellDef)
        },
        undo: function() {
          _this.updateCell(oldCellDef)
        }
      })
      this.context.hot.setDataAtCell(this.rowIndex, this.colIndex, '')
      this.context.hot.render()
    },
    updateCell(newValue) {
      const rowIndex = newValue.rowNumber - 1
      const colIndex = newValue.columnNumber - 1
      const cellDef = this.context.getCell(rowIndex, colIndex)
      cellDef.value = newValue.value
      const selected = this.context.hot.getSelected()
      if (selected) {
        const startRow = selected[0]
        const startCol = selected[1]
        const endRow = selected[2]
        const endCol = selected[3]
        this.refresh(startRow, startCol, endRow, endCol)
      }
      this.context.hot.render()
    },
    refresh(rowIndex, colIndex, row2Index, col2Index) {
      const cellDef = this.context.getCell(rowIndex, colIndex)
      if (!cellDef) {
        return
      }
      this.$refs.parentCellPropertyPanel.refresh(rowIndex, colIndex, row2Index, col2Index)
      this.cellDef = cellDef
      this.rowIndex = rowIndex
      this.colIndex = colIndex
      this.row2Index = row2Index
      this.col2Index = col2Index

      this.initialized = true
      const type = cellDef.value.type || 'simple'
      this.form.celllType = type
      const context = this.context
      if (type === 'simple') {
        this.simpleValue = {
          context: context,
          rowIndex: rowIndex,
          colIndex: colIndex,
          row2Index: row2Index,
          col2Index: col2Index
        }
        if(!cellDef.value || cellDef.value.value === '') {
          if(this.context.hot.getDataAtCell(this.rowIndex, this.colIndex)) {
            this.context.hot.setDataAtCell(this.rowIndex, this.colIndex, '')
          }
        }
      } else if (type === 'expression') {
        this.expressionValue = {
          context: context,
          rowIndex: rowIndex,
          colIndex: colIndex,
          row2Index: row2Index,
          col2Index: col2Index
        }
      } else if (type === 'dataset') {
        this.datasetValue = {
          context: context,
          rowIndex: rowIndex,
          colIndex: colIndex,
          row2Index: row2Index,
          col2Index: col2Index
        }
      } else if (type === 'image') {
        this.imageValue = {
          context: context,
          rowIndex: rowIndex,
          colIndex: colIndex,
          row2Index: row2Index,
          col2Index: col2Index
        }
      } else if (type === 'slash') {
        this.slashValue = {
          context: context,
          rowIndex: rowIndex,
          colIndex: colIndex,
          row2Index: row2Index,
          col2Index: col2Index
        }
      } else if (type === 'zxing') {
        this.form.celllType = cellDef.value.category
        if (cellDef.value.category === 'qrcode') {
          this.qrcodeValue = {
            context: context,
            rowIndex: rowIndex,
            colIndex: colIndex,
            row2Index: row2Index,
            col2Index: col2Index
          }
        } else if (cellDef.value.category === 'barcode') {
          this.barcodeValue = {
            context: context,
            rowIndex: rowIndex,
            colIndex: colIndex,
            row2Index: row2Index,
            col2Index: col2Index
          }
        }
      } else if (type === 'chart') {
        this.chartValue = {
          context: context,
          rowIndex: rowIndex,
          colIndex: colIndex,
          row2Index: row2Index,
          col2Index: col2Index
        }
      }
    },
    _buildWidth(colIndex, colspan, hot) {
      let width = hot.getColWidth(colIndex) - 3
      if (!colspan || colspan < 2) {
        return width
      }
      const start = colIndex + 1
      const end = colIndex + colspan
      for (let i = start; i < end; i++) {
        width += hot.getColWidth(i)
      }
      return width
    },

    _buildHeight(rowIndex, rowspan, hot) {
      let height = hot.getRowHeight(rowIndex) - 3
      if (!rowspan || rowspan < 2) {
        return height
      }
      const start = rowIndex + 1
      const end = rowIndex + rowspan
      for (let i = start; i < end; i++) {
        height += hot.getRowHeight(i)
      }
      return height
    }
  }
}
</script>

<style>
  .my-property-panel {
    width: 100%;
    height: 100%;
  }

  .my-property-panel .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .tab-pane-badge .el-badge__content.is-fixed {
    -webkit-transform: translateY(10%) translateX(150%);
    transform: translateY(10%) translateX(150%);
  }
</style>
