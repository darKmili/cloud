<template>
  <div class="file-up">
    <el-button type="primary" size="medium" icon="el-icon-upload" @click="dialogVisible = true">上传文件</el-button>
    <el-dialog
      title="上传"
      :visible.sync="dialogVisible"
      width="40%"
      v-on:open="openSocket"
      v-on:close="closeSocket"
    >

      <div>
        <template>
          <el-button type="primary" class="file">选择文件
            <input type="file" name="" @change="fileOnchange($event)"> <span id="filename"
                                                                             style="color: red"></span>
          </el-button>
        </template>
        <el-button class="ml-3" type="success" @click="uploadFileFun">
          确认上传
        </el-button>
      </div>
      <br>
      <el-progress :text-inside="true" :stroke-width="20" :percentage="speedOfProgress" :status="status"></el-progress>
      <div style="margin: 20px 0;"></div>
      <el-input
        id="message_content"
        type="textarea"
        :autosize="{ minRows: 6, maxRows: 15}"
        placeholder="上传文件块统计框"
        v-model="textarea"
        ref="textarea"

      >
      </el-input>
      <span slot="footer" class="dialog-footer"></span>
    </el-dialog>
  </div>

</template>

<script>

// import Worker from '../assets/js/readfile.worker.js'

import axios from "axios";

export default {
  props: {
    parentInode: null,
    tableData: null
  },

  data() {
    return {
      dialogVisible: false,
      speedOfProgress: 0,
      textarea: "",
      socket: null,
      status: null,
      fileObject: null,
      uploadFlag: true,
      startSize: 0,
      endSize: 0,
      paragraph: 8 * 1024 * 1024
    }
  }
  ,
  watch: {},

  methods: {

    fileOnchange(e) {
      // 获取文件对象
      this.fileObject = e.target.files[0];
      console.log(this.fileObject)
      this.textarea += (this.fileObject.name + "\n")
      this.textarea += ("共计" + Math.ceil(this.fileObject.size / this.paragraph) + "部分\n")
    },
    // 加密方法
    async encryptKey(keyArr, clientRandomValue, theKeyToEnc) {

      const params = {
        name: "AES-CBC",
        iv: clientRandomValue,
        length: 128
      };

      const aesKey = await crypto.subtle.importKey(
        "raw",
        keyArr.buffer,
        "AES-CBC",
        true,
        ["encrypt", "decrypt"]);

      return await crypto.subtle.encrypt(params, aesKey, theKeyToEnc)
    },
    //分块读取文件
    async readAsBinaryString(file, start, end) {
      return new Promise((resolve, reject) => {
        var reader = new FileReader();
        var blob = file.slice(start, end);
        reader.readAsArrayBuffer(blob);
        reader.onload = (e) => {
          let num = e.target.result
          let v = new Uint8Array(num);
          console.log(v)
          resolve(v);
        };
      })

    },
    stringtoUint8Array(theStr) {
      var arr = [];
      var strLen = theStr.length;
      for (var idx = 0; idx < strLen; ++idx) {
        arr.push(theStr.charCodeAt(idx));
      }

      return new Uint8Array(arr)
    },
    uint8ArrayToString(arrayParam) {
      let arrayLen = arrayParam.length;
      let str = '';
      for (var idx = 0; idx < arrayLen; ++idx) {
        str += String.fromCharCode(arrayParam[idx]);
      }

      return str;
    },
    // 文件上传核心方法
    async uploadFileFun() {
      // 主线程负责读取文件内容
      // 第一个子线程负责加密文件
      // 第二个子线程负责发送数据

      let response = await axios.get('static/js/encryption.worker.js')
      var blob = new Blob([response.data], {
        type: 'text/plain'
      });
      const workerBlob = new Blob([blob])
      let objectURL = URL.createObjectURL(workerBlob);
      let worker = new Worker(objectURL);


      let _this = this
      var paragraph = this.paragraph;
      //文件对象赋值
      let fileData = this.fileObject;
      //切换保存标识的状态
      this.uploadFlag = false;
      //读取文件前128 KB数据利用SHA-256生成256位文件密钥
      var v = await this.readAsBinaryString(fileData, 0, 1024 * 128);
      const sha256Key = await crypto.subtle.digest('SHA-256', v)
      var fileKey = new Uint8Array(new Uint8Array(sha256Key).subarray(0, 16));
      console.log("fileKey:" + fileKey);

      var blockSize = Math.ceil(fileData.size / paragraph)
      //后台只接收字符串类型，我们定义一个字符串的json对象给后台解析
      let fileJson = {
        opt: "fileMetadata",
        data:{
          filename: fileData.name, //_this.uint8ArrayToString(encryptedData1)
          size: fileData.size,
          blockSize: blockSize,
          mtime: fileData.lastModifiedDate,
          fileKey: fileKey,
          userId: localStorage.getItem("uid"),
          parentInode: this.parentInode,
          clientRandomValue: localStorage.getItem('clientRandomValue'), //str
          masterKey: localStorage.getItem('masterKey'),
          webSocketUrl: 'ws://127.0.0.1:8081/cloud/upload/'
        }
      };
      worker.postMessage(fileJson)
      let start = 0
      let idx = 0
      // 数据信息发送到读取线程
      if (fileData != null) {
        // 按照8M大小不断读取文件，并将文件数据发送到下个线程（通过转移，而不是负责）
        while (start < fileData.size) {
          const end = start + paragraph > fileData.size ? fileData.size : start + paragraph
          let block = fileData.slice(start, end);
          // 同步读取方法，只能在work线程中使用

          let binaryArray =  await _this.readAsBinaryString(block)
          // 将数据发送到加密线程中.指定文件块的文件名，
          worker.postMessage({opt: 'block',idx:idx, binaryArray: binaryArray})
          idx++
          start = end
        }
      }

      // 用来接受回显信息(更新上传进度)
      worker.onmessage = (ev) => {
        _this.speedOfProgress = ev
        if (_this.speedOfProgress === 100) {
          _this.uploadFlag = true
        }
      }
    }
    ,
    // 打开上传框的时做的事件
    openSocket() {
      // 这里面存有 用户的 id
      this.speedOfProgress = 0
    },
    // 关闭上传框的时做的事件
    closeSocket() {

    }
  }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.file {
  position: relative;
  display: inline-block;
  background: #D0EEFF;
  border: 1px solid #99D3F5;
  border-radius: 4px;
  padding: 9px 21px;
  overflow: hidden;
  color: #1E88C7;
  text-decoration: none;
  text-indent: 0;
  line-height: 20px;
}

.file input {
  position: absolute;
  font-size: 100px;
  right: 0;
  top: 0;
  opacity: 0;
}

.file:hover {
  background: #AADFFD;
  border-color: #78C3F3;
  color: #004974;
  text-decoration: none;
}
</style>
