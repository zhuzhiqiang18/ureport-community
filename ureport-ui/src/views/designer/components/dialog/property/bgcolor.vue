<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">背景色</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <colorpicker v-model="form.bgcolor" @change="handleBgcolor" />
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.bgcolorScope" size="mini" @change="handleBgcolorScope">
        <el-option v-for="item in scopeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
  </el-row>
</template>
<script>
import { scopeTypes } from '@/data/select-options'

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
        bgcolor: 'rgb(255, 255, 255)',
        bgcolorScope: 'cell'
      },
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
      if (val.bgcolor && val.bgcolorScope) {
        this.form.checked = true
        this.form.bgcolor = 'rgb(' + val.bgcolor + ')'
        this.form.bgcolorScope = val.bgcolorScope
      } else {
        this.form.checked = false
        this.form.bgcolor = 'rgb(255, 255, 255)'
        this.form.bgcolorScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.bgcolor = this.form.bgcolor.replace('rgb(','').replace(')','')
        this.cellStyle.bgcolorScope = this.form.bgcolorScope
      } else {
        this.cellStyle.bgcolor = null
        this.cellStyle.bgcolorScope = null
      }
    },
    handleBgcolor(val) {
      if (this.form.checked) {
        this.cellStyle.bgcolor = val.replace('rgb(','').replace(')','')
      }
    },
    handleBgcolorScope(val) {
      if (this.form.checked) {
        this.cellStyle.bgcolorScope = val
      }
    }
  }
}
</script>
