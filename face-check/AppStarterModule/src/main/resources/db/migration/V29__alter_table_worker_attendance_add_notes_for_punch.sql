ALTER TABLE worker_attendance ADD COLUMN IF NOT EXISTS notes_for_punch_in VARCHAR(3000);
ALTER TABLE worker_attendance ADD COLUMN IF NOT EXISTS notes_for_punch_out VARCHAR(3000);