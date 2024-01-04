<template>
  <div>
    <el-collapse v-model="activeNames">
      <el-collapse-item :disabled="!form.legend.show" name="legend">
        <template slot="title">
          <el-switch v-model="form.legend.show" class="el-switch-mimi" style="width:20px;margin-right:5px" @change="handleChange($event,'legend')" />
          图例
        </template>
        <el-form class="div-block-clearance" label-position="left" label-width="90px" :model="form.yAxis" size="mini" style="margin-top: 10px;">
          <el-form-item label="布局方式">
            <el-radio-group v-model="form.legend.orient">
              <el-radio-button label="horizontal">水平</el-radio-button>
              <el-radio-button label="vertical">垂直</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="文本样式">
            <div style="width: 49%;display: inline-block;">
              <el-select v-model="form.legend.position" placeholder="请选择" style="width:100%">
                <el-option
                  v-for="item in legendLocations"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
              <span class="inline-form-item-label">位置</span>
            </div>
            <div style="width: 49%;float: right;">
              <el-select v-model="form.legend.textStyle.fontWeight" placeholder="请选择" style="width:100%">
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
              <el-select v-model="form.legend.textStyle.fontSize" placeholder="请选择" style="width:100%">
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
              <colorpicker v-model="form.legend.textStyle.color" style="width:100%" />
              <span class="inline-form-item-label">字体颜色</span>
            </div>
          </el-form-item>
        </el-form>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
import { titleLocations } from '@/data/select-options'
import { fontWeights, fontSizes, legendLocations } from '@/data/select-options'

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
      legendLocations: legendLocations,
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
