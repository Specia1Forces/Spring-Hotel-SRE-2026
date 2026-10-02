<script setup>
import { onMounted, reactive, ref } from 'vue'
import { api } from '../api.js'

const accounts = ref([])
const bookings = ref([])
const error = ref('')
const form = reactive({ bookingId: '', amountDue: 0, prepaymentAmount: 0, additionalServicesAmount: 0 })

async function load() {
  try { ;[accounts.value, bookings.value] = await Promise.all([api('/accounts'), api('/bookings')]) }
  catch (requestError) { error.value = requestError.message }
}

async function create() {
  error.value = ''
  try {
    await api('/accounts', {
      method: 'POST',
      body: {
        bookingId: Number(form.bookingId), amountDue: Number(form.amountDue),
        prepaymentAmount: Number(form.prepaymentAmount),
        additionalServicesAmount: Number(form.additionalServicesAmount),
      },
    })
    Object.assign(form, { bookingId: '', amountDue: 0, prepaymentAmount: 0, additionalServicesAmount: 0 })
    await load()
  } catch (requestError) { error.value = requestError.message }
}

onMounted(load)
</script>

<template>
  <section>
    <p class="eyebrow">Оплата</p><h1>Счета</h1>
    <form class="card form-grid" @submit.prevent="create">
      <h2>Новый счёт</h2>
      <label>Бронирование
        <select v-model="form.bookingId" required>
          <option disabled value="">Выберите бронирование</option>
          <option v-for="booking in bookings" :key="booking.id" :value="booking.id">
            №{{ booking.id }} — {{ booking.clientName }}
          </option>
        </select>
      </label>
      <label>К оплате <input v-model.number="form.amountDue" type="number" min="0" required /></label>
      <label>Предоплата <input v-model.number="form.prepaymentAmount" type="number" min="0" required /></label>
      <label>Доп. услуги <input v-model.number="form.additionalServicesAmount" type="number" min="0" required /></label>
      <div class="actions wide"><button class="button">Создать счёт</button></div>
    </form>
    <p v-if="error" class="error">{{ error }}</p>
    <div class="table-wrap">
      <table>
        <thead><tr><th>ID</th><th>Бронирование</th><th>К оплате</th><th>Предоплата</th><th>Доп. услуги</th></tr></thead>
        <tbody>
          <tr v-for="account in accounts" :key="account.id">
            <td>{{ account.id }}</td><td>№{{ account.bookingId }}</td><td>{{ account.amountDue }}</td>
            <td>{{ account.prepaymentAmount }}</td><td>{{ account.additionalServicesAmount }}</td>
          </tr>
          <tr v-if="!accounts.length"><td colspan="5" class="empty">Счетов пока нет</td></tr>
        </tbody>
      </table>
    </div>
  </section>
</template>
