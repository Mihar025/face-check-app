BEGIN;

CREATE TABLE _role(

                      id SERIAL PRIMARY KEY,
                      name VARCHAR(255),
                      created_date TIMESTAMP,
                      last_modified_date TIMESTAMP
);

CREATE TABLE wc_risk_class(
                              code VARCHAR(255) PRIMARY KEY ,
                              description VARCHAR(255),
                              rate NUMERIC,
                              industry_tag VARCHAR(255),
                              effective_year VARCHAR(255)
);

CREATE TABLE _company(
                         id SERIAL PRIMARY KEY,

                         company_name VARCHAR(255) NOT NULL,
                         company_address VARCHAR(255) NOT NULL,
                         company_phone VARCHAR(255) NOT NULL,
                         company_email VARCHAR(255) NOT NULL,

                         company_state_id_number VARCHAR(255) NOT NULL,
                         company_city VARCHAR(255) NOT NULL,
                         company_state VARCHAR(255) NOT NULL,
                         company_zip_code VARCHAR(255) NOT NULL,
                         employer_ein VARCHAR(255) NOT NULL,
                         special_two_char_condition_code_for_mta305 VARCHAR(255) NOT NULL,
                         company_income_per_month NUMERIC(15,2),
                         costs_for_employee_salaries_per_month NUMERIC(15,2),
                         regular_pay_per_month_total NUMERIC(15,2),
                         overtime_pay_per_month_total NUMERIC(15,2),
                         gross_pay_per_month_total NUMERIC(15,2),
                         total_spend_money_per_year NUMERIC(15,2),

                         company_insurance NUMERIC(15,2),
                         social_security_tax_for_company NUMERIC(15,2),
                         federal_withholding_for_company NUMERIC(15,2),
                         ny_state_withholding_for_company NUMERIC(15,2),
                         ny_local_withholding_for_company NUMERIC(15,2),

                         company_payment_position VARCHAR(255),

                         irs_deposited_amount NUMERIC(15,2),
                         when_deposit_amount_was_made TIMESTAMP,
                         workers_quantity INT,
                         first_biweekly_date DATE,

                         emr NUMERIC(15,2),
                         wc_policy_number VARCHAR(255),
                         wc_insurance_carrier VARCHAR(255),
                         funding_bank_name VARCHAR(255),
                         funding_routing_number VARCHAR(255),
                         funding_account_number VARCHAR(255),
                         return_mailing_address VARCHAR(255),
                         default_memo VARCHAR(255),
                         signature_name VARCHAR(255),
                         signature_title VARCHAR(255)

);


CREATE  TABLE  work_site (
                             id SERIAL PRIMARY KEY,

                             site_name VARCHAR(255),
                             address VARCHAR(255),
                             latitude DOUBLE PRECISION ,
                             longitude DOUBLE PRECISION,
                             allowed_radius DOUBLE PRECISION,

                             work_day_start TIME,
                             work_day_end TIME,
                             is_active BOOLEAN,
                             is_worker_did_punch_in BOOLEAN,

                             company_id INT,
                             CONSTRAINT fk_company FOREIGN KEY (company_id) REFERENCES _company(id) ON DELETE SET NULL

);

CREATE TABLE _user(
  id SERIAL PRIMARY KEY,
  first_name VARCHAR(255),
  middle_initial VARCHAR(10),
  last_name VARCHAR(255),
  email VARCHAR(255),
  phone_number VARCHAR(255),
  date_of_birth DATE,
  home_address VARCHAR(255),
  apt VARCHAR(20),
  city VARCHAR(255),
  state VARCHAR(255),
  zipcode VARCHAR(255),

    is_citizen BOOLEAN,
    is_non_citizen_national_of_the_us BOOLEAN,
    is_permanent_resident BOOLEAN,
    is_non_active BOOLEAN,

    is_rehired BOOLEAN,

    date_when_rehired DATE,
    work_authrization_expiry_date DATE,
    uscis_number VARCHAR(255),
    form_i94_admission_number VARCHAR(255),
    passport_country_of_issuance VARCHAR(255),
    photo_file_name VARCHAR(255),
    photo_url VARCHAR(255),
    base_hourly_rate NUMERIC(15,2),
    overtime_rate NUMERIC(15,2),

    is_admin BOOLEAN,
    is_foreman BOOLEAN,
    is_user BOOLEAN,
    is_business_owner BOOLEAN,

    password VARCHAR(255),
    enabled BOOLEAN,
    account_locked BOOLEAN,
    ssn_worker VARCHAR(255),
    gender VARCHAR(20),
    filing_status VARCHAR(50),

    dependents INT,
    extra_with_holdings NUMERIC(15,2),
    multiple_jobs_or_spouse_works BOOLEAN,
    two_jobs_check_box BOOLEAN,
    multiple_jobs_additional_withholding NUMERIC(15,2),
    dependents_under_17 INT,
    other_dependents INT,
    total_dependents_credit NUMERIC (15,2),
    other_income NUMERIC(15,2),
    deductions NUMERIC(15,2),
    exempt_from_withholding BOOLEAN,
    multiple_jobs_worksheet_line_2a NUMERIC(15,2),
    multiple_jobs_worksheet_line_2b NUMERIC(15,2),
    estimated_itemized_deductions NUMERIC(15,2),
    adjustments_schedule1 NUMERIC(15,2),

    live_in_nyc BOOLEAN,
    pay_frequency VARCHAR (255),
    employment_type VARCHAR(255),
    coverage_start_date DATE,
    enrolled_in_health_plan BOOLEAN,
    monthly_health_premium NUMERIC(15,2),
    period_charge_insurance NUMERIC(15,2),
    sick_leave_accrued NUMERIC(15,2),
    sick_leave_used NUMERIC(15,2),
    hours_worked_year_to_date NUMERIC(15,2),
    sick_leave_accrued_this_year NUMERIC(15,2),
    sick_leave_paid BOOLEAN,
    hire_date DATE,
    last_sick_leave_carryover_date DATE,
    sick_leave_carried_over NUMERIC(15,2),

    company_id INT,
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES _company(id) ON DELETE SET NULL,

    wc_risk_class_code VARCHAR(100),
    CONSTRAINT fk_risk_class FOREIGN KEY (wc_risk_class_code) REFERENCES  wc_risk_class(code),


  work_site_id INT,
  CONSTRAINT fk_user_work_site FOREIGN KEY (work_site_id) REFERENCES  work_site(id),

  current_work_site_id INT,
  CONSTRAINT fk_user_current_work_site FOREIGN KEY (current_work_site_id) REFERENCES work_site(id),


    created_date TIMESTAMP,
    last_modified_date TIMESTAMP


);

ALTER TABLE _company ADD COLUMN owner_id INT;
ALTER TABLE _company ADD CONSTRAINT fk_company_owner
    FOREIGN KEY (owner_id) REFERENCES _user(id);

    CREATE TABLE user_roles (
        user_id INT NOT NULL,
        role_id INT NOT NULL,
        CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
        CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES _role(id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, role_id)
    );











CREATE TABLE custom_worker_radius (
                                      work_site_id INT NOT NULL,
                                      worker_id INT NOT NULL,
                                      radius DOUBLE PRECISION,
                                      PRIMARY KEY (work_site_id, worker_id),
                                      CONSTRAINT fk_custom_radius_work_site FOREIGN KEY (work_site_id) REFERENCES work_site(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_custom_radius_worker FOREIGN KEY (worker_id) REFERENCES _user(id) ON DELETE CASCADE
);

CREATE TABLE user_work_sites (
                                 work_site_id INT NOT NULL,
                                 user_id INT NOT NULL,
                                 PRIMARY KEY (work_site_id, user_id),

                                 CONSTRAINT fk_user_work_site_ws FOREIGN KEY (work_site_id) REFERENCES work_site(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_user_work_site_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

CREATE TABLE inactive_days (
                               work_site_id INT NOT NULL,
                               inactive_date DATE NOT NULL,
                               PRIMARY KEY (work_site_id, inactive_date),
                               CONSTRAINT fk_inactive_days_work_site FOREIGN KEY (work_site_id) REFERENCES work_site(id) ON DELETE CASCADE
);


    CREATE TABLE token
    (
        id SERIAL PRIMARY KEY,
        token VARCHAR(255),
        created_at TIMESTAMP,
        expires_at TIMESTAMP,
        validated_at TIMESTAMP,
        user_id INT NOT NULL ,
        CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES _user (id) ON DELETE CASCADE
    );

CREATE TABLE payment_history_irs(
    payment_history_irs_id SERIAL PRIMARY KEY,

    amount  NUMERIC,
    payment_date DATE,
    created_at TIMESTAMP,
    quarter INT,
    year INT,
    payment_type_enum VARCHAR,
    notes VARCHAR,
    company_id INT,
    CONSTRAINT  fk_payment_history_company FOREIGN KEY (company_id) REFERENCES _company(id) ON DELETE SET NULL
);

CREATE TABLE location_record(
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL ,
    latitude DOUBLE PRECISION  NOT NULL,
    longitude DOUBLE PRECISION  NOT NULL,
    timestamp TIMESTAMP  NOT NULL,
    accuracy float,
    speed float,
    bearing float,
    altitude DOUBLE PRECISION,
    provider VARCHAR,
    battery_level INT,
    distance_from_previous DOUBLE PRECISION,
    CONSTRAINT fk_location_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

CREATE TABLE _dependents(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE,
    user_id INT,
    CONSTRAINT fk_dependents_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE
);

CREATE TABLE documents_i9(
    id SERIAL PRIMARY KEY,
    document_title VARCHAR(255),
    issuing_authority VARCHAR(255),
    document_number VARCHAR(255),
    expiration_date DATE,
    user_id INT,
    CONSTRAINT fk_documents_i9_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE

);
CREATE TABLE worker_payroll(
    id SERIAL PRIMARY KEY,
    period_start DATE,
    period_end DATE,
    base_hourly_rate NUMERIC,
    over_time_rate NUMERIC,

    regular_hours DOUBLE PRECISION,
    overtime_hours DOUBLE PRECISION,
    total_hours DOUBLE PRECISION,

    regular_pay NUMERIC,
    overtime_pay NUMERIC,
    gross_pay NUMERIC,

    medicare NUMERIC,
    social_security_employee NUMERIC,
    federal_withholding NUMERIC,
    ny_state_withholding NUMERIC,
    ny_local_withholding NUMERIC,
    ny_disability_withholding NUMERIC,
    ny_paid_family_leave NUMERIC,
    total_deductions NUMERIC,
    retirement401k_contribution NUMERIC,
    health_insurance_cost NUMERIC,
    has_retirement_plan BOOLEAN,
    ny_unemployment_withholding NUMERIC,
    net_pay NUMERIC,
    employer_taxes_calculated BOOLEAN,
    pay_stub_generated BOOLEAN,

    worker_id INT,
    company_id INT,
    wc_risk_code VARCHAR,
    CONSTRAINT fk_worker_payroll_user FOREIGN KEY (worker_id) REFERENCES _user(id) ON DELETE CASCADE,
    CONSTRAINT fk_worker_payroll_company FOREIGN KEY (company_id) REFERENCES _company(id) ON DELETE CASCADE,
    CONSTRAINT fk_worker_payroll_risk_class FOREIGN KEY (wc_risk_code) REFERENCES wc_risk_class(code) ON DELETE CASCADE
);

CREATE TABLE employer_tax_record(
    id SERIAL PRIMARY KEY,
    gross_pay NUMERIC,
    social_security_tax NUMERIC,
    medicare_tax NUMERIC,
    futa_tax NUMERIC,
    suta_tax NUMERIC,
    federal_withholding NUMERIC,
    total_employer_tax NUMERIC,
    period_start DATE,
    period_end DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    social_security_taxable_wages NUMERIC,
    social_security_tips NUMERIC,
    medicare_taxable_wages NUMERIC,
    additional_medicare_wages NUMERIC,
    payment_date DATE,
    futa_taxable_wages NUMERIC,
    suta_taxable_wages NUMERIC,
    company_id INT,
    employee_id INT,
    pay_stub_id INT,
    CONSTRAINT fk_employer_tax_record_company FOREIGN KEY (company_id) REFERENCES _company(id) ON DELETE CASCADE,
    CONSTRAINT fk_employer_tax_record_user FOREIGN KEY (employee_id) REFERENCES _user(id) ON DELETE CASCADE,
    CONSTRAINT fk_employer_tax_record_worker_payroll FOREIGN KEY (pay_stub_id) REFERENCES worker_payroll(id) ON DELETE CASCADE
);
CREATE TABLE worker_schedule(
    id SERIAL PRIMARY KEY,
    schedule_date DATE,
    expected_start_time TIMESTAMP,
    expected_end_time TIMESTAMP,
    shift VARCHAR,
    is_on_duty BOOLEAN,
    start_lunch TIMESTAMP,
    end_lunch TIMESTAMP,
    is_company_paying_lunch BOOLEAN,
    worker_id INT,
    work_site_id INT,
    CONSTRAINT fk_worker_schedule_user FOREIGN KEY (worker_id) REFERENCES _user(id),
    CONSTRAINT fk_worker_schedule_work_site FOREIGN KEY (work_site_id) REFERENCES work_site(id)
);

CREATE TABLE worker_attendance(
    id SERIAL PRIMARY KEY,
    check_in_time TIMESTAMP,
  check_in_photo_url VARCHAR,
  check_in_latitude DOUBLE PRECISION,
  check_in_longitude DOUBLE PRECISION,
  check_in_location VARCHAR,

  check_out_time TIMESTAMP,
  check_out_photo_url VARCHAR,
  check_out_latitude DOUBLE PRECISION,
  check_out_longitude DOUBLE PRECISION,
  check_out_location VARCHAR,


  hours_worked DOUBLE PRECISION,
  overtime_hours DOUBLE PRECISION,
  gross_pay_per_day NUMERIC,
  net_pay NUMERIC,
  period_start DATE,
  period_end DATE,
  notes VARCHAR,
  is_verified BOOLEAN,
  verified_by VARCHAR,
  verification_time TIMESTAMP,
  worker_id INT,
  CONSTRAINT fk_worker_attendance_user FOREIGN KEY (worker_id) REFERENCES _user(id) ON DELETE CASCADE

);

COMMIT;


