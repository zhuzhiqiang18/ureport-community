<template>
  <div>
    <el-button size="mini" type="primary" style="margin-bottom: 10px;" @click="addDataSetParam()">添加参数</el-button>
    <el-table 
    :data="parameters"
    :header-cell-style="()=>'background: #f5f7fa'"
    border  
    :height="160" 
    size="mini" 
    style="width: 100%">
      <el-table-column header-align="center" prop="name" label="参数名称" :show-overflow-tooltip="true" />
      <el-table-column header-align="center" prop="type" label="数据类型" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          {{ paramTypeOptions.find(ele => ele.value === scope.row.type).label }}
        </template>
      </el-table-column>
      <el-table-column header-align="center" prop="defaultValue" label="默认值" :show-overflow-tooltip="true" />
      <el-table-column header-align="center" align="center" label="操作" width="100">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="editDataSetParam(scope.row,scope.$index)"><span style="font-size:12px;margin-right: 10px">编辑</span></el-link>
          <el-link type="primary" :underline="false" @click="removeDataSetParam(scope.$index)"><span style="font-size:12px">移除</span></el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      title="参数配置"
      append-to-body
      :visible.sync="dialogVisible"
      width="400px"
    >
      <el-form :model="form" label-width="85px" ref="datasetParamRuleForm" :rules="rules" size="small">
        <el-form-item label="参数名称:" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="数据类型:">
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%;">
            <el-option v-for="item in paramTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="默认值:">
          <el-input v-model="form.defaultValue" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveDataSetParam()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: {
    parameters: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      edit: false,
      rowIndex: -1,
      dialogVisible: false,
      form: {
        name: '',
        type: 'String',
        defaultValue: '',
      },
      rules: {
        name:[{ required: true, message: '请输入参数名称', trigger: 'blur' }]
      },
      paramTypeOptions: [
        {value: 'String',label: '字符串'},
        {value: 'Integer',label: '整数'},
        {value: 'Float',label: '小数' },
        {value: 'Boolean',label: '布尔值'},
        {value: 'Date',label: '时间'},
        {value: 'List',label: '列表'}
      ],
      oldName: '',
    }
  },
  mounted: function() {

  },
  methods: {
    addDataSetParam() {
      this.edit = false
      this.rowIndex = -1
      this.form = {
        name: '',
        type: 'String',
        defaultValue: ''
      }
      this.dialogVisible = true
    },
    editDataSetParam(row, index) {
      this.edit = true
      this.rowIndex = index
      this.form = {
        name: row.name,
        type: row.type,
        defaultValue: row.defaultValue
      }
      this.dialogVisible = true
    },
    removeDataSetParam(index) {
      this.$confirm('确定删除参数?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.parameters.splice(index, 1)
      })
    },
    saveDataSetParam() {
      this.$refs.datasetParamRuleForm.validate((valid) => {
        if (valid) {
          const parameters = this.parameters
          const edit = this.edit
          const name = this.form.name
          const obj = parameters.find(ele => ele.name === name)
          if(obj) {
            if(!this.edit || (this.edit && parameters.indexOf(obj) !== this.rowIndex)) {
              this.$message.error('参数名称已存在')
              return
            }
          }
          if (edit) {
            parameters.splice(this.rowIndex, 1, this.form)
          } else {
            parameters.push(this.form)
          }
          this.dialogVisible = false
        }
      })
    }
  }
}
</script>

<style>

</style>
