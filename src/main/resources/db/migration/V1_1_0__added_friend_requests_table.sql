-- Создание таблицы friend_requests
CREATE TABLE IF NOT EXISTS friend_requests (
                                               id BIGSERIAL PRIMARY KEY,
                                               sender_id   BIGINT NOT NULL,
                                               receiver_id BIGINT NOT NULL,
                                               status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                                               created TIMESTAMP NOT NULL DEFAULT NOW(),
                                               FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
                                               FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
                                               CONSTRAINT uq_friend_request_pair
                                                   UNIQUE (sender_id, receiver_id)
);

-- Создание индексов для friend_requests
CREATE INDEX IF NOT EXISTS idx_friend_requests_sender
    ON friend_requests(sender_id);

CREATE INDEX IF NOT EXISTS idx_friend_requests_receiver
    ON friend_requests(receiver_id);