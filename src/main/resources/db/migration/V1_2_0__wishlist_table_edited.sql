-- Добавление столбцов приватности в таблицу 'wishlists'
ALTER TABLE wishlists
    ADD COLUMN visibility VARCHAR(55) DEFAULT 'PUBLIC',
    ADD COLUMN reservation VARCHAR(55) DEFAULT 'PUBLIC',
    ADD COLUMN reservation_visibility VARCHAR(55) DEFAULT 'FULL_VISIBLE',
    ADD COLUMN completed_gifts VARCHAR(55) DEFAULT 'KEEP';