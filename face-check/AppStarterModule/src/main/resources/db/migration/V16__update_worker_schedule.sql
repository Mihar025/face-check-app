-- Добавляем новые колонки
ALTER TABLE worker_schedule
    ADD COLUMN IF NOT EXISTS day_of_week VARCHAR(20),
    ADD COLUMN IF NOT EXISTS is_day_off BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS is_template BOOLEAN DEFAULT FALSE;

-- Меняем тип колонок lunch на TIME (только если они не TIME)
DO $$
    BEGIN
        IF (SELECT data_type FROM information_schema.columns
            WHERE table_name = 'worker_schedule' AND column_name = 'start_lunch') != 'time without time zone'
        THEN
            ALTER TABLE worker_schedule
                ALTER COLUMN start_lunch TYPE TIME USING start_lunch::TIME,
                ALTER COLUMN end_lunch TYPE TIME USING end_lunch::TIME;
        END IF;
    END $$;

-- Добавляем индексы
CREATE INDEX IF NOT EXISTS idx_worker_schedule_template
    ON worker_schedule(worker_id, day_of_week, is_template);

CREATE INDEX IF NOT EXISTS idx_worker_schedule_date
    ON worker_schedule(worker_id, schedule_date);