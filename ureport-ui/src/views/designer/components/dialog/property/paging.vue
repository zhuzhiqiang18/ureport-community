<template>
  <el-row class="property-row-item">
    <el-col :span="5" class="property-row-item-label">
      <el-checkbox v-model="form.checked" :disabled="!propertyCondition.name" @change="handleCheck">分页</el-checkbox>
    </el-col>
    <el-col v-if="form.checked" :span="19">
      <el-select v-model="form.paging.position" style="width:100%" size="mini" @change="handleChange">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
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
        paging: {
          position: 'after',
          line: 0
        }
      },
      options: [{
        value: 'before',
        label: '当前行前'
      },
      {
        value: 'after',
        label: '当前行后'
      }
      ]
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
      if (val.paging) {
        this.form.checked = true
        this.form.paging = val.paging
      } else {
        this.form.checked = false
        this.form.paging = {
          position: 'after',
          line: 0
        }
      }
    },
    handleCheck(val) {
      if (val) {
        this.propertyCondition.paging = this.form.paging
      } else {
        this.propertyCondition.paging = null
      }
    },
    handleChange(val) {
      if (this.form.checked) {
        this.propertyCondition.paging = this.form.paging
      }
    }
  },
  template: `
		
	`
}
</script>
