CREATE TABLE IF NOT EXISTS rooms(
    room_id SERIAL PRIMARY KEY,
    room_type VARCHAR,
    is_available BOOLEAN DEFAULT true,
    description TEXT NOT NULL,
    price_per_night NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    room_number INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)