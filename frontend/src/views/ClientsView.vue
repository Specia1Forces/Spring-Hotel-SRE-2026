<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api.js'

const clients = ref([])
const error = ref('')
const form = reactive({
  firstName: '', middleName: '', lastName: '', gender: '', birthDate: '', address: '', phone: '',
})

async function load() {
  try {
    clients.value = await api('/clients')
  } catch (requestError) {
    error.value = requestError.message
  }
}

async function create() {
  error.value = ''
  try {
    await api('/clients', { method: 'POST', body: form })
    Object.assign(form, {
      firstName: '', middleName: '', lastName: '', gender: '', birthDate: '', address: '', phone: '',
    })
    await load()
  } catch (requestError) {
    error.value = requestError.message
  }
}

onMounted(load)
</script>

<template>
  <section>
    <div class="title-row">
      <div><p class="eyebrow">Гости</p><h1>Клиенты</h1></div>
      <RouterLink class="button secondary" to="/contracts">Создать договор</RouterLink>
    </div>

    <form class="card form-grid" @submit.prevent="create">
      <h2>Новый клиент</h2>
      <label>Имя <input v-model.trim="form.firstName" required /></label>
      <label>Отчество <input v-model.trim="form.middleName" /></label>
      <label>Фамилия <input v-model.trim="form.lastName" required /></label>
      <label>Пол <input v-model.trim="form.gender" /></label>
      <label>Дата рождения <input v-model="form.birthDate" type="date" required /></label>
      <label>Телефон <input v-model.trim="form.phone" required /></label>
      <label class="wide">Адрес <input v-model.trim="form.address" /></label>
      <div class="actions wide"><button class="button">Добавить клиента</button></div>
    </form>

    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>ФИО</th><th>Дата рождения</th><th>Телефон</th><th>Адрес</th></tr></thead>
        <tbody>
          <tr v-for="client in clients" :key="client.id">
            <td>{{ client.id }}</td>
            <td>{{ client.lastName }} {{ client.firstName }} {{ client.middleName }}</td>
            <td>{{ client.birthDate }}</td><td>{{ client.phone }}</td><td>{{ client.address }}</td>
          </tr>
          <tr v-if="!clients.length"><td colspan="5" class="empty">Клиентов пока нет</td></tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
