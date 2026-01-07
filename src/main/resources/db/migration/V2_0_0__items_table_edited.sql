-- Добавление столбца валюты в таблицу 'wishlists'
ALTER TABLE items
ADD COLUMN currency VARCHAR(3) DEFAULT 'USD';
