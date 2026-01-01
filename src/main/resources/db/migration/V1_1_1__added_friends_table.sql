-- Создание таблицы friends
CREATE TABLE IF NOT EXISTS friends (
                                       user_id   BIGINT NOT NULL,
                                       friend_id BIGINT NOT NULL,
                                       created   TIMESTAMP NOT NULL DEFAULT NOW(),

                                       PRIMARY KEY (user_id, friend_id),

                                       FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE,
                                       FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создание индексов для friends
CREATE INDEX IF NOT EXISTS idx_friends_user
    ON friends(user_id);