<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api.js'

const bookings = ref([])
const clients = ref([])
const error = ref('')
const form = reactive({
  clientId: '', checkInDate: '', checkOutDate: '', totalAmount: 0,
  prepaymentStatus: '', bookingStatus: '', residenceStatus: '',
})

async function load() {
  try {
    ;[bookings.value, clients.value] = await Promise.all([api('/bookings'), api('/clients')])
  } catch (requestError) { error.value = requestError.message }
}

async function create() {
  error.value = ''
  try {
    await api('/bookings', {
      method: 'POST',
      body: { ...form, clientId: Number(form.clientId), totalAmount: Number(form.totalAmount) },
    })
    Object.assign(form, {
      clientId: '', checkInDate: '', checkOutDate: '', totalAmount: 0,
      prepaymentStatus: '', bookingStatus: '', residenceStatus: '',
    })
    await load()
  } catch (requestError) { error.value = requestError.message }
}

onMounted(load)
</script>

<template>
  <section>
    <div class="title-row">
      <div><p class="eyebrow">Размещение</p><h1>Бронирования</h1></div>
      <RouterLink class="button secondary" to="/accounts">Перейти к счетам</RouterLink>
    </div>

    <form class="card form-grid" @submit.prevent="create">
      <h2>Новое бронирование</h2>
      <label>Клиент
        <select v-model="form.clientId" required>
          <option disabled value="">Выберите клиента</option>
          <option v-for="client in clients" :key="client.id" :value="client.id">
            {{ client.lastName }} {{ client.firstName }}
          </option>
        </select>
      </label>
      <label>Дата заезда <input v-model="form.checkInDate" type="date" required /></label>
      <label>Дата выезда <input v-model="form.checkOutDate" type="date" required /></label>
      <label>Общая сумма <input v-model.number="form.totalAmount" type="number" min="0" required /></label>
      <label>Статус предоплаты <input v-model.trim="form.prepaymentStatus" required /></label>
      <label>Статус бронирования <input v-model.trim="form.bookingStatus" required /></label>
      <label>Статус проживания <input v-model.trim="form.residenceStatus" required /></label>
      <div class="actions wide"><button class="button">Создать бронирование</button></div>
    </form>

    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Клиент</th><th>Заезд</th><th>Выезд</th><th>Сумма</th><th>Статусы</th></tr></thead>
        <tbody>
          <tr v-for="booking in bookings" :key="booking.id">
            <td>{{ booking.id }}</td><td>{{ booking.clientName }}</td>
            <td>{{ booking.checkInDate }}</td><td>{{ booking.checkOutDate }}</td><td>{{ booking.totalAmount }}</td>
            <td>{{ booking.prepaymentStatus }} / {{ booking.bookingStatus }} / {{ booking.residenceStatus }}</td>
          </tr>
          <tr v-if="!bookings.length"><td colspan="6" class="empty">Бронирований пока нет</td></tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
