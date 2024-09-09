-- formated sql

-- changeset dubrovsky:1
CREATE TABLE notification_task(id BIGSERIAL PRIMARY KEY, chat_id BIGINT, text VARCHAR, date_time TIMESTAMP)