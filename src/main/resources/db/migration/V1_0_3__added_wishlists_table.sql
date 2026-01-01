-- Создание таблицы wishlists
CREATE TABLE IF NOT EXISTS wishlists (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         end_date DATE NOT NULL,
                                         user_id BIGINT NOT NULL,
                                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);