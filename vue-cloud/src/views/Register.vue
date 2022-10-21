<template>
  <div class="main">

    <el-card class="box-card" v-loading="loading" :element-loading-text="ruleForm.loading_text">
      <div slot="header" class="clearfix">
        <span>注册</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="login">已经有账号？去登陆
        </el-button>
      </div>
      <el-row type="flex" justify="center">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" class="demo-ruleForm">
          <el-form-item prop="name">
            <el-input v-model="ruleForm.name" placeholder="邮箱"></el-input>
          </el-form-item>
          <el-form-item prop="pass">
            <el-input v-model="ruleForm.pass" show-password placeholder="密码"></el-input>
          </el-form-item>
          <el-form-item prop="repass">
            <el-input v-model="ruleForm.repass" show-password placeholder="确认密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="register('ruleForm')" class="register">注册</el-button>
          </el-form-item>
        </el-form>


      </el-row>
    </el-card>


  </div>
</template>

<script>
import {isEmail} from "../assets/js/pbkdf";
import {doregister} from "../assets/js/register";
import request from "../assets/js/request";

export default {
  name: "Register",
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
      } else if (value.length < 6) {
        callback(new Error('至少为六个字符'));
      } else {
        callback();
      }
    };
    var repass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else if (value !== this.ruleForm.pass) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };

    return {
      loading: false,
      ruleForm: {
        name: '',
        pass: '',
        repass: '',
        loading_text: ''

      },
      rules: {
        name: [
          {validator: name, trigger: 'blur'},

        ],
        pass: [
          {validator: pass, trigger: 'blur'},
        ],
        repass: [
          {validator: repass, trigger: 'blur'},
        ],
      }
    }
  }
  ,
  methods: {
    login() {
      this.$router.push({path: '/'})
    },
    async register(ruleForm) {
      let _this = this

      await this.$refs.ruleForm.validate((valid) => {

        if (valid) {
          _this.loading = true
          _this.loading_text = '正在注册，请稍后'
        } else {
          console.log('error submit!!');
          return false;
        }
      })

      let value = await doregister(_this.ruleForm.pass)

      value.email = _this.ruleForm.name

      alert(JSON.stringify(value))


      request.post("/users/register", JSON.stringify(value)).then(res => {
        if (res.code === 2000) {
          this.$alert('注册成功', '提示', {
            confirmButtonText: '确定',
            callback: action => {
              this.$router.push({path: '/'})
            }

          })

        } else {
          this.loading = false
          this.$refs.ruleForm.resetFields();
          this.$alert(res.msg, '提示', {
            confirmButtonText: '确定',
          })
        }
      })
    },
    home() {
      this.$router.push({path: '/'})
    },
    resetForm(ruleForm) {
      this.$refs.ruleForm.resetFields();
    },
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

.box-card {
  max-width: 400px;
  min-width: 320px;
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);

}

.main {
  background: url("../assets/desert.jpg") no-repeat;
  width: 100%;
  height: 100%;
  background-size: 100% 100%;
}

.register {
  width: 100%;
}
</style>
