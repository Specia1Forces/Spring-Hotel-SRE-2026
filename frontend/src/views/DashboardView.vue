<script setup>
import { computed } from 'vue'
import { currentUser, hasAnyRole } from '../auth.js'

const canManage = computed(() => hasAnyRole('MANAGER', 'ADMIN'))
</script>

<template>
  <section>
    <p class="eyebrow">Добро пожаловать, {{ currentUser?.username }}</p>
    <h1>Управление гостиницей</h1>
    <p class="muted">Выберите нужный раздел. В интерфейсе оставлен только минимальный функционал.</p>

    <div class="dashboard-grid">
      <RouterLink v-if="canManage" class="dashboard-card" to="/clients">
        <strong>Клиенты и договоры</strong>
        <span>Список клиентов и создание записей</span>
      </RouterLink>
      <RouterLink v-if="canManage" class="dashboard-card" to="/bookings">
        <strong>Бронирования и счета</strong>
        <span>Регистрация проживания и оплаты</span>
      </RouterLink>
      <RouterLink v-if="canManage" class="dashboard-card" to="/staff">
        <strong>Персонал</strong>
        <span>Горничные и технические сотрудники</span>
      </RouterLink>
      <RouterLink v-if="canManage" class="dashboard-card" to="/services">
        <strong>Дополнительные услуги</strong>
        <span>Просмотр и полный CRUD</span>
      </RouterLink>
      <RouterLink class="dashboard-card" to="/logs">
        <strong>Журналы</strong>
        <span>Уборка и технический осмотр</span>
      </RouterLink>
    </div>
  </section>
</template>
