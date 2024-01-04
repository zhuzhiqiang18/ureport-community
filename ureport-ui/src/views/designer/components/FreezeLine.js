/**
 * Freeze Cell Line
 */
export default class FreezeLine {
    constructor(context) {
      context.freezeLine = this
      this.context = context
      this.refresh()
    }
    refresh(){
      const ele = document.getElementById(this.context.elementId)
      const master = ele.children[0]
      const wtHolder = master.children[0]
      const wtHider = wtHolder.children[0]
      const wtSpreader = wtHider.children[0]
      const htBorders = wtSpreader.children[1]
      const printLine = htBorders.children[0]

      const y = printLine.children[2]
      y.style.height = '0px'
      y.style.width = '0px'
      const x = printLine.children[3]
      x.style.height = '0px'
      x.style.width = '0px'
      const freeze = this.context.reportDef.freeze
      if(freeze) {
        const table = this.context.hot.table
        const tobdy = table.children[2]
        if(freeze.row && freeze.row <= tobdy.children.length) {
          const tr = tobdy.children[freeze.row - 1]
          x.style.width = wtSpreader.scrollWidth + 'px'
          x.style.top = tr.offsetTop + tr.offsetHeight + 'px'
          x.style.display = ''
        }
        if(freeze.col && tobdy.children.length > 0) {
          const tr = tobdy.children[freeze.row]
          if(freeze.col && freeze.col < tr.children.length) {
            const td = tr.children[freeze.col]
            y.style.height = wtSpreader.scrollHeight + 'px'
            y.style.left = 2 + td.offsetLeft + td.offsetWidth + 'px'
            y.style.display = ''
          }
        }
      }
    }
};
