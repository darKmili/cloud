/**
 * 文件上次线程：负责文件上传工作
 * 与后台建立websocket长连接。发送数据给后台（块哈希、块数据以及加密的块元数据）
 */

let socket = null
let map = new Map();
self.onmessage = ({data}) => {
  var _self = self
  if (data.opt === 'fileMetadata') {
    if(socket ==null){
      console.log("建立连接")
      socket = new WebSocket(data.data.webSocketUrl + data.data.userId);
      console.log(socket)
    }

    socket.onopen = (e) => {
      socket.send(JSON.stringify(data))
    };
    console.log(JSON.stringify(data))
  } else if (data.opt === 'fingerprint') {
    console.log('文件元数据'+data)
    // 先发送文件块哈希，判断文件块是否存在
    if(socket!=null){
      socket.send(JSON.stringify(data))
    }
  }else if (data.opt==="encryptedData"){
    console.log(JSON.stringify(data))
    map.set(data.data.fingerprint,data.data)
    socket.onmessage = function (msg) {
      // 根据回反得消息判断是否发送加密得数据块
      if (msg.opt === 'over') {
        map = null
      }else {
        socket.send(JSON.stringify({opt: 'encryptedData', encryptedData:  Array.from(data.encryptedData)}))
      }
    }
  }

}
