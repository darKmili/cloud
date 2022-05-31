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
      <el-progress :text-inside="true" :stroke-width="20" :percentage="SpeedOfProgress" :status="status"></el-progress>
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
import {dec} from "../assets/js/pbkdf";
export default {
  props: {
    parentInode:null

  },

  data() {
    return {
      dialogVisible: false,
      SpeedOfProgress: 0,
      textarea: "",
      socket: null,
      status: null,
      fileObject: null,
      uploadFlag: true,
      startSize: 0,
      endSize: 0,
      paragraph: 1024 * 1024
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
      if (this.socket == null) {
        alert("还未连接后端")
        return
      }
      let _this = this
      var startSize = this.startSize;
      var endSize = this.endSize;
      var paragraph = this.paragraph;
      //文件对象赋值
      let filedata = this.fileObject;
      //切换保存标识的状态
      this.uploadFlag = false;


      //先向后台传输文件名
      //读取文件前128 KB数据利用SHA-256生成256位密钥
      var v = await this.readAsBinaryString(filedata, 0, 1024 * 128);
      const sha256Key = await crypto.subtle.digest('SHA-256', v)
      var fileKey = new Uint8Array(sha256Key).subarray(0, 16);
      console.log("fileKey:" + fileKey);

      // 获取用户随机数
      var fileSize = filedata.size;
      console.log("file size:" + fileSize);
      let clientRandomValue = this.stringtoUint8Array(localStorage.getItem('clientRandomValue'));
      // let clientRandomValue = new Uint8Array(2 ** 4);
      // window.crypto.getRandomValues(clientRandomValue);


      // 文件名加密
      var fileName = filedata.name;
      let data1 = this.stringtoUint8Array(fileName)
      console.log("fileName:" + data1);
      var encryptedMasterKeyHashValue1 = await this.encryptKey(fileKey, clientRandomValue, data1)
      var encryptedData1 = new Uint8Array(encryptedMasterKeyHashValue1)
      console.log("encryptedData1:" + encryptedData1)
      var c=await dec(fileKey, clientRandomValue, encryptedData1)


      console.log(clientRandomValue)

      // 上次修改时间
      var Mtime = filedata.lastModifiedDate;
      var data2 = this.stringtoUint8Array(Mtime.toString())
      console.log("Mtime:" + data2);
      var encryptedMasterKeyHashValue2 = await this.encryptKey(fileKey, clientRandomValue, data2)
      var encryptedData2 = new Uint8Array(encryptedMasterKeyHashValue2)
      console.log("encryptedData2:" + encryptedData2)
      console.log("clientRandomValue:" + clientRandomValue)
      console.log("fileKey"+fileKey)
      var c=await dec(fileKey, clientRandomValue, encryptedData2)
      console.log("c"+c)
      //对密钥加密
      let masterKey = this.stringtoUint8Array(localStorage.getItem('masterKey'));
      // let masterKey = new Uint8Array(2 ** 4);
      // window.crypto.getRandomValues(masterKey);

      var encryptedMasterKeyHashValue = await this.encryptKey(masterKey, clientRandomValue, fileKey)
      var encryptedkey = new Uint8Array(encryptedMasterKeyHashValue)
      console.log("encrypted key:" + encryptedkey)
      console.log("fileKey:" + fileKey);

      var blockSize = Math.ceil(filedata.size / paragraph)
      console.log("发送文件数据之前")
      //后台只接收字符串类型，我们定义一个字符串的json对象给后台解析
      let fileJson = {
        opt: "fileUpload",
        data: {
          filename: _this.uint8ArrayToString(encryptedData1),
          size: filedata.size,
          blockSize: blockSize,
          mtime:_this.uint8ArrayToString(encryptedData2),
          // fileKey: _this.uint8ArrayToString(fileKey),
          fileKey: _this.uint8ArrayToString(encryptedkey),
          userId: localStorage.getItem("uid"),
          parentInode: this.parentInode
        }
      };

      //后台接收到文件名以后会正式开始传输文件
      console.log("向后台发送消息，请求上传数据")
      this.socket.send(JSON.stringify(fileJson));

      //此处为文件上传的核心中的核心，涉及分块上传
      this.socket.onmessage = function (msg) {
        console.log(msg.data)
        var tip = JSON.parse(msg.data)


        // opt next
        // 上传块元数据
        if (tip.opt === 'blockMetadata') {
          console.log("上传块元数据")
          if (tip.next >= blockSize) {
            _this.socket.send(JSON.stringify({opt: "over"}))
            _this.SpeedOfProgress = 100
          } else {
            _this.SpeedOfProgress = Math.floor(tip.next / blockSize * 100)
            // 代表第几个块
            var start = tip.next * paragraph;
            var end = start + paragraph > filedata.size ? filedata.size : start + paragraph;
            var block = filedata.slice(start, end);
            var reader = new FileReader();
            reader.readAsArrayBuffer(block);
            reader.onload = async function loaded(evt) {
              var arrayBuffer = evt.target.result;
              console.log(arrayBuffer);
              _this.textarea += ("发送文件第" + tip.next + "部分" + '\n');
              // 加密块，生成块的指纹
              console.log(new Uint8Array(arrayBuffer))
              var fingerprint = await crypto.subtle.digest('SHA-1', new Uint8Array(arrayBuffer))
              fingerprint = new Uint8Array(fingerprint).subarray(0, 20)
              console.log("--------------" + fingerprint)
              fingerprint = _this.uint8ArrayToString(fingerprint)
              let fileObjJson = {
                opt: "block",
                data: {
                  fingerprint: fingerprint,
                  idx: tip.next,
                  size: end - start
                }

              };
              _this.socket.send(JSON.stringify(fileObjJson));

            }
          }
          // 上传块数据本身
        } else if (tip.opt === 'blockData') {
          console.log("上传块数据")
          var start = tip.next * paragraph;
          var end = start + paragraph > filedata.size ? filedata.size : start + paragraph;
          var block = filedata.slice(start, end);
          var reader = new FileReader();
          reader.readAsArrayBuffer(block);
          reader.onload = function loaded(evt) {
            var arrayBuffer = evt.target.result;
            _this.socket.send(arrayBuffer)
          }

        } else if (tip.opt === 'over') {
          console.log("上传成功")
          _this.socket.send(JSON.stringify({opt: "over"}))
        }


      }

      this.socket.onclose = function () {
        alert("后端已经断开连接")
        this.socket = null
        this.dialogVisible = false
      }


    }
    ,
    // 打开上传框的时做的事件
    openSocket() {
      // 这里面存有 用户的 id
      this.SpeedOfProgress = 0
      let userid = JSON.parse(localStorage.getItem("userid"));
      if (userid == null) {
        userid = "1"
      }
      var webSocketUrl = 'ws://127.0.0.1:8081/cloud/upload/';//websocketi连接地址 //ws://127.0.0.1:8080/upload/block
      // var paragraph = 1024 * 1024;//文件分块上传大小1M
      // var startSize, endSize = 0;//文件的起始大小和文件的结束大小
      // var i = 0;//第几部分文件

      if (!this.socket || this.socket) {//避免重复连接
        this.socket = new WebSocket(webSocketUrl + userid);
        console.log(this.socket)
        this.socket.onopen = function () {
          console.log("websocket已连接");
        };
        this.socket.onmessage = function (e) {
          if (this.uploadFlag) {
            //服务端发送的消息
            this.textarea += (e.data + '\n');
          }
        };
        this.socket.onclose = function () {
          console.log("websocket已断开");
          this.dialogVisible = false;
        }
      }

    },
    // 关闭上传框的时做的事件
    closeSocket() {
      if (this.socket != null) {
        console.log("端口连接")
        this.socket.close();
      }
      this.textarea = "";
      this.socket = null;
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
