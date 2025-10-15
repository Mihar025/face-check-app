package com.zikpak.facecheck.TestDataForGeneratingData;

import com.zikpak.facecheck.entity.employee.WorkerSchedule;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerScheduleRepository;
import com.zikpak.facecheck.requestsResponses.schedule.WorkerSetScheduleRequest;
import com.zikpak.facecheck.services.workSchedule.WorkerScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WorkerScheduleTest {
    private final UserRepository userRepository;
    private final WorkerScheduleService workerScheduleService;
    public void generateScheduleForWorkers(Integer id){
        LocalDate today = LocalDate.now();

        WorkerSetScheduleRequest request = new WorkerSetScheduleRequest();
        request.setStartTime(LocalTime.of(7, 30));
        request.setEndTime(LocalTime.of(20, 30));
        request.setStartLunch(LocalDateTime.of(today, LocalTime.of(12, 0)));
        request.setEndLunch(LocalDateTime.of(today, LocalTime.of(12, 45)));
        request.setIsCompanyPayingLunch(true);

        var user = userRepository.findById(id).orElseThrow();
      //  workerScheduleService.setScheduleForWorkerScenario2(user.getId(), request);

    }




}
