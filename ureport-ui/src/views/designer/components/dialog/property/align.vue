<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">水平对齐</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.align" placeholder="请选择" size="mini" @change="handleAlign">
        <el-option v-for="item in alignOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.alignScope" placeholder="请选择" size="mini" @change="handleAlignScope">
        <el-option v-for="item in scopeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
  </el-row>
</template>
<script>
import { alignTypes, scopeTypes } from '@/data/select-options'
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
        align: 'left',
        alignScope: 'cell'
      },
      alignOptions: alignTypes,
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
      if (val.align && val.alignScope) {
        this.form.checked = true
        this.form.align = val.align
        this.form.alignScope = val.alignScope
      } else {
        this.form.checked = false
        this.form.align = 'left'
        this.form.alignScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.align = this.form.align
        this.cellStyle.alignScope = this.form.alignScope
      } else {
        this.cellStyle.align = null
        this.cellStyle.alignScope = null
      }
    },
    handleAlign(val) {
      if (this.form.checked) {
        this.cellStyle.align = val
      }
    },
    handleAlignScope(val) {
      if (this.form.checked) {
        this.cellStyle.alignScope = val
      }
    }
  }
}
</script>
