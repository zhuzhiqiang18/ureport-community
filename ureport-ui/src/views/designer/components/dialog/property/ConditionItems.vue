<template>
  <fieldset style="width: 174px;">
    <legend>条件项配置</legend>
    <el-link style="margin-right: 15px;" @click="addConditionItem">新增</el-link>
    <el-link :disabled="!propertyCondition.name" style="margin-right: 15px;" @click="editConditionItem">编辑</el-link>
    <el-link :disabled="!propertyCondition.name" @click="removeConditionItem">移除</el-link>
    <div style="margin-top: 10px;">
      <el-table
        :data="propertyConditions"
        highlight-current-row
        style="width: 100%"
        size="mini"
        border
        :show-header="false"
        @current-change="selecteConditionItem"
      >
        <el-table-column prop="name" show-overflow-tooltip />
      </el-table>
    </div>
    <el-dialog
      :title="edit?'编辑条件项':'添加条件项'"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="400px"
    >
      <el-form ref="property_item_rule_form" label-width="84px" :model="form" :rules="rules">
        <el-form-item label="条件名称:" prop="name">
          <el-input v-model="form.name" placeholder="请输入内容" size="small" style="width:100%" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible=false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveConditionItem()">确 定</el-button>
      </span>
    </el-dialog>
  </fieldset>
</template>
<script>
export default {
  props: {
    propertyConditions: {
      type: Array,
      required: true
    },
    setConditionItem: {
      type: Function,
      required: true
    }
  },
  data: function() {
    return {
      edit: false,
      editIndex: -1,
      dialogVisible: false,
      propertyCondition: {},
      form: {
        name: ''
      },
      rules: {
        name: [
          {
            required:true,
            validator: (rule, value, callback) => {
              if (!value) {
                callback(new Error('请输入条件项名称'))
              }
              const obj = this.propertyConditions.find(function(ele) {
                return ele.name === value
              })
              if (obj) {
                const index = this.propertyConditions.indexOf(obj)
                if(this.edit && index === this.editIndex) {
                  callback()
                } else {
                  callback(new Error('条件项名称已存在'))
                }
              } else {
                callback()
              }
            }
          }
        ]
      }
    }
  },
  mounted: function() {

  },
  methods: {
    selecteConditionItem(item) {
      if(item) {
        this.propertyCondition = item
        this.editIndex = this.propertyConditions.indexOf(item)
        this.setConditionItem(item)
      } else {
        this.propertyCondition = {}
        this.editIndex = -1
        this.setConditionItem({})
      }
    },
    addConditionItem() {
      this.edit = false
      this.form.name = ''
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['property_item_rule_form'].resetFields()
      })
    },
    editConditionItem() {
      this.edit = true
      this.form.name = this.propertyCondition.name
      this.dialogVisible = true
    },
    saveConditionItem() {
      this.$refs['property_item_rule_form'].validate((valid) => {
        if (valid) {
          const newName = this.form.name
          if (this.edit) {
            this.propertyCondition.name = newName
          } else {
            this.propertyConditions.push({
              name: newName,
              cellStyle: {},
              conditions: []
            })
          }
          this.dialogVisible = false
        }
      })
    },
    removeConditionItem() {
      this.propertyConditions.splice(this.editIndex, 1)
    }
  }
}
</script>

<style>
</style>
