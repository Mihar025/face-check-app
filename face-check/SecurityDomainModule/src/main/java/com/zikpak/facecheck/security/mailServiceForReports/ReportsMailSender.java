package com.zikpak.facecheck.security.mailServiceForReports;

import com.zikpak.facecheck.entity.Token;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.TokenRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.security.EmailService;
import com.zikpak.facecheck.security.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportsMailSender {

    private final EmailService emailService;
    private final UserRepository userRepository;


    public void sendEmail940Form(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Form 940 was successfully generated!";
        sendEmail(user, EmailTemplateName.FORM_940, subject);
    }

    public void sendEmail941Form(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Form 941 was successfully generated!";
        sendEmail(user, EmailTemplateName.FORM_941, subject);
    }


    public void sendEmail941SBForm(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Form 941 Schedule B was successfully generated!";
        sendEmail(user, EmailTemplateName.FORM_941_SB, subject);
    }

    public void sendEmailAnnualFutaReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Annual Futa Report was successfully generated!";
        sendEmail(user, EmailTemplateName.ANNUAL_FUTA_REPORT, subject);
    }

    public void sendEmailFutaCompliance(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " Futa Compliance was successfully generated!";
        sendEmail(user, EmailTemplateName.FUTA_COMPLIANCE, subject);
    }

    /*
    This method we are applying for all CSV reports!
     */
    public void sendEmailCSV(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " CSV Reports was successfully generated!";
        sendEmail(user, EmailTemplateName.CSV_REPORTS, subject);
    }

    public void sendEmailEFW2(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " EFW2 was successfully generated!";
        sendEmail(user, EmailTemplateName.EFW2, subject);
    }

    public void sendEmail940FormAndScheduleA(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " 940 Form and Schedule A was successfully generated!";
        sendEmail(user, EmailTemplateName.FORM_940_ScheduleA, subject);
    }

    public void sendEmailHoursReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " Hours Report was successfully generated!";
        sendEmail(user, EmailTemplateName.HOURS_REPORT, subject);
    }

    public void sendEmailPaystubs(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " Paystubs was generated!";
        sendEmail(user, EmailTemplateName.PAYSTUB_FORM, subject);
    }


    public void sendEmailQuarterSutaForm(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " Quarter Suta Form was successfully generated!";
        sendEmail(user, EmailTemplateName.QUARTER_SUTA_FORM, subject);
    }



    public void sendEmailQuarterFUTAReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = " Quarter Suta Form was successfully generated!";
        sendEmail(user, EmailTemplateName.QUARTER_FUTA_REPORT, subject);
    }


    public void sendEmailW2Forms(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "All W2 was successfully generated!";
        sendEmail(user, EmailTemplateName.W2_FORMS, subject);
    }

    public void sendEmailWeeklyPayrollReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Weekly Payroll Report was successfully generated!";
        sendEmail(user, EmailTemplateName.WEEKLY_PAYROLL_REPORT, subject);
    }

    public void sendEmailTaxSummaryReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Tax Summary Quarter Report was successfully generated!";
        sendEmail(user, EmailTemplateName.TAX_SUMMARY_REPORT, subject);
    }

    public void sendEmailXMLReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "XML Report was successfully generated!";
        sendEmail(user, EmailTemplateName.XML_REPORT, subject);
    }

    public void sendAnnualSUTAReport(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "Annual SUTA  Report was successfully generated!";
        sendEmail(user, EmailTemplateName.ANNUAL_SUTA, subject);
    }

    public void sendEmailMTA305Form(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "MTA-305 Report was successfully generated!";
        sendEmail(user, EmailTemplateName.MTA_305, subject);
    }

    public void sendEmailW3Forms(String email) throws MessagingException {
        var user = findUserByEmail(email);
        log.info("User found: {}", user.fullName());
        String subject = "W3 Form was successfully generated!";
        sendEmail(user, EmailTemplateName.W3Official, subject);
    }

    private void sendEmail(User user,
                           EmailTemplateName templateName,
                           String subject)throws MessagingException{
        try {
            emailService.sendEmailCustomReport(
                    user.getEmail(),
                    user.fullName(),
                    templateName,
                    null,
                    subject
            );
            log.info("Email sent successfully");
        } catch (MessagingException e) {
            log.error("Failed to send reset email", e);
            throw e;
        }
    }

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EntityNotFoundException("Cannot find user with provided email " + email));
    }



}
