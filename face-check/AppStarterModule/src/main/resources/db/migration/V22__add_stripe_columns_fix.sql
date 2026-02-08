ALTER TABLE _company ADD COLUMN IF NOT EXISTS stripe_customer_id VARCHAR(255);
ALTER TABLE _company ADD COLUMN IF NOT EXISTS stripe_subscription_id VARCHAR(255);
ALTER TABLE _company ADD COLUMN IF NOT EXISTS stripe_subscription_item_id VARCHAR(255);
ALTER TABLE _company ADD COLUMN IF NOT EXISTS subscription_status VARCHAR(50);
ALTER TABLE _company ADD COLUMN IF NOT EXISTS subscription_current_period_end TIMESTAMP;