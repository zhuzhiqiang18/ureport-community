/**
 * Created by Jacky.Gao on 2017-02-07.
 */


export default class RowColWidthHeightDialog {
  constructor(fun, value, isCol){
    let label = isCol ? '列宽' : '行高'
    const ele = document.createElement('div')
    this.dialog = ele
    ele.style.position = 'absolute'
    ele.innerHTML = `
    <div class="v-modal" tabindex="0" style="z-index: 2004;"></div>
    <div class="el-dialog__wrapper" style="z-index: 2005;">
      <div role="dialog" aria-modal="true" class="el-dialog" style="margin-top: 15vh; width: 400px;">
        <div class="el-dialog__header">
          <span class="el-dialog__title">`+ label +`</span>
          <button id="_row_col_width_height_btn_close"  type="button" aria-label="Close" class="el-dialog__headerbtn">
            <i class="el-dialog__close el-icon el-icon-close"></i>
          </button>
        </div>
        <div class="el-dialog__body">
          <form class="el-form">
            <div class="el-form-item">
              <label class="el-form-item__label" style="width: 80px;">`+ label +`:</label>
              <div class="el-form-item__content" style="margin-left: 80px;">
                <div class="el-input el-input--small" style="width: 100%;">
                  <input id="_row_col_width_height_btn_value" type="text" autocomplete="off" value="`+ value +`" class="el-input__inner">
                </div>
              </div>
            </div>
          </form>
        </div>
        <div class="el-dialog__footer"><span class="dialog-footer">
          <button id="_row_col_width_height_btn_cancel" type="button" class="el-button el-button--default el-button--small">
            <span>取 消</span>
          </button>
          <button id="_row_col_width_height_btn_save" type="button" class="el-button el-button--primary el-button--small">
            <span>确 定</span>
          </button>
        </div>
      </div>
    </div>`
    window.document.body.appendChild(ele)
    const that = this
    const cancelButton = document.getElementById('_row_col_width_height_btn_cancel')
    cancelButton.onclick = function() {
      that.dialog.remove()
    }
    const closeButton = document.getElementById('_row_col_width_height_btn_close')
    closeButton.onclick = function() {
      that.dialog.remove()
    }
    const input = document.getElementById('_row_col_width_height_btn_value')
    const saveButton = document.getElementById('_row_col_width_height_btn_save')
    saveButton.onclick = function() {
      const val = parseInt(input.value)
      if(val){
        fun(val)
        that.dialog.remove()
      }
    }
  }
}
