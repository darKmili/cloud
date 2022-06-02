// 文件下载
import request from "./request";
import {dec} from "./pbkdf";

export function download({userId, row}) {
  var webSocketUrl = 'ws://127.0.0.1:8081/cloud/download/' + userId;
  var socket = new WebSocket(webSocketUrl);
  var datalist = []
  var fileKey = row.fileKey
  socket.onopen = function () {
    console.log("连接成功" + JSON.stringify(row))
    // 1、发送请求,获取文件块位置
    socket.send(JSON.stringify(row));
  }
  socket.onmessage = async function (res) {

    console.log(res.data)
    let blockUrls = JSON.parse(res.data);
    // 2、向存储服务器发送请求，获取文件块. TODO
    for (const url in blockUrls) {
      await request.get(url).then(async function (res) {
        // 解密文件块 TODO
        res = await dec(fileKey, clientRandomValue, encryptedfilename)
        datalist.push(res)
      })
    }

    // 将文件块封装成文件
    let blob = new Blob(datalist, {type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"});
    if (window.navigator.msSaveOrOpenBlob) {
      // IE10+下载
      navigator.msSaveOrBlob(blob, row.name);
    } else {
      // 非IE10+下载
      let link = document.createElement('a');
      link.href = window.URL.createObjectURL(blob);
      link.download = row.name;
      document.body.appendChild(link);
      var evt = document.createEvent("MouseEvents");
      evt.initEvent("click", false, false);
      link.dispatchEvent(evt);//释放URL 对象
      document.body.removeChild(link);
    }
  }

}
