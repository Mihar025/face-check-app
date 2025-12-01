ALTER TABLE _company
    ADD stripe_customer_id VARCHAR(255);

ALTER TABLE _company
    ADD stripe_subscription_id VARCHAR(255);

ALTER TABLE _company
    ADD stripe_subscription_item_id VARCHAR(255);

ALTER TABLE _company
    ADD subscription_status VARCHAR(50);

ALTER TABLE _company
    ADD subscription_current_period_end TIMESTAMP;



