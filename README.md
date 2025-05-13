# 🛡️ Proselyte Event & Notification App with Keycloak

## Описание

Микросервисное приложение из двух компонентов:

- `eventapp`: сервис генерации событий и отправки уведомлений
- `notificationapp`: сервис приёма и хранения уведомлений
- `Keycloak`: обеспечивает OAuth2 авторизацию между сервисами и внешними клиентами

Все взаимодействия между сервисами защищены JWT токенами с ролями, выданными через Keycloak.

## Архитектура

```
+------------+       REST       +-----------------+
|  eventapp  +----------------> | notificationapp |
|            |  Bearer Token   |                 |
+------------+                 +-----------------+
       |
       | Token via Client Credentials
       v
   [ Keycloak ]
```

## Эндпоинты

🔗 [OpenAPI Spec: EventApp](eventapp/openapi.yml)  
🔗 [OpenAPI Spec: NotificationApp](notificationapp/openapi.yml)

### EventApp (`localhost:8091`)

#### GET `/api/v1/events`
Получить список событий

```bash
curl -X GET "http://localhost:8091/api/v1/events?page=0&size=10" \
  -H "Authorization: Bearer <access_token>"
```

#### POST `/api/v1/events`
Создать новое событие

```bash
curl -X POST "http://localhost:8091/api/v1/events" \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
        "type": "UPLOAD",
        "description": "Загрузка файла"
      }'
```

### NotificationApp (`localhost:8092`)

#### POST `/internal/api/v1/notifications`
Создать уведомление

```bash
curl -X POST "http://localhost:8092/internal/api/v1/notifications" \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
        "message": "System alert"
      }'
```

#### GET `/internal/api/v1/notifications/{uid}`
Получить уведомление по UID

```bash
curl -X GET "http://localhost:8092/internal/api/v1/notifications/0c2e6bde-d45d-4b2b-9c67-45fd5b5e0d3e" \
  -H "Authorization: Bearer <access_token>"
```

---

## Быстрый старт через Docker Compose

```bash
docker-compose up
```

Это запустит:
- Keycloak (`localhost:9090`)
- EventApp (`localhost:8091`)
- NotificationApp (`localhost:8092`)
- PostgreSQL (`localhost:5433`)

> Предполагается, что вы уже настроили realm `proselyte` и клиент `eventapp` в Keycloak.

## Локальный запуск без Docker

### 1. Запуск Keycloak

Запуск контейнера отдельно:
```bash
docker-compose up keycloak
```

### 2. Настройка окружения

Убедитесь, что у вас настроены следующие переменные в `application.yml`:

#### EventApp
```yaml
spring.security.oauth2.client.registration.keycloak.client-id: eventapp
spring.security.oauth2.client.registration.keycloak.client-secret: <your-secret>
notificationapp.url: http://localhost:8092
```

#### NotificationApp
```yaml
spring.security.oauth2.resourceserver.jwt.issuer-uri: http://localhost:9090/realms/proselyte
```

### 3. Сборка и запуск вручную

```bash
./gradlew :eventapp:bootRun
./gradlew :notificationapp:bootRun
```

## Конфигурация Keycloak (вручную)

1. Realm: `proselyte`
2. Client: `eventapp`
   - Client ID: `eventapp`
   - Client Secret: `...`
   - Client Authentication: `client_credentials`
   - Enable `Service Accounts`
   - Add Role: `eventapp.user`, `eventapp.admin`
3. Client: `notificationapp`
   - Role: `notificationapp.role_internal_access`
4. Настройте маппинг `roles` в токен (realm roles → claim `roles`)

## Проверка

Получение токена от `eventapp`:
```bash
curl -X POST   http://localhost:9090/realms/proselyte/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials&client_id=eventapp&client_secret=<your-secret>'
```

---

## Структура проекта

```
proselyte-keycloak-course/
├── docker-compose.yml
├── build.gradle
├── settings.gradle
├── gradle.properties
├── eventapp/
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/net/proselyte/eventapp/
│       │   │   ├── client/
│       │   │   ├── config/
│       │   │   ├── dto/
│       │   │   ├── rest/
│       │   │   ├── service/
│       │   │   └── EventappApplication.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
├── notificationapp/
│   ├── build.gradle
│   ├── Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/net/proselyte/notificationapp/
│       │   │   ├── config/
│       │   │   ├── dto/
│       │   │   ├── exception/
│       │   │   ├── rest/
│       │   │   ├── service/
│       │   │   └── NotificationAppApplication.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
└── gradle/
    └── wrapper/
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

## Стек технологий
- Java 24
- Spring Boot 3 (Web, Security, OAuth2, Jackson)
- Keycloak 26
- Docker / Docker Compose
- Gradle

## Bетки:
- STEP1 - начальный коммит с docker-compose.yml для запуска keycloak
- STEP2 - базовое eventapp REST API без security
- STEP3 - добавлен Dockerfile для удобного запуска eventapp
- STEP4 - добавлены настройки безопасности для eventapp
- STEP5 - добавлены настройки авторизации
- STEP6 - добавлена базовая реализация notificationapp и межсервисное взаимодействие без настроек безопасности
- STEP7 - добавлены настройки безопасности для межсервисного взаимодействия 

## Автор
[Eugene Suleimanov](https://github.com/proselytear)
