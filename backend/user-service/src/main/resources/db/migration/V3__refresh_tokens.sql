CREATE TABLE IF NOT EXISTS refresh_tokens(
id SERIAL PRIMARY KEY,
user_id INTEGER NOT NULL,
token VARCHAR(519),
expiry_date TIMESTAMP NOT NULL,
revoked BOOLEAN DEFAULT false,
replaced_by_token VARCHAR(519),
  CONSTRAINT fk_refresh_token_user
            FOREIGN KEY (user_id)
            REFERENCES users(user_id)
            ON DELETE CASCADE
);