<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" style="line-height: 28px;" :disabled="!propertyCondition.name" @change="handleCheck">新值</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="19">
      <el-input v-model="form.newValue" placeholder="请输入内容" size="mini" @change="handleChange" />
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
        newValue: ''
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
      if (val.newValue) {
        this.form.checked = true
        this.form.newValue = val.newValue
      } else {
        this.form.checked = false
        this.form.newValue = ''
      }
    },
    handleCheck(val) {
      if (val) {
        this.propertyCondition.newValue = this.form.newValue
      } else {
        this.propertyCondition.newValue = null
      }
    },
    handleChange(val) {
      if (this.form.checked) {
        this.propertyCondition.newValue = this.form.newValue
      }
    }
  }
}
</script>
