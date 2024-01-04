<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">倾斜</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.italic" size="mini" @change="handleItalic">
        <el-option v-for="item in booleanOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.italicScope" size="mini" @change="handleItalicScope">
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
        italic: 'false',
        italicScope: 'cell'
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
      if (val.italic && val.italicScope) {
        this.form.checked = true
        this.form.italic = val.italic
        this.form.italicScope = val.italicScope
      } else {
        this.form.checked = false
        this.form.italic = 'false'
        this.form.italicScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.italic = this.form.italic
        this.cellStyle.italicScope = this.form.italicScope
      } else {
        this.cellStyle.italic = null
        this.cellStyle.italicScope = null
      }
    },
    handleItalic(val) {
      if (this.form.checked) {
        this.cellStyle.italic = val
      }
    },
    handleItalicScope(val) {
      if (this.form.checked) {
        this.cellStyle.italicScope = val
      }
    }
  }
}
</script>
