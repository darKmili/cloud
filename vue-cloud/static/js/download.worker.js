/**
 * 解密线程，用于解密数据
 */

async function decryptKey(keyArr, clientRandomValue, theKeyToDecrypt) {
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
  console.log("导入密钥")

  return await crypto.subtle.decrypt(params, aesKey, theKeyToDecrypt)
}

self.onmessage = async ({data}) => {
  let arrayBuffer =  await decryptKey(data.fileKey, data.clientRandomValue, data.data)
  console.log(arrayBuffer)
  postMessage({arrayBuffer:arrayBuffer,idx:data.idx},[arrayBuffer])
}
