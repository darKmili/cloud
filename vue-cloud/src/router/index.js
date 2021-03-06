import Vue from 'vue'
import Router from 'vue-router'

import Home from "../views/Home";
import Login from "../views/Login";
import Register from "../views/Register";

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/Home',
      name: 'Home',
      component: Home
    },
    {
      path: '/',
      name: 'Login',
      component: Login
    },
    {
      path: '/Register',
      name: 'Register',
      component: Register
    }

  ]
})
