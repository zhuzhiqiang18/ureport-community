<template>
  <el-form label-position="left" :model="form" size="mini" label-width="90px">
    <el-form-item label="文本内容" style="margin-bottom: 8px;" />
    <el-input v-model="form.value" type="textarea" rows="6" @change="handleValue" />
  </el-form>
</template>

<script>
export default {
  props: {
    simpleValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      form: {
        value: ''
      }
    }
  },
  watch: {
    simpleValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.simpleValue)
  },
  methods: {
    init(val) {
      if (val.context) {
        this.context = val.context
        this.refresh(val.rowIndex, val.colIndex, val.row2Index, val.col2Index)
      }
    },
    refresh(rowIndex, colIndex, row2Index, col2Index) {
      const cellDef = this.context.getCell(rowIndex, colIndex)
      if (!cellDef) {
        return
      }
      this.rowIndex = rowIndex
      this.colIndex = colIndex
      this.row2Index = row2Index
      this.col2Index = col2Index
      this.cellDef = cellDef
      this.form.value = cellDef.value.value
    },
    handleValue(value) {
      const rowIndex = this.rowIndex
      const colIndex = this.colIndex
      this.cellDef.value.value = value
      this.context.hot.setDataAtCell(rowIndex, colIndex, value)
    }
  }
}
</script>
