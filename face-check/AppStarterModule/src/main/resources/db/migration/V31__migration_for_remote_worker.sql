ALTER TABLE _user
    ADD COLUMN IF NOT EXISTS is_remote_worker BOOLEAN DEFAULT FALSE;

-- Notification: поле для targeted notifications
ALTER TABLE notification
    ADD COLUMN IF NOT EXISTS target_user_id INTEGER REFERENCES _user(id);

-- Новая таблица для Random Attendance Verification
CREATE TABLE IF NOT EXISTS _random_attendance_verification (
                                                               id SERIAL PRIMARY KEY,
                                                               worker_id INTEGER REFERENCES _user(id),
                                                               random_attendance_verification_url VARCHAR(255),
                                                               random_attendance_verification_latitude DOUBLE PRECISION,
                                                               random_attendance_verification_longitude DOUBLE PRECISION,
                                                               random_attendance_verification_location VARCHAR(255),
                                                               random_attendance_verification_time TIMESTAMP,
                                                               is_missed_message VARCHAR(255),
                                                               message VARCHAR(255),
                                                               is_successful BOOLEAN,
                                                               is_missed BOOLEAN,
                                                               status VARCHAR(20),
                                                               created_at DATE
);