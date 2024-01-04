<template>
  <el-form class="div-block-clearance" label-position="left" :model="form" size="mini" label-width="90px">
    <el-form-item label="宽:">
      <el-input v-model="form.width" oninput="value=value.replace(/[^\d]/g,'')" size="mini" style="width:100%" @change="handleWidth" />
    </el-form-item>
    <el-form-item label="高:">
      <el-input
        v-model="form.height"
        oninput="value=value.replace(/[^\d]/g,'')"
        size="mini"
        style="width:100%"
        @change="handleHeight"
      />
    </el-form-item>
    <el-form-item label="来源:">
      <el-select v-model="form.source" placeholder="请选择" style="width:100%" @change="handleSource">
        <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-form-item>
    <el-form-item v-if="form.source==='text'" label="路径:">
      <el-input v-model="form.path" @change="handlePath" />
    </el-form-item>
    <el-form-item v-if="form.source==='text'" label="本地图片:">
      <el-button plain @click="handleImage">选择本地图片</el-button>
    </el-form-item>
    <el-form-item v-if="form.source==='expression'" label="扩展方向:">
      <el-radio-group v-model="form.expand" size="mini" @change="handleExpand">
        <el-radio-button v-for="(item,index) in expands" :label="item.value">{{item.label}}</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="form.source==='expression'" label="表达式:" />
    <div :style="{visibility: (form.source==='expression'?'visible': 'hidden')}">
      <textarea ref="imageExpressioncode" />
    </div>
    <el-dialog
      title="选择本地图片"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="450px"
    >
      <div style="height: 400px;overflow: auto;">
        <el-image v-for="url in urls" :key="url" class="report-image" :src="url" lazy @click="handleSaveImage(url)" />
      </div>
    </el-dialog>
  </el-form>
</template>

<script>
import CodeMirror from 'codemirror'
import { scriptValidation, getImages } from '@/api/designer'
import { expands } from '@/data/select-options'

export default {
  props: {
    imageValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      expands: expands,
      dialogVisible: false,
      urls: [],
      sourceOptions: [
        { label: '路径', value: 'text' },
        { label: '表达式', value: 'expression' }
      ],
      form: {
        width: 0,
        height: 0,
        source: 'text',
        path: '',
        expand: 'None',
        expression: ''
      }
    }
  },
  watch: {
    imageValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.imageValue)
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
      if (cellDef.value.width) {
        this.form.width = cellDef.value.width
      } else {
        this.form.width = 0
      }
      if (cellDef.value.height) {
        this.form.height = cellDef.value.height
      } else {
        this.form.height = 0
      }
      if (cellDef.value.source) {
        this.form.source = cellDef.value.source
      } else {
        this.form.source = 'text'
      }
      if (cellDef.value.source === 'text' && cellDef.value.value) {
        this.form.path = cellDef.value.value
      } else {
        this.form.path = ''
      }
      if (cellDef.expand) {
        this.form.expand = cellDef.expand
      } else {
        this.form.expand = 'None'
      }
    },
    initEditor() {
      const that = this
      this.$nextTick(() => {
        if (!this.editor) {
          this.editor = CodeMirror.fromTextArea(this.$refs.imageExpressioncode, {
            mode: 'javascript',
            lineNumbers: true,
            gutters: ['CodeMirror-linenumbers', 'CodeMirror-lint-markers'],
            lint: {
              getAnnotations: that._buildScriptLintFunction(),
              async: true
            }
          })
          this.editor.on('change', function(cm, changes) {
            const expr = cm.getValue()
            that.cellDef.value.value = expr
          })
        }
        if (this.cellDef.value.source === 'expression' && this.cellDef.value.value) {
          this.editor.setValue(this.cellDef.value.value)
        } else {
          this.editor.setValue('')
        }
      })
    },
    handleWidth() {
      this.cellDef.value.width = this.form.width
    },
    handleHeight() {
      this.cellDef.value.height = this.form.height
    },
    handleSource() {
      this.cellDef.value.source = this.form.source
    },
    handlePath() {
      this.cellDef.value.value = this.form.path
    },
    handleExpand(expand) {
      this._setExpand(expand)
    },
    _setExpand(expand) {
      if (this.initialized) {
        return
      }
      const hot = this.imageValue.context.hot
      for (let i = this.rowIndex; i <= this.row2Index; i++) {
        for (let j = this.colIndex; j <= this.col2Index; j++) {
          const cellDef = hot.context.getCell(i, j)
          if (!cellDef) {
            continue
          }
          const type = cellDef.value.type
          if (type === 'dataset' || type === 'expression' || type === 'image') {
            cellDef.expand = expand
          }
        }
      }
      hot.render()
    },
    _buildScriptLintFunction() {
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
    },
    handleImage() {
      getImages({}).then(response => {
        const report = response.data || []
        const urls = []
        for (const url of report) {
          urls.push(url)
        }
        this.urls = urls
        this.dialogVisible = true
      })
    },
    handleSaveImage(url) {
      const arr = url.split('/')
      this.form.path = '/images/' + arr[arr.length - 1]
      this.handlePath()
      this.dialogVisible = false
    }
  }
}
</script>

<style>
  .report-image.el-image {
    width: 100%;
    height: 200px;
    border: 1px solid #ccc;
    padding: 10px;
    cursor: pointer;
  }

  .report-image:hover {
    border-color: #409EFF;
  }
</style>
