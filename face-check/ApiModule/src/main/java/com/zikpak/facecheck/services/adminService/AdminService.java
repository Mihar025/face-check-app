package com.zikpak.facecheck.services.adminService;


import com.zikpak.facecheck.domain.AdminAndForemanFunctionality;
import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.admin.*;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.requestsResponses.attendance.PunchInRequest;
import com.zikpak.facecheck.requestsResponses.attendance.PunchOutRequest;
import com.zikpak.facecheck.services.ForemanAndAdminFunctional.ForemanAndAdminService;
import com.zikpak.facecheck.services.workAttendanceService.WorkAttendanceService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService implements AdminAndForemanFunctionality {


    private final UserRepository userRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final ForemanAndAdminService foremanAndAdminService;
    private final CompanyRepository companyRepository;
    private final WorkerSiteRepository workerSiteRepository;
    private final WorkAttendanceService workAttendanceService;


    @Override
    public PageResponse<WorksiteWorkerResponse> findAllWorkersInWorkSite(int page,
                                                                         int size,
                                                                         Integer workSiteId,
                                                                         Authentication authentication){
        return foremanAndAdminService.findAllWorkersInWorkSite(page, size, workSiteId, authentication);
    }

    @Transactional
    public ChangePunchInForWorkerResponse ChangingPunchInForWorkerIfDoesntExist(
            Integer workerId,
            ChangePunchInRequest changePunchInRequest,
            Authentication authentication) {

        log.info("Starting ChangingPunchInForWorkerIfDoesntExist for worker: {}", workerId);

        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);

        if (changePunchInRequest.getNewPunchInDate() == null ||
                changePunchInRequest.getNewPunchInTime() == null ||
                changePunchInRequest.getDateWhenWorkerDidntMakePunchIn() == null) {
            throw new IllegalArgumentException("Please enter correct date and time!");
        }

        LocalDateTime punchInDateTime = LocalDateTime.of(
                changePunchInRequest.getNewPunchInDate(),
                changePunchInRequest.getNewPunchInTime());

        // ✅ Вызываем новый метод для admin
        workAttendanceService.punchInForWorker(workerId, punchInDateTime, null);

        log.info("Successfully created punch in with full logic for worker: {}", workerId);

        return ChangePunchInForWorkerResponse.builder()
                .workerId(workerId)
                .dateWhenWorkerDidntMakePunchIn(changePunchInRequest.getDateWhenWorkerDidntMakePunchIn())
                .newPunchInDate(changePunchInRequest.getNewPunchInDate())
                .newPunchInTime(changePunchInRequest.getNewPunchInTime())
                .build();
    }

    @Transactional
    public ChangePunchOutForWorkerResponse ChangingPunchOutForWorkerIfDoesntExist(
            Integer workerId,
            ChangePunchOutRequest changePunchOutRequest,
            Authentication authentication) {

        log.info("Starting ChangingPunchOutForWorkerIfDoesntExist for worker: {}", workerId);

        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);

        if (changePunchOutRequest.getNewPunchOutDate() == null ||
                changePunchOutRequest.getNewPunchOutTime() == null ||
                changePunchOutRequest.getDateWhenWorkerDidntMakePunchOut() == null) {
            throw new IllegalArgumentException("Please fill form with correct date, time and date when punch was missed!");
        }

        LocalDateTime punchOutDateTime = LocalDateTime.of(
                changePunchOutRequest.getNewPunchOutDate(),
                changePunchOutRequest.getNewPunchOutTime());

        // ✅ Вызываем новый метод для admin
        workAttendanceService.punchOutForWorker(workerId, punchOutDateTime);

        log.info("Successfully added punch out with full logic for worker: {}", workerId);

        return ChangePunchOutForWorkerResponse.builder()
                .workerId(workerId)
                .dateWhenWorkerDidntMakePunchOut(changePunchOutRequest.getDateWhenWorkerDidntMakePunchOut())
                .newPunchOutDate(changePunchOutRequest.getNewPunchOutDate())
                .newPunchOutTime(changePunchOutRequest.getNewPunchOutTime())
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

        // НЕ ЗАГРУЖАЙ Company через lazy loading!
        // Используй прямой запрос
        Integer adminId = admin.getId();

        // Создай метод в UserRepository
        Integer companyId = userRepository.findCompanyIdByUserId(adminId);

        if (companyId == null) {
            log.warn("No company found for admin");
            return 0;
        }

        return workerSiteRepository.countByCompanyId(companyId);
    }


    public void setUpBudget(Authentication authentication, BigDecimal budget
    ){
        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);

        admin.setActualBudget(budget);
        userRepository.save(admin);
    }

    public BigDecimal findCurrentBudget (Authentication authentication){
        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);
        return admin.getActualBudget();
    }

    public BigDecimal findCurrentExpenses(Authentication authentication){
        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);
        return admin.getExpenses();
    }

    public BigDecimal findCurrentCostOfSalaries(Authentication authentication){
        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);
        return admin.getCostOfSalaries();
    }

    public BigDecimal findCurrentProfit(Authentication authentication){
        User admin = ((User) authentication.getPrincipal());
        doesHaveAdminRole(admin);
        return admin.getProfit();
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
        if(changePunchInRequest.getNewPunchInDate() == null && changePunchInRequest.getNewPunchInTime() == null){
            throw new IllegalArgumentException("Please enter correct date and time!");
        }
        WorkerAttendance newAttendance = WorkerAttendance.builder()
                .worker(worker)
                .checkInTime(LocalDateTime.of(
                        changePunchInRequest.getNewPunchInDate(),
                        changePunchInRequest.getNewPunchInTime()))
                .build();
            workerAttendanceRepository.save(newAttendance);
    }




    private void findAndValidateWorkerAttendanceForPunchOut(User worker,ChangePunchOutRequest changePunchOutRequest ){
        if (changePunchOutRequest.getNewPunchOutDate() == null
                || changePunchOutRequest.getNewPunchOutTime() == null
                || changePunchOutRequest.getDateWhenWorkerDidntMakePunchOut() == null) {
            throw new IllegalArgumentException("Please fill form with correct date, time and date when punch was missed!");
        }

        boolean hasAttendance = worker.getAttendances()
                .stream()
                .anyMatch(attendance -> attendance.getCheckOutTime() != null &&
                        attendance.getCheckOutTime().toLocalDate()
                                .equals(changePunchOutRequest.getDateWhenWorkerDidntMakePunchOut().toLocalDate()));
        if(hasAttendance){
            throw new IllegalStateException("There is already punch Out for this date");
        }
    }




    private void setNewAttendanceForNewPunchOut(User worker, ChangePunchOutRequest changePunchOutRequest){
        if (changePunchOutRequest.getNewPunchOutDate() == null
                || changePunchOutRequest.getNewPunchOutTime() == null
                || changePunchOutRequest.getDateWhenWorkerDidntMakePunchOut() == null) {
            throw new IllegalArgumentException("Please fill form with correct date, time and date when punch was missed!");
        }
        WorkerAttendance newAttendance = WorkerAttendance.builder()
                .worker(worker)
                .checkOutTime(LocalDateTime.of(
                        changePunchOutRequest.getNewPunchOutDate(),
                        changePunchOutRequest.getNewPunchOutTime()))
                .build();

        workerAttendanceRepository.save(newAttendance);
    }






}
