import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'
import { isMock } from '@/mock/mock'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/login-page.vue') },
    {
      path: '/',
      component: () => import('@/views/layout-container.vue'),
      redirect: '/device',
      children: [
        {
          path: '/drink/base',
          component: () => import('@/components/drink-base-info.vue')
        },
        {
          path: '/drink/realtime',
          component: () => import('@/components/drink-real-time-info.vue')
        },
        {
          path: '/device',
          component: () => import('@/components/device-management.vue')
        },
      ]
    }
  ]
})

if (!isMock) {
  router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    if (to.path !== '/login' && !userStore.token) {
      next({ path: '/login' })
    } else next()
  })
}


export default router
