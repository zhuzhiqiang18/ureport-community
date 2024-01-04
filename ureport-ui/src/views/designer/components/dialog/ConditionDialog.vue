<template>
  <div>
    <el-link style="margin-right: 15px;" @click="addCondition">新增</el-link>
    <el-link style="margin-right: 15px;" :disabled="this.editIndex===-1" @click="editCondition">编辑</el-link>
    <el-link :disabled="this.editIndex===-1" @click="removeCondition">移除</el-link>
    <div style="margin-top: 10px;">
      <el-table
        :data="conditions"
        highlight-current-row
        style="width: 100%"
        size="mini"
        border
        :show-header="false"
        @current-change="selecteCondition"
      >
        <el-table-column prop="describe" show-overflow-tooltip>
          <template slot-scope="scope">
            <div v-html="showTableCell(scope.row)" />
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
      :title="edit?'编辑条件配置':'添加条件配置'"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="450px"
    >
      <el-form ref="conditions_rule_form" :rules="rules" label-width="100px" :model="form" size="small">
        <el-form-item v-if="conditions.length > 0 && (!edit || (edit && editIndex > 0))" label="与上一条关系">
          <el-select v-model="form.join" placeholder="请选择" style="width:100%">
            <el-option v-for="item in joinOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="属性值" prop="left">
          <el-select v-model="form.left" placeholder="请选择" style="width:100%">
            <el-option v-for="item in fields" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作符">
          <el-select v-model="form.operation" placeholder="请选择" style="width:100%">
            <el-option v-for="item in symbolOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="值表达式" prop="right">
          <el-input v-model="form.right" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="dialogVisible=false">取 消</el-button>
        <el-button type="primary" size="mini" @click="saveCondition()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { joinTypes, symbolTypes } from '@/data/select-options'
import { scriptValidation } from '@/api/designer'

export default {
  props: {
    conditions: {
      type: Array,
      required: true
    },
    fields: {
      type: Array,
      required: true
    }
  },
  data: function() {
    return {
      edit: false,
      editIndex: -1,
      dialogVisible: false,
      condition: {},
      joinOptions: joinTypes,
      symbolOptions: symbolTypes,
      form: {
        join: 'and',
        left: '',
        operation: '>',
        right: ''
      },
      rules: {
        left: [{ required: true, message: '请选择属性值' }],
        right: [{
            required: true,
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请输入表达式值'))
              } else {
                scriptValidation({
                  content: value
                }).then(response => {
                  const result = response.data
                  if (!result || result.length === 0) {
                    callback()
                  } else {
                    callback(new Error(result[0].message))
                  }
                })
              }
            },
            trigger: 'change'
          },
        ]
      }
    }
  },
  watch: {
    conditions(val) {
      this.condition = {}
      this.edit = false
      this.editIndex = -1
    }
  },
  mounted: function() {

  },
  methods: {
    selecteCondition(item) {
      this.editIndex = this.conditions.indexOf(item)
      this.condition = item || {}
    },
    addCondition() {
      this.form = {
        join: 'and',
        left: '',
        operation: '>',
        right: ''
      }
      this.edit = false
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['conditions_rule_form'].resetFields()
      })
    },
    editCondition() {
      this.edit = true
      const condition = this.condition
      this.editIndex = this.conditions.indexOf(condition)
      this.form = {
        join: condition.join || 'and',
        left: condition.left || '',
        operation: condition.operation || '>',
        right: condition.right || '',
      }
      this.dialogVisible = true
    },
    removeCondition() {
      const conditions = this.conditions
      conditions.splice(this.editIndex, 1)
    },
    showTableCell(item) {
      let describe = ''
      const conditions = this.conditions
      const index = conditions.indexOf(item)
      if (index > 0) {
        if (item.join === 'and') {
          describe = describe + '且'
        } else {
          describe = describe + '或'
        }
      }
      // 条件左值
      describe = describe + '属性值' + item.left
      // 操作符
      const symbolOption = this.symbolOptions.find(function(ele) {
        return ele.value === item.operation
      })
      describe = describe + symbolOption.label + item.right
      return describe
    },
    saveCondition() {
      this.$refs['conditions_rule_form'].validate((valid) => {
        if (valid) {
          const item = this.form
          const conditions = this.conditions
          if (this.edit) {
            conditions.splice(this.editIndex, 1, item)
          } else {
            this.conditions.push(item)
          }
          this.dialogVisible = false
          this.form = {
            join: 'and',
            left: '',
            operation: '>',
            right: ''
          }
        }
      })
    }
  }
}
</script>
