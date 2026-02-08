ALTER TABLE _company
    ALTER COLUMN monthly_subscription TYPE NUMERIC(19,2)
        USING CASE WHEN monthly_subscription = true THEN 1.00 ELSE 0.00 END;

ALTER TABLE _company
    ALTER COLUMN monthly_subscription SET DEFAULT 0.00;

ALTER TABLE _company
    ALTER COLUMN monthly_subscription SET NOT NULL;
