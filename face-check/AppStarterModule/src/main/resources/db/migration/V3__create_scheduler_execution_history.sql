
CREATE TABLE scheduler_execution_history(
                                            id BIGSERIAL PRIMARY KEY,
                                            job_name VARCHAR(200) NOT NULL,
                                            job_group VARCHAR(200) NOT NULL,
                                            company_id INTEGER,
                                            company_name VARCHAR(255),
                                            start_time TIMESTAMP NOT NULL,
                                            end_time TIMESTAMP,
                                            status VARCHAR(50) NOT NULL,
                                            error_message TEXT,
                                            retry_count INTEGER DEFAULT 0,
                                            duration_seconds BIGINT,
                                            records_processed INTEGER,
                                            records_failed INTEGER,
                                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                            CONSTRAINT fk_scheduler_history_company
                                                FOREIGN KEY (company_id)
                                                    REFERENCES _company(id)
                                                    ON DELETE SET NULL
);

CREATE INDEX idx_scheduler_history_status ON scheduler_execution_history(status);
CREATE INDEX idx_scheduler_history_company ON scheduler_execution_history(company_id);
CREATE INDEX idx_scheduler_history_job_name ON scheduler_execution_history(job_name);
CREATE INDEX idx_scheduler_history_created_at ON scheduler_execution_history(created_at);
CREATE INDEX idx_scheduler_history_start_time ON scheduler_execution_history(start_time);