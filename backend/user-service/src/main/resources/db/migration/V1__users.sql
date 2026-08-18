CREATE TABLE IF NOT EXISTS users(
    user_id SERIAL PRIMARY KEY,
    prename TEXT NOT NULL,
    lastname TEXT NOT NULL,
    username VARCHAR(255) NOT NULL,
    street TEXT NOT NULL,
    house_number VARCHAR(255) NOT NULL,
    postal_code INTEGER NOT NULL,
    town TEXT NOT NULL,
    country TEXT NOT NULL,
    email TEXT NOT NULL,
    birth_date DATE NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    role VARCHAR(20) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
