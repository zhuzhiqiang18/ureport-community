<template>
  <div class="_header_footer_settings">
    <div style="color: #999999;">页眉页脚采用表达式方式定义，如："第$[PAGE]页共$[PAGES]页"</div>
    <fieldset style="margin-top: 10px;">
      <legend>页眉</legend>
      <el-form label-position="left" :model="header" size="small" label-width="80px">
        <el-form-item label="字体样式">
          <el-link type="primary" :underline="false" @click="handleStyle(1)">设置样式</el-link>
        </el-form-item>
        <el-form-item label="顶部距离">
          <el-input v-model="form.topMargin" oninput="value=value.replace(/[^\d]/g,'')" size="mini" @change="handleTopMargin" />
        </el-form-item>
        <el-form-item label="对齐方式">
          <el-select v-model="header.align" placeholder="请选择" style="width: 100%;">
            <el-option v-for="item in alignOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="文本内容">
          <el-input v-model="header.center" type="textarea" :rows="3" placeholder="" />
        </el-form-item>
      </el-form>
    </fieldset>
    <fieldset style="margin-top: 10px;">
      <legend>页脚</legend>
      <el-form label-position="left" :model="footer" size="mini" label-width="80px">
        <el-form-item label="字体样式">
          <el-link type="primary" :underline="false" @click="handleStyle(0)">设置样式</el-link>
        </el-form-item>
        <el-form-item label="底部距离">
          <el-input v-model="form.bottomMargin" oninput="value=value.replace(/[^\d]/g,'')" size="mini" @change="handleBottomMargin" />
        </el-form-item>
        <el-form-item label="对齐方式">
          <el-select v-model="footer.align" placeholder="请选择" style="width: 100%;">
            <el-option v-for="item in alignOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="文本内容">
          <el-input v-model="footer.center" type="textarea" :rows="3" placeholder="" />
        </el-form-item>
      </el-form>
    </fieldset>
    <el-dialog
      v-if="dialogVisible"
      title="字体样式配置"
      :visible.sync="dialogVisible"
      append-to-body
      :close-on-click-modal="false"
      width="340px"
    >
      <el-form v-model="form" label-width="60px" label-position="left" size="mini">
        <el-form-item label="字体">
          <el-select v-model="form.fontFamily" placeholder="请选择" style="width:100%">
            <el-option v-for="item in fontFamilyOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="颜色">
          <colorpicker v-model="form.forecolor" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="尺寸">
          <el-select v-model="form.fontSize" placeholder="请选择" style="width:100%">
            <el-option v-for="value in fontSizeOptions" :key="value" :label="value" :value="value" />
          </el-select>
        </el-form-item>
        <el-form-item label="加粗">
          <el-select v-model="form.bold" placeholder="请选择" style="width:100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="倾斜">
          <el-select v-model="form.italic" placeholder="请选择" style="width:100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="下划线">
          <el-select v-model="form.underline" placeholder="请选择" style="width:100%">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="dialogVisible=false">取 消</el-button>
        <el-button type="primary" size="mini" @click="submit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { pointToMM, mmToPoint } from '../../Utils.js'
import { fontFamilys, fontSizes } from '@/data/select-options'
export default {
  name: 'HeaderAndFooterSetting',
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      alignOptions: [
        {label:'居左对齐', value: 'left'},
        {label:'居中对齐', value: 'center'},
        {label:'居右对齐', value: 'right'}
      ],
      isHeader: true,
      dialogVisible: false,
      form: {
        topMargin: 11,
        bottomMargin: 11,
        fontFamily: '宋体',
        forecolor: 'rgb(0, 1, 0)',
        fontSize: 10,
        bold: false,
        italic: false,
        underline: false
      },
      header: {
        left: '',
        center: '',
        right: '',
        align: 'center',
      },
      footer: {
        left: '',
        center: '',
        right: '',
        align: 'center',
      },
      fontFamilyOptions: fontFamilys,
      fontSizeOptions: fontSizes,
      typeOptions: [
        { value: true, label: '是' },
        { value: false, label: '否' }
      ]
    }
  },
  watch: {
    context: function(val) {
      this.init()
    }
  },
  mounted: function() {
    this.init()
  },
  methods: {
    init() {
      let header = this.context.reportDef.header
      if (!header) {
        header = {
          align: 'center',
          left: '',
          center: '',
          right: ''
        }
        this.context.reportDef.header = header
      }
      if (header.margin) {
        this.form.topMargin = pointToMM(header.margin)
      }
      this.header = header
      let footer = this.context.reportDef.footer
      if (!footer) {
        footer = {
          align: 'center',
          left: '',
          center: '',
          right: ''
        }
        this.context.reportDef.footer = footer
      }
      if (footer.margin) {
        this.form.bottomMargin = pointToMM(footer.margin)
      }
      this.footer = footer
    },
    handleTopMargin(val) {
      this.header.margin = mmToPoint(val)
    },
    handleBottomMargin(val) {
      this.footer.margin = mmToPoint(val)
    },
    handleStyle(isHeader) {
      if (isHeader) {
        this.isHeader = true
        const header = this.header
        this.form.fontFamily = header.fontFamily || '宋体'
        this.form.forecolor = header.forecolor ? 'rgb(' + header.forecolor + ')' : 'rgb(0, 1, 0)'
        this.form.fontSize = header.fontSize || 10
        this.form.bold = !!header.bold
        this.form.italic = !!header.italic
        this.form.underline = !!header.underline
      } else {
        this.isHeader = false
        const footer = this.footer
        this.form.fontFamily = footer.fontFamily || '宋体'
        this.form.forecolor = footer.forecolor ? 'rgb(' + footer.forecolor + ')' : 'rgb(0, 1, 0)'
        this.form.fontSize = footer.fontSize || 10
        this.form.bold = !!footer.bold
        this.form.italic = !!footer.italic
        this.form.underline = !!footer.underline
      }
      this.dialogVisible = true
    },
    submit() {
      const form = this.form
      if (this.isHeader) {
        this.header.fontFamily = form.fontFamily
        this.header.forecolor = form.forecolor.replace('rgb(', '').replace(')', '')
        this.header.fontSize = form.fontSize
        this.header.bold = form.bold
        this.header.italic = form.italic
        this.header.underline = form.underline
      } else {
        this.footer.fontFamily = form.fontFamily
        this.footer.forecolor = form.forecolor.replace('rgb(', '').replace(')', '')
        this.footer.fontSize = form.fontSize
        this.footer.bold = form.bold
        this.footer.italic = form.italic
        this.footer.underline = form.underline
      }
      this.dialogVisible = false
    }
  }
}
</script>

<style>
</style>
