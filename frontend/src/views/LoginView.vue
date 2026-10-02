<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '../auth.js'

const route = useRoute()
const router = useRouter()
const form = reactive({ username: '', password: '' })
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await login(form.username, form.password)
    await router.push(route.query.redirect || '/')
  } catch (requestError) {
    error.value = requestError.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <form class="card login-card" @submit.prevent="submit">
      <p class="eyebrow">Управление гостиницей</p>
      <h1>Вход</h1>

      <label>
        Логин
        <input v-model.trim="form.username" autocomplete="username" required />
      </label>
      <label>
        Пароль
        <input v-model="form.password" type="password" autocomplete="current-password" required />
      </label>

      <p v-if="error" class="error">{{ error }}</p>
      <button class="button" :disabled="loading">{{ loading ? 'Входим…' : 'Войти' }}</button>
    </form>
  </main>
</template>
