# Отчёт по экспорту и сбору метрик в VictoriaMetrics

## 1. Назначение приложения

Spring Hotel — клиент-серверное веб-приложение для управления гостиницей. Система позволяет вести данные клиентов, договоров и бронирований, управлять услугами и счетами, а также разграничивает доступ сотрудников с помощью Spring Security.

Приложение развёрнуто в однодовом Kubernetes-кластере Minikube. В рамках данной работы к нему добавлены экспорт технических и бизнес-метрик, мониторинг PostgreSQL и централизованный сбор метрик в VictoriaMetrics.

## 2. Использованный стек

- Java 25;
- Spring Boot 4.1.1;
- Spring Boot Actuator;
- Micrometer;
- Spring Security;
- Spring Data JPA и Hibernate;
- PostgreSQL 17;
- Liquibase;
- Vue 3 и Vite;
- Docker;
- Kubernetes и Minikube;
- Helm;
- VictoriaMetrics single-node;
- vmagent;
- postgres_exporter.

## 3. Общая схема мониторинга

Для сбора метрик используется Pull-модель. Приложение и PostgreSQL exporter предоставляют HTTP-endpoint с метриками, а vmagent периодически обращается к ним и передаёт полученные данные в VictoriaMetrics.

```text
Spring Boot backend
  /actuator/prometheus:8080
             │
             │ Pull
             ▼
          vmagent ── remote write ──► VictoriaMetrics
             ▲                           │
             │ Pull                      └─ хранение и запросы через vmui
             │
postgres_exporter:9187
             │
             ▼
        PostgreSQL
```

VictoriaMetrics, vmagent, backend и PostgreSQL работают внутри одного Minikube-кластера.

## 4. Экспорт метрик Spring Boot

Для экспорта метрик backend подключены зависимости:

- `spring-boot-starter-actuator`;
- `micrometer-registry-prometheus`.

Spring Boot Actuator формирует метрики приложения и JVM, а Micrometer преобразует их в формат, совместимый с Prometheus/OpenMetrics.

Endpoint метрик:

```text
/actuator/prometheus
```

Он включён в `application.properties`:

```properties
management.endpoints.web.exposure.include=health,prometheus
```

Endpoint `/actuator/prometheus` разрешён в Spring Security без авторизации. Во внешнюю сеть он специально не публикуется: vmagent обращается к нему внутри Kubernetes-кластера.

Примеры экспортируемых технических метрик:

- `jvm_memory_used_bytes` — использованная память JVM;
- `jvm_threads_live_threads` — количество активных потоков JVM;
- `process_cpu_usage` — использование процессора Java-процессом;
- `http_server_requests_seconds_count` — число обработанных HTTP-запросов;
- метрики пула соединений HikariCP.

## 5. Бизнес-метрики

В приложении реализован компонент `HotelBusinessMetrics`. Он создаёт четыре счётчика Micrometer типа `Counter`.

| Метрика в VictoriaMetrics | Событие, увеличивающее значение | Назначение |
|---|---|---|
| `hotel_auth_login_success_total` | Успешная авторизация пользователя | Показывает количество успешных входов в систему |
| `hotel_clients_created_total` | Успешное создание клиента | Показывает количество операций создания клиентов |
| `hotel_contracts_created_total` | Успешное создание договора | Показывает количество операций создания договоров |
| `hotel_bookings_created_total` | Успешное создание бронирования | Показывает количество операций создания бронирований |

Счётчик увеличивается только после успешного выполнения соответствующей операции. Например, метрика создания клиента изменяется после сохранения клиента в PostgreSQL.

Счётчики находятся в памяти процесса backend. После перезапуска Pod их текущие значения начинаются заново, однако ранее собранные временные ряды остаются в VictoriaMetrics до окончания срока хранения.

## 6. Экспорт метрик PostgreSQL

Для PostgreSQL используется готовый экспортёр `postgres_exporter`:

```text
quay.io/prometheuscommunity/postgres-exporter:v0.20.1
```

Экспортёр добавлен вторым контейнером в Pod `postgres-0`. Контейнеры одного Pod используют общее сетевое пространство, поэтому экспортёр подключается к PostgreSQL через `localhost:5432`.

Endpoint метрик PostgreSQL:

```text
http://<IP-Pod>:9187/metrics
```

Учётные данные подключения передаются экспортёру из Kubernetes `ConfigMap` и `Secret`. Пароль не записывается непосредственно в образ контейнера или исходный код.

Пример проверочной метрики:

```promql
pg_up{namespace="hotel"}
```

Значение `1` означает, что exporter успешно подключился к PostgreSQL и предоставляет его метрики.

## 7. Service Discovery

vmagent обнаруживает цели динамически через Kubernetes Service Discovery с ролью `pod`. Поиск ограничен namespace `hotel`.

В Pod backend указаны аннотации:

```yaml
prometheus.io/scrape: "true"
prometheus.io/port: "8080"
prometheus.io/path: /actuator/prometheus
```

В Pod PostgreSQL указаны аннотации:

```yaml
prometheus.io/scrape: "true"
prometheus.io/port: "9187"
prometheus.io/path: /metrics
```

Конфигурация relabeling vmagent:

1. оставляет только Pod с аннотацией `prometheus.io/scrape: "true"`;
2. выбирает контейнерный порт, совпадающий с `prometheus.io/port`;
3. устанавливает путь из `prometheus.io/path`;
4. добавляет метки `namespace`, `pod`, `container` и `node`.

Таким образом, IP-адреса Pod не записываются в конфигурацию вручную. При пересоздании Pod vmagent автоматически обнаруживает новую цель.

## 8. VictoriaMetrics и vmagent

VictoriaMetrics развёрнута официальным Helm-чартом в namespace `monitoring` в single-node режиме. Данные сохраняются на PersistentVolumeClaim. Срок хранения метрик установлен в семь дней.

vmagent также развёрнут Helm-чартом. Интервал сбора метрик составляет 15 секунд. Собранные данные отправляются в VictoriaMetrics по адресу:

```text
http://victoria-metrics.monitoring.svc.cluster.local:8428/api/v1/write
```

При проверке страницы vmagent `/targets` были обнаружены две активные цели:

| Цель | Endpoint | Состояние |
|---|---|---|
| PostgreSQL exporter | `http://<IP-Pod>:9187/metrics` | `UP` |
| Spring Boot backend | `http://<IP-Pod>:8080/actuator/prometheus` | `UP` |

Обе цели находились в состоянии `UP`, а число ошибок сбора было равно нулю.

## 9. Развёртывание

Создание namespace:

```powershell
kubectl create namespace monitoring --dry-run=client -o yaml | kubectl apply -f -
```

Установка VictoriaMetrics:

```powershell
helm upgrade --install vm-single vm/victoria-metrics-single --namespace monitoring --values .\monitoring\victoria-metrics-values.yaml --wait --timeout 5m
```

Установка vmagent:

```powershell
helm upgrade --install vmagent vm/victoria-metrics-agent --namespace monitoring --values .\monitoring\vmagent-values.yaml --wait --timeout 5m
```

Проверка ресурсов:

```powershell
kubectl get pods -n monitoring
kubectl get svc -n monitoring
kubectl get pods -n hotel
```

## 10. Проверка сбора метрик

Для просмотра целей vmagent выполняется проброс порта Deployment:

```powershell
kubectl port-forward deployment/vmagent-victoria-metrics-agent -n monitoring 8429:8429
```

Страница целей:

```text
http://localhost:8429/targets
```

Для доступа к VictoriaMetrics выполняется:

```powershell
kubectl port-forward svc/victoria-metrics -n monitoring 8428:8428
```

Интерфейс запросов:

```text
http://localhost:8428/vmui/
```

Использованные проверочные запросы:

### Доступность целей

```promql
up{namespace="hotel"}
```

Значение `1` подтверждает успешный сбор данных с цели.

### Метрики JVM

```promql
jvm_memory_used_bytes{namespace="hotel"}
```

Запрос подтверждает поступление runtime-метрик Java-приложения.

### Метрики PostgreSQL

```promql
pg_up{namespace="hotel"}
```

Значение `1` подтверждает работоспособность PostgreSQL exporter.

### Бизнес-метрики

```promql
{namespace="hotel", __name__=~"hotel_.*_total"}
```

Запрос выводит все четыре бизнес-счётчика гостиничного приложения.

Дополнительно список доступных имён метрик можно получить командой:

```powershell
curl.exe "http://localhost:8428/api/v1/label/__name__/values"
```

## 11. Результат

В Minikube развёрнута VictoriaMetrics и настроен автоматический сбор метрик с использованием vmagent и Kubernetes Service Discovery. Backend экспортирует метрики JVM, Spring Boot и четыре собственные бизнес-метрики. PostgreSQL наблюдается через отдельный контейнер `postgres_exporter`.

В ходе проверки vmagent обнаружил backend и PostgreSQL exporter автоматически. Обе цели имели состояние `UP`, а собранные метрики были доступны для запросов в VictoriaMetrics.

