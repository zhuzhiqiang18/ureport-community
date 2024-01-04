<template>
  <div class="component-icon-btn" @click="handleSave">
    <div class="component-icon-btn-icon" style="line-height: 18px;font-size: 12px;width: 32px;">
      <span>保存</span>
    </div>
    <el-dialog
      title="保存报表"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="400px"
    >
      <el-form ref="saveReprotRuleForm" :rules="rules" :model="form" label-width="90px" size="small">
        <el-form-item label="报表名称:" prop="value">
          <el-input v-model="form.value" placeholder="请输入内容" size="small" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" size="small" @click="save">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { tableToXml } from '../Utils.js'
import { saveReportFile } from '@/api/designer'

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
        value: ''
      },
      rules: {
        value: [
          { required: true, message: '请输入报表名称', trigger: 'blur' }
        ]
      },
      fileName: ''
    }
  },
  mounted: function() {

  },
  methods: {
    handleSave() {
      this.fileName = ''
      const reportFullName = this.context.reportDef.reportFullName
      if (reportFullName === 'classpath:templates/template.ureport.xml') {
        this.dialogVisible = true
      } else {
        this.fileName = reportFullName
        this.doSave()
      }
    },
    save() {
      this.$refs['saveReprotRuleForm'].validate((valid) => {
        if (valid) {
          this.fileName = 'file:' + this.form.value + '.ureport.xml'
          this.doSave()
        }
      })
    },
    async doSave() {
      const oldFileName = this.context.reportDef.reportFullName
      const content = await tableToXml(this.context)
      saveReportFile({
        content,
        name: this.fileName,
        oldName: oldFileName
      }).then(response => {
        this.context.reportTable.loadFile(this.fileName)
        this.dialogVisible = false
        this.$message({ message: '保存成功', type: 'success' })
      })
    }
  }
}
</script>
