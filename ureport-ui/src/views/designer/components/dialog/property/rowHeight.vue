<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" style="line-height: 28px;" :disabled="!propertyCondition.name" @change="handleCheck">行高</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="19">
      <el-input
        v-model="form.rowHeight"
        oninput="value=value.replace(/[^\d]/g,'')"
        size="mini"
        style="width:100%"
        @change="handleChange"
      />
    </el-col>
  </el-row>
</template>

<script>
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
        rowHeight: 0
      }
    }
  },
  watch: {
    propertyCondition(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.propertyCondition)
  },
  methods: {
    init(val) {
      if (val.colWidth !== -1 && val.rowHeight !== null && val.rowHeight !== undefined) {
        this.form.checked = true
        this.form.rowHeight = val.rowHeight
      } else {
        this.form.checked = false
        this.form.rowHeight = 0
      }
    },
    handleCheck(val) {
      if (val) {
        this.propertyCondition.rowHeight = this.form.rowHeight
      } else {
        this.propertyCondition.rowHeight = null
      }
    },
    handleChange(val) {
      if (this.form.checked) {
        this.propertyCondition.rowHeight = this.form.rowHeight
      }
    }
  }
}
</script>
