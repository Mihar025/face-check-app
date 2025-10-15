
ALTER TABLE location_record
    ALTER COLUMN accuracy TYPE DOUBLE PRECISION,
    ALTER COLUMN speed TYPE DOUBLE PRECISION,
    ALTER COLUMN bearing TYPE DOUBLE PRECISION;

CREATE INDEX IF NOT EXISTS idx_location_user_timestamp
    ON location_record(user_id, timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_location_timestamp
    ON location_record(timestamp DESC);

CREATE INDEX IF NOT EXISTS idx_location_coordinates
    ON location_record(latitude, longitude);