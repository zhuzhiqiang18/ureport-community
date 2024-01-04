<template>
  <el-form class="div-block-clearance" label-position="left" :model="form" size="mini" label-width="90px">
    <el-form-item label="扩展方向:">
      <el-radio-group v-model="form.expand" size="mini" @change="handleExpand">
        <el-radio-button v-for="(item,index) in expands" :label="item.value">{{ item.label }}</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="格式化:">
      <g-autocomplete
        v-model="form.format"
        style="width:100%"
        :data="valueFormats"
        @change="handleFormat"
      />
    </el-form-item>
    <el-form-item label="条件属性:">
      <el-badge :value="conditionPropertyItems.length">
        <el-button plain @click="handleClickPropertyCondition">配置条件</el-button>
      </el-badge>
    </el-form-item>
    <el-form-item label="表达式:" />
    <textarea ref="expressioncode" />
    <property-condition-dialog :property-condition-value="propertyConditionValue" />
  </el-form>
</template>

<script>

import CodeMirror from 'codemirror'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/addon/hint/show-hint.js'
import 'codemirror/addon/lint/lint.js'
import PropertyConditionDialog from '../../dialog/PropertyConditionDialog'

import { scriptValidation, parseDataSet } from '@/api/designer'
import { expands, valueFormats } from '@/data/select-options'
import { undoManager } from '../../Utils.js'

export default {
  components: {
    PropertyConditionDialog
  },
  props: {
    expressionValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      conditionPropertyItems: [],
      expands: expands,
      valueFormats: valueFormats,
      editor: null,
      list: [],
      form: {
        expand: 'None',
        format: ''
      },
      content: '',
      propertyConditionValue: {
        dialogVisible: false
      },
      datasources: []
    }
  },
  watch: {
    expressionValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.expressionValue)
  },
  methods: {
    init(val) {
      if (val.context) {
        this.context = val.context
        this.refresh(val.rowIndex, val.colIndex, val.row2Index, val.col2Index)
        this.initEditor()
      }
    },
    refresh(rowIndex, colIndex, row2Index, col2Index) {
      const cellDef = this.context.getCell(rowIndex, colIndex)
      if (!cellDef) {
        return
      }
      this.rowIndex = rowIndex
      this.colIndex = colIndex
      this.row2Index = row2Index
      this.col2Index = col2Index
      this.cellDef = cellDef
      this.datasources = this.context.reportDef.datasources
      this.form.expand = cellDef.expand
      const cellStyle = cellDef.cellStyle
      if (cellStyle.format) {
        this.form.format = cellStyle.format
      } else {
        this.form.format = ''
      }
      this.conditionPropertyItems = cellDef.conditionPropertyItems || []
      if (!cellDef.conditionPropertyItems) {
        cellDef.conditionPropertyItems = this.conditionPropertyItems
      }
    },
    initEditor() {
      const _this = this
      this.$nextTick(() => {
        if (!this.editor) {
          this.editor = CodeMirror.fromTextArea(this.$refs.expressioncode, {
            mode: 'javascript',
            lineNumbers: true,
            gutters: ['CodeMirror-linenumbers', 'CodeMirror-lint-markers'],
            lint: {
              getAnnotations: _this.buildScriptLintFunction(),
              async: true
            }
          })
          this.editor.on('change', function(cm, changes) {
            const expr = cm.getValue()
            const oldValue = _this.cellDef.value.value
            if(oldValue !== expr) {
              _this.cellDef.value.value = expr
              _this.context.hot.setDataAtCell(_this.rowIndex, _this.colIndex, expr)
              undoManager.add({
                redo: function() {
                  _this.cellDef.value.value = expr
                  _this.context.hot.setDataAtCell(_this.rowIndex, _this.colIndex, expr)
                  _this.refreshSelected()
                },
                undo: function() {
                  _this.cellDef.value.value = oldValue
                  _this.context.hot.setDataAtCell(_this.rowIndex, _this.colIndex, oldValue)
                  _this.refreshSelected()
                }
              })
            }
          })
        }
        const value = this.cellDef.value.value || ''
        this.editor.setValue(value)
      })
    },
    handleExpand(val) {
      const _this = this
      const oldValue = this.cellDef.expand
      undoManager.add({
        redo: function() {
          _this.setExpand(val)
          _this.refreshSelected()
        },
        undo: function() {
          _this.setExpand(oldValue)
          _this.refreshSelected()
        }
      })
      this.setExpand(val)
    },
    refreshSelected() {
      const selected = this.context.hot.getSelected()
      if (selected) {
        const startRow = selected[0]
        const startCol = selected[1]
        const endRow = selected[2]
        const endCol = selected[3]
        this.$parent.refresh(startRow, startCol, endRow, endCol)
      }
    },
    handleFormat(val) {
      this.setFormat(val)
    },
    handleExpressionChange(val) {
      this.expressionValue.cellDef.value.value = val
      this.expressionValue.context.hot.setDataAtCell(this.rowIndex, this.colIndex, val)
    },
    setExpand(expand) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      const type = cellDef.value.type
      if (type === 'dataset' || type === 'expression') {
        cellDef.expand = expand
      }
      hot.render()
    },
    setFormat(format) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      cellDef.cellStyle.format = format
    },
    async handleClickPropertyCondition() {
      const expr = this.editor.getValue()
      if (!expr) {
        this.$message('请先输入表达式')
        return
      }
      const result = await parseDataSet({ expr })
      const datasetName = result.data.datasetName || ''
      this.propertyConditionValue = {
        dialogVisible: true,
        conditionPropertyItems: this.conditionPropertyItems,
        datasources: this.datasources,
        datasetName: datasetName
      }
    },
    buildScriptLintFunction() {
      return function(text, updateLinting, options, editor) {
        if (text === '') {
          updateLinting(editor, [])
          return
        }
        if (!text || text === '') {
          return
        }
        scriptValidation({
          content: text
        }).then(response => {
          const result = response.data
          if (result) {
            for (const item of result) {
              item.from = {
                line: item.line - 1
              }
              item.to = {
                line: item.line - 1
              }
            }
            updateLinting(editor, result)
          } else {
            updateLinting(editor, [])
          }
        })
      }
    }
  }
}
</script>
