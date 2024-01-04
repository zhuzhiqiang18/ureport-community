<template>
  <el-container style="position: absolute;width: 100%;height: 100%;">
    <el-header style="height: 38px;">
      <div class="tool-bars">
        <div style="position: relative;height: 24px;">
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">预览报表</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">数据集</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">图表组件</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 80px;line-height: 18px;text-align: center;">Tabs标签页</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">Card卡片</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">保存</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">另存为</div>
          </div>
          <div class="component-icon-btn">
            <div class="component-icon-btn-icon" style="font-size: 12px;width: 60px;line-height: 18px;text-align: center;">表单设计</div>
          </div>
        </div>
      </div>
    </el-header>
    <el-main style="position: relative;">
      <div id="chartcontainer" style="overflow: auto;position: absolute;background: #f0f2f5;width: 100%;height: 100%;padding: 4px;box-sizing: border-box;">
        <grid-layout
          :layout.sync="layout"
          :col-num="24"
          :row-height="height"
          :is-draggable="true"
          :is-resizable="true"
          :is-mirrored="false"
          :vertical-compact="true"
          :margin="[0, 0]"
          :use-css-transforms="true"
        >

          <grid-item
            v-for="item in layout"
            :key="item.i"
            :x="item.x"
            :y="item.y"
            :w="item.w"
            :h="item.h"
            :i="item.i"
          >
            <div style="width: 100%;height: 100%;background: #fff;">
              <el-card class="box-card">
                <div slot="header" class="clearfix">
                  <span>卡片名称</span>
                  <el-button style="float: right; padding: 3px 0" type="text">操作按钮</el-button>
                </div>
                <div v-for="o in 4" :key="o" class="text item">
                  {{ '列表内容 ' + o }}
                </div>
              </el-card>
            </div>
          </grid-item>
        </grid-layout>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import VueGridLayout from 'vue-grid-layout'
export default {
  components: {
    GridLayout: VueGridLayout.GridLayout,
    GridItem: VueGridLayout.GridItem
  },
  data() {
    return {
      height: 0,
      layout: [
        { 'x': 0, 'y': 0, 'w': 6, 'h': 4, 'i': '0' },
        { 'x': 6, 'y': 0, 'w': 6, 'h': 4, 'i': '10' }
      ],
      activeName: 'second',
      tableData: [{
        date: '2016-05-02',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1518 弄'
      }, {
        date: '2016-05-04',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1517 弄'
      }, {
        date: '2016-05-01',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1519 弄'
      }, {
        date: '2016-05-03',
        name: '王小虎',
        address: '上海市普陀区金沙江路 1516 弄'
      }]

    }
  },
  mounted: function() {
    const ele = document.getElementById('chartcontainer')
    this.height = (this.getHeight(ele) - 8) / 18
  },
  methods: {
    getHeight(ele) {
      const height = document.documentElement.clientHeight
      const top = ele.getBoundingClientRect().top
      return height - top
    }
  }
}
</script>

<style>
  .vue-grid-item {
    padding: 4px;
    box-sizing: border-box;
  }

  .vue-grid-item.vue-grid-placeholder {
    background: #3759a7;
  }

  .el-row {
      margin-bottom: 20px;
      &:last-child {
        margin-bottom: 0;
      }
    }
    .el-col {
      border-radius: 4px;
    }
    .bg-purple-dark {
      background: #99a9bf;
    }
    .bg-purple {
      background: #d3dce6;
    }
    .bg-purple-light {
      background: #e5e9f2;
    }
    .grid-content {
      border-radius: 4px;
      min-height: 36px;
    }
    .row-bg {
      padding: 10px 0;
      background-color: #f9fafc;
    }

      .tool-bars {
        position: relative;
        padding: 6px 4px;
        overflow: hidden;
        background: #f2f4f7;
        border-top: 1px solid #e2e6ed;
        border-bottom: 1px solid #e2e6ed;
      }

      .component-icon-btn {
        float: left;
        border-radius: 2px;
        margin: 0px 4px;
        cursor: pointer;
      }

      .component-dropdown-group {
        position: relative;
      }

      .component-icon-btn:hover {
        background: #DCDFE6;
      }

      .component-dropdown-btn:hover,
      .component-dropdown-group:hover {
        background: #C0C4CC;
      }

      .component-icon-btn.selfdefine {
        background: #fff;
      }

      .component-icon-btn.selfdefine:hover {
        background: #DCDFE6;
      }

      .component-icon-btn-icon {
        width: 24px;
        height: 24px;
        padding: 4px;
        color: #606266;
      }

      .right-btn {
        position: absolute;
        top: 11px;
        left: 2px;
        width: 0;
        height: 0;
        border-color: #767c85 transparent transparent;
        border-style: solid solid none;
        border-width: 3px 3px 0;
        transition: transform .3s;
      }

      .component-icon-btn-color {
        position: absolute;
        top: 18px;
        left: 5px;
        width: 15px;
        height: 2px;
      }

      .tool-bars .component-icon-btn svg,
      .tool-bars .component-icon-btn i {
        position: absolute;
      }

      .el-tabs__nav-wrap::after {
        height: 1px;
      }
</style>
