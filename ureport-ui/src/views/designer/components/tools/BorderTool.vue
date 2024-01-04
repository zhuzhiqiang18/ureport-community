<template>
  <div class="component-icon-btn" style="height: 24px;">
    <div class="component-dropdown-group component-icon-btn-icon" style="float: left;" @click="clickBorder" v-html="icons[borderType]" />
    <el-dropdown trigger="click" @visible-change="handleVisibleChange" ref="borderDropdownMenu">
      <span class="el-dropdown-link">
        <div class="component-dropdown-btn" style="width: 10px;height: 24px;float: left;position: relative;">
          <div class="right-btn" />
        </div>
      </span>
      <el-dropdown-menu slot="dropdown">
        <div style="padding: 0px 5px;width: 192px;height: 100px;background: #fff;">
          <div :class="borderType==='none'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('none')">
            <div class="dropdown-menu-icon" v-html="icons.none" />
          </div>
          <div :class="borderType==='top'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('top')">
            <div class="dropdown-menu-icon" v-html="icons.top" />
          </div>
          <div :class="borderType==='right'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('right')">
            <div class="dropdown-menu-icon" v-html="icons.right" />
          </div>
          <div :class="borderType==='bottom'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('bottom')">
            <div class="dropdown-menu-icon" v-html="icons.bottom" />
          </div>
          <div :class="borderType==='left'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('left')">
            <div class="dropdown-menu-icon" v-html="icons.left" />
          </div>
          <div :class="borderType==='full'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('full')">
            <div class="dropdown-menu-icon" v-html="icons.full" />
          </div>
          <div :class="borderType==='outline'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('outline')">
            <div class="dropdown-menu-icon" v-html="icons.outline" />
          </div>

          <div :class="borderType==='inline'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('inline')">
            <div class="dropdown-menu-icon" v-html="icons.inline" />
          </div>

          <div :class="borderType==='inlineVertical'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('inlineVertical')">
            <div class="dropdown-menu-icon" v-html="icons.inlineVertical" />
          </div>

          <div :class="borderType==='inlineHorizontal'?'dropdown-menu-item active':'dropdown-menu-item'" @click="changeBorder('inlineHorizontal')">
            <div class="dropdown-menu-icon" v-html="icons.inlineHorizontal" />
          </div>
          <div style="width: 100%;margin: 10px 0px;border-top: 1px solid #dcdfe6;" />

          <div class="component-icon-btn" style="height: 24px;">
            <div @click="handleColorDropdownMenu">
              <span class="el-dropdown-link">
                <div class="component-icon-btn-icon" style="float: left;">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><path d="M10.485 1.1L11.9 2.516a1 1 0 010 1.414l-6.778 6.778a1 1 0 01-.707.293H2V8.586a1 1 0 01.293-.707L9.07 1.1a1 1 0 011.414 0zM4.415 10l6.777-6.778-1.414-1.414L3 8.586V10h1.414z" fill="#3D4757"/></svg>
                  <div class="component-icon-btn-color" :style="{backgroundColor:borderColor,top:'120px',left: '14px'}"></div>
                </div>
                <div style="width: 10px;height: 24px;float: left;position: relative;">
                  <div class="right-btn"></div>
                </div>
              </span>
              <transition name="el-zoom-in-top">
                <div @click.stop="" v-if="colorDropdownMenu" class="color_dropdown-menu" style="cursor: default;">
                  <table>
                    <tbody>
                      <tr v-for="(arr,i) in colors" :key="i">
                        <td v-for="(color,j) in arr" :key="j" @click="changeColor(color)">
                          <div class="x-spreadsheet-color-palette-cell" :style="{backgroundColor:color}" />
                        </td>
                      </tr>
                    </tbody>
                  </table>
                  <div style="margin-top: 5px;padding-top: 8px;border-top: 1px solid #dcdfe6;text-align: right;">
                    <el-input v-model="custom" placeholder="rgb(0, 1, 0)" size="mini" style="width: 166px;"></el-input>
                    <el-button @click="saveColor()" plain size="mini" style="margin-left: 10px;margin-right: 4px;">确 定</el-button>
                  </div>
                </div>
               </transition>
            </div>
          </div>

          <div class="component-icon-btn" style="height: 26px;border: 1px solid #dcdfe6;">
            <div @click="handleBorderLineDropdownMenu">
              <span class="el-dropdown-link">
                <div class="component-icon-btn-icon" style="float: left;width: 110px;padding-top: 12px;">
                  <div :class="borderClass" />
                </div>
                <div style="width: 10px;height: 24px;float: left;position: relative;">
                  <div class="right-btn"></div>
                </div>
              </span>
              <transition name="el-zoom-in-top">
                <div v-if="borderLineDropdownMenu" class="color_dropdown-menu" style="left:40px">
                    <div :class="borderStyle===1?'border-style active':'border-style'" @click="handleBorderStyle(1)">
                      <div style="border-top: 1px solid #606266;" />
                    </div>
                    <div :class="borderStyle===2?'border-style active':'border-style'" @click="handleBorderStyle(2)">
                      <div style="border-top: 2px solid #606266;" />
                    </div>
                    <div :class="borderStyle===3?'border-style active':'border-style'" @click="handleBorderStyle(3)">
                      <div style="border-top: 3px solid #606266;" />
                    </div>
                    <div :class="borderStyle===4?'border-style active':'border-style'" @click="handleBorderStyle(4)">
                      <div style="border-top: 1px dashed #606266;" />
                    </div>
                    <div :class="borderStyle===5?'border-style active':'border-style'" @click="handleBorderStyle(5)">
                      <div style="border-top: 2px dashed #606266;" />
                    </div>
                    <div :class="borderStyle===6?'border-style active':'border-style'" @click="handleBorderStyle(6)">
                      <div style="border-top: 3px dashed #606266;" />
                    </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import { undoManager } from '../Utils.js'

export default {
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      icons: {
        inlineHorizontal: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><path d="M4 1v1H2v2H1V2a1 1 0 011-1h2zm1 0h2v1H5V1zm3 0h2v1H8V1zm3 0h2a1 1 0 011 1v2h-1V2h-2V1zm3 4v2h-1V5h1zm0 3v2h-1V8h1zm0 3v2a1 1 0 01-1 1h-2v-1h2v-2h1zm-4 3H8v-1h2v1zm-3 0H5v-1h2v1zm-3 0H2a1 1 0 01-1-1v-2h1v2h2v1zm-3-4V8h1v2H1zm0-3V5h1v2H1zm6-5h1v2H7zm0 3h1v2H7zm0 3h1v2H7zm0 3h1v2H7zM2 8V7h2v1zm9 0V7h2v1zM5 8V7h2v1zm3 0V7h2v1z" fill="#909AA9" fill-rule="evenodd"></path><path d="M2 7h5V2H2v2zm0 1v6h6V8H6zm11-1V2H7v5h5zm0 1H8v5h5V8zM2 2h11a1 1 0 010 0v11a1 0 1 01-10 1H3a0 1 0 01-1-0V2a0 1 0 011-1z" fill="#3D4757" fill-rule="evenodd"/></svg>',
        inlineVertical: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><path d="M4 1v1H2v2H1V2a1 1 0 011-1h2zm1 0h2v1H5V1zm3 0h2v1H8V1zm3 0h2a1 1 0 011 1v2h-1V2h-2V1zm3 4v2h-1V5h1zm0 3v2h-1V8h1zm0 3v2a1 1 0 01-1 1h-2v-1h2v-2h1zm-4 3H8v-1h2v1zm-3 0H5v-1h2v1zm-3 0H2a1 1 0 01-1-1v-2h1v2h2v1zm-3-4V8h1v2H1zm0-3V5h1v2H1zm6-5h1v2H7zm0 3h1v2H7zm0 3h1v2H7zm0 3h1v2H7zM2 8V7h2v1zm9 0V7h2v1zM5 8V7h2v1zm3 0V7h2v1z" fill="#909AA9" fill-rule="evenodd"></path><path d="M2 7h5V2H2v3zm0 1v6h5V7H2zm11-1V2H8v6h5zm0 1H8v5h5V5zM2 2h11a9 2 0 010 0v11a1 0 1 01-10 1H3a0 2 0 01-1-0V2a0 2 0 011-1z" fill="#3D4757" fill-rule="evenodd"/></svg>',
        inline: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><path d="M4 1v1H2v2H1V2a1 1 0 011-1h2zm1 0h2v1H5V1zm3 0h2v1H8V1zm3 0h2a1 1 0 011 1v2h-1V2h-2V1zm3 4v2h-1V5h1zm0 3v2h-1V8h1zm0 3v2a1 1 0 01-1 1h-2v-1h2v-2h1zm-4 3H8v-1h2v1zm-3 0H5v-1h2v1zm-3 0H2a1 1 0 01-1-1v-2h1v2h2v1zm-3-4V8h1v2H1zm0-3V5h1v2H1zm6-5h1v2H7zm0 3h1v2H7zm0 3h1v2H7zm0 3h1v2H7zM2 8V7h2v1zm9 0V7h2v1zM5 8V7h2v1zm3 0V7h2v1z" fill="#909AA9" fill-rule="evenodd"/><path d="M2 7h5V2H2v5zm0 1v6h5V8H6zm11-1V2H8v5h5zm0 1H8v5h5V8zM2 2h11a1 1 0 010 0v11a1 0 1 01-10 1H3a0 1 0 01-1-0V2a0 1 0 011-1z" fill="#3D4757" fill-rule="evenodd"/></svg>',
        none: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><path d="M4 1v1H2v2H1V2a1 1 0 011-1h2zm1 0h2v1H5V1zm3 0h2v1H8V1zm3 0h2a1 1 0 011 1v2h-1V2h-2V1zm3 4v2h-1V5h1zm0 3v2h-1V8h1zm0 3v2a1 1 0 01-1 1h-2v-1h2v-2h1zm-4 3H8v-1h2v1zm-3 0H5v-1h2v1zm-3 0H2a1 1 0 01-1-1v-2h1v2h2v1zm-3-4V8h1v2H1zm0-3V5h1v2H1zm6-5h1v2H7zm0 3h1v2H7zm0 3h1v2H7zm0 3h1v2H7zM2 8V7h2v1zm9 0V7h2v1zM5 8V7h2v1zm3 0V7h2v1z" fill="#909AA9" fill-rule="evenodd"/></svg>',
        full: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16"><path d="M2 7h5V2H2v5zm0 1v5h5V8H2zm11-1V2H8v5h5zm0 1H8v5h5V8zM2 1h11a1 1 0 011 1v11a1 1 0 01-1 1H2a1 1 0 01-1-1V2a1 1 0 011-1z" fill="#3D4757" fill-rule="evenodd"/></svg>',
        outline: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><g fill="none" fill-rule="evenodd"><path d="M2 1h11a1 1 0 011 1v11a1 1 0 01-1 1H2a1 1 0 01-1-1V2a1 1 0 011-1zm0 1v11h11V2H2z" fill="#3D4757"/><path d="M7 2h1v2H7V2zm0 3h1v2H7V5zm0 3h1v2H7V8zm0 3h1v2H7v-2zM2 8V7h2v1H2zm3 0V7h2v1H5zm3 0V7h2v1H8zm3 0V7h2v1h-2z" fill="#909AA9"/></g></svg>',
        bottom: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><g fill="none" fill-rule="evenodd"><path d="M4 1v1H2v2H1V2a1 1 0 011-1h2zm1 0h2v1H5V1zm3 0h2v1H8V1zm3 0h2a1 1 0 011 1v2h-1V2h-2V1zm3 4v2h-1V5h1zm0 3v2h-1V8h1zm0 3v3H1v-3h1v2h11v-2h1zM1 10V8h1v2H1zm0-3V5h1v2H1z" fill="#909AA9"/><path d="M7 2h1v2H7V2zm0 3h1v2H7V5zm0 3h1v2H7V8zm0 3h1v2H7v-2zM2 8V7h2v1H2zm3 0V7h2v1H5zm3 0V7h2v1H8zm3 0V7h2v1h-2z" fill="#909AA9"/><path fill="#3D4757" d="M1 13h13v1H1z"/></g></svg>',
        top: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><g fill="none" fill-rule="evenodd"><path d="M11 14v-1h2v-2h1v2a1 1 0 01-1 1h-2zm-1 0H8v-1h2v1zm-3 0H5v-1h2v1zm-3 0H2a1 1 0 01-1-1v-2h1v2h2v1zm-3-4V8h1v2H1zm0-3V5h1v2H1zm0-3V1h13v3h-1V2H2v2H1zm13 1v2h-1V5h1zm0 3v2h-1V8h1z" fill="#909AA9"/><path fill="#3D4757" d="M1 1h13v1H1z"/><path d="M8 13H7v-2h1zm0-3H7V8h1zm0-3H7V5h1zm0-3H7V2h1zm5 3v1h-2V7zm-3 0v1H8V7zM7 7v1H5V7zM4 7v1H2V7z" fill="#909AA9"/></g></svg>',
        left: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><g fill="none" fill-rule="evenodd"><path d="M14 4h-1V2h-2V1h2a1 1 0 011 1v2zm0 1v2h-1V5h1zm0 3v2h-1V8h1zm0 3v2a1 1 0 01-1 1h-2v-1h2v-2h1zm-4 3H8v-1h2v1zm-3 0H5v-1h2v1zm-3 0H1V1h3v1H2v11h2v1zM5 1h2v1H5V1zm3 0h2v1H8V1z" fill="#909AA9"/><path fill="#3D4757" d="M1 1h1v13H1z"/><path d="M13 7v1h-2V7zm-3 0v1H8V7zM7 7v1H5V7zM4 7v1H2V7zm3-5h1v2H7zm0 3h1v2H7zm0 3h1v2H7zm0 3h1v2H7z" fill="#909AA9"/></g></svg>',
        right: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16"><g fill="none" fill-rule="evenodd"><path d="M1 11h1v2h2v1H2a1 1 0 01-1-1v-2zm0-1V8h1v2H1zm0-3V5h1v2H1zm0-3V2a1 1 0 011-1h2v1H2v2H1zm4-3h2v1H5V1zm3 0h2v1H8V1zm3 0h3v13h-3v-1h2V2h-2V1zm-1 13H8v-1h2v1zm-3 0H5v-1h2v1z" fill="#909AA9"/><path fill="#3D4757" d="M13 1h1v13h-1z"/><path d="M2 8V7h2v1zm3 0V7h2v1zm3 0V7h2v1zm3 0V7h2v1zm-3 5H7v-2h1zm0-3H7V8h1zm0-3H7V5h1zm0-3H7V2h1z" fill="#909AA9"/></g></svg>'
      },
      colors: [
        ['rgb(255, 255, 255)', 'rgb(0, 1, 0)', 'rgb(231, 229, 230)', 'rgb(68, 85, 105)', 'rgb(91, 156, 214)',
          'rgb(237, 125, 49)', 'rgb(165, 165, 165)', 'rgb(255, 192, 1)', 'rgb(67, 113, 198)', 'rgb(113, 174, 71)'
        ],
        ['rgb(242, 242, 242)', 'rgb(127, 127, 127)', 'rgb(208, 206, 207)', 'rgb(213, 220, 228)',
          'rgb(222, 234, 246)', 'rgb(252, 229, 213)', 'rgb(237, 237, 237)', 'rgb(255, 242, 205)',
          'rgb(217, 226, 243)', 'rgb(227, 239, 217)'
        ],
        ['rgb(216, 216, 216)', 'rgb(89, 89, 89)', 'rgb(175, 171, 172)', 'rgb(173, 184, 202)',
          'rgb(189, 215, 238)', 'rgb(247, 204, 172)', 'rgb(219, 219, 219)', 'rgb(255, 229, 154)',
          'rgb(179, 198, 231)', 'rgb(197, 224, 179)'
        ],
        ['rgb(191, 191, 191)', 'rgb(63, 63, 63)', 'rgb(117, 111, 111)', 'rgb(133, 150, 176)',
          'rgb(156, 194, 230)', 'rgb(244, 177, 132)', 'rgb(201, 201, 201)', 'rgb(254, 217, 100)',
          'rgb(142, 170, 218)', 'rgb(167, 208, 140)'
        ],
        ['rgb(165, 165, 165)', 'rgb(38, 38, 38)', 'rgb(58, 56, 57)', 'rgb(51, 63, 79)', 'rgb(46, 117, 181)',
          'rgb(196, 90, 16)', 'rgb(123, 123, 123)', 'rgb(191, 142, 1)', 'rgb(47, 85, 150)', 'rgb(83, 129, 54)'
        ],
        ['rgb(127, 127, 127)', 'rgb(12, 12, 12)', 'rgb(23, 21, 22)', 'rgb(34, 42, 53)', 'rgb(31, 78, 122)',
          'rgb(132, 60, 10)', 'rgb(82, 82, 82)', 'rgb(126, 96, 0)', 'rgb(32, 56, 100)', 'rgb(54, 86, 36)'
        ],
        ['rgb(192, 0, 0)', 'rgb(254, 0, 0)', 'rgb(253, 193, 1)', 'rgb(255, 255, 1)', 'rgb(147, 208, 81)',
          'rgb(0, 176, 78)', 'rgb(1, 176, 241)', 'rgb(1, 112, 193)', 'rgb(1, 32, 96)', 'rgb(112, 48, 160)'
        ]
      ],
      custom: '',
      borderType: 'full',
      borderStyle: 1,
      borderClass: 'border-style-number-1',
      borderColor: 'rgb(0, 1, 0)',
      colorDropdownMenu: false,
      borderLineDropdownMenu: false,
    }
  },
  mounted: function() {

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
        valid = valid && this.checkColorValue(val)
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
    handleVisibleChange(val) {
      if (val) {
        this.colorDropdownMenu = false
        this.borderLineDropdownMenu = false
      }
    },
    handleColorDropdownMenu() {
      this.borderLineDropdownMenu = false
      if (this.colorDropdownMenu){
        this.colorDropdownMenu = false
      } else {
        this.colorDropdownMenu = true
      }
      this.custom = this.borderColor
    },
    handleBorderLineDropdownMenu() {
      this.colorDropdownMenu = false
      if (this.borderLineDropdownMenu){
        this.borderLineDropdownMenu = false
      } else {
        this.borderLineDropdownMenu = true
      }
    },
    changeColor(color) {
      this.borderColor = color
      this.colorDropdownMenu = false
    },
    handleBorderStyle(style) {
      this.borderStyle = style
      this.borderClass = 'border-style-number-' +  style
    },
    changeBorder(val) {
      this.borderType = val
      this.clickBorder()
      this.$refs.borderDropdownMenu.hide();
    },
    clickBorder() {
      this.handleBorder()
    },
    handleBorder() {
      const table = this.context.hot
      const selected = table.getSelected()
      if (!selected) {
        return
      }
      let startRow = selected[0]
      let startCol = selected[1]
      let endRow = selected[2]
      let endCol = selected[3]
      let tmp = endRow
      if (startRow > endRow) {
        endRow = startRow
        startRow = tmp
      }
      tmp = endCol
      if (startCol > endCol) {
        endCol = startCol
        startCol = tmp
      }
      const newBorder = {
        width: 1,
        color: '0,0,0',
        style: 'solid'
      }
      newBorder.color = this.borderColor.replace('rgb(','').replace(')','')
      if (this.borderStyle <= 3){
        newBorder.width = this.borderStyle
        newBorder.style = 'solid'
      } else {
        newBorder.width = this.borderStyle - 3
        newBorder.style = 'dashed'
      }
      const borderType = this.borderType
      let oldBorderStyle = this.updateBorderStyles(this.context, startRow, startCol, endRow, endCol, newBorder, borderType)
      table.render()
      const that = this
      undoManager.add({
        redo: function() {
          oldBorderStyle = that.updateBorderStyles(that.context, startRow, startCol, endRow, endCol, newBorder,borderType)
          table.render()
        },
        undo: function() {
          that.updateOldBorderStyles(that.context, startRow, startCol, endRow, endCol, oldBorderStyle)
          table.render()
        }
      })
    },
    updateBorderStyles(context, startRow, startCol, endRow, endCol, newBorder, target) {
      const oldStyle = {}
      for (let i = startRow; i <= endRow; i++) {
        for (let j = startCol; j <= endCol; j++) {
          const cellDef = context.getCell(i, j)
          if (!cellDef) {
            continue
          }
          const cellStyle = cellDef.cellStyle
          oldStyle[i + ',' + j] = {
            leftBorder: cellStyle.leftBorder,
            rightBorder: cellStyle.rightBorder,
            topBorder: cellStyle.topBorder,
            bottomBorder: cellStyle.bottomBorder
          }
          if (target === 'full') {
            cellStyle.leftBorder = Object.assign({},newBorder)
            cellStyle.rightBorder = Object.assign({},newBorder)
            cellStyle.topBorder = Object.assign({},newBorder)
            cellStyle.bottomBorder = Object.assign({},newBorder)
          } else if (target === 'none') {
            cellStyle.leftBorder = ''
            cellStyle.rightBorder = ''
            cellStyle.topBorder = ''
            cellStyle.bottomBorder = ''
          }else if (target === 'outline') {
            if(i === startRow){
              cellStyle.topBorder = Object.assign({},newBorder)
            }
            if(i === endRow){
              cellStyle.bottomBorder = Object.assign({},newBorder)
            }
            if(j === startCol){
              cellStyle.leftBorder = Object.assign({},newBorder)
            }
            if(j === endCol){
              cellStyle.rightBorder = Object.assign({},newBorder)
            }
          } else if (target === 'inline') {
            if(i === startRow){
              cellStyle.topBorder = ''
            } else {
              cellStyle.topBorder = Object.assign({},newBorder)
            }
            if(i === endRow){
              cellStyle.bottomBorder = ''
            } else {
              cellStyle.bottomBorder = Object.assign({},newBorder)
            }
            if(j === startCol){
              cellStyle.leftBorder = ''
            } else {
              cellStyle.leftBorder = Object.assign({},newBorder)
            }
            if(j === endCol){
              cellStyle.rightBorder = ''
            } else {
              cellStyle.rightBorder = Object.assign({},newBorder)
            }
          } else if (target === 'inlineHorizontal') {
            if(i === startRow){
              cellStyle.topBorder = ''
            } else {
              cellStyle.topBorder = Object.assign({},newBorder)
            }
            if(i === endRow){
              cellStyle.bottomBorder = ''
            } else {
              cellStyle.bottomBorder = Object.assign({},newBorder)
            }
            cellStyle.leftBorder = ''
            cellStyle.rightBorder = ''
          } else if (target === 'inlineVertical') {
            cellStyle.topBorder = ''
            cellStyle.bottomBorder = ''
            if(j === startCol){
              cellStyle.leftBorder = ''
            } else {
              cellStyle.leftBorder = Object.assign({},newBorder)
            }
            if(j === endCol){
              cellStyle.rightBorder = ''
            } else {
              cellStyle.rightBorder = Object.assign({},newBorder)
            }
          } else if (target === 'left' && j === startCol) {
            cellStyle.leftBorder = Object.assign({},newBorder)
          } else if (target === 'right' && j === endCol) {
            cellStyle.rightBorder = newBorder
          } else if (target === 'top' && i === startRow) {
            cellStyle.topBorder = Object.assign({},newBorder)
          } else if (target === 'bottom' && i === endRow) {
            cellStyle.bottomBorder = Object.assign({},newBorder)
          }
        }
      }
      return oldStyle
    },
    updateOldBorderStyles(context, startRow, startCol, endRow, endCol, oldBorderStyle) {
      for (let i = startRow; i <= endRow; i++) {
        for (let j = startCol; j <= endCol; j++) {
          const cellDef = context.getCell(i, j)
          if (!cellDef) {
            continue
          }
          const oldBorder = oldBorderStyle[i + ',' + j]
          const cellStyle = cellDef.cellStyle
          cellStyle.leftBorder = oldBorder.leftBorder || ''
          cellStyle.rightBorder = oldBorder.rightBorder || ''
          cellStyle.topBorder = oldBorder.topBorder || ''
          cellStyle.bottomBorder = oldBorder.bottomBorder || ''
        }
      }
    }
  }
}
</script>

<style>

  .dropdown-menu-icon {
    display: inline-block;
    margin-right: 5px;
    cursor: pointer;
  }

  .dropdown-menu-icon svg {
    vertical-align: text-bottom;
  }

  .line-type {
    padding: 5px;
    margin: 8px 0px;
  }

  .line-type:hover {
    cursor: pointer;
    background: #EBEEF5;
  }

  .line-type.active {
    background: #b3d8ff;
  }

  .dropdown-menu-item {
    display: inline-block;
    margin: 5px;
    width: 26px;
    height: 26px;
    padding: 5px;
  }

  .border-style {
    width: 112px;
    height: 30px;
    padding: 15px;
  }

  .border-style div{
    width: 80px;
  }

  .border-style:hover, .dropdown-menu-item:hover {
    background: #E4E7ED;
  }

  .border-style.active,.dropdown-menu-item.active {
    background: #DCDFE6;
  }

  .color_dropdown-menu {
    display: inline-block;
    left:0px;
    top:142px;
    position: absolute;
    padding: 10px;
    background: #fff;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  }

  .border-style-number-1 {
    border-top: 1px solid #606266;
  }
  .border-style-number-2 {
    border-top: 2px solid #606266;
  }
  .border-style-number-3 {
    border-top: 3px solid #606266;
  }
  .border-style-number-4 {
    border-top: 1px dashed #606266;
  }
  .border-style-number-5 {
    border-top: 2px dashed #606266;
  }
  .border-style-number-6 {
    border-top: 3px dashed #606266;
  }
</style>
