<template>
  <div>
    <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form" size="mini" style="margin-top: 10px;">
      <el-form-item label="两端留白">
        <el-switch v-model="form.boundaryGap" class="el-switch-small" />
      </el-form-item>
    </el-form>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="title" :disabled="!form.title.show">
        <template slot="title">
          <el-switch v-model="form.title.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'title')" />
          轴标题
        </template>
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form" size="mini" style="margin-top: 10px;">
          <el-form-item label="标题内容">
            <el-input v-model="form.title.name" placeholder="" />
          </el-form-item>
          <el-form-item label="文本样式">
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.title.location" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in titleLocations"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">位置</span>
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
          <el-form-item label="展示方式">
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.title.rotate" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">旋转</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.title.offset" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">偏移</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item name="axisLine" :disabled="!form.axisLine.show">
        <template slot="title">
          <el-switch v-model="form.axisLine.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'axisLine')" />
          轴线
        </template>
        <line-style :form="form.axisLine.lineStyle" />
      </el-collapse-item>
      <el-collapse-item name="axisTick" :disabled="!form.axisTick.show">
        <template slot="title">
          <el-switch v-model="form.axisTick.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'axisTick')" />
          轴刻度
        </template>
        <line-style :form="form.axisTick.lineStyle" />
      </el-collapse-item>
      <el-collapse-item name="axisLabel" :disabled="!form.axisLabel.show">
        <template slot="title">
          <el-switch v-model="form.axisLabel.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'axisLabel')" />
          轴标签
        </template>
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form" size="mini" style="margin-top: 10px;">
          <el-form-item label="展示方式">
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.axisLabel.rotate" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">旋转</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.axisLabel.margin" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">偏移</span>
            </div>
          </el-form-item>
          <el-form-item label="文本样式">
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.axisLabel.interval" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in intervalTypes"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">间隔</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-select v-model="form.axisLabel.textStyle.fontWeight" placeholder="请选择" style="width:100%">
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
              <el-select v-model="form.axisLabel.textStyle.fontSize" placeholder="请选择" style="width:100%">
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
              <colorpicker v-model="form.axisLabel.textStyle.color" style="width:100%" />
              <span class="inline-form-item-label">字体颜色</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
      <el-collapse-item name="splitLine" :disabled="!form.splitLine.show">
        <template slot="title">
          <el-switch v-model="form.splitLine.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'splitLine')" />
          网格线
        </template>
        <line-style :form="form.splitLine.lineStyle" />
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import lineStyle from './line-style'
import { titleLocations } from '@/data/select-options'
import { fontWeights, fontSizes } from '@/data/select-options'

export default {
  components: {
    lineStyle
  },
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
      titleLocations: titleLocations,
      booleanTypes: [
        { value: true, label: '是' },
        { value: false, label: '否' }
      ],
      activeNames: [],
      intervalTypes: [
        { value: 'auto', label: '自适应' },
        { value: 0, label: 0 },
        { value: 1, label: 1 },
        { value: 2, label: 2 }
      ]
    }
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
