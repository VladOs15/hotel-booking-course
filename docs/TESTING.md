# План тестирования

## Автоматические unit-тесты

```bash
mvn clean test
```

Тесты в `src/test/java` проверяют:

- первый рейтинг без деления на ноль, точную формулу из PDF, оценку вне диапазона;
- недопустимое пересечение бронирований, недоступные даты, успешное создание и событие;
- запрет анонимной регистрации ADMIN, уникальность логина, BCrypt-преобразование пароля;
- обработку неполного/ошибочного диапазона поиска и границ пагинации;
- экспорт двух видов событий в CSV и сохранение Kafka-события в Mongo-модель.

Это unit-тесты на Mockito. Они **не заменяют интеграционные испытания** PostgreSQL / MongoDB / Kafka, которые необходимо выполнить в Docker.

## Интеграционный сценарий в Docker

1. Поднять всю инфраструктуру `docker compose up --build -d`.
2. Проверить `docker compose ps`, `docker compose logs -f app` и успешный ответ с Basic Auth.
3. Без авторизации вызвать `GET /api/hotels` — должен быть `401`.
4. Зарегистрировать USER (`POST /api/users/register`) — `201`.
5. Попробовать зарегистрировать ADMIN публично — `400`.
6. От имени USER вызвать `POST /api/hotels` — `403`.
7. От имени ADMIN создать отель, комнату и фотографию — `201`.
8. От имени USER найти номер по фильтрам и датам, оформить бронь — `201`.
9. Оформить вторую бронь с пересечением — `400`.
10. Оформить вторую бронь с заездом в день первой даты выезда — `201`.
11. Найти номера через `GET /api/rooms` на пересекающийся период — занятые исключены.
12. Поиск номера с одной датой — фильтрация по доступности **не применяется**.
13. Попробовать забронировать недоступную дату комнаты — `400`.
14. Выставить оценку `1..5`; проверить рейтинг и ratingCount. Значения `0` и `6` — `400`.
15. Проверить постраничный `GET /api/bookings` от ADMIN — `200`; от USER — `403`.
16. Подождать несколько секунд, выгрузить `/api/statistics/export` от ADMIN; проверить строки `USER_REGISTERED`, `ROOM_BOOKED`.
17. Остановить / поднять приложение; убедиться, что данные PostgreSQL/MongoDB сохраняются в томах.
18. Повторить запрос `GET /api/hotels/{missingId}` — `404`.

## Контроль SQL целостности

```bash
docker compose exec postgres psql -U hotels -d hotels -c '\dt'
docker compose exec postgres psql -U hotels -d hotels -c 'select id, username, role from app_users;'
docker compose exec postgres psql -U hotels -d hotels -c 'select * from bookings;'
docker compose exec mongodb mongosh --quiet hotel_statistics --eval 'db.events.find().pretty()'
```

Для Kafka:

```bash
docker compose exec kafka kafka-topics --bootstrap-server kafka:9092 --list
docker compose exec kafka kafka-console-consumer --bootstrap-server kafka:9092 --topic hotel-events --from-beginning --timeout-ms 5000
```

Не публикуйте настоящий пароль в `README`, журнале команд или Git. Пример `.env` содержит только демонстрационные значения и должен быть изменён перед запуском.
