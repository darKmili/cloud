<template>
  <el-table
    :data="tableData"
    style="width: 100%"
    :row-class-name="tableRowClassName">
    <el-table-column
      prop="date"
      label="日期"
      width="180">
    </el-table-column>
    <el-table-column
      prop="name"
      label="发送者"
      width="180">
    </el-table-column>
    <el-table-column
      prop="file"
      label="文件">
    </el-table-column>
  </el-table>
</template>

<style>
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>

<script>

import request from "../assets/js/request";
import {encryptlist} from "../assets/js/pbkdf";

export default {
  methods: {
    tableRowClassName({row, rowIndex}) {
      if (rowIndex === 1) {
        return 'warning-row';
      } else if (rowIndex === 3) {
        return 'success-row';
      }
      return '';
    },

    async init() {
      let _this = this
      this.userId = localStorage.getItem("uid")

      let tdata = []
      await request.get("/files/" + _this.userId+"/share/1").then(function (res) {
        console.log(JSON.stringify(res))
        if (res.code != null && res.code !== 2000) {
          alert(res.message);
        }
        tdata = res.data
      })

      console.log(JSON.stringify(tdata))
      this.fromData = tdata
      // 默认curInode 是 0
      this.curInode = 0
      _this.tableData = await encryptlist(_this.type,tdata, _this)

    }
  },
  // 创建时，向后端发起请求，获取分享文件的信息
  created() {

  }
  ,
  data() {
    return {
      tableData: [{
        date: '2016-05-02',
        name: '王小虎',
        file: '上海市普陀区金沙江路 1518 弄',
      }, {
        date: '2016-05-04',
        name: '王小虎',
        file: '上海市普陀区金沙江路 1518 弄'
      }, {
        date: '2016-05-01',
        name: '王小虎',
        file: '上海市普陀区金沙江路 1518 弄',
      }, {
        date: '2016-05-03',
        name: '王小虎',
        file: '上海市普陀区金沙江路 1518 弄'
      }]
    }
  }
}
</script>
