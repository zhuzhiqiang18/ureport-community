<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">格式化</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="19">
      <el-autocomplete
        v-model="form.format"
        :fetch-suggestions="search"
        placeholder="对数字和日期格式化"
        style="width:100%"
        size="mini"
        :trigger-on-focus="false"
        @change="handleFormat"
        @select="handleFormatSelect"
      />
    </el-col>
  </el-row>
</template>

<script>
import { valueFormats } from '@/data/select-options'
export default {
  props: {
    propertyCondition: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      form: {
        checked: false,
        format: ''
      },
      valueFormats: valueFormats,
      cellStyle: {}
    }
  },
  watch: {
    propertyCondition(val) {
      const cellStyle = val.cellStyle
      this.init(cellStyle)
    }
  },
  mounted: function() {
    const cellStyle = this.propertyCondition.cellStyle
    this.init(cellStyle)
  },
  methods: {
    init(val) {
      if (val.format) {
        this.form.checked = true
        this.form.format = val.format
      } else {
        this.form.checked = false
        this.form.format = ''
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.format = this.form.format
      } else {
        this.cellStyle.format = null
      }
    },
    handleFormatSelect(val) {
      if (this.form.checked) {
        this.cellStyle.format = this.form.format
      }
    },
    handleFormat(val) {
      if (this.form.checked) {
        this.cellStyle.format = this.form.format
      }
    },
    search(str, cb) {
      var valueFormats = this.valueFormats
      var results = valueFormats.filter(function(ele) {
        return ele.value.toLowerCase().indexOf(str.toLowerCase()) > -1
      })
      cb(results)
    }
  }
}
</script>
