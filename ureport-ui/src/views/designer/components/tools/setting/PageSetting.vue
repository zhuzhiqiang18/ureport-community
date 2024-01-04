<template>
  <div>
    <fieldset style="margin-top: 10px;">
      <legend>纸张大小</legend>
      <el-form :inline="true" label-position="left" label-width="48px" :model="paper" size="mini">
        <el-form-item label="尺寸:">
          <el-select v-model="paper.paperType" @change="handlePaperTypeChange" style="width: 180px;">
            <el-option
              v-for="(item,index) in paperTypeOptions"
              :key="index+1"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
        <br/>
        <el-form-item label="宽度:" style="margin-bottom: 0px;">
          <el-input v-model="location.width" oninput="value=value.replace(/[^\d]/g,'')" style="width: 180px;"  @change="handlePaperSizeChange" :disabled="paper.paperType!=='CUSTOM'" />
          毫米
        </el-form-item>
        <el-form-item label="高度:" >
          <el-input v-model="location.height" oninput="value=value.replace(/[^\d]/g,'')" style="width: 180px;" @change="handlePaperSizeChange" :disabled="paper.paperType!=='CUSTOM'" />
          毫米
        </el-form-item>
      </el-form>
    </fieldset>
    <fieldset style="margin-top: 10px;">
      <legend>页边距</legend>
      <el-form :inline="true" label-position="left" label-width="48px" :model="paper" size="mini">
        <el-form-item label="左:">
          <el-input v-model="location.leftMargin" oninput="value=value.replace(/[^\d]/g,'')" style="width: 180px;" @change="handlemLeftMargin" />
          毫米
        </el-form-item>
        <el-form-item label="右:" >
          <el-input v-model="location.rightMargin" oninput="value=value.replace(/[^\d]/g,'')" style="width: 180px;" @change="handlemRightMargin" />
          毫米
        </el-form-item>
        <el-form-item label="上:" style="margin-bottom: 0px;">
          <el-input v-model="location.topMargin" oninput="value=value.replace(/[^\d]/g,'')" style="width: 180px;" @change="handlemTopMargin" />
          毫米
        </el-form-item>
        <el-form-item label="下:" >
          <el-input v-model="location.bottomMargin" oninput="value=value.replace(/[^\d]/g,'')" style="width: 180px;" @change="handlemBottomMargin" />
          毫米
        </el-form-item>
      </el-form>
    </fieldset>
    <fieldset style="margin-top: 10px;">
      <legend>方向</legend>
      <el-form label-position="left" label-width="100px" :model="paper" size="mini">
        <el-form-item label="展示方向:">
          <el-select v-model="paper.orientation" @change="handleOrientationChange">
            <el-option
              v-for="(item,index) in orientationOptions"
              :key="index+1"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </fieldset>
    <fieldset style="margin-top: 10px;">
      <legend>Web预览页面</legend>
      <el-form label-position="left" label-width="100px" :model="paper" size="mini">
        <el-form-item label="对齐方式:">
          <el-select v-model="paper.htmlReportAlign">
            <el-option
              v-for="(item,index) in htmlReportAlignOptions"
              :key="index+1"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="套打背景图:" label-width="100px">
          <el-input v-model="paper.bgImage" style="width: 480px" @change="handleChange" />
        </el-form-item>
        <el-form-item label="导出背景图:" label-width="100px">
          <el-checkbox v-model="paper.print" />
        </el-form-item>
        <el-form-item label="本地图片:" label-width="100px">
          <el-button plain @click="handleImage">选择本地图片</el-button>
        </el-form-item>
      </el-form>
    </fieldset>
    <el-dialog
      title="选择本地图片"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="450px"
    >
    <div style="height: 400px;overflow: auto;">
      <el-image class="report-image" v-for="url in urls" :key="url" :src="url" lazy @click="handleSaveImage(url)"></el-image>
    </div>
    </el-dialog>
  </div>
</template>

<script>
import { undoManager, pointToMM, mmToPoint, buildPageSizeList } from '../../Utils.js'
import { getImages } from '@/api/designer'

export default {
  name: 'PageSettings',
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      dialogVisible: false,
      urls: [],
      paper: {
        paperType: 'A4'
      },
      location: {
        width: 0,
        height: 0,
        leftMargin: 0,
        rightMargin: 0,
        topMargin: 0,
        bottomMargin: 0
      },
      paperSizeList: [],
      paperTypeOptions: [
        { label: 'A3', value: 'A3' },
        { label: 'A4', value: 'A4' },
        { label: 'A5', value: 'A5' },
        { label: 'A6', value: 'A6' },
        { label: 'B3', value: 'B3' },
        { label: 'B4', value: 'B4' },
        { label: 'B5', value: 'B5' },
        { label: 'B6', value: 'B6' },
        { label: '自定义', value: 'CUSTOM' }
      ],
      orientationOptions: [
        { label: '纵向', value: 'portrait' },
        { label: '横向', value: 'landscape' }
      ],
      htmlReportAlignOptions: [
        { label: '居左', value: 'left' },
        { label: '居中', value: 'center' },
        { label: '居右', value: 'right' }
      ]
    }
  },
  watch: {
    context: function(val) {
      this.init()
    }
  },
  mounted: function() {
    this.paperSizeList = buildPageSizeList()
    this.init()
  },
  methods: {
    init() {
      this.paper = this.context.reportDef.paper
      if (this.paper.paperType !== 'CUSTOM') {
        const pageSize = this.paperSizeList[this.paper.paperType]
        this.location.width = pageSize.width
        this.location.height = pageSize.height
      } else {
        this.location.width = pointToMM(this.paper.width)
        this.location.height = pointToMM(this.paper.height)
      }
      this.location.leftMargin = pointToMM(this.paper.leftMargin)
      this.location.rightMargin = pointToMM(this.paper.rightMargin)
      this.location.topMargin = pointToMM(this.paper.topMargin)
      this.location.bottomMargin = pointToMM(this.paper.bottomMargin)
    },
    handlePaperTypeChange(value) {
      if (value !== 'CUSTOM') {
        const pageSize = this.paperSizeList[value]
        this.paper.width = mmToPoint(pageSize.width)
        this.paper.height = mmToPoint(pageSize.height)
        this.location.width = pageSize.width
        this.location.height = pageSize.height
        this.context.printLine.refresh()
      }
    },
    handlePaperSizeChange(value) {
      this.paper.width = mmToPoint(this.location.width)
      this.paper.height = mmToPoint(this.location.height)
      this.context.printLine.refresh()
    },
    handleOrientationChange(value) {
      this.context.printLine.refresh()
    },
    handlemLeftMargin(val) {
      this.paper.leftMargin = mmToPoint(val)
    },
    handlemRightMargin(val) {
      this.paper.rightMargin = mmToPoint(val)
    },
    handlemTopMargin(val) {
      this.paper.topMargin = mmToPoint(val)
    },
    handlemBottomMargin(val) {
      this.paper.bottomMargin = mmToPoint(val)
    },
    handleImage() {
      getImages({}).then(response => {
        const report = response.data || []
        const urls = []
        for (const url of report) {
          urls.push(url)
        }
        this.urls = urls
        this.dialogVisible = true
      })
    },
    handleSaveImage(url) {
      this.paper.bgImage = url
      this.setBackGroundImage(url)
      this.dialogVisible = false
    },
    setBackGroundImage(url) {
      const ele = document.getElementById('designer')
      const master = ele.children[0]
      const wtHolder = master.children[0]
      const wtHider = wtHolder.children[0]
      if (url) {
        wtHider.style.background = 'url(' + url + ') 52px 27px no-repeat'
      } else {
        wtHider.style.background = 'transparent'
      }
    },
    handleChange(val) {
      this.setBackGroundImage(val)
    }
  }
}
</script>

<style>
</style>
