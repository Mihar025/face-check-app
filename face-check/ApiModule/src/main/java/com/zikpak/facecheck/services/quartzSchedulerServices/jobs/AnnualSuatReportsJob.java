package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AnnualSuatReportsJob  extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SutaReportService sutaReportService;

    @Autowired
    private SutaReportPdfService sutaReportPdfService;

    @Autowired
    private ReportsMailSender reportsMailSender;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📄 Annual SUTA Report Scheduler запущен: генерируем annual SUTA reports за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        // 1. Берём все компании
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            log.info("ℹ️ Нет компаний для обработки");
            return JobResult.success(0);
        }

        // 2. Собираем их ID и одним запросом вытаскиваем всех сотрудников
        List<Integer> companyIds = companies.stream()
                .map(Company::getId)
                .toList();
        List<User> allEmployees = userRepository.findAllByCompanyIdIn(companyIds);

        // 3. Группируем сотрудников по компании
        Map<Integer, List<User>> employeesByCompany = allEmployees.stream()
                .collect(Collectors.groupingBy(u -> u.getCompany().getId()));

        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Company company : companies) {
            try {
                List<User> employees = employeesByCompany
                        .getOrDefault(company.getId(), Collections.emptyList());

                if (employees.isEmpty()) {
                    log.info("ℹ️ Нет сотрудников в компании: {} за {} год",
                            company.getCompanyName(), previousYear);
                    continue;
                }

                // 5. Генерируем отчёт и сохраняем PDF в S3
                SutaReportDTO reportData = sutaReportService
                        .generateAnnualSutaReport(company.getId(), previousYear);
                byte[] pdfBytes = sutaReportPdfService
                        .generateSutaReportPdf(reportData);

                log.info("✅ Annual SUTA Report сгенерен для компании: {} (ID: {}) за {} год, размер PDF: {} bytes",
                        company.getCompanyName(), company.getId(), previousYear, pdfBytes.length);

                // 6. И сразу шлём письмо, как в других методах
                reportsMailSender.sendAnnualSUTAReport(company.getCompanyEmail());
                successCount++;
                log.info("📧 Email с Annual SUTA Report отправлен компании: {}", company.getCompanyEmail());

            } catch (Exception ex) {
                errorCount++;
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                log.error("❌ Ошибка генерации или отправки annual SUTA report для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
            }
        }
        if (errorCount == 0 ) {
            return JobResult.success(successCount);
        } else if (successCount > 0) {
            return JobResult.partialSuccess(successCount, errorCount, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }
}
