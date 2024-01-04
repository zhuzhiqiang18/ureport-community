<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">字号</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.fontSize" size="mini" @change="handleFontSize">
        <el-option v-for="item in fontSizeOptions" :key="item" :label="item" :value="item" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.fontSizeScope" size="mini" @change="handleFontSizeScope">
        <el-option v-for="item in scopeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
  </el-row>
</template>

<script>
import { fontSizes, scopeTypes } from '@/data/select-options'
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
        fontSize: 10,
        fontSizeScope: 'cell'
      },
      fontSizeOptions: fontSizes,
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
      if (val.fontSize && val.fontSizeScope) {
        this.form.checked = true
        this.form.fontSize = val.fontSize
        this.form.fontSizeScope = val.fontSizeScope
      } else {
        this.form.checked = false
        this.form.fontSize = 10
        this.form.fontSizeScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.fontSize = this.form.fontSize
        this.cellStyle.fontSizeScope = this.form.fontSizeScope
      } else {
        this.cellStyle.fontSize = null
        this.cellStyle.fontSizeScope = null
      }
    },
    handleFontSize(val) {
      if (this.form.checked) {
        this.cellStyle.fontSize = val
      }
    },
    handleFontSizeScope(val) {
      if (this.form.checked) {
        this.cellStyle.fontSizeScope = val
      }
    }
  }
}
</script>
