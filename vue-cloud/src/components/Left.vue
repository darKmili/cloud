<template>
  <el-container>
    <el-header>
      <el-menu
        :default-active="$route.path"
        router
        class="el-menu-vertical-demo"

      >
        <el-menu-item index="/right" >
          <i class="el-icon-tickets"></i>
          <span slot="title">全部文件</span>
        </el-menu-item>
        <el-menu-item index="/picture" >
          <i class="el-icon-picture"></i>
          <span slot="title">图片</span>
        </el-menu-item>
        <el-menu-item index="/document" >
          <i class="el-icon-document"></i>
          <span slot="title" >文档</span>
        </el-menu-item>
        <el-menu-item index="/camera">
          <i class="el-icon-video-camera"></i>
          <span slot="title" >视频</span>
        </el-menu-item>
        <el-menu-item index="/music">
          <i class="el-icon-service"></i>
          <span slot="title" >音乐</span>
        </el-menu-item>
        <el-menu-item index="/fileShare">
          <i class="el-icon-chat-square"></i>
          <span slot="title" >接受文件</span>
        </el-menu-item>
      </el-menu>
    </el-header>
    <el-main></el-main>
    <el-footer>
      <el-progress :text-inside="false" :stroke-width="10" :percentage="used" :show-text="false"></el-progress>
      <el-breadcrumb separator="/" style="margin-top: 5px;font-size: 12px;">
        <el-breadcrumb-item>{{ used }}MB</el-breadcrumb-item>
        <el-breadcrumb-item>{{ total }}MB</el-breadcrumb-item>
      </el-breadcrumb>
    </el-footer>
  </el-container>
</template>

<script>
export default {
  name: "Left",
  data() {
    return {
      used:0,
      total:0,
    }
  },

  created() {
    this.used = Math.ceil(parseFloat(localStorage.getItem("usedCapacity"))/1024/1024);
    this.total = Math.ceil(parseFloat(localStorage.getItem("totalCapacity"))/1024/1024);
  },

}
</script>

<style scoped>
.el-menu {
  background-color: #f7f7f7;
  border: 0;
  /*text-align: center;*/

}

.item {
  color: #424e67;
  font-size: 14px;
}

.el-progress-bar__outer {

}
</style>
