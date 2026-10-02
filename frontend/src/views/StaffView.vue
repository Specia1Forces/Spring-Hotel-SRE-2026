<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api.js'

const maids = ref([])
const maintenance = ref([])
const error = ref('')
const maidForm = reactive({ firstName: '', middleName: '', lastName: '' })
const maintenanceForm = reactive({ firstName: '', middleName: '', lastName: '' })

async function load() {
  try {
    ;[maids.value, maintenance.value] = await Promise.all([
      api('/staff/maids'), api('/staff/maintenance'),
    ])
  } catch (requestError) { error.value = requestError.message }
}

async function create(path, form) {
  error.value = ''
  try {
    await api(path, { method: 'POST', body: form })
    Object.assign(form, { firstName: '', middleName: '', lastName: '' })
    await load()
  } catch (requestError) { error.value = requestError.message }
}

onMounted(load)
</script>

<template>
  <section>
    <p class="eyebrow">Команда</p><h1>Персонал</h1>
    <p v-if="error" class="error">{{ error }}</p>

    <div class="split-grid">
      <div>
        <form class="card form-grid single-column" @submit.prevent="create('/staff/maids', maidForm)">
          <h2>Новая горничная</h2>
          <label>Имя <input v-model.trim="maidForm.firstName" required /></label>
          <label>Отчество <input v-model.trim="maidForm.middleName" /></label>
          <label>Фамилия <input v-model.trim="maidForm.lastName" required /></label>
          <button class="button">Добавить</button>
        </form>
        <ul class="people-list">
          <li v-for="person in maids" :key="person.id">{{ person.lastName }} {{ person.firstName }} {{ person.middleName }}</li>
          <li v-if="!maids.length" class="muted">Список пуст</li>
        </ul>
      </div>

      <div>
        <form class="card form-grid single-column" @submit.prevent="create('/staff/maintenance', maintenanceForm)">
          <h2>Новый технический сотрудник</h2>
          <label>Имя <input v-model.trim="maintenanceForm.firstName" required /></label>
          <label>Отчество <input v-model.trim="maintenanceForm.middleName" /></label>
          <label>Фамилия <input v-model.trim="maintenanceForm.lastName" required /></label>
          <button class="button">Добавить</button>
        </form>
        <ul class="people-list">
          <li v-for="person in maintenance" :key="person.id">{{ person.lastName }} {{ person.firstName }} {{ person.middleName }}</li>
          <li v-if="!maintenance.length" class="muted">Список пуст</li>
        </ul>
      </div>
    </div>
  </section>
</template>
