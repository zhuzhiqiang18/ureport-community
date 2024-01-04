<template>
  <el-dialog
    title="URL参数配置"
    :visible.sync="urlDialog.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="600px"
  >
    <el-button plain size="mini" @click="addParameter">添加参数</el-button>
    <el-table size="mini" :data="urlParameterDialog.tableData" border style="width: 100%;margin-top:10px">
      <el-table-column prop="name" align="center" label="参数名称" width="180" />
      <el-table-column prop="value" align="center" label="参数值表达式" width="180" />
      <el-table-column align="center" label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="editParameter(scope.$index,scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="removeParameter(scope.$index)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
import MyURLParameterItemDialog from './URLParameterItemDialog'

export default {
  components: {
    'myURLParameterItemDialog': MyURLParameterItemDialog
  },
  props: {
    urlDialog: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      delDialogVisible: false,
      edit: false,
      editIndex: -1,
      urlParameterItemDialog: {
        dialogVisible: false,
        urlParameterItem: {
          name: '',
          value: ''
        }
      }
    }
  },
  mounted: function() {

  },
  methods: {
    addParameter: function() {
      this.edit = false
      this.urlParameterItemDialog = {
        dialogVisible: true,
        urlParameterItem: {
          name: '',
          value: ''
        }
      }
    },
    editParameter: function(index, urlParameterItem) {
      this.edit = true
      this.editIndex = index
      this.urlParameterItemDialog = {
        dialogVisible: true,
        urlParameterItem: urlParameterItem
      }
    },
    removeParameter: function(index) {
      this.delDialogVisible = true
      this.editIndex = index
    },
    deleteParameter: function() {
      this.urlParameterDialog.tableData.splice(this.editIndex, 1)
      this.delDialogVisible = false
    },
    afterSave: function() {
      this.urlParameterItemDialog.dialogVisible = false
      const urlParameterItem = this.urlParameterItemDialog.urlParameterItem
      if (!this.edit) {
        this.urlParameterDialog.tableData.push(urlParameterItem)
      } else {
        this.urlParameterDialog.tableData.splice(this.editIndex, 1, urlParameterItem)
        this.editIndex = -1
      }
    }
  }
}
</script>


<style>
</style>
