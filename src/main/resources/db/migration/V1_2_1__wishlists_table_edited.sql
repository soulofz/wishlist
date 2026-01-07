-- Добавление столбцов created & updated в таблицу 'wishlists'
ALTER TABLE wishlists
    ADD COLUMN created TIMESTAMP,
    ADD COLUMN updated TIMESTAMP;