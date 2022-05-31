

//Convert Uint8Array to String
export function uint8ArrayToString(arrayParam) {
  let arrayLen = arrayParam.length;
  let str = '';
  for (var idx = 0; idx < arrayLen; ++idx) {
    str += String.fromCharCode(arrayParam[idx]);
  }
  return str;
}

//Convert String to Uint8Array
export function stringtoUint8Array(theStr) {
  var arr = [];
  var strLen = theStr.length;
  for (var idx = 0; idx < strLen; ++idx) {
    arr.push(theStr.charCodeAt(idx));
  }

  var tmpUint8Array = new Uint8Array(arr);
  return tmpUint8Array
}

/**
 *
 * @param password string
 * @param clientRandomValue string
 * @returns {Promise<CryptoKey>}
 */
export async function pbkdf2Function(password, clientRandomValue) {

  // var randomValueLength = clientRandomValue.byteLength;
  var randomValueLength = 16;
  console.log(clientRandomValue.length)
  // clientRandomValue = uint8ArrayToString(clientRandomValue);
  console.log("clientRandomValue:"+clientRandomValue)
  //Generate Padding String ("SZTUBIGDATA"|| Padding || clientRandomValue)with length 200
  var shaString = "SZTUBIGDATA";
  var padLength = 200 - randomValueLength;
  shaString = shaString.padEnd(padLength, 'A');
  shaString = shaString + clientRandomValue;
  console.log("shaString Length:" + shaString.length)

  const textEncoder = new TextEncoder();
  const shaMessage = textEncoder.encode(shaString);
  const hashBuffer = await crypto.subtle.digest('SHA-256', shaMessage)

  var salt = new Uint8Array(hashBuffer);

  const enPassword = await window.crypto.subtle.importKey(
    "raw",
    textEncoder.encode(password),
    {name: "PBKDF2"},
    false,
    ["deriveBits", "deriveKey"]
  );

  return window.crypto.subtle.deriveKey(
    {
      "name": "PBKDF2",
      "hash": "SHA-256",
      "salt": salt,
      "iterations": 100000
    },
    enPassword,
    {"name": "AES-GCM", "length": 256},
    true,
    ["encrypt", "decrypt"]
  );

}

export async function encryptKey(keyArr, clientRandomValue, theKeyToEnc) {

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

  const encryptedData = await crypto.subtle.encrypt(params, aesKey, theKeyToEnc);
  return encryptedData
}

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

  const decryptedData = await crypto.subtle.decrypt(params, aesKey, theKeyToDecrypt);
  return decryptedData
}

export async function f() {
  return 1;
}

//解密
export async function dec(left128Bits, randomvalue, encryptedMasterKey){

  var deKeyBuffer = await decryptKey(left128Bits, randomvalue, encryptedMasterKey)
  console.log("解密----1")
  var decryptedMasterKey = new Uint8Array(deKeyBuffer)
  console.log("decryptedMasterKey---" + decryptedMasterKey);
  return(decryptedMasterKey)
}

export function isEmail(emailStr) {
  var emailReg = /^\w+@[a-zA-Z0-9]{2,10}(?:\.[a-z]{2,4}){1,3}$/;
  return emailReg.test(emailStr);
}

//文件目录
function Catalogshow(obj) {

}

//展示文件大小
export function showfilesize(filesize){
  if (filesize < 1048576) {
    var size = (filesize / 1024).toFixed(2) + 'KB'
  }
  else if (filesize > 1048576 && filesize < 1073741824) {
    var size = (filesize / 1024 / 1024).toFixed(2) + 'MB'
  }
  else if (filesize > 1073741824) {
    var size = (filesize / 1024 / 1024 / 1024).toFixed(2) + 'GB'
  }
  return(size)
}

function readAsBinaryString(file) {

  return new Promise((resolve, reject) => {
    // var file = document.getElementById("file").files[0];
    var reader = new FileReader();
    //将文件以二进制形式读入页面
    reader.readAsBinaryString(file);
    reader.onload = (e) => {
      var num = e.target.result
      v1 = stringtoUint8Array(num);
      console.log(v1)
      //读取文件前128KB
      var v2 = new Uint8Array(128 * 1024)
      v2 = v1.subarray(0, 128 * 1024)
      console.log(v2)
      sessionStorage.setItem('v2', v2);
      resolve({
        v1,
        v2
      });
    };
  })
}

//upload file
async function UploadFile(obj) {
  var file = document.getElementById("file").files[0];
  //v1文件数据，v2文件前128KB数据
  let {
    v1 = new Uint8Array,
    v2 = new Uint8Array
  } = await readAsBinaryString(file)

  console.log(v1)
  console.log(v2)
  //对文件前128 KB数据利用SHA-256生成256位密钥
  const hashBuffer = await crypto.subtle.digest('SHA-256', v2)
  var sha256Key = new Uint8Array(hashBuffer);
  console.log("sha256Key:" + sha256Key);
  //
  // v3 = uint8ArrayToString(v2)
  // js 获取文件名，大小，ctime,mtime,atime
  fileSize = file.size;
  console.log("file size:" + fileSize);
  fileName = file.name;

  fileType = file.type;
  console.log(fileName)
  console.log(fileType)

  var Mtime = file.lastModifiedDate;
  console.log(Mtime)
  var Ctime = file.DateCreated;
  console.log(Ctime)
  var Atime = file.DateLastAccessed;
  console.log(Atime)


  //发送加密的密钥、文件名、ctime、mtime和atime
  let xhr = new XMLHttpRequest();
  xhr.open("post", url, true);//post data to registerhandle.jsp page

  let data = new FormData();
  data.append("key", key);
  data.append("fileName", fileName);
  data.append("ctime", ctime);
  data.append("mtime", mtime);
  data.append("atime", atime);

  xhr.send(data);
}

//上传成功响应
function uploadComplete(evt) {
  undefined
  //服务器接收完文件返回的结果
  alert("上传成功！");
}

//上传失败
function uploadFailed(evt) {
  undefined
  alert("上传失败！");
}

export async function newFolder(Fname) {
  //生成密钥，加密文件名，mtime，然后用主密钥加密密钥
  let fname=stringtoUint8Array(Fname)
  let fmasterKey = new Uint8Array(2 ** 4);
  var randomvalue = sessionStorage.getItem("randomValue");
  var encryptedfnameHashValue = await encryptKey(fmasterKey, randomvalue, fname)
  var encryptedfname = new Uint8Array(encryptedfnameHashValue)
  console.log("encryptedfname:" + encryptedfname)

  var Mtime = new Date()
  let mtime = stringtoUint8Array(Mtime.toString())
  var encryptedmtimeHashValue = await encryptKey(fmasterKey, randomvalue, mtime)
  var encryptedmtime = new Uint8Array(encryptedmtimeHashValue)
  console.log("encryptedmtime:" + encryptedmtime)

  var MasterKey = sessionStorage.getItem("MasterKey");
  var encryptedfolderMasterKeyHashValue = await encryptKey(MasterKey, randomvalue, fmasterKey)
  var encryptedfolderMasterKey = new Uint8Array(encryptedfolderMasterKeyHashValue)
  console.log("encryptedfolderMasterKey:" + encryptedfolderMasterKey)

  let data = {
    "encryptedfname": encryptedfname,
    "EncryptedfolderMasterKey": encryptedfolderMasterKey,
    "Mtime":encryptedmtime

  };
  return data
}

//delete file
function DeleteFile(obj) {
  new File()
}

//download file
function DownloadFile(obj) {

}


/**
 *方法名：folderSingleCreate
 *作用：创建一级文件夹
 **/
function folderSingleCreate() {

  var path = document.getElementById('folder_single').value;

  if (!runObj.FolderExists(path)) {
    runObj.CreateFolder(path);
    alert('文件夹创建成功。');
  } else {
    alert('文件夹已存在。');

  }


}

function reset() {
  document.getElementById('email').value = '';
  document.getElementById('password').value = '';
  document.getElementById('passwordAgain').value = '';
}

function replace(x) {
  x.value = x.value.trim()
}

export function dateToString(date){
  var year = date.getFullYear();
  var month =(date.getMonth() + 1).toString();
  var day = (date.getDate()).toString();
  if (month.length == 1) {
    month = "0" + month;
  }
  if (day.length == 1) {
    day = "0" + day;
  }
  var hours = date.getHours().toString();
  if(hours.length == 1){
    hours = "0" + hours;
  }
  var mintus = date.getMinutes().toString();
  if(mintus.length == 1){
    mintus = "0" + mintus;
  }
  var second = date.getSeconds().toString();
  if(second.length == 1){
    second = "0" + second;
  }
  var dateTime = year + "-" + month + "-" + day + " " + hours + ":" +  mintus + ":" + second;
  return dateTime;
}
