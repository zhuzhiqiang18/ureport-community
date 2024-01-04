<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">加粗</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.bold" size="mini" @change="handleBold">
        <el-option v-for="item in booleanOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.boldScope" size="mini" @change="handleBoldScope">
        <el-option v-for="item in scopeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
  </el-row>
</template>

<script>
import { booleanTypes, scopeTypes } from '@/data/select-options'
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
        bold: 'false',
        boldScope: 'cell'
      },
      booleanOptions: booleanTypes,
      scopeOptions: scopeTypes,
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
      if (val.bold && val.boldScope) {
        this.form.checked = true
        this.form.bold = val.bold
        this.form.boldScope = val.boldScope
      } else {
        this.form.checked = false
        this.form.bold = 'false'
        this.form.boldScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.bold = this.form.bold
        this.cellStyle.boldScope = this.form.boldScope
      } else {
        this.cellStyle.bold = null
        this.cellStyle.boldScope = null
      }
    },
    handleBold(val) {
      if (this.form.checked) {
        this.cellStyle.bold = val
      }
    },
    handleBoldScope(val) {
      if (this.form.checked) {
        this.cellStyle.boldScope = val
      }
    }
  }
}
</script>
