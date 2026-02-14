package com.zikpak.facecheck.services.notesFromPunchService;


import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import org.springframework.stereotype.Service;

@Service
public class NotesForPunchMapper {


    public NotesForPunchResponse toNotesForPunchResponse(WorkerAttendance workerAttendance) {
        User worker = workerAttendance.getWorker();

        return NotesForPunchResponse.builder()
                .attendanceId(workerAttendance.getId())

                // Worker info
                .workerId(worker.getId())
                .workerFirstName(worker.getFirstName())
                .workerLastName(worker.getLastName())
                .workerFullName(worker.getFirstName() + " " + worker.getLastName())
                .workerProfileImageUrl(worker.getPhotoUrl())

                // Company info
                .companyName(worker.getCompany() != null ? worker.getCompany().getCompanyName() : null)

                // Notes
                .notesForPunchIn(workerAttendance.getNotesForPunchIn())
                .notesForPunchOut(workerAttendance.getNotesForPunchOut())

                // Times
                .checkInTime(workerAttendance.getCheckInTime())
                .checkOutTime(workerAttendance.getCheckOutTime())

                // Location
                .checkInLocation(workerAttendance.getCheckInLocation())
                .checkOutLocation(workerAttendance.getCheckOutLocation())

                .build();
    }
}