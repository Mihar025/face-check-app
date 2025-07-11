package com.zikpak.facecheck.taxesServices.scheduler;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.CompanyPaymentPosition;
import com.zikpak.facecheck.entity.Role;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
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
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportPdfService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.sutaCustomTaxReturn.SutaReportService;
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
import com.zikpak.facecheck.taxesServices.pdfServices.W2OfficialPDFService;
import com.zikpak.facecheck.taxesServices.services.EmployerTaxService;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskCsvService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskServiceForPDF;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    private final RoleRepository roleRepository;
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

    private final SutaReportService sutaReportService;
    private final SutaReportPdfService sutaReportPdfService;

    private final ReportsMailSender reportsMailSender;
    private final W2OfficialPDFService w2OfficialPDFService;


    @Scheduled(cron = "0 0 4 * * SUN") // каждое воскресенье в 4:00 утра
    public void calculateWeeklyEmployerTaxes() {
        log.info("🧮 Scheduler запущен: начинаем расчёт налогов для всех payroll'ов");

        LocalDate today = LocalDate.now();

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByPeriodEnd(today.minusDays(1)) // проверяем вчерашний день
                .stream()
                .filter(p -> !p.isEmployerTaxesCalculated())
                .toList();

        for (WorkerPayroll payroll : payrolls) {
            try {
                Company company = payroll.getCompany(); // ⬅️ вот как берём компанию

                if (!shouldCreateEmployerTaxRecord(payroll, company)) {
                    log.info("⏩ Пропускаем payroll ID {} — период ещё не завершён", payroll.getId());
                    continue;
                }

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


/*

    @Scheduled(cron = "0 0 4 * * SUN") // каждое воскресенье
    public void generatePayStubs() {
        log.info("📄 Scheduler: генерация Paystubs началась");

        LocalDate today = LocalDate.now();

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findAllByPeriodEnd(today)
                .stream()
                .filter(p -> !Boolean.TRUE.equals(p.getPayStubGenerated()))
                .toList();

        for (WorkerPayroll payroll : payrolls) {
            try {
                Company company = payroll.getCompany();

                // Если период компании подходит — генерируем
                if (company.getCompanyPaymentPosition() != null &&
                        today.equals(payroll.getPeriodEnd())) {

                    payStubService.generatePayStubPdf(payroll.getId());
                    payroll.setPayStubGenerated(true);
                    workerPayrollRepository.save(payroll);

                    log.info("✅ Paystub создан для payroll ID: {}", payroll.getId());
                    log.info("📄 Обработано payrolls: {}", payrolls.size());
                }
            } catch (Exception e) {
                log.error("❌ Ошибка при генерации paystub для payroll ID: {}", payroll.getId(), e);
            }
        }

        log.info("🏁 Scheduler: генерация Paystubs завершена");
    }

 */


 // 1) Еженедельные – только для компаний с WEEKLY
    @Scheduled(cron = "0 0 4 * * SUN", zone = "America/New_York")
    public void generateWeeklyPayStubs() {
        log.info("📄 Scheduler (WEEKLY): запуск генерации Paystubs");

        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));

        List<WorkerPayroll> payrolls = workerPayrollRepository
            .findAllByPeriodEnd(today).stream()
            .filter(p -> !Boolean.TRUE.equals(p.getPayStubGenerated()))
            .filter(p -> p.getCompany().getCompanyPaymentPosition() == CompanyPaymentPosition.WEEKLY)
            .toList();

        // Группируем по компаниям
        Map<Company, List<WorkerPayroll>> payrollsByCompany = payrolls.stream()
                .collect(Collectors.groupingBy(WorkerPayroll::getCompany));

        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerPayroll> companyPayrolls = entry.getValue();
            int successCount = 0;

            for (WorkerPayroll payroll : companyPayrolls) {
                try {
                    payStubService.generatePayStubPdf(payroll.getId());
                    payroll.setPayStubGenerated(true);
                    workerPayrollRepository.save(payroll);
                    successCount++;
                    log.info("✅ WEEKLY paystub создан для payroll ID: {}", payroll.getId());
                } catch (Exception e) {
                    log.error("❌ Ошибка WEEKLY для payroll ID: {}", payroll.getId(), e);
                }
            }
            if (successCount > 0) {
                try {
                    reportsMailSender.sendEmailPaystubs(company.getCompanyEmail());
                    log.info("📧 Email отправлен компании: {} (paystubs: {})",
                            company.getCompanyEmail(), successCount);
                } catch (Exception e) {
                    log.error("❌ Ошибка отправки email для компании: {}",
                            company.getCompanyEmail(), e);
                }
            }
        }

        log.info("🏁 Scheduler (WEEKLY): завершено, всего: {}", payrolls.size());
    }

    // 2) Би-недельные – только для BIWEEKLY и только в «чётные» недели
    @Scheduled(cron = "0 30 4 * * SUN", zone = "America/New_York")
    public void generateBiweeklyPayStubs() {
        log.info("📄 Scheduler (BIWEEKLY): запуск генерации Paystubs");

        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));

        List<WorkerPayroll> payrolls = workerPayrollRepository
            .findAllByPeriodEnd(today).stream()
            .filter(p -> !Boolean.TRUE.equals(p.getPayStubGenerated()))
            .filter(p -> p.getCompany().getCompanyPaymentPosition() == CompanyPaymentPosition.BIWEEKLY)
            .filter(p -> {

                LocalDate first = p.getCompany().getFirstBiweeklyDate();
                long weeks = ChronoUnit.WEEKS.between(first, today);
                return weeks % 2 == 0; // только чётная
            })
            .toList();

        // Группируем по компаниям
        Map<Company, List<WorkerPayroll>> payrollsByCompany = payrolls.stream()
                .collect(Collectors.groupingBy(WorkerPayroll::getCompany));

        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerPayroll> companyPayrolls = entry.getValue();
            int successCount = 0;


        for (WorkerPayroll payroll : companyPayrolls) {
            try {
                payStubService.generatePayStubPdf(payroll.getId());
                payroll.setPayStubGenerated(true);
                workerPayrollRepository.save(payroll);
                successCount++;
                log.info("✅ BIWEEKLY paystub создан для payroll ID: {}", payroll.getId());
            } catch (Exception e) {
                log.error("❌ Ошибка BIWEEKLY для payroll ID: {}", payroll.getId(), e);
            }
        }
            if (successCount > 0) {
                try {
                    reportsMailSender.sendEmailPaystubs(company.getCompanyEmail());
                    log.info("📧 Email отправлен компании: {} (paystubs: {})",
                            company.getCompanyEmail(), successCount);
                } catch (Exception e) {
                    log.error("❌ Ошибка отправки email для компании: {}",
                            company.getCompanyEmail(), e);
                }
            }
        }
        log.info("🏁 Scheduler (BIWEEKLY): завершено, всего: {}", payrolls.size());
    }


    @Scheduled(cron = "0 0 5 3 1 *", zone = "America/New_York")
    public void generateAllW2FormsFor2025() {
        int currentYear = LocalDate.now().getYear();
        int targetYear = currentYear - 1; // W-2 за прошлый год

        // Выполняем только в правильный год
        if (currentYear != 2026) {
            log.info("⏭️ Пропускаем генерацию W-2. Текущий год: {}, ожидаем: 2026", currentYear);
            return;
        }

        log.info("📄 W2FormScheduler запущен: генерируем W-2 за {} всем работникам", targetYear);

        // Получаем всех пользователей, у которых есть компания (т.е. они работники)
        List<User> workers = userRepository.findAll().stream()
                .filter(user -> user.getCompany() != null)
                .filter(user -> !user.isBusinessOwner()) // исключаем владельцев бизнеса
                .toList();

        // ИЛИ можно создать специальный метод в репозитории:
        // @Query("SELECT u FROM User u WHERE u.company IS NOT NULL AND u.isBusinessOwner = false")
        // List<User> findAllWorkers();

        // Группируем по компаниям для отправки уведомлений
        Map<Company, List<User>> workersByCompany = new HashMap<>();

        for (User worker : workers) {
            try {
                // Генерируем W-2
                workerPayRollService.generatePDF(worker.getId(), targetYear);
                log.info("✅ Сгенерирован W-2 для workerId={} за {}", worker.getId(), targetYear);

                // Добавляем в группу для компании
                Company company = worker.getCompany();
                workersByCompany.computeIfAbsent(company, k -> new ArrayList<>()).add(worker);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации W-2 для workerId={}", worker.getId(), ex);
            }
        }

        // Отправляем уведомления компаниям
        for (Map.Entry<Company, List<User>> entry : workersByCompany.entrySet()) {
            Company company = entry.getKey();
            int count = entry.getValue().size();
            try {
                reportsMailSender.sendEmailW2Forms(company.getCompanyEmail());
                log.info("📧 Уведомление о W-2 отправлено компании: {} (forms: {})",
                        company.getCompanyEmail(), count);
            } catch (Exception e) {
                log.error("❌ Ошибка отправки уведомления для компании: {}",
                        company.getCompanyEmail(), e);
            }
        }

        log.info("🏁 W2FormScheduler завершил генерацию W-2 за {}. Всего: {} работников",
                targetYear, workers.size());
    }


    @Scheduled(cron = "0 0 7 3 1 *", zone = "America/New_York")
    public void generateAllW2OfficialFormsFor2025() {
        int currentYear = LocalDate.now().getYear();
        int targetYear = currentYear - 1; // W-2 за прошлый год

        if (currentYear != 2026) {
            log.info("⏭️ Пропускаем генерацию W-2. Текущий год: {}, ожидаем: 2026", currentYear);
            return;
        }

        log.info("📄 W2FormScheduler запущен: генерируем W-2 за {} всем работникам", targetYear);

        // Получаем всех пользователей, у которых есть компания (т.е. они работники)
        List<User> workers = userRepository.findAll().stream()
                .filter(user -> user.getCompany() != null)
                .filter(user -> !user.isBusinessOwner()) // исключаем владельцев бизнеса
                .toList();


        // Группируем по компаниям для отправки уведомлений
        Map<Company, List<User>> workersByCompany = new HashMap<>();

        for (User worker : workers) {
            try {
                // Генерируем W-2
                w2OfficialPDFService.generateFilledPdf(worker.getId(), worker.getCompany().getId(),  targetYear);
                log.info("✅ Сгенерирован W-2 для workerId={} за {}", worker.getId(), targetYear);

                // Добавляем в группу для компании
                Company company = worker.getCompany();
                workersByCompany.computeIfAbsent(company, k -> new ArrayList<>()).add(worker);

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации W-2 для workerId={}", worker.getId(), ex);
            }
        }

        for (Map.Entry<Company, List<User>> entry : workersByCompany.entrySet()) {
            Company company = entry.getKey();
            int count = entry.getValue().size();
            try {
                reportsMailSender.sendEmailW2Forms(company.getCompanyEmail());
                log.info("📧 Уведомление о W-2 отправлено компании: {} (forms: {})",
                        company.getCompanyEmail(), count);
            } catch (Exception e) {
                log.error("❌ Ошибка отправки уведомления для компании: {}",
                        company.getCompanyEmail(), e);
            }
        }

        log.info("🏁 W2FormScheduler завершил генерацию W-2 за {}. Всего: {} работников",
                targetYear, workers.size());
    }





//-------- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----
    //New methods

    @Scheduled(cron = "0 0 6 * * SUN", zone = "America/New_York")
    public void generateWeeklyPayrollReports() {
        log.info("📊 Weekly PayrollReportScheduler запущен: генерируем еженедельные отчеты");

        LocalDate endDate = LocalDate.now().minusDays(1);
        LocalDate startDate = endDate.minusDays(6);

        // ОДИН запрос вместо N+1
        List<WorkerPayroll> allWeeklyPayrolls = workerPayrollRepository
                .findAllByPeriodBetween(startDate, endDate);

        // Группируем по компаниям в памяти
        Map<Company, List<WorkerPayroll>> payrollsByCompany = allWeeklyPayrolls.stream()
                .collect(Collectors.groupingBy(wp -> wp.getCompany()));

        // Теперь обрабатываем только компании с payrolls
        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerPayroll> companyPayrolls = entry.getValue();

            try {
                // Генерируем данные отчета
                PayrollSummaryReportDTO reportData = payrollSummaryDataService
                        .generatePayrollSummaryData(company.getId(), startDate, endDate);

                // Генерируем PDF и сохраняем в S3
                payrollSummaryReportService.generatePayrollSummaryReport(reportData);

                log.info("✅ Weekly report сгенерен для компании: {} (ID: {})",
                        company.getCompanyName(), company.getId());

                reportsMailSender.sendEmailWeeklyPayrollReport(company.getCompanyEmail());

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации weekly report для компании ID: {}",
                        company.getId(), ex);
            }
        }

        log.info("🏁 Weekly PayrollReportScheduler завершил работу. Обработано компаний: {}",
                payrollsByCompany.size());
    }


    @Scheduled(cron = "0 0 7 1-7 * SUN", zone = "America/New_York")
    public void generateMonthlyPayrollReports() {
        log.info("📊 Monthly PayrollReportScheduler запущен: генерируем месячные отчеты");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth()); // последний день прошлого месяца
        LocalDate startDate = endDate.withDayOfMonth(1); // первый день прошлого месяца

        log.info("📅 Генерируем отчеты за период: {} - {}", startDate, endDate);

        // ОДИН запрос вместо N+1
        List<WorkerPayroll> allMonthlyPayrolls = workerPayrollRepository
                .findAllByPeriodBetween(startDate, endDate);

        // Группируем по компаниям
        Map<Company, List<WorkerPayroll>> payrollsByCompany = allMonthlyPayrolls.stream()
                .collect(Collectors.groupingBy(WorkerPayroll::getCompany));

        int successCount = 0;
        int errorCount = 0;

        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();

            try {
                PayrollSummaryReportDTO reportData = payrollSummaryDataService
                        .generatePayrollSummaryData(company.getId(), startDate, endDate);

                payrollSummaryReportService.generatePayrollSummaryReport(reportData);

                reportsMailSender.sendEmailWeeklyPayrollReport(company.getCompanyEmail());

                log.info("✅ Monthly report сгенерен для компании: {} (ID: {}) за {}/{}",
                        company.getCompanyName(), company.getId(),
                        startDate.getMonthValue(), startDate.getYear());

                successCount++;
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly report для компании ID: {}",
                        company.getId(), ex);
                errorCount++;
            }
        }

        log.info("🏁 Monthly PayrollReportScheduler завершил работу. " +
                        "Обработано компаний: {}, успешно: {}, с ошибками: {}",
                payrollsByCompany.size(), successCount, errorCount);
    }



    @Scheduled(cron = "0 30 6 * * SUN", zone = "America/New_York")
    public void generateWeeklyHoursReports() {
        log.info("⏰ Weekly HoursReportScheduler запущен: генерируем еженедельные отчеты по часам");

        LocalDate endDate = LocalDate.now().minusDays(1); // суббота
        LocalDate startDate = endDate.minusDays(6); // воскресенье

        log.info("📅 Генерируем отчеты по часам за период: {} - {}", startDate, endDate);

        // ОДИН запрос для всех attendance за неделю
        List<WorkerAttendance> allWeeklyAttendances = attendanceRepository
                .findAllByCheckInTimeBetween(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        // Группируем по компаниям через работников
        Map<Company, List<WorkerAttendance>> attendancesByCompany = allWeeklyAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getWorker().getCompany()));

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Map.Entry<Company, List<WorkerAttendance>> entry : attendancesByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerAttendance> companyAttendances = entry.getValue();

            try {
                // Генерируем данные отчета по часам
                HoursReportDTO reportData = hoursReportDataService
                        .generateHoursReportData(company.getId(), startDate, endDate);

                // Генерируем PDF и сохраняем в S3
                hoursReportPdfService.generateHoursReport(reportData, company.getId());

                // Отправляем email
                reportsMailSender.sendEmailHoursReport(company.getCompanyEmail());

                log.info("✅ Weekly hours report сгенерен для компании: {} (ID: {}, attendances: {})",
                        company.getCompanyName(), company.getId(), companyAttendances.size());

                successCount++;
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации weekly hours report для компании ID: {}",
                        company.getId(), ex);
                errorCount++;
            }
        }

        long companiesWithoutAttendance = companyRepository.count() - attendancesByCompany.size();
        if (companiesWithoutAttendance > 0) {
            log.info("ℹ️ Компаний без attendance за неделю: {}", companiesWithoutAttendance);
        }

        log.info("🏁 Weekly HoursReportScheduler завершил работу. " +
                        "Обработано: {}, успешно: {}, с ошибками: {}",
                attendancesByCompany.size(), successCount, errorCount);
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

        // ОДИН запрос для всех attendance за месяц
        List<WorkerAttendance> allMonthlyAttendances = attendanceRepository
                .findAllByCheckInTimeBetween(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        // Группируем по компаниям
        Map<Company, List<WorkerAttendance>> attendancesByCompany = allMonthlyAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getWorker().getCompany()));

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Map.Entry<Company, List<WorkerAttendance>> entry : attendancesByCompany.entrySet()) {
            Company company = entry.getKey();
            List<WorkerAttendance> companyAttendances = entry.getValue();

            try {
                // Генерируем отчет
                HoursReportDTO reportData = hoursReportDataService
                        .generateHoursReportData(company.getId(), startDate, endDate);

                // Генерируем PDF
                hoursReportPdfService.generateHoursReport(reportData, company.getId());

                // Отправляем email
                reportsMailSender.sendEmailHoursReport(company.getCompanyEmail());

                log.info("✅ Monthly hours report сгенерен для компании: {} (ID: {}) за {}/{} (attendances: {})",
                        company.getCompanyName(), company.getId(),
                        startDate.getMonthValue(), startDate.getYear(),
                        companyAttendances.size());

                successCount++;
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly hours report для компании ID: {}",
                        company.getId(), ex);
                errorCount++;
            }
        }

        // Логируем компании без attendance
        long companiesWithoutAttendance = companyRepository.count() - attendancesByCompany.size();
        if (companiesWithoutAttendance > 0) {
            log.info("ℹ️ Компаний без attendance за месяц {}/{}: {}",
                    startDate.getMonthValue(), startDate.getYear(), companiesWithoutAttendance);
        }

        log.info("🏁 Monthly HoursReportScheduler завершил работу. " +
                        "Обработано: {}, успешно: {}, с ошибками: {}",
                attendancesByCompany.size(), successCount, errorCount);
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

        int successCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                // Генерируем Tax Summary Report
                TaxSummaryReportDTO reportData = taxSummaryDataService
                        .generateTaxSummaryReport(company.getId(), startDate, endDate);

                // Генерируем PDF и сохраняем в S3
                taxSummaryPdfService.generateTaxSummaryReport(reportData);

                // ✅ ДОБАВЛЕНО: Отправляем email
                reportsMailSender.sendEmailTaxSummaryReport(company.getCompanyEmail());
                log.info("✅ Quarterly Tax Summary Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

                successCount++;

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Tax Summary Report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Quarterly TaxSummaryScheduler завершил работу за Q{} {}. " +
                        "Обработано компаний: {}, успешно: {}, с ошибками: {}",
                completedQuarter, currentYear, allCompanies.size(), successCount, errorCount);
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

        // ОДИН запрос для всех payrolls за месяц
        List<WorkerPayroll> allMonthlyPayrolls = workerPayrollRepository
                .findAllByPeriodBetween(startDate, endDate);

        // Группируем по компаниям
        Map<Company, List<WorkerPayroll>> payrollsByCompany = allMonthlyPayrolls.stream()
                .collect(Collectors.groupingBy(WorkerPayroll::getCompany));

        int successCount = 0;
        int errorCount = 0;

        for (Map.Entry<Company, List<WorkerPayroll>> entry : payrollsByCompany.entrySet()) {
            Company company = entry.getKey();

            try {
                PayrollSummaryReportDTO reportData = payrollSummaryDataService
                        .generatePayrollSummaryData(company.getId(), startDate, endDate);

                // Генерируем CSV и сохраняем в S3
                payrollSummaryReportCsvService.generatePayrollSummaryReportCsv(reportData, company.getId());

                // Отправляем email
                reportsMailSender.sendEmailCSV(company.getCompanyEmail());

                log.info("✅ Monthly CSV report сгенерен для компании: {} (ID: {}) за {}/{}",
                        company.getCompanyName(), company.getId(),
                        startDate.getMonthValue(), startDate.getYear());

                successCount++;
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly CSV report для компании ID: {}",
                        company.getId(), ex);
                errorCount++;
            }
        }

        log.info("🏁 Monthly PayrollReportCSV Scheduler завершил работу. " +
                        "Обработано компаний: {}, успешно: {}, с ошибками: {}",
                payrollsByCompany.size(), successCount, errorCount);
    }


    @Scheduled(cron = "0 45 7 1-7 * SUN", zone = "America/New_York") // 45 минут после PDF генерации
    public void generateMonthlyHoursReportsCsv() {
        log.info("⏰ Monthly HoursReportCSV Scheduler запущен: генерируем месячные CSV отчеты по часам");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth());
        LocalDate startDate = endDate.withDayOfMonth(1);

        // ОДИН запрос для всех attendance за месяц
        List<WorkerAttendance> allMonthlyAttendances = attendanceRepository
                .findAllByCheckInTimeBetween(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        // Группируем по компаниям
        Map<Company, List<WorkerAttendance>> attendancesByCompany = allMonthlyAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getWorker().getCompany()));

        int successCount = 0;
        int errorCount = 0;

        for (Map.Entry<Company, List<WorkerAttendance>> entry : attendancesByCompany.entrySet()) {
            Company company = entry.getKey();

            try {
                HoursReportDTO reportData = hoursReportDataService
                        .generateHoursReportData(company.getId(), startDate, endDate);

                // Генерируем CSV и сохраняем в S3
                hoursReportCsvService.generateHoursReportCsv(reportData, company.getId());

                // Отправляем email ПОСЛЕ успешной генерации
                reportsMailSender.sendEmailCSV(company.getCompanyEmail());

                log.info("✅ Monthly hours CSV report сгенерен и отправлен для компании: {} (ID: {}) за {}/{}",
                        company.getCompanyName(), company.getId(),
                        startDate.getMonthValue(), startDate.getYear());

                successCount++;
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации monthly hours CSV report для компании ID: {}",
                        company.getId(), ex);
                errorCount++;
            }
        }

        log.info("🏁 Monthly HoursReportCSV Scheduler завершил работу. " +
                        "Обработано компаний: {}, успешно: {}, с ошибками: {}",
                attendancesByCompany.size(), successCount, errorCount);
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

        int successCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                TaxSummaryReportDTO reportData = taxSummaryDataService
                        .generateTaxSummaryReport(company.getId(), startDate, endDate);

                // Генерируем CSV и сохраняем в S3
                taxSummaryReportCsvService.generateTaxSummaryReportCsv(reportData, company.getId());

                // ✅ ДОБАВЛЕНО: Отправляем email
                reportsMailSender.sendEmailCSV(company.getCompanyEmail());

                log.info("✅ Quarterly Tax Summary CSV Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

                successCount++;

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Tax Summary CSV Report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Quarterly TaxSummaryCSV Scheduler завершил работу за Q{} {}. " +
                        "Обработано компаний: {}, успешно: {}, с ошибками: {}",
                completedQuarter, currentYear, allCompanies.size(), successCount, errorCount);
    }

// 📋 QUARTERLY E-FILE GENERATION (Form 941 & Schedule B)
// =============================================================================

    @Scheduled(cron = "0 0 10 15 1,4,7,10 *", zone = "America/New_York")
    public void generateQuarterlyForm941XmlFiles() {
        log.info("📋 Quarterly Form941XML Scheduler запущен: генерируем quarterly Form 941 XML files");

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
            log.info("ℹ️ Ошибка в логике quarterly Form941XML scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        log.info("📅 Генерируем Form 941 XML files за Q{} {}", completedQuarter, currentYear);

        // Получаем роль ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        // Получаем всех пользователей с ролью ADMIN
        List<User> allAdmins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(adminRole))
                .toList();

        // Группируем админов по компаниям
        Map<Integer, User> adminsByCompanyId = allAdmins.stream()
                .filter(admin -> admin.getCompany() != null)
                .collect(Collectors.toMap(
                        admin -> admin.getCompany().getId(),
                        admin -> admin,
                        (existing, replacement) -> existing
                ));

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                User admin = adminsByCompanyId.get(company.getId());
                if (admin == null) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Form 941 XML",
                            company.getCompanyName());
                    skipCount++;
                    continue;
                }

                // Генерируем Form 941 XML
                String form941Xml = form941XmlGenerator.generateForm941Xml(
                        admin.getId(), company.getId(), currentYear, completedQuarter);

                // Специфичный метод для Form 941
                reportsMailSender.sendEmailXMLReport(company.getCompanyEmail());

                log.info("✅ Form 941 XML сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

                successCount++;

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 941 XML для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Quarterly Form941XML Scheduler завершил работу за Q{} {}. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                completedQuarter, currentYear, allCompanies.size(), successCount, skipCount, errorCount);
    }

    @Scheduled(cron = "0 30 10 15 1,4,7,10 *", zone = "America/New_York") // 30 минут после Form 941
    public void generateQuarterlyForm941ScheduleBXmlFiles() {
        log.info("📋 Quarterly Form941ScheduleBXML Scheduler запущен: генерируем quarterly Form 941 Schedule B XML files");

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
            log.info("ℹ️ Ошибка в логике quarterly Form941ScheduleBXML scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        log.info("📅 Генерируем Form 941 Schedule B XML files за Q{} {}", completedQuarter, currentYear);

        // Получаем роль ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        // Получаем всех пользователей с ролью ADMIN
        List<User> allAdmins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(adminRole))
                .toList();

        // Группируем админов по компаниям
        Map<Integer, User> adminsByCompanyId = allAdmins.stream()
                .filter(admin -> admin.getCompany() != null)
                .collect(Collectors.toMap(
                        admin -> admin.getCompany().getId(),
                        admin -> admin,
                        (existing, replacement) -> existing
                ));

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                User admin = adminsByCompanyId.get(company.getId());
                if (admin == null) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Form 941 Schedule B XML",
                            company.getCompanyName());
                    skipCount++;
                    continue;
                }

                // Генерируем Form 941 Schedule B XML
                String form941ScheduleBXml = form941ScheduleBXmlGenerator.generateForm941ScheduleBXml(
                        admin.getId(), company.getId(), currentYear, completedQuarter);

                // Специфичный метод для Schedule B
                reportsMailSender.sendEmailXMLReport(company.getCompanyEmail());

                log.info("✅ Form 941 Schedule B XML сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}",
                        company.getCompanyName(), company.getId(), completedQuarter, currentYear);

                successCount++;

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 941 Schedule B XML для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Quarterly Form941ScheduleBXML Scheduler завершил работу за Q{} {}. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                completedQuarter, currentYear, allCompanies.size(), successCount, skipCount, errorCount);
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

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                // Проверяем, были ли payrolls у компании за прошлый год
                LocalDate yearStart = LocalDate.of(previousYear, 1, 1);
                LocalDate yearEnd = LocalDate.of(previousYear, 12, 31);

                boolean hasPayrollsInYear = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd);

                if (hasPayrollsInYear) {
                    // Генерируем EFW2 файл
                    byte[] efw2Content = efw2GeneratorService.generateEfw2File(company.getId(), previousYear);

                    // Отправляем email
                    reportsMailSender.sendEmailEFW2(company.getCompanyEmail());

                    log.info("✅ EFW2 file сгенерен и отправлен для компании: {} (ID: {}) за {} год, размер: {} bytes",
                            company.getCompanyName(), company.getId(), previousYear, efw2Content.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls в компании: {} за {} год", company.getCompanyName(), previousYear);
                    skipCount++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации EFW2 file для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Annual EFW2 Scheduler завершил работу за {} год. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                previousYear, allCompanies.size(), successCount, skipCount, errorCount);
    }


    @Scheduled(cron = "0 0 11 15 1 *", zone = "America/New_York")
    public void generateAnnualForm940AndScheduleA() {
        log.info("📄 Annual Form940 + Schedule A Scheduler запущен");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // Form 940 за прошлый год

        log.info("📅 Генерируем Form 940 + Schedule A за {} год", previousYear);

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                // Проверяем, были ли payrolls у компании за прошлый год
                LocalDate yearStart = LocalDate.of(previousYear, 1, 1);
                LocalDate yearEnd = LocalDate.of(previousYear, 12, 31);

                boolean hasPayrollsInYear = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd);

                if (hasPayrollsInYear) {
                    // Генерируем Form 940
                    form940PdfGeneratorService.generate940Pdf(company.getId(), previousYear);

                    // Генерируем Schedule A
                    fillForm940SA.generateFilledPdf(company.getId(), previousYear);

                    // Отправляем email
                    reportsMailSender.sendEmail940FormAndScheduleA(
                            company.getCompanyEmail());

                    log.info("✅ Form 940 + Schedule A сгенерены и отправлены для {} (ID: {}) за {} год",
                            company.getCompanyName(), company.getId(), previousYear);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании {} за {} год",
                            company.getCompanyName(), previousYear);
                    skipCount++;
                }
            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 940 + Schedule A для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Annual Form940 + Schedule A Scheduler завершил работу за {} год. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                previousYear, allCompanies.size(), successCount, skipCount, errorCount);
    }


    @Scheduled(cron = "0 0 12 31 1 *", zone = "America/New_York") // 31 января в 12:00 (после PDF)
    public void generateAnnualForm940XmlFiles() {
        log.info("📄 Annual Form940XML Scheduler запущен: генерируем Form 940 XML e-files за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // Form 940 генерируем за прошлый год

        log.info("📅 Генерируем Form 940 XML e-files за {} год", previousYear);

        // Получаем роль ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        // Получаем всех админов одним запросом
        List<User> allAdmins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(adminRole))
                .toList();

        // Группируем админов по компаниям
        Map<Integer, User> adminsByCompanyId = allAdmins.stream()
                .filter(admin -> admin.getCompany() != null)
                .collect(Collectors.toMap(
                        admin -> admin.getCompany().getId(),
                        admin -> admin,
                        (existing, replacement) -> existing
                ));

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                // Проверяем наличие данных за год
                LocalDate yearStart = LocalDate.of(previousYear, 1, 1);
                LocalDate yearEnd = LocalDate.of(previousYear, 12, 31);

                boolean hasPayrollsInYear = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd);

                if (!hasPayrollsInYear) {
                    log.info("ℹ️ Нет payrolls для компании {} за {} год",
                            company.getCompanyName(), previousYear);
                    skipCount++;
                    continue;
                }

                // Получаем админа компании
                User admin = adminsByCompanyId.get(company.getId());
                if (admin == null) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Form 940 XML",
                            company.getCompanyName());
                    skipCount++;
                    continue;
                }

                // Генерируем Form 940 XML e-file
                String form940Xml = form940XmlGenerator.generateForm940Xml(
                        admin.getId(), company.getId(), previousYear);

                // Отправляем email
                reportsMailSender.sendEmail940Form(company.getCompanyEmail());

                log.info("✅ Form 940 XML e-file сгенерен и отправлен для компании: {} (ID: {}) за {} год",
                        company.getCompanyName(), company.getId(), previousYear);

                successCount++;

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Form 940 XML для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Annual Form940XML Scheduler завершил работу за {} год. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                previousYear, allCompanies.size(), successCount, skipCount, errorCount);
    }


    @Scheduled(cron = "0 30 12 31 1 *", zone = "America/New_York") // 31 января в 12:30 (после Form 940 XML)
    public void generateAnnualForm940ScheduleAXmlFiles() {
        log.info("📄 Annual Form940ScheduleAXML Scheduler запущен: генерируем Schedule A XML e-files за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        log.info("📅 Генерируем Form 940 Schedule A XML e-files за {} год", previousYear);

        // Получаем роль ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        // Получаем всех админов одним запросом
        List<User> allAdmins = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(adminRole))
                .toList();

        // Группируем админов по компаниям
        Map<Integer, User> adminsByCompanyId = allAdmins.stream()
                .filter(admin -> admin.getCompany() != null)
                .collect(Collectors.toMap(
                        admin -> admin.getCompany().getId(),
                        admin -> admin,
                        (existing, replacement) -> existing
                ));

        // Получаем только NY компании
        List<Company> nyCompanies = companyRepository.findAll().stream()
                .filter(company -> "NY".equals(company.getCompanyState()))
                .toList();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;
        int notNyCount = companyRepository.findAll().size() - nyCompanies.size();

        for (Company company : nyCompanies) {
            try {
                // Проверяем наличие данных за год
                LocalDate yearStart = LocalDate.of(previousYear, 1, 1);
                LocalDate yearEnd = LocalDate.of(previousYear, 12, 31);

                boolean hasPayrollsInYear = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd);

                if (!hasPayrollsInYear) {
                    log.info("ℹ️ Нет payrolls для компании {} за {} год",
                            company.getCompanyName(), previousYear);
                    skipCount++;
                    continue;
                }

                // Получаем админа компании
                User admin = adminsByCompanyId.get(company.getId());
                if (admin == null) {
                    log.warn("⚠️ Нет админов для компании {}, пропускаем генерацию Schedule A XML",
                            company.getCompanyName());
                    skipCount++;
                    continue;
                }

                // Генерируем Schedule A XML e-file
                String scheduleAXml = generateForm940ScheduleAXml.generateForm940ScheduleAXml(
                        admin.getId(), company.getId(), previousYear);

                if (!scheduleAXml.isEmpty()) {
                    // Отправляем email
                    reportsMailSender.sendEmail940FormAndScheduleA(company.getCompanyEmail());

                    log.info("✅ Form 940 Schedule A XML e-file сгенерен и отправлен для компании: {} (ID: {}) за {} год",
                            company.getCompanyName(), company.getId(), previousYear);
                    successCount++;
                } else {
                    log.info("ℹ️ Schedule A XML не требуется для компании: {}", company.getCompanyName());
                    skipCount++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации Schedule A XML для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Annual Form940ScheduleAXML Scheduler завершил работу за {} год. " +
                        "NY компании: {}, успешно: {}, пропущено: {}, с ошибками: {}, не-NY компании: {}",
                previousYear, nyCompanies.size(), successCount, skipCount, errorCount, notNyCount);
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

        // Вычисляем даты квартала
        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем FUTA Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли payrolls за квартал
                boolean hasPayrollsInQuarter = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), startDate, endDate);

                if (hasPayrollsInQuarter) {
                    // Генерируем квартальный FUTA отчет
                    FutaReportDTO reportData = futaReportService.generateQuarterlyFutaReport(
                            company.getId(), currentYear, completedQuarter);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

                    // Отправляем email
                    reportsMailSender.sendEmailQuarterFUTAReport(
                            company.getCompanyEmail());

                    log.info("✅ Quarterly FUTA Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), completedQuarter, currentYear, pdfBytes.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании: {} за Q{} {}",
                            company.getCompanyName(), completedQuarter, currentYear);
                    skipCount++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации quarterly FUTA report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Quarterly FUTA Report Scheduler завершил работу за Q{} {}. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                completedQuarter, currentYear, allCompanies.size(), successCount, skipCount, errorCount);
    }

// =============================================================================
// 📋 ANNUAL FUTA REPORTS
// =============================================================================

    /**
     * Генерирует годовые FUTA отчеты
     * Запускается 15 января в 9:00 утра (дедлайн для Form 940)
     */
    @Scheduled(cron = "0 0 9 15 1 *", zone = "America/New_York")
    public void generateAnnualFutaReports() {
        log.info("📄 Annual FUTA Report Scheduler запущен: генерируем annual FUTA reports за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1; // Годовой отчет генерируем за прошлый год

        log.info("📅 Генерируем Annual FUTA Reports за {} год", previousYear);

        // Даты за весь прошлый год
        LocalDate yearStart = LocalDate.of(previousYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(previousYear, 12, 31);

        List<Company> allCompanies = companyRepository.findAll();

        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : allCompanies) {
            try {
                // Проверяем, есть ли payrolls за прошлый год
                boolean hasPayrollsInYear = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd);

                if (hasPayrollsInYear) {
                    // Генерируем годовой FUTA отчет
                    FutaReportDTO reportData = futaReportService.generateAnnualFutaReport(
                            company.getId(), previousYear);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = futaReportPdfService.generateFutaReportPdf(reportData);

                    // Отправляем email
                    reportsMailSender.sendEmailAnnualFutaReport(
                            company.getCompanyEmail());

                    log.info("✅ Annual FUTA Report сгенерен и отправлен для компании: {} (ID: {}) за {} год, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), previousYear, pdfBytes.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании: {} за {} год",
                            company.getCompanyName(), previousYear);
                    skipCount++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации annual FUTA report для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
                errorCount++;
            }
        }

        log.info("🏁 Annual FUTA Report Scheduler завершил работу за {} год. " +
                        "Обработано: {}, успешно: {}, пропущено: {}, с ошибками: {}",
                previousYear, allCompanies.size(), successCount, skipCount, errorCount);
    }

// =============================================================================
// 📋 QUARTERLY FUTA COMPLIANCE CHECK
// =============================================================================

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

            // Даты текущего квартала
            LocalDate quarterStart = LocalDate.of(currentYear, (currentQuarter - 1) * 3 + 1, 1);
            LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);

            // Получаем только компании с payrolls в текущем квартале
            List<Company> companiesWithPayrolls = companyRepository.findAll().stream()
                    .filter(company -> workerPayrollRepository
                            .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), quarterStart, quarterEnd))
                    .toList();

            int totalCompanies = companiesWithPayrolls.size();
            int complianceIssues = 0;
            int checkErrors = 0;

            for (Company company : companiesWithPayrolls) {
                try {
                    // Генерируем текущий квартальный отчет для проверки
                    FutaReportDTO currentReport = futaReportService.generateQuarterlyFutaReport(
                            company.getId(), currentYear, currentQuarter);

                    // Проверяем compliance
                    if (!currentReport.getComplianceStatus() || currentReport.getNeedsPayment()) {
                        complianceIssues++;

                        log.warn("⚠️ COMPLIANCE ALERT: Компания {} (ID: {}) требует внимания по FUTA за Q{} {}. " +
                                        "Compliance: {}, Needs Payment: {}, Remaining Liability: ${}",
                                company.getCompanyName(), company.getId(), currentQuarter, currentYear,
                                currentReport.getComplianceStatus(), currentReport.getNeedsPayment(),
                                currentReport.getRemainingFutaLiability());

                        // Отправляем уведомление
                        reportsMailSender.sendEmailFutaCompliance(company.getCompanyEmail());
                    }

                } catch (Exception ex) {
                    log.error("❌ Ошибка проверки FUTA compliance для компании ID: {}", company.getId(), ex);
                    checkErrors++;
                }
            }

            log.info("📊 FUTA Compliance Check Summary: Проверено компаний: {}, Требуют внимания: {}, Ошибок: {}",
                    totalCompanies, complianceIssues, checkErrors);

        } else if (daysUntilDeadline <= 0) {
            log.warn("🚨 FUTA дедлайн просрочен на {} дней!", Math.abs(daysUntilDeadline));

            // Можно отправить критические уведомления всем компаниям с непогашенными обязательствами

        } else {
            log.info("✅ До FUTA дедлайна еще {} дней. Проверка не требуется.", daysUntilDeadline);
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

        // Даты всего года
        LocalDate yearStart = LocalDate.of(currentYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(currentYear, 12, 31);

        // Получаем только компании с payrolls в текущем году
        List<Company> companiesWithPayrolls = companyRepository.findAll().stream()
                .filter(company -> workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), yearStart, yearEnd))
                .toList();

        int totalCompanies = companiesWithPayrolls.size();
        int companiesWithIssues = 0;
        int perfectCompliance = 0;
        int checkErrors = 0;

        for (Company company : companiesWithPayrolls) {
            try {
                log.info("🔍 Проверяем все квартальные FUTA отчеты для компании: {} (ID: {})",
                        company.getCompanyName(), company.getId());

                // Проверяем все 4 квартала текущего года
                BigDecimal totalAnnualLiability = BigDecimal.ZERO;
                BigDecimal totalAnnualPaid = BigDecimal.ZERO;
                List<String> missingQuarters = new ArrayList<>();
                boolean hasAnyIssues = false;

                for (int quarter = 1; quarter <= 4; quarter++) {
                    try {
                        // Проверяем есть ли данные за квартал
                        LocalDate quarterStart = LocalDate.of(currentYear, (quarter - 1) * 3 + 1, 1);
                        LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);

                        if (workerPayrollRepository.existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
                                company.getId(), quarterStart, quarterEnd)) {

                            FutaReportDTO quarterlyReport = futaReportService.generateQuarterlyFutaReport(
                                    company.getId(), currentYear, quarter);

                            totalAnnualLiability = totalAnnualLiability.add(quarterlyReport.getTotalFutaTaxOwed());
                            totalAnnualPaid = totalAnnualPaid.add(quarterlyReport.getTotalFutaTaxPaid());

                            if (!quarterlyReport.getComplianceStatus()) {
                                missingQuarters.add("Q" + quarter);
                                hasAnyIssues = true;
                            }

                            log.info("✅ Q{} {}: Owed=${}, Paid=${}, Compliant={}",
                                    quarter, currentYear,
                                    quarterlyReport.getTotalFutaTaxOwed(),
                                    quarterlyReport.getTotalFutaTaxPaid(),
                                    quarterlyReport.getComplianceStatus());
                        }

                    } catch (Exception ex) {
                        log.error("❌ Ошибка проверки Q{} для компании {}", quarter, company.getId(), ex);
                        missingQuarters.add("Q" + quarter + " (ERROR)");
                        hasAnyIssues = true;
                    }
                }

                // Итоговый отчет по компании
                BigDecimal remainingLiability = totalAnnualLiability.subtract(totalAnnualPaid);

                log.info("📊 Итоги {} года для {}: Total Owed=${}, Total Paid=${}, Remaining=${}, Issues={}",
                        currentYear, company.getCompanyName(),
                        totalAnnualLiability, totalAnnualPaid, remainingLiability,
                        missingQuarters.isEmpty() ? "None" : String.join(", ", missingQuarters));

                if (!missingQuarters.isEmpty() || remainingLiability.compareTo(BigDecimal.ONE) > 0) {
                    companiesWithIssues++;
                    hasAnyIssues = true;

                    log.warn("⚠️ YEAR-END ALERT: Компания {} требует внимания перед концом года. " +
                                    "Проблемные кварталы: {}, Remaining Liability: ${}",
                            company.getCompanyName(),
                            missingQuarters.isEmpty() ? "None" : String.join(", ", missingQuarters),
                            remainingLiability);

                    // Отправка email уведомлений
                    reportsMailSender.sendEmailAnnualFutaReport(
                            company.getCompanyEmail());
                }

                if (!hasAnyIssues) {
                    perfectCompliance++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка year-end preparation для компании ID: {}", company.getId(), ex);
                checkErrors++;
            }
        }

        log.info("🏁 FUTA Year-End Preparation завершен за {} год. " +
                        "Проверено компаний: {}, С проблемами: {}, Полное соответствие: {}, Ошибок проверки: {}",
                currentYear, totalCompanies, companiesWithIssues, perfectCompliance, checkErrors);
    }



    /**
     * Генерирует квартальные SUTA отчеты
     * Запускается 20 числа каждого квартального месяца в 9:00 (после FUTA)
     */
    @Scheduled(cron = "0 0 9 20 1,4,7,10 *", zone = "America/New_York")
    public void generateQuarterlySutaReports() {
        log.info("📋 Quarterly SUTA Report Scheduler запущен: генерируем quarterly SUTA reports");

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
            log.info("ℹ️ Ошибка в логике quarterly SUTA scheduler. Текущий месяц: {}", currentMonth);
            return;
        }

        // Вычисляем даты квартала
        LocalDate startDate = LocalDate.of(currentYear, (completedQuarter - 1) * 3 + 1, 1);
        LocalDate endDate = startDate.plusMonths(3).minusDays(1);

        log.info("📅 Генерируем SUTA Reports за Q{} {} (период: {} - {})",
                completedQuarter, currentYear, startDate, endDate);

        // Получаем только NY компании
        List<Company> nyCompanies = companyRepository.findAll().stream()
                .filter(company -> "NY".equals(company.getCompanyState()))
                .toList();

        int totalNyCompanies = nyCompanies.size();
        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;

        for (Company company : nyCompanies) {
            try {
                // Проверяем, есть ли payrolls за квартал
                boolean hasPayrollsInQuarter = workerPayrollRepository
                        .existsByCompanyIdAndPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(company.getId(), startDate, endDate);

                if (hasPayrollsInQuarter) {
                    // Генерируем квартальный SUTA отчет
                    SutaReportDTO reportData = sutaReportService.generateQuarterlySutaReport(
                            company.getId(), currentYear, completedQuarter);

                    // Генерируем PDF и сохраняем в S3
                    byte[] pdfBytes = sutaReportPdfService.generateSutaReportPdf(reportData);

                    // Отправляем email
                    reportsMailSender.sendEmailQuarterSutaForm(
                            company.getCompanyEmail()
                    );

                    log.info("✅ Quarterly SUTA Report сгенерен и отправлен для компании: {} (ID: {}) за Q{} {}, размер PDF: {} bytes",
                            company.getCompanyName(), company.getId(), completedQuarter, currentYear, pdfBytes.length);

                    successCount++;
                } else {
                    log.info("ℹ️ Нет payrolls для компании: {} за Q{} {}",
                            company.getCompanyName(), completedQuarter, currentYear);
                    skipCount++;
                }

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации quarterly SUTA report для компании ID: {} за Q{} {}",
                        company.getId(), completedQuarter, currentYear, ex);
                errorCount++;
            }
        }

        long notNyCompanies = companyRepository.count() - totalNyCompanies;

        log.info("🏁 Quarterly SUTA Report Scheduler завершил работу за Q{} {}. " +
                        "NY компании: {}, успешно: {}, пропущено: {}, с ошибками: {}, не-NY компании: {}",
                completedQuarter, currentYear, totalNyCompanies, successCount, skipCount, errorCount, notNyCompanies);
    }
    /**
     * Генерирует годовые SUTA отчеты
     * Запускается 15 января в 10:00 утра (после FUTA, дедлайн для NYS-45)
     */
    @Scheduled(cron = "0 0 10 15 1 *", zone = "America/New_York")
    public void generateAnnualSutaReports() {
        log.info("📄 Annual SUTA Report Scheduler запущен: генерируем annual SUTA reports за прошлый год");

        LocalDate today = LocalDate.now();
        int previousYear = today.getYear() - 1;

        // 1. Берём все компании
        List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            log.info("ℹ️ Нет компаний для обработки");
            return;
        }

        // 2. Собираем их ID и одним запросом вытаскиваем всех сотрудников
        List<Integer> companyIds = companies.stream()
                .map(Company::getId)
                .toList();
        List<User> allEmployees = userRepository.findAllByCompanyIdIn(companyIds);

        // 3. Группируем сотрудников по компании
        Map<Integer, List<User>> employeesByCompany = allEmployees.stream()
                .collect(Collectors.groupingBy(u -> u.getCompany().getId()));

        // 4. Проходим по компаниям
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
                log.info("📧 Email с Annual SUTA Report отправлен компании: {}", company.getCompanyEmail());

            } catch (Exception ex) {
                log.error("❌ Ошибка генерации или отправки annual SUTA report для компании ID: {} за {} год",
                        company.getId(), previousYear, ex);
            }
        }

        log.info("🏁 Annual SUTA Report Scheduler завершил работу за {} год", previousYear);
    }











    private boolean shouldCreateEmployerTaxRecord(WorkerPayroll payroll, Company company) {
        if (payroll == null || company == null || company.getCompanyPaymentPosition() == null) {
            return false;
        }

        return LocalDate.now().isEqual(payroll.getPeriodEnd());
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
