-- Добавление столбцов приватности в таблицу 'wishlists'
ALTER TABLE wishlists
    ADD COLUMN visibility VARCHAR(55),
    ADD COLUMN reservation VARCHAR(55),
    ADD COLUMN reservation_visibility VARCHAR(55),
    ADD COLUMN completed_gifts VARCHAR(55);