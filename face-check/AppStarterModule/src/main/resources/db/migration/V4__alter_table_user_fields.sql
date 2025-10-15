ALTER TABLE _user
    ADD COLUMN IF NOT EXISTS passport_number VARCHAR(255),
    ADD COLUMN IF NOT EXISTS passport_country_of_issuance VARCHAR(255),
    ADD COLUMN IF NOT EXISTS form_i94_admission_number VARCHAR(255),
    ADD COLUMN IF NOT EXISTS ssn_worker VARCHAR(255),
    ADD COLUMN IF NOT EXISTS uscis_number VARCHAR(255);