BEGIN;

-- =====================================================
-- 1. ОПТИМИЗАЦИЯ ТИПОВ ДАННЫХ В СУЩЕСТВУЮЩЕЙ ТАБЛИЦЕ
-- =====================================================

-- Исправляем типы данных если нужно
ALTER TABLE location_record
    ALTER COLUMN accuracy TYPE DOUBLE PRECISION USING accuracy::DOUBLE PRECISION,
    ALTER COLUMN speed TYPE DOUBLE PRECISION USING speed::DOUBLE PRECISION,
    ALTER COLUMN bearing TYPE DOUBLE PRECISION USING bearing::DOUBLE PRECISION;

-- =====================================================
-- 2. СОЗДАЕМ ОПТИМИЗИРОВАННЫЕ ИНДЕКСЫ
-- =====================================================

-- Основной индекс для поиска по пользователю и времени
CREATE INDEX IF NOT EXISTS idx_location_user_timestamp
    ON location_record(user_id, timestamp DESC);

-- Индекс для поиска по времени
CREATE INDEX IF NOT EXISTS idx_location_timestamp
    ON location_record(timestamp DESC);

-- Индекс для геопоиска
CREATE INDEX IF NOT EXISTS idx_location_coordinates
    ON location_record(latitude, longitude);


-- Индекс для поиска по дате и пользователю
CREATE INDEX IF NOT EXISTS idx_location_date_user
    ON location_record(DATE(timestamp), user_id);

-- Индекс для отслеживания низкого заряда батареи
CREATE INDEX IF NOT EXISTS idx_location_battery_low
    ON location_record(user_id, timestamp DESC)
    WHERE battery_level < 20;

-- =====================================================
-- 3. ФУНКЦИЯ ДЛЯ ОЧИСТКИ СТАРЫХ ЗАПИСЕЙ (СТАРШЕ 3 МЕСЯЦЕВ)
-- =====================================================

CREATE OR REPLACE FUNCTION cleanup_old_locations()
    RETURNS void AS $$
DECLARE
    deleted_count INT;
BEGIN
    -- Удаляем записи старше 3 месяцев
    DELETE FROM location_record
    WHERE timestamp < CURRENT_DATE - INTERVAL '3 months';

    -- Получаем количество удаленных записей
    GET DIAGNOSTICS deleted_count = ROW_COUNT;

    -- Логируем результат
    RAISE NOTICE 'Deleted % old location records older than 3 months', deleted_count;

    -- Оптимизируем таблицу после удаления (освобождаем место)
    -- VACUUM ANALYZE location_record;
END;
$$ LANGUAGE plpgsql;

-- Добавляем комментарий к функции
COMMENT ON FUNCTION cleanup_old_locations() IS 'Удаляет записи локаций старше 3 месяцев для оптимизации производительности';

-- =====================================================
-- 4. ОБНОВЛЯЕМ СТАТИСТИКУ ДЛЯ ОПТИМИЗАТОРА
-- =====================================================

ANALYZE location_record;

COMMIT;

-- =====================================================
-- ИНСТРУКЦИЯ ПО ЗАПУСКУ ОЧИСТКИ
-- =====================================================

-- Вариант 1: Ручной запуск (когда нужно)
-- SELECT cleanup_old_locations();

-- Вариант 2: Через pg_cron (если установлен)
-- CREATE EXTENSION IF NOT EXISTS pg_cron;
-- SELECT cron.schedule('cleanup-locations', '0 2 * * 0', 'SELECT cleanup_old_locations()');

-- Вариант 3: Через системный cron (добавить в crontab)
-- 0 2 * * 0 psql -U your_user -d your_database -c "SELECT cleanup_old_locations();"