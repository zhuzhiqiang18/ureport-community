<template>
  <div>
  	<el-row class="property-row-item">
  	  <el-col :span="5" class="property-row-item-label">
  		<el-checkbox v-model="form.checked" style="line-height: 28px;" :disabled="!propertyCondition.name" @change="handleCheck">边框</el-checkbox>
  	  </el-col>
  	  <el-col :span="8" v-if="form.checked" >
  		<el-button size="mini" @click="dialogVisible=true" style="width:100%">配置边框</el-button>
  	  </el-col>
  	</el-row>
  	<el-dialog
  	  title="自定义边框"
  	  append-to-body
  	  :visible.sync="dialogVisible"
  	  width="380px"
  	>
  	  <el-tabs v-model="borderActiveName" stretch>
  	    <el-tab-pane label="上边框" name="top">
  	      <el-form label-width="46px" :model="form.topBorder" size="mini">
  	        <el-form-item label="线型:">
  	          <el-select v-model="form.topBorder.style" placeholder="请选择" @change="handleTopBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderTypeOptions"
  	              :key="item.value"
  	              :label="item.label"
  	              :value="item.value"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="尺寸:">
  	          <el-select v-model="form.topBorder.width" placeholder="请选择" @change="handleTopBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderSizeOptions"
  	              :key="item"
  	              :label="item"
  	              :value="item"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="颜色:">
  	          <colorpicker v-model="form.topBorder.color" @change="handleTopBorder" style="width: 100%;" />
  	        </el-form-item>
  	      </el-form>
  	    </el-tab-pane>
  	    <el-tab-pane label="下边框" name="bottom">
  	      <el-form label-width="46px" :model="form.bottomBorder" size="mini">
  	        <el-form-item label="线型:">
  	          <el-select v-model="form.bottomBorder.style" placeholder="请选择" @change="handleBottomBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderTypeOptions"
  	              :key="item.value"
  	              :label="item.label"
  	              :value="item.value"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="尺寸:">
  	          <el-select v-model="form.bottomBorder.width" placeholder="请选择" @change="handleBottomBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderSizeOptions"
  	              :key="item"
  	              :label="item"
  	              :value="item"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="颜色:">
  	          <colorpicker v-model="form.bottomBorder.color" @change="handleBottomBorder" style="width: 100%;" />
  	        </el-form-item>
  	      </el-form>
  	    </el-tab-pane>
  	    <el-tab-pane label="左边框" name="left">
  	      <el-form label-width="46px" :model="form.leftBorder" size="mini">
  	        <el-form-item label="线型:">
  	          <el-select v-model="form.leftBorder.style" placeholder="请选择" @change="handleLeftBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderTypeOptions"
  	              :key="item.value"
  	              :label="item.label"
  	              :value="item.value"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="尺寸:">
  	          <el-select v-model="form.leftBorder.width" placeholder="请选择" @change="handleLeftBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderSizeOptions"
  	              :key="item"
  	              :label="item"
  	              :value="item"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="颜色:">
  	          <colorpicker v-model="form.leftBorder.color" @change="handleLeftBorder" style="width: 100%;" />
  	        </el-form-item>
  	      </el-form>
  	    </el-tab-pane>
  	    <el-tab-pane label="右边框" name="right">
  	      <el-form label-width="46px" :model="form.rightBorder" size="mini">
  	        <el-form-item label="线型:">
  	          <el-select v-model="form.rightBorder.style" placeholder="请选择" @change="handleRightBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderTypeOptions"
  	              :key="item.value"
  	              :label="item.label"
  	              :value="item.value"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="尺寸:">
  	          <el-select v-model="form.rightBorder.width" placeholder="请选择" @change="handleRightBorder" style="width: 100%;">
  	            <el-option
  	              v-for="item in borderSizeOptions"
  	              :key="item"
  	              :label="item"
  	              :value="item"
  	            />
  	          </el-select>
  	        </el-form-item>
  	        <el-form-item label="颜色:">
  	          <colorpicker v-model="form.rightBorder.color" @change="handleRightBorder" style="width: 100%;" />
  	        </el-form-item>
  	      </el-form>
  	    </el-tab-pane>
  	  </el-tabs>
  	</el-dialog>
  </div>
</template>

<script>

export default {
	props: {
		propertyCondition: {
			type: Object,
			required: true
		}
	},
	data: function () {
	  return {
		dialogVisible: false,
		borderActiveName:'top',
	    form: {
			checked:false,
			leftBorder: {
				color:'rgb(0, 1, 0)',
				width:1,
				style:'solid'
			},
			rightBorder: {
				color:'rgb(0, 1, 0)',
				width:1,
				style:'solid'
			},
			topBorder: {
				color:'rgb(0, 1, 0)',
				width:1,
				style:'solid'
			},
			bottomBorder: {
				color:'rgb(0, 1, 0)',
				width:"1",
				style:'solid'
			}
		},
		borderTypeOptions: [
		  { label: '实线', value: 'solid' },
		  { label: '虚线', value: 'dashed' },
		  { label: '无', value: 'none' }
		],
		borderSizeOptions: [1, 2, 3],
		cellStyle: {}
	  }
	},
	watch : {
		propertyCondition(val){
			const cellStyle = val.cellStyle
			this.init(cellStyle)
		}
	},
	mounted: function () {
		const cellStyle = this.propertyCondition.cellStyle
		this.init(cellStyle)
	},
	methods: {
		init(val) {
			if(val.leftBorder){
				this.form.checked = true
				this.form.leftBorder = JSON.parse(JSON.stringify(val.leftBorder))
				this.form.rightBorder = JSON.parse(JSON.stringify(val.rightBorder))
				this.form.topBorder = JSON.parse(JSON.stringify(val.topBorder))
				this.form.bottomBorder = JSON.parse(JSON.stringify(val.bottomBorder))
			} else {
				this.form.checked = false
				this.form.leftBorder = {color:'rgb(0, 1, 0)',width:1,style:'solid'}
				this.form.rightBorder = {color:'rgb(0, 1, 0)',width:1,style:'solid'}
				this.form.topBorder = {color:'rgb(0, 1, 0)',width:1,style:'solid'}
				this.form.bottomBorder = {color:'rgb(0, 1, 0)',width:1,style:'solid'}
			}
			this.cellStyle = this.propertyCondition.cellStyle
		},
		handleCheck(val) {
			if(val){
				this.cellStyle.leftBorder = this.form.leftBorder
				this.cellStyle.rightBorder = this.form.rightBorder
				this.cellStyle.topBorder = this.form.topBorder
				this.cellStyle.bottomBorder = this.form.bottomBorder
			} else {
				this.cellStyle.leftBorder = null
				this.cellStyle.rightBorder = null
				this.cellStyle.topBorder = null
				this.cellStyle.bottomBorder = null
			}
		},
		handleTopBorder(val) {
			if(this.form.checked){
				this.cellStyle.topBorder = this.form.topBorder
			}
		},
		handleBottomBorder(val) {
			if(this.form.checked){
				this.cellStyle.bottomBorder = this.form.bottomBorder
			}
		},
		handleLeftBorder(val) {
			if(this.form.checked){
				this.cellStyle.leftBorder = this.form.leftBorder
			}
		},
		handleRightBorder(val) {
			if(this.form.checked){
				this.cellStyle.rightBorder = this.form.rightBorder
			}
		},

    }
}
</script>
