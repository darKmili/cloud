<template>
  <div>
    <el-header>
      <UploadFile id="uploadfile" :parentInode="curInode" :tableData="tableData"></UploadFile>
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
          :to="{ path: ''}"
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
            type="danger"
            @click="deleteFile(scope.$index, scope.row)">删除
          </el-button>
          <el-button
            size="mini"
            @click="download(scope.$index, scope.row)" v-if="scope.row.type==='FILE'">下载
          </el-button>
          <el-button
            size="mini"
            @click="" v-if="scope.row.type==='FILE'">分享
          </el-button>
        </template>
      </el-table-column>

      <el-table-column>
        <template slot-scope="scope">
          <el-progress :percentage="percentage" :text-inside="true" :stroke-width="22"
                       v-if="clickingIdx!==null&& clickingIdx===scope.$index "></el-progress>
        </template>
      </el-table-column>
    </el-table>
  </div>

</template>

<script>

import {dateToString, dec, decryptKey, encryptKey, stringtoUint8Array, uint8ArrayToString} from "../assets/js/pbkdf";
import request from "../assets/js/request";
import UploadFile from "./UploadFile";
import axios from "axios";

//解密列表数据1
async function encryptlist(type,tdata, _this) {

  if (tdata == null) {
    _this.$router.push("/");
    alert("请登录")
    return null
  }
  var tablelist = new Set()
  let clientRandomValue = stringtoUint8Array(localStorage.getItem('clientRandomValue'));
  const masterKey = stringtoUint8Array(localStorage.getItem('masterKey'));
  var suffixes = null
  if (type==='Camera'){
     suffixes = ['AVI','MOV','RMVB','RM','MP4']
  }else if (type==='Document'){
     suffixes = ['DOC','DOCX','PPT','XLS','WPS','PDF','CSV','TXT']
  }else if(type==='Music'){
     suffixes = ['MP3','WMA','WAV','MID']
  }else if(type==='Picture'){
     suffixes = ['JPG','JPEG','PNG','GIF']
  }

  if (suffixes===null){

    for (var i = 0; i < tdata.length; i++) {
      tdata[i].percentage = 0
      if (tdata[i].type === 'DIR') {
        tdata[i].size = '-'
      } else {
        var unit = 'B'
        var size = tdata[i].size
        if (size > 1024) {
          size = Math.ceil(size / 1024)
          unit = 'KB'
        }
        if (size > 1024) {
          size = Math.ceil(size / 1024)
          unit = 'MB'
        }
        if (size > 1024) {
          size = Math.ceil(size / 1024)
          unit = 'GB'
        }
        tdata[i].size = size + unit
      }
      var encryptedfileKey = stringtoUint8Array(tdata[i].fileKey)
      // 解密文件密钥
      var fileKey = await dec(masterKey, clientRandomValue, encryptedfileKey)
      tdata[i].fileKey = uint8ArrayToString(new Uint8Array(fileKey))

      var encryptedfilename = stringtoUint8Array(tdata[i].filename)
      var encryptedmtime = stringtoUint8Array(tdata[i].mtime)

      var filename = await dec(fileKey, clientRandomValue, encryptedfilename)
      console.log("文件名：" + filename)
      tdata[i].filename = new TextDecoder().decode(filename);
      var mtime = await dec(fileKey, clientRandomValue, encryptedmtime)
      console.log("mtime：" + mtime)
      let mtimeDate = new Date(new TextDecoder().decode(mtime));
      tdata[i].mtime = dateToString(mtimeDate)

    }
    return tdata

  }else {
    for (var i = 0; i < tdata.length; i++) {
      tdata[i].percentage = 0
      var encryptedfileKey = stringtoUint8Array(tdata[i].fileKey)
      // 解密文件密钥
      var fileKey = await dec(masterKey, clientRandomValue, encryptedfileKey)
      tdata[i].fileKey = uint8ArrayToString(new Uint8Array(fileKey))

      var encryptedfilename = stringtoUint8Array(tdata[i].filename)
      var encryptedmtime = stringtoUint8Array(tdata[i].mtime)

      var filenameCode = await dec(fileKey, clientRandomValue, encryptedfilename)
      console.log("文件名：" + filename)

      tdata[i].filename = new TextDecoder().decode(filenameCode);
      var filename = tdata[i].filename
      var suffix =  filename.substring(filename.lastIndexOf('.')+1,filename.length)
      console.log(suffix)
      if  (suffixes.includes(suffix.toUpperCase())){
        var mtime = await dec(fileKey, clientRandomValue, encryptedmtime)
        console.log("mtime：" + mtime)
        let mtimeDate = new Date(new TextDecoder().decode(mtime));
        tdata[i].mtime = dateToString(mtimeDate)
        if(tdata[i].type!=='DIR'){
          var unit = 'B'
          var size = tdata[i].size
          if (size>=1024){
            size=Math.ceil( size/1024)
            unit = 'KB'
          }
          if (size>=1024){
            size=Math.ceil( size/1024)
            unit = 'MB'
          }
          if (size>=1024){
            size=Math.ceil( size/1024)
            unit = 'GB'
          }
          tdata[i].size = size+unit
        }
        tablelist.add(tdata[i])
      }
    }

    console.log(tablelist)
    return Array.from(tablelist)
  }




}

export default {
  name: 'Right',
  props:{
    type:null
  },
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
      breadlist: [],
      clickingIdx: null,
      percentage: 0
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

    },
    // 下载任务
    async download(index, row) {

      var fileKey = stringtoUint8Array(row.fileKey)
      var clientRandomValue = stringtoUint8Array(localStorage.getItem("clientRandomValue"))
      var filename = row.filename
      let _this = this
      _this.clickingIdx = index
      _this.percentage = 0
      await request.get(
        "/files/" + this.userId + "/" + row.inode + "/blocks"
      ).then(async function (res) {
        console.log(JSON.stringify(res))
        if (res.code != null && res.code !== 2000) {
          alert(res.message);
        }

        // 创建一个数组
        let blocks = new Array(res.data.length)
        var num = 0
        for (var j in res.data) {
          var blockUrl = res.data[j]
          await axios.get(blockUrl, {responseType: 'arraybuffer'}).then(async function (block) {
            blocks[j] = await decryptKey(fileKey, clientRandomValue, block.data)
            num++
            _this.percentage = parseInt(num / res.data.length * 100)
          })

        }

        let blob = new Blob(blocks, {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"});
        var endTime = new Date()
        // console.log("下载时间: " + (parseInt(endTime - startTime)).toString()+ "ms，下载大小："+dataSize+"B")
        if (window.navigator.msSaveOrOpenBlob) {
          // IE10+下载
          navigator.msSaveOrBlob(blob, filename);
        } else {
          // 非IE10+下载
          let link = document.createElement('a');
          link.href = window.URL.createObjectURL(blob);
          link.download = filename;
          document.body.appendChild(link);
          var evt1 = document.createEvent("MouseEvents");
          evt1.initEvent("click", false, false);
          link.dispatchEvent(evt1);//释放URL 对象
          document.body.removeChild(link);
        }
      })
    },
    // 删除文件
    deleteFile(index, row) {
      let _this = this
      request.delete("/files/" + this.userId + "/" + row.inode).then(function (res) {
        if (res.code === 2000) {
          // alert("请求后端成功" + JSON.stringify(res))
          _this.tableData.splice(index, 1)
        }
      })
    },
    // 新增文件夹
    async newFolder() {
      var _this = this
      let textEncoder = new TextEncoder();
      this.userId = localStorage.getItem("uid")
      let clientRandomValue = stringtoUint8Array(localStorage.getItem('clientRandomValue'));
      const masterKey = stringtoUint8Array(localStorage.getItem('masterKey'));

      let name = this.input
      for (var i = 0; i < this.tableData.length; i++) {
        if (this.tableData[i].filename === name) {
          name = name + '.1'
        }
      }
      //文件名，创建时间等加密发送
      // 生成随机的文件密钥 16位，然后将文件密钥加密
      var folderKey = new Uint8Array(2 ** 4);
      window.crypto.getRandomValues(folderKey)

      var folderKeyEd = await encryptKey(masterKey, clientRandomValue, folderKey)


      var Mtime = new Date()
      var data2 = stringtoUint8Array(Mtime.toString())
      console.log("Mtime:" + data2);
      var encryptedMasterKeyHashValue2 = await encryptKey(folderKey, clientRandomValue, data2)
      var encryptedData2 = new Uint8Array(encryptedMasterKeyHashValue2)
      console.log("encryptedData2:" + encryptedData2)
      let encodeName = textEncoder.encode(name);
      var filenameEd = await encryptKey(folderKey, clientRandomValue, encodeName)

      //发送后端加密文件名，mtime，用主密钥加密的密钥 TODO
      await request.post("/files/" + this.userId + "/" + this.curInode, JSON.stringify({
          "filename": uint8ArrayToString(new Uint8Array(filenameEd)),
          "size": 0,
          "mtime": uint8ArrayToString(encryptedData2),
          "fileKey": uint8ArrayToString(new Uint8Array(folderKeyEd)),
          "type": "DIR",
          "state": "UPLOADED",
        })
      ).then(async function (res) {

        if (res.code === 2000) {
          var item = res.data

          if (item.type === 'DIR') {
            item.size = '-'
          } else {
            item.size = item.size + 'B'
          }
          var encryptedfileKey = stringtoUint8Array(item.fileKey)
          // 解密文件密钥
          var fileKey = await dec(masterKey, clientRandomValue, encryptedfileKey)
          item.fileKey = uint8ArrayToString(new Uint8Array(fileKey))

          var encryptedfilename = stringtoUint8Array(item.filename)
          var encryptedmtime = stringtoUint8Array(item.mtime)
          var filename = await dec(fileKey, clientRandomValue, encryptedfilename)
          console.log("文件名：" + filename)
          item.filename = new TextDecoder().decode(filename);
          var mtime = await dec(fileKey, clientRandomValue, encryptedmtime)
          console.log("mtime：" + mtime)
          let mtimeDate = new Date(new TextDecoder().decode(mtime));
          item.mtime = dateToString(mtimeDate)
          _this.tableData.push(item)
        }
      })

      //文件夹展示到当前目录
      this.addfolder = false

    },


    async clickFolder(row) {
      if (row.type === "DIR") {
        this.tableData = []
        for (var i = 0; i < row.childrenFiles.length; i++) {
          this.tableData.concat(await encryptlist(row.childrenFiles[i], this))
        }

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

      let a = this.fromData
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
