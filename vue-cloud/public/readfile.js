/**
 * 负责文件读取。文件读取线程
 */
let start = 0
const block_size = 8 * 1024 * 1024
const worker = new Worker('./encryption.js')
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

  if (fileData!=null){
    // 按照8M大小不断读取文件，并将文件数据发送到下个线程（通过转移，而不是负责）
    while (start < fileData.size()) {
      const end = start + block_size > fileData.size ? fileData.size : start + block_size
      let block = fileData.slice(start, end);
      // 同步读取方法，只能在work线程中使用
      let fileReaderSync = new FileReaderSync();
      let binaryArray = fileReaderSync.readAsArrayBuffer(block)
      // 将数据发送到加密线程中
      worker.postMessage({opt: 'block', binaryArray: binaryArray}, [binaryArray])
      start = start + block_size
    }
  }



}


// worker.onmessage = ({data}) =>{
//   // 接受加密功能encryption.js的脚本线程反馈.该脚本反馈进度
// }

