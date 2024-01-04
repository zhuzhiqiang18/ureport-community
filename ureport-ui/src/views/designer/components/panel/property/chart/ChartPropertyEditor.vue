<template>
  <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form" size="mini">
    <el-form-item label="数据集:">
      <el-select
        v-model="form.datasetName"
        placeholder="请选择"
        style="width:100%"
        @change="handleDataSet"
      >
        <el-option
          v-for="item in datasetOptions"
          :key="item.name"
          :label="item.name"
          :value="item.name"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="分类属性:">
      <el-select
        v-model="form.categoryProperty"
        placeholder="请选择"
        clearable
        style="width:100%"
        @change="handleProperty"
      >
        <el-option
          v-for="item in fieldOptions"
          :key="item.name"
          :label="item.name"
          :value="item.name"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="格式化:">
      <g-autocomplete
        v-model="form.format"
        placeholder="对数字和日期格式化"
        style="width:100%"
        :data="restaurants"
      />
    </el-form-item>
    <el-form-item label="值属性:">
      <el-button @click="add">添加</el-button>
    </el-form-item>
    <el-table
      :data="form.series"
      size="mini"
      border
      style="width: 100%"
    >
      <el-table-column
        prop="seriesText"
        show-overflow-tooltip
        header-align="center"
        align="left"
        label="名称"
      />
      <el-table-column
        prop="seriesProperty"
        show-overflow-tooltip
        label="属性"
        header-align="center"
        align="left"
      />
      <el-table-column
        prop="collectType"
        align="center"
        width="70"
        label="统计方式"
      >
        <template slot-scope="scope">
          {{ aggregateOptions.find(ele => ele.value === scope.row.collectType).label }}
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        width="76"
        label="操作"
      >
        <template slot-scope="scope">
          <el-link :underline="false" type="primary" style="font-size: 12px;margin-right: 5px;" @click="edit(scope.row)">编辑</el-link>
          <el-link :underline="false" type="primary" style="font-size: 12px;" @click="remove(scope.row)">移除</el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      title="属性值配置"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="400px"
    >
      <el-form ref="series_property_item_form" label-position="left" label-width="90px" :model="row" size="small" :rules="rules">
        <el-form-item label="值属性:" prop="seriesProperty">
          <el-select v-model="row.seriesProperty" placeholder="请选择" style="width:100%">
            <el-option v-for="item in fieldOptions" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="值名称:" prop="seriesText">
          <el-input v-model="row.seriesText" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="聚合方式:">
          <el-select v-model="row.collectType" placeholder="请选择" style="width:100%">
            <el-option v-for="item in aggregateOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="保留小数位:">
          <el-input v-model="row.format" placeholder="请输入内容" oninput="value=value.replace(/[^\d]/g,'')" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible=false">取 消</el-button>
        <el-button type="primary" size="small" @click="save">确 定</el-button>
      </span>
    </el-dialog>
  </el-form>
</template>

<script>
import { valueFormats } from '@/data/select-options'

export default {
  props: {
    chartValue: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      dialogVisible: false,
      datasetOptions: [],
      fieldOptions: [],
      chart: {},
      restaurants: valueFormats,
      aggregateOptions: [
        { value: 'select', label: '罗列数据' },
        { value: 'sum', label: '汇总' },
        { value: 'count', label: '统计数量' },
        { value: 'max', label: '最大值' },
        { value: 'min', label: '最小值' },
        { value: 'avg', label: '平均值' }
      ],
      form: {
        datasetName: '',
        categoryProperty: '',
        format: '',
        series: [],
      },
      oldRow: null,
      rules: {
        seriesProperty: [{ required: true, message: '请选择属性值' }],
        seriesText: [{ required: true, message: '请输属性名称' }]
      },
      row: {
        seriesProperty: '',
        seriesText: '',
        collectType: 'sum',
        format: '',
      }
    }
  },
  watch: {
    chartValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.chartValue)
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
      this.cellDef = cellDef
      this.datasetOptions = this.context.getDataSets()
      const dataset = cellDef.value.chart.dataset
      this.form = {
        datasetName: dataset.datasetName,
        categoryProperty: dataset.categoryProperty,
        format: dataset.format,
        series: dataset.series
      }
      if (dataset.datasetName) {
        const ds = this.datasetOptions.find(item => item.name === dataset.datasetName)
        if (ds) {
          this.fieldOptions = ds.fields || []
        }
      }
    },
    handleDataSet(val) {
      const ds = this.datasetOptions.find(ele => ele.name === val)
      this.fieldOptions = ds.fields
      const dataset = this.cellDef.value.chart.dataset
      this.form.categoryProperty = ''
      dataset.datasetName = val
      if(dataset.series.length > 0) {
        dataset.series.splice(0,dataset.series.length)
      }
      this.form.series = dataset.series
      dataset.categoryProperty = ''
    },
    handleProperty(val) {
      const dataset = this.cellDef.value.chart.dataset
      dataset.categoryProperty = val
      this.chartValue.context.refresh(this.cellDef)
    },
    add() {
      this.oldRow = null
      this.row = {
        seriesProperty: '',
        seriesText: '',
        collectType: 'sum',
        format: ''
      }
      this.dialogVisible = true
    },
    edit(row) {
      this.oldRow = row
      const copyRow = JSON.parse(JSON.stringify(row))
      if (copyRow.format === undefined || copyRow.format === null) {
        copyRow.format = ''
      }
      this.row = copyRow
      this.dialogVisible = true
    },
    remove(row) {
      this.oldRow = null
      const index = this.form.series.indexOf(row)
      if (index > -1) {
        this.form.series.splice(index, 1)
      }
    },
    save() {
      this.$refs['series_property_item_form'].validate((valid) => {
        if (valid) {
          const row = this.row
          if (this.oldRow) {
            const index = this.form.series.indexOf(this.oldRow)
            if (index > -1) {
              this.form.series.splice(index, 1, row)
            }
          } else {
            this.form.series.push(row)
          }
          this.chartValue.context.refresh(this.cellDef)
          this.dialogVisible = false
        }
      })
    }
  }
}
</script>
