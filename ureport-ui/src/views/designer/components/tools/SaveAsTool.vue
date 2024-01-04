<template>
  <div class="component-icon-btn" @click="handleSave">
    <div class="component-icon-btn-icon" style="line-height: 18px;font-size: 12px;width: 46px;">
      <span>另存为</span>
    </div>
    <el-dialog title="保存报表"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="400px">
      <el-form :rules="rules" ref="saveReprotRuleForm"  :model="form" label-width="90px" size="small">
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
        value: '',
      },
      rules: {
        value: [
          { required: true, message: '请输入报表名称', trigger: 'blur' },
        ],
      },
      fileName: ''
    }
  },
  mounted: function() {

  },
  methods: {
    handleSave(isNewSave) {
      this.dialogVisible = true
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
      const content = await tableToXml(this.context)
      saveReportFile({
        content,
        name: this.fileName,
        oldName: ''
      }).then(response => {
        this.context.reportTable.loadFile(this.fileName)
        this.dialogVisible = false
        this.$message({message: '保存成功',type: 'success'})
      })
    }
  }
}
</script>
