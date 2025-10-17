-- V19__add_os_version_to_terms_of_use_agreement.sql
ALTER TABLE terms_of_use_agreement
    ADD COLUMN os_version VARCHAR(255);