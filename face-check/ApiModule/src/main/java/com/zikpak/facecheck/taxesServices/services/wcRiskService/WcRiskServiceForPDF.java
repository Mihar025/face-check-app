package com.zikpak.facecheck.taxesServices.services.wcRiskService;

import com.zikpak.facecheck.entity.WcRiskClass;
import com.zikpak.facecheck.entity.employee.WorkerPayroll;
import com.zikpak.facecheck.repository.CompanyRepository;
import com.zikpak.facecheck.repository.WorkerPayrollRepository;
import com.zikpak.facecheck.services.amazonS3Service.AmazonS3Service;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcReportDto;
import com.zikpak.facecheck.taxesServices.services.wcRiskService.dto.WcReportLine;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WcRiskServiceForPDF {

    private final CompanyRepository companyRepository;
    private final WorkerPayrollRepository workerPayrollRepository;
    private final WcReportPdfGeneratorService wcReportPdfGeneratorService;
    private final AmazonS3Service amazonS3Service;

    /**
     * Генерирует PDF отчет и загружает в S3
     */
    public byte[] generateWcReportPdf(Integer companyId, LocalDate periodStart, LocalDate periodEnd) {
        // Генерируем данные отчета
        WcReportDto report = generateReport(companyId, periodStart, periodEnd);

        // Генерируем PDF
        byte[] pdf = wcReportPdfGeneratorService.generateWcReportPdf(report);

        // Формируем ключ для S3
        String companyKeyPart = report.getCompanyName()
                .trim()
                .replaceAll("[^A-Za-z0-9]", "_");

        String periodPart = periodStart.toString()
                + "_to_"
                + periodEnd.toString();

        // Собираем финальный ключ
        String key = String.format(
                "%s/%d/wcReports/%s.pdf",
                companyKeyPart,
                companyId,
                periodPart
        );

        // Загружаем в S3
        try {
            amazonS3Service.uploadPdfToS3(pdf, key);
            log.info("WC Report uploaded to S3 with key: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload WC Report to S3, key: {}", key, e);
            // Не прерываем процесс - пользователь всё равно получит PDF
        }

        return pdf;
    }

    /**
     * Генерирует данные отчета (без PDF)
     */
    public WcReportDto generateReport(Integer companyId,
                                      LocalDate periodStart,
                                      LocalDate periodEnd) {
        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("Period end cannot be before period start");
        }

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + companyId));

        BigDecimal companyEMR = company.getEmr() != null
                ? company.getEmr()
                : BigDecimal.ONE;

        List<WorkerPayroll> payrolls = workerPayrollRepository
                .findByCompanyIdAndPeriodEndBetweenFetchRisk(companyId, periodStart, periodEnd);

        // Группируем по классам риска
        Map<WcRiskClass, BigDecimal> wagesByRisk = payrolls.stream()
                .filter(wp -> wp.getWorker() != null && wp.getWorker().getWcRiskClass() != null)
                .filter(wp -> wp.getGrossPay() != null) // Фильтруем null значения
                .collect(Collectors.groupingBy(
                        wp -> wp.getWorker().getWcRiskClass(),
                        Collectors.mapping(
                                WorkerPayroll::getGrossPay,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        // Создаем строки отчета
        List<WcReportLine> lines = wagesByRisk.entrySet().stream()
                .map(entry -> calculateReportLine(entry.getKey(), entry.getValue(), companyEMR))
                .sorted(Comparator.comparing(WcReportLine::getCode))
                .toList();

        // Считаем общую сумму
        BigDecimal grandTotal = lines.stream()
                .map(WcReportLine::getTotalContribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return WcReportDto.builder()
                .companyName(company.getCompanyName())
                .policyNumber(company.getWcPolicyNumber())
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .emr(companyEMR)
                .lines(lines)
                .grandTotal(grandTotal)
                .build();
    }

    /**
     * Вычисляет строку отчета для конкретного класса риска
     */
    private WcReportLine calculateReportLine(WcRiskClass risk, BigDecimal totalWages, BigDecimal companyEMR) {
        // ВАЖНО: Проверяем, как хранится rate в БД
        // Если rate хранится как процент (например, 5.25 для 5.25%), то делим на 100
        // Если rate хранится как десятичная дробь (например, 0.0525 для 5.25%), то НЕ делим

        // Предполагаем, что rate хранится как процент
        BigDecimal rateDecimal = risk.getRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        // Базовая премия = wages × rate (в десятичном виде)
        BigDecimal basePremium = totalWages
                .multiply(rateDecimal)
                .setScale(2, RoundingMode.HALF_UP);

        // Финальная премия = basePremium × EMR
        BigDecimal totalContribution = basePremium
                .multiply(companyEMR)
                .setScale(2, RoundingMode.HALF_UP);

        return WcReportLine.builder()
                .code(risk.getCode())
                .description(risk.getDescription())
                .totalWages(totalWages)
                .rate(risk.getRate()) // Отображаем оригинальный rate (как процент)
                .emr(companyEMR)
                .basePremium(basePremium)
                .totalContribution(totalContribution)
                .build();
    }
}