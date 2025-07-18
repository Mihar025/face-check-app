package com.zikpak.facecheck.taxesServices.customReportsForCompanys.yearToDateReportTaxes;


import com.zikpak.facecheck.entity.Company;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.EmployerTaxRecordRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class YearToDateCustomReportService {

    private final CompanyRepository companyRepository;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final YearToDateCustomReportPdf yearToDateCustomReportPdf;
    private final EmployerTaxRecordRepository employerTaxRecordRepository;
    private final AmazonS3Service amazonS3Service;


    public byte[] generateYearToDateReport(Integer companyId, LocalDate startDate, LocalDate endDate){
        YearToDateDTO dto = yearToDateReport(companyId, startDate, endDate);
        byte[] report = yearToDateCustomReportPdf.generatePdf(dto);

        String companyKeyPart = dto.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]", "_");

        String periodPart = startDate.toString()
                + "_to_"
                + endDate.toString();

        // Собираем финальный ключ
        String key = String.format(
                "%s/%d/yearToDate/%s.pdf",
                companyKeyPart,
                companyId,
                periodPart
        );

        // Загружаем в S3
        try {
            amazonS3Service.uploadPdfToS3(report, key);
            log.info("Year to date report was uploaded to S3 with key: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload YearToDateReport to S3, key: {}", key, e);
        }

        return report;
    }




    public YearToDateDTO yearToDateReport(Integer companyId, LocalDate startDate, LocalDate endDate ) {
        if(endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Start date must be before End date");
        }

        BigDecimal futa = employerTaxRecordRepository.sumFutaForPeriodStartEnd(companyId, startDate, endDate);
        BigDecimal suta = employerTaxRecordRepository.sumSutaForPeriodStartEnd(companyId, startDate, endDate);
        log.info("SUTA {}", suta);
        log.info("FUTA: {}", futa);

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        List<WorkerPayroll> payrolls = workerPayrollRepository.findAllByCompanyIdAndPeriodBetween(company.getId(),
                startDate,
                endDate);
        Double totalRegularHours = payrolls.stream()
                .map(WorkerPayroll::getRegularHours)
                .filter(p -> p != null)
                .reduce(0.0, Double::sum);

        Double totalOvertimeHours = payrolls.stream()
                .map(WorkerPayroll::getOvertimeHours)
                .filter(p -> p != null)
                .reduce(0.0, Double::sum);


        BigDecimal totalGross = payrolls.stream()
                .map(WorkerPayroll::getGrossPay)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRegularPay = payrolls.stream()
                .map(WorkerPayroll::getRegularPay)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOvertimePay = payrolls.stream()
                .map(WorkerPayroll::getOvertimePay)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFederal = payrolls.stream()
                .map(WorkerPayroll::getFederalWithholding)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSocialSecurity = payrolls.stream()
                .map(WorkerPayroll::getSocialSecurityEmployee)
                .filter(p-> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMedicare = payrolls.stream()
                .map(WorkerPayroll::getMedicare)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalState = payrolls.stream()
                .map(WorkerPayroll::getNyStateWithholding)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalLocal = payrolls.stream()
                .map(WorkerPayroll::getNyLocalWithholding)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //FUTA + SUTA taxes


        BigDecimal totalDisabilityWithholding = payrolls.stream()
                .map(WorkerPayroll::getNyDisabilityWithholding)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaidFamilyLeave = payrolls.stream()
                .map(WorkerPayroll::getNyPaidFamilyLeave)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRetirement401kContribution = payrolls.stream()
                .map(WorkerPayroll::getRetirement401kContribution)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalHealthInsuranceCost = payrolls.stream()
                .map(WorkerPayroll::getHealthInsuranceCost)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDeductions = payrolls.stream()
                .map(WorkerPayroll::getTotalDeductions)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalNet = payrolls.stream()
                .map(WorkerPayroll::getNetPay)
                .filter(p -> p != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return YearToDateDTO.builder()
                .companyId(companyId)
                .companyName(company.getCompanyName())
                .periodStart(startDate)
                .periodEnd(endDate)
                .totalRegularHours(totalRegularHours)
                .totalOvertimeHours(totalOvertimeHours)
                .totalGross(totalGross)
                .totalRegularPay(totalRegularPay)
                .totalOvertimePay(totalOvertimePay)
                .totalFederalWithholding(totalFederal)
                .totalSocialSecurity(totalSocialSecurity)
                .totalMedicare(totalMedicare)
                .totalStateWithHolding(totalState)
                .totalLocalWithholding(totalLocal)
                .totalFutaWithholding(futa)
                .totalSutaWithholding(suta)
                .totalDisabilityWithholding(totalDisabilityWithholding)
                .totalPaidFamilyLeave(totalPaidFamilyLeave)
                .totalRetirement401kContribution(totalRetirement401kContribution)
                .totalHealthInsuranceCost(totalHealthInsuranceCost)
                .totalDeductions(totalDeductions)
                .totalNet(totalNet)
                .build();
    }

}
