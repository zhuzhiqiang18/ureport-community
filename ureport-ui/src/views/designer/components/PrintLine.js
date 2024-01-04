/**
 * Created by Jacky.Gao on 2017-02-18.
 */
import {pointToPixel} from './Utils.js'

export default class PrintLine {
    constructor(context) {
      context.printLine = this
      this.context = context
      this.refresh()
    }
    refresh(){
      const paper = this.context.reportDef.paper
      const orientation=paper.orientation
      let width = paper.width
      let height = paper.height
      if(orientation ==='landscape'){
          width = paper.height
          height = paper.width
      }
      width = pointToPixel(width - paper.leftMargin - paper.rightMargin) + 50
      height = pointToPixel(height - paper.topMargin - paper.bottomMargin) + 25.5
      const ele = document.getElementById(this.context.elementId)
      const master = ele.children[0]
      const wtHolder = master.children[0]
      const wtHider = wtHolder.children[0]
      const wtSpreader = wtHider.children[0]
      const htBorders = wtSpreader.children[1]
      const printLine = htBorders.children[0]
      const y = printLine.children[0]
      y.style.width = '0px'
      if(width > wtSpreader.scrollWidth) {
        y.style.height = '0px'
      } else {
        y.style.left = (width + 1) + 'px'
        if(height > wtSpreader.scrollHeight) {
          y.style.height = (wtSpreader.scrollHeight + 10) + 'px'
        } else {
          y.style.height = (height + 10) + 'px'
        }
      }

      const x = printLine.children[1]
      x.style.height = '0px'
      if(height > wtSpreader.scrollHeight) {
        x.style.width = '0px'
      } else {
        x.style.top = height + 'px'
        if(width > wtSpreader.scrollWidth) {
          y.style.width = (wtSpreader.scrollWidth + 10) + 'px'
        } else {
          x.style.width = (width + 10) + 'px'
        }
      }
    }
};
