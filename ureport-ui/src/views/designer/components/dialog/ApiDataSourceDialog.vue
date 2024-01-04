<template>
  <el-dialog
    title="添加API数据源"
    :visible.sync="apiDataSetValue.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="600px"
  >
  <el-form :rules="rules" ref="apiRuleForm" :model="form" label-position="left" label-width="100px" size="small" style="margin-bottom: 10px;">
    <el-form-item label="数据源名称:" prop="name">
      <el-input v-model="form.name"></el-input>
    </el-form-item>
    <el-form-item label="请求地址:" prop="url">
      <el-input v-model="form.url"></el-input>
    </el-form-item>
    <el-form-item label="请求方式:" prop="method">
      <el-select v-model="form.method" placeholder="">
        <el-option label="GET" value="GET"></el-option>
        <el-option label="POST" value="POST"></el-option>
      </el-select>
    </el-form-item>
  </el-form>
  <el-tabs :value="'first'">
    <el-tab-pane label="头部参数" name="first">
      <headers-dialog :headers="headers" />
    </el-tab-pane>
    <el-tab-pane label="请求参数" name="second">
      <data-set-parameter-dialog :parameters="parameters" />
    </el-tab-pane>
  </el-tabs>
  <span slot="footer" class="dialog-footer">
     <el-button size="small" @click="apiDataSetValue.dialogVisible = false">取 消</el-button>
    <el-button size="small" type="primary" @click="saveDataSource">确 定</el-button>
  </span>
  </el-dialog>
</template>

<script>
import { selectBuildinDruidList } from '@/api/datasource'
import DataSetParameterDialog from './DataSetParameterDialog'
import HeadersDialog from './HeadersDialog'
import { previewApiData } from '@/api/datasource'

export default {
  components: {
    DataSetParameterDialog,
    HeadersDialog
  },
  props: {
    apiDataSetValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      edit: false,
      datasource: null,
      headers: [],
      parameters: [],
      form: {
        name: '',
        url: '',
        method: 'POST'
      },
      rules: {
        name: [
          { required: true, message: '请输入数据源名称', trigger: 'blur' },
        ],
        url: [
          { required: true, message: '请输入请求连接', trigger: 'blur' },
        ],
        method: [
          { required: true, message: '请选择请求方式', trigger: 'blur' },
        ],
      },
    }
  },
  watch: {
    apiDataSetValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.apiDataSetValue)
  },
  methods: {
    init(val) {
      if (val.context && val.context.reportDef) {
        if (val.edit) {
          this.edit = true
          const datasource = val.datasource
          this.datasource = datasource
          this.form.name = datasource.name
          this.form.url = datasource.url
          this.form.method = datasource.method
          this.parameters = datasource.parameters || []
          this.headers = datasource.headers || []
        } else {
          this.edit = false
          this.datasource = null
          this.form.name = ''
          this.form.url = ''
          this.form.method = 'POST'
          this.parameters = [],
          this.headers = []
        }
      }
    },
    saveDataSource() {
      this.$refs['apiRuleForm'].validate((valid) => {
        if (valid) {
          const datasources = this.apiDataSetValue.context.reportDef.datasources || []
          const obj = datasources.find(item => item.name === this.form.name)
          if (obj) {
            if(!this.edit || (this.edit && obj.name != this.datasource.name)) {
              this.$message.error(`已创建数据源${obj.name}`)
              return
            }
          }
          const ds = {
            name: this.form.name,
            method: this.form.method,
            type: 'api',
            url: this.form.url,
            parameters: this.parameters,
            headers: this.headers,
            datasets: []
          }
          const param = {
            url: this.form.url,
            method: this.form.method,
            parameters: this.parameters,
            headers: this.headers,
          }
          previewApiData(param).then(res => {
            const datasetNameMap = {}
            for (const index in datasources) {
              const datasource = datasources[index]
              if(!this.edit || (this.edit && datasource.name !== this.datasource.name)) {
                const datasets = datasource.datasets || []
                for (const dataset of datasets) {
                  datasetNameMap[dataset.name] = true
                }
              }
            }
            const data = res.data || []
            for (const dataset of data) {
              if(datasetNameMap[dataset.name]) {
                this.$message.error(`已创建数据集${dataset.name}`)
                return
              } else {
                const d = this.buildDataSet(dataset)
                if(d) {
                  ds.datasets.push(d)
                }
              }
            }
            if(this.edit) {
              const index = datasources.indexOf(this.datasource)
              datasources.splice(index, 1, ds)
            } else {
              datasources.push(ds)
            }
            this.apiDataSetValue.dialogVisible = false
          })
        }
      })
    },
    buildDataSet(object) {
      let name = object.name
      let data = object.data
      if(data && data.length > 0) {
        const keys = Object.keys(data[0])
        const dataset = {
          name: name,
          fields: []
        }
        for(const key of keys) {
          dataset.fields.push({
            name: key,
            type: null,
          })
        }
        return dataset
      }
      return null
    },
  }
}
</script>
