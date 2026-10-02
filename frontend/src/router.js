import { createRouter, createWebHashHistory } from 'vue-router'
import { authIsLoaded, currentUser, hasAnyRole, loadCurrentUser } from './auth.js'
import AppLayout from './components/AppLayout.vue'
import LoginView from './views/LoginView.vue'
import DashboardView from './views/DashboardView.vue'
import ServicesView from './views/ServicesView.vue'
import ClientsView from './views/ClientsView.vue'
import ContractsView from './views/ContractsView.vue'
import BookingsView from './views/BookingsView.vue'
import AccountsView from './views/AccountsView.vue'
import StaffView from './views/StaffView.vue'
import LogsView from './views/LogsView.vue'

const managementRoles = ['MANAGER', 'ADMIN']

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/login', component: LoginView, meta: { public: true } },
    {
      path: '/',
      component: AppLayout,
      children: [
        { path: '', component: DashboardView },
        { path: 'services', component: ServicesView, meta: { roles: managementRoles } },
        { path: 'clients', component: ClientsView, meta: { roles: managementRoles } },
        { path: 'contracts', component: ContractsView, meta: { roles: managementRoles } },
        { path: 'bookings', component: BookingsView, meta: { roles: managementRoles } },
        { path: 'accounts', component: AccountsView, meta: { roles: managementRoles } },
        { path: 'staff', component: StaffView, meta: { roles: managementRoles } },
        { path: 'logs', component: LogsView },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

router.beforeEach(async (to) => {
  if (!authIsLoaded()) await loadCurrentUser()

  if (to.meta.public) {
    return currentUser.value ? '/' : true
  }

  if (!currentUser.value) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  if (to.meta.roles && !hasAnyRole(...to.meta.roles)) return '/'
  return true
})

export default router
