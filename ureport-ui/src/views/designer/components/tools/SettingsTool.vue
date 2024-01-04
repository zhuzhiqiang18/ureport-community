<template>
  <div class="component-icon-btn" @click="openSettings">
    <div class="component-icon-btn-icon"><i class="el-icon-setting" /></div>
    <el-dialog
      title="报表配置"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="640px"
    >
      <el-tabs v-model="activeName" stretch>
        <el-tab-pane label="页面配置" name="first">
          <PageSettings :context="context" />
        </el-tab-pane>
        <el-tab-pane label="页眉页脚" name="second">
          <HeaderAndFooterSetting :context="context" />
        </el-tab-pane>
        <el-tab-pane label="分页分栏" name="third">
          <PaginationSetting :context="context" />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import PageSettings from './setting/PageSetting'
import HeaderAndFooterSetting from './setting/HeaderAndFooterSetting'
import PaginationSetting from './setting/PaginationSetting'

import { undoManager, pointToMM, mmToPoint, buildPageSizeList } from '../Utils.js'

export default {
  name: 'SettingsTool',
  components: {
    PageSettings,
    HeaderAndFooterSetting,
    PaginationSetting
  },
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      activeName: 'first',
      dialogVisible: false
    }
  },
  mounted: function() {

  },
  methods: {
    openSettings() {
      this.dialogVisible = true
    }
  }
}
</script>

<style>
</style>
