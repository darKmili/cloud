import { encryptKey, pbkdf2Function, uint8ArrayToString} from "./pbkdf";

export async function doregister($password) {


    // 生成两个128位随机数,一个作为客户端端随机数，一个作为主密钥
    let clientRandomValue = new Uint8Array(2 ** 4);
    let masterKey = new Uint8Array(2 ** 4);
    window.crypto.getRandomValues(clientRandomValue);
    window.crypto.getRandomValues(masterKey);
    // console.log("masterKey:" + masterKey);
    // console.log("clientRandomValue:" + clientRandomValue);

    let pbkdf2Key = await pbkdf2Function($password, uint8ArrayToString(clientRandomValue));

    const rawKey = await crypto.subtle.exportKey('raw', pbkdf2Key);
    const rawKeyArray = new Uint8Array(rawKey)

    const left128Bits = rawKeyArray.subarray(0, 16);
    const right128Bits = rawKeyArray.slice(16, rawKeyArray.length);

    // console.log("pbkdf2RawKey:" + rawKeyArray);
    // console.log("left128Bits Length:" + left128Bits.length + " Value:" + left128Bits);
    // console.log("right128Bits Length:" + right128Bits.length + " Value:" + right128Bits);
    // 验证哈希
    const hashBuffer = await crypto.subtle.digest('SHA-256', right128Bits)
    var sha256VerifyKey = new Uint8Array(hashBuffer);

    // console.log("masterKey:" + masterKey)
    var encryptedMasterKeyHashValue = await encryptKey(left128Bits, clientRandomValue, masterKey)
    var encryptedMasterKey = new Uint8Array(encryptedMasterKeyHashValue)
    // console.log("encryptedMasterKey:" + encryptedMasterKey)

    // var deKeyBuffer = await decryptKey(left128Bits, clientRandomValue, encryptedMasterKey)
    // var decryptedMasterKey = new Uint8Array(deKeyBuffer)
    // console.log("decryptedMasterKey:" + decryptedMasterKey)

    //Avoid password stole by server before interaction
    // document.getElementById('password').value = "OverwritePassword";
    // document.getElementById('passwordAgain').value = "OverwritePassword";

    console.log("sha256VerifyKey:" + sha256VerifyKey);
    alert("完成密钥的生成")
    /*What mainly to post: encryptedMasterKey, clientRandomValue, email, sha256VerifyKey
      1. Arraybuffer of Encrypted Master Key: 128 bit;
      2. Arraybuffer of Client Random Value: 128 bit;
      3. String of Email Address;
      3. Arraybuffer of hashed value with sha-256 to the right128Bits;
    */

    //start to post data with XMLHttpRequest()
    // let xhr = new XMLHttpRequest();
    // xhr.open("post", "", true);//post data to registerhandle.jsp page
  return {
      "clientRandomValue": uint8ArrayToString(clientRandomValue),
      "encryptedMasterKey": uint8ArrayToString(encryptedMasterKey),
      "verifyKey": uint8ArrayToString(sha256VerifyKey)
    }

}
