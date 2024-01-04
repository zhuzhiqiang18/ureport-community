<template>
  <el-dialog
    title="数据集管理"
    :visible.sync="sqlDataSetValue.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="1024px"
    top="50px"
  >
    <el-container style="height: 532px;">
      <el-aside width="250px">
        <div style="width: 100%;height: 100%;display: flex;flex-direction: column;">
          <div style="height: 40px">
            <el-input v-model="searchTableName" size="small" placeholder="搜索表" style="width: 100%;">
              <el-button slot="append" icon="el-icon-search" @click="searchTable" />
            </el-input>
          </div>
          <div style="flex: 1;overflow: hidden;">
            <div style="height: 100%;overflow: hidden;padding: 5px 0px;border-radius: 3px;border: 1px solid #EBEEF5;" draggable="false" class="dataset-table el-tree-node is-focusable">
              <el-scrollbar style="height: 100%">
                <div v-for="item in searchTableNameList" class="el-tree-node__content" @dblclick="addTableSql(item.name)">
                  <span class="el-tree-node__expand-icon" />
                  <span class="el-tree-node__label">{{ item.name }}</span>
                </div>
              </el-scrollbar>
            </div>
          </div>
        </div>
      </el-aside>
      <el-main>
        <div style="padding-left: 10px;width: 100%;height: 100%;display: flex;flex-direction: column;">
          <div style="height: 40px">
            <span>数据集名称：</span>
            <el-input v-model="dataSetName" size="small" style="width: 650px;" />
          </div>
          <div style="height: 20px">
            <span>SQL<span style="color: #909399;font-size: 12px;">(SQL支持表达式，格式为：${表达式...})</span>:</span>
          </div>
          <textarea ref="sqlexpressioncode" value="" />
          <div style="height: 32px;line-height: 32px;">
            <span>查询参数<span style="color: #909399;font-size: 12px;">(请将上述SQL中用到的查询参数名定义在下面的表格中)</span>:</span>
          </div>
          <div style="flex: 1;overflow: hidden;">
            <data-set-parameter-dialog :parameters="parameters" />
          </div>
        </div>
      </el-main>
    </el-container>
    <el-dialog
      title="预览数据"
      v-if="previewDialogVisible"
      append-to-body
      :visible.sync="previewDialogVisible"
      width="960px"
    >
      <el-tabs v-model="activeName">
        <el-tab-pane label="信息" name="first">
          <div style="height: 400px;">
            <div v-for="(m,index) in message" :key="index+1"> {{ m }} </div>
          </div>
        </el-tab-pane>
        <el-tab-pane name="second">
          <span slot="label">
            查询结果
            <el-tooltip class="item" effect="dark" content="仅显示前100条数据" placement="top">
              <i class="el-icon-info" />
            </el-tooltip>
          </span>
          <el-table
          :header-cell-style="()=>'background: #f5f7fa'"
          border
          :data="tableData.data"
          size="mini"
          height="400">
            <template v-for="column in tableData.columns">
              <el-table-column header-align="center" :key="column" :prop="column" :label="column" :show-overflow-tooltip="true" min-width="180px" />
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="previewDialogVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="previewDialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
    <span slot="footer" class="dialog-footer">
        <el-button size="small" type="primary" @click="previewData">预览数据</el-button>
        <el-button size="small" type="primary" @click="saveDataSet">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import DataSetParameterDialog from './DataSetParameterDialog'
import CodeMirror from 'codemirror'
import 'codemirror/mode/sql/sql.js'
import 'codemirror/addon/hint/sql-hint.js'
import { previewData, selectDruidTableList, selectDruidTableFieldList } from '@/api/datasource'

export default {
  components: {
    DataSetParameterDialog
  },
  props: {
    sqlDataSetValue: {
      type: Object,
      required: true
    },
    refresh: {
      type: Function,
      required: true
    }
  },
  data: function() {
    return {
      activeName: 'param',
      datasource: null,
      dataset: null,
      tableNameList: [],
      searchTableName: '',
      searchTableNameList: [],
      parameters: [],
      dataSetName: '',
      sql: '',
      previewDialogVisible: false,
      message: [],
      tableData: {
        data: [],
        columns: []
      }
    }
  },
  watch: {
    sqlDataSetValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.sqlDataSetValue)
  },
  methods: {
    init(val) {
      if (val.context && val.context.reportDef) {
        this.datasource = val.datasource
        this.initEditor()
        this.message = []
        this.tableData.data = []
        this.tableData.columns = []
        this.searchTableNameList = []
        this.druidCode = val.code
        this.selecteDruid()
        if (val.edit) {
          const dataset = val.dataset
          this.dataset = dataset
          this.dataSetName = dataset.name
          this.sql = dataset.sql
          this.parameters = dataset.parameters
        } else {
          this.dataset = null
          this.dataSetName = ''
          this.sql = ''
          this.parameters = []
        }
      }
    },
    initEditor() {
      const that = this
      this.$nextTick(() => {
        if (!this.editor) {
          this.editor = CodeMirror.fromTextArea(this.$refs.sqlexpressioncode, {
            mode: 'text/x-sql',
            lineNumbers: true,
            gutters: ['CodeMirror-linenumbers', 'CodeMirror-lint-markers'],
            lint: {
              async: true
            },
            extraKeys: {
              'Ctrl': 'autocomplete'
            },
            matchBrackets: true,
            autofocus: true,
            hintOptions: {
              tables: {},
              completeSingle: false
            }
          })
          this.editor.setSize('100%', '240px')
          this.editor.on('change', function(cm, changes) {
            const expr = cm.getValue()
            that.sql = expr
          })
          this.editor.on('inputRead', function(codeMirror, object) {
            if (object.text[0] !== ' ') {
              that.editor.showHint()
            }
          })
        }
        this.editor.setValue(this.sql)
      })
    },
    selecteDruid() {
      const params = {
        code: this.datasource.code,
        name: this.datasource.name,
        driverClassName: this.datasource.driver,
        url: this.datasource.url,
        userName: this.datasource.username,
        password: this.datasource.password,
      }
      selectDruidTableList(params).then(res => {
        this.tableNameList = res.data
        this.searchTableNameList = res.data
      })
    },
    searchTable() {
      const tables = []
      for (const item of this.tableNameList) {
        const searchTableName = this.searchTableName.toLowerCase()
        if (item.name.indexOf(searchTableName) > -1) {
          tables.push(item)
        }
      }
      this.searchTableNameList = tables
    },
    addTableSql(name) {
      this.editor.setValue('select * from ' + name)
    },
    addTableName(name) {
      this.editor.replaceRange(name, CodeMirror.Pos(this.editor.lastLine()))
    },
    addDataSetParam() {
      this.dataSetParameterValue = {
        dialogVisible: true,
        edit: false,
        parameters: this.parameters
      }
    },
    previewData() {
      if (!this.sql) {
        this.$message('请添加查询SQL语句')
        return
      }
      const param = {
        info: {
          code: this.datasource.code,
          name: this.datasource.name,
          driverClassName: this.datasource.driver,
          url: this.datasource.url,
          userName: this.datasource.username,
          password: this.datasource.password
        },
        sql: this.sql,
        parameters: this.parameters
      }
      previewData(param).then(res => {
        const data = res.data || {}
        const ms = []
        ms.push(data.sql)
        ms.push(data.message || 'OK')
        this.activeName = data.message ? 'first' : 'second'
        this.message = ms
        this.tableData = {
          data: data.data || [],
          columns: data.fields || []
        }
        this.previewDialogVisible = true
      })
    },
    saveDataSet() {
      if (!this.sql) {
        this.$message('请添加查询SQL语句')
        return
      }
      if (!this.dataSetName) {
        this.$message('请输入数据集名称')
        return
      }
      const epx = /^[\d-]/
      if (epx.test(this.dataSetName)) {
        this.$message('名称不能是数字开头')
        return
      }
      const param = {
        info: {
          code: this.datasource.code,
          name: this.datasource.name,
          driverClassName: this.datasource.driver,
          url: this.datasource.url,
          userName: this.datasource.username,
          password: this.datasource.password
        },
        name: this.dataSetName,
        sql: this.sql,
        parameters: this.parameters
      }
      const datasources = this.sqlDataSetValue.context.reportDef.datasources
      for (const index in datasources) {
        const datasource = datasources[index]
        const datasets = datasource.datasets || []
        const old = datasets.find(ele => ele.name === param.name)
        if (old && (!this.sqlDataSetValue.edit || (this.dataset && this.dataset.name !== this.dataSetName))) {
          this.$message('数据集名称重复')
          return
        }
      }
      selectDruidTableFieldList(param).then(res => {
        const data = res.data
        const newDataSet = {
          name: param.name,
          sql: param.sql,
          parameters: param.parameters,
          fields: data
        }
        const ds = this.datasource
        if(!ds.datasets) {
          ds.datasets = []
        }
        if (this.sqlDataSetValue.edit) {
          for (const index in ds.datasets) {
            const d = ds.datasets[index]
            if (d.name === this.sqlDataSetValue.dataset.name) {
              ds.datasets.splice(index, 1, newDataSet)
            }
          }
        } else {
          ds.datasets.push(newDataSet)
        }
        // this.refresh(this.sqlDataSetValue)
        this.sqlDataSetValue.dialogVisible = false
      })
    }
  }
}
</script>

<style>
  .CodeMirror-hints {
    z-index: 3000 !important;
  }

  .CodeMirror {
    border-radius: 2px;
    border: 1px solid #ebeef5;
  }

  .CodeMirror-gutters {
    border-right-color: #ebeef5;
  }

  .dataset-table .el-tree-node__label {
    max-width: 230px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis
  }

  .dataset-table .el-scrollbar__wrap {
    overflow-x: hidden;
  }
</style>
