<template>
  <div class="component-icon-btn" @click="handleOpen">
    <div class="component-icon-btn-icon" style="font-size: 12px;width: 32px;line-height: 18px;text-align: center;">导入</div>
    <el-dialog title="导入Excel模板"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    :before-close="beforeClose"
    width="600px">
      <el-upload
        ref="uploadExcelTemplateRef"
        action=""
        :multiple="false"
        accept=".xls,.xlsx"
        :on-change="changeFiles"
        :limit="1"
        :auto-upload="false"
        style="height: 80px;"
      >
        <el-button slot="trigger" size="small">选取文件</el-button>
        <el-button type="primary" style="margin-left: 10px;" size="small" @click="submitUpload">上传到服务器</el-button>
      </el-upload>
    </el-dialog>
  </div>
</template>
<script>
import { uploadExcelTemplate } from '@/api/designer'

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
      form: {
        file: null
      }
    }
  },
  mounted: function() {

  },
  methods: {
    handleOpen() {
      this.form.file = null
      this.dialogVisible = true
    },
    beforeClose(done) {
      if(this.form.file) {
        this.$refs.uploadExcelTemplateRef.clearFiles()
      }
      done()
    },
    changeFiles(file, fileList) {
      this.form.file = file
    },
    submitUpload() {
      if(this.form.file === null) {
        this.$message('请选择上传文件');
        return
      }
      const formData = new FormData()
      formData.append('file', this.form.file.raw)
      uploadExcelTemplate(formData).then((response) => {
        this.context.reportTable.loadReportDef(response.data)
        this.$refs.uploadExcelTemplateRef.clearFiles()
        this.dialogVisible = false
      })
    }
  }
}
</script>
