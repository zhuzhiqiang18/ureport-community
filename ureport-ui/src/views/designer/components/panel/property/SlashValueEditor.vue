<template>
  <el-form class="div-block-clearance" label-position="left" :model="form" size="mini" label-width="90px">
    <el-form-item label="斜表头名称:">
      <el-input v-model="form.name" placeholder="使用竖线隔开如:名称A|名称B|名称C" @change="handleName" />
    </el-form-item>
    <el-table
      :data="form.slashes"
      size="mini"
      border
      style="width: 100%"
    >
      <el-table-column
        prop="text"
        header-align="center"
        show-overflow-tooltip
        label="项目名称"
      />
      <el-table-column label="X" header-align="center">
        <template slot-scope="scope">
          <el-input v-model="scope.row.x" style="width:100%" size="mini" @change="doDraw" />
        </template>
      </el-table-column>
      <el-table-column label="Y" header-align="center">
        <template slot-scope="scope">
          <el-input v-model="scope.row.y" style="width:100%" size="mini" @change="doDraw" />
        </template>
      </el-table-column>
      <el-table-column label="旋转角度" header-align="center">
        <template slot-scope="scope">
          <el-input v-model="scope.row.degree" style="width:100%" size="mini" @change="doDraw" />
        </template>
      </el-table-column>
    </el-table>
  </el-form>
</template>

<script>
export default {
  props: {
    slashValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      form: {
        name: '',
        slashes: []
      },
      crossTabWidget: null
    }
  },
  watch: {
    slashValue: function(val) {
      this.init(val)
    }
  },
  mounted: function() {
    this.init(this.slashValue)
  },
  methods: {
    init(val) {
      if (val.context) {
        this.context = val.context
        this.refresh(val.rowIndex, val.colIndex, val.row2Index, val.col2Index)
      }
    },
    refresh(rowIndex, colIndex, row2Index, col2Index) {
      const cellDef = this.context.getCell(rowIndex, colIndex)
      if (!cellDef) {
        return
      }
      this.rowIndex = rowIndex
      this.colIndex = colIndex
      this.row2Index = row2Index
      this.col2Index = col2Index
      this.cellDef = cellDef
      if (cellDef.value.slashes) {
        let text = ''
        const slashes = cellDef.value.slashes
        if (slashes && slashes.length > 0) {
          for (let i = 0; i < slashes.length; i++) {
            text = text + '|' + slashes[i].text
          }
          this.form.name = text.replace('|', '')
          this.form.slashes = slashes
          this.doDraw()
        }
      } else {
        cellDef.value.slashes = []
        this.form.slashes = []
      }
    },
    handleName() {
      const slashes = []
      const value = this.form.name
      if (value) {
        let index = 1
        for (const name of value.split('|')) {
          slashes.push({
            degree: 0,
            x: 15 * index,
            y: 15,
            text: name
          })
          index++
        }
        this.cellDef.value.slashes = slashes
        this.form.slashes = slashes
        this.doDraw()
        this.slashValue.context.hot.render()
      }
    },
    doDraw() {
      const cellDef = this.cellDef
      const slashValue = cellDef.value
      const cellStyle = cellDef.cellStyle
      const forecolor = cellStyle.forecolor ? `rgb(${cellStyle.forecolor})` : 'rgb(0,0,0)'
      let border = null
      if (cellStyle.bottomBorder) {
        border = cellStyle.bottomBorder
      } else if (cellStyle.leftBorder) {
        border = cellStyle.leftBorder
      } else if (cellStyle.rightBorder) {
        border = cellStyle.rightBorder
      } else if (cellStyle.topBorder) {
        border = cellStyle.topBorder
      }
      let borderColor = border ? `rgb(${border.color})` : 'rgb(0,0,0)'
      let borderWidth = 1
      if(border && border.width) {
        borderWidth = border.width
      }
      let svgXml = '<svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%">'
      const slashes = slashValue.slashes || []
      const length = slashes.length
      let size = length - 1
      if (length % 2 === 0) {
        svgXml = svgXml + `<line x1="0" y1="0" x2="100%" y2="100%" style="stroke-width: ${borderWidth}px;stroke:${borderColor};"></line>`
        size--
      }
      size = size / 2
      const step = 100 / (size + 1)
      for (let i = 1; i <= size; i++) {
        svgXml = svgXml + `<line x1="0" y1="0" x2="100%" y2="${step * i}%" style="stroke-width:${borderWidth}px;stroke:${borderColor};"></line>`
        svgXml = svgXml + `<line x1="0" y1="0" x2="${step * i}%" y2="100%" style="stroke-width:${borderWidth}px;stroke:${borderColor}"></line>`
      }
      for (let i = 0; i < length; i++) {
        const s = slashes[i]
        svgXml = svgXml + `<text x="${s.x}" y="${s.y}" font-family="${cellStyle.fontFamily || '宋体'}" font-size="${cellStyle.fontSize}" fill="${forecolor}"  transform="rotate(${s.degree} ${s.x},${s.y})">${s.text}</text>`
      }
      svgXml = svgXml + '</svg>'
      cellDef.value.svgXml = svgXml
      this.context.hot.render()
    }
  }
}
</script>

<style>
  .slash-label {
    font-size: 14px;
    color: #606266;
    margin: 0px 10px;
  }
</style>
