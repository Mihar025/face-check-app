package com.zikpak.facecheck.TestDataForGeneratingData.test1;

import com.zikpak.facecheck.TestDataForGeneratingData.TestServiceForCompany;
import com.zikpak.facecheck.TestDataForGeneratingData.UserTestServiceData;
import com.zikpak.facecheck.TestDataForGeneratingData.WorkSiteTestService;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.helperServices.WorkerPayRollService;
import com.zikpak.facecheck.repository.*;
import com.zikpak.facecheck.services.workAttendanceService.PunchType;
import com.zikpak.facecheck.services.workAttendanceService.UpdatePunchForWorkerRequest;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
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
import com.zikpak.facecheck.taxesServices.efiles.csvReports.*;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form940ScheduleAXmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form940XmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form941ScheduleBXmlGenerator;
import com.zikpak.facecheck.taxesServices.efiles.xml.Form941XmlGenerator;
import com.zikpak.facecheck.taxesServices.pdfServices.FillForm940SA;
import com.zikpak.facecheck.taxesServices.pdfServices.Form940PdfGeneratorService;
import com.zikpak.facecheck.taxesServices.scheduler.EmployerTaxScheduler;
import com.zikpak.facecheck.taxesServices.services.EmployerTaxService;
import com.zikpak.facecheck.taxesServices.services.PayStubService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcReportPdfGeneratorService;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.WcRiskServiceForPDF;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollTestDataGenerator {

    private final TestServiceForCompany testServiceForCompany;
    private final UserTestServiceData userTestServiceData;
    private final WorkSiteTestService workSiteTestService;
    private final WorkAttendanceService workAttendanceService;
    private final EmployerTaxScheduler employerTaxScheduler;
    private final EmployerTaxService employerTaxService;

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final WorkerSiteRepository workSiteRepository;
    private final WorkerAttendanceRepository attendanceRepository;
    private final WorkerPayrollRepository payrollRepository;
    private final WorkerScheduleRepository scheduleRepository;
    private final RoleRepository roleRepository;

    // Основные сервисы
    private final PayStubService payStubService;
    private final WorkerPayRollService workerPayRollService;

    // Отчетные сервисы
    private final PayrollSummaryDataService payrollSummaryDataService;
    private final PayrollSummaryReportService payrollSummaryReportService;
    private final HoursReportDataService hoursReportDataService;
    private final HoursReportPdfService hoursReportPdfService;
    private final TaxSummaryDataService taxSummaryDataService;
    private final TaxSummaryPdfService taxSummaryPdfService;

    // CSV сервисы
    private final HoursReportCsvService hoursReportCsvService;
    private final PayrollSummaryReportCsvService payrollSummaryReportCsvService;
    private final TaxSummaryReportCsvService taxSummaryReportCsvService;

    // XML генераторы
    private final Form941XmlGenerator form941XmlGenerator;
    private final Form941ScheduleBXmlGenerator form941ScheduleBXmlGenerator;
    private final EFW2GeneratorService efw2GeneratorService;
    private final Form940PdfGeneratorService form940PdfGeneratorService;
    private final FillForm940SA fillForm940SA;
    private final Form940XmlGenerator form940XmlGenerator;
    private final Form940ScheduleAXmlGenerator form940ScheduleAXmlGenerator;

    // FUTA/SUTA сервисы
    private final FutaReportService futaReportService;
    private final FutaReportPdfService futaReportPdfService;
    private final SutaReportService sutaReportService;
    private final SutaReportPdfService sutaReportPdfService;

    private final WcRiskServiceForPDF wcReportPdfGeneratorService;


    // Константы для генерации
    private static final int YEAR = 2025;
    private static final LocalDate START_DATE = LocalDate.of(YEAR, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(YEAR, 12, 31);


    public void generateFullYearData() {
        log.info("🚀🚀🚀 НАЧИНАЕМ ПОЛНУЮ ГЕНЕРАЦИЮ ДАННЫХ ЗА {} ГОД 🚀🚀🚀", YEAR);

        try {
            // 1. Создаем базовые данные
            log.info("=== ШАГ 1: СОЗДАНИЕ БАЗОВЫХ ДАННЫХ ===");
            setupBaseData();

            // 2. Связываем работников с площадками
            log.info("=== ШАГ 2: СВЯЗЫВАНИЕ РАБОТНИКОВ С ПЛОЩАДКАМИ ===");
            linkWorkersToWorkSites();

            // 3. Генерируем посещаемость за весь год
            log.info("=== ШАГ 3: ГЕНЕРАЦИЯ ПОСЕЩАЕМОСТИ ЗА ВЕСЬ ГОД ===");
            generateYearlyAttendance();

            // 4. Рассчитываем employer taxes для всех payrolls
            log.info("=== ШАГ 4: РАСЧЕТ EMPLOYER TAXES ===");
            calculateAllEmployerTaxes();

            // 5. Генерируем PayStubs для всех payrolls
            log.info("=== ШАГ 5: ГЕНЕРАЦИЯ PAYSTUBS ===");
            generateAllPayStubs();

            // 6. Генерируем W-2 формы
            log.info("=== ШАГ 6: ГЕНЕРАЦИЯ W-2 ФОРМ ===");
            generateAllW2Forms();

            // 7. Генерируем недельные отчеты
            log.info("=== ШАГ 7: ГЕНЕРАЦИЯ НЕДЕЛЬНЫХ ОТЧЕТОВ ===");
            generateAllWeeklyReports();

            // 8. Генерируем месячные отчеты
            log.info("=== ШАГ 8: ГЕНЕРАЦИЯ МЕСЯЧНЫХ ОТЧЕТОВ ===");
            generateAllMonthlyReports();

            // 9. Генерируем квартальные отчеты
            log.info("=== ШАГ 9: ГЕНЕРАЦИЯ КВАРТАЛЬНЫХ ОТЧЕТОВ ===");
            generateAllQuarterlyReports();

            // 10. Генерируем годовые отчеты
            log.info("=== ШАГ 10: ГЕНЕРАЦИЯ ГОДОВЫХ ОТЧЕТОВ ===");
            generateAllAnnualReports();

            log.info("✅✅✅ ГЕНЕРАЦИЯ ЗАВЕРШЕНА УСПЕШНО! ВСЕ ДАННЫЕ И ОТЧЕТЫ СОЗДАНЫ! ✅✅✅");

        } catch (Exception e) {
            log.error("❌❌❌ КРИТИЧЕСКАЯ ОШИБКА ПРИ ГЕНЕРАЦИИ ДАННЫХ ❌❌❌", e);
            throw new RuntimeException("Генерация провалилась", e);
        }
    }

    // ==================== ШАГ 1: БАЗОВЫЕ ДАННЫЕ ====================
    private void setupBaseData() {
        log.info("📋 Создаем компании, админов и работников...");

        // Админы
        var admin1 = userTestServiceData.createAdmin1();
        var admin2 = userTestServiceData.createAdmin2();

        // Компании
        var company1 = testServiceForCompany.createCompany1();
        var company2 = testServiceForCompany.createCompany2();

        company1.setCompanyOwner(admin1);
        company2.setCompanyOwner(admin2);

        admin1.setCompany(company1);
        admin1.setOwnedCompany(company1);
        admin2.setCompany(company2);
        admin2.setOwnedCompany(company2);

        userRepository.save(admin1);
        userRepository.save(admin2);
        companyRepository.save(company1);
        companyRepository.save(company2);

        // Работники для компании 1
        userTestServiceData.createWorker1ForCompany1(company1.getId());
        userTestServiceData.createWorker2ForCompany1(company1.getId());
        userTestServiceData.createWorker3ForCompany1(company1.getId());
        userTestServiceData.createWorker4ForCompany1(company1.getId());
        userTestServiceData.createWorker5ForCompany1(company1.getId());

        // Работники для компании 2
        userTestServiceData.createWorker1ForCompany2(company2.getId());
        userTestServiceData.createWorke2ForCompany2(company2.getId());
        userTestServiceData.createWorker3ForCompany2(company2.getId());
        userTestServiceData.createWorker4ForCompany2(company2.getId());
        userTestServiceData.createWorker5ForCompany2(company2.getId());

        // Рабочие площадки
        workSiteTestService.createWorkSiteForCompany1(company1.getId());
        workSiteTestService.createWorkSiteForCompany2(company2.getId());

        log.info("✅ Базовые данные созданы: 2 компании, 2 админа, 10 работников, 2 площадки");
    }

    // ==================== ШАГ 2: СВЯЗЫВАНИЕ ====================
    private void linkWorkersToWorkSites() {
        List<WorkSite> allWorkSites = workSiteRepository.findAll();
        List<User> allWorkers = userRepository.findAll().stream()
                .filter(u -> u.isUser() && !u.isBusinessOwner())
                .toList();

        for (User worker : allWorkers) {
            WorkSite workSite = allWorkSites.stream()
                    .filter(ws -> ws.getCompany().getId().equals(worker.getCompany().getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("WorkSite not found for company"));

            worker.getWorkSites().add(workSite);
            workSite.getUsers().add(worker);

            userRepository.save(worker);
            workSiteRepository.save(workSite);
        }

        log.info("✅ Работники связаны с площадками");
    }

    // ==================== ШАГ 3: ПОСЕЩАЕМОСТЬ ====================
    private void generateYearlyAttendance() {
        List<User> allWorkers = userRepository.findAll().stream()
                .filter(u -> u.isUser() && !u.isBusinessOwner())
                .toList();

        log.info("👥 Генерируем посещаемость для {} работников за весь {} год", allWorkers.size(), YEAR);

        for (User worker : allWorkers) {
            generateAttendanceForWorker(worker);
        }

        log.info("✅ Посещаемость сгенерирована");
    }

    private void generateAttendanceForWorker(User worker) {
        WorkSite workSite = worker.getWorkSites().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("WorkSite not found"));

        LocalDate currentDate = START_DATE;
        Random random = new Random();

        while (!currentDate.isAfter(END_DATE)) {
            // Пропускаем выходные
            if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            // Проверяем расписание
            WorkerSchedule schedule = scheduleRepository
                    .findByWorkerAndScheduleDate(worker, currentDate)
                    .orElse(null);

            if (schedule == null) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            // Генерируем рабочий день
            double probability = random.nextDouble();
            if (probability < 0.85) {
                createNormalWorkDay(worker, workSite, currentDate, schedule);
            } else if (probability < 0.95) {
                createOvertimeDay(worker, workSite, currentDate, schedule);
            } else if (probability < 0.98) {
                createLateDay(worker, workSite, currentDate, schedule);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    private void createNormalWorkDay(User worker, WorkSite workSite, LocalDate date, WorkerSchedule schedule) {
        try {
            LocalTime scheduledStart = schedule.getExpectedStartTime();
            LocalTime punchInTime = scheduledStart.plusMinutes(
                    ThreadLocalRandom.current().nextInt(-10, 10)
            );

            UpdatePunchForWorkerRequest punchInRequest = new UpdatePunchForWorkerRequest();
            punchInRequest.setNewPunchDate(date);
            punchInRequest.setNewPunchTime(punchInTime);
            punchInRequest.setPunchType(PunchType.PUNCH_IN);
            punchInRequest.setWorkSiteId(workSite.getId());

            workAttendanceService.updatePunchForWorker(worker.getId(), punchInRequest);

            LocalTime scheduledEnd = schedule.getExpectedEndTime();
            LocalTime punchOutTime = scheduledEnd.plusMinutes(
                    ThreadLocalRandom.current().nextInt(-10, 20)
            );

            UpdatePunchForWorkerRequest punchOutRequest = new UpdatePunchForWorkerRequest();
            punchOutRequest.setNewPunchDate(date);
            punchOutRequest.setNewPunchTime(punchOutTime);
            punchOutRequest.setPunchType(PunchType.PUNCH_OUT);
            punchOutRequest.setWorkSiteId(workSite.getId());

            workAttendanceService.updatePunchForWorker(worker.getId(), punchOutRequest);

        } catch (Exception e) {
            log.error("Ошибка создания рабочего дня для {} на {}", worker.getId(), date, e);
        }
    }

    private void createOvertimeDay(User worker, WorkSite workSite, LocalDate date, WorkerSchedule schedule) {
        try {
            LocalTime punchInTime = schedule.getExpectedStartTime().minusMinutes(30);
            UpdatePunchForWorkerRequest punchInRequest = new UpdatePunchForWorkerRequest();
            punchInRequest.setNewPunchDate(date);
            punchInRequest.setNewPunchTime(punchInTime);
            punchInRequest.setPunchType(PunchType.PUNCH_IN);
            punchInRequest.setWorkSiteId(workSite.getId());

            workAttendanceService.updatePunchForWorker(worker.getId(), punchInRequest);

            LocalTime punchOutTime = schedule.getExpectedEndTime().plusHours(2);
            UpdatePunchForWorkerRequest punchOutRequest = new UpdatePunchForWorkerRequest();
            punchOutRequest.setNewPunchDate(date);
            punchOutRequest.setNewPunchTime(punchOutTime);
            punchOutRequest.setPunchType(PunchType.PUNCH_OUT);
            punchOutRequest.setWorkSiteId(workSite.getId());

            workAttendanceService.updatePunchForWorker(worker.getId(), punchOutRequest);

        } catch (Exception e) {
            log.error("Ошибка создания овертайма для {} на {}", worker.getId(), date, e);
        }
    }

    private void createLateDay(User worker, WorkSite workSite, LocalDate date, WorkerSchedule schedule) {
        try {
            LocalTime punchInTime = schedule.getExpectedStartTime().plusMinutes(30);
            UpdatePunchForWorkerRequest punchInRequest = new UpdatePunchForWorkerRequest();
            punchInRequest.setNewPunchDate(date);
            punchInRequest.setNewPunchTime(punchInTime);
            punchInRequest.setPunchType(PunchType.PUNCH_IN);
            punchInRequest.setWorkSiteId(workSite.getId());

            workAttendanceService.updatePunchForWorker(worker.getId(), punchInRequest);

            LocalTime punchOutTime = schedule.getExpectedEndTime();
            UpdatePunchForWorkerRequest punchOutRequest = new UpdatePunchForWorkerRequest();
            punchOutRequest.setNewPunchDate(date);
            punchOutRequest.setNewPunchTime(punchOutTime);
            punchOutRequest.setPunchType(PunchType.PUNCH_OUT);
            punchOutRequest.setWorkSiteId(workSite.getId());

            workAttendanceService.updatePunchForWorker(worker.getId(), punchOutRequest);

        } catch (Exception e) {
            log.error("Ошибка создания опоздания для {} на {}", worker.getId(), date, e);
        }
    }

    // ==================== ШАГ 4: EMPLOYER TAXES ====================
    private void calculateAllEmployerTaxes() {
        List<WorkerPayroll> allPayrolls = payrollRepository.findAll();
        log.info("💰 Рассчитываем employer taxes для {} payrolls", allPayrolls.size());

        int calculated = 0;
        for (WorkerPayroll payroll : allPayrolls) {
            if (!payroll.isEmployerTaxesCalculated()) {
                try {
                    employerTaxService.calculateAndSaveEmployerTaxes(payroll);
                    payroll.setEmployerTaxesCalculated(true);
                    payrollRepository.save(payroll);
                    calculated++;
                } catch (Exception e) {
                    log.error("Ошибка расчета налогов для payroll ID: {}", payroll.getId(), e);
                }
            }
        }

        log.info("✅ Рассчитано employer taxes для {} payrolls", calculated);
    }

    // ==================== ШАГ 5: PAYSTUBS ====================
    // ==================== ШАГ 5: PAYSTUBS ====================
    private void generateAllPayStubs() {
        List<WorkerPayroll> allPayrolls = payrollRepository.findAll();
        log.info("📄 Генерируем PayStubs для {} payrolls", allPayrolls.size());

        int generated = 0;
        for (WorkerPayroll payroll : allPayrolls) {
            if (!Boolean.TRUE.equals(payroll.getPayStubGenerated())) {
                try {
                    payStubService.generatePayStubPdf(payroll.getId());
                    payroll.setPayStubGenerated(true);
                    payrollRepository.save(payroll);
                    generated++;

                    if (generated % 50 == 0) {
                        log.info("Прогресс: {} PayStubs сгенерировано", generated);
                    }
                } catch (Exception e) {
                    log.error("Ошибка генерации PayStub для payroll ID: {}", payroll.getId(), e);
                }
            }
        }

        log.info("✅ Сгенерировано {} PayStubs", generated);
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    private User findCompanyAdmin(Integer companyId) {
        List<User> admins = userRepository.findAllByCompanyId(companyId).stream()
                .filter(user -> user.getRoles() != null &&
                        user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName())))
                .toList();

        return admins.isEmpty() ? null : admins.get(0);
    }


    public void printFinalStatistics() {
        log.info("\n\n==================== ФИНАЛЬНАЯ СТАТИСТИКА ====================");

        // Компании
        long companiesCount = companyRepository.count();
        log.info("📊 Компаний создано: {}", companiesCount);

        // Работники
        long workersCount = userRepository.findAll().stream()
                .filter(u -> u.isUser() && !u.isBusinessOwner())
                .count();
        log.info("👥 Работников создано: {}", workersCount);

        // Посещаемость
        long attendanceCount = attendanceRepository.count();
        log.info("🕐 Записей посещаемости: {}", attendanceCount);

        // Payrolls
        long payrollsCount = payrollRepository.count();
        log.info("💰 Payroll записей: {}", payrollsCount);

        // PayStubs
        long payStubsGenerated = payrollRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getPayStubGenerated()))
                .count();
        log.info("📄 PayStubs сгенерировано: {}", payStubsGenerated);

        // Employer Taxes
        long taxesCalculated = payrollRepository.findAll().stream()
                .filter(WorkerPayroll::isEmployerTaxesCalculated)
                .count();
        log.info("💸 Employer taxes рассчитано: {}", taxesCalculated);

        log.info("===============================================================\n\n");
    }

    // ==================== ШАГ 6: W-2 ФОРМЫ ====================
    private void generateAllW2Forms() {
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            List<User> workers = userRepository.findAllByCompanyId(company.getId()).stream()
                    .filter(u -> !u.isBusinessOwner())
                    .toList();

            log.info("📄 Генерируем W-2 для {} работников компании {}", workers.size(), company.getCompanyName());

            for (User worker : workers) {
                try {
                    workerPayRollService.generatePDF(worker.getId(), YEAR);
                } catch (Exception e) {
                    log.error("Ошибка генерации W-2 для worker ID: {}", worker.getId(), e);
                }
            }
        }

        log.info("✅ W-2 формы сгенерированы");
    }

    // ==================== ШАГ 7: НЕДЕЛЬНЫЕ ОТЧЕТЫ ====================
    private void generateAllWeeklyReports() {
        LocalDate currentWeekStart = START_DATE.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        List<Company> companies = companyRepository.findAll();

        int weekCount = 0;
        while (currentWeekStart.plusDays(6).isBefore(END_DATE.plusDays(1))) {
            LocalDate weekEnd = currentWeekStart.plusDays(6);
            weekCount++;

            for (Company company : companies) {
                try {
                    // Payroll Summary Report
                    PayrollSummaryReportDTO payrollData = payrollSummaryDataService
                            .generatePayrollSummaryData(company.getId(), currentWeekStart, weekEnd);
                    payrollSummaryReportService.generatePayrollSummaryReport(payrollData);

                    // Hours Report
                    HoursReportDTO hoursData = hoursReportDataService
                            .generateHoursReportData(company.getId(), currentWeekStart, weekEnd);
                    hoursReportPdfService.generateHoursReport(hoursData, company.getId());

                } catch (Exception e) {
                    log.error("Ошибка недельных отчетов для компании {} за неделю {}",
                            company.getId(), currentWeekStart, e);
                }
            }

            currentWeekStart = currentWeekStart.plusWeeks(1);
        }

        log.info("✅ Сгенерировано недельных отчетов за {} недель", weekCount);
    }

    // ==================== ШАГ 8: МЕСЯЧНЫЕ ОТЧЕТЫ ====================
    private void generateAllMonthlyReports() {
        List<Company> companies = companyRepository.findAll();

        for (int month = 1; month <= 12; month++) {
            LocalDate monthStart = LocalDate.of(YEAR, month, 1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            log.info("📊 Генерируем месячные отчеты за {}/{}", month, YEAR);

            for (Company company : companies) {
                try {
                    // Payroll Summary Report + CSV
                    PayrollSummaryReportDTO payrollData = payrollSummaryDataService
                            .generatePayrollSummaryData(company.getId(), monthStart, monthEnd);
                    payrollSummaryReportService.generatePayrollSummaryReport(payrollData);
                    payrollSummaryReportCsvService.generatePayrollSummaryReportCsv(payrollData, company.getId());

                    // Hours Report + CSV
                    HoursReportDTO hoursData = hoursReportDataService
                            .generateHoursReportData(company.getId(), monthStart, monthEnd);
                    hoursReportPdfService.generateHoursReport(hoursData, company.getId());
                    hoursReportCsvService.generateHoursReportCsv(hoursData, company.getId());
                    wcReportPdfGeneratorService.generateWcReportPdf(company.getId(), monthStart, monthEnd);
                } catch (Exception e) {
                    log.error("Ошибка месячных отчетов для компании {} за {}/{}",
                            company.getId(), month, YEAR, e);
                }
            }
        }

        log.info("✅ Месячные отчеты сгенерированы за весь год");
    }

    // ==================== ШАГ 9: КВАРТАЛЬНЫЕ ОТЧЕТЫ ====================
    private void generateAllQuarterlyReports() {
        List<Company> companies = companyRepository.findAll();

        for (int quarter = 1; quarter <= 4; quarter++) {
            LocalDate quarterStart = LocalDate.of(YEAR, (quarter - 1) * 3 + 1, 1);
            LocalDate quarterEnd = quarterStart.plusMonths(3).minusDays(1);

            log.info("📋 Генерируем квартальные отчеты за Q{} {}", quarter, YEAR);

            for (Company company : companies) {
                try {
                    // Tax Summary Report + CSV
                    TaxSummaryReportDTO taxData = taxSummaryDataService
                            .generateTaxSummaryReport(company.getId(), quarterStart, quarterEnd);
                    taxSummaryPdfService.generateTaxSummaryReport(taxData);
                    taxSummaryReportCsvService.generateTaxSummaryReportCsv(taxData, company.getId());

                    // FUTA Report
                    FutaReportDTO futaData = futaReportService
                            .generateQuarterlyFutaReport(company.getId(), YEAR, quarter);
                    futaReportPdfService.generateFutaReportPdf(futaData);

                    // SUTA Report (только для NY)
                    if ("NY".equals(company.getCompanyState())) {
                        SutaReportDTO sutaData = sutaReportService
                                .generateQuarterlySutaReport(company.getId(), YEAR, quarter);
                        sutaReportPdfService.generateSutaReportPdf(sutaData);
                    }

                    // Form 941 XML (если есть админ)
                    User admin = findCompanyAdmin(company.getId());
                    if (admin != null) {
                        form941XmlGenerator.generateForm941Xml(admin.getId(), company.getId(), YEAR, quarter);
                        form941ScheduleBXmlGenerator.generateForm941ScheduleBXml(admin.getId(), company.getId(), YEAR, quarter);
                    }

                } catch (Exception e) {
                    log.error("Ошибка квартальных отчетов для компании {} за Q{}",
                            company.getId(), quarter, e);
                }
            }
        }

        log.info("✅ Квартальные отчеты сгенерированы за весь год");
    }

    // ==================== ШАГ 10: ГОДОВЫЕ ОТЧЕТЫ ====================
    private void generateAllAnnualReports() {
        List<Company> companies = companyRepository.findAll();

        log.info("📄 Генерируем годовые отчеты за {} год", YEAR);

        for (Company company : companies) {
            try {
                // FUTA Annual Report
                FutaReportDTO futaData = futaReportService.generateAnnualFutaReport(company.getId(), YEAR);
                futaReportPdfService.generateFutaReportPdf(futaData);

                // SUTA Annual Report (только для NY)
                if ("NY".equals(company.getCompanyState())) {
                    SutaReportDTO sutaData = sutaReportService.generateAnnualSutaReport(company.getId(), YEAR);
                    sutaReportPdfService.generateSutaReportPdf(sutaData);
                }

                // Form 940 PDF
                form940PdfGeneratorService.generate940Pdf(company.getId(), YEAR);
                fillForm940SA.generateFilledPdf(company.getId(), YEAR);

                // Form 940 XML (если есть админ)
                User admin = findCompanyAdmin(company.getId());
                if (admin != null) {
                    form940XmlGenerator.generateForm940Xml(admin.getId(), company.getId(), YEAR);

                    if ("NY".equals(company.getCompanyState())) {
                        form940ScheduleAXmlGenerator.generateForm940ScheduleAXml(admin.getId(), company.getId(), YEAR);
                    }
                }

                // EFW2
                efw2GeneratorService.generateEfw2File(company.getId(), YEAR);

                log.info("✅ Годовые отчеты созданы для компании: {}", company.getCompanyName());

            } catch (Exception e) {
                log.error("Ошибка годовых отчетов для компании {}", company.getId(), e);
            }
        }

        log.info("✅ Все годовые отчеты сгенерированы");
    }

}