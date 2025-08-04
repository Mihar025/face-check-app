package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.pdfServices.FillFormMTA305;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class GenerateMTA305Job extends BaseSchedulerJob {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkerPayrollRepository workerPayrollRepository;

    @Autowired
    private ReportsMailSender reportsMailSender;

    @Autowired
    private FillFormMTA305 fillFormMTA305;

    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("📋 Quarterly MTA-305 Scheduler запущен: генерируем quarterly MTA-305 forms");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился
        int completedQuarter;
        if (currentMonth == 1) {
            completedQuarter = 4;
            currentYear = currentYear - 1;
        } else if (currentMonth == 4) {
            completedQuarter = 1;
        } else if (currentMonth == 7) {
            completedQuarter = 2;
        } else if (currentMonth == 10) {
            completedQuarter = 3;
        } else {
            log.info("ℹ️ Ошибка в логике quarterly MTA-305 scheduler. Текущий месяц: {}", currentMonth);
            return JobResult.success(0);
        }

        // Вычисляем даты квартала
        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем MTA-305 Forms за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        // Получаем только NYC компании (Zone 1)
        List<Company> nycCompanies = companyRepository.findAll().stream()
                .filter(company -> isNYCCompany(company))
                .toList();

        int successCount = 0;
        int errorCount = 0;
        StringBuilder errors = new StringBuilder();

        for (Company company : nycCompanies) {
            try {
                // Проверяем, есть ли payrolls за квартал
                boolean hasPayrollsInQuarter = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                                company.getId(), startDate, endDate);

                if (hasPayrollsInQuarter) {
                    // Проверяем превышение лимита $312,500
                    BigDecimal quarterlyPayroll = workerPayrollRepository
                            .sumGrossWages(company.getId(), startDate, endDate);

                    if (quarterlyPayroll.compareTo(new BigDecimal("312500")) > 0) {
                        // Генерируем MTA-305 форму
                        byte[] mta305Pdf = fillFormMTA305.generateFilledPdf(
                                company.getId(), completedQuarter, currentYear);

                        // Отправляем email
                        reportsMailSender.sendEmailMTA305Form(company.getCompanyOwner().getEmail());

                        log.info("✅ Quarterly MTA-305 Form was generated: {} (ID: {}) for Q{} {}, size PDF: {} bytes, quarterly payroll: ${}",
                                company.getCompanyName(), company.getId(), completedQuarter, currentYear,
                                mta305Pdf.length, quarterlyPayroll);

                        successCount++;
                    } else {
                        log.info("ℹ️ Company {} didnt exceed limit $312,500 for Q{} {} (payroll: ${})",
                                company.getCompanyName(), completedQuarter, currentYear, quarterlyPayroll);
                    }
                } else {
                    log.info("ℹ️ No Payrolls for company: {} за Q{} {}",
                            company.getCompanyName(), completedQuarter, currentYear);
                }

            } catch (Exception ex) {
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                log.error("❌ Error generating quarterly MTA-305 form for company ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
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


    private boolean isNYCCompany(Company company) {
        String city = company.getCompanyCity();
        String state = company.getCompanyState();

        // Проверяем что компания в NY State
        if (!"NY".equalsIgnoreCase(state)) {
            return false;
        }

        // Zone 1 includes: Manhattan, Bronx, Brooklyn, Queens, Staten Island
        if (city != null) {
            String cityLower = city.toLowerCase();
            return cityLower.contains("manhattan") ||
                    cityLower.contains("bronx") ||
                    cityLower.contains("brooklyn") ||
                    cityLower.contains("queens") ||
                    cityLower.contains("staten island") ||
                    cityLower.contains("new york city") ||
                    cityLower.contains("nyc");
        }

        // Если город не указан, но ZIP code можно проверить
        String zipCode = company.getCompanyZipCode();
        if (zipCode != null) {
            return isNYCZipCode(zipCode);
        }

        return false;
    }


    private boolean isNYCZipCode(String zipCode) {
        if (zipCode == null || zipCode.length() < 5) {
            return false;
        }

        String zip5 = zipCode.substring(0, 5);

        // NYC ZIP codes:
        // Manhattan: 10001-10282
        // Bronx: 10451-10475
        // Brooklyn: 11201-11256
        // Queens: 11101-11697
        // Staten Island: 10301-10314

        try {
            int zipInt = Integer.parseInt(zip5);

            return (zipInt >= 10001 && zipInt <= 10282) ||  // Manhattan
                    (zipInt >= 10451 && zipInt <= 10475) ||  // Bronx
                    (zipInt >= 11201 && zipInt <= 11256) ||  // Brooklyn
                    (zipInt >= 11101 && zipInt <= 11697) ||  // Queens
                    (zipInt >= 10301 && zipInt <= 10314);    // Staten Island
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
