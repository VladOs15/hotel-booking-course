CREATE TABLE hotels (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  advertisement_title VARCHAR(300) NOT NULL,
  city VARCHAR(150) NOT NULL,
  address VARCHAR(400) NOT NULL,
  distance_from_center NUMERIC(10,2) NOT NULL CHECK (distance_from_center >= 0),
  rating NUMERIC(2,1) NOT NULL DEFAULT 0 CHECK (rating >= 0 AND rating <= 5),
  rating_count BIGINT NOT NULL DEFAULT 0 CHECK (rating_count >= 0)
);
CREATE TABLE hotel_photos (
  id BIGSERIAL PRIMARY KEY,
  hotel_id BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
  url VARCHAR(2000) NOT NULL
);
CREATE TABLE rooms (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  room_number VARCHAR(30) NOT NULL,
  price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
  max_guests INTEGER NOT NULL CHECK (max_guests > 0),
  hotel_id BIGINT NOT NULL REFERENCES hotels(id),
  CONSTRAINT uq_rooms_hotel_number UNIQUE (hotel_id, room_number)
);
CREATE TABLE room_unavailable_dates (
  room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
  unavailable_date DATE NOT NULL,
  PRIMARY KEY (room_id, unavailable_date)
);
CREATE TABLE app_users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ADMIN'))
);
CREATE TABLE bookings (
  id BIGSERIAL PRIMARY KEY,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  room_id BIGINT NOT NULL REFERENCES rooms(id),
  user_id BIGINT NOT NULL REFERENCES app_users(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT valid_booking_dates CHECK (check_out > check_in)
);
CREATE INDEX idx_rooms_hotel_id ON rooms(hotel_id);
CREATE INDEX idx_bookings_room_dates ON bookings(room_id, check_in, check_out);
CREATE INDEX idx_bookings_user_id ON bookings(user_id);
CREATE INDEX idx_hotels_city ON hotels(city);
