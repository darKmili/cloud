<template>
  <div>
    <el-header>
      <UploadFile id="uploadfile" :parentInode="curInode"></UploadFile>
      <el-button @click="addfolder = true" class="el-icon-folder-add">新建文件夹</el-button>
      <div id="search"><input placeholder="请输入内容" class="search" v-model="keywords"/><i class="el-icon-search"></i>
      </div>
      <div style="height: 15px"></div>
    </el-header>

    <div class="middle-wrapper" style="padding: 10px">
      <!-- 面包屑导航栏 -->

      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/Home',}"><a @click="back">返回</a></el-breadcrumb-item>
        <el-breadcrumb-item
          v-for="(item, index) in breadlist"
          :key="index"
        >{{ item.name }}
        </el-breadcrumb-item>

      </el-breadcrumb>

    </div>

    <el-dialog
      title="输入文件夹名字"
      :visible.sync="addfolder"
      width="20%"
    >
      <el-input v-model="input" placeholder="请输入内容"></el-input>
      <span slot="footer" class="dialog-footer">
            <el-button @click="addfolder = false">取 消</el-button>
           <el-button type="primary" @click="newFolder">确 定</el-button>
        </span>
    </el-dialog>

    <el-table
      :data="tableData"
      style="width: 100%;margin-bottom: 20px;"
      row-key="inode"

    >
      <el-table-column prop="dir" width="60" align="center">
      </el-table-column>
      <el-table-column prop="dir" width="60" align="center">
        <template slot-scope="scope">
          <img class='image' v-if="scope.row.type==='FILE'" src="../assets/afile.png"
               style="height: 30px;max-height: 100%;max-width: 100%">
          <img class='image' v-else src="../assets/folder.png" style="height: 30px;max-height: 100%;max-width: 100%">
        </template>

      </el-table-column>
      <el-table-column
        prop="filename"
        label="文件名"
        width="300">
        <template slot-scope="scope">
          <div style="cursor:pointer;" @click="clickFolder(scope.row)">
            {{ scope.row.filename }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="size"
        label="大小"
        width="270">
      </el-table-column>
      <el-table-column
        prop="mtime"
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
import {
  newFolder,
  encryptKey,
  dateToString,
  stringtoUint8Array,
  dec,
  showfilesize,
  uint8ArrayToString
} from "../assets/js/pbkdf";
import request from "../assets/js/request";
import UploadFile from "./UploadFile";
import {download} from "../assets/js/download";
//解密列表数据1
async function encryptlist(tdata) {
  let clientRandomValue = stringtoUint8Array(localStorage.getItem('clientRandomValue'));
  const masterKey = stringtoUint8Array(localStorage.getItem('masterKey'));
  for (var i = 0; i < tdata.length; i++) {


    var encryptedfileKey = stringtoUint8Array(tdata[i].fileKey)
    var fileKey = await dec(masterKey, clientRandomValue, encryptedfileKey)
    var encryptedfilename = stringtoUint8Array(tdata[i].filename)
    var encryptedmtime = stringtoUint8Array(tdata[i].mtime)
    console.log(encryptedfilename)
    console.log(encryptedmtime)
    console.log(fileKey)
    //文件名解密出问题 TODO
    // var filename = await dec(fileKey, clientRandomValue, encryptedfilename)
    //     console.log("文件名：" + filename)
    //     tdata[i].filename=uint8ArrayToString(filename)
    //     var mtime = await dec(fileKey, clientRandomValue, encryptedmtime)
    //     console.log("mtime：" + mtime)
    //     tdata[i].mtime=dateToString(uint8ArrayToString(mtime).toDate())
    if (tdata[i].type === "DIR") {

      tdata[i].size = '-'
    } else {
      tdata[i].size = showfilesize(tdata[i].size)
    }

  }
  return tdata
}

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
      fromData: [],
      breadlist: []
    }
  },
  created() {
    this.init()
  },
  methods: {
    async init() {
      let _this = this
      this.userId = localStorage.getItem("uid")

      let tdata = []
      await request.get("/files/" + _this.userId).then(function (res) {
        console.log(JSON.stringify(res))

        tdata = res.data

      })
      console.log(JSON.stringify(tdata))
      this.fromData = tdata
      _this.tableData = await encryptlist(tdata)

      // 默认curInode 是 0
      this.curInode = 0
    },
    // 下载任务
    download(index, row) {
      var userId = this.userId
      download({
        userId,
        row
      })
    },
    // 删除文件
    deleteFile(index, row) {

    },
    // 新增文件
    async newFolder() {
      this.userId = localStorage.getItem("uid")
      let clientRandomValue = stringtoUint8Array(localStorage.getItem('clientRandomValue'));
      const masterKey = stringtoUint8Array(localStorage.getItem('masterKey'));
      let name = this.input
      console.log(name)
      for (var i = 0; i < this.tableData.length; i++) {
        if (this.tableData[i].filename == name) {
          name = name + '.1'
        }
      }
      //文件名，创建时间加密发送

      var folderKey = new Uint8Array(2 ** 4);

      let data1 = stringtoUint8Array(name)
      console.log("fileName:" + data1);
      var encryptedMasterKeyHashValue1 = await encryptKey(folderKey, clientRandomValue, data1)
      var encryptedData1 = new Uint8Array(encryptedMasterKeyHashValue1)
      console.log("encryptedData1:" + encryptedData1)

      var Mtime = new Date()
      var data2 = stringtoUint8Array(Mtime.toString())
      console.log("Mtime:" + data2);
      var encryptedMasterKeyHashValue2 = await encryptKey(folderKey, clientRandomValue, data2)
      var encryptedData2 = new Uint8Array(encryptedMasterKeyHashValue2)
      console.log("encryptedData2:" + encryptedData2)

      var encryptedMasterKeyHashValue = await encryptKey(masterKey, clientRandomValue, folderKey)
      var encryptedkey = new Uint8Array(encryptedMasterKeyHashValue)
      console.log("encryptedkey:" + encryptedkey)

      //发送后端加密文件名，mtime，用主密钥加密的密钥 TODO

      await request.post("/files/" + this.userId + "/" + this.curInode, JSON.stringify({
          "filename": uint8ArrayToString(encryptedData1),
          "size": 0,
          "mtime": uint8ArrayToString(encryptedData2),
          "fileKey": uint8ArrayToString(encryptedkey),
          "type": "DIR"
        })
      ).then(function (res) {

        alert("请求后端成功" + JSON.stringify(res))
        if (res.code === 2000) {

          console.log(JSON.stringify(res.data))
        }
      })

      //文件夹展示到当前目录
      // var time=dateToString(Mtime)
      // this.tableData.push({filename: name, mtime: time, size:'-',type:"DIR"})


    },


    async clickFolder(row) {
      if (row.type === "DIR") {
        this.tableData = await encryptlist(row.childrenFiles)
        this.breadlist.push({"name": row.filename, "inode": row.inode, "parent_inode": this.curInode})
        this.curInode = row.inode
      }
    },
    async back() {
      let b = this.fromData
      for (var i = 0; i < this.breadlist.length - 1; i++) {
        for (var j = 0; j < b.length; j++) {
          if (this.breadlist[i].inode === b[j].inode) {
            b = b[j].childrenFiles
          }
        }
      }
      this.tableData = b
      this.breadlist.pop()
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
