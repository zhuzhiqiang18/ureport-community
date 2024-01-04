<template>
  <div v-loading="loading" class="report-show-contain" style="display: flex;flex-direction: column;">
    <div class="form-header-div">
      <el-form v-if="form.items.length > 0" :inline="true" size="small" style="margin-top: 10px;margin-left: 10px;">
            <el-form-item v-for="item in form.items" :label="item.label">
              <el-date-picker
                v-if="item.type==='datetime'"
                v-model="item.value"
                style="width: 190px;"
                :type="item.format"
                value-format="yyyy-MM-dd HH:mm:ss"
                :clearable="false"
                :editable="false"
                placeholder="请选择"
                @change="handleChange($event,item)"
              >
              </el-date-picker>
              <el-radio-group
                v-if="item.type==='radio'"
                v-model="item.value"
                @change="handleChange($event,item)"
              >
                <el-radio v-for="(obj,index) in item.options" :label="obj.value">{{ obj.label }}</el-radio>
              </el-radio-group>
              <el-checkbox-group
                v-if="item.type==='checkbox'"
                v-model="item.value"
                @change="handleChange($event,item)"
              >
                <el-checkbox v-for="(obj,index) in item.options" :label="obj.value">{{ obj.label }}</el-checkbox>
              </el-checkbox-group>
              <el-input
                v-if="item.type==='text'"
                v-model="item.value"
                placeholder="请输入内容"
                @change="handleChange($event,item)"
              >
              </el-input>
              <el-select
                v-if="item.type==='select'"
                v-model="item.value"
                placeholder="请选择"
                filterable
                style="width: 180px;"
                @change="handleChange($event,item)"
              >
                  <el-option
                    v-for="obj in item.options"
                    :key="obj.value"
                    :label="obj.label"
                    :value="obj.value"
                  />
            </el-select>
            <el-select
                v-if="item.type==='multiple-select'"
                v-model="item.value"
                placeholder="请选择"
                filterable
                style="width: 240px;"
                multiple
                collapse-tags
                @change="handleChange($event,item)"
              >
                  <el-option
                    v-for="obj in item.options"
                    :key="obj.value"
                    :label="obj.label"
                    :value="obj.value"
                  />
            </el-select>
        </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width: 60px;" @click="search()">查 询</el-button>
            <el-button style="width: 60px;" @click="reset()">重 置</el-button>
          </el-form-item>
      </el-form>
      <div class="tool-bars">
        <div style="position: relative;height: 24px;">
            <div class="tool-img-block" title="在线打印" @click="printPreview()">
              <img src="@/assets/icons/print.svg" />
            </div>
            <div class="tool-img-block" title="导出PDF" @click="download('pdf')">
              <img src="@/assets/icons/pdf.svg" />
            </div>
            <div class="tool-img-block" title="导出Word" @click="download('word')">
              <img src="@/assets/icons/word.svg" />
            </div>
            <div class="tool-img-block" title="导出Excel" @click="download('excel')">
              <img src="@/assets/icons/excel.svg" />
            </div>
            <template v-if="totalPage > 0">
              <el-link :underline="false" :disabled="pageIndex <= 1" style="margin-left: 10px;" @click="first()">首页</el-link>
              <el-link :underline="false" :disabled="pageIndex <= 1" style="margin-left: 10px;" @click="pre()">上一页</el-link>
              <font style="color: #606266;font-size: 14px;font-weight: 500;margin-left: 10px;">{{pageIndex}}&nbsp;&nbsp;/&nbsp;&nbsp;{{totalPage}}</font>
              <el-link :underline="false" :disabled="pageIndex >= totalPage" style="margin-left: 10px;" @click="next()">下一页</el-link>
              <el-link :underline="false" :disabled="pageIndex >= totalPage" style="margin-left: 10px;" @click="last()">末页</el-link>
            </template>
        </div>
      </div>
    </div>
    <div style="flex: 1;overflow: hidden;position: relative;">
      <div id="headerPanel" style="display: none;padding: 0px 10px;padding-top: 10px;width: 100%;overflow: hidden;position: absolute;background: #ffffff;"></div>
      <div id="leftPanel" style="display: none;padding: 10px;padding-right: 0px;height: 100%;overflow: hidden;position: absolute;background: #ffffff;"></div>
      <div id="fixedPanel" style="display: none;padding-left: 10px;padding-top: 10px;position: absolute;background: #ffffff;"></div>
      <div id="centerPanel" class="report-ureport-box-card" v-on:scroll="handleScroll()">
        <div v-html="'<style>' + style + '</style>' + html" :style="{float:align,background: bgImage}"></div>
      </div>
    </div>
    <el-dialog
      title="预览"
      v-if="iframe"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="960px"
      class="cell-iframe-dialog"
    >
      <div style="width: 100%;height: 600px;">
        <iframe :src="iframe" frameborder="0" style="width: 100%;height: 100%;"></iframe>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { loadData, refreshSearchForm, downloadWord, downloadExcel, downloadPDF, print } from '@/api/report'
import * as echarts from 'echarts'
import * as chart from './components/widget/Chart'
import FreezeCell from './components/FreezeCell'

export default {
  name: 'Preview',
  data() {
    return {
      token: '',
      loading: false,
      reportName: '',
      style: '',
      bgImage: '',
      content: '',
      html: '',
      query: {},
      pageIndex: 1,
      totalPage: 0,
      chartDatas: [],
      form: {
        items: []
      },
      formParameters: {},
      align: 'center',
      dialogVisible: false,
      iframe: '',
    }
  },
  created(){
  	const _this = this
  	window.iframeWindow = _this.iframeWindow
  },
  mounted() {
    let params = this.$route.params
    if(params.reportName) {
      const query = this.$route.query || {}
      for(const key in query) {
        query[key] = decodeURIComponent(query[key])
      }
      this.query = query
      this.token = this.$route.query['X-Token']
      this.reportName = params.reportName
    } else {
      params = JSON.parse(window.localStorage.getItem('reportParameters'))
      this.content = params.content || ''
    }
    this.search()
  },
  methods: {
    reset() {
      self.location.reload()
    },
    search() {
      this.loading = true
      const formParameters = this.getForm()
      this.formParameters = formParameters
      const param = {
        pageIndex: this.pageIndex,
        params: {
          reportName: this.reportName,
          content: this.content,
          query: formParameters
        }
      }
      loadData(param,this.token).then(response => {
        const data = response.data
        if(this.html === '') { // 第一次加载
          const form = data.searchFormData || {items: []}
          // 后台返回时间为字符串时间需要重新转换为时间对象，否则element时间组件切换会报错
          this.initDateParam(form)
          this.form = form
          this.align = data.reportAlign
          this.bgImage = data.bgImage ? 'url(' + data.bgImage + ') no-repeat' : ''
        }
        this.chartDatas = []
        this.html = data.content
        this.style = data.style
        this.totalPage = data.totalPage || 0
        this.$nextTick(function(){
          const freeze = data.freeze
          // 冻结行列
          if(freeze && this.align === 'left' && !data.column) {
            new FreezeCell({
              header: 'headerPanel',
              center: 'centerPanel',
              left: 'leftPanel',
              fixed: 'fixedPanel',
            },freeze)
            this.handleScroll()
          }
          // 加载图表
          const chartDatas = data.chartDatas || []
          for(const chartData of chartDatas) {
            const ele = window.document.getElementById(chartData.id)
            if(ele) {
              const config = JSON.parse(chartData.json)
              const option = chart.builder(config)
              const myChart = echarts.init(ele)
              this.chartDatas.push({
                id: chartData.id,
                myChart: myChart
              })
              myChart.setOption(option)
            }
          }
        })
        this.loading = false
      })
    },
    handleScroll() {
      const centerPanel = document.getElementById("centerPanel")
      document.getElementById("headerPanel").scrollLeft = centerPanel.scrollLeft
      document.getElementById("leftPanel").scrollTop = centerPanel.scrollTop
    },
    first() {
      this.pageIndex = 1
      this.search()
    },
    pre() {
      if(this.pageIndex > 1) {
        this.pageIndex--
        this.search()
      }
    },
    last() {
      this.pageIndex = this.totalPage
      this.search()
    },
    next() {
      if(this.pageIndex < this.totalPage) {
        this.pageIndex++
        this.search()
      }
    },
    getChartImages() {
      const chartImages = {}
      const chartDatas = this.chartDatas || []
      for (var i = 0; i < chartDatas.length; i++) {
        const chartData = chartDatas[i]
        const itemId = chartData.id
        chartImages[itemId] = chartData.myChart.getDataURL({
          type: 'png',
          pixelRatio: 1,
          backgroundColor: '#fff',
          excludeComponents: 'toolbox'
        })
      }
      return chartImages
    },
    async printPreview() {
      this.loading = true
      const param = {
        reportName: this.reportName,
        content: this.content,
        query: this.formParameters,
        chartImages: this.getChartImages()
      }
      await print(param,this.token)
      this.loading = false
    },
    async download(type) {
      this.loading = true
      const param = {
        reportName: this.reportName,
        content: this.content,
        query: this.formParameters
      }
      const upperType = type.toUpperCase()
      if (upperType === 'EXCEL') {
        await downloadExcel(param,this.token)
      } else if (upperType === 'PDF') {
        param.chartImages = this.getChartImages()
        await downloadPDF(param,this.token)
      } else if (upperType === 'WORD') {
        await downloadWord(param,this.token)
      }
      this.loading = false
    },
    handleChange: function(val, item) {
      var param = {
        item: item,
        query: this.getForm(),
        reportName: this.reportName,
        content: this.content
      }
      refreshSearchForm(param,this.token).then((res) => {
        const data = res.data || {}
        const items = this.form.items || []
        for(item of items) {
          const options = data[item.uuid]
          if(options) {
            item.options = options
          }
        }
      })
    },
    handleContent() {
      const html = '<style>' + this.report.style + '</style>' + this.report.content
      return html
    },
    iframeWindow(element) {
      var url = element.getAttribute("url")
      if(url.indexOf("?") > -1) {
        url = url + "&X-Token=" + this.token
      } else {
        url = url + "?X-Token=" + this.token
      }
      this.iframe = url
      this.dialogVisible = true
    },
    initDateParam(form) {
      const items = form.items
      for (const k in items) {
        const item = items[k]
        if (item.type === 'datetime' && item.value) {
          item.value = new Date(item.value)
        }
      }
    },
    getForm() {
      const parameters = JSON.parse(JSON.stringify(this.query))
      const items = this.form.items || []
      for (const k in items) {
        const item = items[k]
        // 时间过滤组件需要格式化
        if (item.type === 'datetime') {
          if(item.defaultValue === '0') {
            parameters[item.bindParameter] = "'" + this.startDateFormat(item) + "'"
          } else if(item.defaultValue === '1') {
            parameters[item.bindParameter] = "'" + this.endDateFormat(item) + "'"
          }
        } else {
          parameters[item.bindParameter] = item.value
        }
      }
      return parameters
    },
    startDateFormat(item) {
      const value = this.dateFormat(item.value)
      if (item.format === 'year') {
        return value.substring(0, 4) + '-01-01 00:00:00'
      }
      if (item.format === 'month') {
        return value.substring(0, 8) + '01 00:00:00'
      } else if (item.format === 'date') {
        return value.substring(0, 10) + ' 00:00:00'
      }
      return value
    },
    endDateFormat(item) {
      const value = this.dateFormat(item.value)
      if (item.format === 'year') {
        return value.substring(0, 4) + '-12-31 23:59:59'
      }
      if (item.format === 'month') {
        const date = new Date(item.value)
        const d = new Date(date.getFullYear(), date.getMonth() + 1, 0)
        return value.substring(0, 8) + d.getDate() + ' 23:59:59'
      }
      if (item.format === 'date') {
        return value.substring(0, 10) + ' 23:59:59'
      }
      return value
    },
    dateFormat(obj) {
      const date = new Date(obj)
      const yyyy = date.getFullYear()
      let MM = date.getMonth() + 1
      MM = MM < 10 ? '0' + MM : MM
      let dd = date.getDate()
      dd = dd < 10 ? '0' + dd : dd
      let HH = date.getHours()
      HH = HH < 10 ? '0' + HH : HH
      let mm = date.getMinutes()
      mm = mm < 10 ? '0' + mm : mm
      let ss = date.getSeconds()
      ss = ss < 10 ? '0' + ss : ss
      return yyyy + '-' + MM + '-' + dd + ' ' + HH + ':' + mm + ':' + ss
    }
  }
}
</script>

<style>
  .report-show-contain {
    width: 100%;
    height: 100%;
    position: absolute;
    overflow: hidden;
  }
  .report-ureport-box-card{
    height: 100%;
    padding: 10px;
    overflow: auto;
  }

  .charts-item-content {
    width:100%;
    height:100%;
    overflow: hidden;
    position: static !important;
  }

  .tool-bars img {
    vertical-align: middle;
    width: 20px;
    height: 20px;
    margin: 2px;
  }

  .form-header-div .el-select__tags-text {
    display: inline-block;
    max-width: 100px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .form-header-div .el-select .el-tag__close.el-icon-close {
    top: -5px;
  }

  .form-header-div .el-form-item--small.el-form-item {
    margin-bottom: 10px;
  }

  .tool-img-block {
    cursor: pointer;
    width: 24px;
    height:24px;
    margin-right: 5px;
    display: inline-block;
  }
  .tool-img-block:hover {
    background: #E4E7ED;
  }

  .cell-iframe-dialog .el-dialog__body {
    padding: 0px;
  }
</style>
