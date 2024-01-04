<template>
  <div>
    <div style="width: 100%;text-align: center;margin-bottom: 10px;">
      <el-radio-group v-model="type" size="mini">
        <el-radio-button label="global">全局</el-radio-button>
        <el-radio-button label="x">X轴</el-radio-button>
        <el-radio-button label="y">Y主轴</el-radio-button>
        <el-radio-button label="y1">Y次轴</el-radio-button>
        <el-radio-button label="legend">图例</el-radio-button>
      </el-radio-group>
    </div>
    <template v-if="options">
      <global-options-editor v-if="type==='global' && series" :form="options" :series="series" />
      <axis-options-editor v-if="type==='x'" :form="options.xAxis" />
      <axis-options-editor v-if="type==='y'" :form="options.yAxis" />
      <axis-options-editor v-if="type==='y1'" :form="options.yAxis1" />
      <legend-options-editor v-if="type==='legend'" :form="options" />
    </template>
  </div>
</template>

<script>

import GlobalOptionsEditor from './components/GlobalOptionsEditor'
import AxisOptionsEditor from '../components/AxisOptionsEditor'
import LegendOptionsEditor from '../components/LegendOptionsEditor'
import { Config } from './config'

export default {
  components: {
    GlobalOptionsEditor,
    AxisOptionsEditor,
    LegendOptionsEditor
  },
  props: {
    chartValue: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      options: null,
      type: 'global',
      config: Config,
      series: null
    }
  },
  watch: {
    chartValue: function(val) {
      this.init(val)
    },
    options: {
      handler() {
        this.refreshChart()
      },
      deep: true
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
      const chart = cellDef.value.chart
      let options = chart.options
      if (!options) {
        options = JSON.parse(JSON.stringify(this.config))
        chart.options = options
      }
      this.options = options
      const dataset = chart.dataset
      if (!dataset.series) {
        dataset.series = []
      }
      this.series = dataset.series
    },
    refreshChart(item) {
      this.chartValue.context.refresh(this.cellDef)
    }
  }
}
</script>

<style>

</style>
