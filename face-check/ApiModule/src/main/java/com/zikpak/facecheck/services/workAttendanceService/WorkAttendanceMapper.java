package com.zikpak.facecheck.services.workAttendanceService;

import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.requestsResponses.AttendanceResponse;
import com.zikpak.facecheck.services.transferService.TransferResponse;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

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



    public TransferResponse toCompanyWorkerTransferResponse(WorkerAttendance attendance) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return TransferResponse.builder()
                .workerId(attendance.getWorker().getId())
                .workerFullName(attendance.getWorker().getFirstName() + " " + attendance.getWorker().getLastName())
                .transferTime(attendance.getTransferTime())
                .formattedTransferTime(attendance.getTransferTime() != null ?
                        attendance.getTransferTime().format(formatter) : null)
                .transferPhotoUrl(attendance.getTransferPhotoUrl())
                .transferLatitude(attendance.getTransferLatitude())
                .transferLongitude(attendance.getTransferLongitude())
                .transferLocation(attendance.getTransferLocation())
                .isSuccessful(true)
                .message("Transfered successful")
                .build();
    }
}
