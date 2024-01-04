<template>
  <el-dropdown trigger="click" @command="addDataSource" style="float: left;">
    <span class="el-dropdown-link">
      <div class="component-icon-btn">
        <div class="component-icon-btn-icon" style="width: 60px;font-size: 12px;line-height: 19px;float: left;">
          <i class="icons icons-16 icons-16-func_database"></i>
          <span style="float: right;">数据源</span>
        </div>
        <div style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn"></div>
        </div>
      </div>
    </span>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item command="jdbc">
        <i class="icons icons-16 icons-16-database" style="font-size: 16px;color:#000000;display: inline-block;vertical-align: text-top;"></i>添加数据源
      </el-dropdown-item>
      <el-dropdown-item command="buildin">
        <i class="icons icons-16 icons-16-func_database" style="display: inline-block;vertical-align: text-top;"></i>内置数据源
      </el-dropdown-item>
      <el-dropdown-item command="api">
        <i class="icons icons-16 icons-16-api_database" style="display: inline-block;vertical-align: text-top;"></i>API数据源
      </el-dropdown-item>
    </el-dropdown-menu>
    <data-source-dialog :custom-data-set-value="customDataSetValue"  />
    <buildin-data-source-dialog :buildin-data-set-value="buildinDataSetValue"  />
    <api-data-source-dialog :api-data-set-value="apiDataSetValue"  />
  </el-dropdown>
</template>

<script>
  import DataSourceDialog from '../dialog/DataSourceDialog'
  import BuildinDataSourceDialog from '../dialog/BuildinDataSourceDialog'
  import ApiDataSourceDialog from '../dialog/ApiDataSourceDialog'

  import { undoManager } from '../Utils.js'

  export default {
    name: 'AlignLeftTool',
    components: {
      DataSourceDialog,
      BuildinDataSourceDialog,
      ApiDataSourceDialog
    },
    props: {
      context: {
        type: Object,
        required: true
      }
    },
    data: function() {
      return {
        customDataSetValue: {
          dialogVisible: false,
        },
        buildinDataSetValue: {
          dialogVisible: false,
        },
        apiDataSetValue: {
          dialogVisible: false,
        }
      }
    },
    mounted: function() {

    },
    methods: {
      addDataSource(v) {
        if(v === 'jdbc') {
          this.addJdbcDataSource()
        } else if(v === 'buildin') {
          this.addBuildinDataSource()
        } else if(v === 'api') {
          this.addApiDataSource()
        }
      },
      addJdbcDataSource() {
        this.customDataSetValue = {
          dialogVisible: true,
          context: this.context
        }
      },
      addBuildinDataSource() {
        this.buildinDataSetValue = {
          dialogVisible: true,
          context: this.context
        }
      },
      addApiDataSource() {
        this.apiDataSetValue = {
          dialogVisible: true,
          context: this.context
        }
      }
    }
  }
</script>

<style>
</style>
