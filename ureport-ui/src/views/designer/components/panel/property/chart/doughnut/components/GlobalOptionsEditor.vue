<template>
  <div>
    <el-collapse v-model="activeNames">
      <el-collapse-item :disabled="!form.title.show" name="title">
        <template slot="title">
          <el-switch v-model="form.title.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'title')" />
          标题
        </template>
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form.title" size="mini" style="margin-top: 10px;">
          <el-form-item label="标题内容">
            <el-input v-model="form.title.text" placeholder="" />
          </el-form-item>
          <el-form-item label="文本样式">
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.title.left" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in alignTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">对齐方式</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-select v-model="form.title.textStyle.fontWeight" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in fontWeights"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">字体粗细</span>
            </div>
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.title.textStyle.fontSize" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="v in fontSizes"
                  :key="v"
                  :label="v"
                  :value="v"
                />
              </el-select>
              <span class="inline-form-item-label">字号</span>
            </div>
            <div style="width: 49%;float: right;">
              <colorpicker v-model="form.title.textStyle.color" style="width:100%" />
              <span class="inline-form-item-label">字体颜色</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item name="color" title="主题颜色">
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form.global" size="mini" style="margin-top: 10px;">
          <el-form-item v-for="(value, index) in form.global.color" label="">
            <colorpicker v-model="form.global.color[index]" style="width:100%" />
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item :disabled="!form.label.show" name="label">
        <template slot="title">
          <el-switch v-model="form.label.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'label')" />
          标注
        </template>
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form.label" size="mini" style="margin-top: 10px;">
          <el-form-item label="文本样式">
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.label.position" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in echartsLablePositions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">标注位置</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-select v-model="form.label.textStyle.fontWeight" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in fontWeights"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">字体粗细</span>
            </div>
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.label.textStyle.fontSize" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="v in fontSizes"
                  :key="v"
                  :label="v"
                  :value="v"
                />
              </el-select>
              <span class="inline-form-item-label">字号</span>
            </div>
            <div style="width: 49%;float: right;">
              <colorpicker v-model="form.label.textStyle.color" style="width:100%" />
              <span class="inline-form-item-label">字体颜色</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item title="展示样式" name="global">
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form.global" size="mini" style="margin-top: 10px;">
          <el-form-item label="半径">
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.global.outsideWidth" style="width: 100%;" placeholder="" :min="0" :max="100" controls-position="right" />
              <span class="inline-form-item-label">外环</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.global.insideWidth" style="width: 100%;" placeholder="" :min="0" :max="100" controls-position="right" />
              <span class="inline-form-item-label">内环</span>
            </div>
          </el-form-item>
          <el-form-item label="位置">
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.global.offsetX" style="width: 100%;" placeholder="" :min="0" :max="100" controls-position="right" />
              <span class="inline-form-item-label">水平</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.global.offsetY" style="width: 100%;" placeholder="" :min="0" :max="100" controls-position="right" />
              <span class="inline-form-item-label">垂直</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>

import { fontWeights, fontSizes, alignTypes } from '@/data/select-options'

export default {
  props: {
    form: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      fontWeights: fontWeights,
      fontSizes: fontSizes,
      alignTypes: alignTypes,
      echartsLablePositions: [
        { value: 'outside', label: '外部' },
        { value: 'inside', label: '内部' }
      ],
      activeNames: []
    }
  },
  watch: {

  },
  mounted: function() {

  },
  methods: {
    handleChange(newValue, target) {
      if (!newValue) {
        const index = this.activeNames.indexOf(target)
        this.activeNames.splice(index, 1)
      }
    }
  }
}
</script>

<style>

</style>
