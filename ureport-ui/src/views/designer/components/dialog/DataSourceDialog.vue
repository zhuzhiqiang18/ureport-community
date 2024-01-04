<template>
  <el-dialog
    title="添加数据源"
    :visible.sync="customDataSetValue.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="600px"
  >
    <el-form ref="datasourceForm" :rules="rules" size="small" :model="form" label-width="80px" label-position="left">
      <el-form-item label="连接名称" prop="name">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="驱动名称" prop="driver">
        <g-autocomplete
          v-model="form.driver"
          style="width:100%"
          :placeholder="'选择数据源驱动'"
          :data="driverOptions"
        />
      </el-form-item>
      <el-form-item label="连接URL" prop="url">
        <el-input v-model="form.url"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" show-password></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" type="primary" @click="handleTestConnection">测试连接</el-button>
      <el-button size="small" type="primary" @click="addDataSource">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { testConnection } from '@/api/datasource'
import uuid from 'node-uuid'
export default {
  props: {
    customDataSetValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      driverOptions: [
        {value: 'com.mysql.cj.jdbc.Driver'},
      ],
      form: {
        name: '',
        driver: '',
        url: '',
        username: '',
        password: '',
      },
      rules: {
        name: [
          { required: true, message: '请输入连接名称', trigger: 'blur' },
        ],
        driver: [
          { required: true, message: '请输入驱动名称', trigger: 'change' }
        ],
        url: [
          { required: true, message: '请输入连接URL', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
      }
    }
  },
  watch: {
    customDataSetValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.customDataSetValue)
  },
  methods: {
    async init(val) {
      if (val.context && val.context.reportDef) {
        if(val.edit) {
          this.form = {
            name: val.datasource.name,
            driver: val.datasource.driver,
            url: val.datasource.url,
            username: val.datasource.username,
            password: val.datasource.password,
          }
        } else {
          this.form = {
            name: '',
            driver: '',
            url: '',
            username: '',
            password: '',
          }
        }
      }
    },
    addDataSource() {
      this.$refs['datasourceForm'].validate((valid) => {
        if (valid) {
          const form = this.form
          const datasources = this.customDataSetValue.context.reportDef.datasources || []
          const obj = datasources.find(item => item.name === form.name)
          if (obj && (!this.customDataSetValue.edit || (this.customDataSetValue.datasource && this.customDataSetValue.datasource.name !== form.name))) {
            this.$message.error('数据源名称重复')
            return
          }
          const params = {
            driverClassName: this.form.driver,
            url: this.form.url,
            userName: this.form.username,
            password: this.form.password
          }
          testConnection(params).then(res =>{
            if(res.data === 'success') {
              if(!this.customDataSetValue.context.reportDef.datasources) {
                this.customDataSetValue.context.reportDef.datasources = []
              }
              if(this.customDataSetValue.edit) {
                const datasource = this.customDataSetValue.datasource
                datasource.name = this.form.name
                datasource.driver = this.form.driver
                datasource.url = this.form.url
                datasource.username = this.form.username
                datasource.password = this.form.password
              } else {
                const datasources = this.customDataSetValue.context.reportDef.datasources
                datasources.push({
                  code: uuid.v1(),
                  name:this.form.name,
                  type: 'jdbc',
                  driver: this.form.driver,
                  url: this.form.url,
                  username: this.form.username,
                  password: this.form.password,
                  datasets: []
                })
              }
              this.customDataSetValue.dialogVisible = false
            } else {
              this.$message.error(res.data)
            }
          })
        }
      })
    },
    handleTestConnection() {
      this.$refs['datasourceForm'].validate((valid) => {
        if (valid) {
          const form = this.form
          const datasources = this.customDataSetValue.context.reportDef.datasources || []
          const obj = datasources.find(item => item.name === form.name)
          if (obj) {
            this.$message.error('数据源名称重复')
            return
          }
          const params = {
            driverClassName: this.form.driver,
            url: this.form.url,
            userName: this.form.username,
            password: this.form.password
          }
          testConnection(params).then(res =>{
            if(res.data === 'success') {
              this.$message({
                message: '连接成功',
                type: 'success'
              })
            } else {
              this.$message.error(res.data)
            }
          })
        }
      })
    }
  },

}
</script>
