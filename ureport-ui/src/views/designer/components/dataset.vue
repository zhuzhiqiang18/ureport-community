<template>
  <el-container id="dataset_container" style="position: relative;height:100%;overflow: hidden;">
    <el-main style="display: flex;flex-direction: column;">
      <div class="dataset-tree-node" style="flex: 1;overflow: auto;">
        <el-scrollbar style="height: 100%">
          <div class="el-tree-node__children">
            <div v-for="(datasource, index) in datasources" class="el-tree-node is-expanded is-current is-focusable">
              <div class="el-tree-node__content" @click="handleExpand(datasource)" @contextmenu.prevent="menu($event,datasource,null)">
                <span :class="expand.indexOf(datasource) > -1 ? 'expanded el-tree-node__expand-icon el-icon-caret-right' : 'el-tree-node__expand-icon el-icon-caret-right' " style="padding: 6px 2px;" />
                  <i v-if="datasource.driver" class="icons icons-16 icons-16-database" style="display: inline-block;vertical-align: text-top;font-size: 16px;color:#000000;" />
                  <i v-else-if="datasource.type==='api'" class="icons icons-16 icons-16-api_database" style="display: inline-block;vertical-align: text-top;"></i>
                  <i v-else class="icons icons-16 icons-16-func_database" style="display: inline-block;vertical-align: text-top;"></i>
                <span class="el-tree-node__label" style="margin-left: 2px;">{{ datasource.name }}</span>
              </div>
              <el-collapse-transition>
                <div v-show="expand.indexOf(datasource) > -1" class="el-tree-node__children">
                  <div v-for="(dataset,index) in datasource.datasets" class="el-tree-node is-focusable">
                    <div class="el-tree-node__content" style="padding-left: 12px;" @click="handleExpand(dataset)" @contextmenu.prevent="menu($event,datasource,dataset)">
                      <span :class="expand.indexOf(dataset) > -1 ? 'expanded el-tree-node__expand-icon el-icon-caret-right' : 'el-tree-node__expand-icon el-icon-caret-right' "  style="padding: 6px 2px;" />
                      <span class="el-tree-node__label">{{ dataset.name }}</span>
                    </div>
                    <el-collapse-transition>
                      <div v-show="expand.indexOf(dataset) > -1" class="el-tree-node__children">
                        <div v-for="field in dataset.fields" class="el-tree-node is-focusable">
                          <div class="el-tree-node__content" style="padding-left: 28px;">
                            <i class="el-icon-rank" style="color: #909399;font-size: 12px;"></i>
                            <span style="margin-left: 2px;" class="el-tree-node__label" :dataset="dataset.name" draggable="true">{{ field.name }}</span>
                          </div>
                        </div>
                      </div>
                    </el-collapse-transition>
                  </div>
                </div>
              </el-collapse-transition>
            </div>
          </div>
        </el-scrollbar>
      </div>
    </el-main>
    <sql-data-set-dialog :sql-data-set-value="sqlDataSetValue" :refresh="init" />
    <data-source-dialog :custom-data-set-value="customDataSetValue"  />
    <api-data-source-dialog :api-data-set-value="apiDataSetValue"  />
    <vue-context ref="menu">
      <li>
        <el-link v-show="datasource && datasource.type !=='api' && !dataset" :underline="false" @click.prevent="addDataSet()"><i class="el-icon-circle-plus-outline" /> 新建数据集</el-link>
        <el-link v-show="!dataset && datasource && (datasource.driver || datasource.type ==='api')" :underline="false" @click.prevent="editDataSource()"><i class="el-icon-edit" /> 编辑数据源</el-link>
        <el-link v-show="!dataset" :underline="false" @click.prevent="removeDataSource()" ><i class="el-icon-delete" /> 删除数据源</el-link>
        <el-link v-show="datasource && datasource.type !=='api' && dataset" :underline="false" @click.prevent="editDataSet()"><i class="el-icon-edit" /> 编辑数据集</el-link>
        <el-link v-show="dataset" :underline="false" @click.prevent="removeDataSet()"><i class="el-icon-delete" /> 删除数据集</el-link>
      </li>
    </vue-context>
  </el-container>
</template>

<script>
import SqlDataSetDialog from './dialog/SqlDataSetDialog'
import DataSourceDialog from './dialog/DataSourceDialog'
import ApiDataSourceDialog from './dialog/ApiDataSourceDialog'

import VueContext from 'vue-context'
import 'vue-context/dist/css/vue-context.css'

export default {
  name: 'DataSetProperty',
  components: {
    SqlDataSetDialog,
    DataSourceDialog,
    ApiDataSourceDialog,
    VueContext
  },
  props: {
    dataSetValue: {
      type: Object,
      required: true
    },
    refreshChildPropertyPanel: {
      type: Function,
      required: true
    },
  },
  data: function() {
    return {
      expand: [],
      datasources: [],
      datasets: [],
      datasource: null,
      dataset: null,
      customDataSetValue: {
        dialogVisible: false
      },
      apiDataSetValue: {
        dialogVisible: false
      },
      sqlDataSetValue: {
        dialogVisible: false
      }
    }
  },
  watch: {
    dataSetValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.dataSetValue)
  },
  methods: {
    init(val) {
      this.expand = []
      if (val.context && val.context.reportDef) {
        if(!val.context.reportDef.datasources) {
          val.context.reportDef.datasources = []
        }
        for(const ds of val.context.reportDef.datasources) {
          if(!ds.datasets) {
            ds.datasets = []
          }
        }
        this.datasources = val.context.reportDef.datasources
      }
    },
    editDataSource() {
      if(this.datasource.type === 'jdbc') {
        this.customDataSetValue = {
          dialogVisible: true,
          edit: true,
          datasource: this.datasource,
          context: this.dataSetValue.context
        }
      } else if(this.datasource.type === 'api') {
        this.apiDataSetValue = {
          dialogVisible: true,
          edit: true,
          datasource: this.datasource,
          context: this.dataSetValue.context
        }
      }
    },
    menu(e, datasource, dataset) {
      this.datasource = datasource
      this.dataset = dataset
      this.$refs.menu.open(e)
    },
    handleExpand(obj) {
      const index = this.expand.indexOf(obj)
      if(index > -1) {
        this.expand.splice(index, 1)
      } else {
        this.expand.push(obj)
      }
    },
    addDataSet() {
      if(this.datasource.type === 'jdbc' || this.datasource.type === 'buildin') {
        this.sqlDataSetValue = {
          dialogVisible: true,
          edit: false,
          datasource: this.datasource,
          context: this.dataSetValue.context
        }
      }
    },
    editDataSet: function() {
      this.sqlDataSetValue = {
        dialogVisible: true,
        edit: true,
        datasource: this.datasource,
        dataset: JSON.parse(JSON.stringify(this.dataset)),
        context: this.dataSetValue.context
      }
    },
    removeDataSource: function() {
      this.$confirm('确定删除数据源?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const datasources = this.dataSetValue.context.reportDef.datasources || []
        const index = datasources.indexOf(this.datasource)
        if (index > -1) {
          datasources.splice(index, 1)
        }
        this.$message({
          type: 'success',
          message: '删除成功!'
        })
      })
    },
    removeDataSet: function() {
      this.$confirm('确定删除数据集?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const datasources = this.dataSetValue.context.reportDef.datasources || []
        for (const ds of datasources) {
          const datasets = ds.datasets || []
          const index = datasets.indexOf(this.dataset)
          if (index > -1) {
            datasets.splice(index, 1)
          }
        }
        this.$message({
          type: 'success',
          message: '删除成功!'
        })
      })
    },
    addField(dataset, field) {
      const context = this.dataSetValue.context
      const selected = context.hot.getSelected()
      if(!selected) {
          return null
      }
      const startRow = selected[0]
      const startCol = selected[1]
      const endRow = selected[2]
      const endCol = selected[3]
      let cellDef = context.getCell(startRow, startCol)
      if (cellDef.value.type !== 'dataset') {
        context.removeCell(cellDef)
        cellDef = {
          value: {
            type: 'dataset',
            conditions: []
          },
          rowNumber: cellDef.rowNumber,
          columnNumber: cellDef.columnNumber,
          cellStyle: cellDef.cellStyle
        }
        context.addCell(cellDef)
      }
      cellDef.expand = 'Down'
      const value = cellDef.value
      value.aggregate = 'group'
      value.datasetName = dataset
      value.property = field
      value.order = 'none'

      let text = value.datasetName + '.' + value.aggregate + '('
      const prop = value.property
      text += prop + ')'
      context.hot.setDataAtCell(startRow, startCol, text)
      this.refreshChildPropertyPanel(startRow, startCol, endRow, endCol)
    }
  }
}
</script>

<style>
  .v-context, .v-context ul {
    border: 0px;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
  }

  .dataset-tree-node .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .data-source-btn {
    cursor: pointer;
    border:none;
    border-radius:0;
    padding: 6px 8px;
    background: #ffffff;
  }

  .data-source-btn:hover {
    background: #E4E7ED;
  }

  .el-tree-node__content svg {
    font-size: 14px;
  }
</style>
