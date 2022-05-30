<template>
  <div>
    <div class="bt">

      <el-breadcrumb separator="/">
        <el-breadcrumb-item>root</el-breadcrumb-item>
      </el-breadcrumb>
      <div style="height: 15px"></div>
      <!--      <el-button type="text" size="medium" icon="el-icon-arrow-left" @click="back">返回</el-button>-->
      <!--      <el-button type="primary" size="medium" icon="el-icon-upload2" @click="dialogVisible = true">上传文件</el-button>-->
      <UploadFile id="uploadfile" :parentInode="curInode"></UploadFile>
      <el-button @click="addfolder = true" class="el-icon-folder-add">新建文件夹</el-button>


      <el-dialog
        title="输入文件夹名字"
        :visible.sync="addfolder"
        width="20%"
      >
        <el-input v-model="input" placeholder="请输入内容"></el-input>
        <span slot="footer" class="dialog-footer">
            <el-button @click="addfolder = false">取 消</el-button>
           <el-button type="primary" @click="newFile">确 定</el-button>
        </span>
      </el-dialog>

      <div id="search"><input placeholder="请输入内容" class="search" v-model="keywords"/><i class="el-icon-search"></i>
      </div>
    </div>
    <div style="height: 15px"></div>
    <el-table
      :data="tableData"
      style="width: 100%;margin-bottom: 20px;"
      row-key="inode"
      >
      <el-table-column prop="dir" width="60" align="center">
      </el-table-column>
      <el-table-column prop="dir" width="60" align="center">
        <template slot-scope="scope">
          <img class='image' v-if="scope.row.type==='FILE'" src="../assets/afile.png" style="height: 30px;max-height: 100%;max-width: 100%">
          <img class='image' v-else src="../assets/folder.png" style="height: 30px;max-height: 100%;max-width: 100%">
        </template>
      </el-table-column>
      <el-table-column
        prop="filename"
        label="文件名"
        width="300">
      </el-table-column>
      <el-table-column
        prop="size"
        label="大小"
        width="270">
      </el-table-column>
      <el-table-column
        prop="time"
        label="修改时间"
        width="220">
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="download(scope.$index, scope.row)">下载
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="deleteFile(scope.$index, scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>


  </div>

</template>

<script>
import {newFolder, dateToString} from "../assets/js/pbkdf";
import request from "../assets/js/request";
import UploadFile from "./UploadFile";

export default {
  name: 'Right',
  components: {
    UploadFile,
  },
  data() {
    return {
      input: '',
      height: window.innerHeight - 62 - 80 - 40,
      keywords: '',
      dialogVisible: false,
      loading: false,
      path: '/',
      username: localStorage.getItem('name'),
      addfolder: false,
      fileData: '',
      tableData: [],
      curInode: null,
      userId: null,
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      let _this = this
      this.userId = localStorage.getItem("uid")
      var root = null
      request.get("/files/" + _this.userId).then(function (res) {
        console.log(JSON.stringify(res))
        _this.tableData = res.data

      })
      // 默认curInode 是 0
      this.curInode = 0
    },
    // 下载任务
    download(index, row) {

    },
    // 删除文件
    deleteFile(index, row) {

    },
    // 新增文件
    newFile() {

    },


    next(name) {
      var newpath = localStorage.getItem('path') + name + '/'
      this.path = newpath
      this.$http.post(this.$HOST + 'v2/filelist', this.$qs.stringify({
        sign: this.$sign,
        username: localStorage.getItem('name'),
        path: newpath

      })).then(res => {
        localStorage.setItem('path', newpath)
        this.tableData = []
        res.data.data.dir.forEach(item => {
          if (item.size == '') {
            var size = '-'
          } else {
            if (item.size < 1048576) {
              var size = (item.size / 1024).toFixed(2) + 'KB'
            } else if (item.size > 1048576 && item.size < 1073741824) {
              var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
            } else if (item.size > 1073741824) {
              var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
            }
          }
          this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
        })
        res.data.data.file.forEach(item => {
          if (item.size == '') {
            var size = '-'
          } else {
            if (item.size < 1048576) {
              var size = (item.size / 1024).toFixed(2) + 'KB'
            } else if (item.size > 1048576 && item.size < 1073741824) {
              var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
            } else if (item.size > 1073741824) {
              var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
            }
          }
          this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
        })
      })


    },
    back() {
      // console.log( localStorage.getItem('path').split('/'))
      var str = localStorage.getItem('path').split('/')
      str.splice(0, 1)
      str.splice(str.length - 1, 1)
      str.splice(str.length - 1, 1)
      var backpath = '/'
      str.forEach(item => {
        backpath += item + '/'
      })
      this.path = backpath
      this.$http.post(this.$HOST + 'v2/filelist', this.$qs.stringify({
        sign: this.$sign,
        username: localStorage.getItem('name'),
        path: backpath

      })).then(res => {
        localStorage.setItem('path', backpath)
        this.tableData = []
        res.data.data.dir.forEach(item => {
          if (item.size == '') {
            var size = '-'
          } else {
            if (item.size < 1048576) {
              var size = (item.size / 1024).toFixed(2) + 'KB'
            } else if (item.size > 1048576 && item.size < 1073741824) {
              var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
            } else if (item.size > 1073741824) {
              var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
            }
          }
          this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
        })
        res.data.data.file.forEach(item => {
          if (item.size == '') {
            var size = '-'
          } else {
            if (item.size < 1048576) {
              var size = (item.size / 1024).toFixed(2) + 'KB'
            } else if (item.size > 1048576 && item.size < 1073741824) {
              var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
            } else if (item.size > 1073741824) {
              var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
            }
          }
          this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
        })
      })
    }
  }
}
</script>
<style scoped>

.bt {
  max-width: 100%;
  background-color: white;
  height: 40px;
  font: 12px/1.5 "Microsoft YaHei", arial, SimSun, "宋体";
  line-height: 30px;
}


#search {
  width: 315px;
  border-radius: 33px;
  background-color: #f7f7f7;
  float: right;
  text-align: center;
  height: 32px;
  line-height: 32px;

}

.search {
  border: none;
  background-color: #f7f7f7;
  height: 30px;
  width: 248px;
}

img {
  width: 30px;
  height: 30px;
}

a {
  color: #424e67;
  text-decoration: none;
}

a:hover {
  color: #09AAFF;
}

.el-icon-delete {
  color: #F56C6C;
}

#uploadfile {
  display: inline-block
}
</style>
