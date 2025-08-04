package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.pdfServices.W3OfficialPDFServicer;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class GenerateAnnualW3Reports extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private W3OfficialPDFServicer w3OfficialPDFServicer;

    @Autowired
    private ReportsMailSender reportsMailSender;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {

        int currentYear = LocalDate.now().getYear();
        int targetYear  = currentYear - 1;

        if (currentYear != 2026) {
            log.info("⏭️ Пропускаем генерацию W-3. Текущий год: {}, ожидаем: 2026", currentYear);
            return JobResult.success(0);
        }
        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();

        log.info("📄 Начинаем генерацию W-3 Official Forms за {} год", targetYear);
        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            try {
                byte[] pdf = w3OfficialPDFServicer.generateFilledPdf(company.getId(), targetYear);
                reportsMailSender.sendEmailW3Forms(company.getCompanyOwner().getEmail());
                log.info("✅ W-3 Official для {} (ID {}) за {} сгенерирован и отправлен, размер {} bytes",
                        company.getCompanyName(), company.getId(), targetYear, pdf.length);
                totalSuccess++;
            } catch (Exception ex) {
                totalFailure++;
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                log.error("❌ Ошибка генерации W-3 Official для компании ID {} за {} год",
                        company.getId(), targetYear, ex);
            }
        }
        if (totalFailure == 0 ) {
            return JobResult.success(totalSuccess);
        } else if (totalSuccess > 0) {
            return JobResult.partialSuccess(totalSuccess, totalFailure, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }
}
