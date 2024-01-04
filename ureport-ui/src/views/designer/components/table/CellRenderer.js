/**
 * Created by Jacky.Gao on 2017-01-31.
 */
import renderChart from '../widget/ChartWidget.js'

import imagePath from '@/assets/icons/image.svg'
import qrcodePath from '@/assets/icons/qrcode.svg'
import barcodePath from '@/assets/icons/barcode.svg'

import exprExpandDown from '@/assets/icons/expr-expand-down.svg'
import expandDown from '@/assets/icons/expand-down.svg'

import exprExpandRight from '@/assets/icons/expr-expand-right.svg'
import expandRight from '@/assets/icons/expand-right.svg'

import property from '@/assets/icons/property.svg'
import expression from '@/assets/icons/expression.svg'
import { fontSizes } from '@/data/select-options.js'

export function afterRenderer(td,row,col,prop,value,cellProperties){
  if(!this.context){
    return
  }
  let cellDef = this.context.getCell(row,col)
  if(!cellDef) {
    return
  }
  const cellStyle = cellDef.cellStyle
  const cellValue = cellDef.value
  const valueType = cellValue.type
  td.innerHTML = ''
  if(valueType==="simple" && cellValue.value === '') {
    if(Object.keys(cellStyle).length === 5 && cellStyle.align === 'left' && cellStyle.fontFamily === '宋体' && cellStyle.fontSize === 10 && cellStyle.valign === 'middle') {
      return
    }
  }
  if(valueType==='dataset') {
      const image = document.createElement('img')
      if(cellDef.expand === 'Down') {
        image.src = expandDown
      } else if(cellDef.expand === 'Right') {
        image.src = expandRight
        image.style.position = 'absolute'
      } else {
        image.src = property
      }
      td.appendChild(image)
      const tip = cellValue.datasetName + "." + cellValue.aggregate+ '(' + cellValue.property + ')'
      const text = document.createTextNode(tip)
      td.appendChild(text)
  } else if(valueType==='expression'){
      const image = document.createElement('img')
      if(cellDef.expand === 'Down') {
        image.src = exprExpandDown
      } else if(cellDef.expand === 'Right') {
        image.src = exprExpandRight
        image.style.position = 'absolute'
      } else {
        image.src = expression
      }
      td.appendChild(image)
      const tip= cellValue.value || ''
      const text = document.createTextNode(tip)
      td.appendChild(text)
  } else if(valueType==='image') {
      const image = document.createElement('img')
      image.src = imagePath
      image.width = 20
      td.appendChild(image)
  } else if(valueType==='slash'){
    let svgXml = cellDef.value.svgXml
    if(svgXml){
      const container = document.createElement('div')
      container.style.width = td.clientWidth - 1 + 'px'
      const rowHeights = this.context.hot.getSettings().rowHeights
      container.style.height = rowHeights[row] + td.clientHeight - 22 + 'px'
      container.innerHTML = svgXml
      td.appendChild(container)
    }
  } else if(valueType==='zxing'){
    let imagePath = qrcodePath
    if(cellValue.category==='barcode') {
      imagePath = barcodePath
    }
    const image = document.createElement('img')
    image.src = imagePath
    image.width =  cellValue.width
    image.height = cellValue.height
    td.appendChild(image)
  } else if(valueType==='chart') {
    const container = document.createElement('div')
    container.style.width = td.clientWidth + 'px'
    const rowHeights = this.context.hot.getSettings().rowHeights
    container.style.height = rowHeights[row] + td.clientHeight - 22 + 'px'
    td.appendChild(container)
    renderChart(container,cellDef)
  } else if(valueType==="simple"){
    let tip = cellValue.value || ""
    if(tip && tip!==""){
      tip = tip.replace(new RegExp('<','gm'),'&lt;')
      tip = tip.replace(new RegExp('>','gm'),'&gt;')
      tip = tip.replace(new RegExp('\r\n','gm'),'<br>')
      tip = tip.replace(new RegExp('\n','gm'),'<br>')
      tip = tip.replace(new RegExp(' ','gm'),'&nbsp;')
      const text = document.createTextNode(tip)
      td.appendChild(text)
    }
  }
  td.style.wordBreak = 'break-all'
  td.style.lineHeight = 'normal'
  td.style.whiteSpace = 'nowrap'
  td.style.padding = '0 1px'
  if(cellStyle.align) {
    td.style.textAlign = cellStyle.align
  }
  if(cellStyle.valign) {
    td.style.verticalAlign = cellStyle.valign
  }
  if(cellStyle.bold) {
    td.style.fontWeight = 'bold'
  }
  if(cellStyle.italic) {
    td.style.fontStyle = 'italic'
  }
  if(cellStyle.underline) {
    td.style.textDecoration = 'underline'
  }
  if(cellStyle.forecolor){
    td.style.color = "rgb("+cellStyle.forecolor+")"
  }
  if(cellStyle.bgcolor){
    td.style.backgroundColor = "rgb("+cellStyle.bgcolor+")"
  }
  if(cellStyle.fontSize) {
    td.style.fontSize = cellStyle.fontSize+"pt"
  }
  if(cellStyle.fontFamily){
    td.style.fontFamily = cellStyle.fontFamily
  }
  if(cellStyle.lineHeight){
    td.style.lineHeight = cellStyle.lineHeight
  }else{
    td.style.lineHeight = ''
  }
  const leftBorder = cellStyle.leftBorder
  if(leftBorder) {
    if(leftBorder === '' || leftBorder.style === "none") {
      td.style.borderLeft = ''
    } else {
      let borderStyle = leftBorder.style ? leftBorder.style : 'solid'
      let borderWidth = leftBorder.width ? parseInt(leftBorder.width) : 0
      if (borderStyle === 'solid') {
        borderStyle = 'double'
      } else {
        borderWidth += 0.5
      }
      const style = borderWidth + "px " + borderStyle  + " rgb(" + leftBorder.color + ")"
      td.style.borderLeft = style
    }
  }
  const rightBorder = cellStyle.rightBorder
  if(rightBorder) {
    if(rightBorder==='' || rightBorder.style==="none") {
      td.style.borderRight = ''
    }else{
      let borderStyle = rightBorder.style ? rightBorder.style : 'solid'
      let borderWidth = rightBorder.width ? parseInt(rightBorder.width) : 0
      if (borderStyle === 'solid') {
        borderStyle = 'double'
      } else {
        borderWidth += 0.5
      }
      let style = borderWidth + "px " + borderStyle + " rgb("+ rightBorder.color+")"
      td.style.borderRight = style
    }
  }
  const topBorder = cellStyle.topBorder;
  if(topBorder){
    if(topBorder ==='' || topBorder.style==="none"){
      td.style.borderTop = ''
    } else {
      let borderStyle = topBorder.style ? topBorder.style : 'solid'
      let borderWidth = topBorder.width ? parseInt(topBorder.width) : 0
      if (borderStyle === 'solid') {
        borderStyle = 'double'
      } else {
        borderWidth += 0.5
      }
      let style = borderWidth + "px " + borderStyle + " rgb("+ topBorder.color+")"
      td.style.borderTop = style
    }
  }
  const bottomBorder = cellStyle.bottomBorder
  if(bottomBorder) {
    if(bottomBorder === '' || bottomBorder.style === "none") {
      td.style.borderBottom = ''
    }else{
      let borderStyle = bottomBorder.style ? bottomBorder.style : 'solid'
      let borderWidth = bottomBorder.width ? parseInt(bottomBorder.width) : 0
      if (borderStyle === 'solid') {
        borderStyle = 'double'
      } else {
        borderWidth += 0.5
      }
      let style = borderWidth + "px " + borderStyle + " rgb("+ bottomBorder.color+")"
      td.style.borderBottom = style
    }
  }
  if(this.context.selection && this.context.selection[0] === row && this.context.selection[1] === col) {
    refreshSelectionCellBoard(td, cellDef)
  }
}

export function refreshSelectionCellBoard(td, cellDef) {
  const selectionCellBoard = document.getElementById("selectionCellBoard")
  selectionCellBoard.innerHTML = ''
  const cellValue = cellDef.value
  const valueType = cellValue.type
  if(valueType === 'image' || valueType==='zxing') {
    if(cellValue.source === 'expression') {
      const image = document.createElement('img')
      if(cellDef.expand === 'Down') {
        image.src = exprExpandDown
      } else if(cellDef.expand === 'Right') {
        image.src = exprExpandRight
        image.style.position = 'absolute'
      } else {
        image.src = expression
      }
      selectionCellBoard.appendChild(image)
      const tip= cellValue.value || ''
      const text = document.createTextNode(tip)
      selectionCellBoard.appendChild(text)
    } else {
      selectionCellBoard.innerHTML = cellValue.value || ''
    }
  } else if(valueType==='slash'){

  } else if(valueType==='chart'){
    selectionCellBoard.innerHTML = ''
  } else {
    selectionCellBoard.innerHTML = td.innerHTML
  }
}
