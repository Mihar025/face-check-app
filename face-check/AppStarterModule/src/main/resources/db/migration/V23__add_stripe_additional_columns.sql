ALTER TABLE _company ADD COLUMN IF NOT EXISTS price_per_employee decimal(10,2);
ALTER TABLE _company ADD COLUMN IF NOT EXISTS monthly_subscription decimal(10,2);
