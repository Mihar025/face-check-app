package com.zikpak.facecheck.services.workAttendanceService;

import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.requestsResponses.AttendanceResponse;
import org.springframework.stereotype.Service;

@Service
public class WorkAttendanceMapper {


    public AttendanceResponse toCompanyWorkerResponse(WorkerAttendance workerAttendance) {
        return AttendanceResponse.builder()
                .workerId(workerAttendance.getWorker().getId())
                .companyId(workerAttendance.getWorker().getCompany() != null ?
                        workerAttendance.getWorker().getCompany().getId() : null)
                .companyName(workerAttendance.getWorker().getCompany() != null ?
                        workerAttendance.getWorker().getCompany().getCompanyName() : null)
                .firstName(workerAttendance.getWorker().getFirstName())
                .lastName(workerAttendance.getWorker().getLastName())
                .email(workerAttendance.getWorker().getEmail())
                .phone(workerAttendance.getWorker().getPhoneNumber())
                .checkInTime(workerAttendance.getCheckInTime())
                .checkInPhotoUrl(workerAttendance.getCheckInPhotoUrl())
                .checkInLatitude(workerAttendance.getCheckInLatitude())
                .checkInLongitude(workerAttendance.getCheckInLongitude())
                .checkInLocation(workerAttendance.getCheckInLocation())
                .checkOutTime(workerAttendance.getCheckOutTime())
                .checkOutPhotoUrl(workerAttendance.getCheckOutPhotoUrl())
                .checkOutLatitude(workerAttendance.getCheckOutLatitude())
                .checkOutLongitude(workerAttendance.getCheckOutLongitude())
                .checkOutLocation(workerAttendance.getCheckOutLocation())
                .hoursWorked(workerAttendance.getHoursWorked())
                .grossPayPerDay(workerAttendance.getGrossPayPerDay())
                .netPay(workerAttendance.getNetPay())
                .periodStart(workerAttendance.getPeriodStart())
                .periodEnd(workerAttendance.getPeriodEnd())
                .attendanceId(workerAttendance.getId())
                .overtimeHours(workerAttendance.getOvertimeHours())
                .build();
    }
}
