<template>
  <div class="file-up">
    <el-button type="primary" size="medium" icon="el-icon-upload" @click="dialogVisible = true">上传文件(采用分块上传)</el-button>
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

        <el-select v-model="paragraph" placeholder="请选择分割文件块大小">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
            size="small"
          >
          </el-option>
        </el-select>
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
      paragraph: 1024,
      options: [{
        value: 1024,
        label: '分块大小 1KB'
      },
        {
          value: 2 * 1024,
          label: '分块大小 2KB'
        }, {
          value: 4 * 1024,
          label: '分块大小 4KB'
        }, {
          value: 8 * 1024,
          label: '分块大小 8KB'
        }, {
          value: 16 * 1024,
          label: '分块大小 16KB'
        }, {
          value: 32 * 1024,
          label: '分块大小 32KB'
        }, {
          value: 64 * 1024,
          label: '分块大小 64KB'
        }, {
          value: 128 * 1024,
          label: '分块大小 128KB'
        }, {
          value: 256 * 1024,
          label: '分块大小 256KB'
        }, {
          value: 512 * 1024,
          label: '分块大小 512KB'
        }, {
          value: 1024 * 1024,
          label: '分块大小 1MB'
        }, {
          value: 2 * 1024 * 1024,
          label: '分块大小 2MB'
        }, {
          value: 4 * 1024 * 1024,
          label: '分块大小 4MB'
        }, {
          value: 8 * 1024 * 1024,
          label: '分块大小 8MB'
        }, {
          value: 16 * 1024 * 1024,
          label: '分块大小 16MB'
        }, {
          value: 32 * 1024 * 1024,
          label: '分块大小 32MB'
        }, {
          value: 64 * 1024 * 1024,
          label: '分块大小 64MB'
        }

      ]
    }
  }
  ,
  created() {
    if (this.socket === null) {
      this.socket = new WebSocket('ws://127.0.0.1:8081/cloud/upload/' + localStorage.getItem("uid"))
    }
  },


  methods: {
    fileOnchange(e) {
      if (e.target.files.length !== 0) {
        // 获取文件对象
        this.fileObject = e.target.files[0];
        console.log(this.fileObject)
        this.textarea += (this.fileObject.name + "\n")
        this.textarea += ("共计" + Math.ceil(this.fileObject.size / this.paragraph) + "部分\n")
      }

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
          // console.log(v)
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
    intToByteBig(number, length) {
      var bytes = new ArrayBuffer(length);
      bytes[3] = (number & 0xff)
      bytes[2] = (number >> 8 & 0xff)
      bytes[1] = (number >> 16 & 0xff)
      bytes[0] = (number >> 24 & 0xff)
      return bytes;
    },
    mergeArrayBuffer(arrays) {
      let totalLen = 0;
      for (let arr in arrays) {
        totalLen += arr.byteLength;
      }
      let res = new Uint8Array(totalLen)
      let offset = 0
      for (let earr in arrays) {
        for (let arr in [earr]) {
          let uint8Arr = new Uint8Array(arr)
          res.set(uint8Arr, offset)
          offset += arr.byteLength
        }
      }
      return res.buffer
    }
    ,
    // 文件上传核心方法
    async uploadFileFun() {

      if (this.socket === null) {
        this.socket = new WebSocket('ws://127.0.0.1:8081/cloud/upload/' + localStorage.getItem("uid"))

      }

      let response = await axios.get('static/js/encryption.worker.js')
      var blob = new Blob([response.data], {
        type: 'text/plain'
      });
      const workerBlob = new Blob([blob])
      let objectURL = URL.createObjectURL(workerBlob);
      let worker = new Worker(objectURL);


      // 开始时间
      var startTime = new Date()

      console.log("开始时间戳：" + startTime.getTime())
      let _this = this
      var paragraph = this.paragraph;
      //文件对象赋值
      let fileData = this.fileObject;
      //切换保存标识的状态
      this.uploadFlag = false;
      //读取文件前128KB数据利用SHA-256生成256位文件密钥
      var v = await this.readAsBinaryString(fileData, 0, 1024 * 128);
      const sha256Key = await crypto.subtle.digest('SHA-256', v)
      var fileKey = new Uint8Array(new Uint8Array(sha256Key).subarray(0, 16));

      var blockSize = Math.ceil(fileData.size / paragraph)
      //后台只接收字符串类型，我们定义一个字符串的json对象给后台解析
      let fileJson = {
        opt: "fileMetadata",
        data: {
          filename: fileData.name,
          size: fileData.size,
          blockSize: blockSize,
          mtime: fileData.lastModifiedDate,
          fileKey: fileKey,
          parentInode: this.parentInode,
          clientRandomValue: localStorage.getItem('clientRandomValue'), //str
          masterKey: localStorage.getItem('masterKey'),
        }
      };
      // 将文件原数据发送到加密线程进行处理
      worker.postMessage(fileJson)

      var t1 = null, t2 = null, t3 = null
      // 用来接受加密算法返回的结果
      worker.onmessage = async (ev) => {
        var data = ev.data
        console.log(data)
        if (data.opt === 'fileMetadata') {
          // 得到加密得文件信息数据，将加密文件信息数据发送到后台
          console.log("文件原数据发送")
          _this.socket.send(JSON.stringify(data))
          let idx = 0
          // 循环读取文件块信息数据
          t1 = new Date()
          console.log("开始时间戳" + t1.getTime())
          for (let start = 0; start < fileData.size; start += paragraph) {
            let date = new Date();
            var block = fileData.slice(start, start + paragraph > fileData.size ? fileData.size : start + paragraph);
            let binaryArray = (await _this.readAsBinaryString(block)).buffer
            t2 = new Date()
            // console.log("读取时间" + (t2 - t1))
            worker.postMessage({opt: 'block', binaryArray: binaryArray, idx: idx, size: block.size}, [binaryArray])
            idx++
          }
        } else if (data.opt === 'block') {
          t3 = new Date()
          // TODO 将验证与发送数据合二为一
          _this.socket.send(data.data)
        }
      }

      _this.socket.onclose = function () {
        alert("后端已经断开连接")
        this.socket = null
        this.dialogVisible = false
      }

      _this.socket.onmessage = async function (msg) {
        if (msg.data === "数据上传失败") {
          console.log(msg.data)
          return
        }

        // 返回文件上传信息，主要用于反馈进度，让用户知道数据上传的具体进度
        var tip = JSON.parse(msg.data)
        _this.speedOfProgress = Math.ceil((tip.idx + 1) / blockSize * 100)
        if (tip.idx+1 === blockSize) {
          var endTime = new Date()
          console.log("发送时间间隔")
          console.log(Math.abs(endTime - startTime) / 1000)
          var unit = 'B'
          var size = fileData.size
          if (size >= 1024) {
            size = Math.ceil(size / 1024)
            unit = 'KB'
          }
          if (size >= 1024) {
            size = Math.ceil(size / 1024)
            unit = 'MB'
          }
          if (size >= 1024) {
            size = Math.ceil(size / 1024)
            unit = 'GB'
          }
          var tmp = tip.parentFilePo
          let item = {
            filename: fileData.name,
            fileKey: _this.uint8ArrayToString(fileKey),
            size: size + unit,
            blockSize: blockSize,
            userId: localStorage.getItem("uid"),
            parentInode: tmp.parentDir.inode,
            mtime: new Date(fileData.lastModified).toLocaleString(),
            type: 'FILE',
            inode: tmp.inode,
            state: 'UPLOADED'
          }
          // 将上传成功的数据添加到列表中
          _this.tableData.push(item)
        }
      }

    }
    ,
    // 单流水线加密发送
    async uploadFileFun2() {
      if (this.socket === null) {
        this.socket = new WebSocket('ws://127.0.0.1:8081/cloud/upload/' + localStorage.getItem("uid"))

      }

      let _this = this
      var paragraph = this.paragraph;
      //文件对象赋值
      let fileData = this.fileObject;
      //切换保存标识的状态
      this.uploadFlag = false;

      var start = new Date()
      var file1 = await this.readAsBinaryString(fileData);
      console.log(new Date()-start)
      //读取文件前128KB数据利用SHA-256生成256位文件密钥
      var v = await this.readAsBinaryString(fileData, 0, 1024 * 128);
      const sha256Key = await crypto.subtle.digest('SHA-256', v)
      var fileKey = new Uint8Array(new Uint8Array(sha256Key).subarray(0, 16));
      var blockSize = Math.ceil(fileData.size / paragraph)
      //后台只接收字符串类型，我们定义一个字符串的json对象给后台解析
      let data = {
        opt: "fileMetadata",
        data: {
          filename: fileData.name,
          size: fileData.size,
          blockSize: blockSize,
          mtime: fileData.lastModifiedDate,
          fileKey: fileKey,
          parentInode: this.parentInode,
          clientRandomValue: localStorage.getItem('clientRandomValue'), //str
          masterKey: localStorage.getItem('masterKey'),
        }
      };
      // 将文件原数据发送到加密线程进行处理

      // 解密文件元数据，将元数据发送到主线程
      // 获取文件密钥
      fileKey = data.data.fileKey
      var clientRandomValue = _this.stringToUint8Array(data.data.clientRandomValue)

      var t1 = new Date()
      console.log(t1.getTime())
      var idx = 0
      for (let start = 0; start < fileData.size; start += paragraph) {
        var block = fileData.slice(start, start + paragraph > fileData.size ? fileData.size : start + paragraph);
        let binaryArray = (await _this.readAsBinaryString(block)).buffer
        // 加密文件块
        let fingerprint = await crypto.subtle.digest('SHA-1', new Uint8Array(binaryArray))
        let encryptedData = await _this.encryptKey(fileKey, clientRandomValue, binaryArray);
        _this.socket.send(encryptedData)

        idx++
      }


    },
    stringToUint8Array(theStr) {
      var arr = [];
      var strLen = theStr.length;
      for (var idx = 0; idx < strLen; ++idx) {
        arr.push(theStr.charCodeAt(idx));
      }
      return new Uint8Array(arr)
    }
    ,
    // 打开上传框的时做的事件
    openSocket() {
      // 这里面存有 用户的 id
      this.speedOfProgress = 0

    },
    // 关闭上传框的时做的事件
    closeSocket() {
      // if (this.socket != null) {
      //   console.log("端口连接")
      //   this.socket.close();
      // }
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
