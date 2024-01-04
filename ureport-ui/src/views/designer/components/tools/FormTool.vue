<template>
  <div class="component-icon-btn" @click="openForm">
    <div class="component-icon-btn-icon"><i class="el-icon-document" /></div>
    <el-dialog title="查询条件" :visible.sync="dialogVisible" append-to-body :close-on-click-modal="false" width="1080px">
      <div style="display: flex;height: 600px;">
        <div style="width: 300px;border: 1px solid #DCDFE6;border-radius: 2px;">
          <el-tabs v-model="activeName" stretch>
              <el-tab-pane label="组件" name="first">
                <el-row style="margin: 0px;padding: 0px 10px;" :gutter="24">
                  <el-col :span="12" v-for="(item, index) in  InputComponent">
                    <el-button class="filter-button-group" type="primary" plain size="small" icon="el-icon-date" @click="handleParameter(item.type)">{{item.label}}</el-button>
                  </el-col>
                </el-row>
              </el-tab-pane>
              <el-tab-pane label="属性" name="second">
                <el-alert v-if="!row.uuid" title="请选中右侧组件配置属性" type="info" :closable="false" />
                <el-form v-if="row.uuid" :model="row" label-width="80px" label-position="left" size="small" style="padding: 0px 10px;">
                  <el-form-item label="标题:">
                    <el-input v-model="row.label" :maxlength="10" style="width: 100%%;" />
                  </el-form-item>
                  <el-form-item label="绑定参数:">
                    <el-input v-model="row.bindParameter" style="width: 100%%;" />
                  </el-form-item>
                  <el-form-item label="日期格式:" v-if="row.type==='datetime'">
                    <el-select v-model="row.format" placeholder="请选择" style="width: 98%;" @change="handleSelectDataSet">
                      <el-option
                        v-for="item in dateTypes"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item label="缺省值:" v-if="row.type==='datetime' && row.format!=='datetime'">
                    <el-radio-group v-model="row.defaultValue">
                      <template v-if="row.format==='year'">
                        <el-radio label="0">yyyy-01-01 00:00:00</el-radio>
                        <el-radio label="1" style="margin-top: 10px;">yyyy-12-31 23:59:59</el-radio>
                      </template>
                      <template v-else-if="row.format==='month'">
                        <el-radio label="0">yyyy-MM-01 00:00:00</el-radio>
                        <el-radio label="1" style="margin-top: 10px;">yyyy-MM-31 23:59:59</el-radio>
                      </template>
                      <template v-else-if="row.format==='date'">
                        <el-radio label="0">yyyy-MM-dd 00:00:00</el-radio>
                        <el-radio label="1" style="margin-top: 10px;">yyyy-MM-dd 23:59:59</el-radio>
                      </template>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="数据来源:" v-if="row.type==='select' || row.type==='multiple-select'">
                    <el-select v-model="row.constant" placeholder="请选择" style="width: 98%;">
                      <el-option
                        v-for="item in dataTypeOptions"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                      />
                    </el-select>
                  </el-form-item>
                  <template v-if="row.constant==='1'">
                    <el-form-item label="数据集:">
                      <el-select v-model="row.dataset" placeholder="请选择" style="width: 98%;" @change="handleSelectDataSet">
                        <el-option
                          v-for="item in datasets"
                          :key="item.name"
                          :label="item.name"
                          :value="item.name"
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="显示值:">
                      <el-select v-model="row.labelField" placeholder="请选择" style="width: 98%;">
                        <el-option
                          v-for="item in dataset.fields"
                          :key="item.name"
                          :label="item.name"
                          :value="item.name"
                        />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="实际值:">
                      <el-select v-model="row.valueField" placeholder="请选择" style="width: 98%;">
                        <el-option
                          v-for="item in dataset.fields"
                          :key="item.name"
                          :label="item.name"
                          :value="item.name"
                        />
                      </el-select>
                    </el-form-item>
                  </template>
                  <template v-if="row.constant==='0'">
                    <el-button plain size="mini" @click="addNormal()">添加</el-button>
                    <el-table
                      :data="row.options"
                      size="mini"
                      height="200"
                      border
                      style="width: 100%;margin-top: 10px;"
                    >
                      <el-table-column
                        prop="value"
                        show-overflow-tooltip
                        label="实际值"
                      />
                      <el-table-column
                        prop="label"
                        show-overflow-tooltip
                        label="显示值"
                      />
                      <el-table-column
                        label="操作"
                        align="center"
                        width="76"
                      >
                        <template slot-scope="scope">
                          <el-link @click="editNormal(scope.$index,scope.row)" :underline="false" type="primary" style="font-size: 12px;margin-right: 5px;">编辑</el-link>
                          <el-link @click="removeNormal(scope.$index,scope.row)"  :underline="false" type="primary" style="font-size: 12px;">移除</el-link>
                        </template>
                      </el-table-column>
                    </el-table>
                  </template>
                </el-form>
              </el-tab-pane>
          </el-tabs>
        </div>
        <div style="flex:1;border: 1px solid #DCDFE6;border-radius: 2px;margin-left: 10px;padding: 10px;overflow: auto;">
          <div v-for="(item,index) in form.items" :label="item.label" @click="handleClick(item)" :class="row.uuid===item.uuid?'div-form-item form-item-active':'div-form-item'">
            <div style="flex: 1;margin: auto;">
            <label style="margin: 0px 2px;">{{item.label}}</label>
              <el-date-picker
                v-if="item.type === 'datetime'"
                v-model="item.value"
                style="width: 190px;"
                :type="item.format"
                placeholder="请选择"
                size="small"
                :clearable="false"
                :editable="false"
              />
              <el-input
                v-if="item.type === 'text'"
                v-model="item.value"
                style="width: 190px;"
                size="small"
                placeholder="请输入内容"
              />
              <el-select
                v-if="item.type === 'select'"
                v-model="item.value"
                placeholder="请选择"
                size="small"
                style="width: 190px;"
              >
                <el-option
                  v-for="option in item.options"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-select
                v-if="item.type === 'multiple-select'"
                v-model="item.value"
                placeholder="请选择"
                size="small"
                multiple
                style="width: 190px;"
              >
                <el-option
                  v-for="option in item.options"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
              <el-radio-group
                v-if="item.type === 'radio'"
                v-model="item.value"
              >
                <el-radio v-for="(option,index) in item.options" :label="option.value">{{option.label}}</el-radio>
              </el-radio-group>
              <el-checkbox-group
                v-if="item.type === 'checkbox'"
                v-model="item.value"
                style="display: inline-block;"
              >
                <el-checkbox v-for="(option,index) in item.options" :label="option.value">{{option.label}}</el-checkbox>
              </el-checkbox-group>
          </div>
          <div><i class="el-icon-delete form-item-delete-btn" @click="remove($event,index)"></i></div>
          </div>
        </div>
      </div>
    </el-dialog>
    <el-dialog
      title="参数配置"
      append-to-body
      :close-on-click-modal="false"
      :visible.sync="normalDialogVisible"
      width="360px"
    >
      <el-form :model="normal" label-width="60px" size="small">
        <el-form-item label="显示值">
          <el-input v-model="normal.label" :maxlength="32" />
        </el-form-item>
        <el-form-item label="实际值">
          <el-input v-model="normal.value" :maxlength="32" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="normalDialogVisible=false">取 消</el-button>
        <el-button type="primary" size="small" @click="saveNormal()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import uuid from 'node-uuid'

export default {
  components: {

  },
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      activeName: 'first',
      row: {},
      datasets: [],
      dataset: {
        fields: []
      },
      form: {
        items: []
      },
      InputComponent: [
        { label: '日期选择框', type: 'datetime' },
        { label: '文本框', type: 'text'},
        { label: '单选列表框', type: 'select' },
        { label: '单选框', type: 'radio' },
        { label: '多选列表框', type: 'multiple-select'},
        { label: '复选框', type: 'checkbox'},
      ],
      dataTypeOptions: [
        { label: '常量', value: '0' },
        { label: '数据集', value: '1' }
      ],
      dateTypes: [
        { label: 'yyyy', value: 'year' },
        { label: 'yyyy-MM', value: 'month' },
        { label: 'yyyy-MM-dd', value: 'date' },
        { label: 'yyyy-MM-dd HH:mm:ss', value: 'datetime' },
      ],
      dialogVisible: false,
      normalDialogVisible: false,
      normaleEdit: false,
      normaleOldVal: '',
      normaleEditIdenx: -1,
      normal: {
        value: '',
        label: ''
      },
    }
  },
  mounted: function() {

  },
  methods: {
    handleClick(item) {
      this.row = item
      if(item.dataset) {
        this.handleSelectDataSet(item.dataset)
      }
    },
    remove(ev, index) {
      const items = this.form.items.splice(index, 1)
      this.row = {}
      ev.stopPropagation()
    },
    handleParameter(type) {
      const item = {
        uuid: uuid.v1(),
        label: '未命名：',
        bindParameter: '',
        type: type,
        value: null
      }
      if(type === 'datetime') {
        item.format = 'month'
        item.defaultValue = '0'
      } else if(type === 'select') {
        item.constant = "0"
        item.options = []
      } else if(type === 'multiple-select') {
        item.constant = "0"
        item.options = []
        item.value = []
      } else if(type === 'radio') {
        item.constant = "0"
        item.value = '1'
        item.options = [
          {label: '上海', value: '1'},
          {label: '北京', value: '2'},
          {label: '广州', value: '3'}
        ]
      } else if(type === 'checkbox') {
        item.constant = "0"
        item.options = [
          {label: '上海', value: '1'},
          {label: '北京', value: '2'},
          {label: '广州', value: '3'}
        ]
        item.value = []
      }
      const items = this.form.items
      items.push(item)
    },
    openForm() {
      this.row = {}
      if (this.context.reportDef.searchForm && this.context.reportDef.searchForm.components) {
        this.form.items = this.context.reportDef.searchForm.components
      } else {
        this.context.reportDef.searchForm = { components: [] }
        this.form.items = this.context.reportDef.searchForm.components
      }
      const datasources = this.context.reportDef.datasources || []
      const datasets = []
      for (const ds of datasources) {
        const d = ds.datasets
        for (const t of d) {
          datasets.push({
            name: t.name,
            fields: t.fields
          })
        }
      }
      this.datasets = datasets
      this.dataset = {
        fields: []
      }
      this.dialogVisible = true
    },
    handleSelectDataSet: function(val) {
      this.dataset = this.datasets.find(item => item.name === val)
    },
    addNormal: function() {
      this.normaleEdit = false
      this.normaleEditIdenx = -1
      this.normal = {
        value: '',
        label: ''
      }
      this.normalDialogVisible = true
    },
    editNormal(index, item) {
      this.normaleEdit = true
      this.normaleEditIdenx = index
      this.normaleOldVal = item.value
      this.normal = JSON.parse(JSON.stringify(item))
      this.normalDialogVisible = true
    },
    removeNormal(index, item) {
      this.$confirm('确认删除数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.row.options.splice(index, 1)
        this.normaleEditIdenx = -1
        this.$message({type: 'success',message: '删除成功!'})
      })
    },
    saveNormal() {
      const options = this.row.options
      const val = this.normal.value
      const obj = options.find(item => item.value === val)
      if(this.normaleEdit) { // 编辑
        if(obj && obj.value !== this.normaleOldVal) {
          this.$message({type: 'info',message: '实际值已存在!'})
          return
        }
        options.splice(this.normaleEditIdenx, 1, this.normal)
      } else { // 新增
        if(obj) {
          this.$message({type: 'info',message: '实际值已存在!'})
          return
        }
        options.push(this.normal)
      }
      this.normalDialogVisible = false
    }
  }
}
</script>

<style>
  .filter-button-group {
    width: 110px;
    margin-top: 10px;
  }

  .filter-component-group {
    margin: 5px;
    padding: 5px;
    border-radius: 3px;
    border: 1px dotted #DCDFE6;
    box-sizing: border-box;
  }

  .filter-component-group:hover{
    border-color: #409EFF;
    cursor: pointer;
  }

  .filter-component-group-select {
    border: 1px solid #409EFF;
  }

  .form-item-remove:hover {
    color: #409EFF
  }

  .form-item-delete-btn {
    line-height: 32px;
    cursor: pointer;
  }

  .form-item-delete-btn:hover {
    color: #409EFF;
  }

  .form-list-item .el-scrollbar__wrap {
    overflow-x: hidden;
  }

  .form-item-active {
    color: #409EFF;
  }

  .div-form-item {
    margin-bottom: 10px;
    padding: 10px;
    border-radius: 2px;
    border: 1px solid #d9ecff;
    display: flex;
  }

  .div-form-item:hover {
    border-color: #67C23A;
  }

  .form-item-active{
    border-color: #409EFF;
  }
</style>
