<template>
  <el-dialog
    title="添加数据源"
    :visible.sync="buildinDataSetValue.dialogVisible"
    append-to-body
    :close-on-click-modal="false"
    width="600px"
  >
    <el-table
      :data="tableData"
      border
      size="mini"
      height="300"
      :header-cell-style="()=>'background: #f5f7fa'"
      style="width: 100%">
      <el-table-column
        prop="name"
        label="数据源名称">
      </el-table-column>
      <el-table-column
        label="操作"
        width="60"
        align="center">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="addDataSource(scope.row)"><span style="font-size: 12px;">添加</span></el-link>
        </template>
      </el-table-column>
    </el-table>
    <!-- <span slot="footer" class="dialog-footer">
      <el-button size="small" type="primary" @click="handleTestConnection">测试连接</el-button>
      <el-button size="small" type="primary" @click="addDataSource">确 定</el-button>
    </span> -->
  </el-dialog>
</template>

<script>
import { selectBuildinDruidList } from '@/api/datasource'
export default {
  props: {
    buildinDataSetValue: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {
      tableData: []
    }
  },
  mounted: function() {
    this.init()
  },
  methods: {
    init() {
      selectBuildinDruidList().then(res => {
        this.tableData = res.data || []
      })
    },
    addDataSource(row) {
      const datasources = this.buildinDataSetValue.context.reportDef.datasources || []
      const obj = datasources.find(item => item.name === row.name)
      if (obj) {
        this.$message.error('数据源名称重复')
        return
      }
      datasources.push({
        code: row.code,
        name:row.name,
        type: 'buildin',
        driver: null,
        url: null,
        username: null,
        password: null,
        datasets: []
      })
      this.buildinDataSetValue.dialogVisible = false
    }
  },

}
</script>
