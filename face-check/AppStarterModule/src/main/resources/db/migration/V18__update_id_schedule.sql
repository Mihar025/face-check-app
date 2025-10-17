-- V18__change_scheduler_id_to_integer.sql
ALTER TABLE scheduler_execution_history
    ALTER COLUMN id TYPE INTEGER;