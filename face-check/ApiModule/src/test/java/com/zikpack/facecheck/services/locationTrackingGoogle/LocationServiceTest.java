package com.zikpack.facecheck.services.locationTrackingGoogle;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.LocationProvider;
import com.zikpak.facecheck.entity.LocationRecord;
import com.zikpak.facecheck.entity.User;
import com.zikpak.facecheck.repository.LocationRecordRepository;
import com.zikpak.facecheck.repository.UserRepository;
import com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle.LocationService;
import com.zikpak.facecheck.taxesServices.services.LocationTrackingGoogle.LocationUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @InjectMocks
    private LocationService locationService;

    @Mock
    private LocationRecordRepository locationRecordRepository;

    @Mock
    private UserRepository userRepository;

    private LocationUpdateDto locationUpdateDto;
    private LocationRecord savedRecord;
    private User worker;


    @BeforeEach
    void setUp() {

        locationUpdateDto = new LocationUpdateDto();
        locationUpdateDto.setLatitude(22.07);
        locationUpdateDto.setLongitude(22.08);
        locationUpdateDto.setAccuracy(2.0);
        locationUpdateDto.setSpeed(1.0);
        locationUpdateDto.setBearing(2.0);
        locationUpdateDto.setAltitude(2.0);
        locationUpdateDto.setProvider("GPS");
        locationUpdateDto.setTimestamp(Instant.now());
        locationUpdateDto.setBatteryLevel(22);

        worker = new User();
        worker.setId(1);

        savedRecord = new LocationRecord();
        savedRecord.setLatitude(22.07);
        savedRecord.setLongitude(22.08);
        savedRecord.setAccuracy(2.0);
        savedRecord.setSpeed(1.0);
        savedRecord.setBearing(2.0);
        savedRecord.setAltitude(2.0);
        locationUpdateDto.setTimestamp(Instant.now());
        savedRecord.setProvider(LocationProvider.GPS);
        savedRecord.setBatteryLevel(22);
    }

    @Test
    void saveLocation(){
        when(userRepository.findById(1)).thenReturn(Optional.of(worker));
        when(locationRecordRepository.findTopByUserOrderByTimestampDesc(worker)).thenReturn(Optional.of(savedRecord));
        when(locationRecordRepository.save(any(LocationRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationRecord record = locationService.saveLocation(1, locationUpdateDto);

        verify(userRepository).findById(1);
        verify(locationRecordRepository).findTopByUserOrderByTimestampDesc(worker);
        verify(locationRecordRepository).save(any(LocationRecord.class));
        assertEquals(locationUpdateDto.getLatitude(), record.getLatitude());
        assertEquals(locationUpdateDto.getLongitude(), record.getLongitude());
    }

    @Test
    void getLastLocation(){
        when(userRepository.findById(1)).thenReturn(Optional.of(worker));
        when(locationRecordRepository.findTopByUserOrderByTimestampDesc(worker)).thenReturn(Optional.of(savedRecord));

        locationService.getLastLocation(1);
        verify(userRepository).findById(1);
        verify(locationRecordRepository).findTopByUserOrderByTimestampDesc(worker);
    }

    @Test
    void getLocationHistory(){
        LocalDate date = LocalDate.of(2020, 1, 1);
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault());
        when(userRepository.findById(1)).thenReturn(Optional.of(worker));
        when(locationRecordRepository.findByUserAndTimestampBetweenOrderByTimestampAsc(worker, startOfDay.toInstant(), endOfDay.toInstant())).thenReturn(List.of());


        locationService.getLocationHistory(1, date);

        verify(userRepository).findById(1);
        verify(locationRecordRepository).findByUserAndTimestampBetweenOrderByTimestampAsc(worker, startOfDay.toInstant(), endOfDay.toInstant());
    }





}
