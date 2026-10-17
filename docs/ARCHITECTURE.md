# Архитектура Hotel Booking

## Компоненты

```mermaid
flowchart LR
    CLIENT[REST клиент / CMS] --> BASIC[HTTP Basic / Spring Security]
    BASIC --> REST[Controllers]
    REST --> SERVICES[Services / validation / transactions]
    SERVICES --> JPA[Repositories / JPA Specifications]
    JPA --> PG[(PostgreSQL)]
    SERVICES --> EVENTS[Domain events AFTER_COMMIT]
    EVENTS --> KAFKA[(Kafka)]
    KAFKA --> CONSUMER[Kafka Consumer]
    CONSUMER --> MONGO[(MongoDB JSON)]
    REST --> CSV[CSV Export Service]
    CSV --> MONGO
```

## Основная модель

```mermaid
erDiagram
    HOTEL ||--o{ ROOM : contains
    HOTEL ||--o{ HOTEL_PHOTO : has
    ROOM ||--o{ ROOM_UNAVAILABLE_DATE : blocks
    ROOM ||--o{ BOOKING : booked_for
    APP_USER ||--o{ BOOKING : makes
    HOTEL { bigint id PK
            string name
            decimal rating
            bigint rating_count }
    ROOM { bigint id PK
           bigint hotel_id FK
           decimal price
           int max_guests }
    HOTEL_PHOTO { bigint id PK
                  bigint hotel_id FK
                  string url }
    ROOM_UNAVAILABLE_DATE { bigint room_id FK
                            date unavailable_date }
    APP_USER { bigint id PK
               string username UK
               string email UK
               string password_hash
               enum role }
    BOOKING { bigint id PK
              bigint room_id FK
              bigint user_id FK
              date check_in
              date check_out }
```

## Сценарий бронирования

```mermaid
sequenceDiagram
    participant C as Client
    participant A as BookingController
    participant S as BookingService
    participant P as PostgreSQL
    participant K as Kafka
    participant M as MongoDB
    C->>A: POST /api/bookings + HTTP Basic
    A->>S: book(principal.id, dates, roomId)
    S->>P: SELECT room FOR UPDATE
    S->>P: Check overlapping bookings
    S->>P: Check unavailable dates
    alt room is free
        S->>P: INSERT booking
        P-->>S: id
        S-->>A: BookingResponse
        S->>K: StatisticsEvent AFTER_COMMIT
        K->>M: consumer saves JSON document
        A-->>C: 201 Created
    else room is occupied
        S-->>A: BadRequestException
        A-->>C: 400
    end
```

Полуоткрытая модель периодов позволяет заезжать в день предыдущего выезда. `room_unavailable_dates` хранит отдельные даты, а `bookings` — интервалы, обе формы занятости учитываются в JPA Specification.

## Границы ответственности

- Controller не содержит бизнес-логики: авторизация/валидация запроса, вызов сервиса, HTTP-код.
- Service управляет транзакциями, проверками и инвариантами.
- Repository предоставляет CRUD, запросы пересечений и пессимистические блокировки.
- MapStruct переводит DTO в сущность и обратно, не предоставляя клиенту `passwordHash` или возможности напрямую править рейтинг.
- Flyway создаёт первичную схему, Hibernate только проверяет (`ddl-auto=validate`).
- Kafka и Mongo не находятся в критическом пути SQL-транзакции.
