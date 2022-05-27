<template>
  <div >
    <div class="bt" >

      <el-button type="text" size="medium" icon="el-icon-arrow-left" @click="back" v-if="path=='/'?false:true">返回上一级</el-button>
<!--      <el-button type="primary" size="medium" icon="el-icon-upload2" @click="dialogVisible = true">上传文件</el-button>-->
      <UploadFile id="uploadfile"></UploadFile>
      <el-button  size="medium" @click="addfolder = true" class="el-icon-folder-add">新建文件夹</el-button>

      <el-dialog
        title="输入文件夹名字"
        :visible.sync="addfolder"
        width="20%"
       >
        <el-input v-model="input" placeholder="请输入内容"></el-input>
        <span slot="footer" class="dialog-footer">
    <el-button @click="addfolder = false">取 消</el-button>
    <el-button type="primary" @click="newfolder">确 定</el-button>
  </span>
      </el-dialog>

      <div id="search"><input placeholder="请输入内容" class="search" v-model="keywords"/><i class="el-icon-search"></i>
      </div>
    </div>
    <el-table
      :data="tableData"
      style="width: 100%;margin-bottom: 20px;"
      row-key="id"

      default-expand-all
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column prop="dir" width="60" align="center">
      </el-table-column>
      <el-table-column prop="dir" width="60" align="center">
      <template slot-scope="scope">
        <img class='image' src="@/assets/afile.png"style="height: 30px;max-height: 100%;max-width: 100%">
      </template>
    </el-table-column>
      <el-table-column
        prop="filename"
        label="文件名"
        width="500">
      </el-table-column>
      <el-table-column
        prop="size"
        label="大小"
        width="270">
      </el-table-column>
      <el-table-column
        prop="time"
        label="修改时间"
        width="220">
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="{row,$index}">
          <el-tooltip class="item" effect="dark" content="下载" placement="bottom-start">
            <el-button type="text"><i class="el-icon-download" @click="download(row.name)" ></i></el-button>
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="删除" placement="bottom-start" >
            <el-button type="text"><i class="el-icon-delete" @click="del(row.name,$index)" ></i></el-button>
          </el-tooltip>

        </template>
      </el-table-column>
    </el-table>



  </div>

</template>

<script>
  import {newFolder,dateToString} from "../assets/js/pbkdf";
  import request from "../assets/js/request";
  import UploadFile from "./UploadFile";

  export default {
    name: 'Right',
    components:{
      UploadFile
    },
    data() {
      return {
        input:'',
        height: window.innerHeight -62 -80 -40 ,
        keywords: '',
        dialogVisible: false,
        loading:false,
        path:'/',
        username:localStorage.getItem('name'),
        addfolder:false,
        fileData:'',

        tableData: [{
          id: 1,
          filename: '1111111',
          size: '209Mb',
          time: '20.10.1'
        }, {
          id: 2,
          filename: '1111111',
          size: '209Mb',
          time: '20.10.1'
        }, {
          id: 3,
          filename: '1111111',
          size: '209Mb',
          time: '20.10.1',
          children: [{
            id: 31,
            filename: '1111111',
            size: '209Mb',
            time: '20.10.1',
            children: [{
              id: 311,
              filename: '1111111',
              size: '209Mb',
              time: '20.10.1',
            }, {
              id: 312,
              filename: '1111111',
              size: '209Mb',
              time: '20.10.1',
            }]
          }, {
            id: 32,
            filename: '1111111',
            size: '209Mb',
            time: '20.10.1',
          }]
        }],
      }
    },
    created() {
    this.init()
    },
    methods: {

      async init(){
        //获取文件目录
        // var userid=localStorage.getItem("uid",user.id);
        // //url
        // await request.post("'/files/'+userid", JSON.stringify({
        //     "email": _this.ruleForm.name
        //   })
        // ).then(async function (res) {
        //
        //   alert("请求后端成功" + JSON.stringify(res))
        //   if (res.code === 2000) {
        //     var filelist = res.data;
        //   }
        // })
        // this.tableData.push({id:3,filename:'1sdjf',  size:"20Mb", time:"20.10.1" })

      },
      search(key) {
        //搜索
        var newlist = []
        this.tableData.forEach(item => {
          if (item.name.indexOf(key) != -1) {
            newlist.push(item)
          }
        })
        return newlist
      },
      handleClose(done) {
        done();

      },

  //       beforeUpload(file){
  //         this.loading=true
  //   let fd = new FormData();
  //   fd.append('file',file);//传文件
  //   fd.append('sign',this.$sign);//传其他参数
  //   fd.append('username',localStorage.getItem('name'));//传其他参数
  //   fd.append('path',localStorage.getItem('path'));//传其他参数
  //         var that=this
  //   this.$http.post(this.$HOST+'v2/upload',fd).then(res=>{
  //
  //    if(res.data.code=='0'){
  //      this.dialogVisible=false
  //      that.tableData=[]
  //      that.$message({
  //        showClose: true,
  //        message: '上传成功',
  //        type: 'success'
  //      });
  //      that.loading=false
  //      this.init()
  //    }
  //   else if(res.data.code=='2'){
  //      that.$message({
  //        showClose: true,
  //        message: '文件已存在',
  //        type: 'warning'
  //      });
  //    }
  //     that.loading=false
  //   })
  // },
      //建立websocket连接
      beforeUpload(file){
        var socket;//websocket连接
        var webSocketUrl = 'ws://127.0.0.1:8033/upload/';
        var roomId = Number(Math.random().toString().substr(3, 3) + Date.now()).toString(36);//房间号，生成唯一的id

        createWebSocketConnect(roomId);//自动调用
        //创建websocket连接
        function createWebSocketConnect(roomId) {
          if (!socket) {//避免重复连接
            console.log(roomId);
            socket = new WebSocket(webSocketUrl + roomId);
            socket.onopen = function () {
              console.log("websocket已连接");
            };
            socket.onmessage = function (e) {
              if (uploadFlag) {
                //服务端发送的消息
                $("#message_content").append(e.data + '\n');
              }
            };
            socket.onclose = function () {
              console.log("websocket已断开");
            }
          }
        }
      },

      submitUpload(){
        var uploadFlag = true;//文件上传的标识
        var paragraph = 1024 * 1024;//文件分块上传大小
        var startSize, endSize = 0;//文件的起始大小和文件的结束大小
        var i = 0;//第几部分文件
        let filedata = fileObject;
      },
      del(name,index){
        this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.tableData.splice(index,1)
          this.$http.post(this.$HOST+'v2/delfile',this.$qs.stringify({
            sign:this.$sign,
            name:localStorage.getItem('path')+name,
            username:this.username
          })).then(res=>{
            if(res.data.code==0){
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
            }
            else{
              this.$message({
                type: 'warning',
                message: '删除失败!'
              });
            }

          })

        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });
        });

      },
      download(name){
        window.location.href=this.$HOST+'v2/download?username='+this.username+'&name='+name
      },

      newfolder(){

        let n =this.input
        var time = new Date()
        var mtime=dateToString(time)

        this.tableData.push({id: 4, filename:n, size: '-', time: mtime,})


        let fdata=newFolder(this.input)
        //发送后端加密文件名，mtime，用主密钥加密的密钥
        this.$http.post(this.$HOST+'v2/newfolder',this.$qs.stringify({
          "filename":fdata.encryptedfname,
          "mtime":fdata.Mtime,
          "encryptedfolderMasterKey": fdata.EncryptedfolderMasterKey
        })).then(res=>{
            if(res.data.code==0){
              this.addfolder=false
              this.input=''

              this.next('')
              this.$alert('创建文件夹成功', '提示', {
                confirmButtonText: '确定',
                callback: action => {
                  this.$message({
                    type: 'info',
                    message:"再见"
                  });
                }
              });
            }
            else{
              this.$alert('创建是失败，文件夹已存在', '提示', {
                confirmButtonText: '确定',
                callback: action => {
                  this.$message({
                    type: 'info',
                    message:"再见"
                  });
                }
              });
            }
        })
      },

      next(name)  {
        var newpath=localStorage.getItem('path')+name+'/'
        this.path=newpath
        this.$http.post(this.$HOST + 'v2/filelist', this.$qs.stringify({
          sign: this.$sign,
          username:localStorage.getItem('name'),
          path:newpath

        })).then(res => {
          localStorage.setItem('path',newpath)
          this.tableData=[]
          res.data.data.dir.forEach(item => {
            if (item.size == '') {
              var size = '-'
            } else {
              if (item.size < 1048576) {
                var size = (item.size / 1024).toFixed(2) + 'KB'
              } else if (item.size > 1048576 && item.size < 1073741824) {
                var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
              } else if (item.size > 1073741824) {
                var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
              }
            }
            this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
          })
          res.data.data.file.forEach(item => {
            if (item.size == '') {
              var size = '-'
            } else {
              if (item.size < 1048576) {
                var size = (item.size / 1024).toFixed(2) + 'KB'
              } else if (item.size > 1048576 && item.size < 1073741824) {
                var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
              } else if (item.size > 1073741824) {
                var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
              }
            }
            this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
          })
        })


      },
      back(){
       // console.log( localStorage.getItem('path').split('/'))
        var str=localStorage.getItem('path').split('/')
        str.splice(0,1)
        str.splice(str.length-1,1)
        str.splice(str.length-1,1)
        var backpath='/'
        str.forEach(item=>{
          backpath+=item+'/'
        })
        this.path=backpath
        this.$http.post(this.$HOST + 'v2/filelist', this.$qs.stringify({
          sign: this.$sign,
          username:localStorage.getItem('name'),
          path:backpath

        })).then(res => {
          localStorage.setItem('path',backpath)
          this.tableData=[]
          res.data.data.dir.forEach(item => {
            if (item.size == '') {
              var size = '-'
            } else {
              if (item.size < 1048576) {
                var size = (item.size / 1024).toFixed(2) + 'KB'
              } else if (item.size > 1048576 && item.size < 1073741824) {
                var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
              } else if (item.size > 1073741824) {
                var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
              }
            }
            this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
          })
          res.data.data.file.forEach(item => {
            if (item.size == '') {
              var size = '-'
            } else {
              if (item.size < 1048576) {
                var size = (item.size / 1024).toFixed(2) + 'KB'
              } else if (item.size > 1048576 && item.size < 1073741824) {
                var size = (item.size / 1024 / 1024).toFixed(2) + 'MB'
              } else if (item.size > 1073741824) {
                var size = (item.size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
              }
            }
            this.tableData.push({name: item.name, time: item.mtime, img: item.img, size: size})
          })
        })
      }
      }
    }
</script>
<style scoped>

  .bt {
    max-width: 100%;
    background-color: white;
    height: 40px;
    font: 12px/1.5 "Microsoft YaHei", arial, SimSun, "宋体";
    line-height: 30px;
  }
  .nav{
    max-width: 100%;
    background-color: white;
    height: 20px;
    /*font: 12px/1.5 "Microsoft YaHei", arial, SimSun, "宋体";*/
    font-size: 8px;
    line-height: 20px;
  }



  #search {
    width: 315px;
    border-radius: 33px;
    background-color: #f7f7f7;
    float: right;
    text-align: center;
    height: 32px;
    line-height: 32px;

  }

  .search {
    border: none;
    background-color: #f7f7f7;
    height: 30px;
    width: 248px;
  }

  img {
    width: 30px;
    height: 30px;
  }

  a {
    color: #424e67;
    text-decoration: none;
  }

  a:hover {
    color: #09AAFF;
  }
  .el-icon-delete{
    color:#F56C6C;
  }
  #uploadfile {
    display: inline-block
  }
</style>
