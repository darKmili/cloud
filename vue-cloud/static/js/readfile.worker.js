importScripts('axios')
/**
 * 负责文件读取。文件读取线程
 */
let start = 0
const block_size = 8 * 1024 * 1024

// const worker = new Worker('./encryption.js')
let  response   = await axios.get('static/js/encryption.worker.js')

var blob = new Blob([response.data], {
  type: 'text/plain'
});
const workerBlob = new Blob([blob])
let objectURL = URL.createObjectURL(workerBlob);

let worker = new Worker(objectURL);
let fileData = null


// 消息读取
self.onmessage = ({data}) => {

  console.log("文件读取线程")

  var _self = self
  worker.onmessage = (e) => {
    // 返回进度给上一级
    _self.postMessage(e, "*")
  }

  // 发送元数据给加解密模块
  fileData = data.data
  start = 0
  data.data = null
  worker.postMessage(data)





}
