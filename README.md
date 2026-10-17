# Hotel Booking — финальная работа по Spring Framework

Полноценный учебный REST-бэкенд сервиса бронирования отелей по **11 заданиям** из PDF курса «Разработка на Spring Framework». Реализовано управление каталогом отелей, комнатами, пользователями, бронированием, рейтингом, поиском с фильтрами и пагинацией, а также событийной статистикой через Kafka и MongoDB с выгрузкой CSV. Административная CMS представлена защищёнными REST-методами; **графический интерфейс CMS в задании не требуется**.

## Технологии

| Компонент | Технология |
| --- | --- |
| Runtime | Java 21, Spring Boot 3.5.7, Maven |
| HTTP API | Spring Web MVC, Bean Validation |
| Данные сервиса | Spring Data JPA, PostgreSQL 16, Flyway |
| Маппинг | MapStruct 1.6.3 |
| Безопасность | Spring Security, HTTP Basic, BCrypt |
| Фильтрация | JPA Specification |
| События | Apache Kafka (+ ZooKeeper) |
| Статистика | Spring Data MongoDB, MongoDB 7 |
| Развёртывание | Dockerfile, Docker Compose |
| Тестирование | JUnit 5, Mockito |

## Быстрый запуск через Docker Compose

Требуются Docker и плагин Docker Compose. Из корневой директории проекта:

```bash
cp .env.example .env
# обязательно измените APP_ADMIN_PASSWORD и POSTGRES_PASSWORD в .env

docker compose up --build -d
docker compose ps
docker compose logs -f app
```

Порт REST API: `http://localhost:8080`. Автоматически запустятся `app`, `postgres`, `mongodb`, `zookeeper`, `kafka`. Flyway создаст схему PostgreSQL. При первом старте будет создан администратор с логином/паролем из `.env` (если его ещё нет). Пароль администратора — не менее 12 символов. В контейнере автоматически используются внутренние имена сервисов (`postgres`, `mongodb`, `kafka`).

**Внимание:** сервисы могут стартовать не мгновенно; дождитесь готовности Kafka и лога успешного запуска Spring Boot. Для остановки `docker compose down`, для полной очистки данных `docker compose down -v` (удаляет содержимое БД!).

## Локальный запуск без Docker для приложения

Нужны JDK 21, Maven 3.9+, работающие PostgreSQL 16, Kafka, MongoDB. Базы/топики создаются автоматически на старте (БД `hotels` должна существовать):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/hotels
export DB_USERNAME=hotels
export DB_PASSWORD=hotels
export MONGODB_URI=mongodb://localhost:27017/hotel_statistics
export KAFKA_BOOTSTRAP_SERVERS=localhost:29092
export APP_ADMIN_USERNAME=admin
export APP_ADMIN_EMAIL=admin@example.com
export APP_ADMIN_PASSWORD='change_this_admin_password'
mvn clean test
mvn spring-boot:run
```

Можно поднять инфраструктуру только в Docker, а Spring запустить локально:

```bash
# сначала создайте .env
docker compose up -d postgres mongodb zookeeper kafka
mvn spring-boot:run
```

Для jar: `mvn clean package` и `java -jar target/hotel-booking-1.0.0.jar`. Тесты из `src/test/java` выполняются `mvn test` и не требуют подключения к БД, так как это unit-тесты. Для проверки реальных SQL-запросов, ZooKeeper/Kafka и сети обязательно выполните ручной сценарий из `docs/TESTING.md` с запущенными контейнерами.

## Первый сценарий API

### 1. Регистрация обычного пользователя (без Basic Auth)

```bash
curl -i -X POST http://localhost:8080/api/users/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"guest1","email":"guest1@example.org","password":"Password123!","role":"USER"}'
```

### 2. Создание отеля (ADMIN)

```bash
curl -i -u admin:change_this_admin_password \
  -X POST http://localhost:8080/api/hotels \
  -H 'Content-Type: application/json' \
  -d '{"name":"Aurora Hotel","advertisementTitle":"Отель у центра","city":"Москва","address":"Тверская, 10","distanceFromCenter":1.50}'
```

Ожидается `201 Created` с `id`, `rating: 0.0`, `ratingCount: 0`. В следующих примерах предположим, что ID отеля `1`.

### 3. Создание номера (ADMIN)

```bash
curl -i -u admin:change_this_admin_password \
  -X POST http://localhost:8080/api/rooms \
  -H 'Content-Type: application/json' \
  -d '{"title":"Комфорт","description":"Двухместный номер","roomNumber":"201","price":6800.00,"maxGuests":2,"hotelId":1,"unavailableDates":["2026-11-12"]}'
```

Предположим, получен ID комнаты `1`.

### 4. Найти доступный номер (USER)

```bash
curl -u guest1:Password123! 'http://localhost:8080/api/rooms?hotelId=1&guestCount=2&minPrice=1000&maxPrice=8000&checkIn=2026-11-02&checkOut=2026-11-05&page=0&size=10'
```

### 5. Забронировать (USER)

```bash
curl -i -u guest1:Password123! \
  -X POST http://localhost:8080/api/bookings \
  -H 'Content-Type: application/json' \
  -d '{"roomId":1,"checkIn":"2026-11-02","checkOut":"2026-11-05"}'
```

Сервис сам определяет ID пользователя из HTTP Basic, **передавать чужой userId при бронировании нельзя**. Повторное бронирование этого же номера на пересекающийся период вернёт `400`.

### 6. Выставить оценку (USER или ADMIN)

```bash
curl -i -u guest1:Password123! -X POST http://localhost:8080/api/hotels/1/rating \
  -H 'Content-Type: application/json' -d '{"mark":5}'
```

### 7. Скачать статистику (ADMIN)

```bash
curl -u admin:change_this_admin_password \
  http://localhost:8080/api/statistics/export -o statistics.csv
```

События доставляются асинхронно через Kafka: для появления новых строк CSV нужно дождаться их обработки.

## Полный список REST-маршрутов

| Метод | URL | Доступ | Назначение |
| --- | --- | --- | --- |
| `POST` | `/api/users/register` | без авторизации | Регистрация `USER` |
| `POST` | `/api/users` | ADMIN | Создание `USER` или `ADMIN` |
| `GET` | `/api/users/{id}`, `/api/users/{id}/edit` | пользователь — свой ID, ADMIN — любой | Пользователь / DTO для редактирования |
| `GET` | `/api/users/by-username/{username}` | свой логин / ADMIN | Поиск по имени |
| `PUT`, `DELETE` | `/api/users/{id}` | ADMIN | Изменение / удаление пользователя |
| `GET` | `/api/hotels`, `/api/hotels/{id}`, `/api/hotels/{id}/edit` | USER, ADMIN | Поиск / чтение / DTO для редактирования |
| `POST` | `/api/hotels` | ADMIN | Добавить отель |
| `PUT`, `DELETE` | `/api/hotels/{id}` | ADMIN | Редактирование / удаление отеля |
| `POST` | `/api/hotels/{id}/rating` | USER, ADMIN | Выставить оценку `1–5` |
| `GET` | `/api/hotels/{id}/photos` | USER, ADMIN | Фотографии отеля |
| `POST`, `DELETE` | `/api/hotels/{id}/photos[/{photoId}]` | ADMIN | Управление фотографиями |
| `GET` | `/api/rooms`, `/api/rooms/{id}`, `/api/rooms/{id}/edit` | USER, ADMIN | Поиск / чтение / DTO для редактирования |
| `POST` | `/api/rooms` | ADMIN | Добавить номер |
| `PUT`, `DELETE` | `/api/rooms/{id}` | ADMIN | Редактирование / удаление номера |
| `POST` | `/api/bookings` | USER, ADMIN | Забронировать номер |
| `GET` | `/api/bookings` | ADMIN | История броней с пагинацией |
| `GET` | `/api/statistics/export` | ADMIN | Выгрузить CSV |

Для списков передаются `page` (с нуля, по умолчанию `0`) и `size` (`1..100`, по умолчанию `20`). Ответ содержит `content`, `totalElements`, `totalPages`, `page`, `size`.

### Фильтры

Отели (`GET /api/hotels`): `id`, `name`, `advertisementTitle`, `city`, `address`, `distanceFromCenter`, `rating`, `ratingCount`. Строковые поля ищутся по частичному совпадению без учёта регистра; остальные — точное совпадение.

Комнаты (`GET /api/rooms`): `id`, `title`, `minPrice`, `maxPrice`, `guestCount`, `checkIn`, `checkOut`, `hotelId`. `guestCount` — минимально необходимая вместимость; `minPrice` и `maxPrice` включительно. Проверка доступности применяется **только если переданы обе даты**. Если передана одна дата, датный фильтр не применяется (как предписывает задание). Не допускаются диапазоны, где выезд не позже заезда.

## Основные решения и неоднозначности задания

1. **Границы бронирования.** Интервал полуоткрытый — `[checkIn, checkOut)`: в день выезда номер уже доступен другому гостю. Пересечение двух броней определяется условием `existing.checkIn < new.checkOut && existing.checkOut > new.checkIn`. Дополнительно учитываются вручную закрытые дни из `room_unavailable_dates`.
2. **Одновременные запросы.** В `BookingService` перед проверкой пересечения номер читается с `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`), чтобы два клиента не подтвердили один номер одновременно. Аналогичная блокировка при изменении рейтинга.
3. **Спорная формула рейтинга в PDF.** На странице 5 явно задано `(rating × count − rating + newMark) / count`, после чего `count += 1`. Это **не стандартное среднее арифметическое**: например, при `count = 2, rating = 3, newMark = 5` будет `4.0`. Код реализует именно приведённую формулу, а для первой оценки (когда `count = 0`) принимает рейтинг равным первой оценке, чтобы избежать деления на ноль. Если куратор требует математически корректный рейтинг, формулу необходимо заменить на `(rating × count + newMark) / (count + 1)` с последующим увеличением счётчика.
4. **Регистрация роли.** DTO принимает поле `role` (как требует задание), но открытый endpoint разрешает только `USER` — иначе любой аноним мог бы создать себе администратора. Пользователь `ADMIN` создаётся через `.env` или защищённый endpoint администратора.
5. **История данных.** Нельзя удалить номер с бронированиями, отель с номерами либо пользователя с бронированиями. Это сохраняет историю и целостность ссылок.
6. **Учебный событийный слой.** События отправляются после коммита БД. Mongo использует `eventId` как уникальный `_id`, поэтому повторная доставка того же Kafka-сообщения не создаёт дубль. Для production-нагрузки стоило бы добавить transactional outbox, DLQ, monitoring и механизм повторных отправок при аварии брокера. Для курса не требуется.
7. **Photo в ER-диаграмме.** Дополнительно включены таблица `hotel_photos`, JPA-сущность и REST-операции для фото; в PDF эта связь изображена на странице 8.

## Обработка ошибок

- `400` — невалидный JSON/поля, конфликт дат, повторный логин/email, отрицательная цена, некорректная роль, ограничения SQL.
- `401` — отсутствующая или неверная HTTP Basic авторизация.
- `403` — пользователь недостаточно привилегирован.
- `404` — запрашиваемой сущности нет.
- `500` — непредвиденная внутренняя ошибка (без утечки stack trace).

Формат: `{"timestamp":"...","status":400,"error":"Bad Request","message":"...","fieldErrors":{...}}`.

## Каталог проекта

```text
src/main/java/ru/course/hotelbooking/
├── controller/       REST API
├── dto/              request / response / filters / pagination / errors
├── exception/        исключения и ControllerAdvice
├── mapper/           MapStruct мапперы
├── model/            сущности PostgreSQL (Hotel, HotelPhoto, Room, AppUser, Booking)
├── repository/       Spring Data JPA
├── search/           JPA Specification
├── security/         Basic Auth, UserDetailsService, создание ADMIN
├── service/          бизнес-логика и блокировки
└── statistics/       Kafka -> MongoDB -> CSV
src/main/resources/db/migration/V1__init.sql
src/test/java/ru/course/hotelbooking/
docs/ARCHITECTURE.md
 docs/TESTING.md
```

Схема в PDF соответствует отношениям: один отель — много комнат и фотографий; одна комната — много броней и закрытых дат; один пользователь — много бронирований; роль пользователя — `USER`/`ADMIN`.

## Соответствие 11 заданиям

| № | Реализация |
|---|---|
| 1 | Spring Boot, `pom.xml`, PostgreSQL, `Dockerfile`, `docker-compose.yml` |
| 2 | `Hotel`, JPA repository/service, DTO, MapStruct, CRUD API, общая пагинация |
| 3 | `ErrorResponse`, `GlobalExceptionHandler` (400/404/500 + 403) |
| 4 | `Room`, закрытые даты, JPA, DTO, MapStruct, CRUD API |
| 5 | `AppUser`, уникальные username/email, repository/service, DTO, MapStruct, регистрация/CRUD |
| 6 | `Booking`, проверка доступности, POST и GET API |
| 7 | `UserDetailsService`, BCrypt, Basic Auth, роль-based endpoint security |
| 8 | `POST /api/hotels/{id}/rating` с формулой из PDF |
| 9 | `HotelSpecifications`, DTO фильтра, пагинация/общее количество записей |
| 10 | `RoomSpecifications`, даты и исключение занятых дат, пагинация броней |
| 11 | Kafka/ZooKeeper, события регистрации/броней, MongoDB JSON, admin CSV |

**Примечание о проверке:** в окружении генерации исходный код и тесты были подготовлены, но собрать проект Maven и запустить Docker-контейнеры там не удалось ввиду отсутствия инструментов и доступа к пакетным репозиториям. Не считайте тесты подтверждённо пройденными, пока не выполнены команды выше в вашем окружении. См. `docs/TESTING.md`.
