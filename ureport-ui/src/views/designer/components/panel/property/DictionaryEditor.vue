<template>
  <div>
    <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form">
      <el-form-item label="映射方式:">
        <el-radio-group v-model="form.mappingType" size="mini" @change="handleMappingType">
          <el-radio-button label="simple">简单值</el-radio-button>
          <el-radio-button label="dataset">数据集</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template v-if="form.mappingType==='simple'">
      <el-button plain size="mini" style="margin: 10px 0px;" @click="addSimpleValue()">添加</el-button>
      <el-table size="mini" :data="mappingItems" style="width: 100%">
        <el-table-column prop="value" label="实际值" width="80" />
        <el-table-column prop="label" label="显示值" />
        <el-table-column label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="editSimpleValue(scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="removeSimpleValue(scope.row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>
    <template v-else>
      <el-form label-position="left" label-width="90px" :model="form" size="mini">
        <el-form-item label="字典数据集:">
          <el-select v-model="form.mappingDataset" placeholder="请选择" style="width: 100%;" @change="handleDataset">
            <el-option v-for="item in datasets" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>

        <el-form-item label="实际值属性:">
          <el-select v-model="form.mappingKeyProperty" placeholder="请选择" style="width: 100%;" @change="handleMappingKeyProperty">
            <el-option v-for="item in fields" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示值属性:">
          <el-select v-model="form.mappingValueProperty" placeholder="请选择" style="width: 100%;" @change="handleMappingValueProperty">
            <el-option v-for="item in fields" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
      </el-form>
    </template>
    <el-dialog :title="edit?'编辑':'添加'" append-to-body :visible.sync="dialogVisible" width="400px">
      <el-form ref="dictionary_item_form" label-width="80px" :model="mappingItem" size="small" :rules="rules">
        <el-form-item label="实际值" prop="value">
          <el-input v-model="mappingItem.value" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="显示值" prop="label">
          <el-input v-model="mappingItem.label" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" size="mini" @click="saveSimpleValue()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

export default {
  props: {
    dataMapping: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      dialogVisible: false,
      edit: false,
      editIndex: -1,
      datasetValue: {},
      datasets: [],
      fields: [],
      mappingItems: [],
      mappingItem: {},
      form: {
        mappingType: 'simple',
        mappingDataset: '',
        mappingKeyProperty: '',
        mappingValueProperty: ''
      },
      rules: {
        value: [{ required: true, message: '请输入实际值' }],
        label: [{ required: true, message: '请输入显示值' }]
      }
    }
  },
  watch: {
    dataMapping: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.dataMapping)
  },
  methods: {
    init(val) {
      this.edit = false
      this.editIndex = -1
      if (val.cellDef) {
        const datasetValue = val.cellDef.value
        this.datasetValue = datasetValue
        if (datasetValue.mappingType) {
          this.form.mappingType = datasetValue.mappingType
        } else {
          this.form.mappingType = 'simple'
        }
        if (!datasetValue.mappingItems) {
          datasetValue.mappingItems = []
        }
        if (datasetValue.mappingDataset) {
          this.form.mappingDataset = datasetValue.mappingDataset
        } else {
          this.form.mappingDataset = ''
        }
        if (datasetValue.mappingKeyProperty) {
          this.form.mappingKeyProperty = datasetValue.mappingKeyProperty
        } else {
          this.form.mappingKeyProperty = ''
        }
        if (datasetValue.mappingValueProperty) {
          this.form.mappingValueProperty = datasetValue.mappingValueProperty
        } else {
          this.form.mappingValueProperty = ''
        }
        this.datasets = val.datasets
        this.mappingItems = datasetValue.mappingItems
        this.mappingItem = {}
      }
    },
    handleMappingType(val) {
      this.datasetValue.mappingType = val
    },
    handleDataset(val) {
      for (const ds of this.datasets) {
        if (ds.name === val) {
          this.fields = ds.fields || []
        }
      }
      this.datasetValue.mappingDataset = val
    },
    handleMappingKeyProperty(val) {
      this.datasetValue.mappingKeyProperty = val
    },
    handleMappingValueProperty(val) {
      this.datasetValue.mappingValueProperty = val
    },
    addSimpleValue() {
      this.edit = false
      this.mappingItem = {
        label: '',
        value: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dictionary_item_form'].resetFields()
      })
    },
    editSimpleValue(item) {
      this.edit = true
      this.editIndex = this.mappingItems.indexOf(item)
      this.mappingItem = JSON.parse(JSON.stringify(item))
      this.dialogVisible = true
    },
    removeSimpleValue(param) {
      this.mappingItems.splice(this.editIndex, 1)
    },
    saveSimpleValue() {
      this.$refs['dictionary_item_form'].validate((valid) => {
        if (valid) {
          if (this.edit) {
            this.mappingItems.splice(this.editIndex, 1, this.mappingItem)
          } else {
            this.mappingItems.push(this.mappingItem)
          }
          this.dialogVisible = false
        }
      })
    }
  }
}
</script>
