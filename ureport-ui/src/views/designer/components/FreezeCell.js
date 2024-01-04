


export default class FreezeCell {
  constructor(options, freeze) {
    this.header = options.header
    this.center = options.center
    this.left = options.left
    this.fixed = options.fixed
    this.handleFreezeCell(freeze)
  }

  handleFreezeCell(freeze) {
    const centerPanel = window.document.getElementById(this.center)
    const tablePanel = centerPanel.children[0]
    const table = tablePanel.children[1]
    const tbody = table.children[0]
    // 固定列
    if(freeze.col) {
      this.handleFreezeCol(table, freeze)
    }
    // 固定行
    if(freeze.row) {
      this.handleFreezeRow(table, freeze)
    }
  }

  handleFreezeCol(table, freeze) {
    const leftPanel = window.document.getElementById(this.left)
    leftPanel.innerHTML = ''
    const leftTablePanel = this.createTabelPanelElement()
    const leftTable = leftTablePanel.children[0]

    const centerPanel = window.document.getElementById(this.center)
    if(centerPanel.scrollWidth > centerPanel.clientWidth) {
      leftPanel.style.paddingBottom = '20px'
      leftPanel.style.height = 'calc(100% - 10px)'
    }
    leftPanel.style.display = ''
    leftPanel.appendChild(leftTablePanel)

    // 交叉部分
    let fixedTable = null
    if(freeze.row) {
      const fixedPanel = window.document.getElementById(this.fixed)
      fixedPanel.innerHTML = ''
      const fixedTablePanel = this.createTabelPanelElement()
      fixedTable = fixedTablePanel.children[0]
      fixedPanel.style.display = ''
      fixedPanel.appendChild(fixedTablePanel)
    }
    const tbody = table.children[0]
    for(var i = 0; i < tbody.children.length; i++) {
      const tr = tbody.children[i]
      const row = tr.cloneNode(true)
      row.innerHTML = ""

      let isFixedRow = false
      for(var j = 0; j < tr.children.length; j++) {
        const td = tr.children[j]
        const colName = td.className.replace(/_/g,'').replace(/[\d]/g,'')
        const tdIndex = this.handleColNameToNumber(colName)
        if(tdIndex <= freeze.col) {
          row.appendChild(td.cloneNode(true))
          if(freeze.row) {
            const index = parseInt(td.className.replace(/_[A-Z]/g,''))
            if(index <= freeze.row) {
              isFixedRow = true
            }
          }
        } else {
          break
        }
      }
      leftTable.appendChild(row)
      if(isFixedRow) {
        fixedTable.appendChild(row.cloneNode(true))
      }
    }
  }

  handleFreezeRow(table, freeze) {
    const headerPanel = window.document.getElementById(this.header)
    headerPanel.innerHTML = ""
    const headerTablePanel = this.createTabelPanelElement()
    const headerTable = headerTablePanel.children[0]
    headerTable.style.width  = table.style.width

    const centerPanel = window.document.getElementById(this.center)
    if(centerPanel.scrollHeight > centerPanel.clientHeight) {
      headerPanel.style.paddingRight = '20px'
      headerPanel.style.width = 'calc(100% - 10px)'
    }
    headerPanel.style.display = ''
    headerPanel.appendChild(headerTablePanel)

    const tbody = table.children[0]
    for(var i = 0; i < tbody.children.length; i++) {
      const tr = tbody.children[i]
      const td = tr.children[0]
      const index = parseInt(td.className.replace(/_[A-Z]/g,''))
      if(freeze.row && index <= freeze.row) {// 冻结行
        headerTable.appendChild(tr.cloneNode(true))
      } else {
        break;
      }
    }
  }

  createTabelPanelElement() {
    const table = window.document.createElement('table')
    table.style.borderCollapse = 'collapse'
    table.style.wordWrap = 'break-word'
    table.style.wordBreak = 'break-all'
    table.style.whiteSpace = 'normal'
    table.style.margin = 'auto'

    const panel = window.document.createElement('div')
    panel.style.float = 'left'
    panel.append(table)
    return panel
  }

  handleColNameToNumber(val) {
    var base = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
    var i = 0
    var j = 0
    var result = 0
    for (var i = 0, j = val.length - 1; i < val.length; i += 1, j -= 1) {
      result += Math.pow(base.length, j) * (base.indexOf(val[i]) + 1)
    }
    return result
  }
}
