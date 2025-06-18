package com.zikpak.facecheck.taxesServices.scheduler;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.taxesServices.ASCIIservices.EFW2GeneratorService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.futaCustomTaxReport.FutaReportService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.payrollReport.PayrollSummaryReportService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.taxSummary.TaxSummaryReportDTO;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form940ScheduleAXmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form940XmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form941ScheduleBXmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form941XmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.csvReports.*;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm940SA;
import com.zikpak.facecheck.taxesServices.pdfServices.Form940PdfGeneratorService;
import com.zikpak.facecheck.taxesServices.services.EmployerTaxService;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployerTaxScheduler {

    private final WorkerPayrollRepository workerPayrollRepository;
    private final EmployerTaxService employerTaxService;
    private final PayStubService payStubService;
    private final WorkerPayRollService workerPayRollService;
    private final UserRepository userRepository;
    private final PayrollSummaryDataService payrollSummaryDataService;
    private final PayrollSummaryReportService payrollSummaryReportService;
    private final CompanyRepository companyRepository;
    private final HoursReportDataService hoursReportDataService;
    private final HoursReportPdfService hoursReportPdfService;
    private final WorkerAttendanceRepository attendanceRepository;
    private final TaxSummaryDataService taxSummaryDataService;
    private final TaxSummaryPdfService taxSummaryPdfService;

    private final HoursReportCsvService hoursReportCsvService;
    private final PayrollSummaryReportCsvService payrollSummaryReportCsvService;
    private final TaxSummaryReportCsvService taxSummaryReportCsvService;

    private final Form941XmlGenerator form941XmlGenerator;
    private final Form941ScheduleBXmlGenerator form941ScheduleBXmlGenerator;
    private final EFW2GeneratorService efw2GeneratorService;

    private final FillForm940SA fillForm940SA;
    private final Form940PdfGeneratorService form940PdfGeneratorService;

    private final Form940XmlGenerator form940XmlGenerator;
    private final Form940ScheduleAXmlGenerator generateForm940ScheduleAXml;


    private final FutaReportService futaReportService;
    private final FutaReportPdfService futaReportPdfService;

    @Scheduled(cron = "0 0 4 * * SUN") // каждое воскресенье в 4:00 утра
    // @Scheduled(fixedDelay = 10000) // каждые 10 секунд
    public void calculateWeeklyEmployerTaxes() {
        log.info("🧮 Scheduler запущен: начинаем расчёт налогов для всех payroll'ов");

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByPeriodEnd(LocalDate.now().minusDays(1)) // вчера была суббота
                .stream()
                .filter(p -> !p.isEmployerTaxesCalculated())
                .toList();

        for (WorkerPayroll payroll : payrolls) {
            try {
                employerTaxService.calculateAndSaveEmployerTaxes(payroll);
                payroll.setEmployerTaxesCalculated(true);

                workerPayrollRepository.save(payroll);
                log.info("✅ Расчёт выполнен для payroll ID: {}", payroll.getId());
            } catch (Exception e) {
                log.error("❌ Ошибка при расчёте налогов для payroll ID: {}", payroll.getId(), e);
            }
        }

        log.info("🏁 Scheduler завершил работу");
    }

    @Scheduled(cron = "0 0 5 * * SUN", zone = "America/New_York")
    public void generateWeeklyPayStubs() {
        log.info("🗓 PayStubScheduler запущен: генерируем paystubs за субботу");

        LocalDate yesterday = LocalDate.now().minusDays(1); // суббота
        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByPeriodEnd(yesterday).stream()
                .filter(p -> !Boolean.TRUE.equals(p.getPayStubGenerated()))
                .toList();

        for (WorkerPayroll payroll : payrolls) {
            try {
                payStubService.generatePayStubPdf(payroll.getId());
                payroll.setPayStubGenerated(true);
                workerPayrollRepository.save(payroll);
                log.info("✅ PayStub сгенерён для payroll ID={}", payroll.getId());
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации paystub для payroll ID={}", payroll.getId(), ex);
            }
        }

        log.info("🏁 PayStubScheduler завершил работу");
    }

    @Scheduled(cron = "0 0 5 3 1 *", zone = "America/New_York")
    public void generateAllW2FormsFor2025() {
        if(LocalDate.now().getYear() != 2026){
            return;
        }

        int targetYear = 2025;

        log.info("📄 W2FormScheduler запущен: генерируем W-2 за {} всем работникам", targetYear);

        // 1) Берём всех пользователей (или, при необходимости, фильтруйте только тех, кто — работники)
        for (User worker : userRepository.findAll()) {
            Integer workerId = worker.getId();
            try {
                // 2) Сервис внутри заливает PDF в S3 по нужному пути
                workerPayRollService.generatePDF(workerId, targetYear);
                log.info("✅ Сгенерирован W-2 для workerId={} за {}", workerId, targetYear);
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации W-2 для workerId={}", workerId, ex);
            }
        }

        log.info("🏁 W2FormScheduler завершил генерацию W-2 за {}", targetYear);
    }








//-------- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
    //New methods

    @Scheduled(cron = "0 0 6 * * SUN", zone = "America/New_York")
    public void generateWeeklyPayrollReports() {
        log.info("📊 Weekly PayrollReportScheduler запущен: генерируем еженедельные отчеты");

        LocalDate endDate = LocalDate.now().minusDays(1); // суббота (последний день недели)
        LocalDate startDate = endDate.minusDays(6); // воскресенье (первый день недели)

        log.info("📅 Генерируем отчеты за период: {} - {}", startDate, endDate);

        // Получаем все компании
        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли payrolls за эту неделю для этой компании
                List<WorkerPayroll> weeklyPayrolls = workerPayrollRepository
                        .findAllByCompanyIdAndPeriodBetween(company.getId(), startDate, endDate);

                if (!weeklyPayrolls.isEmpty()) {
                    // Генерируем данные отчета
                    PayrollSummaryReportDTO reportData = payrollSummaryDataService
                            .generatePayrollSummaryData(company.getId(), startDate, endDate);

                    // Генерируем PDF и сохраняем в S3
                    payrollSummaryReportService.generatePayrollSummaryReport(reportData);
                    log.info("✅ Weekly report сгенерен для компании: {} (ID: {})",
                            company.getCompanyName(), company.getId());
                } else {
                    log.info("ℹ️ Нет payrolls за эту неделю для компании: {}", company.getCompanyName());
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации weekly report для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 Weekly PayrollReportScheduler завершил работу");
    }


    @Scheduled(cron = "0 0 7 1-7 * SUN", zone = "America/New_York")
    public void generateMonthlyPayrollReports() {
        log.info("📊 Monthly PayrollReportScheduler запущен: генерируем месячные отчеты");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth()); // последний день прошлого месяца
        LocalDate startDate = endDate.withDayOfMonth(1); // первый день прошлого месяца

        log.info("📅 Генерируем отчеты за период: {} - {}", startDate, endDate);

        // Получаем все компании
        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли payrolls за этот месяц для этой компании
                List<WorkerPayroll> monthlyPayrolls = workerPayrollRepository
                        .findAllByCompanyIdAndPeriodBetween(company.getId(), startDate, endDate);

                if (!monthlyPayrolls.isEmpty()) {
                    PayrollSummaryReportDTO reportData = payrollSummaryDataService
                            .generatePayrollSummaryData(company.getId(), startDate, endDate);

                    payrollSummaryReportService.generatePayrollSummaryReport(reportData);

                    log.info("✅ Monthly report сгенерен для компании: {} (ID: {}) за {}/{}",
                            company.getCompanyName(), company.getId(),
                            startDate.getMonthValue(), startDate.getYear());
                } else {
                    log.info("ℹ️ Нет payrolls за месяц {}/{} для компании: {}",
                            startDate.getMonthValue(), startDate.getYear(), company.getCompanyName());
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly report для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 Monthly PayrollReportScheduler завершил работу");
    }

    @Scheduled(cron = "0 30 6 * * SUN", zone = "America/New_York")
    public void generateWeeklyHoursReports() {
        log.info("⏰ Weekly HoursReportScheduler запущен: генерируем еженедельные отчеты по часам");

        LocalDate endDate = LocalDate.now().minusDays(1); // суббота
        LocalDate startDate = endDate.minusDays(6); // воскресенье

        log.info("📅 Генерируем отчеты по часам за период: {} - {}", startDate, endDate);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // ✅ ПРАВИЛЬНО: Проверяем есть ли workers в компании
                List<User> workers = userRepository.findAllByCompanyId(company.getId());

                if (!workers.isEmpty()) {
                    // ✅ ПРАВИЛЬНО: Используем тот же метод что в service
                    List<WorkerAttendance> weeklyAttendances = new ArrayList<>();

                    for (User worker : workers) {
                        List<WorkerAttendance> workerAttendances = attendanceRepository
                                .findAllByWorkerIdAndCheckInTimeBetween(
                                        worker.getId(),
                                        startDate.atStartOfDay(),
                                        endDate.atTime(23, 59, 59)
                                );
                        weeklyAttendances.addAll(workerAttendances);
                    }

                    if (!weeklyAttendances.isEmpty()) {
                        // Генерируем данные отчета по часам
                        HoursReportDTO reportData = hoursReportDataService
                                .generateHoursReportData(company.getId(), startDate, endDate);

                        // Генерируем PDF и сохраняем в S3
                        hoursReportPdfService.generateHoursReport(reportData, company.getId());

                        log.info("✅ Weekly hours report сгенерен для компании: {} (ID: {})",
                                company.getCompanyName(), company.getId());
                    } else {
                        log.info("ℹ️ Нет attendance за эту неделю для компании: {}", company.getCompanyName());
                    }
                } else {
                    log.info("ℹ️ Нет сотрудников в компании: {}", company.getCompanyName());
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации weekly hours report для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 Weekly HoursReportScheduler завершил работу");
    }

    // ✅ МЕСЯЧНЫЙ HOURS REPORT (каждое первое воскресенье месяца в 7:30 утра)
    @Scheduled(cron = "0 30 7 1-7 * SUN", zone = "America/New_York")
    public void generateMonthlyHoursReports() {
        log.info("⏰ Monthly HoursReportScheduler запущен: генерируем месячные отчеты по часам");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth());
        LocalDate startDate = endDate.withDayOfMonth(1);

        log.info("📅 Генерируем отчеты по часам за период: {} - {}", startDate, endDate);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // ✅ ПРАВИЛЬНО: Проверяем есть ли workers в компании
                List<User> workers = userRepository.findAllByCompanyId(company.getId());

                if (!workers.isEmpty()) {
                    // ✅ ПРАВИЛЬНО: Используем тот же метод что в service
                    List<WorkerAttendance> monthlyAttendances = new ArrayList<>();

                    for (User worker : workers) {
                        List<WorkerAttendance> workerAttendances = attendanceRepository
                                .findAllByWorkerIdAndCheckInTimeBetween(
                                        worker.getId(),
                                        startDate.atStartOfDay(),
                                        endDate.atTime(23, 59, 59)
                                );
                        monthlyAttendances.addAll(workerAttendances);
                    }

                    if (!monthlyAttendances.isEmpty()) {
                        HoursReportDTO reportData = hoursReportDataService
                                .generateHoursReportData(company.getId(), startDate, endDate);

                        hoursReportPdfService.generateHoursReport(reportData, company.getId());

                        log.info("✅ Monthly hours report сгенерен для компании: {} (ID: {}) за {}/{}",
                                company.getCompanyName(), company.getId(),
                                startDate.getMonthValue(), startDate.getYear());
                    } else {
                        log.info("ℹ️ Нет attendance за месяц {}/{} для компании: {}",
                                startDate.getMonthValue(), startDate.getYear(), company.getCompanyName());
                    }
                } else {
                    log.info("ℹ️ Нет сотрудников в компании: {}", company.getCompanyName());
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly hours report для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 Monthly HoursReportScheduler завершил работу");
    }



    @Scheduled(cron = "0 0 9 1 1,4,7,10 *", zone = "America/New_York")
    public void generateQuarterlyTaxSummaryReports() {
        log.info("📋 Quarterly TaxSummaryScheduler запущен: генерируем quarterly tax summary reports");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился
        int completedQuarter;
        if (currentMonth == 1) {        // 1 января - закончился Q4 прошлого года
            completedQuarter = 4;
            currentYear = currentYear - 1;
        } else if (currentMonth == 4) { // 1 апреля - закончился Q1
            completedQuarter = 1;
        } else if (currentMonth == 7) { // 1 июля - закончился Q2
            completedQuarter = 2;
        } else if (currentMonth == 10) { // 1 октября - закончился Q3
            completedQuarter = 3;
        } else {
            log.info("ℹ️ Ошибка в логике quarterly scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        // Вычисляем даты квартала
        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем Tax Summary Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        // Получаем все компании
        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Генерируем Tax Summary Report
                TaxSummaryReportDTO reportData = taxSummaryDataService
                        .generateTaxSummaryReport(company.getId(), startDate, endDate);

                // Генерируем PDF и сохраняем в S3
                taxSummaryPdfService.generateTaxSummaryReport(reportData);

                log.info("✅ Quarterly Tax Summary Report сгенерен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Tax Summary Report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
            }
        }

        log.info("🏁 Quarterly TaxSummaryScheduler завершил работу за Q{} {}", completedQuarter, currentYear);
    }

// =============================================================================
// 📅 MONTHLY CSV REPORTS
// =============================================================================

    @Scheduled(cron = "0 15 7 1-7 * SUN", zone = "America/New_York") // 15 минут после PDF генерации
    public void generateMonthlyPayrollReportsCsv() {
        log.info("📊 Monthly PayrollReportCSV Scheduler запущен: генерируем месячные CSV отчеты");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth());
        LocalDate startDate = endDate.withDayOfMonth(1);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                List<WorkerPayroll> monthlyPayrolls = workerPayrollRepository
                        .findAllByCompanyIdAndPeriodBetween(company.getId(), startDate, endDate);

                if (!monthlyPayrolls.isEmpty()) {
                    PayrollSummaryReportDTO reportData = payrollSummaryDataService
                            .generatePayrollSummaryData(company.getId(), startDate, endDate);

                    // Генерируем CSV и сохраняем в S3
                    payrollSummaryReportCsvService.generatePayrollSummaryReportCsv(reportData, company.getId());

                    log.info("✅ Monthly CSV report сгенерен для компании: {} (ID: {}) за {}/{}",
                            company.getCompanyName(), company.getId(),
                            startDate.getMonthValue(), startDate.getYear());
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly CSV report для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 Monthly PayrollReportCSV Scheduler завершил работу");
    }

    @Scheduled(cron = "0 45 7 1-7 * SUN", zone = "America/New_York") // 45 минут после PDF генерации
    public void generateMonthlyHoursReportsCsv() {
        log.info("⏰ Monthly HoursReportCSV Scheduler запущен: генерируем месячные CSV отчеты по часам");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth());
        LocalDate startDate = endDate.withDayOfMonth(1);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                List<User> workers = userRepository.findAllByCompanyId(company.getId());

                if (!workers.isEmpty()) {
                    List<WorkerAttendance> monthlyAttendances = new ArrayList<>();

                    for (User worker : workers) {
                        List<WorkerAttendance> workerAttendances = attendanceRepository
                                .findAllByWorkerIdAndCheckInTimeBetween(
                                        worker.getId(),
                                        startDate.atStartOfDay(),
                                        endDate.atTime(23, 59, 59)
                                );
                        monthlyAttendances.addAll(workerAttendances);
                    }

                    if (!monthlyAttendances.isEmpty()) {
                        HoursReportDTO reportData = hoursReportDataService
                                .generateHoursReportData(company.getId(), startDate, endDate);

                        // Генерируем CSV и сохраняем в S3
                        hoursReportCsvService.generateHoursReportCsv(reportData, company.getId());

                        log.info("✅ Monthly hours CSV report сгенерен для компании: {} (ID: {}) за {}/{}",
                                company.getCompanyName(), company.getId(),
                                startDate.getMonthValue(), startDate.getYear());
                    }
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly hours CSV report для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 Monthly HoursReportCSV Scheduler завершил работу");
    }

// =============================================================================
// 📋 QUARTERLY TAX SUMMARY CSV REPORTS
// =============================================================================

    @Scheduled(cron = "0 30 9 1 1,4,7,10 *", zone = "America/New_York") // 30 минут после PDF генерации
    public void generateQuarterlyTaxSummaryReportsCsv() {
        log.info("📋 Quarterly TaxSummaryCSV Scheduler запущен: генерируем quarterly tax summary CSV reports");

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
            log.info("ℹ️ Ошибка в логике quarterly CSV scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        // Вычисляем даты квартала
        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем Tax Summary CSV Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                TaxSummaryReportDTO reportData = taxSummaryDataService
                        .generateTaxSummaryReport(company.getId(), startDate, endDate);

                // Генерируем CSV и сохраняем в S3
                taxSummaryReportCsvService.generateTaxSummaryReportCsv(reportData, company.getId());

                log.info("✅ Quarterly Tax Summary CSV Report сгенерен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Tax Summary CSV Report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
            }
        }

        log.info("🏁 Quarterly TaxSummaryCSV Scheduler завершил работу за Q{} {}", completedQuarter, currentYear);
    }


// 📋 QUARTERLY E-FILE GENERATION (Form 941 & Schedule B)
// =============================================================================

    @Scheduled(cron = "0 0 10 15 1,4,7,10 *", zone = "America/New_York") // 15 число квартальных месяцев в 10:00
    public void generateQuarterlyForm941XmlFiles() {
        log.info("📋 Quarterly Form941XML Scheduler запущен: генерируем quarterly Form 941 XML files");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился
        int completedQuarter;
        if (currentMonth == 1) {        // 15 января - закончился Q4 прошлого года
            completedQuarter = 4;
            currentYear = currentYear - 1;
        } else if (currentMonth == 4) { // 15 апреля - закончился Q1
            completedQuarter = 1;
        } else if (currentMonth == 7) { // 15 июля - закончился Q2
            completedQuarter = 2;
        } else if (currentMonth == 10) { // 15 октября - закончился Q3
            completedQuarter = 3;
        } else {
            log.info("ℹ️ Ошибка в логике quarterly Form941XML scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        log.info("📅 Генерируем Form 941 XML files за Q{} {}", completedQuarter, currentYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Получаем admin пользователя для подписи (берем первого админа компании)
                List<User> admins = userRepository.findAllByCompanyIdAndRole(company.getId(), "ADMIN");
                if (admins.isEmpty()) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Form 941 XML", company.getCompanyName());
                    continue;
                }
                User admin = admins.get(0);

                // Генерируем Form 941 XML
                String form941Xml = form941XmlGenerator.generateForm941Xml(
                        admin.getId(), company.getId(), currentYear, completedQuarter);

                log.info("✅ Form 941 XML сгенерен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 941 XML для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
            }
        }

        log.info("🏁 Quarterly Form941XML Scheduler завершил работу за Q{} {}", completedQuarter, currentYear);
    }

    @Scheduled(cron = "0 30 10 15 1,4,7,10 *", zone = "America/New_York") // 30 минут после Form 941
    public void generateQuarterlyForm941ScheduleBXmlFiles() {
        log.info("📋 Quarterly Form941ScheduleBXML Scheduler запущен: генерируем quarterly Form 941 Schedule B XML files");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился (та же логика)
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
            log.info("ℹ️ Ошибка в логике quarterly Form941ScheduleBXML scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        log.info("📅 Генерируем Form 941 Schedule B XML files за Q{} {}", completedQuarter, currentYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Получаем admin пользователя для подписи
                List<User> admins = userRepository.findAllByCompanyIdAndRole(company.getId(), "ADMIN");
                if (admins.isEmpty()) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Form 941 Schedule B XML", company.getCompanyName());
                    continue;
                }
                User admin = admins.get(0);

                // Генерируем Form 941 Schedule B XML
                String form941ScheduleBXml = form941ScheduleBXmlGenerator.generateForm941ScheduleBXml(
                        admin.getId(), company.getId(), currentYear, completedQuarter);

                log.info("✅ Form 941 Schedule B XML сгенерен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 941 Schedule B XML для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
            }
        }

        log.info("🏁 Quarterly Form941ScheduleBXML Scheduler завершил работу за Q{} {}", completedQuarter, currentYear);
    }

// =============================================================================
// 📄 ANNUAL E-FILE GENERATION (EFW2)
// =============================================================================

    @Scheduled(cron = "0 0 11 31 1 *", zone = "America/New_York") // 31 января в 11:00 (deadline для W-2)
    public void generateAnnualEFW2Files() {
        log.info("📄 Annual EFW2 Scheduler запущен: генерируем EFW2 files за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // EFW2 генерируем за прошлый год

        log.info("📅 Генерируем EFW2 files за {} год", previousYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли сотрудники в компании за этот год
                List<User> employees = userRepository.findAllByCompanyId(company.getId());

                if (!employees.isEmpty()) {
                    // Генерируем EFW2 файл
                    byte[] efw2Content = efw2GeneratorService.generateEfw2File(company.getId(), previousYear);

                    log.info("✅ EFW2 file сгенерен для компании: {} (ID: {}) за {} год, размер: {} bytes",
                            company.getCompanyName(), company.getId(), previousYear, efw2Content.length);
                } else {
                    log.info("ℹ️ Нет сотрудников в компании: {} за {} год", company.getCompanyName(), previousYear);
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации EFW2 file для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
            }
        }

        log.info("🏁 Annual EFW2 Scheduler завершил работу за {} год", previousYear);
    }


    @Scheduled(cron = "0 0 11 15 1 *", zone = "America/New_York")
    public void generateAnnualForm940AndScheduleA() {
        log.info("📄 Annual Form940 + Schedule A Scheduler запущен");

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                //todo change later on 2025!
                form940PdfGeneratorService.generate940Pdf(company.getId(), 2024);
                fillForm940SA.generateFilledPdf(company.getId(), 2024);

                log.info("✅ Form 940 + Schedule A сгенерены для {}", company.getCompanyName());
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации для компании {}", company.getId(), ex);
            }
        }
    }

    @Scheduled(cron = "0 0 12 31 1 *", zone = "America/New_York") // 31 января в 12:00 (после PDF)
    public void generateAnnualForm940XmlFiles() {
        log.info("📄 Annual Form940XML Scheduler запущен: генерируем Form 940 XML e-files за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // Form 940 генерируем за прошлый год

        log.info("📅 Генерируем Form 940 XML e-files за {} год", previousYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Получаем админа компании для подписи
                List<User> admins = getCompanyAdmins(company.getId());
                if (admins.isEmpty()) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Form 940 XML", company.getCompanyName());
                    continue;
                }
                User admin = admins.get(0);

                // Генерируем Form 940 XML e-file
                String form940Xml = form940XmlGenerator.generateForm940Xml(
                        admin.getId(), company.getId(), previousYear);

                log.info("✅ Form 940 XML e-file сгенерен для компании: {} (ID: {}) за {} год",
                        company.getCompanyName(), company.getId(), previousYear);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 940 XML для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
            }
        }

        log.info("🏁 Annual Form940XML Scheduler завершил работу за {} год", previousYear);
    }

    @Scheduled(cron = "0 30 12 31 1 *", zone = "America/New_York") // 31 января в 12:30 (после Form 940 XML)
    public void generateAnnualForm940ScheduleAXmlFiles() {
        log.info("📄 Annual Form940ScheduleAXML Scheduler запущен: генерируем Schedule A XML e-files за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        log.info("📅 Генерируем Form 940 Schedule A XML e-files за {} год", previousYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Только для NY компаний
                if (!"NY".equals(company.getCompanyState())) {
                    log.info("ℹ️ Компания {} в штате {} - Schedule A XML не нужна",
                            company.getCompanyName(), company.getCompanyState());
                    continue;
                }

                // Получаем админа компании для подписи
                List<User> admins = getCompanyAdmins(company.getId());
                if (admins.isEmpty()) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Schedule A XML", company.getCompanyName());
                    continue;
                }
                User admin = admins.get(0);

                // Генерируем Schedule A XML e-file
                String scheduleAXml = generateForm940ScheduleAXml.generateForm940ScheduleAXml(
                        admin.getId(), company.getId(), previousYear);

                if (!scheduleAXml.isEmpty()) {
                    log.info("✅ Form 940 Schedule A XML e-file сгенерен для компании: {} (ID: {}) за {} год",
                            company.getCompanyName(), company.getId(), previousYear);
                } else {
                    log.info("ℹ️ Schedule A XML не требуется для компании: {}", company.getCompanyName());
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Schedule A XML для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
            }
        }

        log.info("🏁 Annual Form940ScheduleAXML Scheduler завершил работу за {} год", previousYear);
    }


    /**
     * Генерирует квартальные FUTA отчеты
     * Запускается 15 числа каждого квартального месяца (янв, апр, июль, окт) в 8:00
     */
    @Scheduled(cron = "0 0 8 15 1,4,7,10 *", zone = "America/New_York")
    public void generateQuarterlyFutaReports() {
        log.info("📋 Quarterly FUTA Report Scheduler запущен: генерируем quarterly FUTA reports");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // Определяем какой квартал только что закончился
        int completedQuarter;
        if (currentMonth == 1) {        // 15 января - закончился Q4 прошлого года
            completedQuarter = 4;
            currentYear = currentYear - 1;
        } else if (currentMonth == 4) { // 15 апреля - закончился Q1
            completedQuarter = 1;
        } else if (currentMonth == 7) { // 15 июля - закончился Q2
            completedQuarter = 2;
        } else if (currentMonth == 10) { // 15 октября - закончился Q3
            completedQuarter = 3;
        } else {
            log.info("ℹ️ Ошибка в логике quarterly FUTA scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        log.info("📅 Генерируем FUTA Reports за Q{} {}", completedQuarter, currentYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли сотрудники в компании
                List<User> employees = userRepository.findAllByCompanyId(company.getId());

                if (!employees.isEmpty()) {
                    // Генерируем квартальный FUTA отчет
                    FutaReportDTO reportData = futaReportService.generateQuarterlyFutaReport(
                            company.getId(), currentYear, completedQuarter);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

                    log.info("✅ Quarterly FUTA Report сгенерен для компании: {} (ID: {}) за Q{} {}, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), completedQuarter, currentYear, pdfBytes.length);
                } else {
                    log.info("ℹ️ Нет сотрудников в компании: {} за Q{} {}",
                            company.getCompanyName(), completedQuarter, currentYear);
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации quarterly FUTA report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
            }
        }

        log.info("🏁 Quarterly FUTA Report Scheduler завершил работу за Q{} {}", completedQuarter, currentYear);
    }

// =============================================================================
// 📋 ANNUAL FUTA REPORTS
// =============================================================================

    /**
     * Генерирует годовые FUTA отчеты
     * Запускается 31 января в 9:00 утра (дедлайн для Form 940)
     */
    @Scheduled(cron = "0 0 9 31 1 *", zone = "America/New_York")
    public void generateAnnualFutaReports() {
        log.info("📄 Annual FUTA Report Scheduler запущен: генерируем annual FUTA reports за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // Годовой отчет генерируем за прошлый год

        log.info("📅 Генерируем Annual FUTA Reports за {} год", previousYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли сотрудники в компании за прошлый год
                List<User> employees = userRepository.findAllByCompanyId(company.getId());

                if (!employees.isEmpty()) {
                    // Генерируем годовой FUTA отчет
                    FutaReportDTO reportData = futaReportService.generateAnnualFutaReport(
                            company.getId(), previousYear);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

                    log.info("✅ Annual FUTA Report сгенерен для компании: {} (ID: {}) за {} год, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), previousYear, pdfBytes.length);
                } else {
                    log.info("ℹ️ Нет сотрудников в компании: {} за {} год",
                            company.getCompanyName(), previousYear);
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации annual FUTA report для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
            }
        }

        log.info("🏁 Annual FUTA Report Scheduler завершил работу за {} год", previousYear);
    }

// =============================================================================
// 📋 QUARTERLY FUTA COMPLIANCE CHECK
// =============================================================================

    /**
     * Проверяет compliance и отправляет уведомления о предстоящих дедлайнах
     * Запускается каждый понедельник в 6:00 утра
     */
    @Scheduled(cron = "0 0 6 * * MON", zone = "America/New_York")
    public void checkQuarterlyFutaCompliance() {
        log.info("⚠️ FUTA Compliance Check Scheduler запущен: проверяем compliance и дедлайны");

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        // Определяем текущий квартал и следующий дедлайн
        int currentQuarter;
        LocalDate nextDeadline;

        if (currentMonth >= 1 && currentMonth <= 3) {
            currentQuarter = 1;
            nextDeadline = LocalDate.of(currentYear, 4, 30); // Q1 deadline
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            currentQuarter = 2;
            nextDeadline = LocalDate.of(currentYear, 7, 31); // Q2 deadline
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            currentQuarter = 3;
            nextDeadline = LocalDate.of(currentYear, 10, 31); // Q3 deadline
        } else {
            currentQuarter = 4;
            nextDeadline = LocalDate.of(currentYear + 1, 1, 31); // Q4 deadline
        }

        // Проверяем только если до дедлайна осталось меньше 2 недель
        long daysUntilDeadline = today.until(nextDeadline).getDays();

        if (daysUntilDeadline <= 14 && daysUntilDeadline > 0) {
            log.info("⚠️ До FUTA дедлайна осталось {} дней. Проверяем compliance для Q{} {}",
                    daysUntilDeadline, currentQuarter, currentYear);

            List<Company> allCompanies = companyRepository.findAll();

            for (Company company : allCompanies) {
                try {
                    // Генерируем текущий квартальный отчет для проверки
                    FutaReportDTO currentReport = futaReportService.generateQuarterlyFutaReport(
                            company.getId(), currentYear, currentQuarter);

                    // Проверяем compliance
                    if (!currentReport.getComplianceStatus() || currentReport.getNeedsPayment()) {
                        log.warn("⚠️ COMPLIANCE ALERT: Компания {} (ID: {}) требует внимания по FUTA за Q{} {}. " +
                                        "Compliance: {}, Needs Payment: {}, Remaining Liability: ${}",
                                company.getCompanyName(), company.getId(), currentQuarter, currentYear,
                                currentReport.getComplianceStatus(), currentReport.getNeedsPayment(),
                                currentReport.getRemainingFutaLiability());

                        // Здесь можно добавить отправку email уведомлений админам компании
                        // sendFutaComplianceAlert(company, currentReport, daysUntilDeadline);
                    }

                } catch (Exception ex) {
                    log.error("❌ Ошибка проверки FUTA compliance для компании ID: {}", company.getId(), ex);
                }
            }
        } else if (daysUntilDeadline <= 0) {
            log.warn("🚨 FUTA дедлайн просрочен на {} дней!", Math.abs(daysUntilDeadline));
        }

        log.info("🏁 FUTA Compliance Check завершен");
    }

// =============================================================================
// 📋 FUTA YEAR-END PREPARATION
// =============================================================================

    /**
     * Подготавливает данные к концу года (проверяет все квартальные отчеты)
     * Запускается 15 декабря в 10:00 утра
     */
    @Scheduled(cron = "0 0 10 15 12 *", zone = "America/New_York")
    public void prepareFutaYearEndReports() {
        log.info("📋 FUTA Year-End Preparation Scheduler запущен: подготавливаем данные к концу года");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        log.info("📅 Подготавливаем FUTA данные за {} год к концу года", currentYear);

        List<Company> allCompanies = companyRepository.findAll();

        for (Company company : allCompanies) {
            try {
                log.info("🔍 Проверяем все квартальные FUTA отчеты для компании: {} (ID: {})",
                        company.getCompanyName(), company.getId());

                // Проверяем все 4 квартала текущего года
                BigDecimal totalAnnualLiability = BigDecimal.ZERO;
                BigDecimal totalAnnualPaid = BigDecimal.ZERO;
                List<String> missingQuarters = new ArrayList<>();

                for (int quarter = 1; quarter <= 4; quarter++) {
                    try {
                        FutaReportDTO quarterlyReport = futaReportService.generateQuarterlyFutaReport(
                                company.getId(), currentYear, quarter);

                        totalAnnualLiability = totalAnnualLiability.add(quarterlyReport.getTotalFutaTaxOwed());
                        totalAnnualPaid = totalAnnualPaid.add(quarterlyReport.getTotalFutaTaxPaid());

                        if (!quarterlyReport.getComplianceStatus()) {
                            missingQuarters.add("Q" + quarter);
                        }

                        log.info("✅ Q{} {}: Owed=${}, Paid=${}, Compliant={}",
                                quarter, currentYear,
                                quarterlyReport.getTotalFutaTaxOwed(),
                                quarterlyReport.getTotalFutaTaxPaid(),
                                quarterlyReport.getComplianceStatus());

                    } catch (Exception ex) {
                        log.error("❌ Ошибка проверки Q{} для компании {}", quarter, company.getId(), ex);
                        missingQuarters.add("Q" + quarter + " (ERROR)");
                    }
                }

                // Итоговый отчет по компании
                BigDecimal remainingLiability = totalAnnualLiability.subtract(totalAnnualPaid);

                log.info("📊 Итоги {} года для {}: Total Owed=${}, Total Paid=${}, Remaining=${}, Issues={}",
                        currentYear, company.getCompanyName(),
                        totalAnnualLiability, totalAnnualPaid, remainingLiability,
                        missingQuarters.isEmpty() ? "None" : String.join(", ", missingQuarters));

                if (!missingQuarters.isEmpty() || remainingLiability.compareTo(BigDecimal.ONE) > 0) {
                    log.warn("⚠️ YEAR-END ALERT: Компания {} требует внимания перед концом года. " +
                                    "Проблемные кварталы: {}, Remaining Liability: ${}",
                            company.getCompanyName(),
                            missingQuarters.isEmpty() ? "None" : String.join(", ", missingQuarters),
                            remainingLiability);

                    // Здесь можно добавить отправку email уведомлений
                    // sendFutaYearEndAlert(company, totalAnnualLiability, totalAnnualPaid, missingQuarters);
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка year-end preparation для компании ID: {}", company.getId(), ex);
            }
        }

        log.info("🏁 FUTA Year-End Preparation завершен за {} год", currentYear);
    }













    private List<User> getCompanyAdmins(Integer companyId) {
        try {
            // Попробуй использовать метод с ролями, если есть
            return userRepository.findAllByCompanyIdAndRole(companyId, "ADMIN");
        } catch (Exception e) {
            return userRepository.findAllByCompanyId(companyId).stream()
                    .filter(user -> {

                        return user.getRoles() != null && user.getRoles().stream()
                                .anyMatch(role -> "ADMIN".equals(role.getName()));
                    })
                    .toList();
        }

    }

}
