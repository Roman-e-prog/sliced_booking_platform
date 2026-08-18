CREATE TABLE IF NOT EXISTS bookings(
    booking_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    room_number INTEGER,
    number_of_persons INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    user_type VARCHAR,
    booking_type VARCHAR,
    room_type VARCHAR,
    payment_status VARCHAR,
    price_per_night NUMERIC(10,2),
    full_price NUMERIC(10,2),
    tax NUMERIC(10,2),
    brutto_price NUMERIC (10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);