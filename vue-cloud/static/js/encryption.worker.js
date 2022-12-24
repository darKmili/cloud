/**
 * 文件加密线程，负责文件块加密
 * 文件加密需要做几件事情。
 * 计算哈希函数，计算加密数据
 */

let fileKey = null
let clientRandomValue = null
let workerScript = `
// var socket = null
var map = new Map();
self.onmessage = ({data}) => {
  var _self = self
  if (data.opt === 'fileMetadata') {
    if(self.socket ==null){
      console.log("建立连接")
      self.socket = new WebSocket(data.data.webSocketUrl + data.data.userId);
      console.log(socket)
    }

    self.socket.onopen = (e) => {
      _self.socket.send(JSON.stringify(data))
    };
    console.log(JSON.stringify(data))
  } else if (data.opt === 'fingerprint') {
    console.log(JSON.stringify(data))
    // 先发送文件块哈希，判断文件块是否存在
    if(self.socket!=null){
      self.socket.send(JSON.stringify(data))
    }
  }else if (data.opt==="encryptedData"){
    console.log(JSON.stringify(data))
    console.log(JSON.stringify(self.socket))
    if(self.socket!=null){
     map.set(data.data.fingerprint,data.data)
     self.socket.onmessage = function (msg) {
      // 根据回反得消息判断是否发送加密得数据块
      if (msg.opt === 'over') {
        map = null
      }else {
        _self.socket.send(JSON.stringify({opt: 'encryptedData', encryptedData:  Array.from(data.data.encryptedData)}))
      }
    }
    }

  }

}
`

const workerBlob = new Blob([workerScript])
const objectURL = URL.createObjectURL(workerBlob);

const worker = new Worker(objectURL);

async function encryptKey(keyArr, clientRandomValue, theKeyToEnc) {

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
}

function uint8ArrayToString(arrayParam) {
  let arrayLen = arrayParam.length;
  let str = '';
  for (var idx = 0; idx < arrayLen; ++idx) {
    str += String.fromCharCode(arrayParam[idx]);
  }
  return str;
}

function stringToUint8Array(theStr) {
  var arr = [];
  var strLen = theStr.length;
  for (var idx = 0; idx < strLen; ++idx) {
    arr.push(theStr.charCodeAt(idx));
  }

  return new Uint8Array(arr)
}

self.onmessage = async ({data}) => {

  console.log("加密线程")
  var _self = self
  worker.onmessage = (e) => {
    // 返回进度给上一级
    _self.postMessage(e, "*")
  }
  if (data.opt === 'fileMetadata') {
    // 解密文件元数据，将元数据发送到upload.js
    // 获取文件密钥
    fileKey = data.data.fileKey
    clientRandomValue = stringToUint8Array(data.data.clientRandomValue)
    console.log("clientRandomValue:" + clientRandomValue);
    console.log("fileKey:" + fileKey);

    // 1、加密文件名
    let fileName = data.data.filename
    let encoder = new TextEncoder();
    let data1 = encoder.encode(fileName);
    console.log("fileName:" + data1);
    let encryptedMasterKeyHashValue1 = await encryptKey(fileKey, clientRandomValue, data1)
    let encryptedData1 = new Uint8Array(encryptedMasterKeyHashValue1)
    data.data.filename = uint8ArrayToString(encryptedData1)

    // 2、加密时间日期
    var Mtime = data.data.mtime;
    var data2 = encoder.encode(Mtime.toString())
    var encryptedMasterKeyHashValue2 = await this.encryptKey(fileKey, clientRandomValue, data2)
    var encryptedData2 = new Uint8Array(encryptedMasterKeyHashValue2)
    data.data.mtime = uint8ArrayToString(encryptedData2)

    // 3、加密文件密钥
    let masterKey = stringToUint8Array(data.data.masterKey)
    var encryptedMasterKeyHashValue = await this.encryptKey(masterKey, clientRandomValue, fileKey)
    data.data.fileKey = uint8ArrayToString(new Uint8Array(encryptedMasterKeyHashValue))

    data.data.masterKey = null
    data.data.clientRandomValue = null
    console.log('文件元数据'+JSON.stringify(data))
    worker.postMessage(data)
  } else if (data.opt === 'block') {
    // 加密块数据，将块数据发送到发送到upload.js
    console.log('文件数据'+JSON.stringify(data) )
    let fingerprint = await crypto.subtle.digest('SHA-1', new Uint8Array(data.binaryArray))
    let arrayToString = uint8ArrayToString(fingerprint);
    worker.postMessage({opt:'fingerprint',data:{fingerprint: arrayToString}})
    let encryptedData = await encryptKey(fileKey, clientRandomValue, data.binaryArray);
    worker.postMessage({opt:'encryptedData',data:{idx:data.idx,fingerprint: arrayToString, encryptedData: encryptedData}}, [ encryptedData])
  }
}
