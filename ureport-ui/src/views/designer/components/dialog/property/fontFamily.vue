<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">字体</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.fontFamily" size="mini" @change="handleFontFamily">
        <el-option v-for="(item, index) in fontFamilyOptions" :key="index" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.fontFamilyScope" size="mini" @change="handleFontFamilyScope">
        <el-option v-for="item in scopeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
  </el-row>
</template>

<script>
import { fontFamilys, scopeTypes } from '@/data/select-options'
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
        fontFamily: '宋体',
        fontFamilyScope: 'cell'
      },
      fontFamilyOptions: fontFamilys,
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
      if (val.fontFamily && val.fontFamilyScope) {
        this.form.checked = true
        this.form.fontFamily = val.fontFamily
        this.form.fontFamilyScope = val.fontFamilyScope
      } else {
        this.form.checked = false
        this.form.fontFamily = '宋体'
        this.form.fontFamilyScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.fontFamily = this.form.fontFamily
        this.cellStyle.fontFamilyScope = this.form.fontFamilyScope
      } else {
        this.cellStyle.fontFamily = null
        this.cellStyle.fontFamilyScope = null
      }
    },
    handleFontFamily(val) {
      if (this.form.checked) {
        this.cellStyle.fontFamily = val
      }
    },
    handleFontFamilyScope(val) {
      if (this.form.checked) {
        this.cellStyle.fontFamilyScope = val
      }
    }
  }
}
</script>
