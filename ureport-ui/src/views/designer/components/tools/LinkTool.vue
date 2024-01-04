<template>
  <div class="component-icon-btn" @click="handleLink">
    <div class="component-icon-btn-icon" style="color: #000000;">
      <i class="el-icon-link" />
    </div>
    <el-dialog ref="urlPropertyDialog" title="添加单元格链接" :visible.sync="dialogVisible" append-to-body :close-on-click-modal="false" width="600px">
      <url-property ref="urlPropertyPanel" :cell-def="cellDef" />
    </el-dialog>
  </div>
</template>

<script>
import UrlProperty from './components/UrlProperty'
export default {
  components: {
    UrlProperty
  },
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      dialogVisible: false,
      cellDef: {}
    }
  },
  mounted: function() {

  },
  methods: {
    handleLink() {
      const table = this.context.hot
      const selected = table.getSelected()
      if (!selected) {
        this.$message('请选择单元格')
        return
      }
      const startRow = selected[0]
      const startCol = selected[1]
      this.cellDef = this.context.getCell(startRow, startCol)
      this.dialogVisible = true
    },
    refresh(rowIndex, colIndex, row2Index, col2Index) {
      this.cellDef = this.context.getCell(rowIndex, colIndex)
    }
  }
}
</script>

<style>
</style>
