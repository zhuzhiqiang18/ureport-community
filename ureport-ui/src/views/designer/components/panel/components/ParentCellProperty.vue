<template>
  <el-form class="div-block-clearance" :model="form" label-position="left" label-width="90px" size="mini">
    <el-form-item label="左父格:" style="text-align: center;">
      <el-select v-model="form.leftParent" style="width: 32%;float: left;" @change="handleLeftParent">
        <el-option
          v-for="item in parentOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="form.leftParentCellName" :disabled="form.leftParent !=='custom'" placeholder="" style="width: 32%;" @change="handleLeftParentCell">
        <el-option
          v-for="item in cellNameOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="form.leftParentRowNumber" :disabled="form.leftParent !=='custom'" placeholder="" style="width: 32%;float: right;" @change="handleLeftParentCell">
        <el-option
          v-for="item in rowNumberOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="上父格:" style="text-align: center;">
      <el-select v-model="form.topParent" style="width: 32%;float: left;" @change="handleTopParent">
        <el-option
          v-for="item in parentOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="form.topParentCellName" :disabled="form.topParent !=='custom'" placeholder="" style="width: 32%" @change="handleTopParentCell">
        <el-option
          v-for="item in cellNameOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="form.topParentRowNumber" :disabled="form.topParent !=='custom'" placeholder="" style="width: 32%;float: right;" @change="handleTopParentCell">
        <el-option
          v-for="item in rowNumberOptions"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
    </el-form-item>
    <!-- <el-form-item label="换行计算:">
      <el-switch v-model="form.wrapCompute" @change="handleWrapCompute" />
    </el-form-item> -->
  </el-form>
</template>

<script>
export default {
  name: 'ParentCellProperty',
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      parentOptions: [
        { value: 'root', label: '无' },
        { value: 'default', label: '默认' },
        { value: 'custom', label: '自定义' }
      ],
      cellNameOptions: [],
      rowNumberOptions: [],
      cellDef: null,
      form: {
        leftParent: 'default',
        leftParentCellName: '',
        leftParentRowNumber: '',
        topParent: 'default',
        topParentCellName: '',
        topParentRowNumber: '',
        wrapCompute: '0'
      }
    }
  },
  mounted: function() {
    this.init()
  },
  methods: {
    init() {
      this.buildParentCellNameOptions()
      this.buildParentRowNumberOptions()
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
      // 处理左父格
      let leftParentCellName = cellDef.leftParentCellName
      if (leftParentCellName) {
        if (leftParentCellName === 'root') {
          this.form.leftParent = 'root'
          this.form.leftParentCellName = ''
          this.form.leftParentRowNumber = ''
        } else {
          this.form.leftParent = 'custom'
          const data = this.parseCellName(leftParentCellName)
          this.form.leftParentCellName = data.name
          this.form.leftParentRowNumber = data.num
        }
      } else {
        if (colIndex === 0) {
          this.form.leftParent = 'root'
          this.form.leftParentCellName = ''
          this.form.leftParentRowNumber = ''
        } else {
          this.form.leftParent = 'default'
          const parentCellName = this.context.getParentCellName(this.rowIndex,this.colIndex,true)
          if(parentCellName) {
            const {name,num} = this.parseCellName(parentCellName)
            this.form.leftParentCellName = name
            this.form.leftParentRowNumber = num
          } else {
            this.form.leftParentCellName = ''
            this.form.leftParentRowNumber = ''
          }
        }
      }
      // 处理右父格
      const topParentCellName = cellDef.topParentCellName
      if (topParentCellName) {
        if (topParentCellName === 'root') {
          this.form.topParent = 'root'
          this.form.topParentCellName = ''
          this.form.topParentRowNumber = ''
        } else {
          this.form.topParent = 'custom'
          const data = this.parseCellName(topParentCellName)
          this.form.topParentCellName = data.name
          this.form.topParentRowNumber = data.num
        }
      } else {
        if (rowIndex === 0) {
          this.form.topParent = 'root'
          this.form.topParentCellName = ''
          this.form.topParentRowNumber = ''
        } else {
          this.form.topParent = 'default'
          const parentCellName = this.context.getParentCellName(this.rowIndex,this.colIndex,false)
          if(parentCellName) {
           const {name,num} = this.parseCellName(parentCellName)
           this.form.topParentCellName = name
           this.form.topParentRowNumber = num
          } else {
           this.form.topParentCellName = ''
           this.form.topParentRowNumber = ''
          }
        }
      }
      const cellStyle = cellDef.cellStyle
      if (cellStyle.wrapCompute) {
        this.form.wrapCompute = true
      } else {
        this.form.wrapCompute = false
      }
    },
    handleLeftParent(val) {
      if (val === 'root') {
        this.form.leftParentCellName = ''
        this.form.leftParentRowNumber = ''
        this.setParentCell('root', true)
      } else if (val === 'default') {
        const parentCellName = this.context.getParentCellName(this.rowIndex,this.colIndex,true)
        if(parentCellName) {
          const {name,num} = this.parseCellName(parentCellName)
          this.form.leftParentCellName = name
          this.form.leftParentRowNumber = num
        } else {
          this.form.leftParentCellName = ''
          this.form.leftParentRowNumber = ''
        }
        this.setParentCell(null, true)
      } else {
        const parentCellName = this.context.getParentCellName(this.rowIndex,this.colIndex,true)
        if(parentCellName) {
         const {name,num} = this.parseCellName(parentCellName)
         this.form.leftParentCellName = name
         this.form.leftParentRowNumber = num
         this.setParentCell(parentCellName, true)
        } else {
         this.form.leftParentCellName = ''
         this.form.leftParentRowNumber = ''
         this.setParentCell('root', true)
        }
      }
    },
    handleLeftParentCell(val) {
      const name = this.form.leftParentCellName
      const num = this.form.leftParentRowNumber
      if (name !== '' && num !== '') {
        this.setParentCell(name + num, true)
      }
    },
    handleTopParent(val) {
      if (val === 'root') {
        this.form.topParentCellName = ''
        this.form.topParentRowNumber = ''
        this.setParentCell('root', false)
      } else if (val === 'default') {
        const parentCellName = this.context.getParentCellName(this.rowIndex,this.colIndex,false)
        if(parentCellName) {
          const {name,num} = this.parseCellName(parentCellName)
          this.form.topParentCellName = name
          this.form.topParentRowNumber = num
        } else {
          this.form.topParentCellName = ''
          this.form.topParentRowNumber = ''
        }
        this.setParentCell(null, false)
      } else {
        const parentCellName = this.context.getParentCellName(this.rowIndex,this.colIndex,false)
        if(parentCellName) {
         const {name,num} = this.parseCellName(parentCellName)
         this.form.topParentCellName = name
         this.form.topParentRowNumber = num
         this.setParentCell(parentCellName, false)
        } else {
         this.form.topParentCellName = ''
         this.form.topParentRowNumber = ''
         this.setParentCell('root', false)
        }
      }
    },
    handleTopParentCell(val) {
      const name = this.form.topParentCellName
      const num = this.form.topParentRowNumber
      if (name !== '' && num !== '') {
        this.setParentCell(name + num, false)
      }
    },
    buildParentCellNameOptions() {
      this.cellNameOptions = []
      const hot = this.context.hot
      const countCols = hot.countCols()
      const cells = []
      for (let j = 0; j < countCols; j++) {
        const name = this.context.getCellName(null, j)
        cells.push({
          value: name,
          label: name
        })
      }
      this.cellNameOptions = cells
    },
    buildParentRowNumberOptions() {
      this.rowNumberOptions = []
      const hot = this.context.hot
      const countRows = hot.countRows()
      const rows = []
      for (let j = 1; j <= countRows; j++) {
        rows.push({
          value: j,
          label: j
        })
      }
      this.rowNumberOptions = rows
    },
    parseCellName(cellName) {
      let pos = -1
      for (let i = 0; i < cellName.length; i++) {
        const char = cellName.charAt(i)
        const num = parseInt(char)
        if (!isNaN(num)) {
          pos = i
          break
        }
      }
      const name = cellName.substring(0, pos)
      const num = cellName.substring(pos, cellName.length)
      return {
        name,
        num
      }
    },
    setParentCell(cellName, isLeft) {
      for (let i = this.rowIndex; i <= this.row2Index; i++) {
        for (let j = this.colIndex; j <= this.col2Index; j++) {
          const cellDef = this.context.getCell(i, j)
          if (!cellDef) {
            continue
          }
          if (isLeft) {
            if (cellName) {
              cellDef.leftParentCellName = cellName
            } else {
              cellDef.leftParentCellName = null
            }
          } else {
            if (cellName) {
              cellDef.topParentCellName = cellName
            } else {
              cellDef.topParentCellName = null
            }
          }
        }
      }
    },
    handleWrapCompute(val) {
      this.setWrapCompute(val)
    },
    setWrapCompute(wrapCompute) {
      for (let i = this.rowIndex; i <= this.row2Index; i++) {
        for (let j = this.colIndex; j <= this.col2Index; j++) {
          const cellDef = this.context.getCell(i, j)
          if (!cellDef) {
            continue
          }
          cellDef.cellStyle.wrapCompute = wrapCompute
        }
      }
    }
  }
}
</script>

<style>
</style>
