-- Создание таблицы security
CREATE TABLE IF NOT EXISTS security (
                                        id BIGSERIAL PRIMARY KEY,
                                        username VARCHAR(255) UNIQUE NOT NULL,
                                        email VARCHAR(255) UNIQUE NOT NULL,
                                        password VARCHAR(255) NOT NULL,
                                        role VARCHAR(50) DEFAULT 'USER',
                                        user_id BIGINT NOT NULL UNIQUE,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);