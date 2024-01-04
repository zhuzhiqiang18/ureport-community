<template>
  <el-dialog
    title="条件属性配置"
    :visible.sync="propertyConditionValue.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    class="condition-dialog"
    width="960px"
  >
    <el-container>
      <el-aside width="180px">
        <condition-items :property-conditions="propertyConditions" :set-condition-item="setConditionItem" />
      </el-aside>
      <el-aside width="300px">
        <condition-rules :property-condition="propertyCondition" :fields="fields" />
      </el-aside>
      <el-main>
        <condition-style :property-condition="propertyCondition" />
      </el-main>
    </el-container>
  </el-dialog>
</template>

<script>

import ConditionItems from './property/ConditionItems'
import ConditionRules from './property/ConditionRules'
import ConditionStyle from './property/ConditionStyle'

export default {
  components: {
    ConditionItems,
    ConditionRules,
    ConditionStyle
  },
  props: {
    propertyConditionValue: {
      type: Object,
      required: true
    },
    conditionAttributes: {
      type: Array,
      default: () => {
        return []
      }
    },
    propertyOptions: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data: function() {
    return {
      fields: [],
      propertyConditions: [],
      propertyCondition: {
        conditions: [],
        cellStyle: {}
      }
    }
  },
  watch: {
    propertyConditionValue(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.propertyConditionValue)
  },
  methods: {
    init(val) {
      this.propertyConditions = val.conditionPropertyItems || []
      this.fields = this.buildFields()
    },
    setConditionItem(item) {
      if(item) {
        if (!item.conditions) {
          item.conditions = []
        }
        if (!item.cellStyle) {
          item.cellStyle = {}
        }
        this.conditions = item.conditions
        this.propertyCondition = item
      }
    },
    buildFields() {
      let fields = []
      const datasources = this.propertyConditionValue.datasources
      const datasetName = this.propertyConditionValue.datasetName
      if (!datasetName || datasetName === '') {
        return fields
      }
      for (const ds of datasources) {
        const datasets = ds.datasets || []
        for (const dataset of datasets) {
          if (dataset.name === datasetName) {
            fields = dataset.fields || []
            break
          }
        }
        if (fields.length > 0) {
          break
        }
      }
      return fields
    }
  }
}
</script>
