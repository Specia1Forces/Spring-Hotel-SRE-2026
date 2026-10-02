<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api.js'

const clients = ref([])
const error = ref('')
const success = ref('')
const form = reactive({ clientId: '', termOfStay: 1, agreementDate: '' })

onMounted(async () => {
  try { clients.value = await api('/clients') } catch (requestError) { error.value = requestError.message }
})

async function create() {
  error.value = ''
  success.value = ''
  try {
    const result = await api('/contracts', {
      method: 'POST',
      body: { ...form, clientId: Number(form.clientId), termOfStay: Number(form.termOfStay) },
    })
    success.value = `Договор №${result.id} создан`
    Object.assign(form, { clientId: '', termOfStay: 1, agreementDate: '' })
  } catch (requestError) { error.value = requestError.message }
}
</script>

<template>
  <section class="narrow-page">
    <p class="eyebrow">Клиенты</p><h1>Новый договор</h1>
    <form class="card form-grid single-column" @submit.prevent="create">
      <label>Клиент
        <select v-model="form.clientId" required>
          <option disabled value="">Выберите клиента</option>
          <option v-for="client in clients" :key="client.id" :value="client.id">
            {{ client.lastName }} {{ client.firstName }} (ID {{ client.id }})
          </option>
        </select>
      </label>
      <label>Срок проживания, дней <input v-model.number="form.termOfStay" type="number" min="1" required /></label>
      <label>Дата заключения <input v-model="form.agreementDate" type="date" required /></label>
      <p v-if="error" class="error">{{ error }}</p><p v-if="success" class="success">{{ success }}</p>
      <button class="button">Создать договор</button>
    </form>
  </section>
</template>
