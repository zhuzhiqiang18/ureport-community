/**
 * Created by Jacky.gao on 2016/7/27.
 */
import UndoManager from 'undo-manager'
import saveSvgAsPng from 'save-svg-as-png'
import { Message } from 'element-ui'

export function resetTableData(hot) {
  const countCols = hot.countCols()
  const  countRows = hot.countRows()
  const  context = hot.context
  const  data = []
  for (let i = 0; i < countRows; i++) {
    let rowData = []
    for (let j = 0; j < countCols; j++) {
      let td = hot.getCell(i, j)
      if (!td) {
        rowData.push("")
        continue
      }
      let cellDef = context.getCell(i, j)
      if (cellDef) {
        let valueType = cellDef.value.type
        let  value = cellDef.value
        if (valueType === 'dataset') {
          let text = value.datasetName + "." + value.aggregate + "("
          let prop = value.property
          if (prop.length > 13) {
            text += prop.substring(0, 10) + '..)'
          } else {
            text += prop + ")"
          }
          rowData.push(text);
        } else if (valueType === 'expression') {
          let v = value.value || ''
          if (v.length > 16) {
            v = v.substring(0, 13) + '...'
          }
          rowData.push(v);
        } else {
          rowData.push(value.value || "");
        }
      } else {
        rowData.push("")
      }
    }
    data.push(rowData)
  }
  hot.loadData(data)
  context.freezeLine.refresh()
}

export function buildNewCellDef(rowNumber, columnNumber) {
  let cellDef = {
    rowNumber,
    columnNumber,
    expand: 'None',
    cellStyle: {
      fontSize: 10,
      forecolor: '0,0,0',
      fontFamily: '宋体',
      align: 'left',
      valign: 'middle'
    },
    value: {
      type: 'simple',
      value: ''
    }
  }
  return cellDef
}

export async function tableToXml(context) {
  const hot = context.hot;
  let countRows = hot.countRows()
  let countCols = hot.countCols()
  let xml = `<?xml version="1.0" encoding="UTF-8"?><ureport>`
  let rowsXml = ''
  let columnXml = ''
  const rowHeaders = context.rowHeaders

  let maxRows = 0
  let maxCols = 0

  for (let i = 0; i < countRows; i++) {
    for (let j = 0; j < countCols; j++) {
      let cellDef = context.getCell(i, j)
      if (!cellDef) {
        continue
      }
      const span = getSpan(hot, i, j)
      let rowSpan = span.rowspan
      let colSpan = span.colspan

      let isDefault = false
      const value = cellDef.value
      if (value.type === 'simple' && value.value === '') {
        isDefault = colSpan === 0 && rowSpan === 0
        const cellStyle = cellDef.cellStyle
        isDefault = isDefault && !(cellStyle.topBorder || cellStyle.bottomBorder || cellStyle.leftBorder || cellStyle
          .rightBorder)
        isDefault = isDefault && !cellStyle.bgcolor
      }
      if (!isDefault) {
        if (colSpan > 0) {
          maxCols = Math.max(maxCols, j + colSpan)
        } else {
          maxCols = Math.max(maxCols, j + 1)
        }
        if (rowSpan > 0) {
          maxRows = Math.max(maxRows, i + rowSpan)
        } else {
          maxRows = Math.max(maxRows, i + 1)
        }
      }
    }
  }
  countRows = maxRows + 1
  countCols = maxCols + 1
  for (let i = 0; i < countRows; i++) {
    let height = hot.getRowHeight(i) || 16
    height = pixelToPoint(height)
    let band = null
    for (let header of rowHeaders) {
      if (header.rowNumber === i) {
        band = header.band
        break
      }
    }
    if (band) {
      rowsXml += `<row row-number="${i+1}" height="${height}" band="${band}"/>`
    } else {
      rowsXml += `<row row-number="${i+1}" height="${height}"/>`
    }
  }
  for (let i = 0; i < countCols; i++) {
    let width = hot.getColWidth(i) || 30
    width = pixelToPoint(width)
    columnXml += `<column col-number="${i+1}" width="${width}"/>`
  }
  let cellXml = ''
  let spanData = []
  for (let i = 0; i < countRows; i++) {
    for (let j = 0; j < countCols; j++) {
      if (spanData.indexOf(i + "," + j) > -1) {
        continue
      }
      let cellDef = context.getCell(i, j)
      if (!cellDef) {
        continue
      }
      let cellName = context.getCellName(i, j)
      cellXml += `<cell expand="${cellDef.expand}" name="${cellName}" row="${(i+1)}" col="${(j+1)}"`;
      if (cellDef.leftParentCellName) {
        cellXml += ` left-cell="${cellDef.leftParentCellName}"`
      }
      if (cellDef.topParentCellName) {
        cellXml += ` top-cell="${cellDef.topParentCellName}"`
      }
      if (cellDef.fillBlankRows) {
        cellXml += ` fill-blank-rows="${cellDef.fillBlankRows}"`;
        if (cellDef.multiple) {
          cellXml += ` multiple="${cellDef.multiple}"`;
        }
      }
      const span = getSpan(hot, i, j)
      let rowSpan = span.rowspan
      let colSpan = span.colspan
      let startRow = i
      let endRow = i + rowSpan - 1
      let startCol = j
      let endCol = j + colSpan - 1
      for (let r = startRow; r <= endRow; r++) {
        for (let c = startCol; c <= endCol; c++) {
          spanData.push(r + "," + c)
        }
      }
      if (rowSpan > 1) {
        cellXml += ` row-span="${rowSpan}"`
      }
      if (colSpan > 1) {
        cellXml += ` col-span="${colSpan}"`
      }
      if (cellDef.linkUrl && cellDef.linkUrl !== '') {
        cellXml += ` link-url="${cellDef.linkUrl}"`
      }
      if (cellDef.linkTargetWindow && cellDef.linkTargetWindow !== '') {
        cellXml += ` link-target-window="${cellDef.linkTargetWindow}"`;
      }

      cellXml += '>'
      let cellStyle = cellDef.cellStyle
      cellXml += buildCellStyle(cellStyle)
      if (cellDef.linkParameters && cellDef.linkParameters.length > 0) {
        for (let param of cellDef.linkParameters) {
          cellXml += `<link-parameter name="${param.name}">`
          cellXml += `<value><![CDATA[${param.value}]]></value>`
          cellXml += `</link-parameter>`
        }
      }
      const value = cellDef.value
      if (value.type === 'dataset') {
        cellXml += buildDateSetCell(value, cellName)
      } else if (value.type === 'expression') {
        cellXml += buildExpressionCell(value, cellName)
      } else if (value.type === 'simple') {
        cellXml += buildSimpleCell(value, cellName)
      } else if (value.type === 'image') {
        cellXml += buildImageCell(value, cellName)
      } else if (value.type === 'zxing') {
        cellXml += buildZxingCell(value, cellName)
      } else if (value.type === 'slash') {
        const td = hot.getCell(i, j);
        cellXml += await buildSlashCell(td, value, cellName)
      } else if (value.type === 'chart') {
        cellXml += buildChartCell(value, cellName)
      }
      const propertyConditions = cellDef.conditionPropertyItems || [];
      for (let pc of propertyConditions) {
        cellXml += `<condition-property-item name="${pc.name}"`
        const rowHeight = pc.rowHeight
        if (rowHeight !== null && rowHeight !== undefined) {
          cellXml += ` row-height="${rowHeight}"`
        }
        const colWidth = pc.colWidth
        if (colWidth !== null && colWidth !== undefined) {
          cellXml += ` col-width="${colWidth}"`
        }
        if (pc.newValue !== null && colWidth !== undefined) {
          cellXml += ` new-value="${pc.newValue}"`
        }
        if (pc.linkUrl && pc.linkUrl !== '') {
          cellXml += ` link-url="${pc.linkUrl}"`
          let targetWindow = pc.linkTargetWindow || '_self'
          cellXml += ` link-target-window="${pc.linkTargetWindow}"`
        }
        cellXml += `>`
        const paging = pc.paging
        if (paging) {
          cellXml += `<paging position="${paging.position}" line="${paging.line}"/>`;
        }
        if (pc.linkParameters && pc.linkParameters.length > 0) {
          for (let param of pc.linkParameters) {
            cellXml += `<link-parameter name="${param.name}">`
            cellXml += `<value><![CDATA[${param.value}]]></value>`
            cellXml += `</link-parameter>`
          }
        }
        const style = pc.cellStyle
        if (style) {
          cellXml += buildCellStyle(style, true)
        }
        const configConditions = pc.conditions || []
        cellXml += buildConditions(configConditions)
        cellXml += `</condition-property-item>`
      }
      cellXml += '</cell>'
    }
  }
  xml += cellXml
  xml += rowsXml
  xml += columnXml
  const header = context.reportDef.header;
  if (header && (header.left || header.center || header.right)) {
    xml += '<header ';
    if (header.fontFamily) {
      xml += ` font-family="${header.fontFamily}"`
    }
    if (header.fontSize) {
      xml += ` font-size="${header.fontSize}"`
    }
    if (header.forecolor) {
      xml += ` forecolor="${header.forecolor}"`
    }
    if (header.bold) {
      xml += ` bold="${header.bold}"`
    }
    if (header.italic) {
      xml += ` italic="${header.italic}"`
    }
    if (header.underline) {
      xml += ` underline="${header.underline}"`
    }
    if (header.margin) {
      xml += ` margin="${header.margin}"`
    }
    if (header.align) {
      xml += ` align="${header.align}"`
    }
    xml += '>';
    if (header.left) {
      xml += `<left><![CDATA[${header.left}]]></left>`;
    }
    if (header.center) {
      xml += `<center><![CDATA[${header.center}]]></center>`;
    }
    if (header.right) {
      xml += `<right><![CDATA[${header.right}]]></right>`;
    }
    xml += '</header>';
  }
  const footer = context.reportDef.footer;
  if (footer && (footer.left || footer.center || footer.right)) {
    xml += '<footer ';
    if (footer.fontFamily) {
      xml += ` font-family="${footer.fontFamily}"`
    }
    if (footer.fontSize) {
      xml += ` font-size="${footer.fontSize}"`
    }
    if (footer.forecolor) {
      xml += ` forecolor="${footer.forecolor}"`
    }
    if (footer.bold) {
      xml += ` bold="${footer.bold}"`
    }
    if (footer.italic) {
      xml += ` italic="${footer.italic}"`
    }
    if (footer.underline) {
      xml += ` underline="${footer.underline}"`
    }
    if (footer.margin) {
      xml += ` margin="${footer.margin}"`
    }
    if (footer.align) {
      xml += ` align="${footer.align}"`
    }
    xml += '>';
    if (footer.left) {
      xml += `<left><![CDATA[${footer.left}]]></left>`;
    }
    if (footer.center) {
      xml += `<center><![CDATA[${footer.center}]]></center>`;
    }
    if (footer.right) {
      xml += `<right><![CDATA[${footer.right}]]></right>`;
    }
    xml += '</footer>';
  }
  const freeze = context.reportDef.freeze
  if (freeze) {
    xml += `<freeze col="${freeze.col || ''}" row="${freeze.row || ''}"></freeze>`
  }
  let datasourceXml = "";
  const datasources = context.reportDef.datasources
  for (let datasource of datasources) {
    let type = datasource.type
    let ds = `<datasource type="${datasource.type}" name="${encode(datasource.name)}"`
    if (type === 'jdbc') {
      ds+=` username="${encode(datasource.username)}"`
      ds+=` password="${encode(datasource.password)}"`
      ds+=` url="${encode(datasource.url)}"`
      ds+=` driver="${datasource.driver}"`
      ds+= '>';
      const datasets = datasource.datasets || []
      for (let dataset of datasets) {
        ds += `<dataset name="${encode(dataset.name)}" type="sql">`;
        ds += `<sql><![CDATA[${dataset.sql}]]></sql>`;
        for (let field of dataset.fields) {
          ds += `<field name="${field.name}"/>`;
        }
        for (let parameter of dataset.parameters) {
          ds += `<parameter name="${encode(parameter.name)}" type="${parameter.type}" default-value="${encode(parameter.defaultValue)}"/>`;
        }
        ds += `</dataset>`;
      }
    } else if (type === 'spring') {
      ds += ` bean="${datasource.beanId}">`;
      for (let dataset of datasource.datasets) {
        ds += `<dataset name="${encode(dataset.name)}" type="bean" method="${dataset.method}" clazz="${dataset.clazz}">`;
        for (let field of dataset.fields) {
          ds += `<field name="${field.name}"/>`;
        }
        ds += `</dataset>`;
      }
    } else if (type === 'api') {
      ds+=` url="${encode(datasource.url)}"`
      ds+=` method="${encode(datasource.method)}"`
      ds += '>';
      if(datasource.headers) {
        for (let header of datasource.headers) {
          ds += `<header name="${header.name}" value="${header.value}" />`;
        }
      }
      if(datasource.parameters) {
        for (let parameter of datasource.parameters) {
          ds += `<parameter name="${parameter.name}" type="${parameter.type}" default-value="${parameter.defaultValue}"/>`;
        }
      }
      for (let dataset of datasource.datasets) {
        ds += `<dataset name="${encode(dataset.name)}" type="api">`;
        for (let field of dataset.fields) {
          ds += `<field name="${field.name}"/>`;
        }
        ds += `</dataset>`
      }
    } else if (type === 'buildin') {
      ds+=` code="${encode(datasource.code)}"`
      ds += '>';
      for (let dataset of datasource.datasets) {
        ds += `<dataset name="${encode(dataset.name)}" type="sql">`;
        ds += `<sql><![CDATA[${dataset.sql}]]></sql>`;
        for (let field of dataset.fields) {
          ds += `<field name="${field.name}"/>`;
        }
        for (let parameter of dataset.parameters) {
          ds += `<parameter name="${parameter.name}" type="${parameter.type}" default-value="${parameter.defaultValue}"/>`;
        }
        ds += `</dataset>`
      }
    }
    ds += "</datasource>";
    datasourceXml += ds;
  }
  xml += datasourceXml;
  const paper = context.reportDef.paper;
  let htmlIntervalRefreshValue = 0;
  if (paper.htmlIntervalRefreshValue !== null && paper.htmlIntervalRefreshValue !== undefined) {
    htmlIntervalRefreshValue = paper.htmlIntervalRefreshValue;
  }
  xml +=
    `<paper type="${paper.paperType}" left-margin="${paper.leftMargin}" right-margin="${paper.rightMargin}"
    top-margin="${paper.topMargin}" bottom-margin="${paper.bottomMargin}" page-enabled="${paper.pageEnabled}" paging-mode="${paper.pagingMode}" fixrows="${paper.fixRows}"
    width="${paper.width}" height="${paper.height}" orientation="${paper.orientation}" html-report-align="${paper.htmlReportAlign}" bg-image="${paper.bgImage || ''}" print="${paper.print || false}" html-interval-refresh-value="${htmlIntervalRefreshValue}" column-enabled="${paper.columnEnabled}"`;
  if (paper.columnEnabled) {
    xml += ` column-count="${paper.columnCount}" column-margin="${paper.columnMargin}"`;
  }
  xml += `></paper>`;
  const searchForm = context.reportDef.searchForm || {}
  const components = searchForm.components || []
  if (components && components.length > 0) {
    xml += buildSearchForm(components)
  }
  xml += `</ureport>`;
  xml = encodeURIComponent(xml);
  return xml;
};

function getLeftParentCell(context, hot, row, col) {
  if (col < 0) {
    return
  }
  const td = hot.getCell(row, col)
  if (td.style.display === 'none') {
    const mergeCells = hot.getSettings().mergeCells
    for (const item of mergeCells) {
      const rowStart = item.row
      const rowspan = item.rowspan
      const colStart = item.col
      const colspan = item.colspan
      const rowEnd = rowStart + rowspan - 1
      const colEnd = colStart + colspan - 1
      if (row >= rowStart && row <= rowEnd && col >= colStart && col <= colEnd) {
        row = rowStart
        col = colStart
        break
      }
    }
  }
  const cellName = context.getCellName(row, col)
  return cellName
}

function getSpan(hot, row, col) {
  const mergeCells = hot.getSettings().mergeCells || [];
  for (let item of mergeCells) {
    if (item.row === row && item.col === col) {
      return item;
    }
  }
  return {
    rowspan: 0,
    colspan: 0
  };
}

function buildConditions(conditions) {
  let cellXml = ``
  for (let condition of conditions) {
    if(!condition.type) {
      // 数据集-过滤条件
      cellXml += `<condition property="${encode(condition.property || condition.left)}" op="${encode(condition.operation)}"`
      if (condition.join && conditions.length > 1) {
        cellXml += ` join="${condition.join}"`
      }
      cellXml += `>`
      cellXml += `<value><![CDATA[${condition.right}]]></value>`
      cellXml += `</condition>`
    } else if(condition.type === 'expression') {
      // 配置条件-条件规则-表达式
      cellXml += `<condition type="expression" op="${encode(condition.operation)}"`
      if (condition.join && conditions.length > 1) {
        cellXml += ` join="${condition.join}"`
      }
      cellXml += `>`
      cellXml += `<left><![CDATA[${condition.left}]]></left>`
      cellXml += `<right><![CDATA[${condition.right}]]></right>`
      cellXml += `</condition>`
    } else if(condition.type === 'property') {
      // 配置条件-条件规则-属性值
      cellXml += `<condition type="property" op="${encode(condition.operation)}" property="${encode(condition.property || condition.left)}"`
      if (condition.join && conditions.length > 1) {
        cellXml += ` join="${condition.join}"`
      }
      cellXml += `>`
      cellXml += `<value><![CDATA[${condition.right}]]></value>`
      cellXml += `</condition>`
    } else if(condition.type === 'current') {
      // 配置条件-条件规则-当前值
      cellXml += `<condition type="current" op="${encode(condition.operation)}"`
      if (condition.join && conditions.length > 1) {
        cellXml += ` join="${condition.join}"`
      }
      cellXml += `>`
      cellXml += `<value><![CDATA[${condition.right}]]></value>`
      cellXml += `</condition>`
    }
  }
  return cellXml
};

function buildCellStyle(cellStyle, condition) {
  let cellXml = "<cell-style"
  if (condition) {
    cellXml += ` for-condition="true"`
  }

  if (cellStyle.fontSize) {
    cellXml += ` font-size="${cellStyle.fontSize}"`
    if (cellStyle.fontSizeScope) {
      cellXml += ` font-size-scope="${cellStyle.fontSizeScope}"`
    }
  }

  if (cellStyle.forecolor) {
    cellXml += ` forecolor="${cellStyle.forecolor}"`
    if (cellStyle.forecolorScope) {
      cellXml += ` forecolor-scope="${cellStyle.forecolorScope}"`
    }
  }

  if(cellStyle.fontFamily) {
    cellXml += ` font-family="${cellStyle.fontFamily}"`
    if (cellStyle.fontFamilyScope) {
      cellXml += ` font-family-scope="${cellStyle.fontFamilyScope}"`
    }
  }

  if (cellStyle.bgcolor) {
    cellXml += ` bgcolor="${cellStyle.bgcolor}"`
    if (cellStyle.bgcolorScope) {
      cellXml += ` bgcolor-scope="${cellStyle.bgcolorScope}"`
    }
  }

  if (cellStyle.format) {
    cellXml += ` format="${cellStyle.format}"`;
  }
  if (cellStyle.bold !== undefined && cellStyle.bold !== null) {
    cellXml += ` bold="${cellStyle.bold}"`
    if (cellStyle.boldScope) {
      cellXml += ` bold-scope="${cellStyle.boldScope}"`
    }
  }

  if (cellStyle.italic !== undefined && cellStyle.italic !== null) {
    cellXml += ` italic="${cellStyle.italic}"`
    if (cellStyle.italicScope) {
      cellXml += ` italic-scope="${cellStyle.italicScope}"`
    }
  }

  if (cellStyle.underline !== undefined && cellStyle.underline !== null) {
    cellXml += ` underline="${cellStyle.underline}"`
    if (cellStyle.underlineScope) {
      cellXml += ` underline-scope="${cellStyle.underlineScope}"`
    }
  }
  // 默认换行计算
  cellXml += ` wrap-compute="true"`
  // if (cellStyle.wrapCompute !== undefined && cellStyle.wrapCompute !== null) {
  //   cellXml += ` wrap-compute="${cellStyle.wrapCompute}"`
  // }
  if (cellStyle.align && cellStyle.align !== '') {
    cellXml += ` align="${cellStyle.align}"`
    if (cellStyle.alignScope) {
      cellXml += ` align-scope="${cellStyle.alignScope}"`
    }
  }
  if (cellStyle.valign && cellStyle.valign !== '') {
    cellXml += ` valign="${cellStyle.valign}"`
    if (cellStyle.valignScope) {
     cellXml += ` valign-scope="${cellStyle.valignScope}"`
    }
  }
  if (cellStyle.lineHeight) {
    cellXml += ` line-height="${cellStyle.lineHeight}"`
  }
  cellXml += '>';
  let leftBorder = cellStyle.leftBorder;
  if (leftBorder && leftBorder.style !== "none") {
    cellXml += `<left-border width="${leftBorder.width}" style="${leftBorder.style}" color="${leftBorder.color}"/>`;
  }
  let rightBorder = cellStyle.rightBorder;
  if (rightBorder && rightBorder.style !== "none") {
    cellXml += `<right-border width="${rightBorder.width}" style="${rightBorder.style}" color="${rightBorder.color}"/>`;
  }
  let topBorder = cellStyle.topBorder;
  if (topBorder && topBorder.style !== "none") {
    cellXml += `<top-border width="${topBorder.width}" style="${topBorder.style}" color="${topBorder.color}"/>`;
  }
  let bottomBorder = cellStyle.bottomBorder;
  if (bottomBorder && bottomBorder.style !== "none") {
    cellXml +=
      `<bottom-border width="${bottomBorder.width}" style="${bottomBorder.style}" color="${bottomBorder.color}"/>`;
  }
  cellXml += '</cell-style>';
  return cellXml;
};

function buildSearchForm(form) {
  let formXml = '<search-form>'
  for (const index in form) {
    const item = form[index]
    if(item.type === 'datetime') {
      formXml = formXml + `<input-datetime uuid="${item.uuid}" label="${item.label}" type="${item.type}" format="${item.format}" bind-parameter="${item.bindParameter}" default-value="${item.defaultValue}"></input-datetime>`
    } else if(item.type === 'checkbox') {
      formXml = formXml + `<input-checkbox uuid="${item.uuid}" label="${item.label}" type="${item.type}" bind-parameter="${item.bindParameter}">`
      const options = item.options || []
      formXml = formXml + buildOptions(options)
      formXml = formXml + `</input-checkbox>`
    } else if(item.type === 'multiple-select') {
      formXml = formXml + `<input-multiple-select uuid="${item.uuid}" label="${item.label}" type="${item.type}" bind-parameter="${item.bindParameter}"`
      if(item.constant) {
        formXml = formXml +  ` constant="${item.constant}"`
      }
      if(item.dataset) {
        formXml = formXml +  ` dataset="${item.dataset}"`
      }
      if(item.labelField) {
        formXml = formXml +  ` label-field="${item.labelField}"`
      }
      if(item.valueField) {
        formXml = formXml +  ` value-field="${item.valueField}"`
      }
      formXml = formXml +  `>`
      const options = item.options || []
      formXml = formXml + buildOptions(options)
      formXml = formXml + `</input-multiple-select>`
    }  else if(item.type === 'select') {
      formXml = formXml + `<input-select uuid="${item.uuid}" label="${item.label}" type="${item.type}" bind-parameter="${item.bindParameter}"`
      if(item.constant) {
        formXml = formXml +  ` constant="${item.constant}"`
      }
      if(item.dataset) {
        formXml = formXml +  ` dataset="${item.dataset}"`
      }
      if(item.labelField) {
        formXml = formXml +  ` label-field="${item.labelField}"`
      }
      if(item.valueField) {
        formXml = formXml +  ` value-field="${item.valueField}"`
      }
      formXml = formXml +  `>`
      const options = item.options || []
      formXml = formXml + buildOptions(options)
      formXml = formXml + `</input-select>`
    } else if(item.type === 'radio') {
      formXml = formXml + `<input-radio uuid="${item.uuid}" label="${item.label}" type="${item.type}" bind-parameter="${item.bindParameter}">`
      const options = item.options || []
      formXml = formXml + buildOptions(options)
      formXml = formXml + `</input-radio>`
    } else if(item.type === 'text') {
      formXml = formXml + `<input-text uuid="${item.uuid}" label="${item.label}" type="${item.type}" bind-parameter="${item.bindParameter}"`
      formXml = formXml +  `>`
      formXml = formXml + `</input-text>`
    }
  }
  formXml = formXml + '</search-form>'
  return formXml
}

function buildDateSetCell(value, name) {
  if (!value.datasetName) {
    const msg = `${name}单元格数据集属性不能为空！`
    Message({message: msg,type: 'error'})
    throw msg
  }
  if (!value.property) {
    const msg = `${name}单元格属性不能为空！`
    Message({message: msg,type: 'error'})
    throw msg
  }
  if (!value.aggregate) {
    const msg = `${name}单元格聚合方式属性不能为空！`
    Message({message: msg,type: 'error'})
    throw msg
  }
  const mappingType = value.mappingType || 'simple'
  let cellXml = `<dataset-value dataset-name="${encode(value.datasetName)}" aggregate="${value.aggregate}" property="${value.property}" order="${value.order}" mapping-type="${mappingType}"`
  if (mappingType === 'dataset') {
    cellXml += ` mapping-dataset="${value.mappingDataset}" mapping-key-property="${value.mappingKeyProperty}" mapping-value-property="${value.mappingValueProperty}"`
  }
  cellXml += '>'
  if(mappingType === 'simple') {
    const mappingItems = value.mappingItems;
    if (mappingItems && mappingItems.length > 0) {
      for (let mappingItem of mappingItems) {
        cellXml += `<mapping-item value="${encode(mappingItem.value)}" label="${encode(mappingItem.label)}"/>`;
      }
    }
  }
  // 过滤条件
  const filterConditions = value.conditions || []
  cellXml += buildConditions(filterConditions)
  // 自定义分组
  if (value.aggregate === 'customgroup') {
    const groupItems = value.groupItems
    if (!groupItems || groupItems.length === 0) {
      const msg = `${name}单元格自定义分组未配置！`
      Message({message: msg,type: 'error'})
      throw msg
    }
    for (let groupItem of groupItems) {
      cellXml += `<group-item name="${groupItem.name}">`
      for (let condition of groupItem.conditions) {
        cellXml += `<condition property="${condition.left}" op="${encode(condition.operation || condition.op)}"`
        if (condition.join) {
          cellXml += ` join="${condition.join}">`
        } else {
          cellXml += `>`
        }
        cellXml += `<value><![CDATA[${condition.right}]]></value>`
        cellXml += `</condition>`
      }
      cellXml += '</group-item>'
    }
  }
  cellXml += `</dataset-value>`
  return cellXml
}

function buildExpressionCell(value, name) {
  if (!value.value || value.value === '') {
    const msg = `${name}单元格表达式不能为空`
    Message({message: msg,type: 'error'})
    throw msg
  }
  const cellXml = `<expression-value><![CDATA[${value.value}]]></expression-value>`
  return cellXml
}

function buildSimpleCell(value, name) {
  const cellXml = `<simple-value><![CDATA[${value.value || ''}]]></simple-value>`
  return cellXml
}

function buildImageCell(value, name) {
  if (!value.value || value.value === '') {
    const msg = `${name}单元格值不能为空`
    Message({message: msg,type: 'error'})
    throw msg
  }
  let cellXml = `<image-value source="${value.source}"`
  if (value.width) {
    cellXml += ` width="${value.width}"`
  }
  if (value.height) {
    cellXml += ` height="${value.height}"`
  }
  cellXml += `>`;
  cellXml += `<text>`;
  cellXml += `<![CDATA[${value.value}]]>`
  cellXml += `</text>`;
  cellXml += `</image-value>`
  return cellXml
}

function buildZxingCell(value, name) {
  let cellXml = `<zxing-value source="${value.source}" category="${value.category}" width="${value.width}" height="${value.height}"`
  if (value.format) {
    cellXml += ` format="${value.format}"`
  }
  cellXml += `>`;
  cellXml += `<text>`;
  cellXml += `<![CDATA[${value.value}]]>`
  cellXml += `</text>`;
  cellXml += `</zxing-value>`
  return cellXml
}

async function buildSlashCell(td, value, name) {
  let cellXml = `<slash-value>`
  const slashes = value.slashes;
  for (let slash of slashes) {
    cellXml += `<slash text="${slash.text}" x="${slash.x}" y="${slash.y}" degree="${slash.degree}"/>`;
  }
  let slashValue = ''
  const arr = td.getElementsByTagName("div")
  if (arr && arr.length > 0) {
    const svgs = arr[0].getElementsByTagName("svg")
    if (svgs && svgs.length > 0) {
      let res = await saveSvgAsPng.svgAsPngUri(svgs[0], {
        encoderOptions: 1
      }, function(base64Data) {
        slashValue = base64Data
      })
    }
  }
  cellXml += `<base64-data>`
  cellXml += `<![CDATA[${slashValue}]]>`
  cellXml += `</base64-data>`
  cellXml += `</slash-value>`
  return cellXml
}

const chartRules = {
  histogram: {
    name:'柱状图',
    x: '==1',
    y: '>=1',
    message: "需要分类属性，至少1个值属性",
  },
  bar: {
    name:'条形图',
    x: '==1',
    y: '>=1',
    message: "需要分类属性，至少1个值属性",
  },
  line: {
    name:'折线图',
    x: '==1',
    y: '>=1',
    message: "需要分类属性，至少1个值属性",
  },
  area: {
    name:'面积图',
    x: '==1',
    y: '>=1',
    message: "需要分类属性，至少1个值属性",
  },
  pie: {
    name:'饼图',
    x: '>=0',
    y: '>=1',
    message: "需要至少1个值属性",
  },
  doughnut: {
    name:'环形图',
    x: '>=0',
    y: '>=1',
    message: "需要至少1个值属性",
  },
  radar: {
    name:'雷达图',
    x: '>=0',
    y: '>=1',
    message: "需要至少1个值属性",
  },
  scatter: {
    name:'散点图',
    x: '==1',
    y: '>=2',
    message: "需要分类属性，至少2个值属性",
  },
  bubble: {
    name:'气泡图',
    x: '==1',
    y: '>=3',
    message: "需要分类属性，至少3个值属性",
  },
  combo: {
    name:'组合图',
    x: '==1',
    y: '>=1',
    message: "需要分类属性，至少1个值属性",
  }
}

function buildChartCell(value, name) {
  const chart = value.chart
  const dataset = chart.dataset || {}
  const series = dataset.series || []

  const currentX = dataset.categoryProperty ? 1 : 0
  const currentY = series.length

  const rule = chartRules[dataset.type]
  if (!eval(currentX + rule.x + " && " + currentY + rule.y)) {
    const msg = `${name}单元格${rule.name}:${rule.message}`
    Message({message: msg,type: 'error'})
    throw msg
  }
  let cellXml = `<chart-value>`
  cellXml += `<dataset dataset-name="${dataset.datasetName}" type="${dataset.type}"`
  if (dataset.categoryProperty) {
    cellXml += ` category-property="${dataset.categoryProperty}"`
  }
  if (dataset.format) {
    cellXml += ` format="${dataset.format}"`
  }
  cellXml += `>`
  if (dataset.series && dataset.series.length > 0) {
    for (const k in dataset.series) {
      const item = dataset.series[k]
      cellXml += `<series`
      if (item.seriesProperty) {
        cellXml += ` series-property="${item.seriesProperty}"`
      }
      if (item.seriesText) {
        cellXml += ` series-text="${item.seriesText}"`
      }
      if (item.collectType) {
        cellXml += ` collect-type="${item.collectType}"`
      }
      if (dataset.type === 'combo') {
        cellXml += ` type="${item.type || 'bar-group'}"`
        cellXml += ` yAxisIndex="${item.yAxisIndex || false}"`
      }
      if (item.format !== undefined && item.format !== '' && item.format !== null) {
        cellXml += ` format="${item.format}"`
      }
      cellXml += `/>`
    }
  }
  if (dataset.dict) {
    const dict = dataset.dict
    const mappingType = dict.mappingType || 'simple'
    cellXml += `<dict mapping-type="${mappingType}"`;
    if (mappingType === 'dataset') {
      cellXml +=
        ` mapping-dataset="${dict.mappingDataset}" mapping-key-property="${dict.mappingKeyProperty}" mapping-value-property="${dict.mappingValueProperty}"`;
    }
    cellXml += `>`;
    if (mappingType === 'simple') {
      const mappingItems = dict.mappingItems;
      if (mappingItems && mappingItems.length > 0) {
        for (let mappingItem of mappingItems) {
          cellXml +=
          `<mapping-item value="${encode(mappingItem.value)}" label="${encode(mappingItem.label)}"/>`;
        }
      }
    }
    cellXml += `</dict>`
  }
  cellXml += `</dataset>`
  const options = chart.options
  if (options) {
    cellXml += `<options>`
    cellXml += `<![CDATA[${JSON.stringify(options)}]]>`
    cellXml += `</options>`
  }
  // 过滤条件
  const filterConditions = value.conditions || []
  cellXml += buildConditions(filterConditions)
  cellXml += `</chart-value>`
  return cellXml
}


function buildOptions(options) {
  let xml = ''
  for (const index in options) {
    const option = options[index]
    xml = xml + `<option value="${option.value}" label="${option.label}"></option>`
  }
  return xml
}

export function encode(text) {
  let result = text.replace(/[<>&"]/g, function(c) {
    return {
      '<': '&lt;',
      '>': '&gt;',
      '&': '&amp;',
      '"': '&quot;'
    } [c];
  });
  return result
}

export function getParameter(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)")
  var r = window.location.search.substr(1).match(reg)
  if (r != null) return r[2]
  return null
}

export function mmToPoint(mm) {
  let value = mm * 2.834646
  return Math.round(value)
}

export function pointToMM(point) {
  let value = point * 0.352778
  return Math.round(value)
}

export function pointToPixel(point) {
  const value = point * 1.33
  return Math.round(value)
}

export function pixelToPoint(pixel) {
  const value = pixel * 0.75
  return Math.round(value)
}

export function setDirty() {
  $('#__save_btn').removeClass('disabled');
}

export function resetDirty() {
  $('#__save_btn').addClass('disabled');
}

export function formatDate(date, format) {
  if (typeof date === 'number') {
    date = new Date(date);
  }
  if (typeof date === 'string') {
    return date;
  }
  var o = {
    "M+": date.getMonth() + 1,
    "d+": date.getDate(),
    "H+": date.getHours(),
    "m+": date.getMinutes(),
    "s+": date.getSeconds()
  };
  if (/(y+)/.test(format))
    format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp("(" + k + ")").test(format))
      format = format.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
  return format;
};

export function buildPageSizeList() {
  return {
    A0: {
      width: 841,
      height: 1189
    },
    A1: {
      width: 594,
      height: 841
    },
    A2: {
      width: 420,
      height: 594
    },
    A3: {
      width: 297,
      height: 420
    },
    A4: {
      width: 210,
      height: 297
    },
    A5: {
      width: 148,
      height: 210
    },
    A6: {
      width: 105,
      height: 148
    },
    A7: {
      width: 74,
      height: 105
    },
    A8: {
      width: 52,
      height: 74
    },
    A9: {
      width: 37,
      height: 52
    },
    A10: {
      width: 26,
      height: 37
    },
    B0: {
      width: 1000,
      height: 1414
    },
    B1: {
      width: 707,
      height: 1000
    },
    B2: {
      width: 500,
      height: 707
    },
    B3: {
      width: 353,
      height: 500
    },
    B4: {
      width: 250,
      height: 353
    },
    B5: {
      width: 176,
      height: 250
    },
    B6: {
      width: 125,
      height: 176
    },
    B7: {
      width: 88,
      height: 125
    },
    B8: {
      width: 62,
      height: 88
    },
    B9: {
      width: 44,
      height: 62
    },
    B10: {
      width: 31,
      height: 44
    }
  }
}

export const undoManager = new UndoManager();

//防抖
export const debounce = (() => {
  let timer = null
  return (callback, wait = 200) => {
    timer&&clearTimeout(timer)
    timer = setTimeout(callback, wait)
  }
})()
