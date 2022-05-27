import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  //全局状态
  state:{
list:[]
  },
  mutations:{
init(state){
  state.list=[]
}
  },
  getters:{

  },
  actions:{

  }
})

export default store
