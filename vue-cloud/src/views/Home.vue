<template>
  <el-container>
    <el-header>
      <Header></Header>
    </el-header>
    <el-container>
      <el-aside>
        <Left></Left>
      </el-aside>
      <el-main>
        <Right></Right>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>

import Header from '../components/Header'
import Right from '../components/Right'
import Left from '../components/Left'

export default {
  name: "Home",

  data() {
    return {}
  },
  components: {
    Header,
    Right,
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
