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
      <el-collapse-item name="grid" title="图表边距">
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form.grid" size="mini" style="margin-top: 10px;">
          <el-form-item label="">
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.grid.top" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">顶部</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.grid.bottom" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">底部</span>
            </div>
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.grid.left" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">左侧</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.grid.right" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">右侧</span>
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
          <el-form-item label="相对偏移">
            <div style="width: 49%;display: inline-block;">
              <el-input-number v-model="form.label.offsetX" style="width: 100%;" placeholder="" controls-position="right" />
              <span class="inline-form-item-label">水平</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-input-number v-model="form.label.offsetY" style="width: 100%;" placeholder="" :min="0" :max="8888" controls-position="right" />
              <span class="inline-form-item-label">垂直</span>
            </div>
          </el-form-item>
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
    </el-collapse>
    <el-row style="border-bottom: 1px solid #EBEEF5;height: 38px">
      <el-col :span="12" style="color: #606266;font-size: 12px;line-height: 38px;">系列名</el-col>
      <el-col :span="8" style="color: #606266;font-size: 12px;line-height: 38px;text-align: center;">图表类型</el-col>
      <el-col :span="4" style="color: #606266;font-size: 12px;line-height: 38px;text-align: center;">次坐标轴</el-col>
    </el-row>
    <el-row v-for="(item, index) in series" style="border-bottom: 1px solid #EBEEF5;height: 38px;padding: 5px 0px;">
      <el-col :span="12" style="color: #606266;font-size: 12px;line-height: 28px;">
       {{item.seriesText}}
      </el-col>
      <el-col :span="8">
        <el-select
          v-model="item.type"
          size="mini"
          placeholder="簇状柱形图"
          style="width:100%"
          @change="handleChartTypeChange($event,index)"
        >
          <el-option
            v-for="item in chartTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-col>
      <el-col :span="4" style="text-align: center;line-height: 28px;">
        <el-checkbox v-model="item.yAxisIndex" @change="handleChartTypeChange($event,index)" />
      </el-col>
    </el-row>
  </div>
</template>

<script>

import { fontWeights, fontSizes, alignTypes } from '@/data/select-options'

export default {
  props: {
    form: {
      type: Object,
      required: true
    },
    series: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      fontWeights: fontWeights,
      fontSizes: fontSizes,
      alignTypes: alignTypes,
      echartsLablePositions: [
        { value: 'right', label: '顶部' },
        { value: 'left', label: '底部' },
        { value: 'inside', label: '内部' }
      ],
      chartTypeOptions: [
        { value: 'bar-group', label: '簇状柱形图' },
        { value: 'bar-stack', label: '堆积柱形图' },
        { value: 'line', label: '折线图' }
      ],
      activeNames: []
    }
  },
  mounted: function() {

  },
  methods: {
    handleChange(newValue, target) {
      if (!newValue) {
        const index = this.activeNames.indexOf(target)
        this.activeNames.splice(index, 1)
      }
    },
    handleChartTypeChange(val, index) {
      const target = this.series[index]
      if(target.type !== 'line') {
        for(let i = 0; i < this.series.length; i++) {
          const item = this.series[i]
          if(i !== index && item.type !== 'line') {
            if(target.yAxisIndex && item.yAxisIndex) {
              this.$set(item, 'type', target.type)
            } else if(!target.yAxisIndex && !item.yAxisIndex) {
              this.$set(item, 'type', target.type)
            }
          }
        }
      }
    }
  }
}
</script>

<style>

</style>
