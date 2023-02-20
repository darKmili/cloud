import Vue from 'vue'
import Router from 'vue-router'

import Home from "../views/Home";
import Login from "../views/Login";
import Register from "../views/Register";
import Right from "../components/Right";
import Picture from "../components/Picture";
import Music from "../components/Music";
import Document from "../components/Document";
import Camera from "../components/Camera";
import FileShare from "../components/FileShare";
Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/home',
      name: 'Home',
      component: Home,
      redirect: '/right'
      ,
      children: [
        {
          path: '/right',
          name: 'Right',
          component: Right
        },
        {
          path: '/picture',
          name: 'Picture',
          component: Picture
        }, {
          path: '/music',
          name: 'Music',
          component: Music
        }, {
          path: '/document',
          name: 'Document',
          component: Document
        }, {
          path: '/camera',
          name: 'Camera',
          component: Camera
        }, {
          path: '/fileShare',
          name: 'FileShare',
          component: FileShare

        }

      ]
    },
    {
      path: '/',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
  ]
})
