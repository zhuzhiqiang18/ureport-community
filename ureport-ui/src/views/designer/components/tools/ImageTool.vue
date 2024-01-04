<template>
  <div class="component-icon-btn" @click="handleOpen">
    <div class="component-icon-btn-icon">
      <i class="icons icons-16 icons-16-picture"></i>
    </div>
    <el-dialog
    title="上传图片"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    :before-close="beforeClose"
    width="600px">
      <el-upload
        ref="uploadImageTemplateRef"
        action=""
        :multiple="false"
        accept=".jpg,.jpeg,.png,.gif"
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
import { uploadImageTemplate } from '@/api/designer'
export default {
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      form: {
        file: null
      },
      dialogVisible: false
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
        this.$refs.uploadImageTemplateRef.clearFiles()
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
      uploadImageTemplate(formData).then((response) => {
        this.$refs.uploadImageTemplateRef.clearFiles()
        this.dialogVisible = false
      })
    }
  }
}
</script>
