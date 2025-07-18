package com.zikpak.facecheck.taxesServices.customReportsForCompanys.workSiteReport;

import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerAttendanceRepository;
import com.zikpak.facecheck.repository.WorkerSiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkSIteReportWorkersService {
    private final WorkerSiteRepository workerSiteRepository;
    private final CompanyRepository companyRepository;
    private final WorkerAttendanceRepository workerAttendanceRepository;

/*

    public byte[] generatePdf(Integer companyId, Integer workSiteId, LocalDate startDate, LocalDate endDate){

    }


    public WorkSiteReportWorkersDTO generateWorkSiteData(Integer companyId, Integer workSiteId, LocalDate startDate, LocalDate endDate){
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date is after End date");
        }

        var foundedCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        var foundedWorkSite = workerSiteRepository.findById(workSiteId)
                .orElseThrow(() -> new IllegalArgumentException("Work site not found"));




    }

 */



}
