<template>
  <div style="padding: 10px;">
    <el-card class="form-search-box-card">
      <el-form :inline="true" :model="form" size="small">
        <el-form-item label="用户名称:">
          <el-input v-model="form.loginName"></el-input>
        </el-form-item>
        <el-form-item label="状态:">
          <el-select v-model="form.visible">
            <el-option label="全部" value=""></el-option>
            <el-option label="正常" value="1"></el-option>
            <el-option label="停用" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadUserList()">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card id="system_user_container" class="box-card" style="margin-top: 10px;">
      <el-button type="primary" size="small" style="margin-bottom: 10px;" @click="addRow()">添加用户</el-button>
      <el-table
        v-if="height"
        :data="tableData"
        size="small"
        :height="height"
      >
        <el-table-column
          prop="loginName"
          label="用户名称"
        />
        <el-table-column
          prop="userName"
          label="用户昵称"
          width="180"
        />
        <el-table-column
          label="状态"
          align="center"
          width="80"
        >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.visible===1" size="small">正常</el-tag>
            <el-tag v-else type="danger" size="small">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          width="200"
        >
          <template slot-scope="scope">
            <el-link type="primary"  @click="editRow(scope.row)" :underline="false" style="margin-right: 10px;"><font style="font-size: 12px;"><i class="el-icon-edit" />编辑</font></el-link>
            <el-link type="primary" @click="removeRow(scope.row)" :underline="false"><font style="font-size: 12px;"><i class="el-icon-delete" />删除</font></el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog
      :title="edit ? '编辑用户':'添加用户'"
      :visible.sync="dialogVisible"
      width="600px"
    >
      <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" size="small">
        <el-form-item label="用户昵称:" prop="userName">
          <el-input v-model="ruleForm.userName" />
        </el-form-item>
        <el-form-item v-if="!edit" label="用户名称:" prop="loginName">
          <el-input v-model="ruleForm.loginName" />
        </el-form-item>
        <el-form-item v-if="!edit" label="用户密码:" prop="password">
          <el-input v-model="ruleForm.password" show-password/>
        </el-form-item>
        <el-form-item label="用户状态:">
          <el-radio-group v-model="ruleForm.visible">
            <el-radio :label="0">停用</el-radio>
            <el-radio :label="1">正常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户角色:" style="height: 200px;overflow: auto;">
          <el-checkbox-group v-model="ruleForm.roleIds">
            <el-checkbox v-for="item in roleList" :label="item.roleId">{{item.roleName}}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveUser()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import IconSelect from './components/icon-select'
import { getRoleList } from '@/api/role'
import { addUser, removeUser, modifyUser, getUser, getUserList } from '@/api/user'

import {getHeight} from '@/utils/height'
import { validUsername } from '@/utils/validate'

export default {
  name: 'Menu',
  components: {
    iconSelect: IconSelect
  },
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!validUsername(value)) {
        callback(new Error('请输入正确的用户名'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能少于6位'))
      } else {
        callback()
      }
    }
    return {
      height: 0,
      form: {
        loginName: '',
        visible: '',
      },
      directory: [],
      dialogVisible: false,
      edit: false,
      ruleForm: {
        userName: '',
        loginName: '',
        password: '',
        visible: 1,
        roleIds: [],
      },
      rules: {
        userName: [
          { required: true, message: '用户昵称不能为空' }
        ],
        loginName: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      tableData: [],
      roleList:[],
      defaultProps: {
        children: 'children',
        label: 'menuName'
      }
    }
  },
  mounted() {
    this.height = getHeight('system_user_container') - 96
    this.loadRoleList()
    this.loadUserList()
  },
  methods: {
    loadRoleList() {
      getRoleList({}).then(res => {
        this.roleList = res.data || []
      })
    },
    loadUserList() {
      const params = {}
      if(this.form.loginName) {
        params.loginName = this.form.loginName
      }
      if(this.form.visible === '0' || this.form.visible === '1') {
        params.visible = parseInt(this.form.visible)
      }
      getUserList(params).then(res => {
        this.tableData = res.data || []
      })
    },
    addRow() {
      this.ruleForm = {
        userName: '',
        loginName: '',
        password: '',
        visible: 1,
        roleIds: [],
      }
      this.edit = false
      this.dialogVisible = true
    },
    async editRow(row) {
      const res = await getUser({userId: row.userId})
      const user = res.data || {}
      const roleIds = user.roleIds || []
      this.ruleForm = {
        userName: row.userName,
        visible: 1,
        roleIds: roleIds,
      }
      this.edit = true
      this.dialogVisible = true
    },
    removeRow(row) {
      this.$confirm('确认删除角色?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeUser({userId: row.userId}).then(res => {
          this.$message({type: 'success', message: '删除成功!'})
          this.dialogVisible = false
        })
      })
    },
    saveUser() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          const params = this.ruleForm
          if(this.edit) {
            modifyUser(params).then(res => {
              this.$message({type: 'success', message: '修改成功!'})
              this.dialogVisible = false
            })
          } else {
            addUser(params).then(res => {
              this.$message({type: 'success', message: '添加成功!'})
              this.dialogVisible = false
            })
          }
        }
      })
    }
  }
}
</script>
