package com.zikpak.facecheck.mapper;


import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.requestsResponses.WorkSiteAllInformationResponse;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteResponse;
import com.zikpak.facecheck.requestsResponses.workSite.data.*;
import com.zikpak.facecheck.requestsResponses.workSite.updates.WorkSiteUpdateAddressResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.WorkSiteUpdateLocationResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.WorkSiteUpdateNameResponse;
import com.zikpak.facecheck.requestsResponses.workSite.updates.WorkSiteUpdateWorkingHoursResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WorkSiteMapper {


    public WorkSiteResponse toWorkSiteResponse(WorkSite foundedWorkSite) {
        return WorkSiteResponse.builder()
                .workSiteId(foundedWorkSite.getId())
                .workSiteName(foundedWorkSite.getSiteName())
                .address(foundedWorkSite.getAddress())
                .latitude(foundedWorkSite.getLatitude())
                .longitude(foundedWorkSite.getLongitude())
                .allowedRadius(foundedWorkSite.getAllowedRadius())
                .workDayStart(foundedWorkSite.getWorkDayStart())
                .workDayEnd(foundedWorkSite.getWorkDayEnd())
                .build();
    }

    public WorkSiteUpdateNameResponse toWorkSiteUpdateNameResponse(WorkSite workSite) {
        return WorkSiteUpdateNameResponse.builder()
                .worksiteId(workSite.getId())
                .workSiteName(workSite.getSiteName())
                .build();
    }

    public WorkSiteUpdateWorkingHoursResponse toWorkSiteUpdateWorkingHoursResponse(WorkSite workSite) {
        return WorkSiteUpdateWorkingHoursResponse.builder()
                .workSiteId(workSite.getId())
                .newStart(workSite.getWorkDayStart())
                .newEnd(workSite.getWorkDayEnd())
                .build();
    }

    public WorkSiteUpdateAddressResponse toWorkSiteUpdateAddressResponse(WorkSite WorkSite) {
        return WorkSiteUpdateAddressResponse.builder()
                .worksiteId(WorkSite.getId())
                .workSiteAddress(WorkSite.getAddress())
                .build();
    }

    public WorkSiteUpdateLocationResponse toWorkSiteUpdateLocationResponse(WorkSite workSite) {
        return WorkSiteUpdateLocationResponse.builder()
                .workSiteId(workSite.getId())
                .newLatitude(workSite.getLatitude())
                .newLongitude(workSite.getLongitude())
                .newRadius(workSite.getAllowedRadius())
                .build();
    }


    public ScheduleInactiveDayResponse toWorkSiteScheduleInactiveDay(WorkSite savedInactiveDay) {
        LocalDate latestInactiveDate = savedInactiveDay.getInactiveDays().stream()
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new RuntimeException("invalid inactive day"));

        return ScheduleInactiveDayResponse.builder()
                .workSiteId(savedInactiveDay.getId())
                .inactiveDate(latestInactiveDate)
                .build();
    }


    public WorkSiteAllInformationResponse toWorkSiteAllInformationResponse(WorkSite workSite) {
        return WorkSiteAllInformationResponse.builder()
                .workSiteId(workSite.getId())
                .siteName(workSite.getSiteName())
                .address(workSite.getAddress())
                .latitude(workSite.getLatitude())
                .longitude(workSite.getLongitude())
                .allowedRadius(workSite.getAllowedRadius())
                .workDayStart(workSite.getWorkDayStart())
                .workDayEnd(workSite.getWorkDayEnd())
                .isActive(workSite.getIsActive())
                .build();


    }
}
