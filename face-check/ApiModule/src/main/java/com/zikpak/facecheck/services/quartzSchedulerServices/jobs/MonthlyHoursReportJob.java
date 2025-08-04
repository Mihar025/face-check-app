package com.zikpak.facecheck.services.quartzSchedulerServices.jobs;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.security.mailServiceForReports.ReportsMailSender;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.BaseSchedulerJob;
import com.zikpak.facecheck.services.quartzSchedulerServices.services.JobResult;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDTO;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportDataService;
import com.zikpak.facecheck.taxesServices.customReportsForCompanys.hoursReport.HoursReportPdfService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MonthlyHoursReportJob extends BaseSchedulerJob {
    @Autowired
    private WorkerAttendanceRepository workerAttendanceRepository;

    @Autowired
    private HoursReportDataService hoursReportDataService;

    @Autowired
    private HoursReportPdfService hoursReportPdfService;

    @Autowired
    private ReportsMailSender reportsMailSender;
    @Override
    protected JobResult executeJob(JobExecutionContext context) throws Exception {
        log.info("⏰ Monthly HoursReportScheduler запущен: генерируем месячные отчеты по часам");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.minusMonths(1).withDayOfMonth(
                today.minusMonths(1).lengthOfMonth());
        LocalDate startDate = endDate.withDayOfMonth(1);

        log.info("📅 Генерируем отчеты по часам за период: {} - {}", startDate, endDate);

        // ОДИН запрос для всех attendance за месяц
        List<WorkerAttendance> allMonthlyAttendances = workerAttendanceRepository
                .findAllByCheckInTimeBetween(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        // Группируем по компаниям
        Map<Company, List<WorkerAttendance>> attendancesByCompany = allMonthlyAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getWorker().getCompany()));

        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();

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

                totalSuccess++;
            } catch (Exception ex) {
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
                log.error("❌ Ошибка генерации monthly hours report для компании ID: {}", company.getId(), ex);
                totalFailure++;
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
