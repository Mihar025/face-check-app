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
public class WeeklyHoursReportJob extends BaseSchedulerJob {

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
        log.info("⏰ Weekly HoursReportScheduler запущен: генерируем еженедельные отчеты по часам");

        LocalDate endDate = LocalDate.now().minusDays(1); // суббота
        LocalDate startDate = endDate.minusDays(6); // воскресенье

        log.info("📅 Генерируем отчеты по часам за период: {} - {}", startDate, endDate);

        // ОДИН запрос для всех attendance за неделю
        List<WorkerAttendance> allWeeklyAttendances = workerAttendanceRepository
                .findAllByCheckInTimeBetween(
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)
                );

        // Группируем по компаниям через работников
        Map<Company, List<WorkerAttendance>> attendancesByCompany = allWeeklyAttendances.stream()
                .collect(Collectors.groupingBy(att -> att.getWorker().getCompany()));

        int totalSuccess = 0;
        int totalFailure = 0;
        StringBuilder errors = new StringBuilder();

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
                totalSuccess++;


                log.info("✅ Weekly hours report сгенерен для компании: {} (ID: {}, attendances: {})",
                        company.getCompanyName(), company.getId(), companyAttendances.size());
            } catch (Exception ex) {
                totalFailure++;
                errors.append("Company ID ").append(company.getId())
                        .append(": ").append(ex.getMessage()).append("\n");
            }
        }
        if (totalFailure == 0 && errors.length() == 0) {
            return JobResult.success(totalSuccess);
        } else if (totalSuccess > 0) {
            return JobResult.partialSuccess(totalSuccess, totalFailure, errors.toString());
        } else {
            return JobResult.failure(errors.toString());
        }
    }

}
