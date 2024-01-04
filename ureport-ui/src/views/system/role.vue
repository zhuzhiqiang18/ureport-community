<template>
  <div style="padding: 10px;">
    <el-card class="form-search-box-card">
      <el-form :inline="true" :model="form" size="small">
        <el-form-item label="角色名称:">
          <el-input v-model="form.roleName"></el-input>
        </el-form-item>
        <el-form-item label="状态:">
          <el-select v-model="form.visible">
            <el-option label="全部" value=""></el-option>
            <el-option label="显示" value="1"></el-option>
            <el-option label="隐藏" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRoleList()">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card id="system_role_container" class="box-card" style="margin-top: 10px;">
      <el-button type="primary" size="small" style="margin-bottom: 10px;" @click="addRow(0)">添加角色</el-button>
      <el-table
        v-if="height"
        :data="tableData"
        size="small"
        :height="height"
      >
        <el-table-column
          prop="roleName"
          label="角色名称"
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
      :title="edit ? '编辑角色':'添加角色'"
      :visible.sync="dialogVisible"
      width="600px"
    >
      <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" size="small">
        <el-form-item label="角色名称:" prop="roleName">
          <el-input v-model="ruleForm.roleName" />
        </el-form-item>
        <el-form-item label="角色状态:">
          <el-radio-group v-model="ruleForm.visible">
            <el-radio :label="0">停用</el-radio>
            <el-radio :label="1">正常</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色菜单:" style="height: 300px;overflow: auto;">
          <el-tree
            ref="roleTree"
            :data="treeData"
            show-checkbox
            node-key="menuId"
            :props="defaultProps">
          </el-tree>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveRole()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import IconSelect from './components/icon-select'
import { addRole, removeRole, modifyRole, getRole, getRoleList } from '@/api/role'
import { getMenuList } from '@/api/menu'
import {getHeight} from '@/utils/height'
export default {
  name: 'Menu',
  components: {
    iconSelect: IconSelect
  },
  data() {
    return {
      height: 0,
      form: {
        menuName: '',
        visible: '',
      },
      directory: [],
      dialogVisible: false,
      edit: false,
      ruleForm: {
        roleName: '',
        visible: 1,
      },
      rules: {
        roleName: [
          { required: true, message: '角色名称不能为空' }
        ]
      },
      tableData: [],
      treeData:[],
      defaultProps: {
        children: 'children',
        label: 'menuName'
      }
    }
  },
  mounted() {
    this.height = getHeight('system_role_container') - 96
    this.loadRoleList()
    this.loadMenuList()
  },
  methods: {
    loadMenuList() {
      getMenuList({}).then(res => {
        const data = res.data || []
        const mapping = {}
        for (const index in data) {
          const item = data[index]
          mapping[item.menuId] = item
        }
        const treeData = []
        for (const index in data) {
          const item = data[index]
          const parent = mapping[item.parentId]
          if (item.parentId === 0 || !parent) {
            treeData.push(item)
          } else {
            let children = parent.children
            if (children == null) {
              children = []
              parent.children = children
            }
            children.push(item)
          }
        }
        this.treeData = treeData
      })
    },
    loadRoleList() {
      const params = {}
      if(this.form.roleName) {
        params.roleName = this.form.roleName
      }
      if(this.form.visible === '0' || this.form.visible === '1') {
        params.visible = parseInt(this.form.visible)
      }
      getRoleList(params).then(res => {
        this.tableData = res.data || []
      })
    },
    addRow() {
      this.ruleForm = {
        userName: '',
        visible: 1,
      }
      this.edit = false
      this.dialogVisible = true
    },
    async editRow(row) {
      this.ruleForm = {
        roleId: row.roleId,
        roleName: row.roleName || '',
        visible: row.visible,
      }
      const res = await getRole({roleId: row.roleId})
      const role = res.data || {}
      const menuIds = role.menuIds || []
      this.dialogVisible = true
      this.edit = true
      const that = this
      setTimeout(function(){
        that.$refs.roleTree.setCheckedKeys([]);
        if(menuIds.length > 0) {
          that.$refs.roleTree.setCheckedKeys(menuIds);
        }
      },100)
    },
    removeRow(row) {
      this.$confirm('确认删除角色?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeRole({roleId: row.roleId}).then(res => {
          this.$message({type: 'success', message: '删除成功!'})
          this.dialogVisible = false
        })
      })
    },
    saveRole() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          const params = this.ruleForm
          const menuIds = this.$refs.roleTree.getCheckedKeys()
          params.menuIds = menuIds
          if(this.edit) {
            modifyRole(params).then(res => {
              this.$message({type: 'success', message: '修改成功!'})
              this.dialogVisible = false
            })
          } else {
            addRole(params).then(res => {
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
