
CREATE TABLE IF NOT EXISTS prices(
    price_id SERIAL PRIMARY KEY,
    room_type VARCHAR,
    booking_type VARCHAR,
    netto_price NUMERIC(10,2) NOT NULL,
    tax_rate NUMERIC(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);