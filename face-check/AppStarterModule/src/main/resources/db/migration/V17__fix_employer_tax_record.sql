-- Приводим тип created_at к DATE (с сохранением только даты)
ALTER TABLE employer_tax_record
    ALTER COLUMN created_at TYPE date
        USING created_at::date;
