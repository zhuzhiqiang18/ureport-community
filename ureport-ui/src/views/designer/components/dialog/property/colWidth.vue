<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" style="line-height: 28px;" :disabled="!propertyCondition.name" @change="handleCheck">列宽</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="19">
      <el-input
        v-model="form.colWidth"
        size="mini"
        oninput="value=value.replace(/[^\d]/g,'')"
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
        colWidth: 0
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
      if (val.colWidth !== -1 && val.colWidth !== null && val.colWidth !== undefined) {
        this.form.checked = true
        this.form.colWidth = val.colWidth
      } else {
        this.form.checked = false
        this.form.colWidth = 0
      }
    },
    handleCheck(val) {
      if (val) {
        this.propertyCondition.colWidth = this.form.colWidth
      } else {
        this.propertyCondition.colWidth = null
      }
    },
    handleChange(val) {
      if (this.form.checked) {
        this.propertyCondition.colWidth = this.form.colWidth
      }
    }
  }
}
</script>
