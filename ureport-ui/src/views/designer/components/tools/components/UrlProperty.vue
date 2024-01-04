<template>
  <div>
    <el-form :model="form" label-position="left" label-width="80px" size="small">
      <el-form-item label="链接配置:">
        <el-input v-model="form.linkUrl" placeholder="支持表达式如:${# == '1' ? 'a.jsp' : 'b.jsp'}" @change="handleLinkUrlChange" />
      </el-form-item>
      <el-form-item label="目标窗口:">
        <el-select v-model="form.linkTargetWindow" placeholder="请选择" style="width: 100%;" @change="handleLinkTargetChange">
          <el-option
            v-for="item in linkTargetOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="参数配置:">
        <el-button plain size="small" @click="addUrlParam()">添加参数</el-button>
      </el-form-item>
    </el-form>
    <el-table size="small" border :data="form.linkParameters" height="300" style="width: 100%;margin-top: 10px;">
      <el-table-column prop="name" label="参数名称" width="180" header-align="center" />
      <el-table-column prop="value" label="参数表达式" header-align="center" />
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="editUrlParam(scope.row)">编辑</el-button>
          <el-button type="text" size="small" @click="removeUrlParam(scope.row)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      :title="edit?'编辑参数':'添加参数'"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="400px"
    >
      <el-form label-width="80px" :model="form.linkParameter" size="small">
        <el-form-item label="参数名称:">
          <el-input v-model="form.linkParameter.name" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="值表达式:">
          <el-input v-model="form.linkParameter.value" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible=false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveUrlParam()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

export default {
  name: 'UrlProperty',
  props: {
    cellDef: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      oldName: '',
      edit: false,
      dialogVisible: false,
      linkTargetOptions: [
        { value: '_blank', label: '新窗口' },
        { value: '_self', label: '当前窗口' },
        { value: '_parent', label: '父窗口' },
        { value: '_top', label: '顶层窗口' },
        { value: '_iframe', label: '弹出窗口' },
      ],
      form: {
        linkUrl: '',
        linkTargetWindow: '_blank',
        linkParameters: [],
        linkParameter: {
          name: '',
          value: ''
        }
      }
    }
  },
  watch: {
    cellDef: function(val) {
      this.refresh(val)
    }
  },
  mounted: function() {
    this.refresh(this.cellDef)
  },
  methods: {
    refresh(cellDef) {
      this.form.linkUrl = cellDef.linkUrl || ''
      if (!cellDef.linkTargetWindow) {
        cellDef.linkTargetWindow = '_blank'
      }
      this.form.linkTargetWindow = cellDef.linkTargetWindow
      if (!cellDef.linkParameters) {
        cellDef.linkParameters = []
      }
      this.form.linkParameters = cellDef.linkParameters
    },
    handleLinkUrlChange(value) {
      this.cellDef.linkUrl = value
    },
    handleLinkTargetChange(value) {
      this.cellDef.linkTargetWindow = value
    },
    addUrlParam() {
      this.form.linkParameter = {
        name: '',
        value: ''
      }
      this.dialogVisible = true
    },
    editUrlParam(row) {
      this.edit = true
      this.oldName = row.name
      this.form.linkParameter = JSON.parse(JSON.stringify(row))
      this.dialogVisible = true
    },
    removeUrlParam(row) {
      const index = this.form.linkParameters.indexOf(row)
      this.form.linkParameters.splice(index, 1)
    },
    saveUrlParam() {
      const linkParameter = this.form.linkParameter
      if (linkParameter.name == '') {
        this.$message.error('请输入参数名称')
        return
      }
      if (linkParameter.value == '') {
        this.$message.error('请输入参数表达式')
        return
      }
      const linkParameters = this.form.linkParameters
      const obj = linkParameters.find(function(ele) {
        return ele.name === linkParameter.name
      })
      if ((obj && !this.edit) || (obj && this.edit && obj.name !== this.oldName)) {
        this.$message.error('参数名称重复')
        return
      }
      if (this.edit) {
        for (let k = 0; k < linkParameters.length; k++) {
          const item = linkParameters[k]
          if (item.name === this.oldName) {
            linkParameters.splice(k, 1, linkParameter)
          }
        }
      } else {
        linkParameters.push(linkParameter)
      }
      this.dialogVisible = false
    }
  }
}
</script>

<style>
</style>
