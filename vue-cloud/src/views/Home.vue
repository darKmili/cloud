<template>
  <el-container>
    <el-header>
      <Header></Header>
    </el-header>
    <el-container>
      <el-aside>
        <Left v-bind:total="total" v-bind:used="used"></Left>
      </el-aside>
      <el-main>
       <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<script>

import Header from '../components/Header'
import Left from '../components/Left'

export default {
  name: "Home",

  data() {
    return {
      total:0,
      used:0,
    }
  },
  components: {
    Header,
    Left
  },
  created() {
    if (localStorage.getItem('uid') === undefined||localStorage.getItem('uid') === ''  ) {
      this.$confirm('您还未登陆/或者登陆已过期', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$router.push({path: '/'})
      }).catch(() => {
        this.$router.push({path: '/'})
      });
    }else {
      this.used = Math.ceil(parseFloat(localStorage.getItem("usedCapacity"))/1024/1024);
      this.total = Math.ceil(parseFloat(localStorage.getItem("totalCapacity"))/1024/1024);
    }

  },
  methods: {}
}
</script>

<style scoped>
.el-header {
  width: 100%;
  box-shadow: 0 2px 6px 0 rgba(0, 0, 0, .05);

}

.el-container {
  height: 100%;

}

.el-aside {
  background-color: #f7f7f7;
  max-width: 194px;

}

.el-main {
  height: calc(100% - 62px);
  max-width: calc(100% - 194px);
}


</style>
