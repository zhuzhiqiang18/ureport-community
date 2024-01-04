<template>
  <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form" size="mini" style="margin-top: 10px;">
    <el-form-item label="类型">
      <el-select v-model="form.type" placeholder="请选择" style="width:100%">
        <el-option
          v-for="item in lineStyles"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="粗细">
      <el-input v-model="form.width" style="width: 100%;" oninput="value=value.replace(/[^\d]/g,'')" />
    </el-form-item>
    <el-form-item label="颜色">
      <colorpicker v-model="form.color" style="width:100%" />
    </el-form-item>
  </el-form>
</template>

<script>
import { lineStyles } from '@/data/select-options'

export default {
  props: {
    form: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      lineStyles: lineStyles
    }
  },
  watch: {

  },
  mounted: function() {

  },
  methods: {

  }
}
</script>
<style>
</style>
