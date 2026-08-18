CREATE TABLE IF NOT EXISTS password_reset_tokens(
id SERIAL PRIMARY KEY,
user_id INTEGER NOT NULL,
token VARCHAR(519),
expiry_date TIMESTAMP NOT NULL,
  CONSTRAINT fk_password_reset_token_user
            FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
);