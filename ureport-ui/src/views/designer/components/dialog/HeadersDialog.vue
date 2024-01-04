<template>
  <div>
    <el-button size="mini" type="primary" style="margin-bottom: 10px;" @click="addDataSetParam()">添加参数</el-button>
    <el-table
    :data="headers"
    :header-cell-style="()=>'background: #f5f7fa'"
    border
    :height="160"
    size="mini"
    style="width: 100%">
      <el-table-column header-align="center" prop="name" label="参数名称" :show-overflow-tooltip="true" />
      <el-table-column header-align="center" prop="value" label="值" :show-overflow-tooltip="true" />
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
        <el-form-item label="参数值:" prop="value">
          <el-input v-model="form.value" />
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
    headers: {
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
        value: '',
      },
      rules: {
        name:[{ required: true, message: '请输入参数名称', trigger: 'blur' }],
        value:[{ required: true, message: '请输入参数值', trigger: 'blur' }]
      },
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
        value: ''
      }
      this.dialogVisible = true
    },
    editDataSetParam(row, index) {
      this.edit = true
      this.rowIndex = index
      this.form = {
        name: row.name,
        value: row.value
      }
      this.dialogVisible = true
    },
    removeDataSetParam(index) {
      this.$confirm('确定删除参数?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.headers.splice(index, 1)
      })
    },
    saveDataSetParam() {
      this.$refs.datasetParamRuleForm.validate((valid) => {
        if (valid) {
          const headers = this.headers
          const edit = this.edit
          const name = this.form.name
          if (edit) {
            headers.splice(this.rowIndex, 1, this.form)
          } else {
            headers.push(this.form)
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
