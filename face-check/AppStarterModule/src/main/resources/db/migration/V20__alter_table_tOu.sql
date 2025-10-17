ALTER TABLE terms_of_use_agreement RENAME COLUMN userid        TO user_id;
ALTER TABLE terms_of_use_agreement RENAME COLUMN "timestamp"   TO time_stamp;
ALTER TABLE terms_of_use_agreement RENAME COLUMN termsversion  TO terms_version;
ALTER TABLE terms_of_use_agreement RENAME COLUMN privacyversion TO privacy_version;
ALTER TABLE terms_of_use_agreement RENAME COLUMN osversion     TO os_version;