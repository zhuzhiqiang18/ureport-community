/**
 * Created by Jacky.Gao on 2017-01-26.
 */
import * as utils from '../Utils.js'
import {afterRenderer} from './CellRenderer.js'
import buildMenuConfigure from './ContextMenu.js'
import Handsontable from 'handsontable'
import { getFileData } from '@/api/designer'

export default class ReportTable {
  constructor(container,callback) {
    this.container = container
    this.hot = new Handsontable(container,{
      startCols:1,
      startRows:1,
      fillHandle:{
        autoInsertRow:false
      },
      height: 60,
      colHeaders:true,
      rowHeaders:true,
      autoColumnSize:false,
      autoRowSize:false,
      manualColumnResize:true,
      manualRowResize:true,
      maxColsNumber:700,
      outsideClickDeselects:false,
    })
    this.buildMenu()
    //处理编辑后赋值问题
    this.hot.addHook("afterChange",function(changes,source) {
      if(!changes || changes.length === 0){
        return
      }
      const arr = changes[0]
      const rowIndex = arr[0]
      const colIndex = arr[1]
      const oldValue = arr[2]
      const newValue = arr[3]
      const cellDef = this.context.getCell(rowIndex,colIndex)
      if (cellDef && oldValue !== newValue) {
        if (cellDef.value && cellDef.value.type === 'simple') {
          cellDef.value.value = newValue
          this.context.hot.setDataAtCell(rowIndex, colIndex, newValue)
          const _this = this
          utils.undoManager.add({
            redo:function(){
              cellDef.value.value = newValue
              _this.context.hot.setDataAtCell(rowIndex, colIndex, newValue)
            },
            undo:function(){
              cellDef.value.value = oldValue
              _this.context.hot.setDataAtCell(rowIndex, colIndex, oldValue)
            }
          })
        }
      }
    })
    this.hot.addHook("afterRenderer",afterRenderer)
    this.cellsMap = new Map()
    this.callback = callback
    this.loadFile("classpath:templates/template.ureport.xml")
    this.hot.addHook('afterRowResize', function(currentRow,newSize) {
      let rowHeights = this.getSettings().rowHeights
      let oldRowHeights = rowHeights.concat([])
      let newRowHeights = rowHeights.concat([])
      newRowHeights.splice(currentRow,1,newSize)
      this.updateSettings({
          rowHeights:newRowHeights,
          manualRowResize:newRowHeights
      })
      const _this = this
      utils.undoManager.add({
        redo:function(){
          rowHeights=_this.getSettings().rowHeights
          oldRowHeights=rowHeights.concat([])
          newRowHeights.splice(currentRow,1,newSize)
          _this.updateSettings({
              rowHeights:newRowHeights,
              manualRowResize:newRowHeights
          })
        },
        undo:function(){
          _this.updateSettings({
              rowHeights:oldRowHeights,
              manualRowResize:oldRowHeights
          })
        }
      })
    })
    this.hot.addHook('afterColumnResize',function(currentColumn,newSize){
      let colWidths=this.getSettings().colWidths;
      let newColWidths=colWidths.concat([]);
      let oldColWidths=colWidths.concat([]);
      newColWidths.splice(currentColumn,1,newSize);
      this.updateSettings({
          colWidths:newColWidths,
          manualColumnResize:newColWidths
      })
      const _this = this
      utils.undoManager.add({
        redo:function(){
            colWidths=_this.getSettings().colWidths;
            newColWidths=colWidths.concat([]);
            oldColWidths=colWidths.concat([]);
            newColWidths.splice(currentColumn,1,newSize);
            _this.updateSettings({
                colWidths:newColWidths,
                manualColumnResize:newColWidths
            })
        },
        undo:function(){
          _this.updateSettings({
              colWidths:oldColWidths,
              manualColumnResize:oldColWidths
          })
        }
      })
    })
  }

  loadFile(file){
    const _this = this
    getFileData({file}).then( res =>{
      const reportDef = res.data
      _this.reportDef = reportDef
      _this._buildReportData(reportDef)
      if(this.callback){
        this.callback.call(_this,reportDef)
      }
      _this.hot.render()
    })
  }

  loadReportDef(reportDef){
    this._buildReportData(reportDef)
    if(this.callback){
      this.callback.call(this,reportDef)
    }
    this.hot.render()
  }

  _buildReportData(data){
    this.cellsMap.clear();
    const rows=data.rows || [];
    for(let i = rows.length + 1; i< 41;i++) {
        rows.push({
        rowNumber:i,
        height:18
      })
    }
    const rowHeights=[];
    for(let row of rows){
        const height=row.height;
        rowHeights.push(utils.pointToPixel(height));
    }
    const columns = data.columns;
    for(let i = columns.length + 1; i< 18;i++) {
        columns.push({
        columnNumber: i,
        hide: false,
        width: 80
      })
    }
    const colWidths=[];
    for(let col of columns){
      const width=col.width;
      colWidths.push(utils.pointToPixel(width))
    }
    const cellsMap = data.cellsMap;
    const dataArray = [],mergeCells = [];
    for(let row of rows) {
      const rowData = [];
      for(let col of columns) {
        let key=row.rowNumber+","+col.columnNumber;
        let cell=cellsMap[key];
        if(cell){
            this.cellsMap.set(key,cell);
            rowData.push(cell.value.value || "");
            let rowspan=cell.rowSpan,colspan=cell.colSpan;
            if(rowspan>0 || colspan>0){
              if(rowspan===0)rowspan=1;
              if(colspan===0)colspan=1;
              mergeCells.push({
                  rowspan,
                  colspan,
                  row:row.rowNumber-1,
                  col:col.columnNumber-1
              });
            }
        } else {
          cell = {
            cellStyle: {
              align: 'left',
              fontSize: 10,
              fontFamily: '宋体',
              lineHeight: 0,
              valign: 'middle'
            },
            colSpan: 0,
            rowSpan: 0,
            columnNumber: col.columnNumber,
            rowNumber: row.rowNumber,
            expand: "None",
            fillBlankRows: false,
            multiple: 0,
            name: "",
            value: {
              type:'simple',
              value:''
            }
          }
          this.cellsMap.set(key,cell);
          rowData.push('');
        }
      }
      dataArray.push(rowData);
    }
    this.hot.loadData(dataArray);
    this.hot.updateSettings({
        colWidths,
        rowHeights,
        mergeCells,
        readOnly:false
    });
  }

  buildMenu(){
    this.hot.updateSettings({
      contextMenu: buildMenuConfigure()
    })
  }

  bindSelectionEvent(callback){
    const _this=this;
    Handsontable.hooks.add("afterSelectionEnd",function(rowIndex,colIndex,row2Index,col2Index){
      callback.call(_this,rowIndex,colIndex,row2Index,col2Index);
    },this.hot)
  }
}
