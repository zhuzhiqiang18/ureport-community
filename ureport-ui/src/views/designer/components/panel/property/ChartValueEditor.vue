<template>
  <div>
    <el-form label-position="left" label-width="90px" size="mini">
      <el-form-item label="图表类型:" style="margin-bottom: 5px;">
        <el-select v-model="type" placeholder="请选择" style="width: 100%;" @change="handleChart">
          <el-option v-for="item in chartOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
    </el-form>
    <el-tabs v-model="activeName" stretch>
      <el-tab-pane label="数据绑定" name="first">
        <chart-property-editor :chart-value="chartValue" />
      </el-tab-pane>
      <el-tab-pane label="过滤条件" name="five">
        <condition-dialog :conditions="conditions" :fields="properties" />
      </el-tab-pane>
      <el-tab-pane label="数据映射" name="third">
        <dictionary-editor :data-mapping="dict" />
      </el-tab-pane>
      <el-tab-pane label="图表样式" name="second">
        <histogram v-if="type==='histogram'" :chart-value="chartValue" />
        <chartbar v-if="type==='bar'" :chart-value="chartValue" />
        <chartline v-if="type==='line'" :chart-value="chartValue" />
        <chartarea v-if="type==='area'" :chart-value="chartValue" />
        <chartpie v-if="type==='pie'" :chart-value="chartValue" />
        <chartdoughnut v-if="type==='doughnut'" :chart-value="chartValue" />
        <chartradar v-if="type==='radar'" :chart-value="chartValue" />
        <chartscatter v-if="type==='scatter'" :chart-value="chartValue" />
        <chartbubble v-if="type==='bubble'" :chart-value="chartValue" />
        <chartcombo v-if="type==='combo'" :chart-value="chartValue" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import ChartPropertyEditor from './chart/ChartPropertyEditor'
import DictionaryEditor from './DictionaryEditor'
import ConditionDialog from '../../dialog/ConditionDialog'

import histogram from './chart/histogram/index'
import chartbar from './chart/bar/index'
import chartline from './chart/line/index'
import chartarea from './chart/area/index'
import chartpie from './chart/pie/index'
import chartdoughnut from './chart/doughnut/index'
import chartradar from './chart/radar/index'
import chartscatter from './chart/scatter/index'
import chartbubble from './chart/bubble/index'
import chartcombo from './chart/combo/index'

export default {
  components: {
    ChartPropertyEditor,
    DictionaryEditor,
    ConditionDialog,
    histogram,
    chartbar,
    chartline,
    chartarea,
    chartpie,
    chartdoughnut,
    chartradar,
    chartscatter,
    chartbubble,
    chartcombo
  },
  props: {
    chartValue: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      activeName: 'first',
      dialogVisible: false,
      type: '',
      properties: [],
      conditions: [],
      dict: {},
      chartOptions: [
        { value: 'histogram', label: '柱状图' },
        { value: 'bar', label: '条形图' },
        { value: 'line', label: '折线图' },
        { value: 'area', label: '区域图' },
        { value: 'pie', label: '饼状图' },
        { value: 'doughnut', label: '环形图' },
        { value: 'radar', label: '雷达图' },
        { value: 'scatter', label: '散点图' },
        { value: 'bubble', label: '气泡图' },
        { value: 'combo', label: '组合图' }
      ]
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
      this.options = null
      const cellDef = this.context.getCell(rowIndex, colIndex)
      if (!cellDef) {
        return
      }
      this.cellDef = cellDef
      const chart = cellDef.value.chart
      if (!chart.dataset.dict) {
        chart.dataset.dict = {}
      }
      const datasets = this.context.getDataSets()
      this.dict = {
        datasets: datasets,
        cellDef: {
          value: chart.dataset.dict
        }
      }
      // 选中数据集字段
      const dataset = cellDef.value.chart.dataset
      this.properties = []
      for (var i = 0; i < datasets.length; i++) {
        const item = datasets[i]
        if (dataset.datasetName === item.name) {
          this.properties = item.fields
        }
      }
      // 初始化系列值
      if (!dataset.series) {
        dataset.series = []
      }
      // 过滤条件
      if (!cellDef.value.conditions) {
        cellDef.value.conditions = []
      }
      this.conditions = cellDef.value.conditions
      this.type = chart.dataset.type
    },
    handleChart(val) {
      const chart = this.cellDef.value.chart
      chart.dataset.type = val
      chart.options = null
      this.chartValue.context.refresh(this.cellDef)
    }
  }
}
</script>

<style>
  .el-collapse-item__header {
    height: 39px;
    line-height: 39px;
  }

  .el-collapse-item__header.is-active {
    border-bottom-color: #EBEEF5;
  }

  .el-switch-mimi.el-switch.is-checked .el-switch__core::after {
      margin-left: -10px;
  }

  .el-switch-mimi .el-switch__core {
    width: 22px;
    height: 10px;
  }

  .el-switch-mimi .el-switch__core:after {
      content: "";
      position: absolute;
      top: -1px;
      left: 0px;
      border-radius: 100%;
      -webkit-transition: all .3s;
      transition: all .3s;
      width: 10px;
      height: 10px;
      background-color: #FFF;
  }

  .el-switch-mimi .el-collapse-item__content {
    padding-bottom: 0px;
  }

  .el-input-number.el-input-number--mini.is-controls-right .el-input__inner {
    text-align: left;
  }

  .inline-form-item-label {
    font-size: 12px;
    color: #909399;
  }

  .el-switch-small.el-switch.is-checked .el-switch__core::after {
    margin-left: -14px;
  }

  .el-switch-small .el-switch__core {
    width: 32px !important;
    height: 16px;
  }

  .el-switch-small .el-switch__core:after {
    top: 0px;
    left: 1px;
    width: 14px;
    height: 14px;
  }
</style>
