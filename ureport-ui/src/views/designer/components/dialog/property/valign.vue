<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">垂直对齐</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="8">
      <el-select v-model="form.valign" size="mini" @change="handleValign">
        <el-option v-for="item in valignOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
    <el-col v-if="form.checked" :span="4" class="property-row-item-label second">
      <span>作用范围</span>
    </el-col>
    <el-col v-if="form.checked" :span="7">
      <el-select v-model="form.valignScope" size="mini" @change="handleValignScope">
        <el-option v-for="item in scopeOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </el-col>
  </el-row>
</template>

<script>
import { valignTypes, scopeTypes } from '@/data/select-options'

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
        valign: 'middle',
        valignScope: 'cell'
      },
      valignOptions: valignTypes,
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
      if (val.valign && val.valignScope) {
        this.form.checked = true
        this.form.valign = val.valign
        this.form.valignScope = val.valignScope
      } else {
        this.form.checked = false
        this.form.valign = 'middle'
        this.form.valignScope = 'cell'
      }
      this.cellStyle = this.propertyCondition.cellStyle
    },
    handleCheck(val) {
      if (val) {
        this.cellStyle.valign = this.form.valign
        this.cellStyle.valignScope = this.form.valignScope
      } else {
        this.cellStyle.valign = null
        this.cellStyle.valignScope = null
      }
    },
    handleValign(val) {
      if (this.form.checked) {
        this.cellStyle.valign = val
      }
    },
    handleValignScope(val) {
      if (this.form.checked) {
        this.cellStyle.valignScope = val
      }
    }
  }
}
</script>
