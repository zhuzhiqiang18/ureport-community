<template>
  <el-tabs value="first" stretch>
    <el-tab-pane label="数据集配置" name="first">
      <el-form label-position="left" :model="form" class="div-block-clearance" size="mini" label-width="80px">
        <el-form-item label="数据集:">
          <el-select v-model="form.datasetName" placeholder="请选择" style="width:100%" @change="handleDatasetChange">
            <el-option v-for="item in datasets" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="属性值:">
          <el-select v-model="form.property" placeholder="请选择" style="width:100%" @change="handlePropertyChange">
            <el-option v-for="item in properties" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="聚合方式:">
          <el-select v-model="form.aggregate" placeholder="请选择" style="width:100%" @change="handleAggregateChange">
            <el-option v-for="item in groups" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.aggregate==='customgroup'" label="分组配置:">
          <el-badge :value="groupItems.length">
            <el-button plain @click="handleCustomgroup">自定义分组</el-button>
          </el-badge>
        </el-form-item>
        <template v-if="form.aggregate==='select'||form.aggregate==='group'||form.aggregate==='customgroup'">
          <el-form-item label="排序方式:">
            <el-radio-group v-model="form.order" @change="handleOrder">
              <el-radio-button v-for="(item,index) in orders" :key="'key_'+index" :label="item.value">{{ item.label }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="扩展方向:">
            <el-radio-group v-model="form.expand" @change="handleExpand">
              <el-radio-button v-for="(item,index) in expands" :key="'key_'+index" :label="item.value">{{ item.label }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </template>
        <!-- <el-form-item label="换行计算:">
          <el-switch v-model="form.wrapCompute" @change="handleWrapCompute" />
        </el-form-item> -->
        <el-form-item label="补充空白:">
          <div style="display: flex;">
            <div>
              <el-checkbox v-model="form.fillBlankRows" @change="handleFillBlankRows" style="margin-right: 5px;"></el-checkbox>
              <label style="font-weight: 400;color: #606266;">数据行倍数：</label>
            </div>
            <div style="flex: 1;">
              <el-input v-model="form.multiple" :disabled="!form.fillBlankRows" style="width:100%" oninput="value=value.replace(/[^\d]/g,'')" @change="handleMultiple" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="格式化:">
          <g-autocomplete
            v-model="form.format"
            style="width:100%"
            :data="valueFormats"
            @change="handleFormat"
          />
        </el-form-item>
        <el-form-item label="条件属性:">
          <el-badge :value="conditionPropertyItems.length">
            <el-button plain @click="handlePropertyCondition">配置条件</el-button>
          </el-badge>
        </el-form-item>
        <property-condition-dialog :property-condition-value="propertyConditionValue" />
      </el-form>
      <custom-group-dialog :custom-group-value="customGroupValue" />
    </el-tab-pane>
    <el-tab-pane name="second">
      <span slot="label">
        <el-badge class="tab-pane-badge" :value="conditions.length">
        过滤条件
        </el-badge>
      </span>
      <condition-dialog :conditions="conditions" :fields="properties" />
    </el-tab-pane>
    <el-tab-pane label="数据映射" name="third">
      <dictionary-editor :data-mapping="dataMapping" />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import CustomGroupDialog from '../../dialog/CustomGroupDialog'
import PropertyConditionDialog from '../../dialog/PropertyConditionDialog'
import ConditionDialog from '../../dialog/ConditionDialog'
import DictionaryEditor from './DictionaryEditor'
import { groups, orders, expands, valueFormats } from '@/data/select-options'

export default {
  components: {
    CustomGroupDialog,
    PropertyConditionDialog,
    ConditionDialog,
    DictionaryEditor
  },
  props: {
    datasetValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      conditionPropertyItems: [],
      groupItems: [],
      groups: groups,
      orders: orders,
      expands: expands,
      valueFormats: valueFormats,
      datasources: [],
      datasets: [],
      properties: [],
      propertyConditionValue: {
        dialogVisible: false
      },
      customGroupValue: {
        dialogVisible: false
      },
      conditions: [],
      dataMapping: {

      },
      condition: {},
      cellDef: {},
      rowIndex: 0,
      colIndex: 0,
      row2Index: 0,
      col2Index: 0,
      form: {
        datasetName: '',
        property: '',
        aggregate: '',
        order: 'none',
        wrapCompute: '0',
        expand: 'Down',
        format: '',
        rowHeight: 0,
        fillBlankRows: false,
        multiple: 0,
        value: ''
      }
    }
  },
  watch: {
    datasetValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.datasetValue)
  },
  methods: {
    init(val) {
      if (val.context) {
        this.context = val.context
        this.refresh(val.rowIndex, val.colIndex, val.row2Index, val.col2Index)
      }
    },
    refresh(rowIndex, colIndex, row2Index, col2Index) {
      const cellDef = this.context.getCell(rowIndex, colIndex)
      if (!cellDef) {
        return
      }
      this.rowIndex = rowIndex
      this.colIndex = colIndex
      this.row2Index = row2Index
      this.col2Index = col2Index
      this.cellDef = cellDef
      this.datasources = this.context.reportDef.datasources
      // 数据集
      if (cellDef.value.datasetName) {
        this.form.datasetName = cellDef.value.datasetName
      } else {
        this.form.datasetName = ''
      }
      let datasets = []
      for (const ds of this.datasources) {
        datasets = datasets.concat(ds.datasets || [])
      }
      this.datasets = datasets
      this.dataMapping = {
        cellDef: cellDef,
        datasets: this.datasets
      }
      // 选中数据集字段
      this.properties = []
      for (var i = 0; i < datasets.length; i++) {
        const item = datasets[i]
        if (cellDef.value.datasetName === item.name) {
          this.properties = item.fields
        }
      }
      // 属性
      if (cellDef.value.property) {
        this.form.property = cellDef.value.property
      } else {
        this.form.property = ''
      }
      // 统计
      if (cellDef.value.aggregate) {
        this.form.aggregate = cellDef.value.aggregate
      } else {
        this.form.aggregate = ''
      }
      //
      if (cellDef.fillBlankRows) {
        this.form.fillBlankRows = true
      } else {
        this.form.fillBlankRows = false
      }
      //
      if (cellDef.multiple) {
        this.form.multiple = cellDef.multiple
      } else {
        this.form.multiple = 0
      }
      //
      const cellStyle = cellDef.cellStyle
      //
      if (cellStyle.wrapCompute) {
        this.form.wrapCompute = '1'
      } else {
        this.form.wrapCompute = '0'
      }
      // 扩展
      this.form.expand = cellDef.expand
      // 排序
      this.form.order = cellDef.value.order
      //
      if (cellStyle.format) {
        this.form.format = cellStyle.format
      } else {
        this.form.format = ''
      }
      //
      if (!cellDef.value.conditions) {
        cellDef.value.conditions = []
      }
      this.conditions = cellDef.value.conditions
      // 条件属性
      if (!cellDef.conditionPropertyItems) {
        cellDef.conditionPropertyItems = []
      }
      this.conditionPropertyItems = cellDef.conditionPropertyItems
      // 自定义分组
      if (!this.cellDef.value.groupItems) {
        this.cellDef.value.groupItems = []
      }
      this.groupItems = this.cellDef.value.groupItems
    },
    handleDatasetChange(val) {
      for (const ds of this.datasets) {
        if (ds.name === val) {
          this.properties = ds.fields || []
        }
      }
      this.setDatasetName(val)
    },
    handlePropertyChange(val) {
      this.setProperty(val)
    },
    handleAggregateChange(aggregate) {
      this.setAggregate(aggregate)
    },
    handleCustomgroup() {
      this.customGroupValue = {
        dialogVisible: true,
        fields: this.properties,
        cellDef: this.cellDef
      }
    },
    handleOrder(order) {
      this.setOrder(order)
    },
    handleExpand(expand) {
      this.setExpand(expand)
    },
    handleWrapCompute(val) {
      this.setWrapCompute(val)
    },
    handleFormat(val) {
      this.setFormat(val)
    },
    handleFillBlankRows(val) {
      this.setFillBlankRows(val)
    },
    handleMultiple(val) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      cellDef.multiple = val
    },
    setDatasetName(datasetName) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      const valueType = cellDef.value.type
      if (valueType === 'dataset') {
        cellDef.value.datasetName = datasetName
      }
      this.updateTableData()
    },
    setProperty(property) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      const valueType = cellDef.value.type
      if (valueType === 'dataset') {
        cellDef.value.property = property
        cellDef.value.aggregate = 'group'
        this.form.aggregate = 'group'
      }
      this.updateTableData()
    },
    setAggregate(aggregate) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      cellDef.value.aggregate = aggregate
      if (aggregate === 'sum' || aggregate === 'count' || aggregate === 'max' || aggregate === 'min' || aggregate === 'avg') {
        cellDef.value.order = 'none'
        cellDef.expand = 'None'
      } else {
        cellDef.expand = 'Down'
      }
      this.updateTableData()
      this.context.hot.render()
    },
    setOrder(order) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      const valueType = cellDef.value.type
      if (valueType === 'dataset') {
        cellDef.value.order = order
      }
    },
    setExpand(expand) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      const valueType = cellDef.value.type
      if (valueType === 'dataset' || valueType === 'expression') {
        cellDef.expand = expand
      }
      this.context.hot.render()
    },
    updateTableData() {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      const value = cellDef.value
      const valueType = cellDef.value.type
      let data = ''
      if (valueType === 'simple') {
        data = value.value
      } else if (valueType === 'dataset') {
        data = value.datasetName + '.' + value.aggregate + '(' + value.property + ')'
      } else if (valueType === 'expression') {
        data = value.value
      }
      this.context.hot.setDataAtCell(cellDef.rowNumber - 1, cellDef.columnNumber - 1, data)
    },
    setWrapCompute(wrapCompute) {
      for (let i = this.rowIndex; i <= this.row2Index; i++) {
        for (let j = this.colIndex; j <= this.col2Index; j++) {
          const cellDef = this.context.getCell(i, j)
          if (!cellDef) {
            continue
          }
          cellDef.cellStyle.wrapCompute = wrapCompute
        }
      }
    },
    setFormat(format) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      cellDef.cellStyle.format = format
    },
    setFillBlankRows(value) {
      const cellDef = this.context.getCell(this.rowIndex, this.colIndex)
      if (!cellDef) {
        return
      }
      cellDef.fillBlankRows = value
      if (!cellDef.multiple) {
        cellDef.multiple = 0
      }
    },
    handlePropertyCondition: function() {
      this.propertyConditionValue = {
        dialogVisible: true,
        conditionPropertyItems: this.conditionPropertyItems,
        datasources: this.datasources,
        datasetName: this.form.datasetName
      }
    }
  }
}
</script>
