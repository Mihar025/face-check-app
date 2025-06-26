package com.zikpak.facecheck.security;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account"),
    RESET_PASSWORD("reset-password"),

    FORM_940("940_form"),
    FORM_941("941_form"),
    FORM_941_SB("941_sb_form"),
    ANNUAL_FUTA_REPORT("annualFutaReport"),
    FUTA_COMPLIANCE("checkQuarterFutaCompliance"),
    CSV_REPORTS("csv_reports"),
    EFW2("efw2"),
    FORM_940_ScheduleA("form940AndScheduleA"),
    HOURS_REPORT("hours_report"),
    PAYSTUB_FORM("paystub_form"),
    QUARTER_SUTA_FORM("quarter_suta_form"),
    QUARTER_FUTA_REPORT("quarter_futa_report"),
    W2_FORMS("w2_forms"),
    WEEKLY_PAYROLL_REPORT("weeklyPayrollReport"),
    TAX_SUMMARY_REPORT("tax_summary_report"),
    XML_REPORT("xml-files"),
    ANNUAL_SUTA("annual-suta")
    ;
    private final String name;
    EmailTemplateName(String name) {
        this.name = name;
    }
}



