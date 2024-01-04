<template>
  <div style="padding: 10px;">
    <el-card class="form-search-box-card">
      <el-form :inline="true" :model="form" size="small">
        <el-form-item label="菜单名称:">
          <el-input v-model="form.menuName"></el-input>
        </el-form-item>
        <el-form-item label="状态:">
          <el-select v-model="form.visible">
            <el-option label="全部" value=""></el-option>
            <el-option label="显示" value="1"></el-option>
            <el-option label="隐藏" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadMenuList()">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card id="system_menu_container" class="box-card" style="margin-top: 10px;">
      <el-button type="primary" size="small" style="margin-bottom: 10px;" @click="addRow(0)">添加菜单</el-button>
      <el-table
        v-if="height"
        :data="searchTableData"
        size="small"
        row-key="menuId"
        :height="height"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      >
        <el-table-column
          prop="menuName"
          label="菜单名称"
        />
        <el-table-column
          prop="url"
          label="菜单路径"
        />
        <el-table-column
          prop="component"
          label="菜单组件"
        />
        <el-table-column
          prop="icon"
          label="图标"
          align="center"
          width="120"
        >
        <template slot-scope="scope">
            <i v-if="scope.row.icon" :class="scope.row.icon" />
          </template>
        </el-table-column>
        <el-table-column
          prop="serialNumber"
          label="序号"
          align="center"
          width="120"
        />
        <el-table-column
          label="状态"
          align="center"
          width="80"
        >
          <template slot-scope="scope">
            <el-tag v-if="scope.row.visible===1" size="small">显示</el-tag>
            <el-tag v-else type="danger" size="small">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center"
          width="200"
        >
          <template slot-scope="scope">
            <el-link v-if="scope.row.menuType===0" type="primary" :underline="false" style="margin-right: 10px;" @click="addRow(scope.row.menuId)"><font style="font-size: 12px;"><i class="el-icon-circle-plus-outline" />新增</font></el-link>
            <el-link type="primary"  @click="editRow(scope.row)" :underline="false" style="margin-right: 10px;"><font style="font-size: 12px;"><i class="el-icon-edit" />编辑</font></el-link>
            <el-link type="primary" @click="removeRow(scope.row)" :underline="false"><font style="font-size: 12px;"><i class="el-icon-delete" />删除</font></el-link>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog
      :title="edit ? '编辑菜单':'添加菜单'"
      :visible.sync="dialogVisible"
      width="600px"
    >
      <el-form ref="ruleForm" :model="ruleForm" :rules="rules" label-width="100px" size="small">
        <el-form-item label="上级目录:">
          <el-select v-model="ruleForm.parentId" :disabled="ruleForm.menuType===0" style="width: 100%;">
            <el-option
              v-for="item in directory"
              :key="item.menuId"
              :label="item.menuName"
              :value="item.menuId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单类型:">
          <el-radio-group v-model="ruleForm.menuType" @change="handleMenuTypeChange">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称:" prop="menuName">
          <el-input v-model="ruleForm.menuName" />
        </el-form-item>
        <el-form-item label="菜单路径:" prop="url">
          <el-input v-model="ruleForm.url" />
        </el-form-item>
        <el-form-item label="菜单组件:" prop="component">
          <el-input :disabled="ruleForm.menuType===0" v-model="ruleForm.component" />
        </el-form-item>
        <el-form-item label="显示状态:">
          <el-radio-group v-model="ruleForm.visible">
            <el-radio :label="0">隐藏</el-radio>
            <el-radio :label="1">显示</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="显示顺序:" prop="serialNumber">
          <el-input v-model="ruleForm.serialNumber" />
        </el-form-item>
        <el-form-item label="菜单图标:">
          <icon-select v-model="ruleForm.icon" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveMenu()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import IconSelect from './components/icon-select'
import { addMenu, remove, modify, get, getMenuList } from '@/api/menu'
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
        parentId: 0,
        menuName: '',
        menuType: 1,
        url: '',
        component: '',
        visible: 1,
        serialNumber: 10
      },
      rules: {
        menuName: [
          { required: true, message: '菜单名称不能为空' }
        ],
        url: [
          { required: true, message: '路径不能为空' }
        ],
        component: [
          { required: true, message: '组件不能为空' }
        ],
        serialNumber: [
          { required: true, message: '显示顺序不能为空' }
        ]
      },
      menuList: [
        { menuId: 0, menuName: '根目录' }
      ],
      searchTableData: [],
      tableData: []
    }
  },
  mounted() {
    this.height = getHeight('system_menu_container') - 96
    this.loadMenuList()
  },
  methods: {
    loadMenuList() {
      const params = {}
      if(this.form.menuName) {
        params.menuName = this.form.menuName
      }
      if(this.form.visible === '0' || this.form.visible === '1') {
        params.visible = parseInt(this.form.visible)
      }
      const directory = []
      directory.push({ menuId: 0, menuName: '根目录' })
      getMenuList(params).then(res => {
        const data = res.data || []
        const mapping = {}
        for (const index in data) {
          const item = data[index]
          mapping[item.menuId] = item
          if (item.menuType === 0 && item.parentId === 0) {
            directory.push(item)
          }
        }
        const tableData = []
        for (const index in data) {
          const item = data[index]
          const parent = mapping[item.parentId]
          if (item.parentId === 0 || !parent) {
            tableData.push(item)
          } else {
            let children = parent.children
            if (children == null) {
              children = []
              parent.children = children
            }
            children.push(item)
          }
        }
        if(this.tableData.length === 0) {
          this.tableData = tableData
          this.directory = directory
        }
        this.searchTableData = tableData
      })
    },
    handleMenuTypeChange(val) {
      if(val === 0) {
       this.ruleForm.parentId = 0
       this.ruleForm.component = 'Layout'
      } else {
        if(!this.ruleForm.component || this.ruleForm.component === 'Layout') {
          this.ruleForm.component = 'demo/index'
        }
      }
    },
    addRow(menuId) {
      let serialNumber = 10
      if(menuId === 0 && this.tableData.length > 0) {
        serialNumber = this.tableData[this.tableData.length - 1].serialNumber + 1
      } else if(this.tableData.length > 0) {
        const item = this.tableData.find(function(e){
          return e.menuId === menuId
        })
        if(item) {
          if(item.children && item.children.length > 0) {
            serialNumber = item.children[item.children.length - 1].serialNumber + 1
          } else {
            serialNumber = item.serialNumber * 10 + 1
          }
        }
      }
      this.ruleForm = {
        parentId: menuId,
        menuName: '',
        menuType: 1,
        url: '',
        component: 'demo/index',
        visible: 1,
        serialNumber: serialNumber
      }
      this.edit = false
      this.dialogVisible = true
    },
    editRow(row) {
      this.ruleForm = {
        menuId: row.menuId,
        parentId: row.parentId,
        menuName: row.menuName || '',
        menuType: row.menuType,
        url: row.url  || '',
        component: row.component || '',
        visible: row.visible,
        serialNumber: row.serialNumber
      }
      this.edit = true
      this.dialogVisible = true
    },
    removeRow(row) {
      this.$confirm('确认删除菜单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        remove({menuId: row.menuId}).then(res => {
          this.$message({type: 'success', message: '删除成功!'})
          this.dialogVisible = false
        })
      })
    },
    saveMenu() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          const params = this.ruleForm
          if(this.edit) {
            modify(params).then(res => {
              this.$message({type: 'success', message: '修改成功!'})
              this.dialogVisible = false
            })
          } else {
            addMenu(params).then(res => {
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
