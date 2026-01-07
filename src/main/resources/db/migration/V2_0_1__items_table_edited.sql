-- Добавление столбца описания в таблицу 'items'
ALTER TABLE items
    ADD COLUMN description VARCHAR(100);