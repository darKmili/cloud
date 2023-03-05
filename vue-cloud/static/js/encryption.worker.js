/**
 * 文件加密线程，负责文件块加密
 * 文件加密需要做几件事情。
 * 计算哈希函数，计算加密数据
 */


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

// arraybuffer类型转16进制字符串
function buf2hex(buffer) {
  return Array.prototype.map.call(new Uint8Array(buffer), x => ('00' +
    x.toString(16)).slice(-2)).join('');

}



function arrayBufferToShareArrayBuffer(array){
  let sab = new SharedArrayBuffer(array.byteLength);
  let uint8Array = new Uint8Array(sab);
  sab.set(array,0)
  return sab.buffer
}


//大端模式 4字节
//number 要转换的整形数值
function intToByteBig(number) {
  var bytes = new Uint8Array(4);
  bytes[3] = (number & 0xff)
  bytes[2] = (number >> 8 & 0xff)
  bytes[1] = (number >> 16 & 0xff)
  bytes[0] = (number >> 24 & 0xff)
  return bytes.buffer;
}


let fileKey = null
let clientRandomValue = null
self.onmessage = async ({data}) => {
  if (data.opt === 'fileMetadata') {
    // 解密文件元数据，将元数据发送到主线程
    // 获取文件密钥
    fileKey = data.data.fileKey
    clientRandomValue = stringToUint8Array(data.data.clientRandomValue)


    // 1、加密文件名
    let fileName = data.data.filename
    let encoder = new TextEncoder();
    let data1 = encoder.encode(fileName);
    // console.log("fileName:" + data1);
    let encryptedMasterKeyHashValue1 = await encryptKey(fileKey, clientRandomValue, data1)
    let encryptedData1 = new Uint8Array(encryptedMasterKeyHashValue1)
    // console.log(encryptedData1)
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
    postMessage(data)
  } else if (data.opt === 'block') {
    // var t1 = new Date()
    // 加密块数据，将块数据发送到发送到upload.js
    let fingerprint = await crypto.subtle.digest('SHA-1', new Uint8Array(data.binaryArray))
    let encryptedData = await encryptKey(fileKey, clientRandomValue, data.binaryArray);

    // var t2 = new Date()
    // console.log("加密时间"+(t2  - t1))
    // let fingerprintString = uint8ArrayToString(new Uint8Array(fingerprint))
    // let encryptedDataString = uint8ArrayToString(new Uint8Array(encryptedData))

    let arrayBuffer = concatenate(fingerprint,intToByteBig(data.idx),intToByteBig(data.size),encryptedData);
    // var t3 = new Date()
    // console.log("数据组合时间"+(t3-t2))
    postMessage({
      opt: 'block',
      data: arrayBuffer
    }, [arrayBuffer])

    // postMessage({
    //   opt: 'block',
    //   // fingerprint: arrayBufferToShareArrayBuffer( fingerprint),
    //   // idx:intToByteBig(data.idx),
    //   encryptedData: encryptedData,
    //   // size:intToByteBig(data.size)
    // },[encryptedData])
  }

  function concatenate(...arrays) {

    let totalLen = 0;

    for (let arr of arrays)

      totalLen += arr.byteLength;

    let res = new Uint8Array(totalLen)

    let offset = 0

    for (let arr of arrays) {

      let uint8Arr = new Uint8Array(arr)

      res.set(uint8Arr, offset)

      offset += arr.byteLength

    }

    return res.buffer

  }

}
