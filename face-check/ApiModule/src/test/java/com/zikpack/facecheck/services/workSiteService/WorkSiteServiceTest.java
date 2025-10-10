package com.zikpack.facecheck.services.workSiteService;

import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.helperServices.WorkSiteServiceImpl;
import com.zikpak.facecheck.mapper.WorkSiteMapper;
import com.zikpak.facecheck.metrics.MetricsWorkSiteService;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.updates.*;
import com.zikpak.facecheck.services.workSiteService.WorkSiteService;
import io.micrometer.core.instrument.Timer;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkSiteServiceTest {

    @InjectMocks
    private WorkSiteServiceImpl workSiteService;

    @Mock
    private WorkerSiteRepository workSiteRepository;

    @Mock
    WorkSiteMapper workSiteMapper;

    @Mock
    private MetricsWorkSiteService metric;

    @Mock
    private Authentication authentication;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    private WorkSiteResponse workSiteResponse;
    private WorkSiteRequest workSiteRequest;
    private WorkSite workSite;
    private WorkSite workSite2;
    private WorkSite workSite3;
    private WorkSite savedWorkSite;
    private SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse setNewCustomRadiusForWorkerInSpecialWorkSiteResponse;
    private SetNewCustomRadiusRequest setNewCustomRadiusRequest;
    private SetNewCustomRadiusResponse setNewCustomRadiusResponse;

    private WorkSiteUpdateLocationResponse workSiteUpdateLocationResponse;
    private WorkSiteUpdateLocationRequest workSiteUpdateLocationRequest;

    private UpdateStatusWorkSiteRequest updateStatusWorkSiteRequestTrue;
    private UpdateStatusWorkSiteRequest updateStatusWorkSiteRequestFalse;



    private UpdateNameRequest updateNameRequest;
    private UpdateNameRequest updateNameRequestFail;
    private WorkSiteUpdateNameResponse workSiteUpdateNameResponse;

    private UpdateWorkSiteAddress updateWorkSiteAddress;
    private UpdateWorkSiteAddress updateWorkSiteAddressFail;
    private WorkSiteUpdateAddressResponse workSiteUpdateAddressResponse;


    private WorkSiteUpdateWorkingHoursRequest updateWorkingHoursRequest;
    private WorkSiteUpdateWorkingHoursResponse workSiteUpdateWorkingHoursResponse;

    private IsWithinRadiusResponse isWithinRadiusResponse;
    private IsWithinRadiusRequest isWithinRadiusRequest;

    private Timer.Sample timerSample;
    private Company company;
    private User admin;
    private User worker;


    @BeforeEach
    public void setUp() {

        company = new Company();
        company.setId(1);
        company.setCompanyName("LLC");
        company.setCompanyAddress("address");
        company.setCompanyPhone("123");
        company.setCompanyEmail("email");
        company.setWorkersQuantity(50);


        admin = new User();
        admin.setId(1);
        admin.setCompany(company);
        admin.setBusinessOwner(true);

        worker = new User();
        worker.setId(2);
        worker.setFirstName("test");
        worker.setLastName("test2");
        worker.setCompany(company);
        worker.setBusinessOwner(false);
        worker.setUser(true);

        workSite = new WorkSite();
        workSite.setId(1);
        workSite.setSiteName("test");
        workSite.setAddress("address");
        workSite.setLatitude(1.0);
        workSite.setLongitude(2.0);
        workSite.setAllowedRadius(3.0);
        workSite.setWorkDayStart(null);
        workSite.setWorkDayEnd(null);
        workSite.setIsActive(true);
        workSite.setCompany(company);


        workSiteResponse = new WorkSiteResponse();
        workSiteResponse.setWorkSiteId(1);
        workSiteResponse.setWorkSiteName("test");
        workSiteResponse.setAddress("address");
        workSiteResponse.setLatitude(1.0);
        workSiteResponse.setLongitude(2.0);
        workSiteResponse.setAllowedRadius(3.0);
        workSiteResponse.setWorkDayStart(null);
        workSiteResponse.setWorkDayEnd(null);

        workSiteRequest = new WorkSiteRequest();
        workSiteRequest.setWorkSiteName("test");
        workSiteRequest.setAddress("address");
        workSiteRequest.setLatitude(1.0);
        workSiteRequest.setLongitude(2.0);
        workSiteRequest.setAllowedRadius(3.0);
        workSiteRequest.setWorkDayStart(null);
        workSiteRequest.setWorkDayEnd(null);

        savedWorkSite = new WorkSite();
        savedWorkSite.setId(1);
        savedWorkSite.setSiteName("test");
        savedWorkSite.setAddress("address");
        savedWorkSite.setLatitude(1.0);
        savedWorkSite.setLongitude(2.0);
        savedWorkSite.setAllowedRadius(3.0);
        savedWorkSite.setWorkDayStart(null);
        savedWorkSite.setWorkDayEnd(null);

        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse = new SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse();
        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse.setWorkSiteId(1);
        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse.setWorkerId(worker.getId());
        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse.setFirstName("test");
        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse.setLastName("test2");
        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse.setCompanyName(company.getCompanyName());
        setNewCustomRadiusForWorkerInSpecialWorkSiteResponse.setNewRadius(4.0);

        setNewCustomRadiusResponse = new SetNewCustomRadiusResponse();
        setNewCustomRadiusResponse.setWorkSiteId(1);
        setNewCustomRadiusResponse.setCustomRadius(4.0);



        setNewCustomRadiusRequest = new SetNewCustomRadiusRequest();
        setNewCustomRadiusRequest.setCustomRadius(5.0);

        workSite2 = new WorkSite();
        workSite2.setId(3);
        workSite2.setSiteName("test3");
        workSite2.setAddress("address3");
        workSite2.setLatitude(1.0);
        workSite2.setLongitude(2.0);
        workSite2.setAllowedRadius(5.0);
        workSite2.setWorkDayStart(null);
        workSite2.setWorkDayEnd(null);
        workSite2.setIsActive(true);

        workSite3 = new WorkSite();
        workSite3.setId(5);
        workSite3.setSiteName("test3");
        workSite3.setAddress("address3");
        workSite3.setLatitude(1.0);
        workSite3.setLongitude(2.0);
        workSite3.setAllowedRadius(5.0);
        workSite3.setWorkDayStart(null);
        workSite3.setWorkDayEnd(null);
        workSite3.setIsActive(false);

        updateNameRequest = new UpdateNameRequest();
        updateNameRequest.setName("test5");

        updateNameRequestFail = new UpdateNameRequest();
        updateNameRequestFail.setName("test");

        workSiteUpdateNameResponse = new WorkSiteUpdateNameResponse();
        workSiteUpdateNameResponse.setWorkSiteName("test5");
        workSiteUpdateNameResponse.setWorksiteId(workSite.getId());



        updateWorkSiteAddress = new UpdateWorkSiteAddress();
        updateWorkSiteAddress.setAddress("adress8");

        workSiteUpdateAddressResponse = new WorkSiteUpdateAddressResponse();
        workSiteUpdateAddressResponse.setWorksiteId(workSite.getId());
        workSiteUpdateAddressResponse.setWorkSiteAddress("address8");



        updateWorkSiteAddressFail = new UpdateWorkSiteAddress();
        updateWorkSiteAddressFail.setAddress("address");

        updateWorkingHoursRequest = new WorkSiteUpdateWorkingHoursRequest();
        updateWorkingHoursRequest.setNewStart(LocalTime.of(7,30,0));
        updateWorkingHoursRequest.setNewEnd(LocalTime.of(16,30,0));


        workSiteUpdateWorkingHoursResponse = new WorkSiteUpdateWorkingHoursResponse();
        workSiteUpdateWorkingHoursResponse.setWorkSiteId(workSite.getId());
        workSiteUpdateWorkingHoursResponse.setNewStart(LocalTime.of(7,30,0));
        workSiteUpdateWorkingHoursResponse.setNewEnd(LocalTime.of(16,30,0));

        workSiteUpdateLocationRequest = new WorkSiteUpdateLocationRequest();
        workSiteUpdateLocationRequest.setNewLatitude(20.0);
        workSiteUpdateLocationRequest.setNewLongitude(21.0);
        workSiteUpdateLocationRequest.setNewRadius(100.0);

        workSiteUpdateLocationResponse = new WorkSiteUpdateLocationResponse();
        workSiteUpdateLocationResponse.setWorkSiteId(workSite.getId());
        workSiteUpdateLocationResponse.setNewLatitude(20.0);
        workSiteUpdateLocationResponse.setNewLongitude(21.0);
        workSiteUpdateLocationResponse.setNewRadius(100.0);

        updateStatusWorkSiteRequestTrue = new UpdateStatusWorkSiteRequest();
        updateStatusWorkSiteRequestTrue.setActive(true);

        updateStatusWorkSiteRequestFalse = new UpdateStatusWorkSiteRequest();
        updateStatusWorkSiteRequestFalse.setActive(false);

        isWithinRadiusRequest = new IsWithinRadiusRequest();
        isWithinRadiusRequest.setLongitude(2.0);
        isWithinRadiusRequest.setLatitude(1.0);

        isWithinRadiusResponse = new IsWithinRadiusResponse();
        isWithinRadiusResponse.setWorksiteId(workSite.getId());
        isWithinRadiusResponse.setProvidedLongitude(2.0);
        isWithinRadiusResponse.setProvidedLatitude(1.0);
        isWithinRadiusResponse.setActualLatitude(workSite.getLatitude());
        isWithinRadiusResponse.setActualLongitude(workSite.getLongitude());
        isWithinRadiusResponse.setAllowedRadius(workSite.getAllowedRadius());
        isWithinRadiusResponse.setWithinRadius(true);


        timerSample = Mockito.mock(Timer.Sample.class);
        when(metric.startTimer()).thenReturn(timerSample);
    }
    @Test
    void isWithinRadius_success(){
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteMapper.toWorkSiteResponse(workSite))
                .thenReturn(workSiteResponse);

        IsWithinRadiusResponse actual = workSiteService
                .isWithinRadius(
                  workSite.getId(),
                  isWithinRadiusRequest
                );

        assertEquals(isWithinRadiusResponse, actual);
        assertEquals(isWithinRadiusResponse.getWorksiteId(), actual.getWorksiteId());
        assertEquals(isWithinRadiusResponse.getProvidedLongitude(), actual.getProvidedLongitude());
        assertEquals(isWithinRadiusResponse.getProvidedLatitude(), actual.getProvidedLatitude());
        assertEquals(true, actual.isWithinRadius());

        verify(workSiteRepository).findById(workSite.getId());

        verify(metric).recordOperationTime(timerSample,"is_within_radius_success");
        verify(metric).recordWorkSiteLatLon(
                workSite.getSiteName(),
                "",
                workSite.getId(),
                workSite.getLatitude(),
                workSite.getLongitude(),
                true
        );
    }




    @Test
    void isWorkSiteActive_success_true(){
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));

        workSiteService.isWorkSiteActive(workSite.getId());

        assertEquals(true, workSite.getIsActive());
      //  verify(metric).recordOperationTime(timerSample,"is_work_site_active_success");

    }



    @Test
    void setWorkSiteActiveOrNotActive_throwIllegalStateException_true(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));

        IllegalStateException ex = assertThrows(
          IllegalStateException.class,
          () -> workSiteService.setWorkSiteActiveOrNotActive(
                  authentication,
                  workSite.getId(),
                  updateStatusWorkSiteRequestTrue
          )
        );
        assertEquals("Work site is already active", ex.getMessage());
        verify(metric).recordOperationTime(timerSample,"set_worksite_active_not_failed");
        verify(metric).recordError("set_worksite_active_not_failed", ex.getMessage(), ex);
    }

    @Test
    void setWorkSiteActiveOrNotActive_throwIllegalStateException_false(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite3.getId())).thenReturn(Optional.of(workSite3));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> workSiteService.setWorkSiteActiveOrNotActive(
                        authentication,
                        workSite3.getId(),
                        updateStatusWorkSiteRequestFalse
                )
        );
        assertEquals("Work site is already inactive", ex.getMessage());
        verify(metric).recordOperationTime(timerSample,"set_worksite_active_not_failed");
        verify(metric).recordError("set_worksite_active_not_failed", ex.getMessage(), ex);
    }

    @Test
    void setWorkSiteActiveOrNotActive_success_true(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite3.getId())).thenReturn(Optional.of(workSite3));
        when(workSiteRepository.save(workSite3)).thenReturn(workSite3);

        workSiteService.setWorkSiteActiveOrNotActive(
                authentication,
                workSite3.getId(),
                updateStatusWorkSiteRequestTrue
        );

        assertEquals(true, workSite3.getIsActive());
        verify(workSiteRepository).findById(workSite3.getId());
        verify(workSiteRepository).save(workSite3);
        verify(metric).recordOperationTime(timerSample,"set_worksite_active_not_success");
    }


    @Test
    void setWorkSiteActiveOrNotActive_success_false(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);

        workSiteService.setWorkSiteActiveOrNotActive(
                authentication,
                workSite.getId(),
                updateStatusWorkSiteRequestFalse
        );

        assertEquals(false, workSite.getIsActive());
        verify(workSiteRepository).findById(workSite.getId());
        verify(workSiteRepository).save(workSite);
        verify(metric).recordOperationTime(timerSample,"set_worksite_active_not_success");
    }


    @Test
    void updateLocation_failed_IllegalStateException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite3.getId())).thenReturn(Optional.of(workSite3));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> workSiteService.updateWorkSiteLocation(
                        authentication,
                        workSite3.getId(),
                        workSiteUpdateLocationRequest
                )
        );

        assertEquals("Work site is not active", ex.getMessage());

        verify(workSiteRepository).findById(workSite3.getId());
        verify(workSiteRepository, never()).save(any());
        verify(workSiteMapper, never()).toWorkSiteUpdateWorkingHoursResponse(any());

        verify(metric).recordOperationTime(timerSample,"update_worksite_location_failed");
        verify(metric).recordError("update_worksite_location_failed", ex.getMessage(), ex);

    }

    @Test
    void updateWorkSiteLocation_success(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);
        when(workSiteMapper.toWorkSiteUpdateLocationResponse(workSite)).thenReturn(workSiteUpdateLocationResponse);

        WorkSiteUpdateLocationResponse actual =
                workSiteService.updateWorkSiteLocation(
                    authentication,
                    workSite.getId(),
                    workSiteUpdateLocationRequest
                );

        assertEquals(workSiteUpdateLocationResponse, actual);
        assertEquals(workSiteUpdateLocationResponse.getWorkSiteId(), actual.getWorkSiteId());
        assertEquals(workSiteUpdateLocationResponse.getNewLatitude(), actual.getNewLatitude());
        assertEquals(workSiteUpdateLocationResponse.getNewLongitude(), actual.getNewLongitude());
        assertEquals(workSiteUpdateLocationResponse.getNewRadius(), actual.getNewRadius());

        verify(workSiteRepository).findById(workSite.getId());
        verify(workSiteRepository).save(workSite);
        verify(workSiteMapper).toWorkSiteUpdateLocationResponse(workSite);

        verify(metric).recordOperationTime(timerSample,"update_worksite_location_success");
        verify(metric).recordWorkSiteLatLon(
                workSite.getSiteName(),
                workSite.getCompany().getCompanyName(),
                workSite.getId(),
                workSite.getLatitude(),
                workSite.getLongitude(),
                true
        );
    }



    @Test
    void updateWorkingHours_failed_IllegalStateException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite3.getId())).thenReturn(Optional.of(workSite3));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> workSiteService.updateWorkingHours(
                        authentication,
                        workSite3.getId(),
                        updateWorkingHoursRequest
                )
        );

        assertEquals("Work site is not active", ex.getMessage());

        verify(workSiteRepository).findById(workSite3.getId());
        verify(workSiteRepository, never()).save(any());
        verify(workSiteMapper, never()).toWorkSiteUpdateWorkingHoursResponse(any());

        verify(metric).recordOperationTime(timerSample,"update_worksite_workinghours_failed");
        verify(metric).recordError("update_worksite_workinghours_failed", ex.getMessage(), ex);

    }


    @Test
    void updateWorkingHours_success(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);
        when(workSiteMapper.toWorkSiteUpdateWorkingHoursResponse(workSite)).thenReturn(workSiteUpdateWorkingHoursResponse);

        WorkSiteUpdateWorkingHoursResponse actual =
                workSiteService.updateWorkingHours(
                  authentication,
                   workSite.getId(),
                  updateWorkingHoursRequest
                );

        assertEquals(workSiteUpdateWorkingHoursResponse, actual);

        assertEquals(workSiteUpdateWorkingHoursResponse.getWorkSiteId(), actual.getWorkSiteId());
        assertEquals(workSiteUpdateWorkingHoursResponse.getNewStart(), actual.getNewStart());
        assertEquals(workSiteUpdateWorkingHoursResponse.getNewEnd(), actual.getNewEnd());

        verify(workSiteRepository).findById(workSite.getId());
        verify(workSiteRepository).save(workSite);
        verify(workSiteMapper).toWorkSiteUpdateWorkingHoursResponse(workSite);

        verify(metric).recordOperationTime(timerSample,"update_worksite_workinghours_success" );
    }




    @Test
    void updateWorkSiteAddress_success(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);
        when(workSiteMapper.toWorkSiteUpdateAddressResponse(workSite)).thenReturn(workSiteUpdateAddressResponse);

        WorkSiteUpdateAddressResponse actual =
                workSiteService.updateWorkSiteAddress(
                        authentication,
                        workSite.getId(),
                        updateWorkSiteAddress
                );
        assertEquals(workSiteUpdateAddressResponse, actual);

        assertEquals(workSiteUpdateAddressResponse.getWorksiteId(), actual.getWorksiteId());
        assertEquals(workSiteUpdateAddressResponse.getWorkSiteAddress(), actual.getWorkSiteAddress());

        verify(workSiteRepository).findById(workSite.getId());
        verify(workSiteRepository).save(workSite);
        verify(workSiteMapper).toWorkSiteUpdateAddressResponse(workSite);

        verify(metric).recordOperationTime(timerSample,"update_worksite_address_success" );
    }

    @Test
    void updateWorkSiteAddress_fail(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));

        IllegalArgumentException ex = assertThrows(
          IllegalArgumentException.class,
                () -> workSiteService.updateWorkSiteAddress(
                        authentication,
                        workSite.getId(),
                        updateWorkSiteAddressFail
                )
        );
        assertEquals("Work site address already exists", ex.getMessage());


    }



    @Test
    void updateWorkSiteName_success(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);
        when(workSiteMapper.toWorkSiteUpdateNameResponse(workSite)).thenReturn(workSiteUpdateNameResponse);
        WorkSiteUpdateNameResponse actual =
                workSiteService.updateWorkSiteName(
                        authentication,
                        workSite.getId(),
                        updateNameRequest
                );

        assertEquals(workSiteUpdateNameResponse.getWorksiteId(), actual.getWorksiteId());
        assertEquals(workSiteUpdateNameResponse.getWorkSiteName(), actual.getWorkSiteName());

        verify(workSiteRepository).save(workSite);
        verify(workSiteMapper).toWorkSiteUpdateNameResponse(workSite);

        verify(metric).recordOperationTime(timerSample,"update_worksite_name_success" );
        verify(metric).recordWorkSiteById(workSite.getSiteName(), workSite.getId(), true);
    }

    @Test
    void updateWorkSiteName_fai_IllegalArgumentException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));

        IllegalArgumentException ex = assertThrows(
                        IllegalArgumentException.class,
                        () -> workSiteService.updateWorkSiteName(
                                authentication,
                                workSite.getId(),
                                updateNameRequestFail
                        )
        );

        assertEquals("Work site name already exists", ex.getMessage());

        verify(workSiteRepository, never()).save(any());
        verify(workSiteMapper, never()).toWorkSiteUpdateNameResponse(any());

        verify(metric).recordOperationTime(timerSample,"update_worksite_name_failed" );
        verify(metric).recordWorkSiteById("unknown", 0, false);
    }




    @Test
    void setNewCustomRadiusForWorkSite(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);
        SetNewCustomRadiusResponse actual =
                workSiteService.setCustomRadiusForWorkSite(
                        authentication,
                        workSite.getId(),
                        setNewCustomRadiusRequest
                );

        assertEquals(1, actual.getWorkSiteId());
        assertEquals(5.0, actual.getCustomRadius());

        verify(metric).recordOperationTime(timerSample,"set_custom_worksite_radius_success" );
        verify(metric).recordWorkSiteById(workSite.getSiteName(), workSite.getId(), true);
        verify(metric).recordWorkSiteRadius(
                workSite.getSiteName(),
                workSite.getCompany().getCompanyName(),
                workSite.getId(),
                setNewCustomRadiusRequest.getCustomRadius(),
                true
        );
    }

    @Test
    void setNewCustomRadiusForWorker(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(userRepository.findById(worker.getId())).thenReturn(Optional.of(worker));
        when(workSiteRepository.save(workSite)).thenReturn(workSite);
        SetNewCustomRadiusForWorkerInSpecialWorkSiteResponse actual =
                workSiteService.setNewCustomRadiusForWorker(
                        workSite.getId(),
                        worker.getId(),
                        setNewCustomRadiusRequest,
                        authentication
                        );

        assertEquals(1, actual.getWorkSiteId());
        assertEquals(2, actual.getWorkerId());
        assertEquals("test", actual.getFirstName());
        assertEquals("test2", actual.getLastName());
        assertEquals(company.getCompanyName(), actual.getCompanyName());
        assertEquals(5.0, actual.getNewRadius());

        verify(metric).recordOperationTime(timerSample,"set_custom_radius_worker_success" );
        verify(metric).recordWorkSiteById(workSite.getSiteName(), workSite.getId(), true);
        verify(metric).recordForWorkerRadius(
                workSite.getSiteName(),
                worker.getCompany().getCompanyName(),
                worker.getFirstName() + " " + worker.getLastName(),
                workSite.getId(),
                setNewCustomRadiusRequest.getCustomRadius(),
                true
        );
    }

    @Test
    void setNewCustomRadiusForWorker_throwWorkSiteNotFoundException() {
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> workSiteService.setNewCustomRadiusForWorker(
                        workSite.getId(),
                        worker.getId(),
                        setNewCustomRadiusRequest,
                        authentication)

        );
        verify(userRepository, never()).findById(any());
        verify(workSiteRepository, never()).save(any());

        verify(metric).recordOperationTime(timerSample,"set_custom_radius__worker_failed" );
        verify(metric).recordWorkSiteById("unknown", 0, false);

    }

    @Test
    void setNewCustomRadiusForWorker_throwUserNotFoundException() {
        when(authentication.getPrincipal()).thenReturn(admin);
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(userRepository.findById(worker.getId())).thenReturn(Optional.empty());
        assertThrows(
                EntityNotFoundException.class,
                () -> workSiteService.setNewCustomRadiusForWorker(
                        workSite.getId(),
                        worker.getId(),
                        setNewCustomRadiusRequest,
                        authentication)

        );
        verify(workSiteRepository, never()).save(any());

        verify(metric).recordOperationTime(timerSample,"set_custom_radius__worker_failed" );
        verify(metric).recordWorkSiteById("unknown", 0, false);
    }

    @Test
    void setNewCustomRadiusForWorker_throwIllegalStateException() {
            when(authentication.getPrincipal()).thenReturn(admin);
            when(workSiteRepository.findById(workSite2.getId())).thenReturn(Optional.of(workSite2));
            when(userRepository.findById(worker.getId())).thenReturn(Optional.of(worker));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> workSiteService.setNewCustomRadiusForWorker(
                        workSite2.getId(),
                        worker.getId(),
                        setNewCustomRadiusRequest,
                        authentication
                )
            );
        assertEquals("Custom radius can't be the same", ex.getMessage() );
        verify(metric).recordOperationTime(timerSample,"set_custom_radius__worker_failed" );
        verify(metric).recordWorkSiteById("unknown", 0, false);
    }

    @Test
    void createWorkSite(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));
        when(workSiteRepository.save(any(WorkSite.class))).thenAnswer(
                invocation -> {
                    WorkSite workSite = invocation.getArgument(0);
                    workSite.setId(savedWorkSite.getId());
                    return workSite;
                });
        when(workSiteMapper.toWorkSiteResponse(any(WorkSite.class))).thenReturn(workSiteResponse);

        WorkSiteResponse expected = workSiteService.createWorkSite(authentication, workSiteRequest);

        assertEquals(workSiteResponse, expected);

        verify(metric).recordWorkSiteById(savedWorkSite.getSiteName(), savedWorkSite.getId(), true);
        verify(metric).recordOperationTime(timerSample, "create_new_work_success");

        verify(workSiteRepository).save(any(WorkSite.class));
        verify(workSiteMapper).toWorkSiteResponse(any(WorkSite.class));
    }

    @Test
    void createWorkSite_CompanyNotFoundException(){
        when(authentication.getPrincipal()).thenReturn(admin);
        when(companyRepository.findById(company.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> workSiteService.createWorkSite(authentication, workSiteRequest)
        );

        verify(metric).recordWorkSiteById("unknown", 0, false);
        verify(metric).recordOperationTime(timerSample, "create_work_failed");
        verify(metric).recordError(eq("f_create_work_failed"), anyString(), any());

    }





    @Test
    void findWorksiteById(){
        when(workSiteRepository.findById(workSite.getId())).thenReturn(Optional.of(workSite));
        when(workSiteMapper.toWorkSiteResponse(workSite)).thenReturn(workSiteResponse);
        WorkSiteResponse actual = workSiteService.findWorkSiteById(workSite.getId());

        assertEquals(workSiteResponse, actual);
        verify(metric).recordWorkSiteById("test", 1, true);
        verify(metric).recordOperationTime(timerSample, "find_worksite_success");
        verify(workSiteMapper).toWorkSiteResponse(workSite);
    }

    @Test
    void findWorksiteById_NotFound(){
        when(workSiteRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> workSiteService.findWorkSiteById(workSite.getId())
        );

        verify(metric).recordWorkSiteById("unknown", 0, false);
        verify(metric).recordOperationTime(timerSample, "find_worksite_failed");
        verify(metric).recordError(eq("f_worksite_failed"), anyString(), any());
    }




}
