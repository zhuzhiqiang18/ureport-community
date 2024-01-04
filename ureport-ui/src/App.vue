<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>

::-webkit-scrollbar {
	width: 8px;
	height: 8px;
	background-color: transparent;
}

::-webkit-scrollbar-track {
	-webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0);
	background-color: #f1f1f1;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	border-radius: 2px;
}

::-webkit-scrollbar-thumb {
	-webkit-border-radius: 2em;
	-moz-border-radius: 2em;
	border-radius: 2em;
	background-color: #c1c1c1;
}

::-webkit-scrollbar-corner {
	background: #eff1f4;
}

div:focus {
	outline: 0
}

div::-webkit-scrollbar,
textarea::-webkit-scrollbar {
	-webkit-appearance: none;
	width: 10px;
	height: 10px
}

div::-webkit-scrollbar-track,
textarea::-webkit-scrollbar-track {
	-webkit-border-radius: 1px;
	-moz-border-radius: 1px;
	border-radius: 1px;
	background-color: rgba(35, 46, 64, .05);
}

div::-webkit-scrollbar-track:hover,
textarea::-webkit-scrollbar-track:hover{
	background-color: rgba(35, 46, 64, .05)
}

div::-webkit-scrollbar-thumb,
textarea::-webkit-scrollbar-thumb {
	border: 2px solid transparent;
	background-clip: padding-box;
	-webkit-border-radius: 10px;
	-moz-border-radius: 10px;
	border-radius: 10px;
	background-color: rgba(35, 46, 64, .3);
}

div::-webkit-scrollbar-thumb:hover,
textarea::-webkit-scrollbar-thumb:hover {
	background-color: rgba(35, 46, 64, .5);
}

div::-webkit-scrollbar-corner,
textarea::-webkit-scrollbar-corner {
	background-color: transparent
}

.el-dialog__header {
  border-bottom: 1px solid #dcdfe6;
}

.el-dialog__body .el-form-item:last-child {
  margin-bottom: 0px;
}

.el-dialog__footer {
  border-top: 1px solid #dcdfe6;
  padding: 10px 15px 10px;
}

.el-header,
.el-footer,
.el-aside,
.el-main {
  margin: 0px;
  padding: 0px;
}

.el-dialog__body {
  padding: 15px;
}

.el-tabs__active-bar,.el-tabs__nav-wrap::after {
  height: 1px;
}

.form-search-box-card .el-card__body {
  padding-bottom: 0px;
}

.chart-tooltip-label {
  font-size:14px;
  color:#666;
  font-weight:400;
  margin-left:2px
}

.chart-tooltip-value {
  float:right;
  margin-left:20px;
  font-size:14px;
  color:#666;
  font-weight:900
}

.tool-bars {
  position: relative;
  padding: 6px 4px;
  overflow: hidden;
  background: #f2f4f7;
  border-top: 1px solid #e2e6ed;
  border-bottom: 1px solid #e2e6ed;
}

.component-icon-btn {
  float: left;
  border-radius: 2px;
  margin: 0px 4px;
  cursor: pointer;
}

.component-dropdown-group {
  position: relative;
}

.component-icon-btn:hover {
  background: #DCDFE6;
}

.component-dropdown-btn:hover,
.component-dropdown-group:hover {
  background: #C0C4CC;
}

.component-icon-btn.selfdefine {
  background: #fff;
}

.component-icon-btn.selfdefine:hover {
  background: #DCDFE6;
}

.component-icon-btn-icon {
  width: 24px;
  height: 24px;
  padding: 4px;
  color: #606266;
}

.component-icon-btn-icon.selected {
  background: #dce1e7;
}

.right-btn {
  position: absolute;
  top: 11px;
  left: 2px;
  width: 0;
  height: 0;
  border-color: #767c85 transparent transparent;
  border-style: solid solid none;
  border-width: 3px 3px 0;
  transition: transform .3s;
}

.component-icon-btn-color {
  position: absolute;
  top: 17px;
  left: 5px;
  width: 14px;
  height: 2px;
}

.tool-bars .component-icon-btn svg,
.tool-bars .component-icon-btn i {
  display: inline-block;
}
</style>
