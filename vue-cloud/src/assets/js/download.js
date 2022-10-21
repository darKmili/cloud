// 文件下载
import request from "./request";
import {dec, stringtoUint8Array} from "./pbkdf";
import da from "element-ui/src/locale/lang/da";

export function download({userId, row}) {
  var webSocketUrl = 'ws://127.0.0.1:8081/cloud/download/' + userId;
  var socket = new WebSocket(webSocketUrl);
  var datalist = []
  var fileKey = stringtoUint8Array( row.fileKey)
  var clientRandomValue = stringtoUint8Array(localStorage.getItem("clientRandomValue"))
  var filename = row.filename
  socket.onopen = function () {
    console.log("连接成功" + JSON.stringify(row))
    // 1、发送请求,获取文件块位置
    socket.send(JSON.stringify({"inode":row.inode}));
  }
  socket.onmessage = async function (res) {
    console.log(res.data)
    let blocks = JSON.parse(res.data);
    // 2、向存储服务器发送请求，获取文件块. TODO
    for (const idx in blocks) {

        var  blockData = stringtoUint8Array(blocks[idx].data)
        // 解密文件块 TODO
        var data = await dec(fileKey, clientRandomValue, blockData)
        datalist.push(data)
    }

    // 将文件块封装成文件
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
      var evt = document.createEvent("MouseEvents");
      evt.initEvent("click", false, false);
      link.dispatchEvent(evt);//释放URL 对象
      document.body.removeChild(link);
    }
  }

}
