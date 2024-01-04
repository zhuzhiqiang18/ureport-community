<template>
  <div>
    <el-row class="property-row-item">
      <el-col :span="5" class="property-row-item-label">
        <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">链接</el-checkbox>
      </el-col>
      <el-col v-if="form.checked" :span="19">
        <el-input v-model="form.linkUrl" placeholder="请输入内容" size="mini" @change="handleChange" />
      </el-col>
    </el-row>
    <el-row v-if="form.checked" class="property-row-item">
      <el-col :span="5" class="property-row-item-label">
        <span>目标窗口</span>
      </el-col>
      <el-col v-if="form.checked" :span="8">
        <el-select v-model="form.linkTargetWindow" placeholder="请选择" size="mini">
          <el-option v-for="item in windowOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-col>
      <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
        <span>链接参数</span>
      </el-col>
      <el-col v-if="form.checked" :span="7">
        <el-button size="mini" style="width:100%" @click="dialogVisible=true">配置参数</el-button>
      </el-col>
    </el-row>
    <el-dialog title="URL参数配置" :visible.sync="dialogVisible" append-to-body :close-on-click-modal="false" width="600px">
      <el-button plain size="small" @click="addUrlParam()" style="margin-bottom: 10px;">添加参数</el-button>
      <el-table size="mini" border :data="form.linkParameters" style="width: 100%">
        <el-table-column header-align="center" prop="name" label="参数名称" width="180" show-overflow-tooltip />
        <el-table-column header-align="center" prop="value" label="参数表达式" show-overflow-tooltip />
        <el-table-column align="center" label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editUrlParam(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="removeUrlParam(scope.row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
    <el-dialog
      :title="paramItemEdit?'编辑参数':'添加参数'"
      :visible.sync="paramItemDialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="400px"
    >
      <el-form ref="property_url_rule_form" label-width="80px" :model="form" size="small" :rules="rules">
        <el-form-item label="参数名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="值表达式" prop="value">
          <el-input v-model="form.value" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="paramItemDialogVisible=false">确 定</el-button>
        <el-button type="primary" size="mini" @click="saveUrlParam()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

export default {
  props: {
    propertyCondition: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      dialogVisible: false,
      windowOptions: [
        {label: '新窗口', value: '_blank'},
        {label: '当窗口', value: '_self'},
        {label: '父窗口', value: '_parent'},
        {label: '顶层窗口', value: '_top'}
      ],
      paramItemEdit: false,
      paramItemIndex: -1,
      paramItemDialogVisible: false,
      form: {
        checked: false,
        linkUrl: '',
        linkTargetWindow: '_blank',
        linkParameters: [],
        name: '',
        value: ''
      },
      rules: {
        'name':[{required: true, message: '请输入参数名称'}],
        'value': [{required: true, message: '请输入值表达式'}]
      }
    }
  },
  watch: {
    propertyCondition(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.propertyCondition)
  },
  methods: {
    init(val) {
      if (val.linkUrl) {
        this.form.checked = true
        this.form.linkUrl = val.linkUrl
        this.form.linkTargetWindow = val.linkTargetWindow
        this.form.linkParameters = val.linkParameters || []
      } else {
        this.form.checked = false
        this.form.linkUrl = ''
        this.form.linkTargetWindow = '_blank'
        this.form.linkParameters = []
      }
    },
    handleCheck(val) {
      if (val) {
        this.propertyCondition.linkUrl = this.form.linkUrl
        this.propertyCondition.linkTargetWindow = this.form.linkTargetWindow
        this.propertyCondition.linkParameters = this.form.linkParameters
      } else {
        this.propertyCondition.linkUrl = null
        this.propertyCondition.linkTargetWindow = null
        this.propertyCondition.linkParameters = null
      }
    },
    handleChange(val) {
      if (this.form.checked) {
        this.propertyCondition.linkUrl = this.form.linkUrl
        this.propertyCondition.linkTargetWindow = this.form.linkTargetWindow
        this.propertyCondition.linkParameters = this.form.linkParameters
      }
    },
    addUrlParam() {
      this.paramItemEdit = false
      this.paramItemDialogVisible = true
      this.$nextTick(()=> {
        this.$refs['property_url_rule_form'].resetFields()
      })
    },
    editUrlParam(param) {
      this.paramItemEdit = true
      this.paramItemIndex = this.form.linkParameters.indexOf(param)
      this.form.name = param.name
      this.form.value = param.value
      this.paramItemDialogVisible = true
    },
    removeUrlParam(param) {
      const paramItemIndex = this.form.linkParameters.indexOf(param)
      this.form.linkParameters.splice(paramItemIndex, 1)
    },
    saveUrlParam() {
      this.$refs['property_url_rule_form'].validate((valid) => {
        if (valid) {
          const linkParameter = {
            name: this.form.name,
            value: this.form.value
          }
          const linkParameters = this.form.linkParameters
          if (this.paramItemEdit) {
            linkParameters.splice(this.paramItemIndex, 1, linkParameter)
          } else {
            linkParameters.push(linkParameter)
          }
          this.handleChange(true)
          this.paramItemDialogVisible = false
        }
      })
    }
  }
}
</script>
