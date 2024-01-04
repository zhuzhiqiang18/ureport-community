<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">前景色</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <colorpicker v-model="form.forecolor" @change="handleForecolor" />
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.forecolorScope" size="mini" @change="handleForecolorScope">
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
        forecolor: 'rgb(0, 1, 0)',
        forecolorScope: 'cell'
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
  created() {

  },
  mounted: function() {
    const cellStyle = this.propertyCondition.cellStyle
    this.init(cellStyle)
  },
  methods: {
    init(val) {
      if (val.forecolor && val.forecolorScope) {
        this.form.checked = true
        this.form.forecolor = 'rgb(' + val.forecolor + ')'
        this.form.forecolorScope = val.forecolorScope
      } else {
        this.form.checked = false
        this.form.forecolor = 'rgb(0, 1, 0)'
        this.form.forecolorScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.forecolor = this.form.forecolor.replace('rgb(','').replace(')','')
        this.cellStyle.forecolorScope = this.form.forecolorScope
      } else {
        this.cellStyle.forecolor = null
        this.cellStyle.forecolorScope = null
      }
    },
    handleForecolor(val) {
      if (this.form.checked) {
        this.cellStyle.forecolor = val.replace('rgb(','').replace(')','')
      }
    },
    handleForecolorScope(val) {
      if (this.form.checked) {
        this.cellStyle.forecolorScope = val
      }
    }
  }
}
</script>
