package com.zikpak.facecheck.services.adminService;


import com.zikpak.facecheck.domain.AdminAndForemanFunctionality;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.*;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements AdminAndForemanFunctionality {


    private final UserRepository userRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final ForemanAndAdminService foremanAndAdminService;
    private final CompanyRepository companyRepository;


    @Override
    public PageResponse<WorksiteWorkerResponse> findAllWorkersInWorkSite(int page, int size, Integer workSiteId, Authentication authentication){
        return foremanAndAdminService.findAllWorkersInWorkSite(page, size, workSiteId, authentication);
    }

    @Transactional
    public ChangePunchInForWorkerResponse ChangingPunchInForWorkerIfDoesntExist(Integer workerId, ChangePunchInRequest changePunchInRequest, Authentication authentication) {
        User admin = ((User) authentication.getPrincipal());

        doesHaveAdminRole(admin);

        var foundedWorker = findWorkerById(workerId);

        findAndValidateWorkerAttendanceForPunchIn(foundedWorker, changePunchInRequest);

        setNewAttendanceForNewPunchIn(foundedWorker, changePunchInRequest);


        return ChangePunchInForWorkerResponse.builder()
                .workerId(foundedWorker.getId())
                .dateWhenWorkerDidntMakePunchIn(changePunchInRequest.getDateWhenWorkerDidntMakePunchIn())
                .newPunchInDate(changePunchInRequest.getNewPunchInDate())
                .newPunchInTime(changePunchInRequest.getNewPunchInTime())
                .build();
    }





    @Transactional
    public ChangePunchOutForWorkerResponse ChangingPunchOutForWorkerIfDoesntExist(Integer workerId, ChangePunchOutRequest changePunchInRequest, Authentication authentication) {
        User admin = ((User) authentication.getPrincipal());

        doesHaveAdminRole(admin);
        var foundedWorker = findWorkerById(workerId);

        findAndValidateWorkerAttendanceForPunchOut(foundedWorker, changePunchInRequest);

        setNewAttendanceForNewPunchOut(foundedWorker, changePunchInRequest);

        return ChangePunchOutForWorkerResponse.builder()
                .workerId(foundedWorker.getId())
                .dateWhenWorkerDidntMakePunchOut(changePunchInRequest.getDateWhenWorkerDidntMakePunchOut())
                .newPunchOutDate(changePunchInRequest.getNewPunchOutDate())
                .newPunchOutTime(changePunchInRequest.getNewPunchOutTime())
                .build();
    }

    @Transactional
    public Integer findAllEmployeesInCompany(Authentication authentication) {
        log.info("FindAllEmployeesInCompany begins");
        User admin = ((User) authentication.getPrincipal());
        log.info("Checking roles");
        doesHaveAdminRole(admin);
        log.info("Successful checked role");
        var foundedAdmin = userRepository.findById(admin.getId())
                .orElseThrow( () -> new  EntityNotFoundException("Admin not found"));
        log.info("Founded admin {}" , foundedAdmin);
        var foundedCompany = companyRepository.findById(foundedAdmin.getCompany().getId())
                .orElseThrow( () -> new  EntityNotFoundException("Company not found"));
        log.info("Founded company {}" , foundedCompany);
        if(!foundedAdmin.getCompany().getId().equals(foundedCompany.getId())) {
            log.info("Checking is Admin in the same company!");
            throw new AccessDeniedException("You dont have permission to access this company");
        }
        return foundedCompany.getEmployees().size();
    }

    public Integer findAllWorksitesInCompany(Authentication authentication) {
        log.info("findAllWorksitesInCompany begins");
        User admin = ((User) authentication.getPrincipal());
        log.info("Checking roles ");
        doesHaveAdminRole(admin);
        log.info("Successful checked  role");
        var foundedAdmin = userRepository.findById(admin.getId())
                .orElseThrow( () -> new  EntityNotFoundException("Admin not found"));
        log.info("Founded admin  {}" ,  foundedAdmin);
        var foundedCompany = companyRepository.findById(foundedAdmin.getCompany().getId())
                .orElseThrow( () -> new  EntityNotFoundException("Company not found"));
        log.info("Founded  company {}" , foundedCompany);
        if(!foundedAdmin.getCompany().getId().equals(foundedCompany.getId())) {
            log.info("Checking i s Admin in the same company!");
            throw new AccessDeniedException("You dont have permission to access this company");
        }
        return foundedCompany.getWorkSites().size();
    }





    private void doesHaveAdminRole(User admin){
        boolean isAdmin = admin.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("FOREMAN"));
        if(!isAdmin){
            throw new AccessDeniedException("You dont have permission to change the worker Punch In,or Punch Out time");
        }
    }

    private User findWorkerById(Integer workerId){
        return userRepository.findById(workerId).orElseThrow(() -> new EntityNotFoundException("Worker not found"));
    }

    private void findAndValidateWorkerAttendanceForPunchIn(User worker,ChangePunchInRequest changePunchInRequest ){
        boolean hasAttendance = worker.getAttendances()
                .stream()
                .anyMatch(attendance -> attendance.getCheckInTime()!= null && attendance.getCheckInTime().toLocalDate()
                        .equals(changePunchInRequest.getDateWhenWorkerDidntMakePunchIn().toLocalDate()));
        if(hasAttendance){
            throw new IllegalStateException("There is already punch in for this date");
        }
    }

    private void setNewAttendanceForNewPunchIn(User worker, ChangePunchInRequest changePunchInRequest){
        WorkerAttendance newAttendance = WorkerAttendance.builder()
                .worker(worker)
                .checkInTime(LocalDateTime.of(
                        changePunchInRequest.getNewPunchInDate(),
                        changePunchInRequest.getNewPunchInTime()))
                .build();
        workerAttendanceRepository.save(newAttendance);
    }




    private void findAndValidateWorkerAttendanceForPunchOut(User worker,ChangePunchOutRequest changePunchInRequest ){
        boolean hasAttendance = worker.getAttendances()
                .stream()
                .anyMatch(attendance -> attendance.getCheckOutTime() != null &&
                        attendance.getCheckOutTime().toLocalDate()
                                .equals(changePunchInRequest.getDateWhenWorkerDidntMakePunchOut().toLocalDate()));
        if(hasAttendance){
            throw new IllegalStateException("There is already punch Out for this date");
        }
    }




    private void setNewAttendanceForNewPunchOut(User worker, ChangePunchOutRequest changePunchOutRequest){
        WorkerAttendance newAttendance = WorkerAttendance.builder()
                .worker(worker)
                .checkOutTime(LocalDateTime.of(
                        changePunchOutRequest.getNewPunchOutDate(),
                        changePunchOutRequest.getNewPunchOutTime()))
                .build();

        workerAttendanceRepository.save(newAttendance);
    }






}
