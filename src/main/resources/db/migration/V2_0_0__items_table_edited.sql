-- Добавление столбца валюты в таблицу 'items'
ALTER TABLE items
ADD COLUMN currency VARCHAR(3) DEFAULT 'USD';
