--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3 (Debian 16.3-1.pgdg120+1)
-- Dumped by pg_dump version 17.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: cleanup_old_locations(); Type: FUNCTION; Schema: public; Owner: misha
--

CREATE FUNCTION public.cleanup_old_locations() RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
    deleted_count INT;
BEGIN
    -- Удаляем записи старше 3 месяцев
    DELETE FROM location_record
    WHERE timestamp < CURRENT_DATE - INTERVAL '3 months';

    -- Получаем количество удаленных записей
    GET DIAGNOSTICS deleted_count = ROW_COUNT;

    -- Логируем результат
    RAISE NOTICE 'Deleted % old location records older than 3 months', deleted_count;

    -- Оптимизируем таблицу после удаления (освобождаем место)
    -- VACUUM ANALYZE location_record;
END;
$$;


ALTER FUNCTION public.cleanup_old_locations() OWNER TO misha;

--
-- Name: FUNCTION cleanup_old_locations(); Type: COMMENT; Schema: public; Owner: misha
--

COMMENT ON FUNCTION public.cleanup_old_locations() IS 'Удаляет записи локаций старше 3 месяцев для оптимизации производительности';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: _company; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public._company (
    id integer NOT NULL,
    company_name character varying(255) NOT NULL,
    company_address character varying(255) NOT NULL,
    company_phone character varying(255) NOT NULL,
    company_email character varying(255) NOT NULL,
    company_state_id_number character varying(255) NOT NULL,
    company_city character varying(255) NOT NULL,
    company_state character varying(255) NOT NULL,
    company_zip_code character varying(255) NOT NULL,
    employer_ein character varying(255) NOT NULL,
    special_two_char_condition_code_for_mta305 character varying(255) NOT NULL,
    company_income_per_month numeric(38,2),
    costs_for_employee_salaries_per_month numeric(38,2),
    regular_pay_per_month_total numeric(38,2),
    overtime_pay_per_month_total numeric(38,2),
    gross_pay_per_month_total numeric(38,2),
    total_spend_money_per_year numeric(38,2),
    company_insurance numeric(38,2),
    social_security_tax_for_company numeric(38,2),
    federal_withholding_for_company numeric(38,2),
    ny_state_withholding_for_company numeric(38,2),
    ny_local_withholding_for_company numeric(38,2),
    company_payment_position character varying(255),
    irs_deposited_amount numeric(38,2),
    when_deposit_amount_was_made timestamp without time zone,
    workers_quantity integer,
    first_biweekly_date date,
    emr numeric(4,2),
    wc_policy_number character varying(50),
    wc_insurance_carrier character varying(100),
    funding_bank_name character varying(255),
    funding_routing_number character varying(255),
    funding_account_number character varying(255),
    return_mailing_address character varying(255),
    default_memo character varying(255),
    signature_name character varying(255),
    signature_title character varying(255),
    owner_id integer
);


ALTER TABLE public._company OWNER TO misha;

--
-- Name: _company_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public._company_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public._company_id_seq OWNER TO misha;

--
-- Name: _company_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public._company_id_seq OWNED BY public._company.id;


--
-- Name: _dependents; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public._dependents (
    id integer NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    birth_date date,
    user_id integer
);


ALTER TABLE public._dependents OWNER TO misha;

--
-- Name: _dependents_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public._dependents_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public._dependents_id_seq OWNER TO misha;

--
-- Name: _dependents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public._dependents_id_seq OWNED BY public._dependents.id;


--
-- Name: _role; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public._role (
    id integer NOT NULL,
    name character varying(255),
    created_date timestamp without time zone,
    last_modified_date timestamp without time zone
);


ALTER TABLE public._role OWNER TO misha;

--
-- Name: _role_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public._role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public._role_id_seq OWNER TO misha;

--
-- Name: _role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public._role_id_seq OWNED BY public._role.id;


--
-- Name: _user; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public._user (
    id integer NOT NULL,
    first_name character varying(255),
    middle_initial character varying(255),
    last_name character varying(255),
    email character varying(255),
    phone_number character varying(255),
    date_of_birth date,
    home_address character varying(255),
    apt character varying(255),
    city character varying(255),
    state character varying(255),
    zipcode character varying(255),
    is_citizen boolean,
    is_non_citizen_national_of_the_us boolean,
    is_permanent_resident boolean,
    is_non_active boolean,
    is_rehired boolean,
    date_when_rehired date,
    work_authrization_expiry_date date,
    uscis_number character varying(255),
    form_i94_admission_number character varying(255),
    passport_country_of_issuance character varying(255),
    photo_file_name character varying(255),
    photo_url character varying(255),
    base_hourly_rate numeric(38,2),
    overtime_rate numeric(38,2),
    is_admin boolean,
    is_foreman boolean,
    is_user boolean,
    is_business_owner boolean,
    password character varying(255),
    enabled boolean,
    account_locked boolean,
    ssn_worker character varying(255),
    gender character varying(255),
    filing_status character varying(255),
    dependents integer,
    extra_with_holdings numeric(38,2),
    multiple_jobs_or_spouse_works boolean,
    two_jobs_check_box boolean,
    multiple_jobs_additional_withholding numeric(10,2),
    dependents_under_17 integer,
    other_dependents integer,
    total_dependents_credit numeric(10,2),
    other_income numeric(12,2),
    deductions numeric(12,2),
    exempt_from_withholding boolean,
    multiple_jobs_worksheet_line_2a numeric(38,2),
    multiple_jobs_worksheet_line_2b numeric(38,2),
    estimated_itemized_deductions numeric(38,2),
    adjustments_schedule1 numeric(38,2),
    live_in_nyc boolean,
    pay_frequency character varying(255),
    employment_type character varying(255),
    coverage_start_date date,
    enrolled_in_health_plan boolean,
    monthly_health_premium numeric(38,2),
    period_charge_insurance numeric(38,2),
    sick_leave_accrued numeric(38,2),
    sick_leave_used numeric(38,2),
    hours_worked_year_to_date numeric(38,2),
    sick_leave_accrued_this_year numeric(38,2),
    sick_leave_paid boolean,
    hire_date date,
    last_sick_leave_carryover_date date,
    sick_leave_carried_over numeric(38,2),
    company_id integer,
    wc_risk_class_code character varying(10),
    work_site_id integer,
    current_work_site_id integer,
    created_date timestamp without time zone,
    last_modified_date timestamp without time zone,
    passport_number character varying(255),
    actual_budget numeric(38,2),
    expenses numeric(38,2),
    cost_of_salaries numeric(38,2),
    profit numeric(38,2),
    ssn_ciphertext bytea,
    ssn_h bytea,
    ssn_iv bytea,
    ssn_key_version integer,
    ssn_last4 character varying(255)
);


ALTER TABLE public._user OWNER TO misha;

--
-- Name: _user_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public._user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public._user_id_seq OWNER TO misha;

--
-- Name: _user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public._user_id_seq OWNED BY public._user.id;


--
-- Name: contact_sales_form; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.contact_sales_form (
    id integer NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    phone_number character varying(255),
    created_date date DEFAULT CURRENT_DATE
);


ALTER TABLE public.contact_sales_form OWNER TO misha;

--
-- Name: contact_sales_form_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.contact_sales_form_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.contact_sales_form_id_seq OWNER TO misha;

--
-- Name: contact_sales_form_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.contact_sales_form_id_seq OWNED BY public.contact_sales_form.id;


--
-- Name: custom_worker_radius; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.custom_worker_radius (
    work_site_id integer NOT NULL,
    worker_id integer NOT NULL,
    radius double precision
);


ALTER TABLE public.custom_worker_radius OWNER TO misha;

--
-- Name: documents_i9; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.documents_i9 (
    id integer NOT NULL,
    document_title character varying(255),
    issuing_authority character varying(255),
    document_number character varying(255),
    expiration_date date,
    user_id integer,
    document_number_ciphertext bytea,
    document_number_h bytea,
    document_number_iv bytea,
    document_number_key_version integer,
    document_number_last4 character varying(255)
);


ALTER TABLE public.documents_i9 OWNER TO misha;

--
-- Name: documents_i9_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.documents_i9_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.documents_i9_id_seq OWNER TO misha;

--
-- Name: documents_i9_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.documents_i9_id_seq OWNED BY public.documents_i9.id;


--
-- Name: employer_tax_record; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.employer_tax_record (
    id integer NOT NULL,
    gross_pay numeric(38,2),
    social_security_tax numeric(38,2),
    medicare_tax numeric(38,2),
    futa_tax numeric(38,2),
    suta_tax numeric(38,2),
    federal_withholding numeric(38,2),
    total_employer_tax numeric(38,2),
    period_start date,
    period_end date,
    created_at date DEFAULT CURRENT_TIMESTAMP,
    social_security_taxable_wages numeric(38,2),
    social_security_tips numeric(38,2),
    medicare_taxable_wages numeric(38,2),
    additional_medicare_wages numeric(38,2),
    payment_date date,
    futa_taxable_wages numeric(38,2),
    suta_taxable_wages numeric(38,2),
    company_id integer,
    employee_id integer,
    pay_stub_id integer
);


ALTER TABLE public.employer_tax_record OWNER TO misha;

--
-- Name: employer_tax_record_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.employer_tax_record_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.employer_tax_record_id_seq OWNER TO misha;

--
-- Name: employer_tax_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.employer_tax_record_id_seq OWNED BY public.employer_tax_record.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO misha;

--
-- Name: inactive_days; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.inactive_days (
    work_site_id integer NOT NULL,
    inactive_date date NOT NULL
);


ALTER TABLE public.inactive_days OWNER TO misha;

--
-- Name: location_record; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.location_record (
    id integer NOT NULL,
    user_id integer NOT NULL,
    latitude double precision NOT NULL,
    longitude double precision NOT NULL,
    "timestamp" timestamp without time zone NOT NULL,
    accuracy double precision,
    speed double precision,
    bearing double precision,
    altitude double precision,
    provider character varying(255),
    battery_level integer,
    distance_from_previous double precision
);


ALTER TABLE public.location_record OWNER TO misha;

--
-- Name: location_record_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.location_record_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.location_record_id_seq OWNER TO misha;

--
-- Name: location_record_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.location_record_id_seq OWNED BY public.location_record.id;


--
-- Name: notification; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.notification (
    id integer NOT NULL,
    created_at timestamp(6) without time zone,
    title character varying(255),
    company_id integer NOT NULL
);


ALTER TABLE public.notification OWNER TO misha;

--
-- Name: notification_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

ALTER TABLE public.notification ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.notification_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: payment_history_irs; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.payment_history_irs (
    payment_history_irs_id integer NOT NULL,
    amount numeric(38,2),
    payment_date date,
    created_at timestamp without time zone,
    quarter integer,
    year integer,
    payment_type_enum character varying(255),
    notes character varying(255),
    company_id integer
);


ALTER TABLE public.payment_history_irs OWNER TO misha;

--
-- Name: payment_history_irs_payment_history_irs_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.payment_history_irs_payment_history_irs_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.payment_history_irs_payment_history_irs_id_seq OWNER TO misha;

--
-- Name: payment_history_irs_payment_history_irs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.payment_history_irs_payment_history_irs_id_seq OWNED BY public.payment_history_irs.payment_history_irs_id;


--
-- Name: scheduler_execution_history; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.scheduler_execution_history (
    id integer NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    company_id integer,
    company_name character varying(255),
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone,
    status character varying(50) NOT NULL,
    error_message text,
    retry_count integer DEFAULT 0,
    duration_seconds bigint,
    records_processed integer,
    records_failed integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE public.scheduler_execution_history OWNER TO misha;

--
-- Name: scheduler_execution_history_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.scheduler_execution_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.scheduler_execution_history_id_seq OWNER TO misha;

--
-- Name: scheduler_execution_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.scheduler_execution_history_id_seq OWNED BY public.scheduler_execution_history.id;


--
-- Name: terms_of_use_agreement; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.terms_of_use_agreement (
    id integer NOT NULL,
    event character varying(255),
    userid integer,
    "timestamp" date,
    termsversion character varying(255),
    privacyversion character varying(255),
    ip character varying(255),
    device character varying(255),
    osversion character varying(255),
    os_version character varying(255),
    privacy_version character varying(255),
    terms_version character varying(255),
    time_stamp date,
    user_id integer
);


ALTER TABLE public.terms_of_use_agreement OWNER TO misha;

--
-- Name: terms_of_use_agreement_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.terms_of_use_agreement_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.terms_of_use_agreement_id_seq OWNER TO misha;

--
-- Name: terms_of_use_agreement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.terms_of_use_agreement_id_seq OWNED BY public.terms_of_use_agreement.id;


--
-- Name: token; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.token (
    id integer NOT NULL,
    token character varying(255),
    created_at timestamp without time zone,
    expires_at timestamp without time zone,
    validated_at timestamp without time zone,
    user_id integer NOT NULL
);


ALTER TABLE public.token OWNER TO misha;

--
-- Name: token_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.token_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.token_id_seq OWNER TO misha;

--
-- Name: token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.token_id_seq OWNED BY public.token.id;


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.user_roles (
    user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.user_roles OWNER TO misha;

--
-- Name: user_work_sites; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.user_work_sites (
    work_site_id integer NOT NULL,
    user_id integer NOT NULL
);


ALTER TABLE public.user_work_sites OWNER TO misha;

--
-- Name: wc_risk_class; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.wc_risk_class (
    code character varying(10) NOT NULL,
    description character varying(255),
    rate numeric(6,4),
    industry_tag character varying(30),
    effective_year integer
);


ALTER TABLE public.wc_risk_class OWNER TO misha;

--
-- Name: work_site; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.work_site (
    id integer NOT NULL,
    site_name character varying(255),
    address character varying(255),
    latitude double precision,
    longitude double precision,
    allowed_radius double precision,
    work_day_start time without time zone,
    work_day_end time without time zone,
    is_active boolean,
    is_worker_did_punch_in boolean,
    company_id integer
);


ALTER TABLE public.work_site OWNER TO misha;

--
-- Name: work_site_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.work_site_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.work_site_id_seq OWNER TO misha;

--
-- Name: work_site_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.work_site_id_seq OWNED BY public.work_site.id;


--
-- Name: worker_attendance; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.worker_attendance (
    id integer NOT NULL,
    check_in_time timestamp without time zone,
    check_in_photo_url character varying(255),
    check_in_latitude double precision,
    check_in_longitude double precision,
    check_in_location character varying(255),
    check_out_time timestamp without time zone,
    check_out_photo_url character varying(255),
    check_out_latitude double precision,
    check_out_longitude double precision,
    check_out_location character varying(255),
    hours_worked double precision,
    overtime_hours double precision,
    gross_pay_per_day numeric(38,2),
    net_pay numeric(38,2),
    period_start date,
    period_end date,
    notes character varying(255),
    is_verified boolean,
    verified_by character varying(255),
    verification_time timestamp without time zone,
    worker_id integer
);


ALTER TABLE public.worker_attendance OWNER TO misha;

--
-- Name: worker_attendance_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.worker_attendance_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.worker_attendance_id_seq OWNER TO misha;

--
-- Name: worker_attendance_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.worker_attendance_id_seq OWNED BY public.worker_attendance.id;


--
-- Name: worker_payroll; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.worker_payroll (
    id integer NOT NULL,
    period_start date,
    period_end date,
    base_hourly_rate numeric(38,2),
    over_time_rate numeric(38,2),
    regular_hours double precision,
    overtime_hours double precision,
    total_hours double precision,
    regular_pay numeric(38,2),
    overtime_pay numeric(38,2),
    gross_pay numeric(38,2),
    medicare numeric(38,2),
    social_security_employee numeric(38,2),
    federal_withholding numeric(38,2),
    ny_state_withholding numeric(38,2),
    ny_local_withholding numeric(38,2),
    ny_disability_withholding numeric(38,2),
    ny_paid_family_leave numeric(38,2),
    total_deductions numeric(38,2),
    retirement401k_contribution numeric(38,2),
    health_insurance_cost numeric(38,2),
    has_retirement_plan boolean,
    ny_unemployment_withholding numeric(12,2),
    net_pay numeric(38,2),
    employer_taxes_calculated boolean,
    pay_stub_generated boolean,
    worker_id integer,
    company_id integer,
    wc_risk_code character varying(10)
);


ALTER TABLE public.worker_payroll OWNER TO misha;

--
-- Name: worker_payroll_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.worker_payroll_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.worker_payroll_id_seq OWNER TO misha;

--
-- Name: worker_payroll_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.worker_payroll_id_seq OWNED BY public.worker_payroll.id;


--
-- Name: worker_schedule; Type: TABLE; Schema: public; Owner: misha
--

CREATE TABLE public.worker_schedule (
    id integer NOT NULL,
    schedule_date date,
    expected_start_time time without time zone,
    expected_end_time time without time zone,
    shift character varying(255),
    is_on_duty boolean,
    start_lunch timestamp without time zone,
    end_lunch timestamp without time zone,
    is_company_paying_lunch boolean,
    worker_id integer,
    work_site_id integer
);


ALTER TABLE public.worker_schedule OWNER TO misha;

--
-- Name: worker_schedule_id_seq; Type: SEQUENCE; Schema: public; Owner: misha
--

CREATE SEQUENCE public.worker_schedule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.worker_schedule_id_seq OWNER TO misha;

--
-- Name: worker_schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: misha
--

ALTER SEQUENCE public.worker_schedule_id_seq OWNED BY public.worker_schedule.id;


--
-- Name: _company id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._company ALTER COLUMN id SET DEFAULT nextval('public._company_id_seq'::regclass);


--
-- Name: _dependents id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._dependents ALTER COLUMN id SET DEFAULT nextval('public._dependents_id_seq'::regclass);


--
-- Name: _role id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._role ALTER COLUMN id SET DEFAULT nextval('public._role_id_seq'::regclass);


--
-- Name: _user id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._user ALTER COLUMN id SET DEFAULT nextval('public._user_id_seq'::regclass);


--
-- Name: contact_sales_form id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.contact_sales_form ALTER COLUMN id SET DEFAULT nextval('public.contact_sales_form_id_seq'::regclass);


--
-- Name: documents_i9 id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.documents_i9 ALTER COLUMN id SET DEFAULT nextval('public.documents_i9_id_seq'::regclass);


--
-- Name: employer_tax_record id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.employer_tax_record ALTER COLUMN id SET DEFAULT nextval('public.employer_tax_record_id_seq'::regclass);


--
-- Name: location_record id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.location_record ALTER COLUMN id SET DEFAULT nextval('public.location_record_id_seq'::regclass);


--
-- Name: payment_history_irs payment_history_irs_id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.payment_history_irs ALTER COLUMN payment_history_irs_id SET DEFAULT nextval('public.payment_history_irs_payment_history_irs_id_seq'::regclass);


--
-- Name: scheduler_execution_history id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.scheduler_execution_history ALTER COLUMN id SET DEFAULT nextval('public.scheduler_execution_history_id_seq'::regclass);


--
-- Name: terms_of_use_agreement id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.terms_of_use_agreement ALTER COLUMN id SET DEFAULT nextval('public.terms_of_use_agreement_id_seq'::regclass);


--
-- Name: token id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.token ALTER COLUMN id SET DEFAULT nextval('public.token_id_seq'::regclass);


--
-- Name: work_site id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.work_site ALTER COLUMN id SET DEFAULT nextval('public.work_site_id_seq'::regclass);


--
-- Name: worker_attendance id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_attendance ALTER COLUMN id SET DEFAULT nextval('public.worker_attendance_id_seq'::regclass);


--
-- Name: worker_payroll id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_payroll ALTER COLUMN id SET DEFAULT nextval('public.worker_payroll_id_seq'::regclass);


--
-- Name: worker_schedule id; Type: DEFAULT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_schedule ALTER COLUMN id SET DEFAULT nextval('public.worker_schedule_id_seq'::regclass);


--
-- Data for Name: _company; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public._company (id, company_name, company_address, company_phone, company_email, company_state_id_number, company_city, company_state, company_zip_code, employer_ein, special_two_char_condition_code_for_mta305, company_income_per_month, costs_for_employee_salaries_per_month, regular_pay_per_month_total, overtime_pay_per_month_total, gross_pay_per_month_total, total_spend_money_per_year, company_insurance, social_security_tax_for_company, federal_withholding_for_company, ny_state_withholding_for_company, ny_local_withholding_for_company, company_payment_position, irs_deposited_amount, when_deposit_amount_was_made, workers_quantity, first_biweekly_date, emr, wc_policy_number, wc_insurance_carrier, funding_bank_name, funding_routing_number, funding_account_number, return_mailing_address, default_memo, signature_name, signature_title, owner_id) FROM stdin;
1	CP Compasny 2	407 Ocean view ave2	34782857902	mishamay583@gmail.com2	208105407	Brooklyn	NY	11235	12-3456789	F2	0.00	0.00	0.00	0.00	0.00	0.00	0.00	4.10	0.00	0.00	0.00	WEEKLY	0.00	\N	1	\N	1.25	WC-2025-12345	ACME Workers’ Comp Ins.								1
2	TEST 	407 Ocen View 1666	+1-347-828-5799	mishamaykinghs@gmail.com	208105407	Brooklyn	NY	11235	12-3456789	F2	0.00	0.00	0.00	0.00	0.00	0.00	0.00	4.10	0.00	0.00	0.00	BIWEEKLY	0.00	\N	1	2025-08-10	1.25	WC-2025-12345	ACME Workers’ Comp Ins.								2
\.


--
-- Data for Name: _dependents; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public._dependents (id, first_name, last_name, birth_date, user_id) FROM stdin;
\.


--
-- Data for Name: _role; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public._role (id, name, created_date, last_modified_date) FROM stdin;
1	USER	2025-08-10 16:43:19.259713	\N
2	ADMIN	2025-08-10 16:43:19.281587	\N
3	FOREMAN	2025-08-10 16:43:19.285415	\N
4	AppOwner	2025-08-18 21:47:59.554072	\N
\.


--
-- Data for Name: _user; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public._user (id, first_name, middle_initial, last_name, email, phone_number, date_of_birth, home_address, apt, city, state, zipcode, is_citizen, is_non_citizen_national_of_the_us, is_permanent_resident, is_non_active, is_rehired, date_when_rehired, work_authrization_expiry_date, uscis_number, form_i94_admission_number, passport_country_of_issuance, photo_file_name, photo_url, base_hourly_rate, overtime_rate, is_admin, is_foreman, is_user, is_business_owner, password, enabled, account_locked, ssn_worker, gender, filing_status, dependents, extra_with_holdings, multiple_jobs_or_spouse_works, two_jobs_check_box, multiple_jobs_additional_withholding, dependents_under_17, other_dependents, total_dependents_credit, other_income, deductions, exempt_from_withholding, multiple_jobs_worksheet_line_2a, multiple_jobs_worksheet_line_2b, estimated_itemized_deductions, adjustments_schedule1, live_in_nyc, pay_frequency, employment_type, coverage_start_date, enrolled_in_health_plan, monthly_health_premium, period_charge_insurance, sick_leave_accrued, sick_leave_used, hours_worked_year_to_date, sick_leave_accrued_this_year, sick_leave_paid, hire_date, last_sick_leave_carryover_date, sick_leave_carried_over, company_id, wc_risk_class_code, work_site_id, current_work_site_id, created_date, last_modified_date, passport_number, actual_budget, expenses, cost_of_salaries, profit, ssn_ciphertext, ssn_h, ssn_iv, ssn_key_version, ssn_last4) FROM stdin;
4	Worker		2	worker2@example.com	+1-347-000-0002	1988-02-02	200 Main St		Brooklyn	NY	11202	f	f	f	f	f	\N	\N						22.00	33.00	f	f	t	f	$2a$10$88sPxQ5j35Ie4tV0zt.qMuhQBNmRK5VxGQ06utbV7CJPYPyMOiFvu	t	f	222-22-2222	FEMALE	MARRIED_FILLING_JOINTLY	2	0.00	t	t	50.00	2	0	4000.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	WEEKLY	W2	\N	f	\N	0.00	0.00	0.00	0.00	0.00	t	\N	\N	0.00	1	\N	\N	\N	2025-08-10 16:43:26.490174	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
5	Worker		3	worker3@example.com	+1-347-000-0003	1992-03-03	300 Main St		Brooklyn	NY	11203	f	f	f	f	f	\N	\N						24.00	36.00	f	f	t	f	$2a$10$mGw.LNzUzskPzCwpmbm4u.V2XaF.JkQcGWF72ecQXcxP1HZBg27Ce	t	f	333-33-3333	FEMALE	HEAD_OF_HOUSEHOLD	1	0.00	f	f	0.00	1	0	2000.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	WEEKLY	W2	\N	f	\N	0.00	0.00	0.00	0.00	0.00	t	\N	\N	0.00	1	\N	\N	\N	2025-08-10 16:43:27.92227	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
6	Worker		4	worker4@example.com	+1-347-000-0004	1994-04-04	400 Main St		Brooklyn	NY	11204	f	f	f	f	f	\N	\N						26.00	39.00	f	f	t	f	$2a$10$o1llkvxmxycfktnVigtt1eK2zsweyiLXkEsObpyVwNplDiQBcI/qS	t	f	444-44-4444	MALE	MARRIED_FILLING_SEPARATELY	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	WEEKLY	W2	\N	f	\N	0.00	0.00	0.00	0.00	0.00	t	\N	\N	0.00	1	\N	\N	\N	2025-08-10 16:43:29.392381	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
7	Worker		5	worker5@example.com	+1-347-000-0005	1996-05-05	500 Main St		Brooklyn	NY	11205	f	f	f	f	f	\N	\N						28.00	42.00	f	f	t	f	$2a$10$90LZVEvBHjX3uXvwAdgZuO/OwJR2nAE7q51B4tJLUsUG7GwxVa3ZO	t	f	555-55-5555	FEMALE	SINGLE	0	0.00	t	t	75.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	WEEKLY	W2	\N	f	\N	0.00	0.00	0.00	0.00	0.00	t	\N	\N	0.00	1	\N	\N	\N	2025-08-10 16:43:30.804463	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
8	Worker 		 1	mishamaykinghsbr67@gmail.com	+1-347-828-5790	1995-08-08	407 Ocean View Ave #2A		Brooklyn	NY	11236	f	f	f	f	f	\N	\N						25.00	0.00	f	f	t	f	$2a$10$NG/4xtJPlwvkPeM7m0S6TewZMvCWPWCnveV/s25kM9mcdugp6pVTK	t	f	123-12-1234	MALE	SINGLE	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	BIWEEKLY	W2	\N	f	\N	\N	0.00	0.00	0.00	0.00	t	2025-01-01	\N	0.00	2	\N	\N	\N	2025-08-10 16:43:32.373106	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
9	Worker  		 2	mishamay712@gmail.com	+1-347-828-5791	1995-08-08	407 Ocean View Ave #4A		Brooklyn	NY	11237	f	f	f	f	f	\N	\N						25.00	0.00	f	f	t	f	$2a$10$0XxvUa0Q1l6BAgHE9wcYu.4ATPQmmgFw8Ekoinr6MJgVU39dt9ZgC	t	f	123-12-1234	MALE	MARRIED_FILLING_SEPARATELY	2	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	BIWEEKLY	W2	\N	f	\N	\N	0.00	0.00	0.00	0.00	t	2025-01-01	\N	0.00	2	\N	\N	\N	2025-08-10 16:43:33.640131	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
10	Worker 		 3	worker18@gmail.com	+1-347-828-5794	1995-08-08	407 Ocean View Ave #6A		Brooklyn	NY	11237	f	f	f	f	f	\N	\N						25.00	0.00	f	f	t	f	$2a$10$rfTrJ6TU5VDh1cR6aRoUfOD334iu/iBu/xtHXgnAfKPSxfWqETAV6	t	f	123-12-1234	MALE	HEAD_OF_HOUSEHOLD	1	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	BIWEEKLY	W2	\N	f	\N	\N	0.00	0.00	0.00	0.00	t	2025-01-01	\N	0.00	2	\N	\N	\N	2025-08-10 16:43:35.393418	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
11	Worker 		 4	worker954@gmail.com	+1-347-828-5734	1995-08-08	407 Ocean View Ave #7A		Brooklyn	NY	11237	f	f	f	f	f	\N	\N						25.00	0.00	f	f	t	f	$2a$10$D4inHa2h4oFGa03gJZQQ..ZpaPXoD1ZgUxo5Q1o8q2xqO7ekS85YC	t	f	123-12-1234	MALE	SINGLE	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	BIWEEKLY	W2	\N	f	\N	\N	0.00	0.00	0.00	0.00	t	2025-01-01	\N	0.00	2	\N	\N	\N	2025-08-10 16:43:37.104195	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
12	Worker 		 5	worker1014@gmail.com	+1-347-828-5734	1995-08-08	407 Ocean View Ave #7A		Brooklyn	NY	11237	f	f	f	f	f	\N	\N						25.00	0.00	f	f	t	f	$2a$10$ucf421C85O1VeU30rfm4Be/Eu7YThEfvDI08p/GYHsoL3Dx/orA7m	t	f	123-12-1234	MALE	SINGLE	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	BIWEEKLY	W2	\N	f	\N	\N	0.00	0.00	0.00	0.00	t	2025-01-01	\N	0.00	2	\N	\N	\N	2025-08-10 16:43:38.86848	\N		\N	\N	\N	\N	\N	\N	\N	\N	\N
1	Test 		 Admin1	mishamay583@gmail.com	+1-347-828-5790	1995-08-08	407 Ocean View Ave #2A		Brooklyn	NY	11235	f	f	f	f	f	\N	\N				profile_mishamay583@gmail.com_20250810-200737.jpg	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/profile_mishamay583%40gmail.com_20250810-200737.jpg	25.00	0.00	t	f	f	t	$2a$10$wPWbFZobDY5ugs5jqqVcYeIrQfvURBmzRhR0wmTEsDJtad8UW/qoG	t	f	123-12-1234	MALE	SINGLE	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	WEEKLY	W2	\N	f	\N	\N	0.13	0.00	4.62	0.13	t	2025-01-01	\N	0.00	1	\N	\N	1	2025-08-10 16:43:24.114354	2025-10-05 19:00:00.079336		100000.00	385.75	110.75	99614.25	\N	\N	\N	\N	\N
2	Test 		 Admin2	mishamaykinghsbr1@gmail.com	+1-347-828-5790	1995-08-08	407 Ocean View Ave #2A		Brooklyn	NY	11236	f	f	f	f	f	\N	\N						25.00	0.00	t	f	f	t	$2a$10$GLHwbF0THjGgWDqhEuLGiejLQFy5YU8OLmJ4meT9XsM8.xK7qg4lW	t	f	123-12-1234	MALE	SINGLE	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	BIWEEKLY	W2	\N	f	\N	\N	0.00	0.00	0.00	0.00	t	2025-01-01	\N	0.00	2	\N	\N	\N	2025-08-10 16:43:24.821439	2025-09-28 18:00:00.05135		\N	0.00	0.00	\N	\N	\N	\N	\N	\N
3	Worker		1	worker1@example.com	+1-347-000-0001	1990-01-01	100 Main St		Brooklyn	NY	11201	f	f	f	f	f	\N	\N						20.00	30.00	t	t	f	f	$2a$10$Zg7VmgsjKhjMB6JVJJtTw.f5Y2vhCNw67fQ8ePFAc0g7083LxEky.	t	f	111-11-1111	MALE	SINGLE	0	0.00	f	f	0.00	0	0	0.00	0.00	0.00	f	0.00	0.00	0.00	0.00	t	WEEKLY	W2	\N	f	\N	0.00	0.00	0.00	0.00	0.00	t	\N	\N	0.00	1	\N	\N	\N	2025-08-10 16:43:25.468708	2025-10-09 20:50:23.883447		\N	\N	\N	\N	\N	\N	\N	\N	\N
\.


--
-- Data for Name: contact_sales_form; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.contact_sales_form (id, first_name, last_name, phone_number, created_date) FROM stdin;
1	Mykhailo	Maidanskyi	3478285790	2025-08-23
2	Mykhailo	Maidanskyi	3478285790	2025-08-23
\.


--
-- Data for Name: custom_worker_radius; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.custom_worker_radius (work_site_id, worker_id, radius) FROM stdin;
\.


--
-- Data for Name: documents_i9; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.documents_i9 (id, document_title, issuing_authority, document_number, expiration_date, user_id, document_number_ciphertext, document_number_h, document_number_iv, document_number_key_version, document_number_last4) FROM stdin;
\.


--
-- Data for Name: employer_tax_record; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.employer_tax_record (id, gross_pay, social_security_tax, medicare_tax, futa_tax, suta_tax, federal_withholding, total_employer_tax, period_start, period_end, created_at, social_security_taxable_wages, social_security_tips, medicare_taxable_wages, additional_medicare_wages, payment_date, futa_taxable_wages, suta_taxable_wages, company_id, employee_id, pay_stub_id) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	init schema	SQL	V1__init_schema.sql	947713632	misha	2025-08-10 16:43:15.663646	125	t
2	2	alter effective year type	SQL	V2__alter_effective_year_type.sql	-287203275	misha	2025-08-10 16:43:15.806889	8	t
3	3	create scheduler execution history	SQL	V3__create_scheduler_execution_history.sql	950404665	misha	2025-08-10 16:43:15.826623	18	t
4	4	alter table user fields	SQL	V4__alter_table_user_fields.sql	1753451230	misha	2025-08-10 16:43:15.851437	2	t
5	5	alter table worker schedule	SQL	V5__alter_table_worker_schedule.sql	1113334591	misha	2025-08-10 16:43:15.858397	8	t
6	6	alter table location record	SQL	V6__alter_table_location_record.sql	264708534	misha	2025-08-13 20:40:03.505212	65	t
7	7	alter yable location record optimization	SQL	V7__alter_yable_location_record_optimization.sql	-356851852	misha	2025-08-18 21:47:55.754546	96	t
8	8	alter table contact sales and privecy polisy	SQL	V8__alter_table_contact_sales_and_privecy_polisy.sql	-1610606402	misha	2025-08-23 19:02:17.697702	77	t
9	9	alter table contact sales created date	SQL	V9__alter_table_contact_sales_created_date.sql	-380875368	misha	2025-08-23 19:19:27.010829	23	t
10	10	rename migration contact sales	SQL	V10__rename_migration_contact_sales.sql	2100260988	misha	2025-08-23 19:24:53.142984	14	t
11	12	alter table notification	SQL	V12__alter_table_notification.sql	-1856934502	misha	2025-09-01 16:07:16.042769	83	t
12	13	alter table user	SQL	V13__alter_table_user.sql	1090917460	misha	2025-09-28 14:23:38.632366	7	t
\.


--
-- Data for Name: inactive_days; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.inactive_days (work_site_id, inactive_date) FROM stdin;
2	2025-11-24
\.


--
-- Data for Name: location_record; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.location_record (id, user_id, latitude, longitude, "timestamp", accuracy, speed, bearing, altitude, provider, battery_level, distance_from_previous) FROM stdin;
1	1	40.5793266	-73.9636725	2025-08-19 14:02:03.514	11.581999778747559	0	0	-30.399999618530273	\N	86	\N
2	1	40.5793266	-73.9636725	2025-08-19 14:02:03.514	11.581999778747559	0	0	-30.399999618530273	\N	86	0
3	1	40.5793266	-73.9636725	2025-08-19 14:02:03.514	11.581999778747559	0	0	-30.399999618530273	\N	86	0
4	1	40.579328	-73.9636653	2025-08-19 14:07:04.705	20	0.016372254118323326	0	-30.399999618530273	\N	88	0.6276741937118864
5	1	40.5793276	-73.9636627	2025-08-19 14:12:00.72	20	0.030327720567584038	0	-30.399999618530273	\N	90	0.22403783513305836
6	1	40.5793245	-73.9636637	2025-08-19 14:16:39.178	20	0.009960650466382504	0	-30.399999618530273	\N	92	0.3548991157158724
7	1	40.5793245	-73.9636637	2025-08-19 14:16:39.178	20	0.009960650466382504	0	-30.399999618530273	\N	92	0
8	1	40.5793245	-73.9636637	2025-08-19 14:16:39.178	20	0.009960650466382504	0	-30.399999618530273	\N	94	0
9	1	40.5793253	-73.9636628	2025-08-19 14:26:40.555	20	0	0	-30.399999618530273	\N	100	0.11700581466992835
10	1	40.5793253	-73.9636628	2025-08-19 14:26:40.555	20	0	0	-30.399999618530273	\N	100	0
11	1	40.5793253	-73.9636628	2025-08-19 14:26:40.555	20	0	0	-30.399999618530273	\N	100	0
12	1	40.5793323	-73.9636586	2025-08-19 14:37:17.929	13.866000175476074	0	0	-30.399999618530273	\N	100	0.8553746895324866
13	1	40.5793323	-73.9636586	2025-08-19 14:37:17.929	13.866000175476074	0	0	-30.399999618530273	\N	100	0
14	1	40.5793234	-73.9636719	2025-08-19 14:42:20.928	20	0	0	-30.399999618530273	\N	100	1.4970029662589646
15	1	40.5793234	-73.9636719	2025-08-19 14:42:20.928	20	0	0	-30.399999618530273	\N	100	0
16	1	40.5793311	-73.9636682	2025-08-19 16:28:35.494	20	0	0	-30.399999618530273	\N	97	0.9114394463521701
17	1	40.5793348	-73.9636653	2025-08-19 17:06:27.085	17.993000030517578	0	0	-30.399999618530273	\N	94	0.4788010560632518
18	1	40.5793327	-73.9636726	2025-08-19 17:06:50.311	13.680999755859375	0.011503527872264385	0	-30.399999618530273	\N	94	0.6592490315758407
19	1	40.5793335	-73.9636814	2025-10-05 19:38:48.279	18.136999130249023	0	0	-30.399999618530273	\N	95	0.7484931378056301
20	1	40.5793335	-73.9636814	2025-10-05 19:38:48.279	18.136999130249023	0	0	-30.399999618530273	\N	95	0
21	1	40.579329	-73.9636685	2025-10-05 19:40:56.384	20	0.008842702955007553	0	-30.399999618530273	\N	96	1.1988623580668898
22	1	40.5793313	-73.9636694	2025-10-05 19:54:15.775	11.906999588012695	0.05472683161497116	0	-30.399999618530273	\N	100	0.26680406497285514
23	1	40.5793313	-73.9636694	2025-10-05 19:54:15.775	11.906999588012695	0.05472683161497116	0	-30.399999618530273	\N	100	0
24	1	40.5793267	-73.9636661	2025-10-05 20:05:05.185	20	0.02921995334327221	0	-30.399999618530273	\N	100	0.5824947082089511
25	1	40.5793267	-73.9636661	2025-10-05 20:14:56.661	100	0	0	-30.399999618530273	\N	100	0
26	1	40.5793231	-73.9636671	2025-10-05 20:28:41.371	20	0	0	-30.399999618530273	\N	100	0.40911346434807755
27	1	40.5793231	-73.9636671	2025-10-05 20:28:41.371	20	0	0	-30.399999618530273	\N	100	0
28	1	40.5793298	-73.9636781	2025-10-05 20:56:32.155	15.437999725341797	0	0	-30.399999618530273	\N	100	1.1908181643858018
29	1	40.5793343	-73.9636704	2025-10-05 20:59:40.884	11.85200023651123	0.06899673491716385	0	-30.399999618530273	\N	100	0.8205206130267102
30	1	40.5793343	-73.9636704	2025-10-05 20:59:40.884	11.85200023651123	0.06899673491716385	0	-30.399999618530273	\N	100	0
31	1	40.5793292	-73.9636681	2025-10-05 21:02:40.699	20	0	0	-30.399999618530273	\N	100	0.5994379498062076
32	1	40.5793312	-73.9636695	2025-10-05 21:08:16.297	15.550999641418457	0	0	-30.399999618530273	\N	100	0.2518663229636569
33	1	40.579335	-73.9636753	2025-10-05 21:12:38.111	12.83899974822998	0.04597623273730278	0	-30.399999618530273	\N	100	0.6468947031678693
34	1	40.5793255	-73.9636665	2025-10-05 21:12:56.896	20	0.05496242642402649	0	-30.399999618530273	\N	100	1.2915912651993944
35	1	40.5793255	-73.9636665	2025-10-05 21:12:56.896	20	0.05496242642402649	0	-30.399999618530273	\N	100	0
36	1	40.5793247	-73.9636635	2025-10-05 21:17:57.016	20	0.060154132544994354	0	-30.399999618530273	\N	100	0.26852240611305234
\.


--
-- Data for Name: notification; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.notification (id, created_at, title, company_id) FROM stdin;
12	2025-09-23 17:14:21.412	Hello From Hikari0	1
13	2025-09-23 17:14:21.457	Hello From Hikari1	1
14	2025-09-23 17:14:21.46	Hello From Hikari2	1
15	2025-09-23 17:14:21.461	Hello From Hikari3	1
19	2025-10-05 16:56:36.944542	Test   Admin1 made punch out, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
20	2025-10-05 16:59:42.424557	Test   Admin1 made punch in, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
21	2025-10-05 17:02:43.133859	Test   Admin1 made punch out, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
22	2025-10-05 17:08:17.916462	Test   Admin1 made punch in, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
23	2025-10-05 17:12:40.378179	Test   Admin1 made punch out, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
24	2025-10-05 17:12:56.767094	Test   Admin1 made punch in, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
25	2025-10-05 17:17:57.731976	Test   Admin1 made punch out, at:2025-10-05 in test Site 1 407 Ocean view Ave	1
26	2025-10-11 15:39:03.429395	Worksite: erveve sgvsgrsg was successfully registered	1
\.


--
-- Data for Name: payment_history_irs; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.payment_history_irs (payment_history_irs_id, amount, payment_date, created_at, quarter, year, payment_type_enum, notes, company_id) FROM stdin;
1	25.00	2025-09-28	2025-09-28 15:07:51.559295	4	2025	UNEMPLOYMENT_TAX_940	I made payment to UT 	1
2	250.00	2025-09-28	2025-09-28 19:12:23.626758	3	2025	PAYROLL_TAX_941	уауауа	1
\.


--
-- Data for Name: scheduler_execution_history; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.scheduler_execution_history (id, job_name, job_group, company_id, company_name, start_time, end_time, status, error_message, retry_count, duration_seconds, records_processed, records_failed, created_at) FROM stdin;
1	futaQuarterlyComplienceJob	TAX_JOBS	\N	\N	2025-08-11 06:05:20.23552	2025-08-11 06:05:20.339337	SUCCESS	\N	0	0	0	0	2025-08-11 06:05:20.269535
3	biWeeklyPayStubJob	TAX_JOBS	\N	\N	2025-08-17 04:55:19.989907	2025-08-17 04:55:20.132543	SUCCESS	\N	0	0	0	0	2025-08-17 04:55:20.020836
2	weeklyPayStubJob	TAX_JOBS	\N	\N	2025-08-17 04:55:19.989948	2025-08-17 04:55:20.132542	SUCCESS	\N	0	0	0	0	2025-08-17 04:55:20.020647
4	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-08-17 04:55:19.989907	2025-08-17 04:55:20.148243	SUCCESS	\N	0	0	0	0	2025-08-17 04:55:20.020647
5	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-08-17 06:12:56.193543	2025-08-17 06:12:56.861987	SUCCESS	\N	0	0	1	0	2025-08-17 06:12:56.200892
6	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-08-17 06:57:22.597759	2025-08-17 06:57:22.958834	SUCCESS	\N	0	0	1	0	2025-08-17 06:57:22.603642
7	futaQuarterlyComplienceJob	TAX_JOBS	\N	\N	2025-08-18 06:05:58.45132	2025-08-18 06:05:58.65714	SUCCESS	\N	0	0	1	0	2025-08-18 06:05:58.48099
8	weeklyPayStubJob	TAX_JOBS	\N	\N	2025-08-24 04:08:17.974447	2025-08-24 04:08:18.17117	SUCCESS	\N	0	0	0	0	2025-08-24 04:08:18.059353
9	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-08-24 04:08:17.972677	2025-08-24 04:08:18.211292	SUCCESS	\N	0	0	0	0	2025-08-24 04:08:18.058929
10	biWeeklyPayStubJob	TAX_JOBS	\N	\N	2025-08-24 04:39:11.992597	2025-08-24 04:39:12.015028	SUCCESS	\N	0	0	0	0	2025-08-24 04:39:11.998915
12	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-08-24 06:34:52.335633	2025-08-24 06:34:53.022367	SUCCESS	\N	0	0	1	0	2025-08-24 06:34:52.344842
11	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-08-24 06:34:52.331536	2025-08-24 06:34:53.022441	SUCCESS	\N	0	0	1	0	2025-08-24 06:34:52.344843
13	futaQuarterlyComplienceJob	TAX_JOBS	\N	\N	2025-08-25 06:13:24.139793	2025-08-25 06:13:24.330176	SUCCESS	\N	0	0	1	0	2025-08-25 06:13:24.166338
14	weeklyPayStubJob	TAX_JOBS	\N	\N	2025-08-31 04:22:57.700258	2025-08-31 04:22:57.894246	SUCCESS	\N	0	0	0	0	2025-08-31 04:22:57.728538
15	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-08-31 04:22:57.698255	2025-08-31 04:22:57.894247	SUCCESS	\N	0	0	0	0	2025-08-31 04:22:57.728811
16	biWeeklyPayStubJob	TAX_JOBS	\N	\N	2025-08-31 04:55:08.105077	2025-08-31 04:55:08.133557	SUCCESS	\N	0	0	0	0	2025-08-31 04:55:08.109351
17	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-08-31 06:08:54.208815	2025-08-31 06:08:54.26517	SUCCESS	\N	0	0	0	0	2025-08-31 06:08:54.221337
18	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-08-31 07:02:58.071944	2025-08-31 07:02:58.137126	SUCCESS	\N	0	0	0	0	2025-08-31 07:02:58.082746
19	futaQuarterlyComplienceJob	TAX_JOBS	\N	\N	2025-09-01 06:06:34.309125	2025-09-01 06:06:34.440978	SUCCESS	\N	0	0	0	0	2025-09-01 06:06:34.336357
20	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-09-14 04:05:26.133546	2025-09-14 04:05:26.325551	SUCCESS	\N	0	0	0	0	2025-09-14 04:05:26.178923
21	weeklyPayStubJob	TAX_JOBS	\N	\N	2025-09-14 04:05:26.133817	2025-09-14 04:05:26.325551	SUCCESS	\N	0	0	0	0	2025-09-14 04:05:26.178747
22	biWeeklyPayStubJob	TAX_JOBS	\N	\N	2025-09-14 04:40:07.708206	2025-09-14 04:40:07.738254	SUCCESS	\N	0	0	0	0	2025-09-14 04:40:07.715277
23	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-09-14 06:07:09.552159	2025-09-14 06:07:09.608453	SUCCESS	\N	0	0	0	0	2025-09-14 06:07:09.559236
24	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-09-14 06:48:09.73547	2025-09-14 06:48:09.819225	SUCCESS	\N	0	0	0	0	2025-09-14 06:48:09.743004
25	futaQuarterlyComplienceJob	TAX_JOBS	\N	\N	2025-09-15 06:20:36.902528	2025-09-15 06:20:37.050243	SUCCESS	\N	0	0	0	0	2025-09-15 06:20:36.94955
27	weeklyPayStubJob	TAX_JOBS	\N	\N	2025-09-21 04:14:34.595768	2025-09-21 04:14:34.727808	SUCCESS	\N	0	0	0	0	2025-09-21 04:14:34.618543
26	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-09-21 04:14:34.595501	2025-09-21 04:14:34.727808	SUCCESS	\N	0	0	0	0	2025-09-21 04:14:34.618544
28	biWeeklyPayStubJob	TAX_JOBS	\N	\N	2025-09-21 04:53:06.939378	2025-09-21 04:53:06.994943	SUCCESS	\N	0	0	0	0	2025-09-21 04:53:06.946652
29	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-09-21 06:27:56.002527	2025-09-21 06:27:56.037481	SUCCESS	\N	0	0	0	0	2025-09-21 06:27:56.009243
30	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-09-21 06:55:14.762754	2025-09-21 06:55:14.826004	SUCCESS	\N	0	0	0	0	2025-09-21 06:55:14.773296
31	futaQuarterlyComplienceJob	TAX_JOBS	\N	\N	2025-09-22 06:12:33.774757	2025-09-22 06:12:33.867364	SUCCESS	\N	0	0	1	0	2025-09-22 06:12:33.784188
33	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-09-28 04:01:47.64061	2025-09-28 04:01:47.719722	SUCCESS	\N	0	0	0	0	2025-09-28 04:01:47.66774
32	weeklyPayStubJob	TAX_JOBS	\N	\N	2025-09-28 04:01:47.641633	2025-09-28 04:01:47.719722	SUCCESS	\N	0	0	0	0	2025-09-28 04:01:47.66774
34	biWeeklyPayStubJob	TAX_JOBS	\N	\N	2025-09-28 05:02:33.49453	2025-09-28 05:02:33.520859	SUCCESS	\N	0	0	0	0	2025-09-28 05:02:33.498922
35	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-09-28 06:03:30.636497	2025-09-28 06:03:30.679402	SUCCESS	\N	0	0	0	0	2025-09-28 06:03:30.645041
36	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-09-28 06:36:30.426852	2025-09-28 06:36:30.455566	SUCCESS	\N	0	0	0	0	2025-09-28 06:36:30.428952
38	monthlyPayrollJob	REPORT_JOBS	\N	\N	2025-10-01 07:30:00.030437	2025-10-01 07:30:00.097056	SUCCESS	\N	0	0	0	0	2025-10-01 07:30:00.040737
37	monthlyHoursJob	REPORT_JOBS	\N	\N	2025-10-01 07:30:00.028518	2025-10-01 07:30:00.098778	SUCCESS	\N	0	0	0	0	2025-10-01 07:30:00.040711
39	calculateWeeklyEmployerTaxesJob	TAX_JOBS	\N	\N	2025-10-05 04:32:51.326841	2025-10-05 04:32:51.379385	SUCCESS	\N	0	0	0	0	2025-10-05 04:32:51.337263
41	weeklyPayrollJob	REPORT_JOBS	\N	\N	2025-10-05 06:44:21.907004	2025-10-05 06:44:21.935938	SUCCESS	\N	0	0	0	0	2025-10-05 06:44:21.911603
40	weeklyHoursJob	REPORT_JOBS	\N	\N	2025-10-05 06:44:21.904379	2025-10-05 06:44:21.938553	SUCCESS	\N	0	0	0	0	2025-10-05 06:44:21.911603
\.


--
-- Data for Name: terms_of_use_agreement; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.terms_of_use_agreement (id, event, userid, "timestamp", termsversion, privacyversion, ip, device, osversion, os_version, privacy_version, terms_version, time_stamp, user_id) FROM stdin;
1	ACCEPTED	\N	\N	\N	\N	24.188.156.166	samsung SM-A405FN	\N	Android 11	1.0.0	1.0.0	2025-10-04	1
\.


--
-- Data for Name: token; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.token (id, token, created_at, expires_at, validated_at, user_id) FROM stdin;
1	686815	2025-08-23 13:29:35.238348	2025-08-23 13:59:35.238354	\N	1
2	609950	2025-08-23 13:31:29.659624	2025-08-23 14:01:29.659627	\N	1
3	662131	2025-08-23 13:35:35.561119	2025-08-23 13:36:01.82626	2025-08-23 13:35:52.198352	1
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.user_roles (user_id, role_id) FROM stdin;
1	2
2	2
4	1
5	1
6	1
7	1
8	1
9	1
10	1
11	1
12	1
3	3
\.


--
-- Data for Name: user_work_sites; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.user_work_sites (work_site_id, user_id) FROM stdin;
1	3
1	4
1	5
1	6
1	7
2	8
2	9
2	10
2	11
2	12
1	1
\.


--
-- Data for Name: wc_risk_class; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.wc_risk_class (code, description, rate, industry_tag, effective_year) FROM stdin;
\.


--
-- Data for Name: work_site; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.work_site (id, site_name, address, latitude, longitude, allowed_radius, work_day_start, work_day_end, is_active, is_worker_did_punch_in, company_id) FROM stdin;
2	test Site 2	4072 Ocean view Ave	40.57940653335323	-73.96374728941586	200	07:30:00	16:30:00	t	\N	2
1	test Site 1	407 Ocean view Ave	40.57940653335323	-73.96374728941586	100	07:00:00	23:00:00	t	f	1
3	erveve	sgvsgrsg	40.58562967670083	-73.97078859289184	100	15:38:00	16:38:00	t	f	1
\.


--
-- Data for Name: worker_attendance; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.worker_attendance (id, check_in_time, check_in_photo_url, check_in_latitude, check_in_longitude, check_in_location, check_out_time, check_out_photo_url, check_out_latitude, check_out_longitude, check_out_location, hours_worked, overtime_hours, gross_pay_per_day, net_pay, period_start, period_end, notes, is_verified, verified_by, verification_time, worker_id) FROM stdin;
2	2025-08-10 16:49:50.085498	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250810-164947.jpg	40.5793357	-73.9636771	407 Ocean view Ave	2025-08-10 16:58:34.868878	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250810-165833.jpg	40.5793313	-73.9636683	407 Ocean view Ave	0.13333333333333333	0	3.33	2.64	2025-08-10	2025-08-16	\N	\N	\N	\N	1
3	2025-08-10 16:59:06.112222	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250810-165905.jpg	40.579324	-73.9636694	407 Ocean view Ave	2025-08-10 17:04:37.661483	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250810-170436.jpg	40.5793265	-73.9636686	407 Ocean view Ave	0.08333333333333333	0	2.08	1.65	2025-08-10	2025-08-16	\N	\N	\N	\N	1
4	2025-08-10 19:50:03.449267	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250810-195001.jpg	40.5793265	-73.9636657	407 Ocean view Ave	2025-08-10 19:51:34.954435	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250810-195134.jpg	40.5793296	-73.9636697	407 Ocean view Ave	0.016666666666666666	0	0.42	0.34	2025-08-10	2025-08-16	\N	\N	\N	\N	1
5	2025-08-10 20:23:42.48782	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250810-202341.jpg	40.5793273	-73.9636657	407 Ocean view Ave	2025-08-10 20:24:02.301605	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250810-202401.jpg	40.5793311	-73.9636693	407 Ocean view Ave	0	0	0.00	\N	2025-08-10	2025-08-16	\N	\N	\N	\N	1
6	2025-08-10 20:24:27.674949	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250810-202426.jpg	40.5793248	-73.9636737	407 Ocean view Ave	2025-08-10 20:24:51.685568	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250810-202451.jpg	40.5793308	-73.9636725	407 Ocean view Ave	0	0	0.00	\N	2025-08-10	2025-08-16	\N	\N	\N	\N	1
1	2025-08-10 16:45:29.551039	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250810-164528.jpg	40.5793299	-73.9636697	407 Ocean view Ave	2025-08-10 16:49:11.737857	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250810-164909.jpg	40.579325	-73.9636708	407 Ocean view Ave	0.05	0	1.25	0.99	2025-08-10	2025-08-16	\N	\N	\N	\N	1
7	2025-08-17 14:34:21.114242	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20250817-143420.jpg	40.5793312	-73.9636763	407 Ocean view Ave	2025-08-17 17:39:24.387541	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20250817-173922.jpg	40.5793299	-73.9636731	407 Ocean view Ave	3.0833333333333335	0	77.08	67.84	2025-08-17	2025-08-23	\N	\N	\N	\N	1
10	2025-10-05 16:59:42.374821	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20251005-165940.jpg	40.5793304	-73.9636717	407 Ocean view Ave	2025-10-05 17:02:43.030944	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20251005-170241.jpg	40.5793304	-73.9636717	407 Ocean view Ave	0.05	0	1.25	1.09	2025-10-05	2025-10-11	\N	\N	\N	\N	1
8	2025-10-05 15:38:33.464693	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20251005-153831.jpg	40.5793314	-73.9636663	407 Ocean view Ave	2025-10-05 15:40:59.653655	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20251005-154057.jpg	40.5793294	-73.9636694	407 Ocean view Ave	0.03333333333333333	0	0.83	0.72	2025-10-05	2025-10-11	\N	\N	\N	\N	1
9	2025-10-05 15:54:15.596447	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20251005-155413.jpg	40.5793257	-73.9636698	407 Ocean view Ave	2025-10-05 16:56:36.833086	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20251005-165634.jpg	40.5793304	-73.9636717	407 Ocean view Ave	1.0333333333333334	0	25.83	22.53	2025-10-05	2025-10-11	\N	\N	\N	\N	1
12	2025-10-05 17:12:56.760063	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20251005-171256.jpg	40.5793291	-73.963674	407 Ocean view Ave	2025-10-05 17:17:57.634161	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20251005-171757.jpg	40.5793291	-73.963674	407 Ocean view Ave	0.08333333333333333	0	2.08	1.82	2025-10-05	2025-10-11	\N	\N	\N	\N	1
11	2025-10-05 17:08:17.867452	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-in-mishamay583%40gmail.com-20251005-170816.jpg	40.5793291	-73.963674	407 Ocean view Ave	2025-10-05 17:12:40.272576	https://s3.us-east-2.amazonaws.com/face-check-punch-in-out-photos/punch-out-mishamay583%40gmail.com-20251005-171238.jpg	40.5793291	-73.963674	407 Ocean view Ave	0.06666666666666667	0	1.67	1.46	2025-10-05	2025-10-11	\N	\N	\N	\N	1
\.


--
-- Data for Name: worker_payroll; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.worker_payroll (id, period_start, period_end, base_hourly_rate, over_time_rate, regular_hours, overtime_hours, total_hours, regular_pay, overtime_pay, gross_pay, medicare, social_security_employee, federal_withholding, ny_state_withholding, ny_local_withholding, ny_disability_withholding, ny_paid_family_leave, total_deductions, retirement401k_contribution, health_insurance_cost, has_retirement_plan, ny_unemployment_withholding, net_pay, employer_taxes_calculated, pay_stub_generated, worker_id, company_id, wc_risk_code) FROM stdin;
1	2025-08-10	2025-08-16	25.00	37.50	0.28	0	0.28	7.00	0.00	7.00	0.10	0.43	0.00	0.00	0.22	0.60	0.03	1.38	\N	\N	\N	\N	5.62	f	\N	1	1	\N
2	2025-08-17	2025-08-23	25.00	37.50	3.08	0	3.08	77.00	0.00	77.00	1.12	4.77	0.00	0.00	2.37	0.60	0.30	9.16	\N	\N	\N	\N	67.84	f	\N	1	1	\N
3	2025-09-01	\N	30.00	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	f	f	3	\N	\N
4	2025-10-05	2025-10-11	25.00	37.50	1.27	0	1.27	31.75	0.00	31.75	0.46	1.97	0.00	0.00	0.98	0.60	0.12	4.13	\N	\N	\N	\N	27.62	f	\N	1	1	\N
\.


--
-- Data for Name: worker_schedule; Type: TABLE DATA; Schema: public; Owner: misha
--

COPY public.worker_schedule (id, schedule_date, expected_start_time, expected_end_time, shift, is_on_duty, start_lunch, end_lunch, is_company_paying_lunch, worker_id, work_site_id) FROM stdin;
1	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
2	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
3	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
4	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
5	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
6	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
7	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
8	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
9	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
10	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
11	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
12	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
13	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
14	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
15	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
16	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
17	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
18	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
19	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
20	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
21	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
22	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
23	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
24	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
25	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
26	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
27	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
28	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
29	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
30	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
31	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
32	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
33	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
34	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
35	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
36	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
37	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
38	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
39	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
40	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
41	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
42	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
43	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
44	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
45	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
46	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
47	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
49	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
50	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
51	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
52	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
53	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
54	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
55	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
56	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
57	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
58	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
59	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
60	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
61	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
62	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
63	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
64	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
65	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
66	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
67	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
68	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
69	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
70	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
71	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
72	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
73	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
74	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
75	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
76	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
77	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
78	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
79	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
80	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
81	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
82	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
83	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
84	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
85	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
86	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
87	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
88	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
89	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
90	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
91	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
92	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
93	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
94	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
95	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
96	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
97	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
98	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
99	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
100	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
101	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
102	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
103	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
104	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
105	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
106	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
107	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
108	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
109	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
110	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
111	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
112	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
113	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
114	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
115	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
116	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
117	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
118	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
119	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
120	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
121	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
122	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
123	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
124	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
125	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
126	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
127	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
128	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
129	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
130	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
131	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
132	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
133	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
134	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
135	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
136	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
137	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
138	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
139	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
140	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
141	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
142	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
143	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
144	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
145	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
146	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
147	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
148	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
149	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
150	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
151	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
152	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
153	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
154	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
155	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
156	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
157	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
158	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
159	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
160	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
161	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
162	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
163	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
164	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
165	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
166	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
167	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
168	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
169	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
170	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
171	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
172	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
173	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
174	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
175	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
176	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
177	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
178	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
179	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
180	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
181	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
182	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
183	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
184	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
185	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
186	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
187	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
188	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
189	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
190	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
191	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
192	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
193	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
194	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
195	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
196	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
197	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
198	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
199	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
200	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
201	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
202	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
203	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
204	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
205	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
206	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
207	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
208	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
209	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
210	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
211	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
212	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
213	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
214	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
215	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
216	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
217	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
218	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
219	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
220	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
221	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
222	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
223	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
224	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
225	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
226	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
227	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
228	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
229	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
230	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
231	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
232	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
233	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
234	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
235	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
236	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
237	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
238	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
239	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
240	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
241	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
242	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
243	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
244	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
245	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
246	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
247	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
248	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
249	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
250	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
251	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
252	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
253	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
254	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
255	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
256	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
257	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
258	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
259	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
260	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
261	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
262	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
263	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
264	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
265	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
266	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
267	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
268	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
269	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
270	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
271	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
272	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
273	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
274	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
275	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
276	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
277	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
278	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
279	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
280	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
281	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
282	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
283	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
284	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
285	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
286	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
287	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
288	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
289	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
290	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
291	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
292	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
293	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
294	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
295	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
296	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
297	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
298	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
299	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
300	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
301	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
302	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
303	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
304	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
305	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
306	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
307	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
308	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
309	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
310	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
311	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
312	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
313	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
314	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
315	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
316	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
317	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
318	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
319	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
320	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
321	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
322	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
323	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
324	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
325	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
326	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
327	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
328	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
329	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
330	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
331	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
332	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
333	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
334	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
335	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
336	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
337	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
338	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
339	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
340	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
341	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
342	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
343	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
344	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
345	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
346	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
347	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
348	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
349	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
350	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
351	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
352	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
353	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
354	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
355	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
356	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
357	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
358	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
359	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
360	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
361	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
362	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
363	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
364	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
365	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
366	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
367	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
368	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
369	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
370	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
371	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
372	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
373	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
374	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
375	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
376	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
377	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
378	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
379	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
380	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
381	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
382	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
383	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
384	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
385	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
386	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
387	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
388	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
389	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
390	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
391	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
392	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
393	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
394	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
395	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
396	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
397	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
398	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
399	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
400	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
401	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
402	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
403	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
404	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
405	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
406	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
407	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
408	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
409	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
410	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
411	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
412	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
413	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
414	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
415	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
416	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
417	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
418	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
419	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
420	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
421	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
422	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
423	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
424	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
425	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
426	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
427	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
428	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
429	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
430	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
431	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
432	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
433	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
434	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
435	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
436	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
437	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
438	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
439	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
440	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
441	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
442	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
443	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
444	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
445	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
446	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
447	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
448	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
449	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
450	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
451	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
452	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
453	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
454	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
455	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
456	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
457	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
458	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
459	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
460	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
461	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
462	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
463	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
464	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
465	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
466	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
467	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
468	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
469	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
470	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
471	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
472	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
473	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
474	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
475	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
476	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
477	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
478	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
479	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
480	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
481	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
482	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
483	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
484	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
485	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
486	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
487	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
488	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
489	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
490	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
491	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
492	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
493	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
494	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
495	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
496	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
497	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
498	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
499	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
500	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
501	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
502	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
503	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
504	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
505	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
506	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
507	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
508	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
509	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
510	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
511	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
512	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
513	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
514	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
515	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
516	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
517	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
518	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
519	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
520	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
521	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
522	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
523	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
524	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
525	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
526	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
527	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
528	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
529	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
530	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
531	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
532	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
533	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
534	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
535	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
536	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
537	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
538	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
539	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
540	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
541	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
542	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
543	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
544	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
545	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
546	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
547	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
548	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
549	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
550	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
551	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
552	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
553	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
554	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
555	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
556	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
557	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
558	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
559	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
560	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
561	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
562	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
563	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
564	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
565	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
566	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
567	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
568	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
569	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
570	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
571	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
572	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
573	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
574	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
575	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
576	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
577	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
578	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
579	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
580	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
581	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
582	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
583	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
584	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
585	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
586	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
587	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
588	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
589	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
590	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
591	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
592	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
593	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
594	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
595	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
596	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
597	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
598	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
599	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
600	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
601	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
602	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
603	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
604	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
605	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
606	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
607	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
608	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
609	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
610	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
611	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
612	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
613	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
614	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
615	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
616	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
617	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
618	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
619	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
620	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
621	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
622	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
623	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
624	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
625	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
626	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	2	\N
627	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
628	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
629	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
630	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
631	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
632	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
633	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
634	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
635	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
636	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
637	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
638	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
639	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
640	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
641	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
642	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
643	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
644	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
645	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
646	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
647	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
648	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
649	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
650	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
651	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
652	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
653	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
654	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
655	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
656	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
657	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
658	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
659	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
660	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
661	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
662	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
663	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
664	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
665	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
666	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
667	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
668	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
669	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
670	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
671	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
672	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
673	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
674	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
675	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
676	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
677	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
678	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
679	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
680	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
681	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
682	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
683	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
684	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
685	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
686	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
687	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
688	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
689	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
690	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
691	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
692	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
693	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
694	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
695	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
696	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
697	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
698	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
699	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
700	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
701	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
702	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
703	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
704	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
705	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
706	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
707	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
708	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
709	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
710	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
711	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
712	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
713	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
714	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
715	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
716	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
717	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
718	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
719	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
720	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
721	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
722	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
723	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
724	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
725	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
726	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
727	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
728	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
729	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
730	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
731	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
732	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
733	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
734	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
735	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
736	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
737	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
738	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
739	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
740	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
741	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
742	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
743	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
744	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
745	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
746	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
747	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
748	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
749	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
750	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
751	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
752	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
753	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
754	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
755	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
756	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
757	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
758	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
759	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
760	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
761	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
762	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
763	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
764	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
765	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
766	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
767	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
768	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
769	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
770	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
771	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
772	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
773	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
774	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
775	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
776	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
777	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
778	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
779	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
780	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
781	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
782	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
783	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
784	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
785	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
786	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
787	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
788	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
789	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
790	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
791	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
792	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
793	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
794	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
795	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
796	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
797	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
798	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
799	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
800	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
801	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
802	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
803	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
804	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
805	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
806	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
807	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
808	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
809	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
810	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
811	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
812	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
813	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
814	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
815	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
816	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
817	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
818	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
819	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
820	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
821	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
822	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
823	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
824	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
825	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
826	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
827	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
828	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
829	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
830	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
831	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
832	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
833	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
834	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
835	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
836	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
837	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
838	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
839	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
840	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
841	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
842	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
843	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
844	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
845	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
846	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
847	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
848	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
849	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
850	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
851	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
852	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
853	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
854	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
855	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
856	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
857	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
858	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
859	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
860	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
861	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
862	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
863	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
864	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
865	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
866	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
867	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
868	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
869	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
870	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
871	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
872	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
873	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
874	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
875	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
876	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
877	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
878	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
879	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
880	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
881	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
882	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
883	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
884	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
885	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
886	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
887	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
888	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
889	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
890	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
891	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
892	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
893	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
894	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
895	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
896	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
897	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
898	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
899	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
900	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
901	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
902	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
903	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
904	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
905	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
906	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
907	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
908	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
909	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
910	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
911	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
912	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
913	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
914	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
915	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
916	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
917	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
918	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
919	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
920	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
921	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
922	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
923	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
924	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
925	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
926	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
927	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
928	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
929	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
930	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
931	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
932	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
933	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
934	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
935	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
936	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
937	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
938	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
939	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	3	\N
940	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
941	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
942	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
943	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
944	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
945	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
946	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
947	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
948	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
949	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
950	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
951	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
952	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
953	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
954	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
955	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
956	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
957	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
958	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
959	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
960	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
961	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
962	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
963	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
964	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
965	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
966	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
967	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
968	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
969	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
970	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
971	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
972	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
973	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
974	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
975	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
976	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
977	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
978	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
979	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
980	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
981	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
982	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
983	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
984	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
985	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
986	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
987	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
988	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
989	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
990	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
991	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
992	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
993	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
994	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
995	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
996	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
997	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
998	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
999	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1000	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1001	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1002	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1003	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1004	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1005	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1006	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1007	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1008	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1009	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1010	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1011	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1012	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1013	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1014	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1015	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1016	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1017	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1018	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1019	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1020	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1021	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1022	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1023	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1024	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1025	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1026	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1027	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1028	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1029	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1030	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1031	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1032	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1033	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1034	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1035	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1036	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1037	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1038	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1039	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1040	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1041	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1042	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1043	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1044	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1045	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1046	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1047	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1048	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1049	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1050	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1051	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1052	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1053	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1054	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1055	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1056	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1057	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1058	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1059	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1060	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1061	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1062	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1063	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1064	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1065	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1066	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1067	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1068	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1069	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1070	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1071	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1072	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1073	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1074	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1075	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1076	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1077	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1078	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1079	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1080	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1081	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1082	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1083	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1084	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1085	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1086	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1087	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1088	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1089	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1090	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1091	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1092	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1093	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1094	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1095	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1096	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1097	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1098	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1099	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1100	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1101	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1102	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1103	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1104	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1105	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1106	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1107	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1108	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1109	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1110	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1111	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1112	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1113	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1114	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1115	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1116	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1117	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1118	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1119	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1120	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1121	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1122	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1123	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1124	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1125	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1126	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1127	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1128	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1129	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1130	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1131	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1132	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1133	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1134	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1135	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1136	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1137	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1138	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1139	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1140	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1141	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1142	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1143	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1144	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1145	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1146	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1147	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1148	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1149	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1150	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1151	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1152	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1153	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1154	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1155	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1156	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1157	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1158	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1159	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1160	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1161	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1162	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1163	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1164	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1165	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1166	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1167	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1168	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1169	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1170	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1171	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1172	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1173	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1174	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1175	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1176	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1177	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1178	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1179	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1180	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1181	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1182	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1183	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1184	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1185	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1186	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1187	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1188	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1189	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1190	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1191	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1192	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1193	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1194	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1195	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1196	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1197	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1198	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1199	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1200	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1201	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1202	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1203	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1204	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1205	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1206	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1207	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1208	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1209	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1210	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1211	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1212	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1213	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1214	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1215	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1216	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1217	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1218	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1219	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1220	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1221	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1222	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1223	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1224	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1225	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1226	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1227	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1228	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1229	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1230	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1231	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1232	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1233	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1234	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1235	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1236	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1237	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1238	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1239	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1240	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1241	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1242	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1243	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1244	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1245	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1246	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1247	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1248	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1249	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1250	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1251	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1252	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	4	\N
1253	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1254	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1255	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1256	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1257	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1258	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1259	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1260	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1261	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1262	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1263	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1264	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1265	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1266	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1267	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1268	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1269	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1270	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1271	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1272	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1273	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1274	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1275	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1276	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1277	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1278	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1279	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1280	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1281	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1282	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1283	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1284	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1285	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1286	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1287	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1288	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1289	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1290	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1291	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1292	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1293	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1294	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1295	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1296	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1297	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1298	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1299	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1300	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1301	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1302	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1303	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1304	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1305	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1306	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1307	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1308	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1309	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1310	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1311	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1312	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1313	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1314	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1315	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1316	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1317	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1318	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1319	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1320	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1321	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1322	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1323	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1324	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1325	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1326	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1327	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1328	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1329	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1330	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1331	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1332	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1333	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1334	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1335	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1336	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1337	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1338	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1339	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1340	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1341	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1342	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1343	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1344	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1345	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1346	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1347	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1348	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1349	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1350	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1351	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1352	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1353	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1354	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1355	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1356	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1357	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1358	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1359	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1360	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1361	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1362	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1363	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1364	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1365	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1366	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1367	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1368	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1369	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1370	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1371	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1372	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1373	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1374	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1375	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1376	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1377	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1378	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1379	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1380	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1381	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1382	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1383	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1384	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1385	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1386	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1387	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1388	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1389	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1390	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1391	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1392	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1393	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1394	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1395	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1396	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1397	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1398	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1399	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1400	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1401	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1402	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1403	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1404	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1405	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1406	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1407	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1408	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1409	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1410	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1411	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1412	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1413	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1414	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1415	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1416	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1417	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1418	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1419	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1420	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1421	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1422	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1423	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1424	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1425	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1426	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1427	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1428	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1429	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1430	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1431	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1432	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1433	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1434	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1435	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1436	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1437	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1438	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1439	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1440	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1441	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1442	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1443	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1444	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1445	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1446	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1447	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1448	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1449	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1450	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1451	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1452	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1453	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1454	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1455	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1456	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1457	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1458	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1459	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1460	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1461	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1462	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1463	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1464	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1465	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1466	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1467	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1468	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1469	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1470	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1471	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1472	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1473	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1474	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1475	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1476	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1477	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1478	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1479	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1480	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1481	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1482	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1483	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1484	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1485	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1486	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1487	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1488	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1489	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1490	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1491	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1492	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1493	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1494	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1495	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1496	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1497	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1498	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1499	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1500	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1501	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1502	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1503	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1504	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1505	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1506	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1507	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1508	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1509	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1510	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1511	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1512	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1513	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1514	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1515	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1516	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1517	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1518	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1519	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1520	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1521	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1522	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1523	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1524	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1525	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1526	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1527	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1528	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1529	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1530	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1531	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1532	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1533	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1534	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1535	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1536	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1537	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1538	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1539	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1540	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1541	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1542	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1543	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1544	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1545	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1546	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1547	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1548	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1549	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1550	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1551	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1552	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1553	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1554	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1555	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1556	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1557	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1558	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1559	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1560	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1561	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1562	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1563	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1564	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1565	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	5	\N
1566	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1567	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1568	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1569	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1570	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1571	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1572	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1573	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1574	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1575	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1576	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1577	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1578	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1579	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1580	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1581	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1582	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1583	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1584	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1585	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1586	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1587	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1588	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1589	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1590	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1591	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1592	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1593	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1594	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1595	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1596	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1597	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1598	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1599	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1600	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1601	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1602	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1603	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1604	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1605	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1606	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1607	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1608	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1609	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1610	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1611	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1612	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1613	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1614	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1615	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1616	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1617	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1618	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1619	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1620	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1621	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1622	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1623	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1624	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1625	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1626	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1627	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1628	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1629	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1630	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1631	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1632	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1633	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1634	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1635	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1636	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1637	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1638	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1639	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1640	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1641	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1642	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1643	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1644	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1645	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1646	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1647	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1648	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1649	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1650	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1651	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1652	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1653	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1654	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1655	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1656	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1657	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1658	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1659	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1660	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1661	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1662	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1663	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1664	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1665	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1666	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1667	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1668	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1669	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1670	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1671	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1672	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1673	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1674	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1675	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1676	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1677	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1678	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1679	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1680	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1681	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1682	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1683	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1684	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1685	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1686	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1687	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1688	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1689	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1690	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1691	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1692	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1693	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1694	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1695	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1696	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1697	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1698	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1699	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1700	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1701	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1702	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1703	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1704	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1705	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1706	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1707	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1708	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1709	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1710	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1711	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1712	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1713	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1714	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1715	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1716	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1717	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1718	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1719	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1720	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1721	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1722	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1723	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1724	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1725	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1726	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1727	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1728	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1729	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1730	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1731	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1732	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1733	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1734	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1735	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1736	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1737	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1738	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1739	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1740	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1741	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1742	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1743	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1744	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1745	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1746	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1747	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1748	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1749	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1750	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1751	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1752	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1753	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1754	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1755	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1756	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1757	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1758	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1759	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1760	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1761	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1762	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1763	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1764	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1765	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1766	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1767	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1768	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1769	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1770	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1771	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1772	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1773	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1774	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1775	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1776	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1777	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1778	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1779	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1780	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1781	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1782	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1783	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1784	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1785	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1786	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1787	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1788	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1789	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1790	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1791	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1792	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1793	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1794	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1795	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1796	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1797	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1798	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1799	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1800	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1801	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1802	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1803	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1804	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1805	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1806	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1807	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1808	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1809	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1810	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1811	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1812	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1813	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1814	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1815	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1816	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1817	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1818	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1819	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1820	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1821	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1822	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1823	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1824	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1825	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1826	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1827	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1828	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1829	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1830	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1831	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1832	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1833	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1834	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1835	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1836	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1837	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1838	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1839	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1840	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1841	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1842	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1843	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1844	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1845	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1846	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1847	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1848	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1849	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1850	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1851	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1852	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1853	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1854	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1855	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1856	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1857	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1858	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1859	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1860	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1861	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1862	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1863	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1864	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1865	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1866	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1867	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1868	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1869	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1870	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1871	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1872	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1873	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1874	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1875	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1876	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1877	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1878	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	6	\N
1879	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1880	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1881	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1882	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1883	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1884	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1885	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1886	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1887	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1888	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1889	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1890	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1891	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1892	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1893	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1894	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1895	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1896	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1897	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1898	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1899	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1900	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1901	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1902	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1903	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1904	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1905	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1906	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1907	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1908	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1909	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1910	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1911	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1912	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1913	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1914	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1915	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1916	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1917	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1918	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1919	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1920	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1921	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1922	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1923	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1924	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1925	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1926	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1927	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1928	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1929	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1930	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1931	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1932	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1933	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1934	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1935	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1936	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1937	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1938	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1939	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1940	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1941	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1942	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1943	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1944	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1945	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1946	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1947	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1948	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1949	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1950	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1951	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1952	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1953	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1954	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1955	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1956	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1957	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1958	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1959	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1960	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1961	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1962	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1963	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1964	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1965	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1966	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1967	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1968	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1969	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1970	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1971	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1972	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1973	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1974	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1975	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1976	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1977	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1978	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1979	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1980	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1981	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1982	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1983	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1984	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1985	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1986	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1987	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1988	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1989	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1990	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1991	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1992	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1993	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1994	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1995	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1996	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1997	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1998	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
1999	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2000	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2001	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2002	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2003	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2004	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2005	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2006	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2007	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2008	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2009	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2010	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2011	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2012	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2013	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2014	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2015	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2016	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2017	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2018	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2019	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2020	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2021	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2022	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2023	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2024	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2025	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2026	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2027	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2028	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2029	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2030	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2031	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2032	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2033	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2034	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2035	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2036	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2037	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2038	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2039	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2040	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2041	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2042	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2043	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2044	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2045	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2046	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2047	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2048	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2049	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2050	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2051	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2052	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2053	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2054	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2055	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2056	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2057	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2058	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2059	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2060	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2061	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2062	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2063	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2064	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2065	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2066	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2067	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2068	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2069	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2070	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2071	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2072	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2073	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2074	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2075	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2076	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2077	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2078	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2079	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2080	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2081	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2082	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2083	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2084	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2085	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2086	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2087	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2088	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2089	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2090	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2091	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2092	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2093	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2094	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2095	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2096	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2097	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2098	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2099	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2100	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2101	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2102	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2103	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2104	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2105	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2106	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2107	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2108	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2109	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2110	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2111	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2112	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2113	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2114	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2115	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2116	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2117	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2118	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2119	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2120	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2121	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2122	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2123	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2124	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2125	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2126	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2127	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2128	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2129	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2130	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2131	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2132	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2133	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2134	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2135	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2136	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2137	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2138	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2139	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2140	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2141	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2142	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2143	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2144	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2145	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2146	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2147	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2148	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2149	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2150	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2151	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2152	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2153	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2154	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2155	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2156	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2157	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2158	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2159	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2160	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2161	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2162	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2163	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2164	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2165	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2166	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2167	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2168	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2169	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2170	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2171	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2172	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2173	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2174	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2175	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2176	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2177	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2178	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2179	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2180	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2181	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2182	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2183	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2184	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2185	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2186	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2187	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2188	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2189	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2190	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2191	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	7	\N
2192	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2193	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2194	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2195	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2196	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2197	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2198	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2199	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2200	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2201	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2202	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2203	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2204	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2205	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2206	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2207	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2208	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2209	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2210	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2211	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2212	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2213	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2214	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2215	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2216	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2217	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2218	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2219	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2220	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2221	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2222	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2223	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2224	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2225	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2226	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2227	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2228	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2229	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2230	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2231	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2232	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2233	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2234	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2235	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2236	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2237	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2238	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2239	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2240	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2241	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2242	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2243	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2244	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2245	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2246	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2247	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2248	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2249	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2250	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2251	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2252	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2253	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2254	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2255	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2256	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2257	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2258	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2259	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2260	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2261	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2262	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2263	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2264	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2265	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2266	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2267	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2268	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2269	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2270	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2271	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2272	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2273	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2274	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2275	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2276	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2277	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2278	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2279	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2280	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2281	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2282	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2283	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2284	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2285	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2286	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2287	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2288	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2289	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2290	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2291	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2292	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2293	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2294	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2295	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2296	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2297	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2298	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2299	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2300	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2301	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2302	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2303	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2304	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2305	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2306	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2307	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2308	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2309	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2310	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2311	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2312	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2313	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2314	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2315	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2316	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2317	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2318	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2319	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2320	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2321	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2322	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2323	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2324	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2325	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2326	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2327	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2328	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2329	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2330	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2331	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2332	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2333	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2334	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2335	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2336	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2337	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2338	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2339	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2340	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2341	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2342	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2343	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2344	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2345	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2346	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2347	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2348	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2349	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2350	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2351	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2352	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2353	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2354	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2355	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2356	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2357	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2358	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2359	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2360	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2361	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2362	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2363	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2364	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2365	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2366	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2367	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2368	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2369	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2370	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2371	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2372	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2373	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2374	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2375	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2376	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2377	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2378	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2379	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2380	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2381	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2382	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2383	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2384	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2385	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2386	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2387	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2388	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2389	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2390	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2391	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2392	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2393	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2394	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2395	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2396	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2397	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2398	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2399	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2400	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2401	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2402	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2403	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2404	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2405	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2406	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2407	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2408	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2409	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2410	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2411	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2412	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2413	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2414	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2415	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2416	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2417	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2418	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2419	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2420	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2421	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2422	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2423	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2424	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2425	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2426	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2427	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2428	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2429	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2430	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2431	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2432	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2433	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2434	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2435	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2436	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2437	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2438	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2439	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2440	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2441	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2442	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2443	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2444	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2445	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2446	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2447	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2448	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2449	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2450	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2451	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2452	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2453	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2454	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2455	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2456	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2457	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2458	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2459	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2460	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2461	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2462	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2463	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2464	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2465	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2466	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2467	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2468	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2469	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2470	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2471	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2472	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2473	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2474	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2475	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2476	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2477	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2478	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2479	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2480	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2481	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2482	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2483	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2484	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2485	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2486	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2487	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2488	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2489	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2490	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2491	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2492	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2493	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2494	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2495	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2496	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2497	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2498	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2499	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2500	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2501	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2502	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2503	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2504	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	8	\N
2505	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2506	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2507	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2508	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2509	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2510	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2511	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2512	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2513	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2514	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2515	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2516	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2517	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2518	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2519	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2520	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2521	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2522	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2523	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2524	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2525	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2526	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2527	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2528	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2529	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2530	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2531	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2532	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2533	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2534	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2535	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2536	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2537	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2538	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2539	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2540	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2541	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2542	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2543	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2544	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2545	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2546	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2547	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2548	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2549	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2550	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2551	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2552	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2553	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2554	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2555	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2556	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2557	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2558	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2559	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2560	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2561	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2562	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2563	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2564	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2565	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2566	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2567	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2568	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2569	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2570	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2571	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2572	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2573	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2574	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2575	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2576	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2577	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2578	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2579	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2580	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2581	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2582	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2583	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2584	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2585	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2586	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2587	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2588	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2589	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2590	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2591	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2592	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2593	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2594	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2595	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2596	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2597	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2598	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2599	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2600	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2601	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2602	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2603	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2604	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2605	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2606	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2607	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2608	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2609	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2610	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2611	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2612	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2613	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2614	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2615	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2616	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2617	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2618	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2619	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2620	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2621	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2622	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2623	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2624	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2625	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2626	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2627	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2628	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2629	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2630	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2631	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2632	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2633	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2634	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2635	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2636	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2637	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2638	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2639	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2640	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2641	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2642	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2643	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2644	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2645	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2646	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2647	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2648	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2649	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2650	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2651	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2652	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2653	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2654	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2655	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2656	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2657	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2658	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2659	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2660	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2661	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2662	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2663	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2664	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2665	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2666	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2667	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2668	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2669	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2670	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2671	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2672	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2673	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2674	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2675	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2676	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2677	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2678	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2679	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2680	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2681	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2682	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2683	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2684	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2685	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2686	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2687	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2688	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2689	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2690	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2691	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2692	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2693	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2694	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2695	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2696	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2697	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2698	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2699	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2700	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2701	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2702	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2703	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2704	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2705	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2706	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2707	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2708	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2709	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2710	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2711	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2712	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2713	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2714	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2715	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2716	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2717	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2718	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2719	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2720	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2721	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2722	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2723	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2724	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2725	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2726	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2727	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2728	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2729	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2730	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2731	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2732	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2733	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2734	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2735	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2736	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2737	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2738	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2739	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2740	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2741	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2742	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2743	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2744	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2745	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2746	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2747	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2748	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2749	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2750	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2751	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2752	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2753	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2754	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2755	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2756	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2757	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2758	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2759	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2760	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2761	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2762	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2763	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2764	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2765	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2766	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2767	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2768	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2769	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2770	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2771	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2772	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2773	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2774	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2775	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2776	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2777	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2778	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2779	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2780	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2781	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2782	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2783	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2784	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2785	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2786	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2787	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2788	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2789	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2790	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2791	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2792	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2793	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2794	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2795	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2796	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2797	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2798	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2799	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2800	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2801	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2802	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2803	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2804	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2805	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2806	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2807	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2808	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2809	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2810	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2811	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2812	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2813	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2814	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2815	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2816	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2817	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	9	\N
2818	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2819	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2820	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2821	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2822	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2823	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2824	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2825	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2826	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2827	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2828	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2829	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2830	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2831	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2832	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2833	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2834	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2835	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2836	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2837	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2838	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2839	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2840	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2841	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2842	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2843	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2844	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2845	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2846	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2847	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2848	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2849	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2850	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2851	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2852	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2853	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2854	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2855	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2856	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2857	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2858	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2859	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2860	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2861	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2862	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2863	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2864	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2865	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2866	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2867	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2868	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2869	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2870	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2871	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2872	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2873	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2874	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2875	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2876	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2877	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2878	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2879	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2880	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2881	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2882	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2883	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2884	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2885	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2886	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2887	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2888	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2889	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2890	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2891	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2892	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2893	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2894	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2895	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2896	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2897	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2898	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2899	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2900	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2901	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2902	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2903	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2904	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2905	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2906	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2907	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2908	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2909	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2910	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2911	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2912	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2913	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2914	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2915	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2916	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2917	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2918	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2919	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2920	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2921	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2922	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2923	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2924	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2925	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2926	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2927	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2928	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2929	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2930	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2931	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2932	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2933	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2934	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2935	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2936	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2937	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2938	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2939	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2940	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2941	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2942	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2943	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2944	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2945	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2946	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2947	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2948	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2949	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2950	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2951	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2952	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2953	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2954	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2955	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2956	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2957	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2958	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2959	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2960	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2961	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2962	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2963	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2964	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2965	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2966	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2967	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2968	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2969	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2970	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2971	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2972	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2973	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2974	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2975	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2976	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2977	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2978	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2979	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2980	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2981	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2982	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2983	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2984	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2985	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2986	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2987	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2988	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2989	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2990	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2991	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2992	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2993	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2994	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2995	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2996	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2997	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2998	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
2999	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3000	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3001	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3002	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3003	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3004	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3005	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3006	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3007	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3008	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3009	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3010	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3011	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3012	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3013	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3014	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3015	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3016	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3017	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3018	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3019	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3020	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3021	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3022	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3023	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3024	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3025	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3026	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3027	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3028	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3029	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3030	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3031	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3032	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3033	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3034	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3035	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3036	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3037	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3038	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3039	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3040	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3041	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3042	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3043	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3044	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3045	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3046	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3047	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3048	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3049	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3050	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3051	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3052	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3053	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3054	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3055	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3056	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3057	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3058	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3059	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3060	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3061	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3062	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3063	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3064	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3065	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3066	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3067	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3068	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3069	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3070	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3071	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3072	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3073	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3074	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3075	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3076	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3077	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3078	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3079	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3080	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3081	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3082	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3083	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3084	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3085	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3086	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3087	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3088	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3089	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3090	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3091	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3092	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3093	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3094	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3095	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3096	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3097	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3098	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3099	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3100	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3101	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3102	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3103	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3104	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3105	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3106	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3107	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3108	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3109	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3110	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3111	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3112	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3113	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3114	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3115	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3116	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3117	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3118	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3119	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3120	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3121	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3122	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3123	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3124	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3125	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3126	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3127	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3128	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3129	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3130	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	10	\N
3131	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3132	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3133	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3134	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3135	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3136	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3137	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3138	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3139	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3140	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3141	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3142	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3143	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3144	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3145	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3146	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3147	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3148	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3149	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3150	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3151	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3152	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3153	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3154	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3155	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3156	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3157	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3158	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3159	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3160	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3161	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3162	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3163	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3164	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3165	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3166	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3167	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3168	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3169	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3170	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3171	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3172	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3173	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3174	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3175	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3176	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3177	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3178	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3179	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3180	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3181	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3182	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3183	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3184	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3185	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3186	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3187	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3188	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3189	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3190	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3191	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3192	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3193	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3194	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3195	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3196	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3197	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3198	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3199	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3200	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3201	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3202	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3203	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3204	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3205	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3206	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3207	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3208	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3209	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3210	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3211	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3212	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3213	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3214	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3215	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3216	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3217	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3218	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3219	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3220	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3221	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3222	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3223	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3224	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3225	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3226	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3227	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3228	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3229	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3230	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3231	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3232	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3233	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3234	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3235	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3236	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3237	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3238	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3239	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3240	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3241	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3242	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3243	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3244	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3245	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3246	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3247	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3248	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3249	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3250	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3251	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3252	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3253	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3254	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3255	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3256	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3257	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3258	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3259	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3260	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3261	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3262	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3263	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3264	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3265	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3266	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3267	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3268	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3269	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3270	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3271	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3272	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3273	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3274	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3275	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3276	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3277	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3278	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3279	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3280	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3281	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3282	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3283	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3284	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3285	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3286	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3287	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3288	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3289	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3290	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3291	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3292	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3293	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3294	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3295	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3296	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3297	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3298	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3299	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3300	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3301	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3302	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3303	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3304	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3305	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3306	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3307	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3308	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3309	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3310	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3311	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3312	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3313	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3314	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3315	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3316	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3317	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3318	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3319	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3320	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3321	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3322	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3323	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3324	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3325	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3326	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3327	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3328	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3329	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3330	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3331	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3332	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3333	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3334	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3335	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3336	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3337	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3338	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3339	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3340	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3341	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3342	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3343	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3344	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3345	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3346	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3347	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3348	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3349	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3350	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3351	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3352	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3353	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3354	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3355	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3356	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3357	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3358	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3359	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3360	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3361	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3362	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3363	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3364	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3365	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3366	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3367	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3368	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3369	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3370	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3371	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3372	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3373	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3374	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3375	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3376	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3377	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3378	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3379	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3380	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3381	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3382	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3383	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3384	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3385	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3386	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3387	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3388	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3389	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3390	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3391	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3392	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3393	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3394	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3395	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3396	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3397	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3398	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3399	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3400	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3401	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3402	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3403	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3404	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3405	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3406	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3407	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3408	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3409	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3410	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3411	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3412	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3413	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3414	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3415	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3416	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3417	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3418	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3419	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3420	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3421	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3422	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3423	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3424	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3425	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3426	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3427	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3428	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3429	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3430	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3431	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3432	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3433	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3434	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3435	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3436	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3437	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3438	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3439	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3440	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3441	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3442	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3443	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	11	\N
3444	2025-08-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3445	2025-08-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3446	2025-08-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3447	2025-08-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3448	2025-08-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3449	2025-08-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3450	2025-08-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3451	2025-08-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3452	2025-08-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3453	2025-08-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3454	2025-08-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3455	2025-08-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3456	2025-08-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3457	2025-08-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3458	2025-08-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3459	2025-08-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3460	2025-08-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3461	2025-08-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3462	2025-08-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3463	2025-09-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3464	2025-09-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3465	2025-09-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3466	2025-09-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3467	2025-09-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3468	2025-09-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3469	2025-09-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3470	2025-09-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3471	2025-09-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3472	2025-09-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3473	2025-09-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3474	2025-09-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3475	2025-09-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3476	2025-09-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3477	2025-09-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3478	2025-09-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3479	2025-09-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3480	2025-09-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3481	2025-09-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3482	2025-09-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3483	2025-09-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3484	2025-09-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3485	2025-09-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3486	2025-09-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3487	2025-09-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3488	2025-09-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3489	2025-10-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3490	2025-10-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3491	2025-10-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3492	2025-10-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3493	2025-10-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3494	2025-10-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3495	2025-10-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3496	2025-10-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3497	2025-10-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3498	2025-10-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3499	2025-10-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3500	2025-10-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3501	2025-10-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3502	2025-10-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3503	2025-10-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3504	2025-10-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3505	2025-10-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3506	2025-10-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3507	2025-10-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3508	2025-10-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3509	2025-10-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3510	2025-10-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3511	2025-10-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3512	2025-10-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3513	2025-10-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3514	2025-10-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3515	2025-10-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3516	2025-11-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3517	2025-11-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3518	2025-11-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3519	2025-11-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3520	2025-11-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3521	2025-11-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3522	2025-11-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3523	2025-11-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3524	2025-11-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3525	2025-11-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3526	2025-11-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3527	2025-11-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3528	2025-11-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3529	2025-11-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3530	2025-11-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3531	2025-11-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3532	2025-11-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3533	2025-11-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3534	2025-11-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3535	2025-11-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3536	2025-11-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3537	2025-11-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3538	2025-11-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3539	2025-11-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3540	2025-11-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3541	2025-12-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3542	2025-12-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3543	2025-12-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3544	2025-12-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3545	2025-12-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3546	2025-12-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3547	2025-12-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3548	2025-12-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3549	2025-12-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3550	2025-12-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3551	2025-12-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3552	2025-12-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3553	2025-12-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3554	2025-12-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3555	2025-12-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3556	2025-12-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3557	2025-12-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3558	2025-12-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3559	2025-12-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3560	2025-12-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3561	2025-12-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3562	2025-12-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3563	2025-12-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3564	2025-12-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3565	2025-12-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3566	2025-12-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3567	2025-12-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3568	2026-01-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3569	2026-01-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3570	2026-01-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3571	2026-01-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3572	2026-01-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3573	2026-01-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3574	2026-01-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3575	2026-01-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3576	2026-01-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3577	2026-01-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3578	2026-01-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3579	2026-01-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3580	2026-01-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3581	2026-01-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3582	2026-01-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3583	2026-01-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3584	2026-01-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3585	2026-01-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3586	2026-01-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3587	2026-01-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3588	2026-01-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3589	2026-01-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3590	2026-01-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3591	2026-01-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3592	2026-01-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3593	2026-01-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3594	2026-02-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3595	2026-02-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3596	2026-02-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3597	2026-02-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3598	2026-02-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3599	2026-02-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3600	2026-02-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3601	2026-02-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3602	2026-02-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3603	2026-02-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3604	2026-02-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3605	2026-02-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3606	2026-02-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3607	2026-02-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3608	2026-02-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3609	2026-02-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3610	2026-02-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3611	2026-02-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3612	2026-02-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3613	2026-02-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3614	2026-02-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3615	2026-02-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3616	2026-02-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3617	2026-02-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3618	2026-03-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3619	2026-03-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3620	2026-03-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3621	2026-03-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3622	2026-03-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3623	2026-03-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3624	2026-03-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3625	2026-03-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3626	2026-03-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3627	2026-03-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3628	2026-03-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3629	2026-03-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3630	2026-03-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3631	2026-03-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3632	2026-03-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3633	2026-03-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3634	2026-03-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3635	2026-03-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3636	2026-03-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3637	2026-03-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3638	2026-03-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3639	2026-03-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3640	2026-03-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3641	2026-03-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3642	2026-03-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3643	2026-03-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3644	2026-03-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3645	2026-04-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3646	2026-04-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3647	2026-04-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3648	2026-04-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3649	2026-04-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3650	2026-04-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3651	2026-04-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3652	2026-04-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3653	2026-04-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3654	2026-04-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3655	2026-04-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3656	2026-04-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3657	2026-04-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3658	2026-04-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3659	2026-04-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3660	2026-04-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3661	2026-04-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3662	2026-04-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3663	2026-04-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3664	2026-04-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3665	2026-04-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3666	2026-04-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3667	2026-04-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3668	2026-04-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3669	2026-04-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3670	2026-04-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3671	2026-05-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3672	2026-05-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3673	2026-05-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3674	2026-05-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3675	2026-05-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3676	2026-05-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3677	2026-05-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3678	2026-05-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3679	2026-05-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3680	2026-05-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3681	2026-05-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3682	2026-05-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3683	2026-05-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3684	2026-05-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3685	2026-05-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3686	2026-05-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3687	2026-05-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3688	2026-05-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3689	2026-05-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3690	2026-05-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3691	2026-05-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3692	2026-05-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3693	2026-05-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3694	2026-05-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3695	2026-05-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3696	2026-05-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3697	2026-06-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3698	2026-06-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3699	2026-06-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3700	2026-06-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3701	2026-06-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3702	2026-06-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3703	2026-06-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3704	2026-06-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3705	2026-06-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3706	2026-06-11	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3707	2026-06-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3708	2026-06-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3709	2026-06-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3710	2026-06-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3711	2026-06-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3712	2026-06-18	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3713	2026-06-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3714	2026-06-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3715	2026-06-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3716	2026-06-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3717	2026-06-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3718	2026-06-25	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3719	2026-06-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3720	2026-06-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3721	2026-06-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3722	2026-06-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3723	2026-07-01	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3724	2026-07-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3725	2026-07-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3726	2026-07-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3727	2026-07-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3728	2026-07-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3729	2026-07-08	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3730	2026-07-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3731	2026-07-10	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3732	2026-07-12	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3733	2026-07-13	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3734	2026-07-14	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3735	2026-07-15	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3736	2026-07-16	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3737	2026-07-17	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3738	2026-07-19	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3739	2026-07-20	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3740	2026-07-21	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3741	2026-07-22	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3742	2026-07-23	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3743	2026-07-24	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3744	2026-07-26	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3745	2026-07-27	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3746	2026-07-28	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3747	2026-07-29	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3748	2026-07-30	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3749	2026-07-31	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3750	2026-08-02	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3751	2026-08-03	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3752	2026-08-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3753	2026-08-05	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3754	2026-08-06	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3755	2026-08-07	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
3756	2026-08-09	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	12	\N
48	2025-10-04	07:30:00	20:30:00	DAY	f	2025-08-10 12:00:00	2025-08-10 12:45:00	t	1	\N
3757	2025-10-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3758	2025-10-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3759	2025-10-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3760	2025-10-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3761	2025-10-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3762	2025-10-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3763	2025-10-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3764	2025-10-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3765	2025-10-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3766	2025-10-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3767	2025-10-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3768	2025-10-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3769	2025-10-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3770	2025-10-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3771	2025-10-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3772	2025-10-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3773	2025-10-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3774	2025-10-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3775	2025-10-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3776	2025-10-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3777	2025-10-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3778	2025-10-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3779	2025-10-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3780	2025-10-31	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3781	2025-11-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3782	2025-11-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3783	2025-11-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3784	2025-11-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3785	2025-11-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3786	2025-11-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3787	2025-11-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3788	2025-11-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3789	2025-11-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3790	2025-11-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3791	2025-11-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3792	2025-11-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3793	2025-11-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3794	2025-11-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3795	2025-11-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3796	2025-11-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3797	2025-11-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3798	2025-11-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3799	2025-11-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3800	2025-11-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3801	2025-11-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3802	2025-11-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3803	2025-11-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3804	2025-11-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3805	2025-11-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3806	2025-12-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3807	2025-12-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3808	2025-12-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3809	2025-12-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3810	2025-12-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3811	2025-12-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3812	2025-12-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3813	2025-12-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3814	2025-12-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3815	2025-12-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3816	2025-12-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3817	2025-12-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3818	2025-12-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3819	2025-12-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3820	2025-12-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3821	2025-12-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3822	2025-12-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3823	2025-12-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3824	2025-12-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3825	2025-12-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3826	2025-12-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3827	2025-12-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3828	2025-12-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3829	2025-12-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3830	2025-12-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3831	2025-12-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3832	2025-12-31	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3833	2026-01-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3834	2026-01-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3835	2026-01-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3836	2026-01-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3837	2026-01-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3838	2026-01-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3839	2026-01-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3840	2026-01-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3841	2026-01-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3842	2026-01-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3843	2026-01-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3844	2026-01-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3845	2026-01-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3846	2026-01-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3847	2026-01-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3848	2026-01-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3849	2026-01-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3850	2026-01-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3851	2026-01-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3852	2026-01-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3853	2026-01-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3854	2026-01-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3855	2026-01-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3856	2026-01-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3857	2026-01-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3858	2026-01-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3859	2026-02-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3860	2026-02-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3861	2026-02-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3862	2026-02-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3863	2026-02-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3864	2026-02-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3865	2026-02-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3866	2026-02-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3867	2026-02-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3868	2026-02-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3869	2026-02-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3870	2026-02-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3871	2026-02-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3872	2026-02-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3873	2026-02-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3874	2026-02-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3875	2026-02-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3876	2026-02-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3877	2026-02-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3878	2026-02-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3879	2026-02-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3880	2026-02-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3881	2026-02-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3882	2026-02-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3883	2026-03-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3884	2026-03-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3885	2026-03-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3886	2026-03-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3887	2026-03-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3888	2026-03-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3889	2026-03-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3890	2026-03-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3891	2026-03-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3892	2026-03-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3893	2026-03-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3894	2026-03-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3895	2026-03-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3896	2026-03-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3897	2026-03-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3898	2026-03-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3899	2026-03-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3900	2026-03-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3901	2026-03-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3902	2026-03-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3903	2026-03-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3904	2026-03-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3905	2026-03-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3906	2026-03-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3907	2026-03-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3908	2026-03-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3909	2026-03-31	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3910	2026-04-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3911	2026-04-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3912	2026-04-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3913	2026-04-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3914	2026-04-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3915	2026-04-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3916	2026-04-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3917	2026-04-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3918	2026-04-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3919	2026-04-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3920	2026-04-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3921	2026-04-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3922	2026-04-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3923	2026-04-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3924	2026-04-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3925	2026-04-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3926	2026-04-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3927	2026-04-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3928	2026-04-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3929	2026-04-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3930	2026-04-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3931	2026-04-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3932	2026-04-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3933	2026-04-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3934	2026-04-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3935	2026-04-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3936	2026-05-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3937	2026-05-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3938	2026-05-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3939	2026-05-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3940	2026-05-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3941	2026-05-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3942	2026-05-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3943	2026-05-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3944	2026-05-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3945	2026-05-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3946	2026-05-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3947	2026-05-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3948	2026-05-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3949	2026-05-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3950	2026-05-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3951	2026-05-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3952	2026-05-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3953	2026-05-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3954	2026-05-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3955	2026-05-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3956	2026-05-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3957	2026-05-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3958	2026-05-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3959	2026-05-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3960	2026-05-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3961	2026-05-31	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3962	2026-06-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3963	2026-06-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3964	2026-06-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3965	2026-06-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3966	2026-06-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3967	2026-06-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3968	2026-06-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3969	2026-06-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3970	2026-06-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3971	2026-06-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3972	2026-06-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3973	2026-06-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3974	2026-06-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3975	2026-06-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3976	2026-06-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3977	2026-06-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3978	2026-06-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3979	2026-06-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3980	2026-06-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3981	2026-06-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3982	2026-06-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3983	2026-06-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3984	2026-06-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3985	2026-06-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3986	2026-06-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3987	2026-06-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3988	2026-07-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3989	2026-07-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3990	2026-07-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3991	2026-07-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3992	2026-07-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3993	2026-07-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3994	2026-07-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3995	2026-07-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3996	2026-07-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3997	2026-07-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3998	2026-07-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
3999	2026-07-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4000	2026-07-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4001	2026-07-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4002	2026-07-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4003	2026-07-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4004	2026-07-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4005	2026-07-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4006	2026-07-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4007	2026-07-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4008	2026-07-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4009	2026-07-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4010	2026-07-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4011	2026-07-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4012	2026-07-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4013	2026-07-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4014	2026-07-31	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4015	2026-08-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4016	2026-08-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4017	2026-08-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4018	2026-08-05	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4019	2026-08-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4020	2026-08-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4021	2026-08-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4022	2026-08-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4023	2026-08-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4024	2026-08-12	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4025	2026-08-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4026	2026-08-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4027	2026-08-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4028	2026-08-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4029	2026-08-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4030	2026-08-19	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4031	2026-08-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4032	2026-08-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4033	2026-08-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4034	2026-08-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4035	2026-08-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4036	2026-08-26	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4037	2026-08-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4038	2026-08-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4039	2026-08-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4040	2026-08-31	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4041	2026-09-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4042	2026-09-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4043	2026-09-03	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4044	2026-09-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4045	2026-09-06	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4046	2026-09-07	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4047	2026-09-08	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4048	2026-09-09	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4049	2026-09-10	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4050	2026-09-11	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4051	2026-09-13	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4052	2026-09-14	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4053	2026-09-15	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4054	2026-09-16	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4055	2026-09-17	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4056	2026-09-18	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4057	2026-09-20	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4058	2026-09-21	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4059	2026-09-22	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4060	2026-09-23	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4061	2026-09-24	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4062	2026-09-25	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4063	2026-09-27	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4064	2026-09-28	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4065	2026-09-29	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4066	2026-09-30	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4067	2026-10-01	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4068	2026-10-02	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
4069	2026-10-04	07:30:00	16:30:00	DAY	f	2025-10-10 12:00:00	2025-10-10 12:45:00	t	4	\N
\.


--
-- Name: _company_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public._company_id_seq', 2, true);


--
-- Name: _dependents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public._dependents_id_seq', 1, false);


--
-- Name: _role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public._role_id_seq', 4, true);


--
-- Name: _user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public._user_id_seq', 12, true);


--
-- Name: contact_sales_form_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.contact_sales_form_id_seq', 2, true);


--
-- Name: documents_i9_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.documents_i9_id_seq', 1, false);


--
-- Name: employer_tax_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.employer_tax_record_id_seq', 1, false);


--
-- Name: location_record_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.location_record_id_seq', 36, true);


--
-- Name: notification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.notification_id_seq', 26, true);


--
-- Name: payment_history_irs_payment_history_irs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.payment_history_irs_payment_history_irs_id_seq', 2, true);


--
-- Name: scheduler_execution_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.scheduler_execution_history_id_seq', 41, true);


--
-- Name: terms_of_use_agreement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.terms_of_use_agreement_id_seq', 1, true);


--
-- Name: token_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.token_id_seq', 3, true);


--
-- Name: work_site_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.work_site_id_seq', 3, true);


--
-- Name: worker_attendance_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.worker_attendance_id_seq', 12, true);


--
-- Name: worker_payroll_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.worker_payroll_id_seq', 4, true);


--
-- Name: worker_schedule_id_seq; Type: SEQUENCE SET; Schema: public; Owner: misha
--

SELECT pg_catalog.setval('public.worker_schedule_id_seq', 4069, true);


--
-- Name: _company _company_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._company
    ADD CONSTRAINT _company_pkey PRIMARY KEY (id);


--
-- Name: _dependents _dependents_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._dependents
    ADD CONSTRAINT _dependents_pkey PRIMARY KEY (id);


--
-- Name: _role _role_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._role
    ADD CONSTRAINT _role_pkey PRIMARY KEY (id);


--
-- Name: _user _user_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._user
    ADD CONSTRAINT _user_pkey PRIMARY KEY (id);


--
-- Name: contact_sales_form contact_sales_form_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.contact_sales_form
    ADD CONSTRAINT contact_sales_form_pkey PRIMARY KEY (id);


--
-- Name: custom_worker_radius custom_worker_radius_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.custom_worker_radius
    ADD CONSTRAINT custom_worker_radius_pkey PRIMARY KEY (work_site_id, worker_id);


--
-- Name: documents_i9 documents_i9_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.documents_i9
    ADD CONSTRAINT documents_i9_pkey PRIMARY KEY (id);


--
-- Name: employer_tax_record employer_tax_record_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.employer_tax_record
    ADD CONSTRAINT employer_tax_record_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: inactive_days inactive_days_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.inactive_days
    ADD CONSTRAINT inactive_days_pkey PRIMARY KEY (work_site_id, inactive_date);


--
-- Name: location_record location_record_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.location_record
    ADD CONSTRAINT location_record_pkey PRIMARY KEY (id);


--
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- Name: payment_history_irs payment_history_irs_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.payment_history_irs
    ADD CONSTRAINT payment_history_irs_pkey PRIMARY KEY (payment_history_irs_id);


--
-- Name: scheduler_execution_history scheduler_execution_history_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.scheduler_execution_history
    ADD CONSTRAINT scheduler_execution_history_pkey PRIMARY KEY (id);


--
-- Name: terms_of_use_agreement terms_of_use_agreement_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.terms_of_use_agreement
    ADD CONSTRAINT terms_of_use_agreement_pkey PRIMARY KEY (id);


--
-- Name: token token_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT token_pkey PRIMARY KEY (id);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: user_work_sites user_work_sites_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.user_work_sites
    ADD CONSTRAINT user_work_sites_pkey PRIMARY KEY (work_site_id, user_id);


--
-- Name: wc_risk_class wc_risk_class_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.wc_risk_class
    ADD CONSTRAINT wc_risk_class_pkey PRIMARY KEY (code);


--
-- Name: work_site work_site_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.work_site
    ADD CONSTRAINT work_site_pkey PRIMARY KEY (id);


--
-- Name: worker_attendance worker_attendance_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_attendance
    ADD CONSTRAINT worker_attendance_pkey PRIMARY KEY (id);


--
-- Name: worker_payroll worker_payroll_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_payroll
    ADD CONSTRAINT worker_payroll_pkey PRIMARY KEY (id);


--
-- Name: worker_schedule worker_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_schedule
    ADD CONSTRAINT worker_schedule_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_location_battery_low; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_location_battery_low ON public.location_record USING btree (user_id, "timestamp" DESC) WHERE (battery_level < 20);


--
-- Name: idx_location_coordinates; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_location_coordinates ON public.location_record USING btree (latitude, longitude);


--
-- Name: idx_location_date_user; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_location_date_user ON public.location_record USING btree (date("timestamp"), user_id);


--
-- Name: idx_location_timestamp; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_location_timestamp ON public.location_record USING btree ("timestamp" DESC);


--
-- Name: idx_location_user_timestamp; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_location_user_timestamp ON public.location_record USING btree (user_id, "timestamp" DESC);


--
-- Name: idx_scheduler_history_company; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_scheduler_history_company ON public.scheduler_execution_history USING btree (company_id);


--
-- Name: idx_scheduler_history_created_at; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_scheduler_history_created_at ON public.scheduler_execution_history USING btree (created_at);


--
-- Name: idx_scheduler_history_job_name; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_scheduler_history_job_name ON public.scheduler_execution_history USING btree (job_name);


--
-- Name: idx_scheduler_history_start_time; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_scheduler_history_start_time ON public.scheduler_execution_history USING btree (start_time);


--
-- Name: idx_scheduler_history_status; Type: INDEX; Schema: public; Owner: misha
--

CREATE INDEX idx_scheduler_history_status ON public.scheduler_execution_history USING btree (status);


--
-- Name: work_site fk_company; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.work_site
    ADD CONSTRAINT fk_company FOREIGN KEY (company_id) REFERENCES public._company(id) ON DELETE SET NULL;


--
-- Name: _company fk_company_owner; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._company
    ADD CONSTRAINT fk_company_owner FOREIGN KEY (owner_id) REFERENCES public._user(id);


--
-- Name: custom_worker_radius fk_custom_radius_work_site; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.custom_worker_radius
    ADD CONSTRAINT fk_custom_radius_work_site FOREIGN KEY (work_site_id) REFERENCES public.work_site(id) ON DELETE CASCADE;


--
-- Name: custom_worker_radius fk_custom_radius_worker; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.custom_worker_radius
    ADD CONSTRAINT fk_custom_radius_worker FOREIGN KEY (worker_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: _dependents fk_dependents_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._dependents
    ADD CONSTRAINT fk_dependents_user FOREIGN KEY (user_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: documents_i9 fk_documents_i9_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.documents_i9
    ADD CONSTRAINT fk_documents_i9_user FOREIGN KEY (user_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: employer_tax_record fk_employer_tax_record_company; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.employer_tax_record
    ADD CONSTRAINT fk_employer_tax_record_company FOREIGN KEY (company_id) REFERENCES public._company(id) ON DELETE CASCADE;


--
-- Name: employer_tax_record fk_employer_tax_record_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.employer_tax_record
    ADD CONSTRAINT fk_employer_tax_record_user FOREIGN KEY (employee_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: employer_tax_record fk_employer_tax_record_worker_payroll; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.employer_tax_record
    ADD CONSTRAINT fk_employer_tax_record_worker_payroll FOREIGN KEY (pay_stub_id) REFERENCES public.worker_payroll(id) ON DELETE CASCADE;


--
-- Name: inactive_days fk_inactive_days_work_site; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.inactive_days
    ADD CONSTRAINT fk_inactive_days_work_site FOREIGN KEY (work_site_id) REFERENCES public.work_site(id) ON DELETE CASCADE;


--
-- Name: location_record fk_location_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.location_record
    ADD CONSTRAINT fk_location_user FOREIGN KEY (user_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: payment_history_irs fk_payment_history_company; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.payment_history_irs
    ADD CONSTRAINT fk_payment_history_company FOREIGN KEY (company_id) REFERENCES public._company(id) ON DELETE SET NULL;


--
-- Name: _user fk_risk_class; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._user
    ADD CONSTRAINT fk_risk_class FOREIGN KEY (wc_risk_class_code) REFERENCES public.wc_risk_class(code);


--
-- Name: scheduler_execution_history fk_scheduler_history_company; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.scheduler_execution_history
    ADD CONSTRAINT fk_scheduler_history_company FOREIGN KEY (company_id) REFERENCES public._company(id) ON DELETE SET NULL;


--
-- Name: token fk_token_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.token
    ADD CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: _user fk_user_company; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._user
    ADD CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES public._company(id) ON DELETE SET NULL;


--
-- Name: _user fk_user_current_work_site; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._user
    ADD CONSTRAINT fk_user_current_work_site FOREIGN KEY (current_work_site_id) REFERENCES public.work_site(id);


--
-- Name: _user fk_user_work_site; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public._user
    ADD CONSTRAINT fk_user_work_site FOREIGN KEY (work_site_id) REFERENCES public.work_site(id);


--
-- Name: user_work_sites fk_user_work_site_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.user_work_sites
    ADD CONSTRAINT fk_user_work_site_user FOREIGN KEY (user_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: user_work_sites fk_user_work_site_ws; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.user_work_sites
    ADD CONSTRAINT fk_user_work_site_ws FOREIGN KEY (work_site_id) REFERENCES public.work_site(id) ON DELETE CASCADE;


--
-- Name: user_roles fk_users_roles_role; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES public._role(id) ON DELETE CASCADE;


--
-- Name: user_roles fk_users_roles_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: worker_attendance fk_worker_attendance_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_attendance
    ADD CONSTRAINT fk_worker_attendance_user FOREIGN KEY (worker_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: worker_payroll fk_worker_payroll_company; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_payroll
    ADD CONSTRAINT fk_worker_payroll_company FOREIGN KEY (company_id) REFERENCES public._company(id) ON DELETE CASCADE;


--
-- Name: worker_payroll fk_worker_payroll_risk_class; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_payroll
    ADD CONSTRAINT fk_worker_payroll_risk_class FOREIGN KEY (wc_risk_code) REFERENCES public.wc_risk_class(code) ON DELETE CASCADE;


--
-- Name: worker_payroll fk_worker_payroll_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_payroll
    ADD CONSTRAINT fk_worker_payroll_user FOREIGN KEY (worker_id) REFERENCES public._user(id) ON DELETE CASCADE;


--
-- Name: worker_schedule fk_worker_schedule_user; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_schedule
    ADD CONSTRAINT fk_worker_schedule_user FOREIGN KEY (worker_id) REFERENCES public._user(id);


--
-- Name: worker_schedule fk_worker_schedule_work_site; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.worker_schedule
    ADD CONSTRAINT fk_worker_schedule_work_site FOREIGN KEY (work_site_id) REFERENCES public.work_site(id);


--
-- Name: notification fki3qxd167b5wxg9i1p04le49uq; Type: FK CONSTRAINT; Schema: public; Owner: misha
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT fki3qxd167b5wxg9i1p04le49uq FOREIGN KEY (company_id) REFERENCES public._company(id);


--
-- PostgreSQL database dump complete
--

