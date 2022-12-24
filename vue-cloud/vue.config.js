module.exports = {
  devServer: {                //记住，别写错了devServer//设置本地默认端口  选填
    port: 8081,
    proxy: {                 //设置代理，必须填
      '/api': {              //设置拦截器  拦截器格式   斜杠+拦截器名字，名字可以自己定
        target: 'http://localhost:8080',     //代理的目标地址
        ws: true,                          // 支持websocket
        changeOrigin: true,              //是否设置同源，输入是的
        pathRewrite: {                   //路径重写
          '^/api': ''                     //选择忽略拦截器里面的内容，当后端服务器没有api路径
        }
      }
    },
    headers: {'Access-Control-Allow-Origin': '*',}

  },
  chainWebpack(config) {
    // set worker-loader
    config.module
      .rule('worker')
      .test(/\.worker\.js$/)
      .use('worker-loader')
      .loader('worker-loader')
      .end();

    // 解决：worker 热更新问题
    config.module.rule('js').exclude.add(/\.worker\.js$/);
  },
  parallel: false,
  chainWebpack: config => {
    // 解决：“window is undefined”报错，这个是因为worker线程中不存在window对象，因此不能直接使用，要用this代替
    config.output.globalObject('this')
  }

}
