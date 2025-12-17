-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY,
                                     avatar_path VARCHAR(255),
                                     first_name VARCHAR(255),
                                     last_name VARCHAR(255),
                                     age INTEGER NOT NULL DEFAULT 0,
                                     created TIMESTAMP,
                                     updated TIMESTAMP,
                                     birthday DATE
);

-- Последовательность для users
CREATE SEQUENCE IF NOT EXISTS user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;