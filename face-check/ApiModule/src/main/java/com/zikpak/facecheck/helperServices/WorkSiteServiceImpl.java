package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.WorkSiteService;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.mapper.WorkSiteMapper;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteClosedDaysResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkerCurrentlyWorkingInWorkSite;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite.SelectWorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkSiteServiceImpl implements WorkSiteService {

    private final WorkerSiteRepository workSiteRepository;
    private final WorkSiteMapper workSiteMapper;
    private final UserRepository userRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;


    @Override
    public WorkSiteResponse findWorkSiteById(Integer id) {
       // var foundedWorkSite = findWorkSiteBySpecialId(id);
        var foundedWorkSite = workSiteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work site not found"));
        return workSiteMapper.toWorkSiteResponse(foundedWorkSite);
    }


    // todo in future this method should return all work sites which located very closed to user location
    // todo basicly method should analizing user location and finding for for him work sites, which is more close to him!
    @Override
    public PageResponse<WorkSiteResponse> findAllWorkSites(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("siteName").descending());
        Page<WorkSite> workSites = workSiteRepository.findAll(pageable);
        List<WorkSiteResponse> workSiteResponses = workSites.getContent().stream()
                .map(workSiteMapper::toWorkSiteResponse)
                .toList();

        return new PageResponse<>(
                workSiteResponses,
                workSites.getNumber(),
                workSites.getSize(),
                workSites.getTotalElements(),
                workSites.getTotalPages(),
                workSites.isFirst(),
                workSites.isLast()
        );
    }

    @Override
    @Transactional
    public SelectWorkSiteResponse selectWorkSite(Integer workSiteId, Authentication authentication) {
        var user = checkIsUserAuthenticatedAndFindHim(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);

        if (!foundedWorkSite.getIsActive()) {
            throw new AccessDeniedException("Work site are not active");
        }

        user.setCurrentWorkSite(foundedWorkSite);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to select work site");
        }

        return SelectWorkSiteResponse.builder()
                .selectedWorkSiteId(foundedWorkSite.getId())
                .selectedWorkSiteName(foundedWorkSite.getSiteName())
                .workerId(user.getId())
                .build();
    }


    @Override
    @Transactional
    public SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse setNewCustomRadiusForWorker(
            Integer workSiteId,
            Integer workerId,
            SetNewCustomRadiusRequest customRadius,
            Authentication authentication) {


        checkIsUserHasAdminRoleAndBusinessOwner(authentication);


        var foundedWorkSite = workSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new EntityNotFoundException("Work site with id " + workSiteId + " not found"));


        var foundedWorker = userRepository.findById(workerId)
                .orElseThrow(() -> new EntityNotFoundException("Worker with id " + workerId + " not found"));

        log.info("Finding worksite + Finding Worker: {} {}",
                foundedWorkSite.getSiteName(),
                foundedWorker.getFirstName());


        if (!foundedWorkSite.getIsActive()) {
            throw new IllegalStateException("Work site is not active");
        }

        Double currentRadius = foundedWorkSite.getCustomRadius() != null
                ? foundedWorkSite.getCustomRadius().getOrDefault(foundedWorker.getId(), foundedWorkSite.getAllowedRadius())
                : foundedWorkSite.getAllowedRadius();

        if (currentRadius.equals(customRadius.getCustomRadius())) {
            throw new IllegalStateException("Custom radius can't be the same");
        }


        if (foundedWorkSite.getCustomRadius() == null) {
            foundedWorkSite.setCustomRadius(new HashMap<>());
        }


        foundedWorkSite.getCustomRadius().put(foundedWorker.getId(), customRadius.getCustomRadius());

        log.info("Setting new custom radius for worker: {}", customRadius.getCustomRadius());

        var savedWorkSiteRadius = workSiteRepository.save(foundedWorkSite);
        log.info("Radius saved successfully!");

        return SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse.builder()
                .workSiteId(savedWorkSiteRadius.getId())
                .workerId(foundedWorker.getId())
                .firstName(foundedWorker.getFirstName())
                .lastName(foundedWorker.getLastName())
                .companyName(foundedWorker.getCompany().getCompanyName())
                .newRadius(customRadius.getCustomRadius())
                .build();
    }

    @Override
    public WorkSiteResponse createWorkSite(Authentication  authentication,WorkSiteRequest request) {
            checkIsUserHasAdminRoleAndBusinessOwner(authentication);
            var newWorkSite =  WorkSite.builder()
                    .siteName(request.getWorkSiteName())
                    .address(request.getAddress())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .allowedRadius(request.getAllowedRadius())
                    .workDayStart(request.getWorkDayStart())
                    .workDayEnd(request.getWorkDayEnd())
                    .build();
        var savedWorkSite = workSiteRepository.save(newWorkSite);
        return  workSiteMapper.toWorkSiteResponse(savedWorkSite);
    }





    @Override
    public SetNewCustomRadiusResponse setCustomRadiusForWorkSite(Authentication authentication, Integer workSiteId, SetNewCustomRadiusRequest customRadius) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);

        var foundedWorkSite = workSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new EntityNotFoundException("Work site with id " + workSiteId + " not found"));
        log.info("Finding worksite: " + foundedWorkSite.getSiteName());

        if(!foundedWorkSite.getIsActive()) {
            throw new IllegalStateException("Work site is not active");
        }
            if(foundedWorkSite.getAllowedRadius().equals(customRadius.getCustomRadius())){
                throw new IllegalStateException("Custom radius can't be the same");
            }

            foundedWorkSite.setAllowedRadius(customRadius.getCustomRadius());
            log.info("Setting new allowed radius! " + customRadius.getCustomRadius());
            var savedWorkSiteRadius =  workSiteRepository.save(foundedWorkSite);
            log.info("Saving radius!");
        return SetNewCustomRadiusResponse.builder()
                                        .workSiteId(savedWorkSiteRadius.getId())
                                        .customRadius(savedWorkSiteRadius.getAllowedRadius())
                                        .build();
    }

    @Override
    public WorkSiteUpdateNameResponse updateWorkSiteName(Authentication authentication, Integer workSiteId, UpdateNameRequest request) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
        if(foundedWorkSite.getSiteName().equals(request.getName())) {
            throw new IllegalArgumentException("Work site name already exists");
        }
        foundedWorkSite.setSiteName(request.getName());
        workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteUpdateNameResponse(foundedWorkSite);
    }

    @Override
    public WorkSiteUpdateAddressResponse updateWorkSiteAddress(Authentication authentication,Integer workSiteId, UpdateWorkSiteAddress updateWorkSiteAddress) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
        if(foundedWorkSite.getAddress().equals(updateWorkSiteAddress.getAddress())) {
            throw new IllegalArgumentException("Work site address already exists");
        }
        foundedWorkSite.setAddress(updateWorkSiteAddress.getAddress());
        workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteUpdateAddressResponse(foundedWorkSite);
    }

    @Override
    public WorkSiteUpdateWorkingHoursResponse updateWorkingHours(Authentication authentication,Integer workSiteId, WorkSiteUpdateWorkingHoursRequest request) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
        if(!foundedWorkSite.getIsActive()) {
            throw new IllegalStateException("Work site is not active");
        }
        foundedWorkSite.setWorkDayStart(request.getNewStart());
        foundedWorkSite.setWorkDayEnd(request.getNewEnd());
        workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteUpdateWorkingHoursResponse(foundedWorkSite);
    }

    @Override
    public WorkSiteUpdateLocationResponse updateWorkSiteLocation(Authentication authentication,Integer workSiteId, WorkSiteUpdateLocationRequest request) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
        if(!foundedWorkSite.getIsActive()) {
            throw new IllegalStateException("Work site is not active");
        }
        foundedWorkSite.setLatitude(request.getNewLatitude());
        foundedWorkSite.setLongitude(request.getNewLongitude());
        foundedWorkSite.setAllowedRadius(request.getNewRadius());
        workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteUpdateLocationResponse(foundedWorkSite);
    }

/*
                OPTIONAL METHODS FOR UNEXPECTED SITUATIONS!
                    A) setWorkSiteActiveOrNotActive
                    B) scheduleInactiveDay

 */

    @Override
    public void setWorkSiteActiveOrNotActive(Authentication authentication, Integer workSiteId, UpdateStatusWorkSiteRequest updateStatusWorkSiteRequest) {

        checkIsUserHasAdminRoleAndBusinessOwner(authentication);


        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);

        var active = updateStatusWorkSiteRequest.isActive();
        if (foundedWorkSite.getIsActive() == updateStatusWorkSiteRequest.isActive()) {
            throw new IllegalStateException(
                    active
                            ? "Work site is already active"
                            : "Work site is already inactive"
            );
        }

        foundedWorkSite.setIsActive(updateStatusWorkSiteRequest.isActive());
        workSiteRepository.save(foundedWorkSite);

        // Можно добавить логирование
        log.info("Work site {} status changed to {}", workSiteId, active ? "active" : "inactive");
    }



        // future add push notification, that the in scheduled day work site will be closed
    @Override
    public ScheduleInactiveDayResponse scheduleInactiveDay(Authentication authentication, Integer workSiteId, ScheduleInactiveDayRequest inactiveDate) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);

        if(inactiveDate.getInactiveDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot schedule inactive day in past!");
        }
        if(foundedWorkSite.getInactiveDays().contains(inactiveDate.getInactiveDate())) {
            throw new IllegalStateException("Inactive day is already exist with this date!");
        }

            if(inactiveDate.getInactiveDate().getDayOfWeek() == DayOfWeek.SATURDAY){
                throw new IllegalStateException("Cannot schedule inactive day, in Saturday!");
            }

        Set<LocalDate> inactiveDays = foundedWorkSite.getInactiveDays();
        if(inactiveDays == null) {
            inactiveDays = new HashSet<>();
        }

        if(inactiveDays.contains(inactiveDate)) {
            throw new IllegalStateException("Cannot schedule inactive day in past!");
        }

        inactiveDays.add(inactiveDate.getInactiveDate());
        foundedWorkSite.setInactiveDays(inactiveDays);
        var savedInactiveDay = workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteScheduleInactiveDay(savedInactiveDay);
    }

    @Override
    public ScheduleInactiveDayResponse removeInactiveDay(Authentication authentication, Integer workSiteId,ScheduleInactiveDayRequest inactiveDate) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite =findWorkSiteBySpecialId(workSiteId);
        if(inactiveDate.getInactiveDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot remove inactive day in past!");
        }


        if(inactiveDate.getInactiveDate().getDayOfWeek() == DayOfWeek.SATURDAY){
            throw new IllegalStateException("Cannot remove inactive day, in Saturday!");
        }
        Set<LocalDate> inactiveDays = foundedWorkSite.getInactiveDays();
        if(inactiveDays == null || !inactiveDays.contains(inactiveDate.getInactiveDate())) {
            throw new IllegalStateException("This date is not scheduled as inactive day!");
        }

        inactiveDays.remove(inactiveDate.getInactiveDate());
        foundedWorkSite.setInactiveDays(inactiveDays);
        var savedInactiveDate = workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteScheduleInactiveDay(savedInactiveDate);
    }

    @Override
    public boolean isWorkSiteActive(Integer workSiteId) {
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);

        if(!foundedWorkSite.getIsActive()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        Set<LocalDate> inactiveDays = foundedWorkSite.getInactiveDays();
        if(inactiveDays != null && inactiveDays.contains(today)) {
            return false;
        }

        if(today.getDayOfWeek() == DayOfWeek.SATURDAY){
            return false;
        }

       return true;
    }

    @Override
    public IsWithinRadiusResponse isWithinRadius(Integer workSiteId, IsWithinRadiusRequest isWithinRadiusRequest) {

       var foundedWorkSite =  findWorkSiteById(workSiteId);

       if(foundedWorkSite.getLatitude() == null || foundedWorkSite.getLongitude() == null ||
            foundedWorkSite.getAllowedRadius() == null) {
           throw new IllegalStateException("Work site doesnt have any coordinates!");
       }

       double distance = calculateDistance(
               foundedWorkSite.getLatitude(),
               foundedWorkSite.getLongitude(),
               isWithinRadiusRequest.getLatitude(),
               isWithinRadiusRequest.getLongitude()
       );

        boolean isWithinRadius = distance <= foundedWorkSite.getAllowedRadius();

        return IsWithinRadiusResponse.builder()
                .worksiteId(foundedWorkSite.getWorkSiteId())
                .providedLatitude(isWithinRadiusRequest.getLatitude())
                .providedLongitude(isWithinRadiusRequest.getLongitude())
                .actualLatitude(foundedWorkSite.getLatitude())
                .actualLongitude(foundedWorkSite.getLongitude())
                .allowedRadius(foundedWorkSite.getAllowedRadius())
                .isWithinRadius(isWithinRadius)
                .build();

    }


    public boolean isWithinRadiusForPunchInOut(Integer workSiteId, Double userLatitude, Double userLongitude) {

        var foundedWorkSite =  findWorkSiteById(workSiteId);

        if(foundedWorkSite.getLatitude() == null || foundedWorkSite.getLongitude() == null ||
                foundedWorkSite.getAllowedRadius() == null) {
            throw new IllegalStateException("Work site is not active");
        }

        double distance = calculateDistance(
                foundedWorkSite.getLatitude(),
                foundedWorkSite.getLongitude(),
                userLatitude,
                userLongitude
        );
        return distance <= foundedWorkSite.getAllowedRadius();

    }





    @Override
    public boolean canPunchInOut(Authentication authentication, Integer workSiteId, Integer userId,CanPunchOutRequestWorkSite canPunchOutRequestWorkSite) {
        checkIsUserAuthenticatedAndFindHim(authentication);
        var workSite = findWorkSiteBySpecialId(workSiteId);

            if(!workSite.getIsActive()){
                throw new IllegalStateException("Work site is not active");
            }

            var earlierPunchInTime = workSite.getWorkDayStart().minusMinutes(10);
            var latestPunchOutTime  = workSite.getWorkDayEnd().plusMinutes(10);

            if(canPunchOutRequestWorkSite.getCanPunchOut().isBefore(earlierPunchInTime)) {
                return false;
            }
            if(canPunchOutRequestWorkSite.getCanPunchOut().isAfter(latestPunchOutTime)) {
                return false;
            }

            return true;
    }

    public boolean canPunchInOutForWorkAttendance(Authentication authentication, Integer workSiteId, Integer userId, LocalTime currentTime) {
        checkIsUserAuthenticatedAndFindHim(authentication);
        var workSite = findWorkSiteBySpecialId(workSiteId);

        if(workSite.getWorkDayStart() == null || workSite.getWorkDayEnd() == null) {
            throw new IllegalStateException("Work site schedule is not set");
        }
        if(!workSite.getIsActive()){
            throw new IllegalStateException("Work site is inactive");
        }

        var earlierPunchInTime = workSite.getWorkDayStart().minusMinutes(10);
        var latestPunchOutTime = workSite.getWorkDayEnd().plusMinutes(10);

        boolean isOvernightShift = workSite.getWorkDayEnd().isBefore(workSite.getWorkDayStart());

        if(currentTime.isBefore(earlierPunchInTime)) {
            throw new IllegalStateException("You cannot punch in - it's too early!");
        }

        if(isOvernightShift) {
            return currentTime.isAfter(earlierPunchInTime) ||
                    currentTime.isBefore(latestPunchOutTime);
        } else {
            return currentTime.isAfter(earlierPunchInTime) &&
                    currentTime.isBefore(latestPunchOutTime);
        }
    }

    @Override
    public PageResponse<WorkSiteClosedDaysResponse> findWorkSiteClosedDays(Integer workSiteId, int page, int size, Authentication authentication) {

        var workSite = findWorkSiteBySpecialId(workSiteId);

        Set<LocalDate> innactiveDays = workSite.getInactiveDays();
        if(innactiveDays == null) {
            innactiveDays = new HashSet<>();
        }
        var response = WorkSiteClosedDaysResponse.builder()
                .workSiteId(workSite.getId())
                .siteName(workSite.getSiteName())
                .closedDays(new ArrayList<>(innactiveDays))
                .build();

        List<WorkSiteClosedDaysResponse> responseList = List.of(response);

        return new PageResponse<>(
                responseList,
                page,
                size,
                responseList.size(),
                1,
                true,
                true
        );
    }

    @Override
    public PageResponse<WorkerCurrentlyWorkingInWorkSite> findAllWorkerInWorkSiteRelated(Integer workSiteId, int page, int size, Authentication authentication) {
        User user = ((User) authentication.getPrincipal());

        if(!user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"))){
            throw new AccessDeniedException("Only admins have an access to this function");
        }
        var adminsCompany = user.getCompany().getId();
        var foundedWorksite = findWorkSiteById(workSiteId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("wa.checkInTime").descending());
        Page<User> findAllUsersLocatedInWorkSite = userRepository.findAllUsersLocatedInWorkSite(pageable, adminsCompany, foundedWorksite.getWorkSiteId());
        List<WorkerCurrentlyWorkingInWorkSite> workerCurrentlyWorkingInWorkSitesResponse = findAllUsersLocatedInWorkSite.getContent().stream()
                .map(users -> {
                    WorkerAttendance latestAttendance = workerAttendanceRepository
                            .findFirstByWorkerAndCheckOutTimeIsNullOrderByCheckInTimeDesc(users)
                            .orElseThrow(() -> new RuntimeException("No attendance record found!"));

                    return WorkerCurrentlyWorkingInWorkSite.builder()
                            .workerId(users.getId())
                            .workSiteId(workSiteId)
                            .punchedIn(latestAttendance.getCheckInTime())
                            .workerFullName(users.getFirstName() + " " + users.getLastName())
                            .workerPhoneNumber(users.getPhoneNumber())
                            .workSiteName(foundedWorksite.getWorkSiteName())
                            .workSiteAddress(foundedWorksite.getAddress())
                            .build();

                }).toList();

        return new PageResponse<>(
                workerCurrentlyWorkingInWorkSitesResponse,
                findAllUsersLocatedInWorkSite.getNumber(),
                findAllUsersLocatedInWorkSite.getSize(),
                findAllUsersLocatedInWorkSite.getTotalElements(),
                findAllUsersLocatedInWorkSite.getTotalPages(),
                findAllUsersLocatedInWorkSite.isFirst(),
                findAllUsersLocatedInWorkSite.isLast()
        );




    }

    private User checkIsUserAuthenticatedAndFindHim(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(user.getId() == null) {
            throw new EntityNotFoundException("Cannot find User!");
        }
        return userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot find User!"));
    }

    private User checkIsUserHasAdminRoleAndBusinessOwner(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if(!user.isAdmin() && !user.isBusinessOwner()) {
            throw new AccessDeniedException("You dont have permission for this operation!");
        }
        return user;
    }

    private WorkSite findWorkSiteBySpecialId(Integer workSiteId){
        return workSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find WorkSite!"));
    }




    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Проверка валидности координат
        if (lat1 < -90 || lat1 > 90 || lat2 < -90 || lat2 > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (lon1 < -180 || lon1 > 180 || lon2 < -180 || lon2 > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }

        final double R = 6371000; // Радиус Земли в метрах

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double x = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));

        return  R * y;

    }



}
