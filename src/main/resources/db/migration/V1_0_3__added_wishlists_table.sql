-- Создание таблицы wishlists
CREATE TABLE IF NOT EXISTS wishlists (
                                         id BIGINT PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL,
                                         end_date DATE NOT NULL,
                                         user_id BIGINT NOT NULL,
                                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Последовательность для wishlists
CREATE SEQUENCE IF NOT EXISTS wishlist_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;