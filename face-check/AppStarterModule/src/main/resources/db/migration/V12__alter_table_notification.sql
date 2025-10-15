-- V1__create_notification_table.sql
CREATE TABLE notification (
                              id SERIAL PRIMARY KEY,
                              title VARCHAR(255),
                              created_at TIMESTAMP,
                              company_id INTEGER NOT NULL,

                              CONSTRAINT fk_notification_company
                                  FOREIGN KEY (company_id)
                                      REFERENCES _company(id)
                                      ON DELETE CASCADE
);

-- Создаем индекс для быстрого поиска по компании
CREATE INDEX idx_notification_company_id ON notification(company_id);

-- Создаем индекс для поиска по дате
CREATE INDEX idx_notification_created_at ON notification(created_at);

-- Составной индекс для частого запроса (уведомления компании за день)
CREATE INDEX idx_notification_company_date ON notification(company_id, created_at);