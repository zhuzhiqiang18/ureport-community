<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">下划线</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.underline" size="mini" @change="handleUnderline">
        <el-option v-for="item in booleanOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.underlineScope" size="mini" @change="handleUnderlineScope">
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
        underline: 'false',
        underlineScope: 'cell'
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
  created() {

  },
  mounted: function() {
    const cellStyle = this.propertyCondition.cellStyle
    this.init(cellStyle)
  },
  methods: {
    init(val) {
      if (val.underline && val.underlineScope) {
        this.form.checked = true
        this.form.underline = val.underline
        this.form.underlineScope = val.underlineScope
      } else {
        this.form.checked = false
        this.form.underline = 'false'
        this.form.underlineScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.underline = this.form.underline
        this.cellStyle.underlineScope = this.form.underlineScope
      } else {
        this.cellStyle.underline = null
        this.cellStyle.underlineScope = null
      }
    },
    handleUnderline(val) {
      if (this.form.checked) {
        this.cellStyle.underline = val
      }
    },
    handleUnderlineScope(val) {
      if (this.form.checked) {
        this.cellStyle.underlineScope = val
      }
    }
  }
}
</script>
