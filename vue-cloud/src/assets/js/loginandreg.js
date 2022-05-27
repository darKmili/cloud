async function login() {
    let $email = $("#email")
    let $password = $("#password");

    sessionStorage.setItem('key', $("password"));

    // 检查邮箱格式密码格式是否正确
    if ($email === null || $password === null || $password.val().length === 0 || $password.val().length > 16 || !isEmail($email.val())) {
        console.log("邮箱格式不正确或者密码格式为不正确");
        return ;
    }


    // 1、检查用户是否存在,如果存在，则返回用户随机数
    let checkData = null;
    $.ajax({
        url: "/user/check",
        method: "post",
        async: false,
        data: {email:$email.val()},
        success: function (data) {
            checkData = data;
        }
    });

    if(checkData.code!==0){
        alert(checkData.message);
        return;
    }
    // clientRandomValue 此时是个字符串
    let  clientRandomValue = checkData.data;
    console.log("clientRandomValue------"+clientRandomValue)
    // 2、计算验证哈希值，发送到后端验证，验证成功，就跳转到主页
    let pbkdf2Key = await pbkdf2Function($password.val(), clientRandomValue);

    const rawKey = await crypto.subtle.exportKey('raw', pbkdf2Key);
    const rawKeyArray = new Uint8Array(rawKey)

    const left128Bits = rawKeyArray.subarray(0, 16);
    const right128Bits = rawKeyArray.slice(16, rawKeyArray.length);



    const hashBuffer = await crypto.subtle.digest('SHA-256', right128Bits)
    console.log("-------------------"+hashBuffer+"-------------------------------")
    var sha256VerifyKey = new Uint8Array(hashBuffer);
    // var sha256VerifyKey =hashBuffer.toString();
    console.log("sha256VerifyKey----------" + sha256VerifyKey);
    var loginData = {
        email:$email.val(),
        sha256VerifyKey:uint8ArrayToString(sha256VerifyKey)
    }


    $.ajax({
        url: "/user/login",
        data: JSON.stringify(loginData),
        method: "post",
        async: true,
        contentType :'application/json',
        dataType:'json',
        success: function (data) {
            if (data.code === 0) {
                setTimeout("window.location.href='" + data.data +"'" , 500);
            } else {
                alert("账号密码错误");
                window.location.href = "#";
            }
        }
    })
}

