<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { currentUser, hasAnyRole, logout } from '../auth.js'

const router = useRouter()
const canManage = computed(() => hasAnyRole('MANAGER', 'ADMIN'))

async function signOut() {
  try {
    await logout()
  } catch {
    // Локальная авторизация уже очищена в logout, даже если backend недоступен.
  }
  await router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">Spring Hotel</RouterLink>
      <nav class="nav">
        <RouterLink to="/">Главная</RouterLink>
        <RouterLink v-if="canManage" to="/services">Услуги</RouterLink>
        <RouterLink v-if="canManage" to="/clients">Клиенты</RouterLink>
        <RouterLink v-if="canManage" to="/contracts">Договоры</RouterLink>
        <RouterLink v-if="canManage" to="/bookings">Бронирования</RouterLink>
        <RouterLink v-if="canManage" to="/accounts">Счета</RouterLink>
        <RouterLink v-if="canManage" to="/staff">Персонал</RouterLink>
        <RouterLink to="/logs">Журналы</RouterLink>
      </nav>
      <div class="user-menu">
        <span>{{ currentUser?.username }}</span>
        <button class="button secondary" type="button" @click="signOut">Выйти</button>
      </div>
    </header>

    <main class="page">
      <RouterView />
    </main>
  </div>
</template>
