-- V__add_transfer_fields_to_worker_attendance.sql

ALTER TABLE worker_attendance
    ADD COLUMN IF NOT EXISTS transfer_time TIMESTAMP,
    ADD COLUMN IF NOT EXISTS transfer_photo_url TEXT,
    ADD COLUMN IF NOT EXISTS transfer_latitude DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS transfer_longitude DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS transfer_location TEXT;