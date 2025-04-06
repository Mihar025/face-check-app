package com.zikpak.facecheck.services.adminService;


import com.zikpak.facecheck.domain.AdminAndForemanFunctionality;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
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

    /*

        1) Find all admins, formans, users!
            a) When we find some worker etc, should see all his contact info :
                a) Name + Lastname, Phone number, email


     6)
        7) Change employee salary!
         10) Forman could change schedule for worker, by reposting to the admin!

     */




    private final UserRepository userRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;
    private final ForemanAndAdminService foremanAndAdminService;



    @Override
    public PageResponse<WorksiteWorkerResponse> findAllWorkersInWorkSite(int page, int size, Integer workSiteId, Authentication authentication){
       return foremanAndAdminService.findAllWorkersInWorkSite(page, size, workSiteId, authentication);
    }







    @Transactional
    public ChangePunchInForWorkerResponse ChangingPunchInForWorkerIfDoesntExist(Integer workerId, ChangePunchInRequest changePunchInRequest, Authentication authentication) {
        User admin = ((User) authentication.getPrincipal());

        // Checking is that operation doing admin!
        doesHaveAdminRole(admin);

        // find worker by id
        var foundedWorker = findWorkerById(workerId);

        // check is worker really does not do the punch in!
        findAndValidateWorkerAttendanceForPunchIn(foundedWorker, changePunchInRequest);

        // If worker did not make the punch in, we are changing his punch in time to special time, which admin will set!
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

        // Checking is that operation doing admin!
        doesHaveAdminRole(admin);
        // find worker by id
        var foundedWorker = findWorkerById(workerId);

        // check is worker really does not do the punch in!

        findAndValidateWorkerAttendanceForPunchOut(foundedWorker, changePunchInRequest);

        // If worker did not make the punch in, we are changing his punch in time to special time, which admin will set!
        setNewAttendanceForNewPunchOut(foundedWorker, changePunchInRequest);

        return ChangePunchOutForWorkerResponse.builder()
                .workerId(foundedWorker.getId())
                .dateWhenWorkerDidntMakePunchOut(changePunchInRequest.getDateWhenWorkerDidntMakePunchOut())
                .newPunchOutDate(changePunchInRequest.getNewPunchOutDate())
                .newPunchOutTime(changePunchInRequest.getNewPunchOutTime())
                .build();
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
