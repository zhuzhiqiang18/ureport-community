<template>
  <div>
    <div style="width: 100%;text-align: center;margin-bottom: 10px;">
      <el-radio-group v-model="type" size="mini">
        <el-radio-button label="global">全局</el-radio-button>
        <el-radio-button label="legend">图例</el-radio-button>
      </el-radio-group>
    </div>
    <template v-if="options">
      <global-options-editor v-if="type==='global'" :form="options" />
      <legend-options-editor v-if="type==='legend'" :form="options" />
    </template>
  </div>
</template>

<script>

import GlobalOptionsEditor from './components/GlobalOptionsEditor'
import LegendOptionsEditor from '../components/LegendOptionsEditor'
import { Config } from './config'

export default {
  components: {
    GlobalOptionsEditor,
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
      config: Config
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
    },
    refreshChart(item) {
      this.chartValue.context.refresh(this.cellDef)
    }
  }
}
</script>

<style>

</style>
