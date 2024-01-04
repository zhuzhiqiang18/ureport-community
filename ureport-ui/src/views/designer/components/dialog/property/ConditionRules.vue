<template>
  <fieldset style="width: 294px;">
    <legend>条件规则配置</legend>
    <el-link style="margin-right: 15px;" :disabled="!propertyCondition.name" @click="addConditionRule">新增</el-link>
    <el-link style="margin-right: 15px;" :disabled="this.editIndex===-1" @click="editConditionRule">编辑</el-link>
    <el-link :disabled="this.editIndex===-1" @click="removeConditionRule">移除</el-link>
    <div style="margin-top: 10px;">
      <el-table
        :data="propertyCondition.conditions"
        highlight-current-row
        style="width: 100%"
        size="mini"
        border
        :show-header="false"
        @current-change="selecteConditionRule"
      >
        <el-table-column prop="describe" show-overflow-tooltip>
          <template slot-scope="scope">
            <div v-html="showTableCell(scope.row)" />
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog
      :title="edit?'编辑条件规则':'添加条件规则'"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="650px"
    >
      <el-form ref="property_condition_rule_form" label-width="100px" :model="form" size="small" :rules="rules">
        <el-form-item v-if="propertyCondition.conditions.length > 0 && (!edit || (edit && editIndex > 0))" label="与上一条关系">
          <el-select v-model="form.join" placeholder="请选择" style="width:100%">
            <el-option v-for="item in joinOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件左值">
          <el-select v-model="form.type" placeholder="请选择" style="width:100%">
            <el-option v-for="item in leftOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type=='property'" label="左值属性值" prop="property">
          <el-select v-model="form.property" placeholder="请选择" style="width:100%">
            <el-option v-for="item in fields" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type=='expression'" label="左值表达式" prop="expression">
          <el-input v-model="form.expression" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="操作符">
          <el-select v-model="form.symbol" placeholder="请选择" style="width:100%">
            <el-option v-for="item in symbolOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="值表达式" prop="right">
          <el-input v-model="form.right" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible=false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveConditionRule()">确 定</el-button>
      </span>
    </el-dialog>
  </fieldset>
</template>

<script>
import { joinTypes, symbolTypes } from '@/data/select-options'
import { scriptValidation } from '@/api/designer'

export default {
  props: {
    propertyCondition: {
      type: Object,
      default: () => {
        return {
          conditions: []
        }
      }
    },
    fields: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data: function() {
    return {
      rules: {
        property: [
          {
            required: true,
            validator: (rule, value, callback) => {
              if (this.form.type === 'property' && !value) {
                callback(new Error('请选择属性值'))
              } else {
                callback()
              }
            },
            trigger: 'change'
          }
        ],
        expression: [
          {
            required: true,
            validator: (rule, value, callback) => {
              if (this.form.type === 'expression' && !value) {
                callback(new Error('请输入左值表达式'))
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
          }
        ],
        right: [
          {
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
          }
        ]
      },
      edit: false,
      editIndex: -1,
      condition: {},
      dialogVisible: false,
      joinOptions: joinTypes,
      leftOptions: [
        { label: '当前值', value: 'current' },
        { label: '属性', value: 'property' },
        { label: '表达式', value: 'expression' }
      ],
      symbolOptions: symbolTypes,
      form: {
        type: 'current',
        property: '',
        expression: '',
        symbol: '>',
        right: ''
      }
    }
  },
  watch: {
    propertyCondition(val) {
      this.condition = {}
      this.edit = false
      this.editIndex = -1
    }
  },
  mounted: function() {
    this.condition = {}
  },
  methods: {
    selecteConditionRule(item) {
      this.editIndex = this.propertyCondition.conditions.indexOf(item)
      this.condition = item || {}
    },
    addConditionRule() {
      this.form = {
        join: 'and',
        type: 'current',
        property: '',
        expression: '',
        symbol: '>',
        right: ''
      }
      this.edit = false
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['property_condition_rule_form'].resetFields()
      })
    },
    editConditionRule() {
      this.editIndex = this.propertyCondition.conditions.indexOf(this.condition)
      const row = JSON.parse(JSON.stringify(this.condition))
      this.form = {
        join: row.join,
        type: row.type || 'current',
        property: '',
        expression: '',
        symbol: row.operation,
        right: row.right
      }
      if (row.type === 'property') {
        this.form.property = row.left
      } else if (row.type === 'expression') {
        this.form.expression = row.left
      }
      this.edit = true
      this.dialogVisible = true
    },
    showTableCell(item) {
      const conditions = this.propertyCondition.conditions || []
      let describe = ''
      const index = conditions.indexOf(item)
      if (index > 0) {
        if (item.join === 'and') {
          describe = describe + '且'
        } else {
          describe = describe + '或'
        }
      }
      // 条件左值
      if (item.type === 'current') {
        describe = describe + '当前值'
      } else if (item.type === 'property') {
        describe = describe + '属性值' + item.left
      } else {
        describe = describe + '表达式' + item.left
      }
      // 操作符
      const symbolOption = this.symbolOptions.find(function(ele) {
        return ele.value === item.operation
      })
      describe = describe + symbolOption.label + item.right
      return describe
    },
    saveConditionRule() {
      this.$refs['property_condition_rule_form'].validate((valid) => {
        if (valid) {
          const item = {
            type: this.form.type,
            operation: this.form.symbol,
            join: this.form.join,
            right: this.form.right
          }
          if (this.form.type === 'property') {
            item.left = this.form.property
          } else if (this.form.type === 'expression') {
            item.left = this.form.expression
          }
          const conditions = this.propertyCondition.conditions || []

          if (this.edit) {
            conditions.splice(this.editIndex, 1, item)
          } else {
            conditions.push(item)
          }
          this.dialogVisible = false
          this.form = {
            join: 'and',
            type: 'current',
            property: '',
            expression: '',
            symbol: '>',
            right: ''
          }
        }
      })
    },
    removeConditionRule() {
      const conditions = this.propertyCondition.conditions || []
      conditions.splice(this.editIndex, 1)
      this.editIndex = -1
    }
  }
}
</script>

<style>
</style>
