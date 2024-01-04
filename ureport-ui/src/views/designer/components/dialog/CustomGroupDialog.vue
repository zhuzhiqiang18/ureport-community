<template>
  <el-dialog
    title="自定义分组配置配置"
    :visible.sync="customGroupValue.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    class="condition-dialog"
    width="790px"
  >
    <el-container>
      <el-aside width="204px">
        <fieldset style="width: 200px;height:400px">
          <legend>自定义分组项</legend>
          <el-link style="margin-right: 15px;" @click="addCustomGroupItem">新增</el-link>
          <el-link :disabled="!groupItem.name" style="margin-right: 15px;" @click="editCustomGroupItem">编辑</el-link>
          <el-link :disabled="!groupItem.name" @click="removeCustomGroupItem">移除</el-link>
          <div style="margin-top: 10px;">
            <el-table
              :data="groupItems"
              highlight-current-row
              style="width: 100%"
              size="mini"
              border
              :show-header="false"
              @current-change="selecteCustomGroupItem"
            >
              <el-table-column prop="name" show-overflow-tooltip />
            </el-table>
          </div>
        </fieldset>
      </el-aside>
      <el-main>
        <fieldset style="width: 552px;height:400px">
          <legend>分组条件</legend>
          <el-link style="margin-right: 15px;" :disabled="!groupItem.name" @click="addCondition">新增</el-link>
          <el-link style="margin-right: 15px;" :disabled="conditionIndex===-1" @click="editCondition">编辑</el-link>
          <el-link :disabled="conditionIndex===-1" @click="removeCondition">移除</el-link>
          <div style="margin-top: 10px;">
            <el-table
              :data="groupItem.conditions"
              highlight-current-row
              style="width: 100%"
              size="mini"
              border
              :show-header="false"
              @current-change="selecteCondition"
            >
              <el-table-column prop="describe" show-overflow-tooltip>
                <template slot-scope="scope">
                  <div v-html="showCondition(scope.row)" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </fieldset>
      </el-main>
      <el-dialog
        :title="groupItemEdit?'添加自定义分组项':'编辑自定义分组项'"
        :visible.sync="groupItemDialogVisible"
        append-to-body
        :close-on-click-modal="false"
        width="400px"
      >
        <el-form ref="custom_group_item_form" label-width="84px" :model="form" :rules="rules">
          <el-form-item label="分组名称:" prop="name">
            <el-input v-model="form.name" placeholder="请输入内容" size="small" style="width:100%" />
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button size="small" @click="groupItemDialogVisible=false">取 消</el-button>
          <el-button type="primary" size="small" @click="saveCustomGroupItem">确 定</el-button>
        </span>
      </el-dialog>
      <el-dialog
        :title="conditionEdit?'编辑条件配置':'添加条件配置'"
        :visible.sync="conditionDialogVisible"
        append-to-body
        :close-on-click-modal="false"
        width="450px"
      >
        <el-form ref="custom_group_item_rule_form" label-width="100px" :model="form" size="small" :rules="rules">
          <el-form-item v-if="isJoin" label="与上一条关系">
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
          <el-button size="small" @click="conditionDialogVisible=false">取 消</el-button>
          <el-button type="primary" size="small" @click="saveCondition()">确 定</el-button>
        </span>
      </el-dialog>
    </el-container>
  </el-dialog>
</template>

<script>
import { joinTypes, symbolTypes } from '@/data/select-options'
import { scriptValidation } from '@/api/designer'
export default {
  props: {
    customGroupValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      rules: {
        'name': [
          {
            required: true,
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请输入条件项名称'))
              }
              const obj = this.groupItems.find(function(ele) {
                return ele.name === value
              })
              if (obj) {
                const index = this.groupItems.indexOf(obj)
                if(this.groupItemEdit && index === this.groupItemIndex) {
                  callback()
                } else {
                  callback(new Error('条件项名称已存在'))
                }
              } else {
                callback()
              }
            }
          }
        ],
        left: [
          {
            required: true,
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请选择属性值'))
              } else {
                callback()
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
      fields: [],
      joinOptions: joinTypes,
      symbolOptions: symbolTypes,
      groupItems: [],
      groupItemEdit: false,
      groupItemIndex: -1,
      groupItemDialogVisible: false,
      groupItem: {
        conditions: []
      },
      isJoin: false,
      conditionEdit: false,
      conditionIndex: -1,
      conditionDialogVisible: false,
      condition: {},
      form: {
        name: '',
        join: 'and',
        left: '',
        operation: '>',
        right: ''
      }
    }
  },
  watch: {
    customGroupValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.customGroupValue)
  },
  methods: {
    init(val) {
      this.groupItemEdit = false
      this.groupItemIndex = -1
      this.conditionEdit = false
      this.conditionIndex = -1
      this.fields = val.fields || []
      if (val.cellDef) {
        this.groupItems = val.cellDef.value.groupItems
      }
    },
    addCustomGroupItem() {
      this.groupItemEdit = false
      this.form.name = ''
      this.groupItemDialogVisible = true
      this.$nextTick(() => {
        this.$refs['custom_group_item_form'].resetFields()
      })
    },
    selecteCustomGroupItem(item) {
      this.groupItemIndex = this.groupItems.indexOf(item)
      this.groupItem = item || { conditions: [] }
    },
    editCustomGroupItem() {
      this.groupItemEdit = true
      this.form.name = this.groupItem.name
      this.groupItemDialogVisible = true
    },
    removeCustomGroupItem() {
      this.groupItems.splice(this.groupItemIndex, 1)
    },
    saveCustomGroupItem() {
      this.$refs['custom_group_item_form'].validate((valid) => {
        if (valid) {
          const newName = this.form.name
          if (this.groupItemEdit) {
            this.groupItem.name = newName
          } else {
            this.groupItems.push({
              name: newName,
              conditions: []
            })
          }
          this.groupItemDialogVisible = false
        }
      })
    },
    addCondition() {
      if(this.groupItem.conditions.length > 0) {
        this.isJoin = true
      } else {
        this.isJoin = false
      }
      this.conditionEdit = false
      this.form.join = 'and'
      this.form.left = ''
      this.form.operation = '>'
      this.form.right = ''
      this.conditionDialogVisible = true
      this.$nextTick(() => {
        this.$refs['custom_group_item_rule_form'].resetFields()
      })
    },
    selecteCondition(item) {
      const conditions = this.groupItem.conditions || []
      this.conditionIndex = conditions.indexOf(item)
      this.condition = item || {}
    },
    editCondition() {
      if(this.groupItem.conditions.length > 0 && this.conditionIndex !== 0) {
        this.isJoin = true
      } else {
        this.isJoin = false
      }
      this.conditionEdit = true
      this.form.join = this.condition.join
      this.form.left = this.condition.left
      this.form.operation = this.condition.operation
      this.form.right = this.condition.right
      this.conditionDialogVisible = true
    },
    removeCondition() {
      const conditions = this.groupItem.conditions
      conditions.splice(this.conditionIndex, 1)
    },
    showCondition(item) {
      let describe = ''
      const conditions = this.groupItem.conditions
      const conditionIndex = conditions.indexOf(item)
      if (conditionIndex > 0) {
        if (item.join === 'and') {
          describe = describe + '且'
        } else {
          describe = describe + '或'
        }
      }
      // 条件左值
      describe = describe + '属性值' + item.left
      // 操作符
      const obj = this.symbolOptions.find(function(ele) {
        return ele.value === item.operation
      })
      describe = describe + obj.label + item.right
      return describe
    },
    saveCondition() {
      this.$refs['custom_group_item_rule_form'].validate((valid) => {
        if (valid) {
          const item = {
            left: this.form.left,
            operation: this.form.operation,
            right: this.form.right,
            join: this.form.join
          }
          const conditions = this.groupItem.conditions
          if (this.conditionEdit) {
            conditions.splice(this.conditionIndex, 1, item)
          } else {
            conditions.push(item)
          }
          this.conditionDialogVisible = false
        }
      })
    }
  }
}
</script>
