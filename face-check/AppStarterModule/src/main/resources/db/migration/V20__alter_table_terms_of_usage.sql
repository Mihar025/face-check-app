-- V20__complete_column_renames.sql
DO $$
    BEGIN
        -- user_id
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'terms_of_use_agreement'
                     AND column_name = 'userid') THEN
            ALTER TABLE terms_of_use_agreement RENAME COLUMN userid TO user_id;
        END IF;

        -- time_stamp
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'terms_of_use_agreement'
                     AND column_name = 'timestamp') THEN
            ALTER TABLE terms_of_use_agreement RENAME COLUMN "timestamp" TO time_stamp;
        END IF;

        -- terms_version
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'terms_of_use_agreement'
                     AND column_name = 'termsversion') THEN
            ALTER TABLE terms_of_use_agreement RENAME COLUMN termsversion TO terms_version;
        END IF;

        -- privacy_version
        IF EXISTS (SELECT 1 FROM information_schema.columns
                   WHERE table_name = 'terms_of_use_agreement'
                     AND column_name = 'privacyversion') THEN
            ALTER TABLE terms_of_use_agreement RENAME COLUMN privacyversion TO privacy_version;
        END IF;
    END $$;