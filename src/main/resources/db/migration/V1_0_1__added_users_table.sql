-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     avatar_path VARCHAR(255),
                                     first_name VARCHAR(255),
                                     last_name VARCHAR(255),
                                     age INTEGER NOT NULL DEFAULT 0,
                                     created TIMESTAMP,
                                     updated TIMESTAMP,
                                     birthday DATE
);