-- Добавление столбца паблик адйи(облачного хранилища) в таблицу 'users и items'
ALTER TABLE users
    ADD COLUMN avatar_public_id VARCHAR(100);

ALTER TABLE items
    ADD COLUMN image_public_id VARCHAR(100);