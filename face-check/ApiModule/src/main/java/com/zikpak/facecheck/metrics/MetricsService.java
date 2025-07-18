package com.zikpak.facecheck.metrics;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public void recordPunchIn(String workSiteName, boolean success){
        meterRegistry.counter("attendance.punch_in",
                "worksite", workSiteName,
                "status", success ? "success" : "failed"
        ).increment();

        if(success){
            log.debug("Punch IN recorded for worksite {}", workSiteName);
        }
    }

    public void recordPunchOut(String workSiteName,
                               String companyName,
                               String workerName,
                               boolean success,
                               double hoursWorked,
                               double overtime){
        meterRegistry.counter("attendance.punch_out",
                "worksite", workSiteName,
                "company", companyName,
                "worker", workerName,
                "status", success ? "success" : "failed"
        ).increment();

        if(success){
            meterRegistry.summary("attendance.hours_worked", "worksite", workSiteName)
                            .record(hoursWorked);
            if(overtime > 0){
                meterRegistry.summary("attendance.overtime", "worksite", workSiteName)
                        .record(overtime);
            }

            if(overtime > 0){
                meterRegistry.counter("attendance.overtime", "worksite", workSiteName)
                        .increment();
            }
            log.debug("Punch OUT recorded for worksite {}, Company {}, Worker: {}", workSiteName, companyName, workerName);
        }
    }
    public void recordLocationValidation(String workSiteName,
                                         String workerFullName,
                                         String companyName,
                                         boolean isInRadius,
                                         boolean success){
        meterRegistry.counter("attendance.location_validation",
                "worksite", workSiteName,
                "workerFullName", workerFullName,
                "companyName", companyName,
                "isInRadius", isInRadius ? "in_radius" : "out_radius",
                "success", success ? "success" : "failed"
        ).increment();
        if(isInRadius && success){
            log.debug("Location: {} , worker {}, Company {}, isInRadius {},   success: {}",
                    workSiteName, workerFullName, companyName, isInRadius,  success);
        }
    }

    public void recordEarlyPunchIn (String workerFullName, long minutesEarly) {
        meterRegistry.counter("attendance.early_punch_in").increment();
        meterRegistry.summary("attendance.early_punch_in_minutes").record(minutesEarly);

        if(minutesEarly > 15){
            log.warn("Very early punch-in {} came {} minutes early", workerFullName, minutesEarly);
        }
    }

    public void recordLatePunchOut(String workerFullName, long minutesLate) {
        meterRegistry.counter("attendance.late_punch_out").increment();
        meterRegistry.summary("attendance.late_punch_out_minutes").record(minutesLate);
        log.warn("Very late punch-out {} gone {} minutes later", workerFullName, minutesLate);
    }

    public void recordPayrollCalculations(BigDecimal grossPay,
                                          BigDecimal netPay,
                                          BigDecimal totalDeductions) {
        meterRegistry.summary("payroll.gross_pay").record(grossPay.doubleValue());
        meterRegistry.summary("payroll.net_pay").record(netPay.doubleValue());
        meterRegistry.summary("payroll.total_deductions").record(totalDeductions.doubleValue());
    }

    public void recordPhotoUploading(String type, boolean success, long uploadTimeMs){
        meterRegistry.counter("attendance.photo_uploading",
                "type", type,
                "status", success ? "success" : "failed"
        ).increment();

        if(success){
            meterRegistry.timer("attendance.photo.upload_time", "type", type)
                    .record(uploadTimeMs, TimeUnit.MILLISECONDS);
        }
    }





    public void recordEarningPeriod(String workerName,
                                    double gross,
                                    double net,
                                    double hoursWorked
                                    ){
        meterRegistry.counter("attendance.financial_info",
                "worker", workerName).increment();
        meterRegistry.summary("attendance.gross",
                "worker", workerName
        ).record(gross);

        meterRegistry.summary("attendance.net",
                "worker", workerName
        ).record(net);

        meterRegistry.summary("attendance.hoursWorked",
                "worker", workerName
        ).record(hoursWorked);
        log.debug("Data for Worker: {}  was recorded! Gross:{}, Net:{}, Hours Worked:{}", workerName, gross, net, hoursWorked );
    }


    public void recordPayrollCalculations(
                                    String workerName,
                                    String companyName,
                                    double gross,
                                    double net,
                                    double hoursWorked,
                                    double medicare,
                                    double SSM_Employee,
                                    double federal,
                                    double NyStateWithholding,
                                    double NyLocalWithholding,
                                    double NyDisabilWithholding,
                                    double paidFamilyLeave,
                                    double totalDeductions
                                    ) {
        meterRegistry.counter("attendance.new_payroll_period",
                "worker", workerName,
                "company", companyName).increment();
        meterRegistry.summary("attendance.gross",
                "worker", workerName,
                "company", companyName
        ).record(gross);

        meterRegistry.summary("attendance.net",
                "worker", workerName,
                "company", companyName
        ).record(net);

        meterRegistry.summary("attendance.hoursWorked",
                "worker", workerName,
                "company", companyName
        ).record(hoursWorked);

        meterRegistry.summary("attendance.medicare",
                "worker", workerName,
                "company", companyName
        ).record(medicare);


        meterRegistry.summary("attendance.SSM_Employee",
                "worker", workerName,
                "company", companyName
        ).record(SSM_Employee);

        meterRegistry.summary("attendance.federal",
                "worker", workerName,
                "company", companyName
        ).record(federal);

        meterRegistry.summary("attendance.NyStateWithholding",
                "worker", workerName,
                "company", companyName
        ).record(NyStateWithholding);

        meterRegistry.summary("attendance.NyLocalWithholding",
                "worker", workerName,
                "company", companyName
        ).record(NyLocalWithholding);
        meterRegistry.summary("attendance.NyDisabilWithholding",
                "worker", workerName,
                "company", companyName
        ).record(NyDisabilWithholding);


        meterRegistry.summary("attendance.paidFamilyLeave",
                "worker", workerName,
                "company", companyName
        ).record(paidFamilyLeave);
        meterRegistry.summary("attendance.totalDeductions",
                "worker", workerName,
                "company", companyName
        ).record(totalDeductions);

        log.debug("Data for Worker: {} in Company {}  was recorded! Gross:{}, Net:{}, Hours Worked:{}, Medicare {}, SSN {}, Federal {}, NYSTW {}, NYL {}, NYDW {}, PFL {}, total Deductions {}, PS {}, PE {}",
                workerName,
                companyName,
                gross,
                net,
                hoursWorked,
                medicare,
                SSM_Employee,
                federal,
                NyStateWithholding,
                NyLocalWithholding,
                NyDisabilWithholding,
                paidFamilyLeave,
                totalDeductions
        );

    }






    public void recordError (String operation, String errorType, Exception e){
        meterRegistry.counter("attendance.errors",
                "operations", operation,
                "error_type", errorType,
                "exception", e.getClass().getSimpleName()).increment();
        log.error("Error in {}: {} - {}", operation, errorType, e);
    }


    public Timer.Sample startTimer(){
        return Timer.start(meterRegistry);
    }

    public void recordOperationTime(Timer.Sample sample, String operation) {
        sample.stop(meterRegistry.timer("attendance.operation_time", "operation", operation));
    }


}
