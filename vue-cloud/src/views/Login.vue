<template>
  <div class="main">
    <el-card class="box-card" v-loading="loading" element-loading-text="正在登陆，请稍后">
      <div slot="header" class="clearfix">
        <span>登陆</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="register">还没有账号？去注册
        </el-button>
      </div>
      <el-row type="flex" justify="center">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
          <el-form-item prop="name">
            <el-input v-model="ruleForm.name " placeholder="邮箱"></el-input>
          </el-form-item>
          <el-form-item prop="pass">
            <el-input v-model="ruleForm.pass" show-password placeholder="密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="login('ruleForm')" class="login">登陆</el-button>
          </el-form-item>
        </el-form>
      </el-row>
    </el-card>


  </div>
</template>

<script>
import {isEmail,dec, pbkdf2Function, uint8ArrayToString, decryptKey,stringtoUint8Array} from "../assets/js/pbkdf";
import request from "../assets/js/request"

export default {
  name: "Login",

  data() {
    var name = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入邮箱'));
      } else if (!isEmail(value)) {
        callback(new Error('邮箱格式不正确'));
      } else {
        callback();
      }
    };
    var pass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        callback();
      }
    };
    return {
      loading: false,
      ruleForm: {
        name: '',
        pass: ''
      },
      rules: {
        name: [
          {validator: name, trigger: 'blur'},

        ],
        pass: [
          {validator: pass, trigger: 'blur'},
        ],

      }

    }
  }
  ,
  methods: {
    async login(ruleForm) {
      // 防止以前的数据没有清除，影响调试
      localStorage.clear();

      var randomvalue = null;
      var verify = null;
      let _this = this;
      // 先验证账号
      await this.$refs.ruleForm.validate(async (valid) => {
        if (valid) {
          _this.loading = true
        } else {
          console.log('error submit!!');
          return false;
        }
      });


      await request.post("/users/check", JSON.stringify({
          "email": _this.ruleForm.name
        })
      ).then(async function (res) {

        alert("请求后端成功" + JSON.stringify(res))
        if (res.code === 2000) {
          randomvalue = res.data;
          // sessionStorage只能存储字符串数据，无法直接存储数组类型和JSON对象,TODO关注
          // 可以无需存储
          sessionStorage.setItem('randomValue', randomvalue)
          // 计算256验证哈希
        }
      })


      let pbkdf2Key = await pbkdf2Function(_this.ruleForm.pass, randomvalue);

      const rawKey = await crypto.subtle.exportKey('raw', pbkdf2Key);
      const rawKeyArray = new Uint8Array(rawKey)

      const left128Bits = rawKeyArray.subarray(0, 16);
      // 只能保存字符串
      sessionStorage.setItem('left128Bits', uint8ArrayToString(left128Bits))
      const right128Bits = rawKeyArray.slice(16, rawKeyArray.length);

      const hashBuffer = await crypto.subtle.digest('SHA-256', right128Bits)
      console.log("-----------" + hashBuffer + "---------------")
      var sha256VerifyKey = new Uint8Array(hashBuffer);
      console.log("sha256VerifyKey---" + sha256VerifyKey);
      verify = uint8ArrayToString(sha256VerifyKey)
      alert("验证哈希"+verify)


      await request.post("/users/login", JSON.stringify({
        "email": _this.ruleForm.name,
        "verifyKey": verify,
      })).then(async function (res) {
        console.log((JSON.stringify(res)))

        // 响应成功的数据 res

        // {
        //   "code": 2000,
        //   "message": "请求成功",
        //   "data": {
        //   "id": 1,
        //     "email": "1227642494@qq.com",
        //     "name": "leon",
        //     "face": null,
        //     "clientRandomValue": "aaaaaa",
        //     "encryptedMasterKey": "aaaaaa",
        //     "curLoadTime": null,
        //     "lastLoadTime": null,
        //     "registerTime": null,
        //     "usedCapacity": 0,
        //     "totalCapacity": 60000,
        //     "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyYW5kb21WYWx1ZSI6ImFhYWFhYSIsImlkIjoxLCJleHAiOjE2NTM0OTEwOTYsImVtYWlsIjoiMTIyNzY0MjQ5NEBxcS5jb20ifQ.Yh2m3MiIuE1beMflIEIAMJrZAg1GPoiK_INoDpD5-Xw"
        // }
        // }




        if (res.code === 2000) {
          _this.loading = false
          var  user = res.data
          //获取加密后的主密钥然后解密存储,TODO
          var encryptedMasterKey = stringtoUint8Array( user.encryptedMasterKey)
          let randomvalue = stringtoUint8Array( user.clientRandomValue);
          const left128Bits = stringtoUint8Array(sessionStorage.getItem("left128Bits"));

           // var deKeyBuffer = await decryptKey(left128Bits, randomvalue, encryptedMasterKey)
          // var decryptedMasterKey = new Uint8Array(deKeyBuffer)
          // console.log("decryptedMasterKey---" + decryptedMasterKey);


          console.log(encryptedMasterKey)
          console.log(randomvalue)
          console.log(left128Bits)

          // 将返回的数据全部放到 localStorage 中

          function saveUser(user){
            localStorage.setItem("uid",user.id);
            localStorage.setItem("email",user.email);
            localStorage.setItem("name",user.name);
            localStorage.setItem("clientRandomValue",user.clientRandomValue);
            localStorage.setItem("verifyKey",user.verifyKey);
            // 这里应该存解密好了的主密钥
            localStorage.setItem("encryptedMasterKey",user.encryptedMasterKey);
            localStorage.setItem("curLoadTime",user.curLoadTime);
            localStorage.setItem("lastLoadTime",user.lastLoadTime);
            localStorage.setItem("registerTime",user.registerTime);
            localStorage.setItem("usedCapacity",user.usedCapacity);
            localStorage.setItem("totalCapacity",user.totalCapacity);
          }
          saveUser(user)
          // TODO 解密主密钥





          _this.$alert('登陆成功', '提示', {
            confirmButtonText: '确定',
            callback: action => {
              _this.$router.push({path: 'Home'})
            }
          })
          setTimeout(() => {
            _this.$router.push({path: '/Home'})
          }, 1000)
        } else {
          _this.loading = false
          _this.$alert(res.message, '提示', {
            confirmButtonText: '确定',
          })
        }


      })
      var eKey = stringtoUint8Array(localStorage.getItem("encryptedMasterKey"))
      let ran = stringtoUint8Array( localStorage.getItem("clientRandomValue"));
      var decryptedMasterKey=await dec(left128Bits, ran, eKey)
      localStorage.setItem('masterKey', uint8ArrayToString(decryptedMasterKey))
      var asterKey=stringtoUint8Array(localStorage.getItem("masterKey"));
      console.log(asterKey)

    },
    resetForm(ruleForm) {
      this.$refs.ruleForm.resetFields();
    },
    register() {
      this.$router.push({path: 'Register'})
    },
    home() {
      this.$router.push({path: '/Home'})
    }
  }
}
</script>

<style scoped>
a {
  color: white;
  text-decoration: none;
}

a:hover {
  color: gray;
}

.main {
  background: url("../assets/desert.jpg") no-repeat;
  width: 100%;
  height: 100%;
  background-size: 100% 100%;
}

.box-card {
  max-width: 400px;
  min-width: 320px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);

}

.login {
  width: 100%;
}

</style>

