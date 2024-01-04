<template>
  <div>
    <fieldset style="margin-top: 10px;">
      <legend>分页配置</legend>
      <el-form label-width="80px" label-position="left" :model="paper" size="mini">
        <el-form-item label="分页:">
          <el-radio-group v-model="paper.pageEnabled">
            <el-radio-button :label="false">禁用</el-radio-button>
            <el-radio-button :label="true">启用</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分页方式:">
          <el-radio-group v-model="paper.pagingMode">
            <el-radio-button label="fitpage">自动</el-radio-button>
            <el-radio-button label="fixrows">固定</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="每页行数:">
          <el-input v-model="paper.fixRows" :disabled="paper.pagingMode !=='fixrows'"  oninput="value=value.replace(/[^\d]/g,'')" style="width: 136px;" size="mini" />
        </el-form-item>
      </el-form>
    </fieldset>
    <fieldset style="margin-top: 10px;">
      <legend>分栏配置</legend>
      <el-form label-width="80px" label-position="left" :model="paper" size="mini">
          <el-form-item label="分栏:">
            <el-radio-group v-model="paper.columnEnabled">
              <el-radio-button :label="false">禁用</el-radio-button>
              <el-radio-button :label="true">启用</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="栏数:">
            <el-select v-model="paper.columnCount" :disabled="!paper.columnEnabled" style="width: 120px;">
              <el-option
                v-for="(item,index) in columnCountOptions"
                :key="index+1"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="栏间距:">
            <el-input v-model="paper.columnMMMargin" :disabled="!paper.columnEnabled" oninput="value=value.replace(/[^\d]/g,'')" style="width: 120px;" size="mini" @change="handleColumnMargin" />
          </el-form-item>
        </el-form>
    </fieldset>
  </div>
</template>

<script>
import { pointToMM, mmToPoint } from '../../Utils.js'
export default {
  name: 'PaginationSetting',
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      paper: {
        pageEnabled: false,
        pagingMode: 'fitpage',
        fixRows: 10,
        columnEnabled: false,
        columnCount: 2,
        columnMMMargin: 2,
        columnMargin: 2
      },
      columnCountOptions: [
        { label: '2栏', value: 2 },
        { label: '3栏', value: 3 },
        { label: '4栏', value: 4 },
        { label: '5栏', value: 5 },
        { label: '6栏', value: 6 },
        { label: '7栏', value: 7 },
        { label: '8栏', value: 8 },
        { label: '9栏', value: 9 },
        { label: '10栏', value: 10 }
      ]
    }
  },
  watch: {
    context: function(val) {
      this.init()
    }
  },
  created() {
    this.init()
  },
  mounted: function() {

  },
  methods: {
    init() {
      const paper = this.context.reportDef.paper
      if(paper.pageEnabled === undefined) {
        paper.pageEnabled = false
      }
      if(paper.pagingMode === undefined) {
        paper.pagingMode = 'fitpage'
      }
      if(paper.fixRows === undefined) {
        paper.fixRows = 10
      }
      if (this.paper.columnEnabled) {
        this.columnEnabled = true
      }
      this.paper = paper
    },
    handleColumnMargin(val) {
      this.paper.columnMargin = mmToPoint(val)
    }
  }
}
</script>

<style>
</style>
