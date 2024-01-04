<template>
  <div class="el-color-picker-select el-select el-select--mini">
      <el-dropdown size="mini" @visible-change="handleVisible" style="width: 100%;" ref="colorPickerDropdown" trigger="click">
        <span class="el-dropdown-link">
          <div :class="visible ? 'el-input el-input--mini el-input--suffix' : 'el-input el-input--mini el-input--suffix is-focus'">
            <input type="text" readonly="readonly" autocomplete="off" class="el-input__inner" :placeholder="option==''?'请选择':''" :style="{backgroundColor:option}">
            <span class="el-input__suffix">
              <span class="el-input__suffix-inner">
                <i v-if="visible" class="el-select__caret el-select__caret el-input__icon el-icon-arrow-up" />
                <i v-else class="el-select__caret el-input__icon el-icon-arrow-up is-reverse" />
              </span>
            </span>
          </div>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item style="padding:0px;background:#fff">
            <div @click.stop="" style="cursor: default;padding:10px;padding-bottom: 0px;">
              <table>
                <tbody>
                  <tr v-for="(arr,i) in colors" :key="i">
                    <td v-for="(color,j) in arr" :key="j" @click="changeColor(color)">
                      <div class="x-spreadsheet-color-palette-cell" :style="{backgroundColor:color}" />
                    </td>
                  </tr>
                </tbody>
              </table>
              <div style="padding-bottom: 6px;margin-top: 5px;padding-top: 8px;border-top: 1px solid #dcdfe6;text-align: right;">
                <el-input v-model="custom" placeholder="rgb(0, 1, 0)" size="mini" style="width: 165px;"></el-input>
                <el-button @click="saveColor()" plain size="mini" style="margin-left: 10px;margin-right: 4px;">确 定</el-button>
              </div>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
  </div>
</template>
<script>

export default {
  model: {
	  prop: 'value',
	  event: 'change'
  },
  props: {
	  value: {
	    type: String,
	    default: () => {
	      return ''
	    }
	  }
  },
  watch: {
    value: function(val) {
      this.option = val
    }
  },
  data() {
	  return {
	    visible: false,
	    option: '',
      custom: '',
	    colors: [
	      ['rgb(255,255,255)','rgb(0,1,0)','rgb(231,229,230)','rgb(68,85,105)','rgb(91,156,214)','rgb(237,125,49)','rgb(165,165,165)','rgb(255,192,1)','rgb(67,113,198)','rgb(113,174,71)'],
	      ['rgb(242,242,242)','rgb(127,127,127)','rgb(208,206,207)','rgb(213,220,228)','rgb(222,234,246)','rgb(252,229,213)','rgb(237,237,237)','rgb(255,242,205)','rgb(217,226,243)','rgb(227,239,217)'],
	      ['rgb(216,216,216)','rgb(89,89,89)','rgb(175,171,172)','rgb(173,184,202)','rgb(189,215,238)','rgb(247,204,172)','rgb(219,219,219)','rgb(255,229,154)','rgb(179,198,231)','rgb(197,224,179)'],
	      ['rgb(191,191,191)','rgb(63,63,63)','rgb(117,111,111)','rgb(133,150,176)','rgb(156,194,230)','rgb(244,177,132)','rgb(201,201,201)','rgb(254,217,100)','rgb(142,170,218)','rgb(167,208,140)'],
	      ['rgb(165,165,165)','rgb(38,38,38)','rgb(58,56,57)','rgb(51,63,79)','rgb(46,117,181)','rgb(196,90,16)','rgb(123,123,123)','rgb(191,142,1)','rgb(47,85,150)','rgb(83,129,54)'],
	      ['rgb(127,127,127)','rgb(12,12,12)','rgb(23,21,22)','rgb(34,42,53)','rgb(31,78,122)','rgb(132,60,10)','rgb(82,82,82)','rgb(126,96,0)','rgb(32,56,100)','rgb(54,86,36)'],
	      ['rgb(192,0,0)','rgb(254,0,0)','rgb(253,193,1)','rgb(255,255,1)','rgb(147,208,81)','rgb(0,176,78)','rgb(1,176,241)','rgb(1,112,193)','rgb(1,32,96)','rgb(112,48,160)']
	    ]
	  }
  },
  mounted() {
	  this.option = this.value
  },
  methods: {
    saveColor() {
      if(!this.custom) {
        this.$message('请输入颜色值');
        return
      }
      const custom = this.custom.toLocaleLowerCase().trim().replace(/' '/,'')
      let valid = true
      valid = valid && custom.startsWith('rgb(') && custom.endsWith(")")
      const arr = custom.replace("rgb(",'').replace(')','').split(',')
      valid = valid && arr.length === 3
      for(const val of arr) {
        valid = valid && this.checkColorValue(val.trim())
      }
      if(!valid) {
        this.$message('颜色值不合法');
        return
      }
      const color = `rgb(${parseInt(arr[0].trim())},${parseInt(arr[1].trim())},${parseInt(arr[2].trim())})`
      this.changeColor(color)
    },
    checkColorValue(val) {
      return !isNaN(val) && parseInt(val) <= 255 && parseInt(val) >= 0
    },
    handleVisible(visible) {
      this.visible = visible
      if(visible) {
        this.custom = this.option
      }
    },
	  changeColor(color) {
	    this.option = color
	    this.$emit('change', color)
      this.$refs.colorPickerDropdown.hide()
	  }
  }
}

</script>
