package com.zikpak.facecheck.TestDataForGeneratingData;

import com.zikpak.facecheck.entity.employee.WorkSite;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import com.zikpak.facecheck.requestsResponses.workSite.WorkSiteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WorkSiteTestService {

    private final WorkerSiteRepository workerSiteRepository;
    private final CompanyRepository companyRepository;

    public void createWorkSiteForCompany1(Integer companyId){
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        WorkSite workSite = new WorkSite();
        workSite.setSiteName("test Site 1");
        workSite.setAddress("407 Ocean view Ave");
        workSite.setLatitude(40.57940653335323);
        workSite.setLongitude(73.96374728941586);
        workSite.setAllowedRadius(100.0);
        workSite.setWorkDayStart(LocalTime.of(7, 0));
        workSite.setWorkDayEnd(LocalTime.of(16, 0));
                workSite.setIsActive(true);
        workSite.setIsWorkerDidPunchIn(null);
                workSite.setInactiveDays(null);
        workSite.setCompany(company);
        workerSiteRepository.save(workSite);
    }

    public void createWorkSiteForCompany2(Integer companyId){
        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        WorkSite workSite = new WorkSite();
        workSite.setSiteName("test Site 2");
        workSite.setAddress("4072 Ocean view Ave");
        workSite.setLatitude(40.57940653335323);
        workSite.setLongitude(73.96374728941586);
        workSite.setAllowedRadius(101.0);
        workSite.setWorkDayStart(LocalTime.of(7, 0));
        workSite.setWorkDayEnd(LocalTime.of(16, 0));
        workSite.setIsActive(true);
        workSite.setIsWorkerDidPunchIn(null);
        workSite.setInactiveDays(null);
        workSite.setCompany(company);
        workerSiteRepository.save(workSite);
    }

    }

