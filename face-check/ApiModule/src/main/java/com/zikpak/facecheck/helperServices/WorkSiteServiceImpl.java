package com.zikpak.facecheck.helperServices;

import com.zikpak.facecheck.domain.WorkSiteService;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.entity.employee.WorkerAttendance;
import com.zikpak.facecheck.mapper.WorkSiteMapper;
import com.zikpak.facecheck.metrics.MetricsWorkSiteService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.requestsResponses.PageResponse;
import com.zikpak.facecheck.requestsResponses.WorkSiteAllInformationResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteClosedDaysResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkerCurrentlyWorkingInWorkSite;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.selectWorkSite.SelectWorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.*;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationRequest;
import com.zikpak.facecheck.taxesServices.services.notificationService.NotificationService;
import io.micrometer.core.instrument.Timer;
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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

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
    private final CompanyRepository companyRepository;

    private final NotificationService notificationService;

    private final MetricsWorkSiteService metric;


    @Override
    @Cacheable(
            value = "workSite",
            key = "#id",
            unless = "#result == null"
    )
    public WorkSiteResponse findWorkSiteById(Integer id) {
        Timer.Sample timer = metric.startTimer();
        try {
            var foundedWorkSite = workSiteRepository.findByIdWithCompany(id)
                    .orElseThrow(() -> new EntityNotFoundException("Work site not found"));
            metric.recordWorkSiteById(foundedWorkSite.getSiteName(), foundedWorkSite.getId(), true);
            metric.recordOperationTime(timer, "find_worksite_success");

            return workSiteMapper.toWorkSiteResponse(foundedWorkSite);
        } catch (Exception e) {
            metric.recordWorkSiteById("unknown", 0, false);
            metric.recordOperationTime(timer, "find_worksite_failed");
            metric.recordError("f_worksite_failed", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Cacheable(
            value = "workSites",
            key = "#authentication.principal.company.id + '_page_' + #page + '_size_' + #size",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PageResponse<WorkSiteResponse> findAllWorkSites(Authentication authentication, int page, int size) {
        Timer.Sample timer = metric.startTimer();
        try {

            User user = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page, size, Sort.by("siteName").descending());
            Page<WorkSite> workSites = workSiteRepository.findAllByCompanyId(user.getCompany().getId(), pageable);
            List<WorkSiteResponse> workSiteResponses = workSites.getContent().stream()
                    .map(workSiteMapper::toWorkSiteResponse)
                    .toList();

            for (WorkSite workSite : workSites) {
                metric.recordWorkSiteById(workSite.getSiteName(), workSite.getId(), true);
                metric.recordOperationTime(timer, "find_all_worksite_success");
            }

            return new PageResponse<>(
                    workSiteResponses,
                    workSites.getNumber(),
                    workSites.getSize(),
                    workSites.getTotalElements(),
                    workSites.getTotalPages(),
                    workSites.isFirst(),
                    workSites.isLast()
            );

        } catch (Exception e) {
            metric.recordWorkSiteById("unknown", 0, false);
            metric.recordOperationTime(timer, "find_all_worksite_failed");
            metric.recordError("f_all_worksite_failed", e.getMessage(), e);
            throw e;
        }
    }


    @Cacheable(
            value = "workSites",
            key = "'all_companies_page_' + #page + '_size_' + #size",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PageResponse<WorkSiteResponse> findAllWorkSitesFromAllCompanies(Authentication authentication, int page, int size) {
        Timer.Sample timer = metric.startTimer();
        try {

            checkIsUserHasAdminRoleAndBusinessOwner(authentication);

            Pageable pageable = PageRequest.of(page, size, Sort.by("siteName").descending());
            Page<WorkSite> workSites = workSiteRepository.findAll(pageable);
            List<WorkSiteResponse> workSiteResponses = workSites.getContent().stream()
                    .map(workSiteMapper::toWorkSiteResponse)
                    .toList();

            for (WorkSite workSite : workSites) {
                metric.recordWorkSiteById(workSite.getSiteName(), workSite.getId(), true);
                metric.recordOperationTime(timer, "find_all_worksite_success");
            }

            return new PageResponse<>(
                    workSiteResponses,
                    workSites.getNumber(),
                    workSites.getSize(),
                    workSites.getTotalElements(),
                    workSites.getTotalPages(),
                    workSites.isFirst(),
                    workSites.isLast()
            );

        } catch (Exception e) {
            metric.recordWorkSiteById("unknown", 0, false);
            metric.recordOperationTime(timer, "find_all_worksite_failed");
            metric.recordError("f_all_worksite_failed", e.getMessage(), e);
            throw e;
        }
    }




    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", allEntries = true)
    })
    public WorkSiteResponse createWorkSite(Authentication authentication, WorkSiteRequest request) {
        Timer.Sample timer = metric.startTimer();
        try {

            var admin  = checkIsUserHasAdminRoleAndBusinessOwner(authentication);
            var company = admin.getCompany();

            if(company == null) {
                throw new AccessDeniedException("Access dined");
            }
            var newWorkSite = WorkSite.builder()
                    .siteName(request.getWorkSiteName())
                    .address(request.getAddress())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .allowedRadius(request.getAllowedRadius())
                    .workDayStart(request.getWorkDayStart())
                    .isActive(true)
                    .isWorkerDidPunchIn(false)
                    .workDayEnd(request.getWorkDayEnd())
                    .company(company)
                    .build();
            var savedWorkSite = workSiteRepository.save(newWorkSite);

         //   company.addWorkSite(newWorkSite);

            companyRepository.save(company);

            NotificationRequest notification = NotificationRequest.builder()
                    .message("Worksite: " + newWorkSite.getSiteName()+ " " + newWorkSite.getAddress() +  " was successfully registered")
                    .adminOnly(true)
                    .build();

            notificationService.createNotification(company.getId(), notification);

            metric.recordWorkSiteById(newWorkSite.getSiteName(), newWorkSite.getId(), true);

            metric.recordOperationTime(timer, "create_new_work_success");

            return workSiteMapper.toWorkSiteResponse(savedWorkSite);
        } catch (Exception e) {
            metric.recordWorkSiteById("unknown", 0, false);
            metric.recordOperationTime(timer, "create_work_failed");
            metric.recordError("f_create_work_failed", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Transactional
    public SelectWorkSiteResponse selectWorkSite(Integer workSiteId, Authentication authentication) {
        Timer.Sample timer = metric.startTimer();
        try {
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
        } catch (Exception e) {
            metric.recordWorkSiteById("unknown", 0, false);
            metric.recordOperationTime(timer, "select_work_failed");
            metric.recordError("f_select_work_success", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse setNewCustomRadiusForWorker(
            Integer workSiteId,
            Integer workerId,
            SetNewCustomRadiusRequest customRadius,
            Authentication authentication) {

        Timer.Sample timer = metric.startTimer();
        try {
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

            metric.recordOperationTime(timer, "set_custom_radius_worker_success");
            metric.recordWorkSiteById(foundedWorkSite.getSiteName(), foundedWorkSite.getId(), true);
            metric.recordForWorkerRadius(
                    foundedWorkSite.getSiteName(),
                    foundedWorker.getCompany().getCompanyName(),
                    foundedWorker.getFirstName() + " " + foundedWorker.getLastName(),
                    foundedWorkSite.getId(),
                    customRadius.getCustomRadius(),
                    true
            );
            return SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse.builder()
                    .workSiteId(savedWorkSiteRadius.getId())
                    .workerId(foundedWorker.getId())
                    .firstName(foundedWorker.getFirstName())
                    .lastName(foundedWorker.getLastName())
                    .companyName(foundedWorker.getCompany().getCompanyName())
                    .newRadius(customRadius.getCustomRadius())
                    .build();
        } catch (Exception e) {
            metric.recordOperationTime(timer, "set_custom_radius__worker_failed");
            metric.recordWorkSiteById("unknown", 0, false);
            throw e;
        }
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public SetNewCustomRadiusResponse setCustomRadiusForWorkSite(Authentication authentication,
                                                                 Integer workSiteId,
                                                                 SetNewCustomRadiusRequest customRadius) {
        Timer.Sample timer = metric.startTimer();
        try {
            checkIsUserHasAdminRoleAndBusinessOwner(authentication);

            var foundedWorkSite = workSiteRepository.findById(workSiteId)
                    .orElseThrow(() -> new EntityNotFoundException("Work site with id " + workSiteId + " not found"));
            log.info("Finding worksite: " + foundedWorkSite.getSiteName());

            if (!foundedWorkSite.getIsActive()) {
                throw new IllegalStateException("Work site is not active");
            }
            if (foundedWorkSite.getAllowedRadius().equals(customRadius.getCustomRadius())) {
                throw new IllegalStateException("Custom radius can't be the same");
            }

            foundedWorkSite.setAllowedRadius(customRadius.getCustomRadius());
            log.info("Setting new allowed radius! " + customRadius.getCustomRadius());
            var savedWorkSiteRadius = workSiteRepository.save(foundedWorkSite);
            log.info("Saving radius!");

            metric.recordOperationTime(timer, "set_custom_worksite_radius_success");
            metric.recordWorkSiteById(foundedWorkSite.getSiteName(), foundedWorkSite.getId(), true);
            metric.recordWorkSiteRadius(
                    foundedWorkSite.getSiteName(),
                    foundedWorkSite.getCompany().getCompanyName(),
                    foundedWorkSite.getId(),
                    customRadius.getCustomRadius(),
                    true
            );


            return SetNewCustomRadiusResponse.builder()
                    .workSiteId(savedWorkSiteRadius.getId())
                    .customRadius(savedWorkSiteRadius.getAllowedRadius())
                    .build();
        } catch (Exception e) {
            metric.recordOperationTime(timer, "set_worksite_custom_radius_failed");
            metric.recordWorkSiteById("unknown", 0, false);
            metric.recordError("f_set_worksite_custom_radius_failed", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public WorkSiteUpdateNameResponse updateWorkSiteName(Authentication authentication,
                                                         Integer workSiteId,
                                                         UpdateNameRequest request) {
        Timer.Sample timer = metric.startTimer();
        try {
            checkIsUserHasAdminRoleAndBusinessOwner(authentication);
            var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
            if (foundedWorkSite.getSiteName().equals(request.getName())) {
                throw new IllegalArgumentException("Work site name already exists");
            }
            foundedWorkSite.setSiteName(request.getName());
            workSiteRepository.save(foundedWorkSite);

            metric.recordOperationTime(timer, "update_worksite_name_success");
            metric.recordWorkSiteById(foundedWorkSite.getSiteName(), foundedWorkSite.getId(), true);
            return workSiteMapper.toWorkSiteUpdateNameResponse(foundedWorkSite);
        } catch (Exception e) {
            metric.recordOperationTime(timer, "update_worksite_name_failed");
            metric.recordWorkSiteById("unknown", 0, false);
            throw e;
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public WorkSiteUpdateAddressResponse updateWorkSiteAddress(Authentication authentication, Integer workSiteId, UpdateWorkSiteAddress updateWorkSiteAddress) {
        Timer.Sample timer = metric.startTimer();
        try {

            checkIsUserHasAdminRoleAndBusinessOwner(authentication);
            var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
            if (foundedWorkSite.getAddress().equals(updateWorkSiteAddress.getAddress())) {
                throw new IllegalArgumentException("Work site address already exists");
            }
            foundedWorkSite.setAddress(updateWorkSiteAddress.getAddress());
            workSiteRepository.save(foundedWorkSite);
            metric.recordOperationTime(timer, "update_worksite_address_success");
            return workSiteMapper.toWorkSiteUpdateAddressResponse(foundedWorkSite);
        } catch (Exception e) {
            metric.recordOperationTime(timer, "update_worksite_address_failed");
            metric.recordError("update_worksite_address_failed", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public WorkSiteUpdateWorkingHoursResponse updateWorkingHours(Authentication authentication, Integer workSiteId, WorkSiteUpdateWorkingHoursRequest request) {
        Timer.Sample timer = metric.startTimer();
        try {
            checkIsUserHasAdminRoleAndBusinessOwner(authentication);
            var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
            if (!foundedWorkSite.getIsActive()) {
                throw new IllegalStateException("Work site is not active");
            }

            foundedWorkSite.setWorkDayStart(request.getNewStart());
            foundedWorkSite.setWorkDayEnd(request.getNewEnd());
            workSiteRepository.save(foundedWorkSite);
            metric.recordOperationTime(timer, "update_worksite_workinghours_success");
            return workSiteMapper.toWorkSiteUpdateWorkingHoursResponse(foundedWorkSite);
        } catch (Exception e) {
            metric.recordOperationTime(timer, "update_worksite_workinghours_failed");
            metric.recordError("update_worksite_workinghours_failed", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public WorkSiteUpdateLocationResponse updateWorkSiteLocation(Authentication authentication, Integer workSiteId, WorkSiteUpdateLocationRequest request) {
        Timer.Sample timer = metric.startTimer();
        try {
            checkIsUserHasAdminRoleAndBusinessOwner(authentication);
            var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
            if (!foundedWorkSite.getIsActive()) {
                throw new IllegalStateException("Work site is not active");
            }

            foundedWorkSite.setLatitude(request.getNewLatitude());
            foundedWorkSite.setLongitude(request.getNewLongitude());
            foundedWorkSite.setAllowedRadius(request.getNewRadius());

            workSiteRepository.save(foundedWorkSite);
            metric.recordOperationTime(timer, "update_worksite_location_success");
            metric.recordWorkSiteLatLon(
                    foundedWorkSite.getSiteName(),
                    foundedWorkSite.getCompany().getCompanyName(),
                    foundedWorkSite.getId(),
                    foundedWorkSite.getLatitude(),
                    foundedWorkSite.getLongitude(),
                    true
            );
            return workSiteMapper.toWorkSiteUpdateLocationResponse(foundedWorkSite);
        } catch (Exception e) {
            metric.recordOperationTime(timer, "update_worksite_location_failed");
            metric.recordError("update_worksite_location_failed", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public void setWorkSiteActiveOrNotActive(Authentication authentication, Integer workSiteId, UpdateStatusWorkSiteRequest updateStatusWorkSiteRequest) {
        Timer.Sample timer = metric.startTimer();
        try {
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
            log.info("Work site {} status changed to {}", workSiteId, active ? "active" : "inactive");
            metric.recordOperationTime(timer, "set_worksite_active_not_success");

        } catch (Exception e) {
            metric.recordOperationTime(timer, "set_worksite_active_not_failed");
            metric.recordError("set_worksite_active_not_failed", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public ScheduleInactiveDayResponse scheduleInactiveDay(Authentication authentication, Integer workSiteId, ScheduleInactiveDayRequest inactiveDate) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);

        if (inactiveDate.getInactiveDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot schedule inactive day in past!");
        }
        if (foundedWorkSite.getInactiveDays().contains(inactiveDate.getInactiveDate())) {
            throw new IllegalStateException("Inactive day is already exist with this date!");
        }

        if (inactiveDate.getInactiveDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
            throw new IllegalStateException("Cannot schedule inactive day, in Saturday!");
        }

        Set<LocalDate> inactiveDays = foundedWorkSite.getInactiveDays();
        if (inactiveDays == null) {
            inactiveDays = new HashSet<>();
        }

        if (inactiveDays.contains(inactiveDate)) {
            throw new IllegalStateException("Cannot schedule inactive day in past!");
        }

        inactiveDays.add(inactiveDate.getInactiveDate());
        foundedWorkSite.setInactiveDays(inactiveDays);
        var savedInactiveDay = workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteScheduleInactiveDay(savedInactiveDay);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public ScheduleInactiveDayResponse removeInactiveDay(Authentication authentication, Integer workSiteId, ScheduleInactiveDayRequest inactiveDate) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);
        if (inactiveDate.getInactiveDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot remove inactive day in past!");
        }


        if (inactiveDate.getInactiveDate().getDayOfWeek() == DayOfWeek.SATURDAY) {
            throw new IllegalStateException("Cannot remove inactive day, in Saturday!");
        }

        Set<LocalDate> inactiveDays = foundedWorkSite.getInactiveDays();
        if (inactiveDays == null || !inactiveDays.contains(inactiveDate.getInactiveDate())) {
            throw new IllegalStateException("This date is not scheduled as inactive day!");
        }

        inactiveDays.remove(inactiveDate.getInactiveDate());
        foundedWorkSite.setInactiveDays(inactiveDays);
        var savedInactiveDate = workSiteRepository.save(foundedWorkSite);
        return workSiteMapper.toWorkSiteScheduleInactiveDay(savedInactiveDate);
    }

    @Override

    public boolean isWorkSiteActive(Integer workSiteId) {
        Timer.Sample timer = metric.startTimer();
        try {
            var foundedWorkSite = findWorkSiteBySpecialId(workSiteId);

            if (!foundedWorkSite.getIsActive()) {
                return false;
            }

            LocalDate today = LocalDate.now();
            Set<LocalDate> inactiveDays = foundedWorkSite.getInactiveDays();
            if (inactiveDays != null && inactiveDays.contains(today)) {
                return false;
            }

            if (today.getDayOfWeek() == DayOfWeek.SATURDAY) {
                return false;
            }
            metric.recordOperationTime(timer, "is_work_site_active_success");
            return true;
        } catch (Exception e) {
            metric.recordOperationTime(timer, "is_work_site_active_failed");
            throw e;
        }
    }




    @Override
    public IsWithinRadiusResponse isWithinRadius(Integer workSiteId, IsWithinRadiusRequest isWithinRadiusRequest) {
        Timer.Sample timer = metric.startTimer();
        try {

            var foundedWorkSite = findWorkSiteById(workSiteId);

            if (foundedWorkSite.getLatitude() == null || foundedWorkSite.getLongitude() == null ||
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
            metric.recordOperationTime(timer, "is_within_radius_success");
            metric.recordWorkSiteLatLon(
                    foundedWorkSite.getWorkSiteName(),
                    "",
                    foundedWorkSite.getWorkSiteId(),
                    foundedWorkSite.getLatitude(),
                    foundedWorkSite.getLongitude(),
                    true
            );



            return IsWithinRadiusResponse.builder()
                    .worksiteId(foundedWorkSite.getWorkSiteId())
                    .providedLatitude(isWithinRadiusRequest.getLatitude())
                    .providedLongitude(isWithinRadiusRequest.getLongitude())
                    .actualLatitude(foundedWorkSite.getLatitude())
                    .actualLongitude(foundedWorkSite.getLongitude())
                    .allowedRadius(foundedWorkSite.getAllowedRadius())
                    .isWithinRadius(isWithinRadius)
                    .build();
        } catch (Exception e) {
            metric.recordOperationTime(timer, "is_within_radius_failed");
            metric.recordError("is_within_radius_failed", e.getMessage(), e);
            throw e;
        }

    }

    public boolean isWithinRadiusForPunchInOut(Integer workSiteId, Double userLatitude, Double userLongitude) {
        Timer.Sample timer = metric.startTimer();
        try {
            var foundedWorkSite = findWorkSiteById(workSiteId);

            if (foundedWorkSite.getLatitude() == null || foundedWorkSite.getLongitude() == null ||
                    foundedWorkSite.getAllowedRadius() == null) {
                throw new IllegalStateException("Work site is not active");
            }

            double distance = calculateDistance(
                    foundedWorkSite.getLatitude(),
                    foundedWorkSite.getLongitude(),
                    userLatitude,
                    userLongitude
            );
            metric.recordOperationTime(timer, "is_within_radius_for_punch_in_out_success");
            metric.recordWorkSiteLatLon(
                    foundedWorkSite.getWorkSiteName(),
                    " ",
                    foundedWorkSite.getWorkSiteId(),
                    foundedWorkSite.getLatitude(),
                    foundedWorkSite.getLongitude(),
                    true
            );
            return distance <= foundedWorkSite.getAllowedRadius();

        } catch (Exception e) {
            metric.recordOperationTime(timer, "is_within_radius_for_punch_in_out_failed");
            metric.recordError("is_within_radius_for_punch_out_failed", e.getMessage(), e);
            throw e;
        }

    }


    @Override
    public boolean canPunchInOut(Authentication authentication, Integer workSiteId, Integer userId, CanPunchOutRequestWorkSite canPunchOutRequestWorkSite) {
        Timer.Sample timer = metric.startTimer();
        try {
            checkIsUserAuthenticatedAndFindHim(authentication);
            var workSite = findWorkSiteBySpecialId(workSiteId);

            if (!workSite.getIsActive()) {
                throw new IllegalStateException("Work site is not active");
            }

            var earlierPunchInTime = workSite.getWorkDayStart().minusMinutes(10);
            var latestPunchOutTime = workSite.getWorkDayEnd().plusMinutes(10);

            if (canPunchOutRequestWorkSite.getCanPunchOut().isBefore(earlierPunchInTime)) {
                return false;
            }
            if (canPunchOutRequestWorkSite.getCanPunchOut().isAfter(latestPunchOutTime)) {
                return false;
            }

            metric.recordOperationTime(timer, "can_punch_in_out_success");
            metric.recordWorkSiteLatLon(
                    workSite.getSiteName(),
                    workSite.getCompany().getCompanyName(),
                    workSite.getId(),
                    workSite.getLatitude(),
                    workSite.getLongitude(),
                    true
            );

            return true;
        } catch (Exception e) {
            metric.recordOperationTime(timer, "can_punch_in_out_failed");
            metric.recordError("can_punch_in_out_failed", e.getMessage(), e);
            throw e;
        }
    }

    public boolean canPunchInOutForWorkAttendance(Authentication authentication, Integer workSiteId, Integer userId, LocalTime currentTime) {
        Timer.Sample timer = metric.startTimer();
        try {
            checkIsUserAuthenticatedAndFindHim(authentication);
            var workSite = findWorkSiteBySpecialId(workSiteId);

            if (workSite.getWorkDayStart() == null || workSite.getWorkDayEnd() == null) {
                throw new IllegalStateException("Work site schedule is not set");
            }
            if (!workSite.getIsActive()) {
                throw new IllegalStateException("Work site is inactive");
            }

            var earlierPunchInTime = workSite.getWorkDayStart().minusMinutes(10);
            var latestPunchOutTime = workSite.getWorkDayEnd().plusMinutes(10);

            boolean isOvernightShift = workSite.getWorkDayEnd().isBefore(workSite.getWorkDayStart());
            metric.recordOperationTime(timer, "can_punch_in_out_work_attendance_success");
            metric.recordWorkSiteLatLon(
                    workSite.getSiteName(),
                    workSite.getCompany().getCompanyName(),
                    workSite.getId(),
                    workSite.getLatitude(),
                    workSite.getLongitude(),
                    true
            );

            if (currentTime.isBefore(earlierPunchInTime)) {
                throw new IllegalStateException("You cannot punch in - it's too early!");
            }

            if (isOvernightShift) {
                return currentTime.isAfter(earlierPunchInTime) ||
                        currentTime.isBefore(latestPunchOutTime);
            } else {
                return currentTime.isAfter(earlierPunchInTime) &&
                        currentTime.isBefore(latestPunchOutTime);
            }

        } catch (Exception e) {
            metric.recordOperationTime(timer, "can_punch_in_out_work_attendance_failed");
            metric.recordError("can_punch_in_out_work_attendance_failed", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Cacheable(
            value = "workSite",
            key = "'closedDays_' + #workSiteId",
            unless = "#result == null"
    )
    public PageResponse<WorkSiteClosedDaysResponse> findWorkSiteClosedDays(Integer workSiteId, int page, int size, Authentication authentication) {

        var workSite = findWorkSiteBySpecialId(workSiteId);

        Set<LocalDate> innactiveDays = workSite.getInactiveDays();
        if (innactiveDays == null) {
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
    public PageResponse<WorkerCurrentlyWorkingInWorkSite> findAllWorkerInWorkSiteRelated(Integer workSiteId,
                                                                                         int page,
                                                                                         int size,
                                                                                         Authentication authentication) {

        User user = ((User) authentication.getPrincipal());

        if (!user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"))) {
            throw new AccessDeniedException("Only admins have an access to this function");
        }

        var adminsCompany = user.getCompany().getId();
        var foundedWorksite = findWorkSiteById(workSiteId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("wa.checkInTime").descending());
        Page<Object[]> results = userRepository.findAllActiveWorkersWithAttendance(pageable, adminsCompany, foundedWorksite.getWorkSiteId());

        List<WorkerCurrentlyWorkingInWorkSite> workerCurrentlyWorkingInWorkSitesResponse = results.getContent().stream()
                .map(arr -> {
                    User users = (User) arr[0];
                    WorkerAttendance latestAttendance = (WorkerAttendance) arr[1];

                    return WorkerCurrentlyWorkingInWorkSite.builder()
                            .workerId(users.getId())
                            .workSiteId(workSiteId)
                            .punchedIn(latestAttendance.getCheckInTime())
                            .workerFullName(users.getFirstName() + " " + users.getLastName())
                            .workerPhoneNumber(users.getPhoneNumber())
                            .photoUrl(users.getPhotoUrl())
                            .workSiteName(foundedWorksite.getWorkSiteName())
                            .workSiteAddress(foundedWorksite.getAddress())
                            .build();
                }).toList();

        return new PageResponse<>(
                workerCurrentlyWorkingInWorkSitesResponse,
                results.getNumber(),
                results.getSize(),
                results.getTotalElements(),
                results.getTotalPages(),
                results.isFirst(),
                results.isLast()
        );
    }


    @Transactional(rollbackOn = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "workSites", allEntries = true),
            @CacheEvict(value = "workSite", key = "#workSiteId")
    })
    public void deleteWorkSiteById(Authentication authentication, Integer workSiteId) {
        var admin = checkIsUserHasAdminRoleAndBusinessOwner(authentication);

        var workSite = workSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new EntityNotFoundException("WorkSite not found"));

        if (!workSite.getCompany().getId().equals(admin.getCompany().getId())) {
            throw new AccessDeniedException("Access denied");
        }

        workSiteRepository.clearWorkSiteUserAssociations(workSiteId);

        workSiteRepository.clearCurrentWorkSiteReferences(workSiteId);

        workSiteRepository.deleteById(workSiteId);
    }
    @Cacheable(
            value = "workSite",
            key = "'allInfo_' + #workSiteId",  // ← ИСПРАВЬ!
            unless = "#result == null"
    )
    public WorkSiteAllInformationResponse findWorkSiteAllInformationById(Authentication authentication, Integer workSiteId) {
        checkIsUserHasAdminRoleAndBusinessOwner(authentication);
        var workSite = findWorkSiteBySpecialId(workSiteId);
        return workSiteMapper.toWorkSiteAllInformationResponse(workSite);
    }


    public Integer countAllWorksitesRelatedToTheCompany(Authentication authentication) {
        var admin = (User) authentication.getPrincipal();
        var companyId  = admin.getCompany().getId();
        var foundedWorksites = workSiteRepository.findAllByCompanyId(companyId);
        return foundedWorksites.size();
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

        boolean isAppOwner = user.getRoles().stream()
                .anyMatch(role -> "AppOwner".equals(role.getName()));

        if(!user.isAdmin() && !user.isBusinessOwner() && !isAppOwner) {
            throw new AccessDeniedException("You dont have permission for this operation!");
        }
        return user;
    }

    private WorkSite findWorkSiteBySpecialId(Integer workSiteId){
        return workSiteRepository.findByIdWithCompany(workSiteId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find WorkSite!"));
    }




    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
       Timer.Sample timer = metric.startTimer();
       try {

           if (lat1 < -90 || lat1 > 90 || lat2 < -90 || lat2 > 90) {
               throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
           }
           if (lon1 < -180 || lon1 > 180 || lon2 < -180 || lon2 > 180) {
               throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
           }

           final double R = 6371000;

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
            metric.recordOperationTime(timer, "calculate_distance_success");
            metric.recordDistance(x,y, true);
           return R * y;
       } catch (Exception e) {
           metric.recordOperationTime(timer, "calculate_distance_failed");
           metric.recordError("calculate_distance_failed", e.getMessage(), e);
           throw e;

       }

    }


}
