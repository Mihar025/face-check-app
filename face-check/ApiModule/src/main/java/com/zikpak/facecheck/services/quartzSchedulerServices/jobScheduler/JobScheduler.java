package com.zikpak.facecheck.services.quartzSchedulerServices.jobScheduler;

import com.zikpak.facecheck.services.quartzSchedulerServices.jobs.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobScheduler {

    private final Scheduler scheduler;

    @PostConstruct
    public void scheduleJobs() {
        try {
            scheduleWeeklyEmployerTaxes();
            scheduleWeeklyPayStubJob();
            scheduleBIWeeklyPayStubJob();
            scheduleW2OfficialForms();
            scheduleWeeklyPayrollReport();
            scheduleMonthlyPayrollReport();
            scheduleWeeklyHoursReport();
            scheduleMonthlyHoursReport();
            scheduleQuarterlyTaxSummaryReportReport();
            scheduleW3Job();
            scheduleMTA305Job();
            scheduleAnnualSutaReportJob();
            scheduleQuarterlySutaReportJob();
            scheduleFutaYearEndReportsJob();
            //check again this:
            scheduleAnnualFutaReportsJob();
            ////
            scheduleFutaQuarterlyComplienceJob();
            scheduleQuarterlyFutaReportsJob();

        } catch (SchedulerException e) {
            log.error("❌ Failed to schedule jobs", e);
        }
    }

    private void scheduleQuarterlyFutaReportsJob() throws SchedulerException {
        JobDetail quarterFutaEndJob = JobBuilder.newJob(QuarterlyFutaReportsJob.class)
                .withIdentity("futaQuarterlyReportJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger quarterFutaEndTrigger = TriggerBuilder.newTrigger()
                .withIdentity("futaQuarterlyReportTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 8 15 1,4,7,10 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(quarterFutaEndJob.getKey())) {
            scheduler.deleteJob(quarterFutaEndJob.getKey());
        }

        scheduler.scheduleJob(quarterFutaEndJob, quarterFutaEndTrigger);

        log.info("✅ Futa Quarterly Report Job scheduled successfully");
    }

    private void scheduleFutaQuarterlyComplienceJob() throws SchedulerException {
        JobDetail yearFutaEndJob = JobBuilder.newJob(CheckQuarterlyFutaComplienceJob.class)
                .withIdentity("futaQuarterlyComplienceJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger yearFutaEndTrigger = TriggerBuilder.newTrigger()
                .withIdentity("futaQuarterlyComplienceTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 6 ? * MON")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(yearFutaEndJob.getKey())) {
            scheduler.deleteJob(yearFutaEndJob.getKey());
        }

        scheduler.scheduleJob(yearFutaEndJob, yearFutaEndTrigger);

        log.info("✅ Futa Quarterly Complience Job scheduled successfully");
    }

    private void scheduleAnnualFutaReportsJob() throws SchedulerException {
        JobDetail yearFutaEndJob = JobBuilder.newJob(GenerateAnnualFutaReports.class)
                .withIdentity("annualFutaReportsJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger yearFutaEndTrigger = TriggerBuilder.newTrigger()
                .withIdentity("annualFutaReportsTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 9 15 1 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(yearFutaEndJob.getKey())) {
            scheduler.deleteJob(yearFutaEndJob.getKey());
        }

        scheduler.scheduleJob(yearFutaEndJob, yearFutaEndTrigger);

        log.info("✅ Annual Futa Report Job scheduled successfully");
    }

    private void scheduleFutaYearEndReportsJob() throws SchedulerException {
        JobDetail yearFutaEndJob = JobBuilder.newJob(FutaYearEndReportJob.class)
                .withIdentity("yearFutaEndJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger yearFutaEndTrigger = TriggerBuilder.newTrigger()
                .withIdentity("yearFutaEndTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 10 15 12 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(yearFutaEndJob.getKey())) {
            scheduler.deleteJob(yearFutaEndJob.getKey());
        }

        scheduler.scheduleJob(yearFutaEndJob, yearFutaEndTrigger);

        log.info("✅ Year Futa End Job scheduled successfully");
    }


    private void scheduleQuarterlySutaReportJob() throws SchedulerException {
        JobDetail quarterlySutaJob = JobBuilder.newJob(QuarterlySutaReportsJob.class)
                .withIdentity("quarterlySutaReportJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger quarterlySutaTrigger = TriggerBuilder.newTrigger()
                .withIdentity("quarterlySutaReportTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 9 20 1,4,7,10 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(quarterlySutaJob.getKey())) {
            scheduler.deleteJob(quarterlySutaJob.getKey());
        }

        scheduler.scheduleJob(quarterlySutaJob, quarterlySutaTrigger);

        log.info("✅ quarterly Suta  Job scheduled successfully");
    }

    private void scheduleAnnualSutaReportJob() throws SchedulerException {
        JobDetail quarterlySutaJob = JobBuilder.newJob(AnnualSuatReportsJob.class)
                .withIdentity("annualSutaReportJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger quarterlySutaTrigger = TriggerBuilder.newTrigger()
                .withIdentity("annualSutaReportTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 10 15 1 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(quarterlySutaJob.getKey())) {
            scheduler.deleteJob(quarterlySutaJob.getKey());
        }

        scheduler.scheduleJob(quarterlySutaJob, quarterlySutaTrigger);

        log.info("✅ Annual Suta  Job scheduled successfully");
    }



    private void scheduleMTA305Job() throws SchedulerException {
        JobDetail mta305Job = JobBuilder.newJob(GenerateMTA305Job.class)
                .withIdentity("mta305Job", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger mta305Trigger = TriggerBuilder.newTrigger()
                .withIdentity("mta305Trigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 11 25 1,4,7,10 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(mta305Job.getKey())) {
            scheduler.deleteJob(mta305Job.getKey());
        }

        scheduler.scheduleJob(mta305Job, mta305Trigger);

        log.info("✅ MYA 305 scheduled successfully");
    }

    private void scheduleW3Job() throws SchedulerException {
        JobDetail w3Job = JobBuilder.newJob(GenerateAnnualW3Reports.class)
                .withIdentity("w3Job", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger w3Trigger = TriggerBuilder.newTrigger()
                .withIdentity("w3Trigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 8 3 1 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(w3Job.getKey())) {
            scheduler.deleteJob(w3Job.getKey());
        }

        scheduler.scheduleJob(w3Job, w3Trigger);

        log.info("✅ W3 Official scheduled successfully");
    }


    private void scheduleWeeklyPayStubJob() throws SchedulerException {
        // Регистрируем WeeklyPayStubJob
        JobDetail weeklyPayStubJob = JobBuilder.newJob(WeeklyPayStubJob.class)
                .withIdentity("weeklyPayStubJob", "TAX_JOBS")
                .storeDurably()
                .build();

        // Trigger - каждое воскресенье в 4:00 утра
        Trigger weeklyPayStubTrigger = TriggerBuilder.newTrigger()
                .withIdentity("weeklyPayStubTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 4 ? * SUN")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        // Удаляем старую версию если есть
        if (scheduler.checkExists(weeklyPayStubJob.getKey())) {
            scheduler.deleteJob(weeklyPayStubJob.getKey());
        }

        // Регистрируем
        scheduler.scheduleJob(weeklyPayStubJob, weeklyPayStubTrigger);

        log.info("✅ WeeklyPayStubJob scheduled successfully");
    }

    private void scheduleBIWeeklyPayStubJob() throws SchedulerException {
        // Регистрируем WeeklyPayStubJob
        JobDetail biWeeklyPayStubJob = JobBuilder.newJob(BiWeeklyPayStubJob.class)
                .withIdentity("biWeeklyPayStubJob", "TAX_JOBS")
                .storeDurably()
                .build();

        // Trigger - каждое воскресенье в 4:00 утра
        Trigger biWeeklyPayStubTrigger = TriggerBuilder.newTrigger()
                .withIdentity("biWeeklyPayStubTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 30 4 ? * SUN")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        // Удаляем старую версию если есть
        if (scheduler.checkExists(biWeeklyPayStubJob.getKey())) {
            scheduler.deleteJob(biWeeklyPayStubJob.getKey());
        }
        // Регистрируем
        scheduler.scheduleJob(biWeeklyPayStubJob, biWeeklyPayStubTrigger);

        log.info("✅ BiWeeklyPayStubJob scheduled successfully");
    }


    private void scheduleWeeklyEmployerTaxes() throws SchedulerException {
        JobDetail weeklyEmployerTaxesJob = JobBuilder.newJob(CalculateWeeklyEmployerTaxes.class)
                .withIdentity("calculateWeeklyEmployerTaxesJob", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger weeklyEmployerTaxesTrigger = TriggerBuilder.newTrigger()
                .withIdentity("calculateWeeklyEmployerTaxesTrigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 4 ? * SUN")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        // Удаляем старую версию если есть
        if (scheduler.checkExists(weeklyEmployerTaxesJob.getKey())) {
            scheduler.deleteJob(weeklyEmployerTaxesJob.getKey());
        }
        // Регистрируем
        scheduler.scheduleJob(weeklyEmployerTaxesJob, weeklyEmployerTaxesTrigger);

        log.info("✅ WeeklyEmployerTaxes scheduled successfully");
    }


    private void scheduleW2OfficialForms() throws SchedulerException {
        // Регистрируем WeeklyPayStubJob
        JobDetail w2Job = JobBuilder.newJob(OfficialW2Forms2025.class)
                .withIdentity("officialW2Job", "TAX_JOBS")
                .storeDurably()
                .build();

        Trigger w2Trigger = TriggerBuilder.newTrigger()
                .withIdentity("officialW2Trigger", "TAX_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 7 3 1 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        // Удаляем старую версию если есть
        if (scheduler.checkExists(w2Job.getKey())) {
            scheduler.deleteJob(w2Job.getKey());
        }
        // Регистрируем
        scheduler.scheduleJob(w2Job, w2Trigger);

        log.info("✅ W2Official was scheduled successfully");
    }


    private void scheduleWeeklyPayrollReport() throws SchedulerException {
        JobDetail wpJob = JobBuilder.newJob(WeeklyPayrollReportJob.class)
                .withIdentity("weeklyPayrollJob", "REPORT_JOBS")
                .storeDurably()
                .build();

        Trigger wpTrigger = TriggerBuilder.newTrigger()
                .withIdentity("weeklyPayrollTrigger", "REPORT_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 6 ? * SUN")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        // Удаляем старую версию если есть
        if (scheduler.checkExists(wpJob.getKey())) {
            scheduler.deleteJob(wpJob.getKey());
        }
        // Регистрируем
        scheduler.scheduleJob(wpJob, wpTrigger);

        log.info("✅ Worker Payroll was scheduled successfully");
    }


    private void scheduleMonthlyPayrollReport() throws SchedulerException {
        JobDetail wpJob = JobBuilder.newJob(MonthlyPayrollReports.class)
                .withIdentity("monthlyPayrollJob", "REPORT_JOBS")
                .storeDurably()
                .build();

        Trigger wpTrigger = TriggerBuilder.newTrigger()
                .withIdentity("monthlyPayrollTrigger", "REPORT_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 7 ? * 1#1")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(wpJob.getKey())) {
            scheduler.deleteJob(wpJob.getKey());
        }
        // Регистрируем
        scheduler.scheduleJob(wpJob, wpTrigger);

        log.info("✅ Worker Monthly Payroll was scheduled successfully");
    }


    private void scheduleWeeklyHoursReport() throws SchedulerException {
        JobDetail wpJob = JobBuilder.newJob(WeeklyHoursReportJob.class)
                .withIdentity("weeklyHoursJob", "REPORT_JOBS")
                .storeDurably()
                .build();

        Trigger wpTrigger = TriggerBuilder.newTrigger()
                .withIdentity("weeklyHoursTrigger", "REPORT_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 30 6 ? * SUN")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(wpJob.getKey())) {
            scheduler.deleteJob(wpJob.getKey());
        }
        // Регистрируем
        scheduler.scheduleJob(wpJob, wpTrigger);

        log.info("✅ Worker Weekly Hours Report was scheduled successfully");
    }


    private void scheduleMonthlyHoursReport() throws SchedulerException {
        JobDetail wpJob = JobBuilder.newJob(MonthlyHoursReportJob.class)
                .withIdentity("monthlyHoursJob", "REPORT_JOBS")
                .storeDurably()
                .build();

        Trigger wpTrigger = TriggerBuilder.newTrigger()
                .withIdentity("monthlyHoursTrigger", "REPORT_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 30 7 ? * 1#1")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(wpJob.getKey())) {
            scheduler.deleteJob(wpJob.getKey());
        }
        scheduler.scheduleJob(wpJob, wpTrigger);

        log.info("✅ Worker Monthly Hours report was scheduled successfully");
    }


    private void scheduleQuarterlyTaxSummaryReportReport() throws SchedulerException {
        JobDetail wpJob = JobBuilder.newJob(QuarterlyTaxSummaryReportJob.class)
                .withIdentity("quarterlyTaxSummaryReportJob", "REPORT_JOBS")
                .storeDurably()
                .build();

        Trigger taxSummaryTrigger = TriggerBuilder.newTrigger()
                .withIdentity("quarterlyTaxSummaryReportTrigger", "REPORT_JOBS")
                .withSchedule(CronScheduleBuilder
                        .cronSchedule("0 0 9 1 1,4,7,10 ?")
                        .inTimeZone(TimeZone.getTimeZone("America/New_York")))
                .build();

        if (scheduler.checkExists(wpJob.getKey())) {
            scheduler.deleteJob(wpJob.getKey());
        }
        // Регистрируем0
        scheduler.scheduleJob(wpJob, taxSummaryTrigger);

        log.info("✅ Worker Monthly Hours report was scheduled successfully");
    }





}