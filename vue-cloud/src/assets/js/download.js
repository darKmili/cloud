// 文件下载
import request from "./request";
import {decryptKey, dec, stringtoUint8Array} from "./pbkdf";
import da from "element-ui/src/locale/lang/da";

function encodeUtf8(text) {
  const code = encodeURIComponent(text);
  const bytes = [];
  for (var i = 0; i < code.length; i++) {
    const c = code.charAt(i);
    if (c === '%') {
      const hex = code.charAt(i + 1) + code.charAt(i + 2);
      const hexVal = parseInt(hex, 16);
      bytes.push(hexVal);
      i += 2;
    } else bytes.push(c.charCodeAt(0));
  }
  return bytes;
}

async function readAsBinaryArray(file) {
  return new Promise((resolve, reject) => {
    var reader = new FileReader();
    reader.readAsArrayBuffer(file);
    reader.onload = (e) => {
      let num = e.target.result
      let v = new Uint8Array(num);
      console.log(v)
      resolve(v);
    };
  })
}

export function download({userId, row}) {
  var fs = require("fs");
  let textEncoder = new TextEncoder();
  var webSocketUrl = 'ws://127.0.0.1:8081/cloud/download/' + userId;
  var socket = new WebSocket(webSocketUrl);
  var datalist = []
  var fileKey = stringtoUint8Array(row.fileKey)
  var clientRandomValue = stringtoUint8Array(localStorage.getItem("clientRandomValue"))
  var filename = row.filename
  var index = 0
  socket.onopen = function () {
    console.log("连接成功" + JSON.stringify(row))
    // 1、发送请求,获取文件信息
    socket.send(JSON.stringify({"inode": row.inode,"index":index}));
  }

  socket.onmessage = async function (res) {

    console.log(res.data)
    let blocks = res.data;
    let arrayBuffer = await readAsBinaryArray(blocks);
    if (arrayBuffer.length===4){
      // 2、将文件块封装成文件
      let blob = new Blob(datalist, {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"});
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
    }else {
      // 1、解密文件块
      var data = await decryptKey(fileKey, clientRandomValue, arrayBuffer)
      console.log("解密成功")
      datalist.push(data)
      index+=1
      socket.send(JSON.stringify({"inode": row.inode,"index":index}));
    }
  }


}
