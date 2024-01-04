<template>
  <el-dialog
    title="URL参数配置"
    :visible.sync="urlParameterItemDialog.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="400px"
  >
    <el-form :model="form" size="mini" label-width="120px">
      <el-form-item label="参数名称:">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="参数值表达式:">
        <el-input v-model="form.value" />
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" size="mini" @click="save">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>

export default {
  props: {
    urlParameterItemDialog: {
      type: Object,
      required: true
    },
    afterSave: {
      type: Function,
      required: true
    }
  },
  data: function() {
    return {
      form: {
        name: '',
        value: ''
      }
    }
  },
  watch: {
    urlParameterItemDialog: function(val) {
      this.form.name = this.urlParameterItemDialog.urlParameterItem.name
      this.form.value = this.urlParameterItemDialog.urlParameterItem.value
    }
  },
  mounted: function() {

  },
  methods: {
    save: function() {
      if (this.form.name === '' || this.form.value === '') {
        this.$message('参数项请输入完整')
        return
      }
      this.urlParameterItemDialog.urlParameterItem = this.form
      this.form = {
        name: '',
        value: ''
      }
      this.afterSave()
    }
  }

}
</script>
