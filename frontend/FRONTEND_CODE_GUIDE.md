# Путеводитель по Vue frontend

Этот документ объясняет весь код каталога `frontend`: как запускается Vue, где хранится пользователь, как выполняются REST-запросы, как защищены маршруты и что делает каждый компонент.

## 1. Общая схема

```text
index.html
    ↓ подключает
src/main.js
    ↓ создаёт Vue-приложение
App.vue
    ↓ показывает текущий маршрут
Vue Router
    ↓ выбирает layout и view
View-компонент
    ↓ вызывает api(...)
Spring REST API
```

Frontend — одностраничное приложение, SPA. После первой загрузки переходы между экранами выполняет Vue Router без полной перезагрузки HTML.

Проект намеренно простой:

- Vue 3 Composition API;
- Vite;
- Vue Router;
- обычный `fetch` вместо Axios;
- небольшой reactive-модуль вместо Pinia;
- обычный CSS без UI-фреймворка;
- Session Cookie и CSRF вместо JWT.

## 2. Основные конструкции Vue

Перед разбором файлов полезно знать несколько конструкций:

- `ref(value)` создаёт реактивное значение. В JavaScript оно читается как `.value`, а в шаблоне Vue разворачивает его автоматически.
- `reactive(object)` создаёт реактивный объект, удобный для состояния формы.
- `computed(() => ...)` создаёт вычисляемое значение, которое обновляется при изменении зависимостей.
- `onMounted(callback)` запускает функцию после появления компонента на странице.
- `<script setup>` — компактный синтаксис Vue. Все переменные и функции из блока сразу доступны в `<template>`.
- `v-model` связывает поле ввода со значением формы в обе стороны.
- `v-if` условно показывает элемент.
- `v-for` повторяет элемент для каждого объекта массива.
- `@click` и `@submit` подписываются на события.
- `.prevent` отменяет стандартное действие браузера, например перезагрузку страницы при submit.
- `.trim` удаляет пробелы по краям строки.
- `.number` преобразует значение input в число.
- `RouterLink` выполняет переход без перезагрузки.
- `RouterView` показывает компонент активного маршрута.

## 3. Файлы сборки и запуска

### `package.json`

Описывает frontend как npm-проект.

Команды:

- `npm run dev` запускает Vite dev server;
- `npm run build` создаёт production-сборку в `dist`;
- `npm run preview` локально показывает готовую сборку.

Основные зависимости:

- `vue` — компоненты, реактивность и шаблоны;
- `vue-router` — маршруты;
- `vite` — dev server и сборка;
- `@vitejs/plugin-vue` — компиляция `.vue` файлов.

### `vite.config.js`

- `defineConfig(...)` создаёт конфигурацию Vite.
- `plugins: [vue()]` подключает обработку Vue Single File Components.
- `server.port = 5173` задаёт порт, разрешённый в CORS backend.

### `.env.example`

Содержит пример переменной:

```env
VITE_API_URL=http://localhost:8080/api/v1
```

Vite передаёт в браузер только переменные с префиксом `VITE_`. Для локального переопределения используется `.env.local`, который не попадает в Git.

### `index.html`

Единственный HTML-файл SPA:

- задаёт русский язык, кодировку, viewport и title;
- содержит пустой `<div id="app"></div>`;
- загружает `/src/main.js` как ES module.

Vue монтируется в `#app` и дальше сам управляет его содержимым.

### `.gitignore`

Исключает:

- `node_modules` — скачанные npm-зависимости;
- `dist` — результат сборки;
- локальные `.env` файлы.

`package-lock.json`, наоборот, хранится в проекте: он фиксирует точные версии зависимостей.

## 4. Точка входа

### `src/main.js`

Импортирует Vue, корневой компонент, router, функцию очистки авторизации и общие стили.

#### Обработчик `auth:unauthorized`

```js
window.addEventListener('auth:unauthorized', ...)
```

Срабатывает, когда API-клиент получает `401` от защищённого бизнес-маршрута:

1. запоминает текущий URL;
2. вызывает `clearAuthentication()`;
3. перенаправляет на `/login`;
4. передаёт исходный URL в `redirect`, чтобы после повторного входа вернуться назад.

#### Создание приложения

```js
createApp(App).use(router).mount('#app')
```

- `createApp(App)` создаёт приложение;
- `.use(router)` подключает маршрутизацию;
- `.mount('#app')` вставляет Vue в HTML-контейнер.

### `src/App.vue`

Корневой компонент содержит только `<RouterView />`. Он передаёт отображение страницы Vue Router. Для публичного login будет показан `LoginView`, для защищённых страниц — `AppLayout` с дочерним view.

## 5. API-клиент

### `src/api.js`

Центральный модуль всех запросов к Spring.

#### Состояние

- `API_URL` берётся из `VITE_API_URL`, иначе используется `http://localhost:8080/api/v1`.
- `csrf` хранит текущий CSRF-объект только в памяти вкладки. В `localStorage` он не записывается.

#### Класс `ApiError`

Расширяет стандартный `Error`.

- `constructor(message, status, fieldErrors = {})` сохраняет:
  - понятное сообщение;
  - HTTP-статус;
  - ошибки отдельных полей Spring Validation.

`status = 0` используется для сетевой ошибки, когда HTTP-ответ вообще не получен.

#### Функции

- `request(url, options)` — внутренний wrapper над `fetch`. Если backend недоступен, преобразует техническую ошибку браузера в понятный `ApiError`.
- `errorMessage(data, status)` — внутренне выбирает сообщение backend и добавляет `fieldErrors`, например `phone — must not be blank`.
- `refreshCsrf()` — вызывает публичный `GET /auth/csrf`, сохраняет полученный токен и возвращает его.
- `clearCsrf()` — удаляет токен из памяти.
- `api(path, options = {})` — универсальная функция REST-запроса:
  1. по умолчанию использует `GET`;
  2. добавляет `Accept: application/json`;
  3. при наличии body добавляет `Content-Type: application/json`;
  4. перед `POST`, `PATCH` и `DELETE` получает CSRF при необходимости;
  5. добавляет CSRF-заголовок;
  6. всегда отправляет cookie через `credentials: 'include'`;
  7. сериализует body через `JSON.stringify`;
  8. разбирает JSON-ответ;
  9. при `401` от бизнес-API создаёт событие `auth:unauthorized`;
  10. при другой ошибке выбрасывает `ApiError`;
  11. для `204` возвращает `null`, иначе возвращает JSON.

Все view-компоненты работают с backend только через `api()`, поэтому настройки cookie, JSON, CSRF и ошибок не дублируются.

## 6. Авторизация и общее состояние

### `src/auth.js`

Это маленькое глобальное хранилище вместо Pinia.

#### Реактивное состояние

```js
const state = reactive({
  user: null,
  loaded: false,
})
```

- `user` — текущий объект `{ username, roles }` или `null`;
- `loaded` — была ли уже выполнена первоначальная проверка сессии.

Экспортируемые computed-значения:

- `currentUser` — текущий пользователь;
- `isAuthenticated` — `true`, если пользователь существует.

#### Функции

- `hasAnyRole(...roles)` возвращает `true`, если пользователь имеет хотя бы одну переданную роль.
- `loadCurrentUser()` вызывает `GET /auth/me`. При `401` считает пользователя анонимным; при недоступном backend также позволяет открыть login. В `finally` устанавливает `loaded = true`.
- `login(username, password)`:
  1. получает CSRF;
  2. отправляет `POST /auth/login`;
  3. сохраняет пользователя;
  4. очищает старый CSRF;
  5. получает новый токен уже для авторизованной сессии;
  6. устанавливает `loaded = true`.
- `logout()` отправляет `POST /auth/logout`. Через `finally` очищает локальное состояние, даже если backend не ответил.
- `clearAuthentication()` удаляет пользователя, отмечает проверку выполненной и очищает CSRF.
- `authIsLoaded()` возвращает значение `loaded`; router использует его, чтобы не запрашивать `/auth/me` при каждом переходе.

Состояние хранится только в памяти. После обновления вкладки router заново вызывает `/auth/me`, а Spring узнаёт пользователя по HttpOnly cookie `JSESSIONID`.

## 7. Маршрутизация

### `src/router.js`

Используется `createWebHashHistory()`. Поэтому адрес имеет вид `http://localhost:5173/#/clients`. Часть после `#` обрабатывает браузер и Vue Router, поэтому статическому production-серверу не требуется fallback для `/clients`.

### Таблица маршрутов

| Путь | Компонент | Доступ |
|---|---|---|
| `/login` | `LoginView` | публичный |
| `/` | `DashboardView` | любой авторизованный |
| `/services` | `ServicesView` | MANAGER, ADMIN |
| `/clients` | `ClientsView` | MANAGER, ADMIN |
| `/contracts` | `ContractsView` | MANAGER, ADMIN |
| `/bookings` | `BookingsView` | MANAGER, ADMIN |
| `/accounts` | `AccountsView` | MANAGER, ADMIN |
| `/staff` | `StaffView` | MANAGER, ADMIN |
| `/logs` | `LogsView` | любой авторизованный |

Неизвестный путь перенаправляется на `/`.

### Navigation guard `router.beforeEach`

Выполняется перед каждым переходом:

1. один раз загружает пользователя через `loadCurrentUser()`;
2. не пускает авторизованного пользователя обратно на login;
3. неавторизованного отправляет на login и сохраняет исходный URL;
4. проверяет `meta.roles`;
5. пользователя без нужной роли возвращает на главную.

Скрытие ссылок во frontend — только удобство интерфейса. Реальную безопасность независимо проверяет Spring Security.

## 8. Общий layout

### `src/components/AppLayout.vue`

Оборачивает все защищённые экраны.

#### Состояние

- `router` получен через `useRouter()` для программного перехода.
- `canManage` — computed-проверка ролей `MANAGER`/`ADMIN`.

#### Метод

- `signOut()` вызывает `logout()`. Ошибка связи игнорируется, поскольку локальная сессия всё равно очищена, после чего пользователь переходит на `/login`.

#### Шаблон

- верхняя панель содержит бренд, меню, имя пользователя и кнопку выхода;
- управленческие ссылки показываются только при `canManage`;
- журналы доступны любому авторизованному пользователю;
- внутренний `<RouterView />` отображает выбранный дочерний экран.

## 9. Экраны

### `LoginView.vue`

#### Состояние

- `route` — текущий маршрут и его query-параметры;
- `router` — управление переходом;
- `form` — `{ username, password }`;
- `error` — текст ошибки;
- `loading` — блокировка кнопки во время запроса.

#### Метод `submit()`

Очищает ошибку, включает loading, вызывает `login()`, затем переходит на сохранённый `redirect` или на главную. В `catch` показывает ошибку, в `finally` выключает loading.

HTML-атрибуты `autocomplete` помогают менеджеру паролей браузера. Пароль не сохраняется во frontend.

### `DashboardView.vue`

- `canManage` вычисляет наличие роли MANAGER/ADMIN.
- Показывает имя пользователя и карточки разделов.
- Управленческие карточки скрываются от `HOTEL_MANAGEMENT_SERVICE`.
- Карточка журналов остаётся доступной всем авторизованным ролям.

Компонент не отправляет HTTP-запросы.

### `ServicesView.vue`

Полный CRUD дополнительных услуг.

#### Состояние

- `services` — список API;
- `error` — ошибка;
- `loading` — первоначальная/повторная загрузка;
- `form` — `{ id, name, price }`. Наличие `id` означает режим редактирования.

#### Функции

- `load()` вызывает `GET /services`, управляет loading и error.
- `edit(service)` копирует выбранную услугу в форму.
- `reset()` возвращает форму в режим создания.
- `save()` выбирает:
  - `POST /services`, если `id` отсутствует;
  - `PATCH /services/{id}`, если услуга редактируется.
  После сохранения очищает форму и обновляет список.
- `remove(service)` спрашивает подтверждение, вызывает `DELETE /services/{id}` и обновляет список.
- `onMounted(load)` автоматически загружает данные при открытии страницы.

### `ClientsView.vue`

#### Состояние

- `clients` — список клиентов;
- `error` — сообщение;
- `form` — поля `ClientRequest`: имя, отчество, фамилия, пол, дата рождения, адрес, телефон.

#### Функции

- `load()` вызывает `GET /clients`.
- `create()` вызывает `POST /clients`, очищает форму и повторно загружает таблицу.
- `onMounted(load)` запускает чтение при открытии.

Шаблон содержит форму, таблицу и ссылку на создание договора.

### `ContractsView.vue`

#### Состояние

- `clients` — варианты select;
- `error` и `success` — результат операции;
- `form` — `clientId`, `termOfStay`, `agreementDate`.

#### Функции

- callback `onMounted(...)` загружает клиентов через `GET /clients`.
- `create()` преобразует числовые поля, вызывает `POST /contracts`, показывает номер договора и очищает форму.

Списка договоров нет, потому что минимальный Spring API поддерживает только создание.

### `BookingsView.vue`

#### Состояние

- `bookings` — таблица бронирований;
- `clients` — select клиентов;
- `error`;
- `form` соответствует `BookingCreateRequest`.

#### Функции

- `load()` параллельно через `Promise.all` вызывает `GET /bookings` и `GET /clients`.
- `create()` приводит ID и сумму к числам, вызывает `POST /bookings`, очищает форму и обновляет данные.
- `onMounted(load)` выполняет первоначальную загрузку.

Проверка обязательности и минимальной суммы начинается в HTML, а порядок дат окончательно проверяет backend.

### `AccountsView.vue`

#### Состояние

- `accounts` — счета;
- `bookings` — список для выбора;
- `error`;
- `form` соответствует `AccountCreateRequest`.

#### Функции

- `load()` параллельно вызывает `GET /accounts` и `GET /bookings`.
- `create()` преобразует все числа, вызывает `POST /accounts`, очищает форму и обновляет таблицу.
- `onMounted(load)` загружает страницу.

### `StaffView.vue`

#### Состояние

- `maids` — горничные;
- `maintenance` — технические сотрудники;
- `error`;
- `maidForm` и `maintenanceForm` содержат одинаковые поля ФИО.

#### Функции

- `load()` параллельно получает `/staff/maids` и `/staff/maintenance`.
- `create(path, form)` — общая функция для двух форм: отправляет POST на переданный URL, очищает нужную форму и обновляет оба списка.
- `onMounted(load)` выполняет первоначальную загрузку.

### `LogsView.vue`

Статический компонент без `<script>` и API-запросов. Он показывает два направления будущего функционала:

- журнал уборки;
- журнал технического осмотра.

Это соответствует исходной MVC-версии, где раздел был только навигационной страницей.

## 10. Общие стили

### `src/styles.css`

CSS разделён логически:

- `:root`, `body`, `*` — базовые шрифты, цвета и `box-sizing`;
- `button`, `.button`, `.secondary`, `.link-button` — основные и текстовые кнопки;
- `.topbar`, `.brand`, `.nav`, `.user-menu` — верхняя навигация;
- `.page`, `.narrow-page`, `.title-row` — ширина и компоновка страниц;
- `.card`, `.dashboard-grid`, `.dashboard-card` — карточки и главная панель;
- `.form-grid`, `.single-column`, `.wide`, `label`, `input`, `select` — формы;
- `.table-wrap`, `table`, `th`, `td` — таблицы с горизонтальным скроллом;
- `.error`, `.success`, `.muted`, `.empty` — состояния интерфейса;
- `.login-page`, `.login-card` — экран входа;
- `.split-grid`, `.people-list` — две колонки персонала;
- media queries для ширины `820px` и `520px` переводят интерфейс в мобильную компоновку.

CSS глобальный: scoped-стили в компонентах не используются, чтобы не дублировать оформление одинаковых форм и таблиц.

## 11. Соответствие frontend и Spring

| Vue-файл | REST API | Spring controller |
|---|---|---|
| `auth.js`, `LoginView` | `/auth/csrf`, `/auth/login`, `/auth/me`, `/auth/logout` | `AuthRestController` |
| `ServicesView` | `/services`, `/services/{id}` | `ServiceRestController` |
| `ClientsView` | `/clients` | `ClientRestController` |
| `ContractsView` | `/clients`, `/contracts` | `ClientRestController`, `ContractRestController` |
| `BookingsView` | `/clients`, `/bookings` | `ClientRestController`, `BookingRestController` |
| `AccountsView` | `/bookings`, `/accounts` | `BookingRestController`, `AccountRestController` |
| `StaffView` | `/staff/maids`, `/staff/maintenance` | `StaffRestController` |
| `LogsView` | нет запросов | пока нет REST-контроллера |

## 12. Пример полного сценария входа

```text
Пользователь открывает /#/login
    ↓
router вызывает GET /auth/me
    ↓ 401, если сессии нет
Пользователь вводит manager / пароль
    ↓
auth.login получает CSRF
    ↓
POST /auth/login + cookie + CSRF
    ↓
Spring создаёт JSESSIONID
    ↓
auth.login получает новый CSRF для этой сессии
    ↓
router открывает главную страницу
```

## 13. Пример создания клиента

```text
ClientsView form
    ↓ v-model
reactive form object
    ↓ create()
api('/clients', { method: 'POST', body: form })
    ↓ JSON + JSESSIONID + X-CSRF-TOKEN
ClientRestController
    ↓ 201 Created
create() очищает форму
    ↓
load() обновляет таблицу
```

## 14. Где хранится состояние

| Данные | Место хранения | Срок жизни |
|---|---|---|
| Текущий пользователь | reactive `state` в `auth.js` | до обновления вкладки |
| Авторизованная сессия | HttpOnly `JSESSIONID` cookie | пока действует Spring-сессия |
| CSRF | переменная `csrf` в `api.js` | до обновления вкладки/logout |
| Данные таблиц | `ref` внутри view | пока открыт компонент |
| Данные форм | `reactive` внутри view | пока открыт компонент |

Пароль, session ID и CSRF не сохраняются в `localStorage`.

## 15. Что пока намеренно отсутствует

- Pinia/Vuex;
- Axios;
- TypeScript;
- UI-компонентная библиотека;
- frontend unit/e2e-тесты;
- пагинация, поиск и фильтры;
- полноценные журналы;
- комнаты и документы;
- JWT и refresh token.

Это не забытые зависимости, а решение сохранить первую версию небольшой и понятной.

## 16. Рекомендуемый порядок чтения

1. `index.html` и `main.js` — как приложение запускается.
2. `router.js` — какие страницы существуют.
3. `auth.js` — как хранится пользователь.
4. `api.js` — как frontend общается со Spring.
5. `AppLayout.vue` — общее меню.
6. `LoginView.vue` — простой пример формы и перехода.
7. `ClientsView.vue` — простой список + POST.
8. `ServicesView.vue` — полный CRUD.
9. Остальные views — варианты того же шаблона.
10. `styles.css` — оформление и адаптивность.

После этого весь frontend читается как несколько небольших повторяющихся сценариев: загрузить список, заполнить форму, отправить JSON, показать ошибку и обновить список.
