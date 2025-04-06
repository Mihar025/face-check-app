package com.zikpak.facecheck.services.ForemanAndAdminFunctional;


import com.zikpak.facecheck.domain.AdminAndForemanFunctionality;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.mapper.UserMapper;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.WorksiteWorkerResponse;
import com.zikpak.facecheck.requestsResponses.worker.UpdatePunchInForWorkerResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ForemanAndAdminService implements AdminAndForemanFunctionality {

    private final WorkerSiteRepository workerSiteRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WorkerPayrollRepository workerPayrollRepository;



    //todo uncoment tommorow and check test again!
    @Override
    public PageResponse<WorksiteWorkerResponse> findAllWorkersInWorkSite(int page, int size, Integer workSiteId, Authentication authentication){
        User user = ((User) authentication.getPrincipal());

        var foundedWorkSite = workerSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new EntityNotFoundException("Work site with provided id, wasn't found"));
/*
        if (!foundedWorkSite.getCompanies().stream()
                .map(Company::getId)
                .anyMatch(companyId -> companyId.equals(user.getCompany().getId()))) {
            throw new AccessDeniedException("Company ID wrong!");
        }

 */

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<User> allWorkersInWorkSite  = userRepository.findAllWorkerInWorkSite(pageable, foundedWorkSite.getId(), user.getCompany().getId());
        List<WorksiteWorkerResponse> workerResponses = allWorkersInWorkSite.stream()
                .map(userMapper::toUserWorkSiteResponse)
                .toList();
        log.info("Workers received for worksite {}: {}", workSiteId, workerResponses.size());

        return new PageResponse<>(
                workerResponses,
                allWorkersInWorkSite.getNumber(),
                allWorkersInWorkSite.getSize(),
                allWorkersInWorkSite.getTotalElements(),
                allWorkersInWorkSite.getTotalPages(),
                allWorkersInWorkSite.isFirst(),
                allWorkersInWorkSite.isLast()
        );
    }
    @Transactional(rollbackOn = Exception.class)
    public UpdatePunchInForWorkerResponse updateLatestPunchInForWorkerResponse(Authentication authentication, Integer workerId, LocalDateTime newPunchInTime) {
        User user = ((User) authentication.getPrincipal());

        if (!user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("FOREMAN"))) {
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        User foundedWorker = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker with provided id wasn't found"));

        WorkerAttendance attendance = workerAttendanceRepository.findLatestAttendanceByWorkerId(foundedWorker.getId())
                .orElseThrow(() -> new EntityNotFoundException("No attendance records found for this worker"));

        attendance.setCheckInTime(newPunchInTime);

        return UpdatePunchInForWorkerResponse.builder()
                .workerId(foundedWorker.getId())
                .newPunchInTime(newPunchInTime)
                .workerFullName(foundedWorker.getFirstName() + " " + foundedWorker.getLastName())
                .current_work_site(foundedWorker.getCurrentWorkSite() != null ?
                        foundedWorker.getCurrentWorkSite().getSiteName() : "No current site")
                .build();
    }

    @Transactional
    public void deleteWorkerPunchIn(Authentication authentication, Integer workerId) {
        User user = ((User) authentication.getPrincipal());

        if (!user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("FOREMAN"))) {
            throw new AccessDeniedException("You don't have permission for this operation!");
        }

        var foundedEmployee = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker with provided id wasn't found"));

        if (!foundedEmployee.getCompany().getId().equals(user.getCompany().getId())) {
            throw new AccessDeniedException("This person doesn't exist in this company!");
        }

        var lastAttendance = workerAttendanceRepository.findLatestAttendanceByWorkerId(workerId)
                .orElseThrow(() -> new EntityNotFoundException("No attendance records found for this worker"));

        workerAttendanceRepository.delete(lastAttendance);

        log.info("Deleted last attendance record for worker with ID: {}", workerId);
    }


}
