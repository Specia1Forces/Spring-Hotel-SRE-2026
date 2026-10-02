<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api.js'

const services = ref([])
const error = ref('')
const loading = ref(true)
const form = reactive({ id: null, name: '', price: 0 })

async function load() {
  loading.value = true
  error.value = ''
  try {
    services.value = await api('/services')
  } catch (requestError) {
    error.value = requestError.message
  } finally {
    loading.value = false
  }
}

function edit(service) {
  Object.assign(form, service)
}

function reset() {
  Object.assign(form, { id: null, name: '', price: 0 })
}

async function save() {
  error.value = ''
  try {
    const path = form.id ? `/services/${form.id}` : '/services'
    const method = form.id ? 'PATCH' : 'POST'
    await api(path, { method, body: { name: form.name, price: Number(form.price) } })
    reset()
    await load()
  } catch (requestError) {
    error.value = requestError.message
  }
}

async function remove(service) {
  if (!window.confirm(`Удалить услугу «${service.name}»?`)) return
  try {
    await api(`/services/${service.id}`, { method: 'DELETE' })
    await load()
  } catch (requestError) {
    error.value = requestError.message
  }
}

onMounted(load)
</script>

<template>
  <section>
    <p class="eyebrow">Каталог</p>
    <h1>Дополнительные услуги</h1>

    <form class="card form-grid compact-form" @submit.prevent="save">
      <h2>{{ form.id ? 'Изменить услугу' : 'Новая услуга' }}</h2>
      <label>Название <input v-model.trim="form.name" required /></label>
      <label>Цена <input v-model.number="form.price" type="number" min="0" required /></label>
      <div class="actions">
        <button class="button">{{ form.id ? 'Сохранить' : 'Добавить' }}</button>
        <button v-if="form.id" class="button secondary" type="button" @click="reset">Отмена</button>
      </div>
    </form>

    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="loading" class="muted">Загрузка…</p>
    <div v-else class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Название</th><th>Цена</th><th></th></tr></thead>
        <tbody>
          <tr v-for="service in services" :key="service.id">
            <td>{{ service.id }}</td><td>{{ service.name }}</td><td>{{ service.price }}</td>
            <td class="row-actions">
              <button class="link-button" @click="edit(service)">Изменить</button>
              <button class="link-button danger" @click="remove(service)">Удалить</button>
            </td>
          </tr>
          <tr v-if="!services.length"><td colspan="4" class="empty">Услуг пока нет</td></tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
