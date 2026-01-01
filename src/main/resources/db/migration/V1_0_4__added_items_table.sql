-- Создание таблицы items
CREATE TABLE IF NOT EXISTS items (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     shop_link VARCHAR(255) NOT NULL,
                                     price BIGINT NOT NULL,
                                     image_url VARCHAR(255) NOT NULL,
                                     status VARCHAR(50) DEFAULT 'AVAILABLE',
                                     wishlist_id BIGINT NOT NULL,
                                     reserved_by BIGINT,
                                     FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON DELETE CASCADE,
                                     FOREIGN KEY (reserved_by) REFERENCES users(id) ON DELETE SET NULL
);