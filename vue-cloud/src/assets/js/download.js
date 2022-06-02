// 文件下载
export function download({userId, row}) {
  var webSocketUrl = 'ws://127.0.0.1:8081/cloud/download/' + userId;
  var socket = new WebSocket(webSocketUrl);
  var datalist = new Array(row.blockSize)
  socket.onopen = function () {
    console.log("连接成功" + JSON.stringify(row))
    socket.send(JSON.stringify(row));
  }
  socket.onmessage = function (res) {
    if (res.data === "over") {
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
      return
    }
    console.log(res.data)
    let block = JSON.parse(res.data);
    // 解密文件块 TODO
    datalist[block.idx] = block.data;
  }

}
