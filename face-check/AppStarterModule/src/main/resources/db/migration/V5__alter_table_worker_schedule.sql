ALTER TABLE worker_schedule
    ALTER COLUMN expected_start_time TYPE time USING expected_start_time::time,
    ALTER COLUMN expected_end_time   TYPE time USING expected_end_time::time;
