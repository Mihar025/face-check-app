-- === USER TABLE - Add encryption columns if not exist ===
DO $$
    BEGIN
        -- ssn_ciphertext
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='_user' AND column_name='ssn_ciphertext') THEN
            ALTER TABLE _user ADD COLUMN ssn_ciphertext BYTEA;
        END IF;

        -- ssn_iv
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='_user' AND column_name='ssn_iv') THEN
            ALTER TABLE _user ADD COLUMN ssn_iv BYTEA;
        END IF;

        -- ssn_key_version
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='_user' AND column_name='ssn_key_version') THEN
            ALTER TABLE _user ADD COLUMN ssn_key_version INTEGER;
        END IF;

        -- ssn_h
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='_user' AND column_name='ssn_h') THEN
            ALTER TABLE _user ADD COLUMN ssn_h BYTEA;
        END IF;

        -- ssn_last4
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='_user' AND column_name='ssn_last4') THEN
            ALTER TABLE _user ADD COLUMN ssn_last4 VARCHAR(4);
        END IF;
    END $$;

-- === DOCUMENTS_I9 TABLE - Add encryption columns if not exist ===
DO $$
    BEGIN
        -- document_number_ciphertext
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='documents_i9' AND column_name='document_number_ciphertext') THEN
            ALTER TABLE documents_i9 ADD COLUMN document_number_ciphertext BYTEA;
        END IF;

        -- document_number_iv
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='documents_i9' AND column_name='document_number_iv') THEN
            ALTER TABLE documents_i9 ADD COLUMN document_number_iv BYTEA;
        END IF;

        -- document_number_key_version
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='documents_i9' AND column_name='document_number_key_version') THEN
            ALTER TABLE documents_i9 ADD COLUMN document_number_key_version INTEGER;
        END IF;

        -- document_number_h
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='documents_i9' AND column_name='document_number_h') THEN
            ALTER TABLE documents_i9 ADD COLUMN document_number_h BYTEA;
        END IF;

        -- document_number_last4
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                       WHERE table_name='documents_i9' AND column_name='document_number_last4') THEN
            ALTER TABLE documents_i9 ADD COLUMN document_number_last4 VARCHAR(4);
        END IF;
    END $$;

