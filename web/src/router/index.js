import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView';
import RankListIndexView from '@/views/ranklist/RankListIndexView';
import RecordIndexView from '@/views/record/RecordIndexView';
import RecordContentView from '@/views/record/RecordContentView';
import UserBotIndexView from '@/views/user/bot/UserBotIndexView';
import NotFound from '@/views/error/NotFound';
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView';
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView';
import UserAccountAcWingWebReceiveCodeView from '@/views/user/account/UserAccountAcWingWebReceiveCodeView';

import store from '@/store/index';

const routes = [
  {
    path: "/",
    name: "home",
    component: PkIndexView,
    meta:{
      requestAuth: true
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta:{
      requestAuth: true
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RankListIndexView,
    meta:{
      requestAuth: true
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta:{
      requestAuth: true
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta:{
      requestAuth: true
    }
  },
  {
    path: "/record/:recordId/",
    name: "record_content",
    component: RecordContentView,
    meta:{
      requestAuth: true
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta:{
      requestAuth: false
    }
  },
  {
    path: "/:catchAll(.*)/",
    redirect: "/404/",
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta:{
      requestAuth: false
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta:{
      requestAuth: false
    }
  },
  {
    path: "/user/account/acwing/web/receive_code",
    name: "user_account_acwing_web/receive_code",
    component: UserAccountAcWingWebReceiveCodeView,
    meta:{
      requestAuth: false
    }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if(to.meta.requestAuth && !store.state.user.is_login){
    next({name: 'user_account_login'})
  }else{
    next();
  }
})


export default router