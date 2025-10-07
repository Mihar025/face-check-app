ALTER TABLE wc_risk_class
    ALTER COLUMN effective_year
        TYPE integer
        USING (effective_year::integer);
