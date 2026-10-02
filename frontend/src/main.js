import { createApp } from 'vue'
import App from './App.vue'
import router from './router.js'
import { clearAuthentication } from './auth.js'
import './styles.css'

window.addEventListener('auth:unauthorized', () => {
  const currentPath = router.currentRoute.value.fullPath
  clearAuthentication()

  if (router.currentRoute.value.path !== '/login') {
    router.replace({ path: '/login', query: { redirect: currentPath } })
  }
})

createApp(App).use(router).mount('#app')
