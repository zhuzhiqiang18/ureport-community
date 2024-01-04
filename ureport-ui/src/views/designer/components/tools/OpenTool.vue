<template>
  <div class="component-icon-btn" @click="handleOpen">
    <div class="component-icon-btn-icon" style="line-height: 18px;font-size: 12px;width: 32px;">
      <span>打开</span>
    </div>
    <el-dialog title="打开报表"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="800px"
    >
      <div 
      v-loading="loading"
      element-loading-text="拼命加载中"
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(0, 0, 0, 0.8)"
      style="height: 400px;">
        <el-table
        size="small"
        v-if="reportFiles.length > 0"
        :height="400"
        :data="reportFiles"
        border
        :header-cell-style="()=>'background: #f5f7fa'"
        style="width: 100%;">
          <el-table-column prop="name" label="报表文件名称" />
          <el-table-column prop="updateDate" label="更新时间" width="160" align="center" />
          <el-table-column label="操作" width="100" align="center">
            <template slot-scope="scope">
              <el-link  :underline="false" type="primary" @click="handleEdit(scope.row)"><span style="font-size: 12px;">打开</span></el-link>
              <el-link  :underline="false" type="primary" @click="handleRemove(scope.row)" style="margin-left: 10px;"><span style="font-size: 12px;">删除</span></el-link>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getReports, deleteReportFile } from '@/api/designer'

export default {
  props: {
    context: {
      type: Object,
      required: true
    }
  },

  data: function() {
    return {
      dialogVisible: false,
      loading: false,
      value: '',
      fileName: '',
      reportFiles: []
    }
  },
  mounted: function() {

  },
  methods: {
    handleOpen() {
      this.loading = true
      this.reportFiles = []
      getReports({}).then(res => {
        this.reportFiles = res.data || []
        this.loading = false
      })
      this.dialogVisible = true
    },
    handleEdit(row) {
      const fileName = 'file:' + row.name
      this.context.reportTable.loadFile(fileName)
      this.dialogVisible = false
    },
    handleRemove(row) {
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const that = this
        const fullFile = 'file:' + row.name
        deleteReportFile({ file: fullFile }).then(response => {
          const index = that.reportFiles.indexOf(row)
          that.reportFiles.splice(index, 1)
        })
      })
    }
  }
}
</script>
